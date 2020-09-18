ThisBuild / scalaVersion := "2.13.3"
ThisBuild / organization := "com.github.meandor"

lazy val hello = (project in file("."))
  .settings(
    name := "DoctorFate",
    version := "0.1.0-SNAPSHOT",
    libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.0" % Test
  )
