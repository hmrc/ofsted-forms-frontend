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

package uk.gov.hmrc.ofstedformsfrontend

import java.util.UUID

import play.api.mvc._
import play.api.test.Helpers
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.ofstedformsfrontend.authentication.{AuthenticateActions, AuthenticatedRequest, AuthenticatedUser, CheckAdminPass}
import uk.gov.hmrc.ofstedformsfrontend.connectors.{NotificationId, NotificationsConnector}
import uk.gov.hmrc.ofstedformsfrontend.controllers.actions.{DraftRequest, FormActions, FormRequest, SubmittedRequest}
import uk.gov.hmrc.ofstedformsfrontend.forms._

import scala.concurrent.{ExecutionContext, Future}

object Fake {
  val checkAdminPass = new CheckAdminPass(Set("admin@example.com"), ExecutionContext.global)

  def loggedAs(user: AuthenticatedUser): AuthenticateActions = new AuthenticateActions {
    override def parser: BodyParser[AnyContent] = Helpers.stubBodyParser()

    override def invokeBlock[A](request: Request[A], block: AuthenticatedRequest[A] => Future[Result]): Future[Result] = {
      block(new AuthenticatedRequest(user, request))
    }

    override protected def executionContext: ExecutionContext = ExecutionContext.global
  }

  def formActions(general: Option[GeneralForm] = None, draftStub: Option[Draft] = None, submittedStub: Option[SubmittedForm] = None): FormActions = {
    new FormActions {
      override def fetch[P](id: FormId): ActionRefiner[AuthenticatedRequest, FormRequest] = new ActionRefiner[AuthenticatedRequest, FormRequest] {
        override protected def refine[A](request: AuthenticatedRequest[A]): Future[Either[Result, FormRequest[A]]] = Future.successful {
          general.fold[Either[Result, FormRequest[A]]](Left(Results.NotFound("Not specified"))){ form =>
            Right(new FormRequest(form, request))
          }
        }

        override protected def executionContext: ExecutionContext = ExecutionContext.global
      }

      override def draft[P](id: FormId): ActionRefiner[AuthenticatedRequest, DraftRequest] = new ActionRefiner[AuthenticatedRequest, DraftRequest] {
        override protected def refine[A](request: AuthenticatedRequest[A]): Future[Either[Result, DraftRequest[A]]] = Future.successful {
          draftStub.fold[Either[Result, DraftRequest[A]]](Left(Results.NotFound("Not specified"))){ form =>
            Right(new DraftRequest(form, request))
          }
        }

        override protected def executionContext: ExecutionContext = ExecutionContext.global
      }

      override def submitted(id: FormId): ActionRefiner[AuthenticatedRequest, SubmittedRequest] = new ActionRefiner[AuthenticatedRequest, SubmittedRequest] {
        override protected def refine[A](request: AuthenticatedRequest[A]): Future[Either[Result, SubmittedRequest[A]]] = Future.successful {
          submittedStub.fold[Either[Result, SubmittedRequest[A]]](Left(Results.NotFound("Not specified"))){ form =>
            Right(new SubmittedRequest(form, request))
          }
        }

        override protected def executionContext: ExecutionContext = ExecutionContext.global
      }
    }
  }

  val notificationsConnector: NotificationsConnector = new NotificationsConnector {
    override def submission(formId: FormId, email: String, submission: Occurrence)(implicit hc: HeaderCarrier): Future[NotificationId] = {
      Future.successful(new NotificationId(UUID.randomUUID()))
    }

    override def acceptance(formId: FormId, email: String, submission: Occurrence)(implicit hc: HeaderCarrier): Future[NotificationId] = {
      Future.successful(new NotificationId(UUID.randomUUID()))
    }

    override def rejection(formId: FormId, email: String, submission: Occurrence)(implicit hc: HeaderCarrier): Future[NotificationId] = {
      Future.successful(new NotificationId(UUID.randomUUID()))
    }
  }
}
