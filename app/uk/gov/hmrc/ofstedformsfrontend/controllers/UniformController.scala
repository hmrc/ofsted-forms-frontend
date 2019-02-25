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

import java.util.concurrent.atomic.AtomicReference

import javax.inject.{Inject, Singleton}
import ltbs.uniform.ErrorTree
import ltbs.uniform.interpreters.playframework._
import ltbs.uniform.web._
import play.api._
import play.api.i18n.I18nSupport
import play.api.mvc._
import play.twirl.api.Html
import uk.gov.hmrc.ofstedformsfrontend.views._

import scala.concurrent.{ExecutionContext, Future}

object MemoryPersistence extends Persistence {
  private val storage = new AtomicReference(Map.empty[String, String])

  override def dataGet: Future[DB] = {
    Future.successful(storage.get())
  }

  override def dataPut(dataIn: DB): Future[Unit] = {
    storage.set(dataIn)
    Future.successful(Unit)
  }
}



@Singleton
class UniformController @Inject()(mcc: MessagesControllerComponents)
                                 (chrome: html.FormChrome)
                                 (implicit executionContext: ExecutionContext) extends AbstractController(mcc) with PlayInterpreter with I18nSupport {

  def messages(request: Request[AnyContent]): Messages = {
    val playMessages = messagesApi.preferred(request)
    new Messages {
      override def get(key: String, args: Any*): Option[String] = Some(playMessages.apply(key, args))

      override def get(key: List[String], args: Any*): Option[String] = Some(playMessages.apply(key, args))

      override def list(key: String, args: Any*): List[String] = ???
    }
  }

  override def renderForm(key: String,
                          errors: ErrorTree,
                          form: Html,
                          breadcrumbs: List[String],
                          request: Request[AnyContent],
                          messages: Messages): Html = {
    chrome(key, errors, form, breadcrumbs)(convertMessages(messagesApi.preferred(request)), request)
  }

  def form(implicit key: String) = Action.async { implicit request =>
    runWeb(
      program = ???,
      persistence = MemoryPersistence
    ){ result =>
      Future.successful(Results.Ok(result.toString))
    }
  }
}

