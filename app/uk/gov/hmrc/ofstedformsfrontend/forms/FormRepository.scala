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

import java.util.concurrent.atomic.AtomicReference

import com.google.inject.ImplementedBy
import javax.inject.Singleton
import uk.gov.hmrc.ofstedformsfrontend.authentication.AuthenticatedUser

import scala.annotation.tailrec
import scala.concurrent.Future

@ImplementedBy(classOf[MemoryFormRepository])
trait FormRepository {

  /**
    * @param id requested form identifier
    * @return Success(form) when exist, Failed(NoSuchElement) where not found, Failed(Throwable) on exception form upstream
    */
  def find(id: FormId): Future[GeneralForm]

  def findDraft(id: FormId): Future[Draft]

  def findSubmitted(id: FormId): Future[SubmittedForm]

  def findSubmitted(): Future[scala.collection.immutable.Iterable[GeneralForm]]

  def save(form: Draft): Future[Draft]

  def save(form: SubmittedForm): Future[SubmittedForm]

  def save(form: ApprovedForm): Future[ApprovedForm]

  def save(form: RejectedForm): Future[RejectedForm]

  def findWhereCreatorIs(creator: AuthenticatedUser): Future[scala.collection.immutable.Iterable[GeneralForm]]
}


@Singleton
final class MemoryFormRepository extends FormRepository {
  private val database = new AtomicReference[Map[FormId, GeneralForm]](Map.empty)

  @tailrec
  override def save(form: Draft): Future[Draft] = {
    val now = database.get()
    if(database.compareAndSet(now, now.updated(form.id, form))){
      Future.successful(form)
    } else {
      save(form)
    }
  }

  @tailrec
  override def save(form: SubmittedForm): Future[SubmittedForm] = {
    val now = database.get()
    if(database.compareAndSet(now, now.updated(form.id, form))){
      Future.successful(form)
    } else {
      save(form)
    }
  }

  @tailrec
  override def save(form: ApprovedForm): Future[ApprovedForm] = {
    val now = database.get()
    if(database.compareAndSet(now, now.updated(form.id, form))){
      Future.successful(form)
    } else {
      save(form)
    }
  }

  @tailrec
  override def save(form: RejectedForm): Future[RejectedForm] = {
    val now = database.get()
    if(database.compareAndSet(now, now.updated(form.id, form))){
      Future.successful(form)
    } else {
      save(form)
    }
  }

  override def findWhereCreatorIs(creator: AuthenticatedUser): Future[scala.collection.immutable.Iterable[GeneralForm]] = {
    val map = database.get()
    Future.successful {
      map.withFilter {
        case (_, form) => form.created.executor == creator
      }.map(_._2)
    }
  }

  override def find(id: FormId): Future[GeneralForm] = {
    database.get.get(id).fold[Future[GeneralForm]](Future.failed(new NoSuchElementException(s"There is no form $id")))(Future.successful)
  }

  override def findDraft(id: FormId): Future[Draft] = {
    database.get.get(id).fold[Future[Draft]](Future.failed(new NoSuchElementException(s"There is no form $id"))){
      case form: Draft => Future.successful(form)
      case _ => Future.failed(new IllegalStateException("There is form but it is not Draft"))
    }
  }

  override def findSubmitted(): Future[scala.collection.immutable.Iterable[GeneralForm]] = Future.successful {
    database.get.withFilter {
      case (_, form) => form.submitted.isDefined
    }.map(_._2)
  }

  override def findSubmitted(id: FormId): Future[SubmittedForm] = {
    database.get.get(id).fold[Future[SubmittedForm]](Future.failed(new NoSuchElementException(s"There is no form $id"))){
      case form: SubmittedForm => Future.successful(form)
      case _ => Future.failed(new IllegalStateException("There is form but it is not Draft"))
    }
  }
}
