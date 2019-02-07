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
import cats.Monoid
import cats.data.Validated
import cats.implicits._

import concurrent.{ExecutionContext, Future}
import org.atnos.eff._
import play.api._
import mvc._
import play.api.data._
import Forms._
import ltbs.uniform.ErrorTree
import ltbs.uniform.web.Messages
import play.api.i18n.I18nSupport
import play.twirl.api.Html
import uk.gov.hmrc.ofstedformsfrontend.examples.GreasySpoon
import uk.gov.hmrc.ofstedformsfrontend.examples.GreasySpoon.GreasyStack

trait WebMonadForm[T] {
  def encode(in: T): Encoded

  def decode(out: Encoded): T

  def playForm(key: String,
               validation: T => Validated[ValidationError, T]): Form[T]

  def render(key: String,
             existing: ValidatedData[T],
             request: Request[AnyContent]): Html
}

class UniformController(mcc: MessagesControllerComponents)
                       (implicit executionContext: ExecutionContext) extends AbstractController(mcc) with PlayInterpreter with I18nSupport {

  val booleanForm: WebMonadForm[Boolean] = new WebMonadForm[Boolean] {

    def decode(out: Encoded): Boolean = out == "true"

    def encode(in: Boolean): Encoded = in.toString

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

  val intForm: WebMonadForm[Int] = new WebMonadForm[Int] {

    def decode(out: Encoded): Int = out.toInt

    def encode(in: Int): Encoded = in.toString

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

  val convertedProgram = GreasySpoon.greasySpoon[CombinedStack]
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
