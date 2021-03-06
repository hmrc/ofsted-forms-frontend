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

package uk.gov.hmrc.ofstedformsfrontend.upscan

import akka.http.javadsl.model.MediaTypes
import akka.stream.Materializer
import akka.stream.scaladsl.Source
import akka.util.ByteString
import javax.inject.{Inject, Named}
import org.joda.time.DateTime
import org.slf4j.{Logger, LoggerFactory}
import play.api.libs.json.{JsError, Json}
import play.api.libs.ws.WSClient
import play.api.mvc.{Call, MultipartFormData}
import play.mvc.Http.{Response, Status}
import uk.gov.hmrc.http.{HeaderCarrier, Upstream4xxResponse}
import uk.gov.hmrc.play.audit.http.HeaderFieldsExtractor
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import uk.gov.hmrc.play.audit.model.{DataCall, MergedDataEvent}
import uk.gov.hmrc.play.bootstrap.http.HttpClient

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.{Failure, Success}

case class UploadRequest(href: String, fields: Map[String, String])

object UploadRequest {

  import play.api.libs.functional.syntax._
  import play.api.libs.json._

  implicit val reads: Reads[UploadRequest] = (
    (__ \ "href").read[String] and
      (__ \ "fields").read[Map[String, String]]
    ).apply(apply _)
}


case class UploadDescriptor(reference: String, uploadRequest: UploadRequest)

object UploadDescriptor {

  import play.api.libs.functional.syntax._
  import play.api.libs.json._

  implicit val reads: Reads[UploadDescriptor] = (
    (__ \ "reference").read[String] and
      (__ \ "uploadRequest").read[UploadRequest]
    ).apply(apply _)
}

class UpscanClient @Inject()(httpClient: HttpClient,
                             wsClient: WSClient,
                             auditConnector: AuditConnector,
                             @Named("appName") serviceName: String,
                             @Named("upscan-initiate-base-url") upscanBaseUrl: String,
                             @Named("self-base-url") selfBaseUrl: String)
                            (implicit executionContext: ExecutionContext, materializer: Materializer) {

  private val logger = LoggerFactory.getLogger(this.getClass)

  private val initiateUrl = upscanBaseUrl + "/upscan/initiate"

  def initiate(callbackUrl: String, minimumFileSize: Long, maximumFileSize: Long)
              (implicit hc: HeaderCarrier): Future[UploadDescriptor] = {
    val payload = Json.obj(
      "callbackUrl" -> callbackUrl,
      "minimumFileSize" -> minimumFileSize,
      "maximumFileSize" -> maximumFileSize
    )
    httpClient.POST(initiateUrl, payload).flatMap(response =>
      response.json.validate[UploadDescriptor]
        .map(Future.successful)
        .recoverTotal(error => Future.failed(UpscanException.byJsError(error)))
    )
  }

  def initiate(callback: Call, minimumFileSize: Long = 0, maximumFileSize: Long = 100000000)
              (implicit hc: HeaderCarrier): Future[UploadDescriptor] = {
    initiate(selfBaseUrl + callback.url, minimumFileSize, maximumFileSize)
  }

  def upload(href: String, form: MultipartFormData[Source[ByteString, _]])(implicit headerCarrier: HeaderCarrier): Future[Unit] = {

    val parts: Source[MultipartFormData.Part[Source[ByteString, _]], _] = Source.apply(form.dataParts.flatMap {
      case (key, values) => values.map(value => MultipartFormData.DataPart(key, value): MultipartFormData.Part[Source[ByteString, _]])
    } ++ form.files)
    val requestData = DataCall(
      tags = Map.empty,
      detail = Map(
        "method" -> "POST",
        "path" -> href
      ),
      generatedAt = DateTime.now()
    )
    wsClient.url(href).post(parts).andThen {
      case Failure(exception) =>
        val responseData = DataCall(
          tags = Map.empty,
          detail = Map(
            "message" -> exception.getMessage
          ),
          generatedAt = DateTime.now()
        )
        logger.warn("Error on uploading file to upscan", exception)
        auditConnector.sendMergedEvent(MergedDataEvent(serviceName, "OutboundCall", request = requestData, response = responseData))
      case Success(response) =>
        val responseData = DataCall(
          tags = Map.empty,
          detail = Map(
            "statusCode" -> String.valueOf(response.status),
            "responseMessage" -> response.body[String]
          ),
          generatedAt = DateTime.now()
        )
        auditConnector.sendMergedEvent(MergedDataEvent(serviceName, "OutboundCall", request = requestData, response = responseData))
        logger.info("Recieve successful repsonse form {} with status {}", href, response.status)
    }.map { response =>
      if (response.status != Status.NO_CONTENT) {
        throw new UpscanException("S3 did not return 204 status code - " + headerCarrier.requestId)
      }
      Unit
    }
  }


}

class UpscanException(message: String) extends Exception(message)

object UpscanException {
  def byJsError(errors: JsError) =
    new UpscanException("Unable to parse response - %s".format(JsError.toJson(errors).toString()))
}