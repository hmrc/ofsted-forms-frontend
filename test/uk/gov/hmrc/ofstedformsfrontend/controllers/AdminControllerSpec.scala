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
import org.scalatest.concurrent.PatienceConfiguration.Timeout
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Milliseconds, Span}
import org.scalatest.{BeforeAndAfterEach, Matchers, WordSpec}
import play.api.mvc.Security.AuthenticatedRequest
import play.api.test.FakeRequest
import play.twirl.api.Html
import uk.gov.hmrc.ofstedformsfrontend.forms._
import uk.gov.hmrc.ofstedformsfrontend.views.html.admin.{FormView, PendingFormList}
import uk.gov.hmrc.ofstedformsfrontend.{Example, Fake}

import scala.concurrent.Future


class AdminControllerSpec extends WordSpec with Matchers with MockitoSugar with ArgumentMatchersSugar with ScalaFutures
  with ResetMocksAfterEachTest with BeforeAndAfterEach with PlayControllerStubs {

  val formRepository = mock[FormRepository]

  val formListView = mock[PendingFormList]

  val submittedFormView = mock[FormView]

  override protected def beforeEach(): Unit = {
    when(submittedFormView.apply(*)(*, *)) thenReturn Html("")
    when(formListView.apply(*)(*, *)) thenReturn Html("")
    when(formRepository.save(any[ApprovedForm])) thenAnswer[ApprovedForm] (form => Future.successful(form))
    when(formRepository.save(any[RejectedForm])) thenAnswer[RejectedForm] (form => Future.successful(form))
    when(formRepository.findSubmitted()) thenReturn Future.successful(List())
    super.beforeEach()
  }


  "Admin Controller" when {
    "deals with admin" should {
      val submittedForm = SubmittedForm(FormId(), FormKind.SC1, Occurrence(Example.admin), Occurrence(Example.admin))

      val controller = new AdminController(
        stubMessagesControllerComponents,
        Fake.loggedAs(Example.admin),
        Fake.formActions(submittedStub = Some(submittedForm)),
        Fake.checkAdminPass,
        formRepository,
        Fake.notificationsConnector
      )(formListView, submittedFormView)

      "render pending form list" in {
        val request = new AuthenticatedRequest(Example.admin, FakeRequest("GET", "/").withBody((): Unit))
        whenReady(controller.submittedForms().apply(request)){ _ =>
          verify(formListView).apply(*)(*,*)
        }
      }

      "render submitted form" in {

        val request = new AuthenticatedRequest(Example.admin, FakeRequest("GET", "/").withBody((): Unit))
        whenReady(controller.showForm(submittedForm.id).apply(request)){ _ =>
          verify(submittedFormView).apply(*)(*,*)
        }
      }

      "accept submitted form" in {
        val request = new AuthenticatedRequest(Example.admin, FakeRequest("GET", "/").withBody((): Unit))
        whenReady(controller.accept(submittedForm.id).apply(request), Timeout(scaled(Span(500, Milliseconds)))){ response =>
          response.header.status shouldEqual 303
          verify(formRepository).save(any[ApprovedForm])
        }
      }

      "reject submitted form" in {
        val request = new AuthenticatedRequest(Example.admin, FakeRequest("GET", "/").withBody((): Unit))
        whenReady(controller.reject(submittedForm.id).apply(request), Timeout(scaled(Span(500, Milliseconds)))){ response =>
          response.header.status shouldEqual 303
          verify(formRepository).save(any[RejectedForm])
        }
      }
    }
    "deals with user" should {
      val controller = new AdminController(
        stubMessagesControllerComponents,
        Fake.loggedAs(Example.admin),
        Fake.formActions(),
        Fake.checkAdminPass,
        formRepository,
        Fake.notificationsConnector
      )(formListView, submittedFormView)

    }
  }


}
