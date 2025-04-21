import sbt._

object Dependencies {

  object V {
    val cats             = "2.7.0"
    val catsEffect       = "3.5.7"
    val catsRetry        = "3.1.3"
    val circe            = "0.14.7"
    val ciris            = "3.6.0"
    val derevo           = "0.13.0"
    val javaxCrypto      = "1.0.1"
    val fs2              = "3.11.0"
    val fs2Kafka         = "3.0.1"
    val fs2Cron          = "0.9.0"
    val http4s           = "0.23.27"
    val http4sJwtAuth    = "1.2.3"
    val log4cats         = "2.7.0"
    val monocle          = "3.1.0"
    val newtype          = "0.4.4"
    val refined          = "0.9.29"
    val redis4cats       = "1.1.1"
    val skunk            = "0.6.4"
    val squants          = "1.8.3"
    val pureconfig       = "0.17.7"
    val ip4s             = "3.6.0"
    val betterMonadicFor = "0.3.1"
    val kindProjector    = "0.13.3"
    val logback          = "1.2.11"
    val organizeImports  = "0.6.0"
    val semanticDB       = "4.11.2"
    val scodecs          = "1.11.9"
    val tapir            = "1.11.25"
    val tapirSttpClient3 = "3.9.7"
    val kafkaVersion     = "3.4.0"

    val bouncyCastle = "1.2.2.1-jre17"

    val weaver = "0.8.3"

    val meowmtl = "0.5.0"

    val fs2ftp = "0.8.4"
    
    val MunitVersion     = "0.7.19"

  }

  object Libraries {
    def circe(artifact: String): ModuleID      = "io.circe"                    %% s"circe-$artifact"      % V.circe
    def ciris(artifact: String): ModuleID      = "is.cir"                      %% artifact                % V.ciris
    def derevo(artifact: String): ModuleID     = "tf.tofu"                     %% s"derevo-$artifact"     % V.derevo
    def http4s(artifact: String): ModuleID     = "org.http4s"                  %% s"http4s-$artifact"     % V.http4s
    def meowmtl(artifact: String): ModuleID    = "com.olegpy"                  %% s"meow-mtl-$artifact"   % V.meowmtl
    def pureconfig(artifact: String): ModuleID = "com.github.pureconfig"       %% s"pureconfig-$artifact" % V.pureconfig
    def tapir(artifact: String): ModuleID      = "com.softwaremill.sttp.tapir" %% s"tapir-$artifact"      % V.tapir

    val cats         = "org.typelevel"    %% "cats-core"     % V.cats
    val catsEffect   = "org.typelevel"    %% "cats-effect"   % V.catsEffect
    val catsRetry    = "com.github.cb372" %% "cats-retry"    % V.catsRetry
    val squants      = "org.typelevel"    %% "squants"       % V.squants
    val fs2          = "co.fs2"           %% "fs2-core"      % V.fs2
    val fs2Codecs    = "co.fs2"           %% "fs2-scodec"    % V.fs2
    val fs2Protocols = "co.fs2"           %% "fs2-protocols" % V.fs2
    val munit      = "org.scalameta" %% "munit" % V.MunitVersion 

    val fs2Kafka           = "com.github.fd4s"        %% "fs2-kafka"       % V.fs2Kafka
    val fs2Cron            = "eu.timepit"             %% "fs2-cron-cron4s" % V.fs2Cron
    val fs2ftp             = "com.github.regis-leray" %% "fs2-ftp"         % V.fs2ftp
    val circeCore          = circe("core")
    val circeGeneric       = circe("generic")
    val circeParser        = circe("parser")
    val circeLiteral       = circe("literal")
    val circeRefined       = circe("refined")
    val circeGenericExtras = circe("generic-extras")

    val tapirCore    = tapir("core")
    val tapirHttp4s  = tapir("http4s-server")
    val tapirSwagger = tapir("swagger-ui-bundle")
    val tapirCirce   = tapir("json-circe")
    val tapirCats    = tapir("cats")
    val tapirRefined = tapir("refined")
    val tapirNewType = tapir("newtype")
    val tapirDerevo  = tapir("derevo")

    val tapirSttpStubServer    = tapir("sttp-stub-server")
    val tapirPrometheusMetrics = tapir("prometheus-metrics")

    val tapirSttpClient3 = "com.softwaremill.sttp.client3" %% "circe" % V.tapirSttpClient3

    val cirisCore    = ciris("ciris")
    val cirisEnum    = ciris("ciris-enumeratum")
    val cirisRefined = ciris("ciris-refined")

    val derevoCore  = derevo("core")
    val derevoCats  = derevo("cats")
    val derevoCirce = derevo("circe-magnolia")

    val http4sDsl    = http4s("dsl")
    val http4sServer = http4s("ember-server")
    val http4sClient = http4s("ember-client")
    val http4sCirce  = http4s("circe")

    val meowmtlCore    = meowmtl("core")
    val meowmtlEffects = meowmtl("effects")
    val meowmtlMonix   = meowmtl("monix")

    val pureconfigCore        = "com.github.pureconfig" %% "pureconfig" % V.pureconfig
    val pureconfigCatsEffects = pureconfig("cats-effect")
    val pureconfigIp4s        = pureconfig("ip4s")

    val http4sJwtAuth = "dev.profunktor" %% "http4s-jwt-auth" % V.http4sJwtAuth

    val monocleCore  = "dev.optics" %% "monocle-core"  % V.monocle
    val monocleMacro = "dev.optics" %% "monocle-macro" % V.monocle

    val refinedCore       = "eu.timepit" %% "refined"            % V.refined
    val refinedCats       = "eu.timepit" %% "refined-cats"       % V.refined
    val refinedPureconfig = "eu.timepit" %% "refined-pureconfig" % V.refined

    val kafkaStreams = "org.apache.kafka" % "kafka-streams" % V.kafkaVersion
    val kafkaStreamsTestUtils = "org.apache.kafka" % "kafka-streams-test-utils" % V.kafkaVersion
    
    val log4cats = "org.typelevel" %% "log4cats-slf4j" % V.log4cats
    val newtype  = "io.estatico"   %% "newtype"        % V.newtype

    val scodecCore    = "org.scodec" %% "scodec-core"    % V.scodecs
    val scodecTestKit = "org.scodec" %% "scodec-testkit" % V.scodecs

    val javaxCrypto = "javax.xml.crypto" % "jsr105-api" % V.javaxCrypto

    val redis4catsEffects  = "dev.profunktor" %% "redis4cats-effects"  % V.redis4cats
    val redis4catsLog4cats = "dev.profunktor" %% "redis4cats-log4cats" % V.redis4cats

    val skunkCore    = "org.tpolecat" %% "skunk-core"    % V.skunk
    val skunkCirce   = "org.tpolecat" %% "skunk-circe"   % V.skunk
    val skunkRefined = "org.tpolecat" %% "skunk-refined" % V.skunk

    val logback = "ch.qos.logback" % "logback-classic" % V.logback

    val bouncyCastle = "com.guicedee.services" % "bouncycastle" % V.bouncyCastle

    // Test
    val catsLaws          = "org.typelevel"       %% "cats-laws"          % V.cats
    val log4catsNoOp      = "org.typelevel"       %% "log4cats-noop"      % V.log4cats
    val monocleLaw        = "dev.optics"          %% "monocle-law"        % V.monocle
    val refinedScalacheck = "eu.timepit"          %% "refined-scalacheck" % V.refined
    val weaverCats        = "com.disneystreaming" %% "weaver-cats"        % V.weaver
    val weaverDiscipline  = "com.disneystreaming" %% "weaver-discipline"  % V.weaver
    val weaverScalaCheck  = "com.disneystreaming" %% "weaver-scalacheck"  % V.weaver

    // Scalafix rules
    val organizeImports = "com.github.liancheng" %% "organize-imports" % V.organizeImports
  }

  object CompilerPlugin {

    val betterMonadicFor = compilerPlugin(
      "com.olegpy" %% "better-monadic-for" % V.betterMonadicFor
    )

    val kindProjector = compilerPlugin(
      ("org.typelevel" % "kind-projector" % V.kindProjector).cross(CrossVersion.full)
    )

    val semanticDB = compilerPlugin(
      ("org.scalameta" % "semanticdb-scalac" % V.semanticDB).cross(CrossVersion.full)
    )
  }

}
