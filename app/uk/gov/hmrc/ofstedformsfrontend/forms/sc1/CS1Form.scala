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
import java.time.{LocalDate => Date}

import org.w3c.dom.{Document, DocumentFragment}
import uk.gov.hmrc.ofstedformsfrontend.marshallers.xml.XmlMarshaller

sealed trait ServiceType extends EnumEntry

object ServiceType extends Enum[ServiceType] {

  val values = findValues

  case object ChildrenHome extends ServiceType

  case object AdoptionSupportingAgency extends ServiceType

  case object IndependentFosteringAgency extends ServiceType

  case object ResidentialFamilyCentre extends ServiceType

  case object VoluntaryAdoption extends ServiceType

  case object ResidentialHolidayScheme extends ServiceType

}

sealed trait ApplicantType extends EnumEntry

object ApplicantType extends Enum[ApplicantType] {
  val values = findValues

  case object Individual extends ApplicantType

  case object Organisation extends ApplicantType

  case object Partnership extends ApplicantType

}

sealed trait Act extends EnumEntry

object Act extends Enum[Act] {
  val values = findValues

  case object RegHomes1984 extends Act

  case object RegHomes1991 extends Act

  case object ChildrenAct1989 extends Act

  case object ChildcareAct2006 extends Act

  case object NursesAgenciesAct1957 extends Act

  case object CareStdAct2000 extends Act

  case object HealthSocialCareAct2008 extends Act

}

sealed trait Applicant

//case class Individual(title: String,
//                      firstName: String,
//                      surname: String,
//                      dob: Date,
//                      position: String,
//                      management: Boolean,
//                      contactChildren: Boolean) extends Applicant

sealed trait OrgOrPartnership extends Applicant {
  def refusedApplication: Option[RefusedApplication]

  def name: String

  def address: Address
}

sealed trait OrganisationSector extends EnumEntry

case class Subsidiary(name: String, //
                      address: Address, //
                      email: String, //
                      telephone: String, //
                      ofstedRegNo: String)

case class HoldingCompany(name: String, //
                          address: Address, //
                          email: String, //
                          telephone: String, //
                          creationDate: Date, //
                          charityNumber: Option[String], //
                          companyNumber: Option[String], //
                          subsidiaries: Option[Subsidiary])

//case class Organisation(refusedApplication: Option[RefusedApplication], //
//                        orgSector: OrganisationSector, //
//                        orgType: OrganisationType, //
//                        name: String, //
//                        address: Address, //
//                        email: String, //
//                        telephone: String, //
//                        creationDate: Date, //
//                        charityNumber: Option[String], //
//                        companyNumber: Option[String], //
//                        commsOptOut: Boolean, //
//                        holdingCompany: Option[HoldingCompany], //
//                        manager: Individual, //
//                        members: Option[Individual], // list
//                        membersDisqualified: Option[Individual]) extends OrgOrPartnership

//case class Partnership(refusedApplication: Option[RefusedApplication],
//                       name: String,
//                       address: Address,
//                       members: Option[Individual]) extends OrgOrPartnership

case class RegisteredEstablishmentOrAgency(name: String,
                                           address: Address,
                                           ofstedRegNo: Option[String])

case class Cs1Form(serviceType: ServiceType,
                   applicantType: ApplicantType,
                   isEducationalEstablishment: Boolean,
                   dfeNumber: Option[String],
                   rea: Option[RegisteredEstablishmentOrAgency],
                   financialViability: Option[String],
                   interestsRea: Option[RegisteredEstablishmentOrAgency],
                   applicableActs: Set[Act],
                   moreDetails: Option[String],
                   targetOpDate: Date,
                   applicant: Applicant)

case class RefusedApplication(refAppName: String,
                              refAppOfstedRegNo: String,
                              refAppOrgID: String,
                              refAppDetails: String)
