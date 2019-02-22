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

package uk.gov.hmrc.ofstedformsfrontend.views

import org.scalatest.enablers.Emptiness
import org.scalatest.{FlatSpec, Matchers}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.i18n.Messages
import play.api.mvc.Request
import play.api.test.{CSRFTokenHelper, FakeRequest, Helpers}
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.ofstedformsfrontend.Example
import uk.gov.hmrc.ofstedformsfrontend.forms.{FormId, FormKind, Occurrence, SubmittedForm}
import uk.gov.hmrc.ofstedformsfrontend.views.html.admin.{FormView, PendingFormList}
import uk.gov.hmrc.ofstedformsfrontend.views.html.user_form_list

class TemplateTests extends FlatSpec with GuiceOneAppPerSuite with Matchers {

  implicit val request: Request[_] = CSRFTokenHelper.addCSRFToken(FakeRequest())

  implicit val messages: Messages = Helpers.stubMessages()

  val submitted = SubmittedForm(FormId(), FormKind.SC1, Occurrence(Example.user), Occurrence(Example.user))

  implicit val fullResult: Emptiness[play.twirl.api.HtmlFormat.Appendable] = new Emptiness[HtmlFormat.Appendable] {
    override def isEmpty(thing: HtmlFormat.Appendable): Boolean = {
      thing.body.isEmpty
    }
  }

  "Admin Form" should "render correctly" in {
    val template = app.injector.instanceOf[FormView]
    template(submitted) shouldNot be (empty)
  }

  "Admin Submitted Forms List" should "render correctly" in {
    val template = app.injector.instanceOf[PendingFormList]
    template(List.empty) shouldNot be (empty)
  }

  "User Form view" should "redner correclty" in {
    val template = app.injector.instanceOf[html.FormView]
    template(submitted) shouldNot be (empty)
  }

  "User Form List" should "render correctly" in {
    val template = app.injector.instanceOf[user_form_list]
    template(List.empty) shouldNot be (empty)
  }

  "Error Template" should "render correctly" in {
    val template = app.injector.instanceOf[html.error_template]
    template("Title", "Heading", "Message") shouldNot be (empty)
  }
}
