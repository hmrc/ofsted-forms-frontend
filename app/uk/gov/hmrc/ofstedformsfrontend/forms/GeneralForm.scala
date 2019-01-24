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

package uk.gov.hmrc.ofstedformsfrontend.forms

import java.util.UUID

import enumeratum._
import uk.gov.hmrc.ofstedformsfrontend.authentication.AuthenticateUser

import scala.collection.immutable

case class FormId(value: UUID)

object FormId {
  def apply(): FormId = new FormId(UUID.randomUUID())
}

sealed trait FormKind extends EnumEntry

object FormKind extends Enum[FormKind] {
  override def values: immutable.IndexedSeq[FormKind] = findValues

  case object SC1 extends FormKind
}


trait GeneralForm {
  def id: FormId

  def kind: FormKind

  def created: Occurrence

  def submitted: Option[Occurrence]

  def completed: Option[Occurrence]

  def accepted: Option[Occurrence]
}

object GeneralForm {
  def create(kind: FormKind, creator: AuthenticateUser): GeneralForm = new Draft(
    id = FormId(),
    kind = kind,
    created = Occurrence(creator)
  )
}

case class Draft(id: FormId, kind: FormKind, created: Occurrence) extends GeneralForm {
  override def submitted: Option[Occurrence] = None

  override def completed: Option[Occurrence] = ???

  override def accepted: Option[Occurrence] = None
}