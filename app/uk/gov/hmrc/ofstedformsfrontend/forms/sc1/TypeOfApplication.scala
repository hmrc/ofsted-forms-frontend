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

import java.time.LocalDate
import enumeratum._
import org.w3c.dom.{Document, DocumentFragment}
import uk.gov.hmrc.ofstedformsfrontend.marshallers.xml.{EnumMarshaller, IntEnumMarshaller, XmlMarshaller}

import scala.collection.immutable

sealed abstract class SectorType(val value: Int) extends EnumEntry

object SectorType extends Enum[SectorType] {

  case object LocalAuthority extends SectorType(value = 3)

  case object HealthAuthority extends SectorType(value = 4)

  case object Voluntary extends SectorType(value = 2)

  case object Private extends SectorType(value = 1)

  val values: immutable.IndexedSeq[SectorType] = findValues

  implicit val marshaller: XmlMarshaller[SectorType] = new IntEnumMarshaller[SectorType]
}

sealed abstract class OwnershipType(val value: Int) extends EnumEntry

object OwnershipType extends Enum[OwnershipType] {

  case object Individual extends OwnershipType(1)

  case object LocalAuthority extends OwnershipType(2)

  case object Organisation extends OwnershipType(3)

  val values: immutable.IndexedSeq[OwnershipType] = findValues

  implicit val marshaller: XmlMarshaller[OwnershipType] = new EnumMarshaller[OwnershipType]
}

sealed abstract class SectorId(val value: Int) extends EnumEntry

object SectorId extends Enum[SectorId] {

  case object Private extends SectorId(1)

  case object Voluntary extends SectorId(2)

  case object LocalAuth extends SectorId(3)

  case object HealthAuth extends SectorId(4)

  val values: immutable.IndexedSeq[SectorId] = findValues

  implicit val marshaller: XmlMarshaller[SectorId] = new IntEnumMarshaller[SectorId]
}

sealed trait ProvisionTypeType

object ProvisionTypeType {

  case object Childminder extends ProvisionTypeType

  case object DayCare extends ProvisionTypeType

  case object CMNetwork extends ProvisionTypeType

  case object PortageScheme extends ProvisionTypeType

  case object IndependentSchool extends ProvisionTypeType

  case object ChildrensHome extends ProvisionTypeType

  case object VoluntaryAdoptionAgency extends ProvisionTypeType

  case object IndependentFosteringAgency extends ProvisionTypeType

  case object AdoptionSupportAgency extends ProvisionTypeType

  case object ResidentialFamilyCentre extends ProvisionTypeType

  case object BoardingSchool extends ProvisionTypeType

  case object FurtherEducationCollege extends ProvisionTypeType

  case object ResidentialSpecialSchool extends ProvisionTypeType

  case object LocalAuthorityAdoptionAgency extends ProvisionTypeType

  case object LocalAuthorityFosteringAgency extends ProvisionTypeType

  case object PrivateFosteringArrangements extends ProvisionTypeType

//  val values: immutable.IndexedSeq[ProvisionTypeType] = findValues

  implicit val marshaller: XmlMarshaller[ProvisionTypeType] = ???
}

case class SCTypeOfApplicationType(applicationType: ProvisionTypeType,
                                   sectorId: SectorType,
                                   applyingAs: OwnershipType,
                                   OnSchoolPremises: Boolean,
                                   OfstedSchoolReferenceNumber: Option[String],
                                   SchoolName: Option[String],
                                   PurchaseExisting: Boolean,
                                   TargetOpeningDate: LocalDate)

object SCTypeOfApplicationType {
  implicit val marshaller: XmlMarshaller[SCTypeOfApplicationType] = new XmlMarshaller[SCTypeOfApplicationType] {
    override def marshall(obj: SCTypeOfApplicationType)(implicit document: Document): DocumentFragment = {
      createFragment(document) { fragment =>
        fragment.createValue("ApplicationType", obj.applicationType)
          .createValue("SectorId", obj.sectorId)
          .createValue("ApplyingAs", obj.applyingAs)
          .createValue("OnSchoolPremises", obj.OnSchoolPremises)(XmlMarshaller.yesNoMarshaller)
          .createValue("OfstedSchoolReferenceNumber", obj.OfstedSchoolReferenceNumber)
          .createValue("PurchaseExisting", obj.PurchaseExisting)(XmlMarshaller.yesNoMarshaller)
          .createValue("TargetOpeningDate", obj.TargetOpeningDate)
      }
    }
  }

  object Uniform {

    import org.atnos.eff._
    import ltbs.uniform._

    type Stack = Fx.fx7[
      UniformAsk[ProvisionTypeType, ?],
      UniformAsk[SectorType, ?],
      UniformAsk[OwnershipType, ?],
      UniformAsk[Boolean, ?],
      UniformAsk[Int, ?],
      UniformAsk[Option[String], ?],
      UniformAsk[LocalDate, ?]
    ]


    def program[S: _uniform[ProvisionTypeType, ?]: _uniform[SectorType, ?]: _uniform[OwnershipType, ?]: _uniform[Boolean, ?]: _uniform[Int, ?]: _uniform[Option[String], ?]: _uniform[LocalDate, ?]] = for {
      applicationType <- uask[S, ProvisionTypeType]("applicationType")
      sectorId <- uask[S, SectorType]("sectorId")
      applyingAs <- uask[S, OwnershipType]("applyingAs")
      onSchoolPremises <- uask[S, Boolean]("onSchoolPremises")
      ofstedSchoolReferenceNumber <- uask[S, Option[String]]("ofstedSchoolReferenceNumber")
      schoolName <- uask[S, Option[String]]("schoolName")
      purchaseExisting <- uask[S, Boolean]("purchaseExisting")
      targetOpeningDate <- uask[S, LocalDate]("tragetOpeningDate")
    } yield SCTypeOfApplicationType(applicationType, sectorId, applyingAs, onSchoolPremises, ofstedSchoolReferenceNumber, schoolName,  purchaseExisting, targetOpeningDate)

//    def translate[R, U, E] = new Translate[UniformAsk[SCTypeOfApplicationType, ?], U] {
//      override def apply[X](kv: UniformAsk[SCTypeOfApplicationType, X]): Eff[U, X] = {
//        ???
//      }
//    }
  }
  //
  //  implicit def journey[R]: _uniform[SCTypeOfApplicationType, R] =
}