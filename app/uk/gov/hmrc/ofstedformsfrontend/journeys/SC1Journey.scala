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

package uk.gov.hmrc.ofstedformsfrontend.journeys

import uk.gov.hmrc.ofstedformsfrontend.forms.sc1._
import ltbs.uniform._
import org.atnos.eff._
import cats.implicits._
import java.time.LocalDate


import scala.concurrent.Future

//import java.io.File
case class File(file: String) // not supported by JS

object Sc1Journey {

  //  type Stack = Fx.append[Fx.append[
  //    Fx.fx12[
  //      UniformAsk[ServiceType,?],
  //      UniformAsk[ApplicantType,?],
  //      UniformAsk[Boolean,?],
  //      UniformAsk[Option[String],?],
  //      UniformAsk[Option[RegisteredEstablishmentOrAgency],?],
  //      UniformAsk[Set[Act],?],
  //      UniformAsk[String,?],
  //      UniformAsk[Option[RefusedApplication],?],
  //      UniformAsk[Date,?],
  //      UniformAsk[OrganisationSector,?],
  //      UniformAsk[OrganisationType,?],
  //      UniformAsk[Address,?]
  //      ],
  //    Fx.fx11[
  //      UniformAsk[HoldingCompany,?],
  //      UniformAsk[Option[Individual],?],
  //      UniformAsk[Individual,?],
  //      UniformAsk[Site,?],
  //      UniformAsk[Establishment,?],
  //      UniformAsk[Option[Option[File]],?],
  //      UniformAsk[Signature,?],
  //      UniformAsk[Option[Staff],?],
  //      UniformAsk[File,?],
  //      UniformAsk[(Int,Int,Boolean),?],
  //      UniformAsk[Int,?]
  //      ]],
  //    Fx.fx7[
  //      UniformAsk[ChildrensHomeType,?],
  //      UniformAsk[Set[VoluntaryAdoptionType],?],
  //      UniformAsk[IndependentFosteringType,?],
  //      UniformAsk[Set[AdoptionSupportType],?],
  //      UniformAsk[(Int,Int),?],
  //      UniformAsk[AdoptionSupportingAgencyServices, ?],
  //      UniformAsk[MaxChildrenPerCategory, ?]
  //      ]
  //    ]
  //
  //  def program[R
  //  : _uniform[ServiceType,?]
  //  : _uniform[ApplicantType,?]
  //  : _uniform[Boolean,?]
  //  : _uniform[Option[String],?]
  //  : _uniform[Option[RegisteredEstablishmentOrAgency],?]
  //  : _uniform[Set[Act],?]
  //  : _uniform[String,?]
  //  : _uniform[Option[RefusedApplication],?]
  //  : _uniform[Date,?]
  //  : _uniform[OrganisationSector,?]
  //  : _uniform[OrganisationType,?]
  //  : _uniform[Address,?]
  //  : _uniform[HoldingCompany,?]
  //  : _uniform[Option[Individual],?]
  //  : _uniform[Individual,?]
  //  : _uniform[Site,?]
  //  : _uniform[Establishment,?]
  //  : _uniform[Option[Option[File]],?]
  //  : _uniform[Signature,?]
  //  : _uniform[Option[Staff],?]
  //  : _uniform[File,?]
  //  : _uniform[(Int,Int),?]
  //  : _uniform[(Int,Int,Boolean),?]
  //  : _uniform[Int,?]
  //  : _uniform[ChildrensHomeType,?]
  //  : _uniform[Set[VoluntaryAdoptionType],?]
  //  : _uniform[IndependentFosteringType,?]
  //  : _uniform[Set[AdoptionSupportType],?]
  //  : _uniform[AdoptionSupportingAgencyServices,?]
  //  : _uniform[MaxChildrenPerCategory, ?]
  //  ]: Eff[R, Cs1Form] = {
  //
  //    // the 'or' boolean monoid - we need this for 'emptyUnless'
  //    implicit val mbool = new cats.Monoid[Boolean] {
  //      def empty = false
  //      def combine(a: Boolean, b: Boolean) = a || b
  //    }
  //
  //    def applicantProgram(applicantType: ApplicantType): Eff[R, Applicant] = {
  //      applicantType match {
  //        case ApplicantType.Individual =>
  //          uask[R,Individual]("individual").map{x => x : Applicant}
  //        case ApplicantType.Organisation => (
  //          uask[R,Option[RefusedApplication]]("refusedApplication"),
  //          uask[R,OrganisationSector]("orgSector"),
  //          uask[R,OrganisationType]("orgType"),
  //          uask[R,String]("organisationName"),
  //          uask[R,Address]("address"),
  //          uask[R,String]("email"),
  //          uask[R,String]("telephone"),
  //          uask[R,Date]("creationDate"),
  //          uask[R,Option[String]]("charityNumber"),
  //          uask[R,Option[String]]("companyNumber"),
  //          uask[R,Boolean]("commsOptOut"),
  //          uask[R,HoldingCompany]("holdingCompany") when uask[R,Boolean]("holdingCompanyOption"),
  //          uask[R,Individual]("manager"), //
  //          uask[R,Option[Individual]]("organisationMembers"), // list
  //          uask[R,Option[Individual]]("membersDisqualified") // list
  //        ).mapN(Organisation.apply)
  //        case ApplicantType.Partnership => (
  //          uask[R,Option[RefusedApplication]]("refusedApplication"),
  //          uask[R,String]("partnershipName"),
  //          uask[R,Address]("address"),
  //          uask[R,Option[Individual]]("partnershipMembers")
  //        ).mapN(Partnership.apply)
  //      }
  //    }
  //
  //    def serviceProgram(serviceType: ServiceType): Eff[R, Service] = serviceType match {
  //      case ServiceType.ChildrenHome => (
  //        uask[R, ChildrensHomeType]("establishmentType"),
  //        uask[R, Int]("regPlacesNo"),
  //        uask[R, Boolean]("homeSingleSex"),
  //        uask[R, MaxChildrenPerCategory]("maxChildrenPerCategory"),
  //        uask[R, Option[String]]("care4Adults"),
  //        uask[R, (Int, Int)]("ageRange"),
  //        uask[R, Option[String]]("childrenSelectionCriteria"),
  //        uask[R, String]("facilitiesServiceDetails"),
  //        uask[R, String]("protectingHealthDetails"),
  //        uask[R, String]("fireEmergencyPrecautions"),
  //        uask[R, String]("religiousObservence"),
  //        uask[R, String]("contactChildRelatives"),
  //        uask[R, String]("stepsHomeLocation"),
  //        uask[R, String]("childrenConcernsComplaints"),
  //        uask[R, String]("childrenEducation"),
  //        uask[R, String]("placementReviewing"),
  //        uask[R, Boolean]("indepPersonReg43CH2015"),
  //        uask[R, Boolean]("establishmentCareQualCom"),
  //        uask[R, Boolean]("shortBreaksService")
  //      ).mapN(ChildrenHome)
  //
  //      case ServiceType.AdoptionSupportingAgency => (
  //        uask[R, Set[AdoptionSupportType]]("adoptionSupportType"),
  //        uask[R, AdoptionSupportingAgencyServices]("adoptionSupportingAgencyServices")
  //      ).mapN(AdoptionSupportingAgency)
  //
  //      case ServiceType.IndependentFosteringAgency => (
  //        uask[R, IndependentFosteringType]("independentFosteringType"),
  //        uask[R, Boolean]("parentChildrenAct1989"),
  //        uask[R, Boolean]("panel"),
  //        uask[R, Boolean]("panelVarious"),
  //        uask[R, Boolean]("panelJoint")
  //      ).mapN(IndependentFosteringAgency)
  //
  //      case ServiceType.ResidentialFamilyCentre => (
  //        uask[R, Int]("familiesAccomodated"),
  //        uask[R, Boolean]("emergencyAdmissions"),
  //        uask[R, Boolean]("courtReferrals"),
  //        uask[R, Boolean]("refuge"),
  //        uask[R, Boolean]("careForAdultsWithoutChildren"),
  //        uask[R, Boolean]("admitChildrenWithCarers"),
  //        uask[R, Boolean]("otherService")
  //      ).mapN(ResidentialFamilyCentre)
  //
  //      case ServiceType.VoluntaryAdoption => (
  //        uask[R, Set[VoluntaryAdoptionType]]("voluntaryAdoptionType"),
  //        uask[R, Option[String]]("branches"),
  //        uask[R, Boolean]("panel"),
  //        uask[R, Boolean]("panelMoreBranches"),
  //        uask[R, Boolean]("birthRecordsCounselling"),
  //        uask[R, Boolean]("intermediaryServices"),
  //        uask[R, Set[AdoptionSupportType]]("supportProvidedDetail") emptyUnless uask[R, Boolean]("supportProvided")
  //      ).mapN(VoluntaryAdoption)
  //
  //      case ServiceType.ResidentialHolidayScheme => (
  //        uask[R, (Int, Int)]("ageRange"),
  //        uask[R, Boolean]("singleSex"),
  //        uask[R, Option[String]]("selectionCriteria"),
  //        uask[R, String]("facilitiesServicesDetails"),
  //        uask[R, String]("missingChildrenProcedure"),
  //        uask[R, String]("concernsComplaints")
  //      ).mapN(ResidentialHolidayScheme)
  //    }
  //
  //    for {
  //      signature                  <- uask[R, Signature]("signature")
  //      serviceType                <- uask[R, ServiceType]("serviceType")
  //      applicantType              <- uask[R, ApplicantType]("applicantType")
  //      isEducationalEstablishment <- uask[R, Boolean]("isEducationalEstablishment")
  //      dfeNumber                  <- uask[R, Option[String]]("defNumber") emptyUnless isEducationalEstablishment
  //      rea                        <- uask[R, Option[RegisteredEstablishmentOrAgency]]("rea")
  //      financialViability         <- uask[R, Option[String]]("financialViability")
  //      interestsRea               <- uask[R, Option[RegisteredEstablishmentOrAgency]]("interestsRea")
  //      applicableActs             <- uask[R, Set[Act]]("applicableActs")
  //      moreDetails                <- uask[R, String]("moreDetails") when (applicableActs.nonEmpty)
  //      targetOpDate               <- uask[R, Date]("targetOpDate")
  //      applicant                  <- applicantProgram(applicantType)
  //      establishment              <- uask[R, Establishment]("establishment")
  //      otherPremises              <- uask[R, Site]("otherPremises") when (uask[R, Boolean]("haveOtherPremises")) // Option[Site] not compiling for some reason
  //      travelArrangements         <- uask[R, String]("travelArrangements") when (otherPremises.toList.size >1)
  //      planningPermit             <- if (
  //        List(
  //          ServiceType.AdoptionSupportingAgency, ServiceType.IndependentFosteringAgency,
  //          ServiceType.VoluntaryAdoption, ServiceType.ChildrenHome, ServiceType.ResidentialFamilyCentre
  //        ) contains serviceType
  //      ) (uask[R, Option[Option[File]]]("planningPermit")) else (Eff.pure[R,Option[Option[File]]](None))
  //      okWithoutPermitChange      <- uask[R, Boolean]("okWithoutPermitChange") when (
  //        serviceType == ServiceType.ResidentialHolidayScheme)
  //      accessReqDisabilityAct2005 <- uask[R, Option[String]]("accessReqDisabilityAct2005")
  //      premisesSecurity           <- uask[R, String]("premisesSecurity")
  //      staffPosts                 <- uask[R, Option[Staff]]("staffPosts") // list
  //      staffPostsTimes            <- uask[R, (Int,Int,Boolean)]("staffPostsTimes") emptyUnless staffPosts.nonEmpty
  //      service                      <- serviceProgram(serviceType)
  //      establishmentChargesRange     <- uask[R, String]("establishmentChargesRange")
  //      managerTitle                  <- uask[R, String]("managerTitle")
  //      managerForename               <- uask[R, String]("managerForename")
  //      managerSurname                <- uask[R, String]("managerSurname")
  //      managerOtherAgency            <- uask[R, Option[String]]("managerOtherAgency")
  //      checksAdoptionRegulations2005 <- uask[R, Boolean]("checksAdoptionRegulations2005")
  //      proceduresAndPolicies         <- uask[R, File]("proceduresAndPolicies")
  //      noPreviousApplicationSince30Sep2010 <- uask[R, Boolean]("noPreviousApplicationSince30Sep2010")
  //
  //      researchOrgComsOptOut               <- uask[R, Boolean]("researchOrgComsOptOut")
  //    } yield Cs1Form(
  //      serviceType,
  //      applicantType,
  //      isEducationalEstablishment,
  //      dfeNumber,
  //      rea,
  //      financialViability,
  //      interestsRea,
  //      applicableActs,
  //      moreDetails,
  //      targetOpDate,
  //      applicant,
  //      establishment,
  //      otherPremises,
  //      travelArrangements,
  //      planningPermit,
  //      okWithoutPermitChange,
  //      accessReqDisabilityAct2005,
  //      premisesSecurity,
  //      staffPosts,
  //      staffPostsTimes,
  //      service,
  //      establishmentChargesRange,
  //      managerTitle,
  //      managerForename,
  //      managerSurname,
  //      managerOtherAgency,
  //      checksAdoptionRegulations2005,
  //      proceduresAndPolicies,
  //      noPreviousApplicationSince30Sep2010,
  //      signature,
  //      researchOrgComsOptOut
  //    )
  //  }

  type Stack = // Fx.append[
    SCTypeOfApplicationType.Uniform.Stack
    //    UniformAsk[SCTypeOfApplicationType, ?],
//    Fx.fx4[
//      UniformAsk[Organisation, ?],
//      UniformAsk[Individual, ?],
//      UniformAsk[Premises, ?],
//      UniformAsk[Provision, ?]
//      ]
//    ]

  def program2[R: _uniform[ProvisionTypeType, ?]: _uniform[SectorType, ?]: _uniform[OwnershipType, ?]: _uniform[Boolean, ?]: _uniform[Int, ?]: _uniform[Option[String], ?]: _uniform[LocalDate, ?]]: Eff[R, SC1FormInformation] = {
    for {
//      typeOfApplication <- uask[R, SCTypeOfApplicationType]("typeOfApplication")
      typeOfApplication <- SCTypeOfApplicationType.Uniform.program[R]
//      organization <- uask[R, Organisation]("organization")
//      nominated <- uask[R, Individual]("nominated")
//      premises <- uask[R, Premises]("premises")
//      provision <- uask[R, Provision]("provision")
//      manager <- uask[R, Individual]("manager")
    } yield SC1FormInformation(
      typeOfApplication,
      ???,
      ???,
      ???,
      ???,
      ???
//      organization,
//      nominated,
//      premises,
//      provision,
//      manager
    )
  }

}
