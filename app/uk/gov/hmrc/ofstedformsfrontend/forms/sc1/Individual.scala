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

import org.joda.time.DateTime
import org.w3c.dom.{Document, Node}
import uk.gov.hmrc.ofstedformsfrontend.marshallers.xml.XmlMarshaller

trait Individual

object Individual {
  implicit val marshaller: XmlMarshaller[Individual] = new XmlMarshaller[Individual] {
    override def marshall(obj: Individual)(implicit document: Document): Node = {
      obj match {
        case known: KnownIndividual => KnownIndividual.marshaller.marshall(known)
        case unknown: UnknownIndividual => UnknownIndividual.marshaller.marshall(unknown)
      }
    }
  }
}

case class KnownIndividual(id: Int, EY2Enclosed: Boolean) extends Individual

object KnownIndividual {
  implicit val marshaller: XmlMarshaller[KnownIndividual] = new XmlMarshaller[KnownIndividual] {
    override def marshall(obj: KnownIndividual)(implicit document: Document): Node = {
      createFragment(document){
        _.createValue("AlreadyKnownOfsted", "Yes")
          .createValue("EY2Enclosed", obj.EY2Enclosed)(XmlMarshaller.yesNoMarshaller)
          .createValue("IndividualId", obj.id)
      }
    }
  }
}

case class UnknownIndividual(EY2Enclosed: Boolean,
                             title: Title,
                             firstName: String,
                             surname: String,
                             dayOfBorn: DateTime,
                             receiveElectronicCommunication: Boolean) extends Individual

object UnknownIndividual {
  implicit val marshaller: XmlMarshaller[UnknownIndividual] = new XmlMarshaller[UnknownIndividual] {
    override def marshall(obj: UnknownIndividual)(implicit document: Document): Node = {
      createFragment(document){
        _.createValue("AlreadyKnownOfsted", "No")
          .createValue("EY2Enclosed", obj.EY2Enclosed)(XmlMarshaller.yesNoMarshaller)
          .createValue("IndividualId", "0") // TODO ask can we remove this
          .createValue("IndividualTitle", obj.title)
          .createValue("IndividualFirstNames", obj.firstName)
          .createValue("IndividualSurname", obj.surname)
          .createValue("IndividualDOB", obj.dayOfBorn)
          .createValue("IndividualReceiveElectronicCommunication", obj.receiveElectronicCommunication)(XmlMarshaller.yesNoMarshaller)
      }
    }
  }
}

