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

package uk.gov.hmrc.ofstedformsfrontend.authentication

import com.google.inject.Inject
import javax.inject.Provider
import play.api.Configuration
import play.api.mvc.RequestHeader

class AuthenticationConfiguration(val loginUrl: String, continueBaseUrl: String) {
  def continueUrl(request: RequestHeader): String = {
    continueBaseUrl + request.uri
  }
}

class AuthenticationConfigurationProvider @Inject()(configuration: Configuration) extends Provider[AuthenticationConfiguration] {
  override val get: AuthenticationConfiguration = new AuthenticationConfiguration(
    loginUrl = configuration.get[String]("authentication.login-redirection-url"),
    continueBaseUrl = configuration.get[String]("authentication.continue-base-url")
  )
}