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
import enumeratum.values.{IntEnum, IntEnumEntry}
import org.joda.time.DateTime
import org.w3c.dom.{Document, Node}
import uk.gov.hmrc.ofstedformsfrontend.marshallers.xml.{EnumMarshaller, IntEnumMarshaller, XmlMarshaller}

case class Organisation(id: OrganisationId,
                        organizationType: OrganisationType,
                        name: String,
                        address: Address,
                        phone: String,
                        dateOfBeing: DateTime,
                        registratedCharityNo: Option[String],
                        companyRegistrationNo: Option[String],
                        subsidiary: Option[Boolean],
                        registeredPersonsDetails: RegisteredPersonsDetails) {
  require(name.length <= 100, "organization name length must be at most 100")
  require(phone.length <= 30, "organization phone legnth must be at most 30")
}

object Organisation {
  implicit val marshaller: XmlMarshaller[Organisation] = new XmlMarshaller[Organisation] {
    override def marshall(obj: Organisation)(implicit document: Document): Node = {
      createFragment(document){
        _.createValue("OrganisationId", obj.id)
          .createValue("OrganisationType", obj.organizationType)
          .createValue("OrganisationName", obj.name)
          .createValue("OrgAddress", obj.address)
          .createValue("OrganisationTelNo", obj.phone)
          .createValue("OrganisationDateOfBeing", obj.dateOfBeing)
          .createElement("RegisteredCharityNo"){ _.createValue(obj.registratedCharityNo) }
          .createValue("CompanyRegistrationNo", obj.companyRegistrationNo)
          .createValue("RegisteredPersonsDetails", obj.registeredPersonsDetails)
      }
    }
  }
}

case class OrganisationId(value: Int)

object OrganisationId {
  implicit val marshaller: XmlMarshaller[OrganisationId] = new XmlMarshaller[OrganisationId] {
    override def marshall(obj: OrganisationId)(implicit document: Document): Node = {
      document.createTextNode(obj.value.toString)
    }
  }
}

sealed abstract class OrganisationType(val value: Int) extends EnumEntry

object OrganisationType extends Enum[OrganisationType] {
  val values = findValues

  case object Partnership extends OrganisationType(4)

  case object Company extends OrganisationType(7)

  case object StatutoryBody extends OrganisationType(6)

  case object Committee extends OrganisationType(3)

  case object Other extends OrganisationType(8)

  implicit val marshaller: XmlMarshaller[OrganisationType] = new EnumMarshaller[OrganisationType]
}

case class RegisteredPersonsDetails(persons: Seq[Person])

object RegisteredPersonsDetails {
  implicit val marshaller: XmlMarshaller[RegisteredPersonsDetails] = new XmlMarshaller[RegisteredPersonsDetails] {
    override def marshall(obj: RegisteredPersonsDetails)(implicit document: Document): Node = {
      createFragment(document){ fragment =>
        obj.persons.foreach { person =>
          fragment.createElement("RegisteredPerson"){ element =>
            element.createValue("Person", person)
          }
        }
      }
    }
  }
}

