import play.core.PlayVersion.current
import play.sbt.PlayImport._
import sbt.Keys.libraryDependencies
import sbt._

object AppDependencies {

  object Versions {
    val uniform: String = "0.3.0"
    val enumeratum: String = "1.5.13"
  }

  val compile = Seq(
    "uk.gov.hmrc"             %% "govuk-template"           % "5.26.0-play-26",
    "uk.gov.hmrc"             %% "play-ui"                  % "7.27.0-play-26",
    "uk.gov.hmrc"             %% "bootstrap-play-26"        % "0.36.0",
    "com.beachape"            %% "enumeratum"               % Versions.enumeratum,
    "com.beachape"            %% "enumeratum-play"          % Versions.enumeratum,
    "com.luketebbs.uniform"   %% "core"                     % Versions.uniform,
    "com.luketebbs.uniform"   %% "interpreter-play26"       % Versions.uniform
  )

  val test = Seq(
    "org.scalatest"           %% "scalatest"                % "3.0.4"                 % "test",
    "org.jsoup"               %  "jsoup"                    % "1.10.2"                % "test",
    "com.typesafe.play"       %% "play-test"                % current                 % "test",
    "org.pegdown"             %  "pegdown"                  % "1.6.0"                 % "test, it",
    "uk.gov.hmrc"             %% "service-integration-test" % "0.4.0-play-26"         % "test, it",
    "org.scalatestplus.play"  %% "scalatestplus-play"       % "3.1.0"                 % "test, it"
  )

}
