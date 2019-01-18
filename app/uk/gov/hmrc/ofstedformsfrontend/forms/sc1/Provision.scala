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

import org.w3c.dom.{Document, Node}
import uk.gov.hmrc.ofstedformsfrontend.marshallers.xml.XmlMarshaller

case class Provision(subType: Int,
                     fte: Int,
                     childrensHome: ChildrensHome,
                     residentialFamilyCentre: ResidentialFamilyCenter,
                     independentFosteringAgency: IndependentFosteringAgency,
                     independentAdoptionAgency: IndependentAdoptionAgency,
                     adoptionsSupportAgency: AdoptionsSupportAgency,
                     ageFrom: Int,
                     ageTo: Int)

object Provision {
  implicit val marshaller: XmlMarshaller[Provision] = new XmlMarshaller[Provision] {
    override def marshall(obj: Provision)(implicit document: Document): Node = {
      createFragment(document){
        _.createValue("ProvisionSubTypeId", obj.subType) //FIXME corret value here
          .createValue("NumberFTE", obj.fte)
          .createValue("ChildrensHome", obj.childrensHome)
          .createValue("ResidentialFamilyCentre", obj.residentialFamilyCentre)
          .createValue("IndependentFosteringAgency", obj.independentFosteringAgency)
          .createValue("IndependentAdoptionAgency", obj.independentAdoptionAgency)
          .createValue("AdoptionsSupportAgency", obj.adoptionsSupportAgency)
          .createValue("AgeFrom", obj.ageFrom)
          .createValue("AgeTo", obj.ageTo)
          .createValue("ChargeRange", "")
      }
    }
  }
}


case class ChildrensHome(maxUsers: Int,
                         maxMale: Int,
                         maxFemale: Int,
                         EBDChildren: Int,
                         PDChildren: Int,
                         LDChildren: Int,
                         MDChildren: Int,
                         PDPChildren: Int,
                         PAPChildren: Int,
                         SIChildren: Int,
                         OtherChildren: Int,
                         AdultCare: Boolean,
                         AdultCareDesc: String)

object ChildrensHome {
  implicit val marshaller: XmlMarshaller[ChildrensHome] = new XmlMarshaller[ChildrensHome] {
    override def marshall(obj: ChildrensHome)(implicit document: Document): Node = {
      createFragment(document) {
        _.createValue("MaxUsers", obj.maxUsers)
          .createValue("EBDChildren", obj.EBDChildren)
          .createValue("PDChildren", obj.PDChildren)
          .createValue("LDChildren", obj.LDChildren)
          .createValue("MDChildren", obj.MDChildren)
          .createValue("PDPChildren", obj.PDPChildren)
          .createValue("PAPChildren", obj.PAPChildren)
          .createValue("SIChildren", obj.SIChildren)
          .createValue("AdultCare", obj.AdultCare)(XmlMarshaller.yesNoMarshaller)
          .createValue("AdultCareDesc", obj.AdultCareDesc)
      }
    }
  }
}

case class ResidentialFamilyCenter(familyCount: Int,
                                   emergencyAdmission: Boolean,
                                   directCourtReferral: Boolean,
                                   refugeService: Boolean,
                                   adultsWithoutChildren: Boolean,
                                   childrenWithCarers: Boolean,
                                   serviceOtherThanRFC: Boolean)

object ResidentialFamilyCenter {
  implicit val marshaller: XmlMarshaller[ResidentialFamilyCenter] = new XmlMarshaller[ResidentialFamilyCenter] {
    override def marshall(obj: ResidentialFamilyCenter)(implicit document: Document): Node = {
      createFragment(document){
        _.createValue("FamilyCount", obj.familyCount)
          .createValue("EmergencyAdmission", obj.emergencyAdmission)(XmlMarshaller.yesNoMarshaller)
          .createValue("DirectCourtReferral", obj.directCourtReferral)(XmlMarshaller.yesNoMarshaller)
          .createValue("RefugeService", obj.refugeService)(XmlMarshaller.yesNoMarshaller)
          .createValue("AdultsWithoutChildren", obj.adultsWithoutChildren)(XmlMarshaller.yesNoMarshaller)
          .createValue("ChildrenWithCarers", obj.childrenWithCarers)(XmlMarshaller.yesNoMarshaller)
          .createValue("ServiceOtherThanRFC", obj.serviceOtherThanRFC)(XmlMarshaller.yesNoMarshaller)
      }
    }
  }
}

// FIXME fields here
case class IndependentFosteringAgency()

object IndependentFosteringAgency {
  implicit val marshaller: XmlMarshaller[IndependentFosteringAgency] = new XmlMarshaller[IndependentFosteringAgency] {
    override def marshall(obj: IndependentFosteringAgency)(implicit document: Document): Node = {
      createFragment(document){
        _.createValue("PlacementConditions", 0)
      }
    }
  }
}

// FIXME fields here
case class IndependentAdoptionAgency()

object IndependentAdoptionAgency {
  implicit val marshaller: XmlMarshaller[IndependentAdoptionAgency] = new XmlMarshaller[IndependentAdoptionAgency] {
    override def marshall(obj: IndependentAdoptionAgency)(implicit document: Document): Node = {
      createFragment(document){
        _.createValue("AdoptionConditions", 0)
      }
    }
  }
}

// FIXME fields here
case class AdoptionsSupportAgency()

object AdoptionsSupportAgency {
  implicit val marshaller: XmlMarshaller[AdoptionsSupportAgency] = new XmlMarshaller[AdoptionsSupportAgency] {
    override def marshall(obj: AdoptionsSupportAgency)(implicit document: Document): Node = {
      createFragment(document){
        _.createValue("AdoptionCounselling", false)(XmlMarshaller.yesNoMarshaller)
          .createValue("AdoptionAdvice", false)(XmlMarshaller.yesNoMarshaller)
          .createValue("MediationService", false)(XmlMarshaller.yesNoMarshaller)
          .createValue("TherapeuticService", false)(XmlMarshaller.yesNoMarshaller)
          .createValue("RelationshipSupport", false)(XmlMarshaller.yesNoMarshaller)
          .createValue("DisruptionSupport", false)(XmlMarshaller.yesNoMarshaller)
          .createValue("IntermediaryService", false)(XmlMarshaller.yesNoMarshaller)
          .createValue("AgencySupport", false)(XmlMarshaller.yesNoMarshaller)
      }
    }
  }
}