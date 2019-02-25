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

import play.api.http.{DefaultFileMimeTypes, FileMimeTypesConfiguration}
import play.api.mvc.{DefaultActionBuilder, DefaultMessagesActionBuilderImpl, DefaultMessagesControllerComponents, MessagesControllerComponents}
import play.api.test.Helpers.{stubBodyParser, stubLangs, stubMessagesApi, stubPlayBodyParsers}
import play.api.test.NoMaterializer

import scala.concurrent.ExecutionContext

trait PlayControllerStubs {

  def stubMessagesControllerComponents: MessagesControllerComponents = {
    val messagesApi = stubMessagesApi()
    val executionContext = ExecutionContext.global
    DefaultMessagesControllerComponents(
      messagesActionBuilder = new DefaultMessagesActionBuilderImpl(stubBodyParser(),  messagesApi)(executionContext),
      actionBuilder = DefaultActionBuilder(stubBodyParser())(ExecutionContext.global),
      parsers = stubPlayBodyParsers(NoMaterializer),
      messagesApi = messagesApi,
      langs = stubLangs(),
      fileMimeTypes = new DefaultFileMimeTypes(FileMimeTypesConfiguration(Map.empty)),
      executionContext = executionContext
    )
  }
}

object PlayControllerStubs extends PlayControllerStubs


