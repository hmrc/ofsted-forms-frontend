import uk.gov.hmrc.DefaultBuildSettings.integrationTestSettings
import uk.gov.hmrc.SbtArtifactory
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin.publishingSettings

val appName = "ofsted-forms-frontend"

lazy val microservice = Project(appName, file("."))
  .enablePlugins(play.sbt.PlayScala, SbtAutoBuildPlugin, SbtGitVersioning, SbtDistributablesPlugin, SbtArtifactory, GformToUniformPlugin)
  .settings(
    majorVersion                     := 0,
    libraryDependencies              ++= AppDependencies.compile ++ AppDependencies.test
  )
  .settings(publishingSettings: _*)
  .configs(IntegrationTest)
  .settings(integrationTestSettings(): _*)
  .settings(resolvers += Resolver.jcenterRepo)
  .settings(scalacOptions += "-Ypartial-unification")
  .settings(
    coverageExcludedPackages := """uk\.gov\.hmrc\.BuildInfo;.*\.Routes;.*\.RoutesPrefix;.*\.Reverse[^.]*""",
    coverageMinimum := 70.00,
    coverageFailOnMinimum := true,
    coverageHighlighting := true,
    parallelExecution in Test := false,
    addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full),
    sourceGenerators in Compile += (GformToUniformPlugin.gformsToUniform in Compile),
    GformToUniformPlugin.gformsControllerPackage in Compile := Some("gforms.controllers"),
    GformToUniformPlugin.gformsKnownDirectSubclassesBodge in Compile := Some(baseDirectory.value / "app" / "uk" / "gov" / "hmrc" / "ofstedformsfrontend" / "controllers"),
    target in GformToUniformPlugin.gformsToUniform := baseDirectory.value / "target" / "scala-2.11" / "src_managed" / "main"
//    managedSourceDirectories in Compile += (target in GformToUniformPlugin.gformsToUniform).value
//    sourceDirectories in Compile += baseDirectory.value / "target" / "scala-2.11" / "gforms"
  )

addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.3")
