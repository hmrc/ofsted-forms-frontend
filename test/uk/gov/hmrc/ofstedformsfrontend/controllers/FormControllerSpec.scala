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

package uk.gov.hmrc.ofstedformsfrontend.controllers

import org.mockito.integrations.scalatest.ResetMocksAfterEachTest
import org.mockito.{ArgumentMatchersSugar, MockitoSugar}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{BeforeAndAfterEach, Matchers, WordSpec}
import play.api.test.FakeRequest
import play.twirl.api.Html
import uk.gov.hmrc.ofstedformsfrontend.authentication.AuthenticatedRequest
import uk.gov.hmrc.ofstedformsfrontend.forms._
import uk.gov.hmrc.ofstedformsfrontend.views.html.FormView
import uk.gov.hmrc.ofstedformsfrontend.{Example, Fake}

import scala.concurrent.Future

class FormControllerSpec extends WordSpec with Matchers with MockitoSugar with ResetMocksAfterEachTest
  with ArgumentMatchersSugar with PlayControllerStubs with BeforeAndAfterEach with ScalaFutures with IntegrationPatience {

  val formRepository: FormRepository = mock[FormRepository]

  val formView: FormView = mock[FormView]

  override protected def beforeEach(): Unit = {
    when(formView.apply(*)(*, *)).thenAnswer(Html.apply(""))
    when(formRepository.save(any[SubmittedForm])).thenAnswer[SubmittedForm](Future.successful)
    super.beforeEach()
  }

  "Form Controller" when {


    "deals with admin" should {
      val draft = Draft(FormId(), FormKind.SC1, Occurrence(Example.admin))

      val controller = new FormController(
        mcc = stubMessagesControllerComponents,
        forms = Fake.formActions(general = Some(draft), draftStub = Some(draft)),
        formRepository = formRepository,
        notificationsConnector = Fake.notificationsConnector,
        authenticate = Fake.loggedAs(Example.admin)
      )(formView)

      "allow to get form" in {
        val request = new AuthenticatedRequest(Example.admin, FakeRequest("GET", "/").withBody((): Unit))
        whenReady(controller.show(draft.id).apply(request)){ response =>
          response.header.status shouldEqual 200
        }
      }

      "allow to submit form" in {
        val request = new AuthenticatedRequest(Example.admin, FakeRequest("GET", "/").withBody((): Unit))
        whenReady(controller.submmision(draft.id).apply(request)){ response =>
          response.header.status shouldEqual 303
        }
      }
    }

    "deals with user" should {
      val draft = Draft(FormId(), FormKind.SC1, Occurrence(Example.user))
      val request = new AuthenticatedRequest(Example.user, FakeRequest("GET", "/").withBody((): Unit))

      val controller = new FormController(
        mcc = stubMessagesControllerComponents,
        forms = Fake.formActions(general = Some(draft), draftStub = Some(draft)),
        formRepository = formRepository,
        notificationsConnector = Fake.notificationsConnector,
        authenticate = Fake.loggedAs(Example.user)
      )(formView)

      "allow to get form" in {
        whenReady(controller.show(draft.id).apply(request)){ response =>
          response.header.status shouldEqual 200
        }
      }

      "allow to submit form" in {
        whenReady(controller.submmision(draft.id).apply(request)){ response =>
          response.header.status shouldEqual 303
        }
      }
    }
  }

}
