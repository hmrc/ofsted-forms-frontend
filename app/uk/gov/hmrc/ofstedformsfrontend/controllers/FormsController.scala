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
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, MessagesControllerComponents}
import uk.gov.hmrc.ofstedformsfrontend.authentication.{AuthenticateActions, CheckAdminPass}
import uk.gov.hmrc.ofstedformsfrontend.forms.{FormKind, FormRepository, GeneralForm}
import uk.gov.hmrc.ofstedformsfrontend.views.html
import uk.gov.hmrc.play.bootstrap.controller.FrontendController

@Singleton
class FormsController @Inject()(authenticate: AuthenticateActions,
                                checkAdminPass: CheckAdminPass,
                                mcc: MessagesControllerComponents,
                                formRepository: FormRepository)
                               (user_form_list: html.user_form_list) extends FrontendController(mcc) with I18nSupport {

  def all(): Action[Unit] = authenticate(parse.empty).async { implicit request =>
    formRepository.findWhereCreatorIs(request.requester).map{ forms =>
      Ok(user_form_list(forms))
    }(defaultExecutionContext)
  }

  def create: Action[FormKind] = authenticate(parse.form(FormsController.kindForm)).async { implicit request =>
    formRepository.save(GeneralForm.create(request.body, request.requester)).map { _ =>
      Redirect(routes.FormsController.all())
    }(defaultExecutionContext)
  }
}

object FormsController {
  val kindForm: Form[FormKind] = {
    import play.api.data.Forms._
    import play.api.data._

    Form(single("kind" -> FormKind.formField))
  }
}
