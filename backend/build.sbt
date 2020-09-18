ThisBuild / scalaVersion := "2.12.12"
ThisBuild / organization := "com.github.meandor"
ThisBuild / scapegoatVersion := "1.3.8"

val AkkaVersion     = "2.6.8"
val AkkaHttpVersion = "10.2.0"

lazy val doctorFate = (project in file("."))
  .settings(
    name := "DoctorFate",
    version := "0.1.0-SNAPSHOT",
    libraryDependencies += "com.typesafe.akka"          %% "akka-stream"    % AkkaVersion,
    libraryDependencies += "com.typesafe.akka"          %% "akka-http"      % AkkaHttpVersion,
    libraryDependencies += "ch.qos.logback"             % "logback-classic" % "1.2.3",
    libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging"  % "3.9.2",
    libraryDependencies += "org.scalatest"              %% "scalatest"      % "3.2.0" % Test
  )
