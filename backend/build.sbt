ThisBuild / scalaVersion := "2.12.12"
ThisBuild / organization := "com.github.meandor"
ThisBuild / scapegoatVersion := "1.3.11"

val akkaVersion     = "2.6.13"
val akkaHttpVersion = "10.2.4"
val circeVersion    = "0.13.0"

lazy val app = (project in file("."))
  .configs(IntegrationTest)
  .settings(
    name := "DoctorFate",
    version := "0.1.0-SNAPSHOT",
    Defaults.itSettings,
    parallelExecution in IntegrationTest := false,
    mainClass in assembly := Some("com.github.meandor.doctorfate.DoctorFate"),
    test in assembly := {},
    libraryDependencies += "com.typesafe"               % "config"               % "1.4.1",
    libraryDependencies += "com.typesafe.akka"          %% "akka-actor-typed"    % akkaVersion,
    libraryDependencies += "com.typesafe.akka"          %% "akka-stream"         % akkaVersion,
    libraryDependencies += "com.typesafe.akka"          %% "akka-http"           % akkaHttpVersion,
    libraryDependencies += "ch.megard"                  %% "akka-http-cors"      % "1.1.1",
    libraryDependencies += "ch.qos.logback"             % "logback-classic"      % "1.2.3",
    libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging"       % "3.9.2",
    libraryDependencies += "io.circe"                   %% "circe-core"          % circeVersion,
    libraryDependencies += "io.circe"                   %% "circe-generic"       % circeVersion,
    libraryDependencies += "io.circe"                   %% "circe-parser"        % circeVersion,
    libraryDependencies += "de.heikoseeberger"          %% "akka-http-circe"     % "1.35.3",
    libraryDependencies += "com.auth0"                  % "java-jwt"             % "3.14.0",
    libraryDependencies += "com.github.daddykotex"      %% "courier"             % "2.0.0",
    libraryDependencies += "org.flywaydb"               % "flyway-core"          % "7.6.0",
    libraryDependencies += "org.postgresql"             % "postgresql"           % "42.2.19",
    libraryDependencies += "org.scalikejdbc"            %% "scalikejdbc"         % "3.5.0",
    libraryDependencies += "org.scalatest"              %% "scalatest"           % "3.2.5" % "it,test",
    libraryDependencies += "org.mockito"                %% "mockito-scala"       % "1.16.29" % Test,
    libraryDependencies += "com.typesafe.akka"          %% "akka-stream-testkit" % akkaVersion % "it,test",
    libraryDependencies += "com.typesafe.akka"          %% "akka-http-testkit"   % akkaHttpVersion % "it,test"
  )
