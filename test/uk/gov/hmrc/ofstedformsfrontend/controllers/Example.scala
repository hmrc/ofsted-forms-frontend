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

package uk.gov.hmrc.ofstedformsfrontend.controllers

import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import org.joda.time.DateTime
import org.scalatest.{FlatSpec, Ignore, Matchers}
import uk.gov.hmrc.ofstedformsfrontend.forms.sc1.{Address, Addresses}

@Ignore
class Example extends FlatSpec with Matchers {

  val address1 = Address(None, "Example 1", "Example 2", None, "Worthing", "UK", "BN11-1NX", "Description", "Code", None, Some(DateTime.now()), Some(DateTime.now()))
  val address2 = Address(None, "Example 1", "Example 2", None, "Worthing", "UK", "BN11-1NX", "Description", "Code", None, Some(DateTime.now()), Some(DateTime.now()))
  val addresses = Addresses(Seq(address1, address2))
  val builderFactory = DocumentBuilderFactory.newInstance()
  val builder = builderFactory.newDocumentBuilder()
  val document = builder.newDocument()
  val fragemtn = Addresses.marshaller.marshall(addresses)(document)
  val ele = document.createElement("Addresses")
  ele.appendChild(fragemtn)
  document.appendChild(ele)
  val transformerFactory = TransformerFactory.newInstance()
  val transformer = transformerFactory.newTransformer()
  val source = new DOMSource(document)
  val target = new StreamResult(System.out)
  transformer.transform(source, target)
}
