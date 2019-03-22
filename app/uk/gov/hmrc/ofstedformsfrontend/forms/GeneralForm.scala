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

import java.time.ZonedDateTime
import java.util.UUID

import enumeratum._
import play.api.libs.json.{Reads, Writes}
import play.api.mvc.PathBindable
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.ofstedformsfrontend.authentication.AuthenticatedUser
import uk.gov.hmrc.ofstedformsfrontend.connectors.{NotificationId, NotificationsConnector}

import scala.collection.immutable
import scala.concurrent.{ExecutionContext, Future}

case class FormId(value: UUID) {
  def asString: String = value.toString
}

object FormId {
  def apply(): FormId = new FormId(UUID.randomUUID())

  implicit val pathBindable: PathBindable[FormId] = PathBindable.bindableUUID.transform[FormId](apply, _.value)

  implicit val reads: Reads[FormId] = Reads.uuidReads.map(FormId.apply)

  implicit val writes: Writes[FormId] = Writes(id => Writes.UuidWrites.writes(id.value))
}

sealed trait FormKind extends EnumEntry

object FormKind extends PlayEnum[FormKind] {
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

  def isAccepted: Boolean = accepted.isDefined
}

object GeneralForm {
  def create(kind: FormKind, creator: AuthenticatedUser): Draft = new Draft(
    id = FormId(),
    kind = kind,
    created = Occurrence(creator)
  )
}

case class RejectedForm(id: FormId, kind: FormKind, created: Occurrence, submission: Occurrence, rejection: Occurrence) extends GeneralForm {
  override def submitted: Option[Occurrence] = Some(submission)

  override def completed: Option[Occurrence] = Some(rejection)

  override def accepted: Option[Occurrence] = None
}


case class ApprovedForm(id: FormId, kind: FormKind, created: Occurrence, submission: Occurrence, acceptance: Occurrence) extends GeneralForm {
  override def submitted: Option[Occurrence] = Some(submission)

  override def completed: Option[Occurrence] = Some(acceptance)

  override def accepted: Option[Occurrence] = Some(acceptance)
}

case class SubmittedForm(id: FormId, kind: FormKind, created: Occurrence, submission: Occurrence) extends GeneralForm {
  override val submitted: Option[Occurrence] = Some(submission)

  override def completed: Option[Occurrence] = None

  override def accepted: Option[Occurrence] = None

  def accept(acceptor: AuthenticatedUser,
             notificationsConnector: NotificationsConnector,
             formRepository: FormRepository)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[ApprovedForm] = {
    val acceptance = Occurrence(acceptor, ZonedDateTime.now())
    val result = ApprovedForm(id, kind, created, submission, acceptance)
    formRepository.save(result).flatMap( saved =>
      notificationsConnector.acceptance(saved.id, saved.created.executor.email, saved.acceptance, kind).map(_ => saved)
    )
  }

  def reject(rejector: AuthenticatedUser,
             notificationsConnector: NotificationsConnector,
             formRepository: FormRepository)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[RejectedForm] = {
    val rejection = Occurrence(rejector, ZonedDateTime.now())
    val result = RejectedForm(id, kind, created, submission, rejection)
    formRepository.save(result).flatMap( saved =>
      notificationsConnector.rejection(saved.id, saved.created.executor.email, saved.rejection, kind).map(_ => saved)
    )
  }
}

case class Draft(id: FormId, kind: FormKind, created: Occurrence) extends GeneralForm {
  override def submitted: Option[Occurrence] = None

  override def completed: Option[Occurrence] = None

  override def accepted: Option[Occurrence] = None

  def submit(submitter: AuthenticatedUser,
             notificationsConnector: NotificationsConnector,
             formRepository: FormRepository)
            (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[(SubmittedForm, NotificationId)] = {
    val submission = Occurrence(submitter, ZonedDateTime.now())
    val result = SubmittedForm(id, kind, created, submission)
    formRepository.save(result).flatMap( saved =>
      notificationsConnector.submission(saved.id, submitter.email, submission, kind)
        .map(notification => (saved, notification))
    )

  }
}
