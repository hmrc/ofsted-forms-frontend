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

import ltbs.uniform.interpreters.playframework._
import cats._
import cats.data._

import concurrent.{ExecutionContext, Future}
import org.atnos.eff._
import play.api._
import mvc._
import play.api.data._
import Forms._
import javax.inject.{Inject, Singleton}
import ltbs.uniform.{ErrorTree, _}
import ltbs.uniform.web._
import play.api.i18n.I18nSupport
import play.twirl.api.Html
import uk.gov.hmrc.ofstedformsfrontend.examples.GreasySpoon
import uk.gov.hmrc.ofstedformsfrontend.examples.GreasySpoon.GreasyStack
import uk.gov.hmrc.ofstedformsfrontend.views._

import scala.util.Try

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

//  val booleanForm: PlayForm[Boolean] = new PlayForm[Boolean] {
//
//    override def render(key: String, existing: Option[Encoded], data: Request[AnyContent], errors: ErrorTree): Html = {
//      Html(s"INT FORM INPUT")
//    }
//
//    override def receiveInput(data: Request[AnyContent]): Encoded = ???
//
//    override def encode(in: Boolean): Encoded = ???
//
//    override def decode(out: Encoded): Either[ErrorTree, Boolean] = ???
//
//  }
//
//  val intForm: PlayForm[Int] = new PlayForm[Int] {
//
//    override def render(key: String, existing: Option[Encoded], data: Request[AnyContent], errors: ErrorTree): Html = {
//      Html(s"INT FORM INPUT")
//    }
//
//    override def receiveInput(data: Request[AnyContent]): Encoded = ???
//
//    override def encode(in: Int): Encoded = ???
//
//    override def decode(out: Encoded): Either[ErrorTree, Int] = ???
//
//  }

  type CombinedStack = FxAppend[GreasyStack, PlayStack]

  def convertedProgram(implicit request: Request[AnyContent], targetId: String) ={
    import ltbs.uniform.web.parser._
    import ltbs.uniform.widgets.govuk._
    GreasySpoon.greasySpoon[CombinedStack]
      .useForm(PlayForm.automatic[Int])      // map Int fields
      .useForm(PlayForm.automatic[Boolean])  // map Boolean fields
  }



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
      program = convertedProgram(request, key),
      persistence = MemoryPersistence
    ){ result =>
      Future.successful(Results.Ok(result.toString))
    }
  }
}

