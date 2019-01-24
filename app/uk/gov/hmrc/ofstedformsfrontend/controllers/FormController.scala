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

import javax.inject.{Inject, Singleton}
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, MessagesControllerComponents}
import uk.gov.hmrc.auth.core.{AuthConnector, AuthorisedFunctions}
import uk.gov.hmrc.ofstedformsfrontend.authentication.{AuthenticateActionBuilder, CheckAdminPass}
import uk.gov.hmrc.ofstedformsfrontend.config.AppConfig
import uk.gov.hmrc.ofstedformsfrontend.views.html
import uk.gov.hmrc.play.bootstrap.controller.FrontendController

@Singleton
class FormController @Inject()(val authConnector: AuthConnector,
                               authenticate: AuthenticateActionBuilder,
                               checkAdminPass: CheckAdminPass,
                               mcc: MessagesControllerComponents)
                              (pending_forms_list: html.pending_forms_list,
                               user_form_list: html.user_form_list)
                              (implicit config: AppConfig) extends FrontendController(mcc) with I18nSupport with AuthorisedFunctions {

  def pendingForms(): Action[Unit] = (authenticate andThen checkAdminPass) (parse.empty) { implicit request =>
    Ok(pending_forms_list())
  }

  def all(): Action[Unit] = authenticate(parse.empty) { implicit request =>
    Ok(user_form_list(Seq.empty))
  }
}
