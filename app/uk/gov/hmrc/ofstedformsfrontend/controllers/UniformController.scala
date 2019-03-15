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

import gforms.SC1TestBuild._
import java.util.concurrent.atomic.AtomicReference
import cats.implicits._
import javax.inject.{Inject, Singleton}
import ltbs.uniform._
import ltbs.uniform.interpreters.playframework._
import ltbs.uniform.web._
import play.api._
import play.api.i18n.I18nSupport
import play.api.mvc._
import play.twirl.api.Html
import uk.gov.hmrc.ofstedformsfrontend.views._
import java.io.File
import java.time.LocalDate
import scala.concurrent.{ExecutionContext, Future}
//import ltbs.uniform.gformsparser.Address
import org.atnos.eff._

import ltbs.uniform.widgets.govuk.{html => govhtml,_}



object MemoryPersistence extends Persistence {
  private val storage = new AtomicReference(Map.empty[List[String], String])

  override def dataGet: Future[DB] = {
    Future.successful(storage.get())
  }

  override def dataPut(dataIn: DB): Future[Unit] = {
    storage.set(dataIn)
    Future.successful(Unit)
  }
}

@Singleton
class UniformController @Inject()
  (mcc: MessagesControllerComponents)
  (chrome: html.FormChrome)
  (implicit executionContext: ExecutionContext)
    extends AbstractController(mcc)
    with gforms.controllers.SC1TestBuildController with I18nSupport
{

  import InferParser._
  import ltbs.uniform.web.parser._

  def persistence(in: Request[AnyContent]): Persistence = MemoryPersistence

  def messages(request: Request[AnyContent]): Messages =
    BestGuessMessages(CmsMessages(gforms.SC1TestBuild.messages.mapValues(List(_))))

  override def renderForm(key: List[String],
                          errors: ErrorTree,
                          form: Html,
                          breadcrumbs: List[String],
                          request: Request[AnyContent],
    messagesOld: Messages): Html = {
    chrome(key.mkString("."), errors, form, breadcrumbs)(messagesOld, request)
  }

  def listingPage[A](key: List[String], errors: ltbs.uniform.ErrorTree, elements: List[A], messages: ltbs.uniform.web.Messages)(implicit evidence$1: ltbs.uniform.web.Htmlable[A]): play.twirl.api.Html  = ???

  def form(implicit key: String) = Action.async { implicit request =>
    implicit val keys: List[String] = key.split("/").toList
    implicit def renderTell: (Unit, String) => Html = {case _ => Html("")}
    interpretedJourney{_ => Future.successful(Ok("fin"))}
  }
}

case class CmsMessages(
  underlying: Map[String, List[String]]
) extends ltbs.uniform.web.Messages {

  @annotation.tailrec
  private def replaceArgs(
    input: String,
    args: List[String],
    count: Int = 0
  ): String = args match {
    case Nil    => input
    case h :: t => replaceArgs(input.replace(s"[{]$count[}]", h), t, count+1)
  }
 
  def get(key: String, args: Any*): Option[String] =
    underlying.get(key).flatMap{_.headOption}.map{
      replaceArgs(_,args.toList.map(_.toString))
    }

  def get(key: List[String], args: Any*): Option[String] = {

    @annotation.tailrec
    def inner(innerkey: List[String]): Option[String] = {
      innerkey match {
        case Nil => None
        case x::xs =>
          get(x, args:_*) match {
            case Some(o) => Some(o)
            case None => inner(xs)
          }
      }
    }
    inner(key)
  }


  def list(key: String, args: Any*): List[String] =
    underlying.getOrElse(key,Nil).map{ x =>
      replaceArgs(x,args.toList.map(_.toString))
    }
}
