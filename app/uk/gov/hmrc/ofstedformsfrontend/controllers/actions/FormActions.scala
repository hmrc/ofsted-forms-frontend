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

import javax.inject.Inject
import play.api.mvc._
import uk.gov.hmrc.ofstedformsfrontend.authentication.{AuthenticatedRequest, AuthenticatedUser}
import uk.gov.hmrc.ofstedformsfrontend.forms._

import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.language.higherKinds
import scala.util.{Failure, Success}

class FormRequest[A](val form: GeneralForm, request: AuthenticatedRequest[A]) extends WrappedRequest[A](request) {
  def requester: AuthenticatedUser = request.requester
}

class DraftRequest[A](val form: Draft, request: AuthenticatedRequest[A]) extends WrappedRequest[A](request) {
  def requester: AuthenticatedUser = request.requester
}

class SubmittedRequest[A](val form: SubmittedForm, request: AuthenticatedRequest[A]) extends WrappedRequest[A](request) {
  def requester: AuthenticatedUser = request.requester
}

trait FormRequestBuilder[A, R[_]] {
  def build[T](form: A, request: AuthenticatedRequest[T]): R[T]
}

class FormFetchAction[T <: GeneralForm, R[_] <: Request[_]](val executionContext: ExecutionContext,
                                                            val id: FormId)
                                                           (fetcher: FormId => Future[T],
                                                            builder: FormRequestBuilder[T, R]) extends ActionRefiner[AuthenticatedRequest, R] {

  override protected def refine[A](request: AuthenticatedRequest[A]): Future[Either[Result, R[A]]] = {
    val promise = Promise[Either[Result, R[A]]]
    fetcher(id).onComplete {
      case Success(value) => promise.success(Right(builder.build[A](value, request)))
      case Failure(e: NoSuchElementException) => promise.success(Left(Results.NotFound(e.getMessage)))
      case Failure(_) => promise.success(Left(Results.InternalServerError))
    }(executionContext)
    promise.future
  }
}

class FormActions @Inject()(formRepository: FormRepository, executionContext: ExecutionContext) {

  private val generalFormBuilder: FormRequestBuilder[GeneralForm, FormRequest] = new FormRequestBuilder[GeneralForm, FormRequest] {
    override def build[T](form: GeneralForm, request: AuthenticatedRequest[T]): FormRequest[T] = new FormRequest(form, request)
  }

  def fetch[P](id: FormId) =
    new FormFetchAction(executionContext, id)(formRepository.find, generalFormBuilder)

  private val draftBuilder: FormRequestBuilder[Draft, DraftRequest] = new FormRequestBuilder[Draft, DraftRequest] {
    override def build[T](form: Draft, request: AuthenticatedRequest[T]): DraftRequest[T] = new DraftRequest[T](form, request)
  }

  def draft[P](id: FormId) = new FormFetchAction(executionContext, id)(formRepository.findDraft, draftBuilder)


  private val submittedBuilder: FormRequestBuilder[SubmittedForm, SubmittedRequest] = new FormRequestBuilder[SubmittedForm, SubmittedRequest] {
    override def build[T](form: SubmittedForm, request: AuthenticatedRequest[T]): SubmittedRequest[T] = new SubmittedRequest[T](form, request)
  }

  def submitted(id: FormId) = new FormFetchAction(executionContext, id)(formRepository.findSubmitted, submittedBuilder)
}

