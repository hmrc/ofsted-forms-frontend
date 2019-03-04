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

import com.google.inject.ImplementedBy
import javax.inject.Inject
import play.api.mvc._
import uk.gov.hmrc.auth.core.retrieve._
import uk.gov.hmrc.auth.core.{AuthConnector, AuthorisedFunctions, _}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.HeaderCarrierConverter

import scala.concurrent.{ExecutionContext, Future}

class AuthenticatedRequest[A](val requester: AuthenticatedUser,
                              val headerCarrier: HeaderCarrier,
                              request: Request[A]) extends WrappedRequest[A](request)


@ImplementedBy(classOf[AuthenticateActionBuilder])
trait AuthenticateActions extends ActionBuilder[AuthenticatedRequest, AnyContent]

class AuthenticateActionBuilder @Inject()(val authConnector: AuthConnector,
                                          configuration: AuthenticationConfiguration,
                                          val parser: BodyParsers.Default,
                                          val executionContext: ExecutionContext) extends AuthenticateActions with AuthorisedFunctions {

  private def extractHeaders(rh: RequestHeader) =
    HeaderCarrierConverter.fromHeadersAndSessionAndRequest(rh.headers, Some(rh.session), Some(rh))

  override def invokeBlock[A](request: Request[A], block: AuthenticatedRequest[A] => Future[Result]): Future[Result] = {
    val carrier = extractHeaders(request)
    authorised().retrieve(Retrievals.email and Retrievals.internalId) {
      case Some(email) ~ Some(internalId) =>
        val user = AuthenticatedUser(internalId, email)
        block(new AuthenticatedRequest[A](user, carrier, request))
      case _ =>
        Future.successful(Results.Forbidden("You are not have email or internalId"))
    }(carrier, executionContext).recover {
      case _: NoActiveSession =>
        Results.Redirect(configuration.loginUrl, Map("continue" -> Seq(configuration.continueUrl(request))))
    }(executionContext)
  }
}