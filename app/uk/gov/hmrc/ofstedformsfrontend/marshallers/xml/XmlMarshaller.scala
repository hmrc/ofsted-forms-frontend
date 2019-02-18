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

package uk.gov.hmrc.ofstedformsfrontend.marshallers.xml

import java.time.LocalDate
import java.time.format.{DateTimeFormatter, FormatStyle}
import java.util.Formatter

import enumeratum.EnumEntry
import enumeratum.values.IntEnumEntry
import org.joda.time.{DateTime, DateTimeZone}
import org.joda.time.format.ISODateTimeFormat
import org.w3c.dom.{Document, DocumentFragment, Element, Node}

class ElementBuilder(element: Element, document: Document) {

  def createElement(name: String)(f: Element => Unit): ElementBuilder = {
    val child = document.createElement(name)
    element.appendChild(child)
    f(element)
    this
  }

  def createValue[T](name: String, t: T)(implicit marshaller: XmlMarshaller[T]): ElementBuilder = {
    val child = document.createElement(name)
    element.appendChild(child)
    child.appendChild(marshaller.marshall(t)(document))
    this
  }

  def createValue[T](name: String, t: Option[T])(implicit marshaller: XmlMarshaller[T]): ElementBuilder = {
    t.foreach { value =>
      val child = document.createElement(name)
      element.appendChild(child)
      child.appendChild(marshaller.marshall(value)(document))
    }
    this
  }

  def createValue[T](t: T)(implicit marshaller: XmlMarshaller[T]): ElementBuilder = {
    element.appendChild(marshaller.marshall(t)(document))
    this
  }

  def appendChild(node: Node): ElementBuilder = {
    element.appendChild(node)
    this
  }
}

class FragmentBuilder(fragment: DocumentFragment, document: Document) {
  def createElement(name: String)(f: ElementBuilder => Unit): FragmentBuilder = {
    val element = document.createElement(name)
    fragment.appendChild(element)
    f(new ElementBuilder(element, document))
    this
  }

  def createValue[T](name: String, t: T)(implicit marshaller: XmlMarshaller[T]): FragmentBuilder = {
    val child = document.createElement(name)
    fragment.appendChild(child)
    child.appendChild(marshaller.marshall(t)(document))
    this
  }

  def createValue[T](name: String, t: Option[T])(implicit marshaller: XmlMarshaller[T]): FragmentBuilder= {
    t.foreach { value =>
      val child = document.createElement(name)
      fragment.appendChild(child)
      child.appendChild(marshaller.marshall(value)(document))
    }
    this
  }

  def createValue[T](t: T)(implicit marshaller: XmlMarshaller[T]): FragmentBuilder = {
    fragment.appendChild(marshaller.marshall(t)(document))
    this
  }
}


trait XmlMarshaller[T] {
  def createFragment(document: Document)(f: FragmentBuilder => Unit): DocumentFragment = {
    val fragment = document.createDocumentFragment()
    f(new FragmentBuilder(fragment, document))
    fragment
  }

  def marshall(obj: T)(implicit document: Document): Node
}

object XmlMarshaller {
  implicit val dateTimeMarshaller: XmlMarshaller[DateTime] = new XmlMarshaller[DateTime] {

    private val format = ISODateTimeFormat.dateHourMinuteSecond()

    override def marshall(obj: DateTime)(implicit document: Document): Node = {
      document.createTextNode(obj.toString(format))
    }
  }

  implicit val localDateMarshaller: XmlMarshaller[LocalDate] = new XmlMarshaller[LocalDate] {

    private val format = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)

    override def marshall(obj: LocalDate)(implicit document: Document): Node = {
      document.createTextNode(format.format(obj))
    }
  }

  implicit val stringMarshaller: XmlMarshaller[String] = new XmlMarshaller[String] {
    override def marshall(obj: String)(implicit document: Document): Node = {
      document.createTextNode(obj)
    }
  }

  implicit val intMarshaller: XmlMarshaller[Int] = new XmlMarshaller[Int] {
    override def marshall(obj: Int)(implicit document: Document): Node = {
      document.createTextNode(obj.toString)
    }
  }

  val yesNoMarshaller: XmlMarshaller[Boolean] = new XmlMarshaller[Boolean] {
    override def marshall(obj: Boolean)(implicit document: Document): Node = {
      val value = if(obj) {
        "Yes"
      } else {
        "No"
      }
      document.createTextNode(value)
    }
  }

  val booleanMarshaller: XmlMarshaller[Boolean] = new XmlMarshaller[Boolean] {
    override def marshall(obj: Boolean)(implicit document: Document): Node = {
      val value = if(obj) {
        "true"
      } else {
        "false"
      }
      document.createTextNode(value)
    }
  }

  implicit def optionalMarshaller[T](implicit xmlMarshaller: XmlMarshaller[T]): XmlMarshaller[Option[T]] = {
    new XmlMarshaller[Option[T]] {
      override def marshall(obj: Option[T])(implicit document: Document): DocumentFragment = {
        val fragment = document.createDocumentFragment()
        obj.foreach { value =>
          fragment.appendChild(xmlMarshaller.marshall(value))
        }
        fragment
      }
    }
  }

  def emptyOnNone[T](implicit xmlMarshaller: XmlMarshaller[T]): XmlMarshaller[Option[T]] = {
    new XmlMarshaller[Option[T]] {
      override def marshall(obj: Option[T])(implicit document: Document): DocumentFragment = {
        val fragment = document.createDocumentFragment()
        obj.foreach { value =>
          fragment.appendChild(xmlMarshaller.marshall(value))
        }
        fragment
      }
    }
  }
}

class EnumMarshaller[T <: EnumEntry] extends XmlMarshaller[T] {
  override def marshall(obj: T)(implicit document: Document): Node = {
    XmlMarshaller.stringMarshaller.marshall(obj.entryName)
  }
}

class IntEnumMarshaller[T <: IntEnumEntry] extends XmlMarshaller[T] {
  override def marshall(obj: T)(implicit document: Document): Node = {
    XmlMarshaller.stringMarshaller.marshall(obj.value.toString)
  }
}