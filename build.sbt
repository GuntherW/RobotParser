name := """RobotParser"""

version := "1.0"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.5"

resolvers ++= Seq(
  "anormcypher" at "http://repo.anormcypher.org/",
  "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/"
)

libraryDependencies ++= Seq(
 	 "org.scalatest" %% "scalatest" % "2.2.1" % "test",
 	 "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.2",
 	  "org.anormcypher" %% "anormcypher" % "0.6.0",
 	   "org.apache.commons" % "commons-lang3" % "3.3.2"
)