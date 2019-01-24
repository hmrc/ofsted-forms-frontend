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
import uk.gov.hmrc.ofstedformsfrontend.forms.FormKind
import uk.gov.hmrc.ofstedformsfrontend.forms.envelop.FormInformation
import uk.gov.hmrc.ofstedformsfrontend.marshallers.xml.XmlMarshaller

case class SC1FormInformation(typeOfApplicationType: SCTypeOfApplicationType,
                              organisation: Organisation,
                              nominated: Individual,
                              premises: Premises,
                              provision: Provision,
                              manager: Individual) extends FormInformation {

  override def toDocument(implicit document: Document): DocumentFragment = {
    val fragment = document.createDocumentFragment()
    val root = document.createElement(FormKind.SC1.toString)
    fragment.appendChild(root)
    root.appendChild(SC1FormInformation.marshaller.marshall(this)(document))
    fragment
  }
}

object SC1FormInformation {
  implicit val marshaller: XmlMarshaller[SC1FormInformation] = new XmlMarshaller[SC1FormInformation] {
    override def marshall(obj: SC1FormInformation)(implicit document: Document): DocumentFragment = {
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