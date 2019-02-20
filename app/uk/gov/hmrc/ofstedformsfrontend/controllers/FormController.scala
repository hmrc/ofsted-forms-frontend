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

import javax.inject.Inject
import play.api.i18n.I18nSupport
import play.api.mvc._
import uk.gov.hmrc.ofstedformsfrontend.authentication.{AuthenticateActionBuilder, AuthenticatedUser, AuthenticatedRequest}
import uk.gov.hmrc.ofstedformsfrontend.connectors.NotificationsConnector
import uk.gov.hmrc.ofstedformsfrontend.forms.{Draft, FormId, FormRepository, GeneralForm}
import uk.gov.hmrc.ofstedformsfrontend.views.html
import uk.gov.hmrc.play.bootstrap.controller.{BackendController, FrontendController}

import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.util.{Failure, Success}

class FormRequest[A](val form: GeneralForm, request: AuthenticatedRequest[A]) extends WrappedRequest[A](request) {
  def requester: AuthenticatedUser = request.requester
}

class DraftRequest[A](val form: Draft, request: AuthenticatedRequest[A]) extends WrappedRequest[A](request) {
  def requester: AuthenticatedUser = request.requester
}

class FormController @Inject()(mcc: MessagesControllerComponents,
                               formRepository: FormRepository,
                               notificationsConnector: NotificationsConnector,
                               authenticate: AuthenticateActionBuilder)
                              (form_view: html.form_view) extends FrontendController(mcc) with I18nSupport {

  def fetchForm(id: FormId) = new ActionRefiner[AuthenticatedRequest, FormRequest] {
    /**
      * FIXME on scala 2.12 here should be used Future#transform(Try[_] => Try[_]) instead manual crafting promise
      */
    override protected def refine[A](request: AuthenticatedRequest[A]): Future[Either[Result, FormRequest[A]]] = {
      val promise = Promise[Either[Result, FormRequest[A]]]
      formRepository.find(id).onComplete {
        case Success(value) => promise.success(Right(new FormRequest[A](value, request)))
        case Failure(e: NoSuchElementException) => promise.success(Left(Results.NotFound(e.getMessage)))
        case Failure(_) => promise.success(Left(Results.InternalServerError))
      }(executionContext)
      promise.future
    }

    override protected def executionContext: ExecutionContext = mcc.executionContext
  }

  def fetchDraft(id: FormId) = new ActionRefiner[AuthenticatedRequest, DraftRequest] {
    /**
      * FIXME on scala 2.12 here should be used Future#transform(Try[_] => Try[_]) instead manual crafting promise
      */
    override protected def refine[A](request: AuthenticatedRequest[A]): Future[Either[Result, DraftRequest[A]]] = {
      val promise = Promise[Either[Result, DraftRequest[A]]]
      formRepository.findDraft(id).onComplete {
        case Success(value) => promise.success(Right(new DraftRequest[A](value, request)))
        case Failure(e: NoSuchElementException) => promise.success(Left(Results.NotFound(e.getMessage)))
        case Failure(_) => promise.success(Left(Results.InternalServerError))
      }(executionContext)
      promise.future
    }

    override protected def executionContext: ExecutionContext = mcc.executionContext
  }

  def show(id: FormId) = (authenticate andThen fetchForm(id)) (parse.empty) { implicit request =>
    Ok(form_view(request.form))
  }

  def submmision(id: FormId) = (authenticate andThen fetchDraft(id)).async { implicit request =>
    request.form.submit(request.requester, notificationsConnector, formRepository)(hc(request), mcc.executionContext).map( _ =>
      SeeOther(routes.FormsController.all().absoluteURL())
    )(mcc.executionContext)
  }
}
