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

import org.scalatest.{FlatSpec, Matchers}
import play.api.mvc.{AnyContent, Results}
import play.api.test.FakeRequest
import play.api.test.Helpers._

import scala.concurrent.{ExecutionContext, Future}

class CheckAdminPassSpec extends FlatSpec with Matchers {

  val executionContext = ExecutionContext.global

  val check = new CheckAdminPass(Set("admin@example.com"), executionContext)

  val adminRequest = new AuthenticatedRequest(AuthenticatedUser("admin-internalId", "admin@example.com"), FakeRequest("POST", "/"))

  val userRequest = new AuthenticatedRequest(AuthenticatedUser("user-internalId", "user@example.com"), FakeRequest("POST", "/"))

  it should "pass admin" in {
    val response = check.invokeBlock[AnyContent](adminRequest, _ => Future.successful(Results.Ok("Ok")))
    status(response) shouldBe 200
  }

  it should "block user" in {
    val response = check.invokeBlock[AnyContent](userRequest, _ => Future.successful(Results.Ok("Ok")))
    status(response) shouldBe 403
  }
}
