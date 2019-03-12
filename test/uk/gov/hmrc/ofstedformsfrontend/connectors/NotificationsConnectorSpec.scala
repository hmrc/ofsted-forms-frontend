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

import java.time.ZonedDateTime
import java.util.UUID

import org.mockito.MockitoSugar
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.ofstedformsfrontend.authentication.AuthenticatedUser
import uk.gov.hmrc.ofstedformsfrontend.forms.{FormId, FormKind, Occurrence}

class NotificationsConnectorSpec extends FlatSpec with Matchers with MockitoSugar with GuiceOneAppPerSuite
  with ScalaFutures with WiremockSpec with BeforeAndAfterAll with IntegrationPatience {

  protected override def beforeAll(): Unit = {
    super.beforeAll()
    addStubMapping(
      any(urlEqualTo("/ofsted-forms-notifications/submission"))
        .willReturn(ok.withBody(UUID.randomUUID().toString))
        .build()
    )

    addStubMapping(
      any(urlEqualTo("/ofsted-forms-notifications/acceptance"))
        .willReturn(ok.withBody(UUID.randomUUID().toString))
        .build()
    )

    addStubMapping(
      any(urlEqualTo("/ofsted-forms-notifications/rejection"))
        .willReturn(ok.withBody(UUID.randomUUID().toString))
        .build()
    )
  }

  override def fakeApplication(): Application = new GuiceApplicationBuilder()
    .configure("microservice.services.ofsted-forms-notifications.port" -> wiremockServer.port())
    .build()

  implicit val hc: HeaderCarrier = HeaderCarrier()

  private val formId = FormId(UUID.fromString("da7740d5-6026-4cdd-bbc1-10cb077cc47b"))

  private val email = "jan.kowalski@example.com"

  private val moment = Occurrence(AuthenticatedUser("", ""), ZonedDateTime.parse("2004-02-12T15:19:21Z"))

  val expected = equalToJson(
    """{
      |  "time" : "2004-02-12T15:19:21Z",
      |  "email" : "jan.kowalski@example.com",
      |  "id" : "da7740d5-6026-4cdd-bbc1-10cb077cc47b",
      |  "kind" : "SC1"
      |}
    """.stripMargin
  )



  it should "make correct request to service on submission" in {
    val connector = app.injector.instanceOf[NotificationsConnector]
    connector.submission(formId, email, moment, FormKind.SC1).futureValue
    wiremockServer.verify(
      postRequestedFor(urlEqualTo("/ofsted-forms-notifications/submission"))
        .withRequestBody(expected)
    )
  }

  it should "make correct request to service on acceptance" in {
    val connector = app.injector.instanceOf[NotificationsConnector]
    connector.acceptance(formId, email, moment, FormKind.SC1).futureValue
    wiremockServer.verify(
      postRequestedFor(urlEqualTo("/ofsted-forms-notifications/acceptance"))
        .withRequestBody(expected)
    )
  }

  it should "make correct request to service on rejection" in {
    val connector = app.injector.instanceOf[NotificationsConnector]
    connector.rejection(formId, email, moment, FormKind.SC1).futureValue
    wiremockServer.verify(
      postRequestedFor(urlEqualTo("/ofsted-forms-notifications/acceptance"))
        .withRequestBody(expected)
    )
  }

}
