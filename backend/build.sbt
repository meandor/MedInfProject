ThisBuild / scalaVersion := "2.12.12"
ThisBuild / organization := "com.github.meandor"
ThisBuild / scapegoatVersion := "1.3.8"

val akkaVersion     = "2.6.9"
val akkaHttpVersion = "10.2.0"
val circeVersion    = "0.13.0"

lazy val app = (project in file("."))
  .settings(
    name := "DoctorFate",
    version := "0.1.0-SNAPSHOT",
    mainClass in assembly := Some("com.github.meandor.doctorfate.DoctorFate"),
    test in assembly := {},
    libraryDependencies += "com.typesafe.akka"          %% "akka-actor-typed"    % akkaVersion,
    libraryDependencies += "com.typesafe.akka"          %% "akka-stream"         % akkaVersion,
    libraryDependencies += "com.typesafe.akka"          %% "akka-http"           % akkaHttpVersion,
    libraryDependencies += "ch.qos.logback"             % "logback-classic"      % "1.2.3",
    libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging"       % "3.9.2",
    libraryDependencies += "io.circe"                   %% "circe-core"          % circeVersion,
    libraryDependencies += "io.circe"                   %% "circe-generic"       % circeVersion,
    libraryDependencies += "io.circe"                   %% "circe-parser"        % circeVersion,
    libraryDependencies += "de.heikoseeberger"          %% "akka-http-circe"     % "1.34.0",
    libraryDependencies += "com.auth0"                  % "java-jwt"             % "3.3.0",
    libraryDependencies += "org.scalatest"              %% "scalatest"           % "3.2.0" % Test,
    libraryDependencies += "org.mockito"                %% "mockito-scala"       % "1.15.0" % Test,
    libraryDependencies += "com.typesafe.akka"          %% "akka-stream-testkit" % akkaVersion % Test,
    libraryDependencies += "com.typesafe.akka"          %% "akka-http-testkit"   % akkaHttpVersion % Test
  )
