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

package uk.gov.hmrc.ofstedformsfrontend.controllers.actions

import org.mockito.MockitoSugar
import org.scalatest.{FlatSpec, Matchers}
import play.api.mvc.{AnyContent, Results}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.ofstedformsfrontend.authentication.{AuthenticatedRequest, AuthenticatedUser}
import uk.gov.hmrc.ofstedformsfrontend.forms._

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor, Future}

class FormActionsSpec extends FlatSpec with MockitoSugar with Matchers {

  val executionContext: ExecutionContextExecutor = ExecutionContext.global

  val formRepository: FormRepository = mock[FormRepository]

  val actions = new FormActions(formRepository, executionContext)

  it should "fetch general form" in {
    val id = FormId()
    val user = AuthenticatedUser("internalId", "user@example.com")
    when(formRepository.find(id)) thenReturn Future.successful(Draft(id, FormKind.SC1, Occurrence(user)))
    val request = new AuthenticatedRequest(user,FakeRequest("POST", "/"))
    status {
      actions.fetch(id).invokeBlock[AnyContent](request, request => {
        request.form.id shouldEqual id
        Future.successful(Results.Ok("Pass"))
      })
    } shouldBe 200
  }

  it should "fetch draft form" in {
    val id = FormId()
    val user = AuthenticatedUser("internalId", "user@example.com")
    when(formRepository.findDraft(id)) thenReturn Future.successful(Draft(id, FormKind.SC1, Occurrence(user)))
    val request = new AuthenticatedRequest(user,FakeRequest("POST", "/"))
    status {
      actions.draft(id).invokeBlock[AnyContent](request, request => {
        request.form.id shouldEqual id
        Future.successful(Results.Ok("Pass"))
      })
    } shouldBe 200
  }

  it should "fetch submitted form" in {
    val id = FormId()
    val user = AuthenticatedUser("internalId", "user@example.com")
    when(formRepository.findSubmitted(id)) thenReturn Future.successful(SubmittedForm(id, FormKind.SC1, Occurrence(user), Occurrence(user)))
    val request = new AuthenticatedRequest(user,FakeRequest("POST", "/"))
    status {
      actions.submitted(id).invokeBlock[AnyContent](request, request => {
        request.form.id shouldEqual id
        Future.successful(Results.Ok("Pass"))
      })
    } shouldBe 200
  }

}
