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

package uk.gov.hmrc.ofstedformsfrontend.uniform

import org.atnos.eff._
import ltbs.uniform._

case class Pizza(size: Int, toppings: List[String], base: Int)

class ExampleProgram {

  def intProgram[Stack : _uniform[Int, ?]]: Eff[Stack, Int] = for {
    value <- uask[Stack, Int]("favouriteNumber")
  } yield value

  def pizzaProgram[Stack: _uniform[Int, ?] : _uniform[List[String], ?]]: Eff[Stack, Pizza] = for {
    size <- uask[Stack, Int]("size")
    toppings <- uask[Stack, List[String]]("toppings")
    base <- uask[Stack, Int]("base")
  } yield Pizza(size, toppings, base)
}
