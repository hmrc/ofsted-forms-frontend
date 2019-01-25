import play.core.PlayVersion.current
import play.sbt.PlayImport._
import sbt.Keys.libraryDependencies
import sbt._

object AppDependencies {

  val compile = Seq(

    "uk.gov.hmrc"             %% "govuk-template"           % "5.26.0-play-26",
    "uk.gov.hmrc"             %% "play-ui"                  % "7.27.0-play-26",
    "uk.gov.hmrc"             %% "bootstrap-play-26"        % "0.36.0",
    "com.beachape"            %% "enumeratum"               % "1.5.13",
    "com.beachape"            %% "enumeratum-play"          % "1.5.13" // waits for OF-74 to be merged to not conflict
  )

  val test = Seq(
    "org.scalatest"           %% "scalatest"                % "3.0.4"                 % "test",
    "org.jsoup"               %  "jsoup"                    % "1.10.2"                % "test",
    "com.typesafe.play"       %% "play-test"                % current                 % "test",
    "org.pegdown"             %  "pegdown"                  % "1.6.0"                 % "test, it",
    "uk.gov.hmrc"             %% "service-integration-test" % "0.2.0"                 % "test, it",
    "org.scalatestplus.play"  %% "scalatestplus-play"       % "2.0.0"                 % "test, it"
  )

}
