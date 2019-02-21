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
import scala.language.existentials
import scala.language.higherKinds
import scala.util.{Failure, Success}

class FormRequest[A, F](val form: GeneralForm, request: AuthenticatedRequest[A]) extends WrappedRequest[A](request) {
  def requester: AuthenticatedUser = request.requester
}

class DraftRequest[A](val form: Draft, request: AuthenticatedRequest[A]) extends WrappedRequest[A](request) {
  def requester: AuthenticatedUser = request.requester
}

class SubmittedRequest[A](val form: SubmittedForm, request: AuthenticatedRequest[A]) extends WrappedRequest[A](request) {
  def requester: AuthenticatedUser = request.requester
}


abstract class FormFetchAction[T <: GeneralForm, R[_] <: Request[_]](val executionContext: ExecutionContext,
                                                                     val id: FormId) extends ActionRefiner[AuthenticatedRequest, R] {

  override protected def refine[A](request: AuthenticatedRequest[A]): Future[Either[Result, R[A]]] = {
    val promise = Promise[Either[Result, R[A]]]
    fetchForm(id).onComplete {
      case Success(value) => promise.success(Right(buildRequest[A](value, request)))
      case Failure(e: NoSuchElementException) => promise.success(Left(Results.NotFound(e.getMessage)))
      case Failure(_) => promise.success(Left(Results.InternalServerError))
    }(executionContext)
    promise.future
  }

  abstract def fetchForm(id: FormId): Future[T]

  abstract def buildRequest[A](form: T, request: AuthenticatedRequest[A]): R[A]
}

class GeneralFormFetcher(formRepository: FormRepository,
                         executionContext: ExecutionContext,
                         id: FormId) extends FormFetchAction[GeneralForm, FormRequest](executionContext, id) {

  override def fetchForm(id: FormId): Future[GeneralForm] = formRepository.find(id)

  override def buildRequest[A](form: GeneralForm, request: AuthenticatedRequest[A]): FormRequest[A] = {
    new FormRequest[A](form, request)
  }
}

class DraftFormFetcher(formRepository: FormRepository,
                         executionContext: ExecutionContext,
                         id: FormId) extends FormFetchAction[Draft, DraftRequest](executionContext, id) {

  override def fetchForm(id: FormId): Future[Draft] = formRepository.findDraft(id)

  override def buildRequest[A](form: Draft, request: AuthenticatedRequest[A]): DraftRequest[A] = {
    new DraftRequest[A](form, request)
  }
}

class DraftFormFetcher(formRepository: FormRepository,
                       executionContext: ExecutionContext,
                       id: FormId) extends FormFetchAction[SubmittedForm, SubmittedRequest](executionContext, id) {

  override def fetchForm(id: FormId): Future[SubmittedForm] = formRepository.findSubmitted(id)

  override def buildRequest[A](form: SubmittedForm, request: AuthenticatedRequest[A]): SubmittedRequest[A] = {
    new SubmittedRequest[A](form, request)
  }
}

class FormActions @Inject()(formRepository: FormRepository, executionContext: ExecutionContext) {
  def fetch[P](id: FormId) =
    new FormFetchAction[GeneralForm, FormRequest, P](executionContext, id)(formRepository.find, new FormRequest(_: GeneralForm, _: AuthenticatedRequest[P]))

  def draft[P](id: FormId) =
    new FormFetchAction[Draft, DraftRequest, P](executionContext, id)(formRepository.findDraft, new DraftRequest(_: Draft, _: AuthenticatedRequest[P]))

  //  def submitted(id: FormId) =
  //    new FormFetchAction[GeneralForm, SubmittedRequest](executionContext, id)(formRepository.findSubmitted, new SubmittedRequest(_, _))
}
