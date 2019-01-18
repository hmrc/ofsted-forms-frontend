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
import org.w3c.dom.{Document, DocumentFragment}
import uk.gov.hmrc.ofstedformsfrontend.marshallers.xml.XmlMarshaller

case class Premises(settingName: String,
                    addressDetails: Address,
                    telephoneNumber: String, // FIXME type
                    faxNumber: Option[String], // FIXME type
                    emailAddress: Option[String], // FIXME type
                    localAuthority: Option[String],
                    areaIdType: Option[AreaIdType],
                    regionId: Option[RegionIdType],
                    overnightCare: Boolean,
                    specificRequirements: Option[String],
                    haveSoleUseOfPremises: Boolean,
                    readyForInspectorVisit: Boolean,
                    prposedPremisesReadyDate: DateTime,
                    noOfToilets: Int,
                    noOfWashbasins: Int,
                    otherPremises: OtherPremises) {
  require(telephoneNumber.length <= 30, "premises telephone length must be at most 30 ")
}

object Premises {
  implicit val marshaller: XmlMarshaller[Premises] = new XmlMarshaller[Premises] {
    override def marshall(obj: Premises)(implicit document: Document): DocumentFragment = {
      createFragment(document){ fragment =>
        fragment.createValue("AddressSameAsPrevious", "Null")
          .createValue("SettingName", obj.settingName)
          .createValue("AddressDetails", obj.addressDetails)
          .createValue("TelephoneNumber", obj.telephoneNumber)
          .createValue("OvernightCare", obj.overnightCare)(XmlMarshaller.yesNoMarshaller)
          .createValue("HaveSoleUseOfPremises", obj.haveSoleUseOfPremises)(XmlMarshaller.yesNoMarshaller)
          .createValue("ReadyForInspectorVisit", obj.readyForInspectorVisit)(XmlMarshaller.yesNoMarshaller)
          .createValue("PrposedPremisesReadyDate", obj.prposedPremisesReadyDate)
          .createValue("NoOfToilets", obj.noOfToilets)
          .createValue("NoOfWashbasins", obj.noOfWashbasins)
          .createValue("OtherPremises", obj.otherPremises)
      }
    }
  }
}

case class OtherPremises(addresses: Seq[Address])

object OtherPremises {
  implicit val marshaller: XmlMarshaller[OtherPremises] = new XmlMarshaller[OtherPremises] {
    override def marshall(obj: OtherPremises)(implicit document: Document): DocumentFragment = {
      createFragment(document){ fragment =>
        obj.addresses.foreach { address =>
          fragment.createValue("Address", address)
        }
      }
    }
  }
}

case class AreaIdType()