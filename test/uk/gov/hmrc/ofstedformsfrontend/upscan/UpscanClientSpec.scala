/*
 * Copyright 2019 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.ofstedformsfrontend.upscan

import java.util.UUID

import org.mockito.MockitoSugar
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers, WordSpec}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.ofstedformsfrontend.connectors.WiremockSpec
import uk.gov.hmrc.ofstedformsfrontend.controllers.routes
import uk.gov.hmrc.ofstedformsfrontend.forms.FormId

import scala.concurrent.ExecutionContext

class UpscanClientSpec extends FlatSpec with Matchers with MockitoSugar with GuiceOneAppPerSuite
  with ScalaFutures with WiremockSpec with BeforeAndAfterAll with IntegrationPatience {

  protected override def beforeAll(): Unit = {
    super.beforeAll()
    addStubMapping(
      any(urlEqualTo("/upscan/initiate"))
        .willReturn(ok.withBody("""{
                                  |    "reference": "11370e18-6e24-453e-b45a-76d3e32ea33d",
                                  |    "uploadRequest": {
                                  |        "href": "https://bucketName.s3.eu-west-2.amazonaws.com",
                                  |        "fields": {
                                  |            "Content-Type": "application/xml",
                                  |            "acl": "private",
                                  |            "key": "xxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx",
                                  |            "policy": "xxxxxxxx==",
                                  |            "x-amz-algorithm": "AWS4-HMAC-SHA256",
                                  |            "x-amz-credential": "ASIAxxxxxxxxx/20180202/eu-west-2/s3/aws4_request",
                                  |            "x-amz-date": "yyyyMMddThhmmssZ",
                                  |            "x-amz-meta-callback-url": "https://myservice.com/callback",
                                  |            "x-amz-signature": "xxxx"
                                  |        }
                                  |    }
                                  |}""".stripMargin)) // JSON comes from https://github.com/hmrc/upscan-initiate/#requesting-a-url-to-upload-to-
        .build()
    )
  }

  override def fakeApplication(): Application = new GuiceApplicationBuilder()
    .configure("microservice.services.upscan-initiate.port" -> wiremockServer.port())
    .build()

  it should "initiete correctlly new upload" in {
    val client = app.injector.instanceOf[UpscanClient]
    val formId = FormId()
    val hc = HeaderCarrier()
    whenReady(client.initiate(routes.FormController.submmision(formId))(hc)){ upload =>
      upload.reference shouldNot be(empty)
      upload.uploadRequest.href shouldNot be(empty)
      upload.uploadRequest.fields should contain key("key")
      upload.uploadRequest.fields should contain key("policy")
    }
  }
}
