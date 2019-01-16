package uk.gov.hmrc.ofstedformsfrontend.forms.cs1

import org.joda.time.DateTime
import org.w3c.dom.{Document, DocumentFragment}
import uk.gov.hmrc.ofstedformsfrontend.marshallers.xml.XmlMarshaller

case class Premises(settingName: String,
                    addressDetails: Address,
                    telephoneNumber: String, // FIXME type
                    faxNumber: String, // FIXME type
                    emailAddress: String, // FIXME type
                    localAuthority: String,
                    areaIdType: AreaIdType,
                    regionId: RegionIdType,
                    overnightCare: Boolean,
                    specificRequirements: String,
                    haveSoleUseOfPremises: Boolean,
                    readyForInspectorVisit: Boolean,
                    prposedPremisesReadyDate: DateTime,
                    noOfToilets: Int,
                    nOfWashbasins: Int,
                    otherPremises: OtherPremises)

// FIXME add missing values
case class AreaIdType()

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