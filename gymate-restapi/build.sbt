name := """gymate-restapi"""
organization := "pl.tzch96"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.2"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test

libraryDependencies ++= Seq(evolutions,
  jdbc,
  "org.postgresql" % "postgresql" % "42.2.12",
  "org.playframework.anorm" %% "anorm" % "2.6.4")
libraryDependencies += "com.typesafe.play" %% "play-json" % "2.8.1"
libraryDependencies += "com.github.t3hnar" %% "scala-bcrypt" % "4.1"
// Adds additional packages into Twirl
//TwirlKeys.templateImports += "pl.tzch96.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "pl.tzch96.binders._"
