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

import org.w3c.dom.{Document, DocumentFragment}
import uk.gov.hmrc.ofstedformsfrontend.marshallers.xml.XmlMarshaller

case class SC1Form(typeOfApplicationType: SCTypeOfApplicationType,
                   organisation: Organisation,
                   nominated: Individual,
                   premises: Premises,
                   provision: Provision,
                   manager: Individual)

object SC1Form {
  implicit val marshaller = new XmlMarshaller[SC1Form] {
    override def marshall(obj: SC1Form)(implicit document: Document): DocumentFragment = {
      createFragment(document){ fragment =>
        fragment.createValue("TypeOfApplication", obj.typeOfApplicationType)
          .createValue("Organisation", obj.organisation)
          .createValue("NominatedPerson", obj.nominated)
          .createValue("Premises", obj.premises)
          .createValue("Provision", obj.provision)
          .createValue("Manager", obj.manager)
      }
    }
  }
}