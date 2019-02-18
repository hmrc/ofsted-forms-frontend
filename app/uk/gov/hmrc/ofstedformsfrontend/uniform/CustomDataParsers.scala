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

import java.time.LocalDate

import cats.implicits._
import cats.Invariant
import java.time.LocalDate

import enumeratum._
import ltbs.uniform.Tree
import ltbs.uniform._
import enumeratum.values.{IntEnum, IntEnumEntry}
import ltbs.uniform.{ErrorTree, Tree}
import ltbs.uniform.web.{DataParser, Input}
import ltbs.uniform.web.parser.{intParser, stringParser}
import org.joda.time.DateTime

object CustomDataParsers {
  import ltbs.uniform.web.parser._

//  implicit def localDateParser: DataParser[DateTime] = new DataParser[DateTime] {
//
//    def bind(in: Input): Either[ErrorTree,LocalDate] = {
//      def numField(key: String) =
//        (in.get(key) >>= intParser.bind).leftMap{ x =>
//          Tree("", Map(key -> x))
//        }.toValidated
//
//      (
//        numField("year"),
//        numField("month"),
//        numField("day")
//      ).tupled.toEither.flatMap{ case (y,m,d) =>
//        Either.catchOnly[java.time.DateTimeException]{
//          LocalDate.of(y,m,d)
//        }.leftMap(_ => Tree("badDate"))
//      }
//    }
//
//    def unbind(a: LocalDate): Input = Tree(
//      Nil,
//      Map(
//        "year" -> Tree(List(a.getYear.toString)),
//        "month" -> Tree(List(a.getMonthValue.toString)),
//        "day" -> Tree(List(a.getDayOfMonth.toString)))
//    )
//  }

  implicit def enumeratumIntParser[A <: IntEnumEntry](implicit enum: IntEnum[A]): DataParser[A] =
    new DataParser[A] {
      def bind(in: Input): Either[ErrorTree,A] = stringParser.bind(in) >>= { x =>
        Either.catchOnly[NoSuchElementException](enum.withValue(x.toInt)).leftMap{_ => Tree("badValue")}
      }
      def unbind(a: A): Input = Tree(List(a.toString))
    }

  implicit def enumeratumIntSetParser[A <: IntEnumEntry](implicit enum: IntEnum[A]): DataParser[Set[A]] =
    new DataParser[Set[A]] {
      def bind(in: Input): Either[ErrorTree,Set[A]] = {
        in.value.map{ x =>
          Either.catchOnly[NoSuchElementException](enum.withValue(x.toInt)).leftMap{_ => Tree("badValue"): ErrorTree}
        }.sequence.map{_.toSet}
      }
      def unbind(a: Set[A]): Input = Tree(a.map(_.toString).toList)
    }

}
