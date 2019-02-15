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

package uk.gov.hmrc.ofstedformsfrontend.examples

import org.atnos.eff._
import ltbs.uniform._
import cats.implicits._

object GreasySpoon {

  type Money = Int

  type GreasyStack = Fx2[UniformAsk[Int,?], UniformAsk[Boolean,?]]

  def greasySpoon[S : _uniform[Int,?] : _uniform[Boolean,?]] : Eff[S,Money] = for {
    age           <- uask[S,Int]("age")
    food          <- uask[S,Boolean]("wantFood")
    tea           <- uask[S,Boolean]("wantTea")
    baconCost     <- uask[S,Int]("bacon").map(_ * 12) emptyUnless food
    eggsCost      <- uask[S,Int]("eggs").map(_ * 24) emptyUnless food
    foodCost      = baconCost + eggsCost
    teaCost       <- uask[S,Int]("sugar").map(_ * 10 + 50) emptyUnless tea
    youngDiscount = if (age < 16) teaCost / 10 else 0
    oldDiscount   = if (age > 60) (teaCost + foodCost) * (Math.min(age - 60,25) / 100) else 0
  } yield (foodCost + teaCost + youngDiscount + oldDiscount)

}