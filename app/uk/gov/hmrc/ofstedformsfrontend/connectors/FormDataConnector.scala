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

import java.time.ZonedDateTime

import com.google.inject.ImplementedBy
import javax.inject.{Inject, Named}
import play.api.libs.json.{JsValue, Json, Reads, Writes}
import uk.gov.hmrc.http.{HeaderCarrier, HttpReads}
import uk.gov.hmrc.ofstedformsfrontend.forms.FormId
import uk.gov.hmrc.play.bootstrap.http.HttpClient

import scala.concurrent.{ExecutionContext, Future}

case class FormData(internalId: String,
                    id: FormId,
                    data: Map[String, String],
                    etag: String,
                    createAt: ZonedDateTime,
                    updatedAt: ZonedDateTime)

object FormData {

  import play.api.libs.json._
  import play.api.libs.functional.syntax._

  implicit val reads: Reads[FormData] = (
    (__ \ "_id").read[String] and
      (__ \ "formId").read[FormId] and
      (__ \ "data").read[Map[String, String]] and
      (__ \ "_etag").read[String] and
      (__ \ "_created").read[ZonedDateTime] and
      (__ \ "_updated").read[ZonedDateTime]
    ) (FormData.apply _)
}

@ImplementedBy(classOf[FormDataConnector])
trait FormDataProvider {
  def get(id: FormId)(implicit hc: HeaderCarrier): Future[FormData]

  def put(data: FormData, value: Map[String, String])(implicit hc: HeaderCarrier): Future[Unit]
}

class FormDataConnector @Inject()(@Named("proxy") httpClient: HttpClient,
                                  @Named("ofsted-db-base-url") baseUrl: String)
                                 (implicit ec: ExecutionContext) extends FormDataProvider {

  override def get(id: FormId)(implicit hc: HeaderCarrier): Future[FormData] = {
    httpClient.GET[FormData](s"${baseUrl}/forms/${id.asString}")
  }

  override def put(data: FormData, value: Map[String, String])(implicit hc: HeaderCarrier): Future[Unit] = {
    val payload = Json.obj(
      "formId" -> data.id,
      "data" -> value
    )
    val url = s"${baseUrl}/forms/${data.internalId}"
    val updatedHeader = hc.copy(otherHeaders = hc.otherHeaders :+ ("If-Match" -> data.etag))
    httpClient.PUT[JsValue, JsValue](url,payload)(implicitly[Writes[JsValue]], implicitly[HttpReads[JsValue]], hc, ec)
      .map(_ => ())
  }
}
