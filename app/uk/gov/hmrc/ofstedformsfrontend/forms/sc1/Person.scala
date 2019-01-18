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

package uk.gov.hmrc.ofstedformsfrontend.forms.sc1

import enumeratum._
import org.joda.time.DateTime
import org.w3c.dom.{Document, Node}
import uk.gov.hmrc.ofstedformsfrontend.marshallers.xml.{EnumMarshaller, XmlMarshaller}

import scala.collection.immutable

case class Person(title: Title,
                  firstName: String,
                  surname: String,
                  dateOfBirth: DateTime,
                  PositionResponsibilityContactWithChildren: Option[String],
                  HasSubmitedOtherDCXOrCMXColumn: Boolean,
                  id: Option[Int],
                  IndividualId: Int) {
  require(firstName.length <= 50, "firstname length must be at most 50")
  require(surname.length <= 30, "surname length must be at most 30")
}

object Person {
  implicit val marshaller: XmlMarshaller[Person] = new XmlMarshaller[Person] {
    override def marshall(obj: Person)(implicit document: Document): Node = {
      createFragment(document){
        _.createValue("Title", obj.title)
          .createValue("FirstName", obj.firstName)
          .createValue("Surname", obj.surname)
          .createValue("DateOfBirth", obj.dateOfBirth)
          .createValue("PositionResponsibilityContactWithChildren", obj.PositionResponsibilityContactWithChildren)
          .createValue("HasSubmitedOtherDCXOrCMXColumn", obj.HasSubmitedOtherDCXOrCMXColumn)(XmlMarshaller.booleanMarshaller)
          .createValue("ID", obj.id)
          .createValue("IndividualId", obj.IndividualId)
      }
    }
  }
}

sealed trait Title extends EnumEntry

object Title extends Enum[Title] {
  val values: immutable.IndexedSeq[Title] = findValues

  case object Mr extends Title

  case object Mrs extends Title

  case object Miss extends Title

  case object Ms extends Title

  case object Other extends Title

  case object Master extends Title

  implicit val marshaller: XmlMarshaller[Title] = new EnumMarshaller[Title]
}