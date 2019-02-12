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

import ltbs.uniform.interpreters.playframework._
import cats._
import cats.data._

import concurrent.{ExecutionContext, Future}
import org.atnos.eff._
import play.api._
import mvc._
import play.api.data._
import Forms._
import ltbs.uniform._
import ltbs.uniform.web._
import play.api.i18n.I18nSupport
import play.twirl.api.Html
import uk.gov.hmrc.ofstedformsfrontend.examples.GreasySpoon
import uk.gov.hmrc.ofstedformsfrontend.examples.GreasySpoon.GreasyStack

class UniformController(mcc: MessagesControllerComponents)
                       (implicit executionContext: ExecutionContext) extends AbstractController(mcc) with PlayInterpreter with I18nSupport {

  val booleanForm: PlayForm[Boolean] = new PlayForm[Boolean] {


    override def encode(in: Boolean): Encoded = ???

    override def render(key: String, existing: Option[Encoded], data: Request[AnyContent], errors: ErrorTree): Html = ???

    override def receiveInput(data: Request[AnyContent]): Encoded = ???

    override def decode(out: Encoded): Either[ErrorTree, Boolean] = ???


    def playForm(key: String, validation: Boolean => Validated[ValidationError, Boolean]): Form[Boolean] =
      Form(single(key -> boolean))

    def render(key: String, existing: ValidatedData[Boolean], request: Request[AnyContent]): Html = {
      val form = existing match {
        case Some(Validated.Invalid(e)) => Form(single(key -> boolean)).withError("", e)
        case _ => Form(single(key -> boolean))
      }

      ??? // replace with your view
    }
  }

  val intForm: PlayForm[Boolean] = new PlayForm[Boolean] {


    override def render(key: String, existing: Option[Encoded], data: Request[AnyContent], errors: ErrorTree): Html = ???

    override def receiveInput(data: Request[AnyContent]): Encoded = ???

    override def encode(in: Boolean): Encoded = ???

    override def decode(out: Encoded): Either[ErrorTree, Boolean] = ???

    def playForm(key: String, validation: Int => Validated[ValidationError, Int]): Form[Int] =
      Form(single(key -> number))

    def render(key: String, existing: ValidatedData[Int], request: Request[AnyContent]): Html = {
      val form = existing match {
        case Some(Validated.Invalid(e)) => Form(single(key -> number)).withError("", e)
        case _ => Form(single(key -> boolean))
      }

      ??? // replace with your view
    }
  }

  type CombinedStack = FxAppend[GreasyStack, PlayStack]

  implicit val targetId: String = "uniform-example"

  def convertedProgram(implicit request: Request[AnyContent]) = GreasySpoon.greasySpoon[CombinedStack]
    .useForm(intForm)      // map Int fields
    .useForm(booleanForm)  // map Boolean fields



  override def messages(request: Request[AnyContent]): Messages = {
    val playMessages = messagesApi.preferred(request)
    new Messages {
      override def get(key: String, args: Any*): Option[String] = Some(playMessages.apply(key, args))

      override def get(key: List[String], args: Any*): Option[String] = ???

      override def list(key: String, args: Any*): List[String] = ???
    }
  }

  override def renderForm(key: String,
                          errors: ErrorTree,
                          form: Html,
                          breadcrumbs: List[String],
                          request: Request[AnyContent],
                          messages: Messages): Html = ???
}
