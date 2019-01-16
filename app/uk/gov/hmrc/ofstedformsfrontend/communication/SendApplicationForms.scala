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
import org.joda.time.LocalDate

import scala.collection.immutable

class SendApplicationForms(forms: ApplicationForms,
                           formType: FormType,
                           formId: FormId,
                           createdBy: CreatedBy,
                           createdDate: LocalDate,
                           source: ApplicationSource,
                           parentId: ParentId,
                           communicationId: Option[String],
                           urn: URN,
                           documents: Option[Documents],
                           formMetadata: FormMetada,
                           payments: Option[Payments])



case class ApplicationForms()

sealed trait FormType extends EnumEntry

object FormType extends Enum[FormType] {
  val values: immutable.IndexedSeq[FormType] = findValues

  case object SC1 extends FormType
}


case class FormId()

case class CreatedBy()

case class ApplicationSource()

case class ParentId()

case class URN()

case class Documents()

case class Payments()

case class FormMetada()

