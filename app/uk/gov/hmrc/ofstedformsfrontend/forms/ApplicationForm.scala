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

package uk.gov.hmrc.ofstedformsfrontend.forms

import enumeratum._
import org.joda.time.DateTime
import org.w3c.dom.{Document, DocumentFragment, Node}
import uk.gov.hmrc.ofstedformsfrontend.communication.FormType
import uk.gov.hmrc.ofstedformsfrontend.marshallers.xml.{EnumMarshaller, XmlMarshaller}

import scala.collection.immutable

trait Form {
  def toDocument(document: Document): DocumentFragment
}


case class ApplicationForm(id: Int,
                           form: Form,
                           createdBy: String,
                           createdDate: DateTime,
                           source: ApplicationSource,
                           parentId: Int,
                           comunicationId: String,
                           urn: String,
                           documents: Documents,
                           metadata: FormMetadata)

object ApplicationForm {
  implicit val marshaller: XmlMarshaller[ApplicationForm] = new XmlMarshaller[ApplicationForm] {
    override def marshall(obj: ApplicationForm)(implicit document: Document): Node = {
      createFragment(document) {
        _.createElement("FormType") { element =>
          element.appendChild(obj.form.toDocument(document))
        }.createValue("FormId", obj.id)
          .createValue("CreatedBy", obj.createdBy)
          .createValue("CreatedDate", obj.createdDate)
          .createValue("Source", obj.source)
          .createValue("ParentID", obj.parentId)
          .createValue("CommunicationID", obj.comunicationId)
          .createValue("URN", obj.urn)
          .createValue("Documents", obj.documents)
          .createValue("FormMetaData", obj.metadata)
      }
    }
  }
}

sealed abstract class ApplicationSource(val value: Int) extends EnumEntry

object ApplicationSource extends Enum[ApplicationSource] {

  val values: immutable.IndexedSeq[ApplicationSource] = findValues

  case object Postal extends ApplicationSource(1)

  case object Internal extends ApplicationSource(2)

  case object Online extends ApplicationSource(3)

  case object Telephone extends ApplicationSource(4)

  implicit val marshaller: XmlMarshaller[ApplicationSource] = new EnumMarshaller[ApplicationSource]
}

case class FormMetadata()

object FormMetadata {
  implicit val marshaller: XmlMarshaller[FormMetadata] = new XmlMarshaller[FormMetadata] {
    override def marshall(obj: FormMetadata)(implicit document: Document): Node = {
      document.createDocumentFragment()
    }
  }
}