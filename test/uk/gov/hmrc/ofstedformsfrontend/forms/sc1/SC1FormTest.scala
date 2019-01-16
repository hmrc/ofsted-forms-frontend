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

import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.{OutputKeys, TransformerFactory}
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import org.joda.time.DateTime
import org.scalatest.FunSuite
import uk.gov.hmrc.ofstedformsfrontend.forms
import uk.gov.hmrc.ofstedformsfrontend.marshallers.xml.XmlMarshaller

class SC1FormTest extends FunSuite {

  val typeOfApplication = new SCTypeOfApplicationType(
    ProvisionTypeType.ChildrensHome,
    SectorType.Private,
    OwnershipType.Organisation,
    OnSchoolPremises = false,
    Some("0"),
    None,
    PurchaseExisting = false,
    DateTime.parse("2019-05-01T00:00:00")
  )

  val organization = Organisation(
    OrganisationId(0),
    OrganisationType.Company,
    name = "Swansea Care Group Ltd",
    Address(
      None,
      "Swansea House",
      "New Road",
      None,
      town = "Burscough",
      county = "Lancashire",
      postCode = "L40 1RX",
      "",
      "888",
      None,
      None,
      None
    ),
    "01792123456",
    DateTime.parse("2015-05-18T00:00:00"),
    Some(""),
    Some("09596207"),
    None,
    RegisteredPersonsDetails(Seq(
      Person(
        Title.Mr,
        "Alex",
        "Test",
        DateTime.parse("1982-11-21T00:00:00"),
        PositionResponsibilityContactWithChildren = Some("Director"),
        HasSubmitedOtherDCXOrCMXColumn = true,
        id = Some(0),
        IndividualId = 0)
    ))
  )

  val nominated = UnknownIndividual(
    EY2Enclosed = false,
    Title.Mr,
    "Thomas",
    "Ghannad",
    DateTime.parse("1984-12-21T00:00:00"),
    receiveElectronicCommunication = false
  )

  val address = Address(
    None,
    "4 Swansea Route",
    "",
    None,
    "Southport",
    "Merseyside",
    "PR9 1NN",
    "",
    "343",
    None,
    None,
    None
  )

  val premise = new Premises(
    "Swansea House",
    address,
    "01792123456",
    None,
    None,
    None,
    None,
    None,
    overnightCare = false,
    specificRequirements = None,
    haveSoleUseOfPremises = true,
    readyForInspectorVisit = true,
    prposedPremisesReadyDate = DateTime.parse("0001-01-01T00:00:00"),
    noOfToilets = 1,
    noOfWashbasins = 1,
    otherPremises = OtherPremises(Seq.empty)
  )

  val provision = Provision(
    6,
    5,
    ChildrensHome(2, 0, 0, 2, 0, 0, 0, 0, 0, 2, 0, AdultCare = false, ""),
    ResidentialFamilyCenter(0, false, false, false, false, false, false),
    IndependentFosteringAgency(),
    IndependentAdoptionAgency(),
    AdoptionsSupportAgency(),
    9,
    17
  )

  val manager = UnknownIndividual(
    EY2Enclosed = false,
    Title.Mrs,
    "test\t",
    "test",
    DateTime.parse("1984-12-21T00:00:00"),
    receiveElectronicCommunication = false
  )

  val sc1 = SC1Form(typeOfApplication, organization, nominated, premise, provision, manager)

  val builderFactory = DocumentBuilderFactory.newInstance()
  val builder = builderFactory.newDocumentBuilder()
  val document = builder.newDocument()
  val marshaller = implicitly[XmlMarshaller[SC1Form]]
  val fragemtn = marshaller.marshall(sc1)(document)
  val ele = document.createElement("SC1")
  ele.appendChild(fragemtn)
  document.appendChild(ele)
  val transformerFactory = TransformerFactory.newInstance()
  val transformer = transformerFactory.newTransformer()
  transformer.setOutputProperty(OutputKeys.INDENT, "yes")
  transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2")
  val source = new DOMSource(document)
  val target = new StreamResult(System.out)
  transformer.transform(source, target)

}
