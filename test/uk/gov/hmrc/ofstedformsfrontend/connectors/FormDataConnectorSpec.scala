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

package uk.gov.hmrc.ofstedformsfrontend.connectors

import java.util.UUID

import org.mockito.MockitoSugar
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers, WordSpec}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.ofstedformsfrontend.forms.FormId

import scala.concurrent.ExecutionContext

class FormDataConnectorSpec extends FlatSpec with Matchers with MockitoSugar with GuiceOneAppPerSuite
  with ScalaFutures with WiremockSpec with BeforeAndAfterAll with IntegrationPatience {

  implicit val executionContext = ExecutionContext.global

  val internalId = "5c94bd999dc6d61166a23383"

  val formId = FormId()

  protected override def beforeAll(): Unit = {
    super.beforeAll()
    addStubMapping(
      post(urlEqualTo("/forms"))
        .willReturn(ok.withBody(
          s"""{
            |  "_updated": "Fri, 22 Mar 2019 10:48:57 GMT",
            |  "_created": "Fri, 22 Mar 2019 10:48:57 GMT",
            |  "_etag": "6dd99c8de8b8a18518a62ab9dd07b1e4b2dfc015",
            |  "_id": "${internalId}",
            |  "_status": "OK"
            |}
            |""".stripMargin
        )).build()
    )

    addStubMapping(
      get(urlEqualTo(s"/forms/${formId.asString}"))
        .willReturn(ok.withBody(
          s"""{
             |  "_id": "${internalId}",
             |  "formId": "${formId.asString}",
             |  "data": {},
             |  "_updated": "Fri, 22 Mar 2019 10:48:57 GMT",
             |  "_created": "Fri, 22 Mar 2019 10:48:57 GMT",
             |  "_etag": "6dd99c8de8b8a18518a62ab9dd07b1e4b2dfc015"
             |}
             |""".stripMargin
        )).build()
    )

    addStubMapping(
      get(urlEqualTo(s"/forms/${internalId}"))
        .willReturn(ok.withBody(
          s"""{
             |  "_id": "${internalId}",
             |  "formId": "${formId.asString}",
             |  "data": {
             |    "fizz": "buzz"
             |  },
             |  "_updated": "Fri, 22 Mar 2019 10:50:58 GMT",
             |  "_created": "Fri, 22 Mar 2019 10:48:57 GMT",
             |  "_etag": "75bd0bbb4cbf5a07ed35b3c2412f4bd7b76da221"
             |}
             |""".stripMargin
        )).build()
    )

    addStubMapping(
      put(urlEqualTo(s"/forms/${internalId}"))
        .withHeader("If-Match", equalTo("6dd99c8de8b8a18518a62ab9dd07b1e4b2dfc015"))
        .withRequestBody(matchingJsonPath("$.formId"))
        .willReturn(ok.withBody(
          """{
            |  "_updated": "Fri, 22 Mar 2019 10:50:58 GMT",
            |  "_created": "Fri, 22 Mar 2019 10:48:57 GMT",
            |  "_id": "5c94bd999dc6d61166a23383",
            |  "_etag": "75bd0bbb4cbf5a07ed35b3c2412f4bd7b76da221",
            |  "_status": "OK"
            |}
            |""".stripMargin
        )).build()
    )
  }


  override def fakeApplication(): Application = {
    new GuiceApplicationBuilder()
      .configure("microservice.services.ofsted-db.port" -> wiremockServer.port())
      .build()
  }

  implicit val hc = HeaderCarrier()

  it should "allow to create form data" in {
    val connector = app.injector.instanceOf[FormDataProvider]
    whenReady(connector.create(formId)) { _ =>
      wiremockServer.verify(
        postRequestedFor(urlEqualTo("/forms")).withRequestBody(matchingJsonPath("$.formId"))
      )
    }
  }

  it should "allow to fetch information by formId" in {
    val connector = app.injector.instanceOf[FormDataProvider]
    whenReady(connector.get(formId)) { _ =>
      wiremockServer.verify(
        getRequestedFor(urlEqualTo(s"/forms/${formId.asString}"))
      )
    }
  }

  it should "allow to put new data information" in {
    val connector = app.injector.instanceOf[FormDataProvider]
    val update = connector.get(formId).flatMap( data =>
      connector.put(data, Map("fizz" -> "buzz"))
    )
    whenReady(update) { _ =>
      wiremockServer.verify(
        putRequestedFor(urlEqualTo(s"/forms/${internalId}")).withRequestBody(matchingJsonPath("$.formId"))
      )
    }
  }

}
