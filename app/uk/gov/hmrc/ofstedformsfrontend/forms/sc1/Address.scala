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

package uk.gov.hmrc.ofstedformsfrontend.forms.cs1

import org.joda.time.DateTime
import org.w3c.dom.{Document, DocumentFragment}
import uk.gov.hmrc.ofstedformsfrontend.marshallers.xml.XmlMarshaller

case class Addresses(value: Seq[Address])

object Addresses {
  implicit val marshaller: XmlMarshaller[Addresses] = new XmlMarshaller[Addresses] {
    override def marshall(obj: Addresses)(implicit document: Document): DocumentFragment = {
      createFragment(document) { fragment =>
        obj.value.foreach { address =>
          fragment.createValue("Address", address)
        }
      }
    }
  }
}

case class Address(id: Option[Int],
                   address1: String,
                   address2: String,
                   address3: Option[String],
                   town: String,
                   country: String,
                   postCode: String,
                   localAuthorityDescription: String,
                   localAuthorityCode: String,
                   regionId: Option[RegionIdType],
                   from: DateTime,
                   to: DateTime)

object Address {
  implicit val marshaller: XmlMarshaller[Address] = new XmlMarshaller[Address] {
    override def marshall(obj: Address)(implicit document: Document): DocumentFragment = {
      createFragment(document) {
        _.createValue("ID", obj.id)
          .createValue("Address1", obj.address1)
          .createValue("Address2", obj.address2)
          .createValue("Address3", obj.address3)
          .createValue("Town", obj.town)
          .createValue("Country", obj.country)
          .createValue("LocalAuthorityDescription", obj.localAuthorityCode)
          .createValue("LocalAuthorityCode", obj.localAuthorityCode)
          .createValue("RegionId", obj.regionId)
          .createValue("FromDate", obj.from)
          .createValue("ToDate", obj.to)
      }
    }
  }
}

/**
  * Reference A.10.32
  *
  * @param id - value of enumeration
  */
case class RegionIdType(id: Int)

object RegionIdType {
  implicit val marshaller: XmlMarshaller[RegionIdType] = new XmlMarshaller[RegionIdType] {
    override def marshall(obj: RegionIdType)(implicit document: Document): DocumentFragment = {
      createFragment(document) {
        _.createValue(obj.id)
      }
    }
  }
}
