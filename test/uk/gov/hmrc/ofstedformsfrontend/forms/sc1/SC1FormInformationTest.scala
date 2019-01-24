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

import java.io.StringWriter

import javax.xml.parsers.{DocumentBuilder, DocumentBuilderFactory}
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.{OutputKeys, TransformerFactory}
import org.joda.time.DateTime
import org.scalatest.matchers.{MatchResult, Matcher}
import org.scalatest.{Matchers, WordSpec}
import org.w3c.dom.{Document, Node, NodeList}
import uk.gov.hmrc.ofstedformsfrontend.communication.SendApplicationForms
import uk.gov.hmrc.ofstedformsfrontend.forms._
import uk.gov.hmrc.ofstedformsfrontend.forms.envelop._

import scala.annotation.tailrec

class SC1FormInformationTest extends WordSpec with Matchers {

  implicit class NodeListExtension(nodeList: NodeList){
    def toMap: Map[String, Seq[Node]] = {
      @tailrec
      def loop(index: Int, acc: Map[String, Seq[Node]]): Map[String, Seq[Node]] = {
        if(index < nodeList.getLength){
          val node = nodeList.item(index)
          val name = node.getNodeName
          println(s"$name '${node.getNodeValue}'")
          loop(index + 1, acc.updated(name, acc.getOrElse(name, Seq.empty) :+ node))
        } else {
          acc
        }
      }
      loop(0, Map.empty)
    }
  }

  def sameAs(requirements: Document): Matcher[Document] = new Matcher[Document]{

    def compareNodes(prefix: String, a: Node, b: Node): MatchResult = {
      if(a.getNodeName == b.getNodeName){
        val x = a.getChildNodes.toMap
        val y = b.getChildNodes.toMap
        val xElements = x.keySet
        val yElements = y.keySet
        val more = xElements.diff(yElements)
        val less = yElements.diff(xElements)
        if(more.nonEmpty || less.nonEmpty){
          val redundant = if(more.nonEmpty) { "redundat elements " + more.mkString("{",",","}") } else {" "}
          val missing = if(less.nonEmpty) { "missing elements" + less.mkString("{", ",", "}") } else { "" }
          MatchResult.apply(
            matches = false,
            s"Documents have different $redundant $missing at $prefix",
            s"Documents have identical keys at $prefix"
          )
        } else {
          more.intersect(less).foldLeft[MatchResult](MatchResult(matches = true, "Nodes are equal", "Nodes are not equal")) {
            case (prev, name) =>
              println(name)
              val result = compareNodeList(prefix + s".$name", x(name), y(name))
              if(result.matches){
                prev
              } else {
                result
              }
          }
        }
      } else {
        MatchResult(
          matches = false,
          s"Node name ${a.getNodeName} is not equal ${b.getNodeName} at $prefix",
          s"Node name ${b.getNodeName} is equal ${b.getNodeName} at $prefix"
        )
      }
    }

    def compareNodeList(prefix: String, a: Seq[Node], b: Seq[Node]): MatchResult = {
      val redundant = a.foldLeft[MatchResult](MatchResult(matches = true, "Nodes are equal", "Nodes are not equal")) {
        case (result, node) =>
          b.foldLeft[MatchResult](result) {
            case (outcome, x) =>
              val comparision = compareNodes(prefix, node, x)
              if(comparision.matches){
                outcome
              } else {
                comparision
              }
          }
      }
      b.foldLeft[MatchResult](redundant) {
        case (result, node) =>
          b.foldLeft[MatchResult](result) {
            case (outcome, x) =>
              val comparision = compareNodes(prefix, node, x)
              if(comparision.matches){
                outcome
              } else {
                comparision
              }
          }
      }
    }

    override def apply(left: Document): MatchResult = {
      compareNodes(".", left.getDocumentElement, requirements.getDocumentElement)
    }
  }

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

  val sc1 = SC1FormInformation(typeOfApplication, organization, nominated, premise, provision, manager)

  val form = ApplicationForm(
    id = 1,
    form = sc1,
    "rbhatt",
    DateTime.parse("2018-11-28T01:45:34"),
    ApplicationSource.Online,
    0,
    "C50329212",
    "2510737",
    Documents(Seq(Attachment("Ofsted_SC1", "doc", "Base64String"))),
    FormMetadata()
  )

  val sendApplicationFormEvelop = SendApplicationForms(Seq(form))

  val builderFactory: DocumentBuilderFactory = DocumentBuilderFactory.newInstance()
  builderFactory.setIgnoringElementContentWhitespace(true)
  val builder: DocumentBuilder = builderFactory.newDocumentBuilder()
  val document: Document = sendApplicationFormEvelop.toDocument(builder)

  private val source: Document = builder.parse(ClassLoader.getSystemResourceAsStream("SC1-full.xml"))


  "Form" should {
    "create correct document" in {
      document should sameAs(source)
    }
  }

  def print(document: Document): Unit ={
    val transformerFactory = TransformerFactory.newInstance()
    val indentingTransformer = transformerFactory.newTransformer()
    indentingTransformer.setOutputProperty(OutputKeys.INDENT, "yes")
    indentingTransformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no")
    indentingTransformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4")
    val writer = new StringWriter()
    val source = new DOMSource(document)
    val target = new StreamResult(writer)

    println(writer.toString)
  }


}
