import play.core.PlayVersion.current
import play.sbt.PlayImport._
import sbt.Keys.libraryDependencies
import sbt._

object AppDependencies {

  object Versions {
    val uniform: String = "3e8ab265d61dc98cc4d6930b3bd6e5f688ae4a7e-SNAPSHOT"
    val enumeratum: String = "1.5.13"
  }

  val compile = Seq(
    "uk.gov.hmrc"             %% "govuk-template"           % "5.30.0-play-26",
    "uk.gov.hmrc"             %% "play-ui"                  % "7.33.0-play-26",
    "uk.gov.hmrc"             %% "bootstrap-play-26"        % "0.37.0",
    "com.beachape"            %% "enumeratum"               % Versions.enumeratum,
    "com.beachape"            %% "enumeratum-play"          % Versions.enumeratum,
    "com.luketebbs.uniform"   %% "core"                     % Versions.uniform,
    "com.luketebbs.uniform"   %% "interpreter-play26"       % Versions.uniform,
    "com.luketebbs.uniform"   %% "govuk-widgets"            % Versions.uniform
  )

  val test = Seq(
    "org.scalatest"           %% "scalatest"                % "3.0.4"                 % "test",
    "org.jsoup"               %  "jsoup"                    % "1.10.2"                % "test",
    "org.mockito"             %% "mockito-scala"            % "1.1.4"                 % "test",
    "com.typesafe.play"       %% "play-test"                % current                 % "test",
    "org.pegdown"             %  "pegdown"                  % "1.6.0"                 % "test, it",
    "uk.gov.hmrc"             %% "service-integration-test" % "0.6.0-play-26"         % "test, it",
    "org.scalatestplus.play"  %% "scalatestplus-play"       % "3.1.0"                 % "test, it",
    "com.github.tomakehurst"  %  "wiremock-jre8"            % "2.21.0"                % "test"
  )

}
