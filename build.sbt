name := """gymate-restapi"""
organization := "pl.tzch96"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala, SwaggerPlugin)
swaggerDomainNameSpaces := Seq("models")

scalaVersion := "2.13.2"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test

libraryDependencies ++= Seq(evolutions,
  jdbc,
  "org.postgresql" % "postgresql" % "42.2.12")
libraryDependencies += "com.typesafe.play" %% "play-json" % "2.8.1"
libraryDependencies += "com.github.t3hnar" %% "scala-bcrypt" % "4.1"
libraryDependencies += "org.webjars" % "swagger-ui" % "3.9.3"
libraryDependencies += filters
libraryDependencies += "com.pauldijou" %% "jwt-play-json" % "4.2.0"