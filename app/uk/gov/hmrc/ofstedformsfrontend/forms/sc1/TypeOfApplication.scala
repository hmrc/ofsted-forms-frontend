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

import enumeratum._
import enumeratum.values.{IntEnum, IntEnumEntry}
import org.joda.time.LocalDateTime

import scala.collection.immutable

class SCTypeOfApplicationType(applicationType: ProvisionTypeType,
                              sectorId: SectorType,
                              applyingAs: OwnershipType,
                              OnSchoolPremises: Boolean,
                              OfstedSchoolReferenceNumber: String,
                              SchoolName: String,
                              PurchaseExisting: Boolean,
                              TargetOpeningDate: LocalDateTime)

sealed abstract class SectorType(val value: Int) extends IntEnumEntry

object SectorType extends IntEnum[SectorType] {
  val values = findValues

  case object LocalAuthority extends SectorType(value = 3)

  case object HealthAuthority extends SectorType(value = 4)

  case object Voluntary extends SectorType(value = 2)

  case object Private extends SectorType(value = 1)

}

sealed abstract class OwnershipType(val value: Int) extends IntEnumEntry

object OwnershipType extends IntEnum[OwnershipType] {
  val values: immutable.IndexedSeq[OwnershipType] = findValues

  case object Individual extends OwnershipType(1)

  case object LocalAuthority extends OwnershipType(2)

  case object Organisation extends OwnershipType(3)
}

sealed abstract class SectorId(val value: Int) extends IntEnumEntry

object SectorId extends IntEnum[SectorId] {
  val values: immutable.IndexedSeq[SectorId] = findValues

  case object Private extends SectorId(1)

  case object Voluntary extends SectorId(2)

  case object LocalAuth extends SectorId(3)

  case object HealthAuth extends SectorId(4)
}


sealed trait ProvisionTypeType extends EnumEntry

object ProvisionTypeType extends Enum[ProvisionTypeType] {
  val values: immutable.IndexedSeq[ProvisionTypeType] = findValues

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

}
