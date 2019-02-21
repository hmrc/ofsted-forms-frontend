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
import uk.gov.hmrc.ofstedformsfrontend.authentication.AuthenticateActionBuilder
import uk.gov.hmrc.ofstedformsfrontend.connectors.NotificationsConnector
import uk.gov.hmrc.ofstedformsfrontend.controllers.actions.FormActions
import uk.gov.hmrc.ofstedformsfrontend.forms.{FormId, FormRepository}
import uk.gov.hmrc.ofstedformsfrontend.views.html
import uk.gov.hmrc.play.bootstrap.controller.FrontendController

class FormController @Inject()(mcc: MessagesControllerComponents,
                               forms: FormActions,
                               formRepository: FormRepository,
                               notificationsConnector: NotificationsConnector,
                               authenticate: AuthenticateActionBuilder)
                              (form_view: html.form_view) extends FrontendController(mcc) with I18nSupport {

  def show(id: FormId) = (authenticate andThen forms.fetch(id)) (parse.empty) { implicit request =>
    Ok(form_view(request.form))
  }

  def submmision(id: FormId) = (authenticate andThen forms.draft(id)).async { implicit request =>
    request.form.submit(request.requester, notificationsConnector, formRepository)(hc(request), mcc.executionContext).map( _ =>
      Redirect(routes.FormsController.all())
    )(mcc.executionContext)
  }
}
