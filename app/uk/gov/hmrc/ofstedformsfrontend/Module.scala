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

import com.google.inject.{AbstractModule, Provides}
import javax.inject.{Named, Singleton}
import play.api.Configuration
import scala.collection.JavaConverters._

class Module extends AbstractModule {

  @Provides
  @Named("admins")
  @Singleton
  def adminsFromConfig(configuration: Configuration): Set[String] = {
    configuration.getStringList("ofsted-forms.admins")
      .map(_.asScala.toSet)
      .getOrElse(throw new RuntimeException("there is no configuration in 'ofsted-forms.admins'"))
  }

  override def configure(): Unit = {
    // Nothing to bind
  }
}
