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

package uk.gov.hmrc.ofstedformsfrontend.forms.envelop

import org.w3c.dom.{Document, Node}
import uk.gov.hmrc.ofstedformsfrontend.marshallers.xml.XmlMarshaller

case class Documents(attachments: Seq[Attachment])

object Documents {
  implicit val marshaller: XmlMarshaller[Documents] = new XmlMarshaller[Documents] {
    override def marshall(obj: Documents)(implicit document: Document): Node = {
      createFragment(document){ fragment =>
        obj.attachments.foreach { attachment =>
          fragment.createValue("Attachment", attachment)
        }
      }
    }
  }
}

case class Attachment(name: String, extension: String, data: String)

object Attachment {
  implicit val marshaller: XmlMarshaller[Attachment] = new XmlMarshaller[Attachment] {
    override def marshall(obj: Attachment)(implicit document: Document): Node = {
      createFragment(document){
        _.createValue("Name", obj.name)
          .createValue("Extension", obj.extension)
          .createValue("Data", obj.data)
      }
    }
  }
}
