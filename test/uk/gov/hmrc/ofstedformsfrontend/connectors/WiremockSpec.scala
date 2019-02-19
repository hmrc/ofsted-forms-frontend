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

package uk.gov.hmrc.ofstedformsfrontend.connectors

import com.github.tomakehurst.wiremock._
import com.github.tomakehurst.wiremock.client.{MappingBuilder, ResponseDefinitionBuilder, WireMock}
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.matching.{RequestPatternBuilder, StringValuePattern, UrlPattern}
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import org.scalatest.{BeforeAndAfterAll, Suite}


trait WiremockSpec extends BeforeAndAfterAll { self: Suite =>

  import WireMockConfiguration._

  protected val wiremockServer = new WireMockServer(options().dynamicPort())

  override protected def beforeAll(): Unit = {
    super.beforeAll()
    wiremockServer.start()
  }

  override protected def afterAll(): Unit = {
    wiremockServer.stop()
    super.afterAll()
  }

  def any(pattern: UrlPattern): MappingBuilder = WireMock.any(pattern)

  def urlEqualTo(url: String): UrlPattern = WireMock.urlEqualTo(url)

  def ok: ResponseDefinitionBuilder = WireMock.ok()

  def ok(body: String): ResponseDefinitionBuilder = WireMock.ok(body)

  def url(url: String): String = wiremockServer.url(url)

  def addStubMapping(mapping: StubMapping): Unit = wiremockServer.addStubMapping(mapping)

  def postRequestedFor(matcher: UrlPattern): RequestPatternBuilder = WireMock.postRequestedFor(matcher)

  def equalToJson(json: String): StringValuePattern = WireMock.equalToJson(json)
}
