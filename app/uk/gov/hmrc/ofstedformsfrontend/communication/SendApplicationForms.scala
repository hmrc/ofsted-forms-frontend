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

package uk.gov.hmrc.ofstedformsfrontend.communication

import enumeratum._
import javax.xml.parsers.DocumentBuilder
import org.w3c.dom.Document
import uk.gov.hmrc.ofstedformsfrontend.forms.GeneralForm
import uk.gov.hmrc.ofstedformsfrontend.forms.envelop.ApplicationForm

import scala.collection.immutable

case class SendApplicationForms(forms: Seq[ApplicationForm]) {
  def toDocument(build: DocumentBuilder): Document = {
    val document = build.newDocument()
    val root = document.createElement("ApplicationForms")
    document.appendChild(root)
    forms.foreach { form =>
      val element  = document.createElement("ApplicationForm")
      element.appendChild(ApplicationForm.marshaller.marshall(form)(document))
      root.appendChild(element)
    }
    document
  }
}

sealed trait FormType extends EnumEntry

object FormType extends Enum[FormType] {
  val values: immutable.IndexedSeq[FormType] = findValues

  case object SC1 extends FormType
}
