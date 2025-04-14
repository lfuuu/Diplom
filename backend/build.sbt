import Dependencies._

ThisBuild / scalaVersion := "2.13.15"
ThisBuild / version := "2.0.0"
ThisBuild / organization := "com.mcn"
ThisBuild / organizationName := "MCN Telecom"

ThisBuild / evictionErrorLevel := Level.Warn
ThisBuild / scalafixDependencies += Libraries.organizeImports

ThisBuild / exportJars := true

//resolvers += Resolver.sonatypeRepo("snapshots")

//val scalafixCommonSettings = inConfig(IntegrationTest)(scalafixConfigSettings(IntegrationTest))

val commonSettings = List(
  scalafmtOnCompile := true, // recommended in Scala 3 = false
  testFrameworks += new TestFramework("weaver.framework.CatsEffect"),
  scalacOptions ++= List(
    "-Ymacro-annotations",
    "-Yrangepos",
    "-Wconf:cat=unused-imports:info,cat=unused-params:s,cat=unused-locals:s,cat=unused-privates:s"
  ), // Было unused:info s -silent
  libraryDependencies ++= List(
    CompilerPlugin.kindProjector,
    CompilerPlugin.betterMonadicFor,
    CompilerPlugin.semanticDB,
    Libraries.cirisCore,
    Libraries.cats,
    Libraries.catsEffect,
    Libraries.catsRetry,
    Libraries.circeCore,
    Libraries.circeGeneric,
    Libraries.circeParser,
    Libraries.circeRefined,
    Libraries.http4sServer,
    Libraries.http4sJwtAuth,
    Libraries.http4sClient,
    Libraries.http4sCirce,
    Libraries.derevoCore,
    Libraries.derevoCats,
    Libraries.derevoCirce,
    Libraries.pureconfigCore,
    Libraries.pureconfigCatsEffects,
    Libraries.pureconfigIp4s,
    Libraries.fs2,
    Libraries.fs2Codecs,
    Libraries.fs2Protocols,
    Libraries.fs2Kafka,
    Libraries.http4sDsl,
    Libraries.log4cats,
    Libraries.logback % Runtime,
    Libraries.monocleCore,
    Libraries.monocleMacro,
    Libraries.newtype,
    Libraries.refinedCore,
    Libraries.refinedCats,
    Libraries.tapirCore,
    Libraries.tapirHttp4s,
    Libraries.tapirSwagger,
    Libraries.tapirCirce,
    Libraries.tapirPrometheusMetrics,
    Libraries.tapirCats,
    Libraries.tapirRefined,
    Libraries.tapirNewType,
    Libraries.tapirDerevo,
    Libraries.tapirSttpStubServer % Test,
    Libraries.tapirSttpClient3 % Test,
    Libraries.skunkCore,
    Libraries.skunkCirce,
    Libraries.skunkRefined,
    Libraries.squants,
    Libraries.fs2Kafka,
    Libraries.bouncyCastle,
    Libraries.http4sJwtAuth,
    Libraries.refinedPureconfig,
    Libraries.catsLaws % Test,
    Libraries.log4catsNoOp,
    Libraries.scodecCore,
    Libraries.meowmtlCore,
    Libraries.scodecTestKit % Test,
    Libraries.monocleLaw        % Test,
    Libraries.refinedScalacheck % Test,
    Libraries.weaverCats        % Test,
    Libraries.weaverDiscipline  % Test,
    Libraries.weaverScalaCheck  % Test
  ),
  // https://stackoverflow.com/questions/54625572/sbt-assembly-errordeduplicate-different-file-contents-found-in-io-netty-versio
  assembly / assemblyMergeStrategy := {
    case PathList("META-INF", "io.netty.versions.properties") => MergeStrategy.first
    case _                                                    => MergeStrategy.first
  }
)

lazy val root = (project in file("."))
  .settings(commonSettings: _*)
  .settings(
    name := "diplom",
    Compile / mainClass := Some("com.mcn.diplom.Main"),
    makeBatScripts := Seq(),
    Compile / run / fork := true,
    assembly / assemblyJarName := "diplom.jar",
    assembly / mainClass := Some("com.mcn.diplom.Main"),
    // assembly / assemblyMergeStrategy := {

    //   // case PathList("reference.conf")                                         => MergeStrategy.concat
    //   // case "logback.xml" | "logback-prod.xml"                                 => MergeStrategy.discard
    //   // case "application.conf"                                                 => MergeStrategy.discard
    //   // case "application-test.conf" | "logback-test.xml_" | "logback-test.xml" => MergeStrategy.discard
    //   // case _                                                                  => MergeStrategy.first
    // }
  )

addCommandAlias("lint", ";scalafixAll --rules OrganizeImports")
