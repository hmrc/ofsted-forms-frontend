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

import akka.stream.scaladsl.{FileIO, Source}
import akka.util.ByteString
import javax.inject.Inject
import play.api.Logger
import play.api.i18n.I18nSupport
import play.api.mvc._
import uk.gov.hmrc.ofstedformsfrontend.authentication.AuthenticateActions
import uk.gov.hmrc.ofstedformsfrontend.connectors.NotificationsConnector
import uk.gov.hmrc.ofstedformsfrontend.controllers.actions.FormActions
import uk.gov.hmrc.ofstedformsfrontend.forms.{FormId, FormRepository}
import uk.gov.hmrc.ofstedformsfrontend.upscan.UpscanClient
import uk.gov.hmrc.ofstedformsfrontend.views.html.FormView
import uk.gov.hmrc.ofstedformsfrontend.views.html.upscan.UploadForm
import uk.gov.hmrc.play.bootstrap.controller.FrontendController

import scala.concurrent.Future

class FormController @Inject()(mcc: MessagesControllerComponents,
                               forms: FormActions,
                               formRepository: FormRepository,
                               upscanClient: UpscanClient,
                               notificationsConnector: NotificationsConnector,
                               authenticate: AuthenticateActions)
                              (formView: FormView, uploadForm: UploadForm) extends FrontendController(mcc) with I18nSupport {

  def show(id: FormId): Action[Unit] = (authenticate andThen forms.fetch(id)) (parse.empty) { implicit request =>
    Ok(formView(request.form))
  }

  def file(id: FormId) = (authenticate andThen forms.fetch(id)).async(parse.multipartFormData(2 << 20)){ implicit request =>
    val multipart = request.body
    multipart.dataParts.get("href").flatMap(_.headOption).fold(Future.successful(BadRequest("missing href"))){ href =>
      val data = multipart.dataParts.flatMap {
        case (key, value) if key.startsWith("upscan.") => Some((key.stripPrefix("upscan."), value))
        case _ => None
      }
      val withSource = multipart.files.map(file => file.copy(ref = FileIO.fromPath(file.ref.path): Source[ByteString, Any]))
      val prepared = multipart.copy(files = withSource, dataParts = data)
      upscanClient.upload(href, prepared).map(_ => Redirect(routes.FormsController.all()))(mcc.executionContext)
    }
  }

  def fileForm(id: FormId) = (authenticate andThen forms.fetch(id)).async(parse.empty) { implicit request =>
    upscanClient.initiate(routes.FormController.callback(id)).map{ descriptor =>
      Ok(uploadForm(descriptor, id))
    }(mcc.executionContext)
  }

  def callback(id: FormId) = Action(parse.json) { implicit request =>
    Logger.info(s"Callback from upscan-notify - body ${request.body}")
    NoContent
  }

  def submmision(id: FormId): Action[Unit] = (authenticate andThen forms.draft(id)).async(parse.empty) { implicit request =>
    request.form.submit(request.requester, notificationsConnector, formRepository)(hc(request), mcc.executionContext).map( _ =>
      Redirect(routes.FormsController.all())
    )(mcc.executionContext)
  }
}
