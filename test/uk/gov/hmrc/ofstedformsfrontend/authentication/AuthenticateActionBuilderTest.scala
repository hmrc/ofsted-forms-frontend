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

package uk.gov.hmrc.ofstedformsfrontend.authentication

import akka.util.Timeout
import org.mockito.{MockitoSugar, _}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{FlatSpec, Matchers}
import play.api.mvc.{BodyParsers, Results}
import play.api.test.{FakeRequest, Helpers, NoMaterializer}
import uk.gov.hmrc.auth.core.retrieve.~
import uk.gov.hmrc.auth.core.{AuthConnector, SessionRecordNotFound}

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor, Future}

class AuthenticateActionBuilderTest extends FlatSpec with Matchers with MockitoSugar with ScalaFutures with ArgumentMatchersSugar {

  implicit val timeout: Timeout = Timeout(patienceConfig.timeout)

  val executionContext: ExecutionContextExecutor = ExecutionContext.global

  val configuration = new AuthenticationConfiguration(
    loginUrl = "http://localhost:8080/",
    continueBaseUrl = "http://localhost:8080"
  )

  val authConnector = mock[AuthConnector]

  val builder = new AuthenticateActionBuilder(
    authConnector,
    configuration,
    new BodyParsers.Default()(NoMaterializer),
    executionContext
  )

  it should "authorized request" in {
    when(authConnector.authorise[~[Option[String], Option[String]]](*, *)(*, *)) thenReturn Future.successful(new ~(Some("example@example.com"), Some("internalId")))
    val request = FakeRequest("POST", "/")
    val response = builder.apply(_ => Results.Ok("ok")).apply(request)
    Helpers.status(response) shouldBe 200
  }

  it should "redirect when no active session is present" in {
    when(authConnector.authorise[~[Option[String], Option[String]]](*, *)(*, *)) thenReturn Future.failed(SessionRecordNotFound())
    val request = FakeRequest("POST", "/")
    val response = builder.apply(_ => Results.Ok("ok")).apply(request)
    Helpers.status(response) shouldBe 303
  }

  it should "return forbiden if email is missing" in {
    when(authConnector.authorise[~[Option[String], Option[String]]](*, *)(*, *)) thenReturn Future.successful(new ~(None, Some("internalId")))
    val request = FakeRequest("POST", "/")
    val response = builder.apply(_ => Results.Ok("ok")).apply(request)
    Helpers.status(response) shouldBe 403
  }

  it should "return forbiden if internal identifier is missing" in {
    when(authConnector.authorise[~[Option[String], Option[String]]](*, *)(*, *)) thenReturn Future.successful(new ~(Some("example@example.com"), None))
    val request = FakeRequest("POST", "/")
    val response = builder.apply(_ => Results.Ok("ok")).apply(request)
    Helpers.status(response) shouldBe 403
  }

  it should "return forbiden if f internalId and email is missing" in {
    when(authConnector.authorise[~[Option[String], Option[String]]](*, *)(*, *)) thenReturn Future.successful(new ~(None, None))
    val request = FakeRequest("POST", "/")
    val response = builder.apply(_ => Results.Ok("ok")).apply(request)
    Helpers.status(response) shouldBe 403
  }
}