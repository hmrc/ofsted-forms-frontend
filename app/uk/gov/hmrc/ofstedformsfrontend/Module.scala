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

package uk.gov.hmrc.ofstedformsfrontend

import akka.actor.ActorSystem
import com.google.inject.{AbstractModule, Provides}
import javax.inject.{Named, Singleton}
import play.api.libs.ws.{DefaultWSProxyServer, WSClient, WSProxyServer}
import play.api.{ConfigLoader, Configuration}
import uk.gov.hmrc.ofstedformsfrontend.authentication.{AuthenticationConfiguration, AuthenticationConfigurationProvider}
import uk.gov.hmrc.play.audit.http.HttpAuditing
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig
import uk.gov.hmrc.play.bootstrap.http.{DefaultHttpClient, HttpClient}
import uk.gov.hmrc.play.http.ws.WSProxy

class Module extends AbstractModule {

  @Provides
  @Named("admins-ips")
  @Singleton
  def adminsFromConfig(configuration: Configuration): Set[String] = {
    configuration.get[Set[String]]("ofsted-forms.admins-ips")(ConfigLoader.seqStringLoader.map(_.toSet))
  }

  @Provides
  @Named("ofsted-forms-notifications-base-url")
  @Singleton
  def ofstedFormsNotificationBaseUrl(servicesConfig: ServicesConfig): String = {
    servicesConfig.baseUrl("ofsted-forms-notifications")
  }

  @Provides
  @Named("self-base-url")
  @Singleton
  def ofstedFormsFrontendBaseUrl(servicesConfig: ServicesConfig): String = {
    servicesConfig.baseUrl("ofsted-forms-frontend")
  }

  @Provides
  @Named("ofsted-forms-proxy-base-url")
  @Singleton
  def ofstedFormsProxyBaseUrl(servicesConfig: ServicesConfig): String = {
    servicesConfig.baseUrl("ofsted-forms-proxy")
  }

  @Provides
  @Named("upscan-initiate-base-url")
  @Singleton
  def upscanInitiateBaseUrl(servicesConfig: ServicesConfig): String = {
    servicesConfig.baseUrl("upscan-initiate")
  }

  @Provides
  @Named("ofsted-db-base-url")
  @Singleton
  def ofstedDbBaseUrl(servicesConfig: ServicesConfig): String = {
    servicesConfig.baseUrl("ofsted-db")
  }

  @Provides
  @Singleton
  def proxyConfiguration(configuration: Configuration): Option[WSProxyServer] = {
    if(configuration.get[Boolean]("proxy.proxyRequiredForThisEnvironment")){
      val host = configuration.get[String]("proxy.host")
      val port = configuration.get[Int]("proxy.port")(ConfigLoader.stringLoader.map(_.toInt))
      val username = configuration.get[Option[String]]("proxy.username")
      val password = configuration.get[Option[String]]("proxy.password")
      val protocol = configuration.get[Option[String]]("proxy.protocol")
      Some(DefaultWSProxyServer(host, port, protocol, username, password))
    } else {
      None
    }
  }

  @Provides
  @Named("proxy")
  @Singleton
  def proxyHttpClient(proxyConfig: Option[WSProxyServer],
                      config: Configuration,
                      audit: HttpAuditing,
                      wsClient: WSClient,
                      system: ActorSystem): HttpClient = {
    new DefaultHttpClient(config, audit, wsClient, system) with WSProxy {
      override def wsProxyServer: Option[WSProxyServer] = proxyConfig
    }
  }

  override def configure(): Unit = {
    bind(classOf[AuthenticationConfiguration]).toProvider(classOf[AuthenticationConfigurationProvider])
  }
}
