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

import com.google.inject.ImplementedBy
import javax.inject.{Inject, Named, Singleton}
import play.api.libs.json.Json
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.http.HttpClient

import scala.concurrent.{ExecutionContext, Future}

case class OfstedUrn(urn: String)

object OfstedUrn {
  implicit val ofsteduUnFormat = Json.format[OfstedUrn]
}

@ImplementedBy(classOf[HttpCygnumConnector])
trait CygnumConnector {
  def getUrn()(implicit hc : HeaderCarrier) : Future[OfstedUrn]
}

@Singleton
class HttpCygnumConnector @Inject()(httpClient: HttpClient,
                                           @Named("ofsted-forms-proxy-base-url") baseUrl: String)
                                          (implicit ec: ExecutionContext) extends CygnumConnector {

  private val getUrnUrl = baseUrl + "/ofsted-forms-proxy/geturn"

  def getUrn()(implicit hc: HeaderCarrier): Future[OfstedUrn] = {
    geturncall(getUrnUrl)
  }

  private def geturncall(url : String)(implicit hc: HeaderCarrier): Future[OfstedUrn] = {
    httpClient.GET[OfstedUrn](url)
  }

}

