name := """RobotParser"""

version := "1.0"

scalaVersion := "2.11.5"

resolvers ++= Seq(
  "anormcypher" at "http://repo.anormcypher.org/"
)


libraryDependencies ++= Seq(
 	 "org.scalatest" %% "scalatest" % "2.2.4" % "test",
 	 "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.2",
 	 "org.anormcypher" %% "anormcypher" % "0.6.0",
 	 "org.apache.commons" % "commons-lang3" % "3.3.2",
 	 "com.github.scopt" %% "scopt" % "3.3.0"
)

mainClass in assembly := Some("de.codecentric.wittig.Main")

assemblyJarName in assembly := s"${name.value}-${version.value}.jar"

enablePlugins(JavaAppPackaging)

packageDescription in Debian := "Parst Robotdateien und persistiert sie in eine Neo4J Datenbank."

maintainer in Debian := "Gunther Wittig"

mainClass in Compile := Some("de.codecentric.wittig.Main")
