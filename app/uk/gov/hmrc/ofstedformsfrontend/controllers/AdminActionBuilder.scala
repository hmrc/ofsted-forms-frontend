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

import javax.inject.{Inject, Named, Singleton}
import play.api.mvc._
import uk.gov.hmrc.auth.core.retrieve.Retrievals
import uk.gov.hmrc.auth.core.{AuthConnector, AuthorisedFunctions}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.HeaderCarrierConverter

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AdminActionBuilder @Inject()(val authConnector: AuthConnector,
                                   @Named("admins") admins: Set[String],
                                   cc: ControllerComponents)
  extends ActionBuilderImpl[AnyContent](cc.parsers.default)(cc.executionContext) with AuthorisedFunctions {

  override protected def composeAction[A](action: Action[A]): Action[A] =
    new AdminAction[A](authConnector, admins, action)
}


class AdminAction[A](val authConnector: AuthConnector,
                     admins: Set[String],
                     action: Action[A]) extends Action[A] with AuthorisedFunctions {

  private def extractHeaders(rh: RequestHeader) =
    HeaderCarrierConverter.fromHeadersAndSessionAndRequest(rh.headers, Some(rh.session), Some(rh))

  override def parser: BodyParser[A] = action.parser

  override def executionContext: ExecutionContext = action.executionContext

  override def apply(request: Request[A]): Future[Result] = {
    authorised().retrieve(Retrievals.email) {
      case Some(email) =>
        if (admins.contains(email)) {
          action(request)
        } else {
          Future.successful(Results.Forbidden("You are not on list of admins"))
        }
      case None =>
        Future.successful(Results.Forbidden("You are not have email"))
    }(extractHeaders(request), action.executionContext)
  }
}