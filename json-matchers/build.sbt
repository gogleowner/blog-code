
name := "json-matchers"

version := "0.1"

scalaVersion := "2.12.8"

libraryDependencies ++= Seq(
  "org.json4s" % "json4s-jackson_2.12" % "3.6.3",
  "org.scalactic" %% "scalactic" % "3.0.5",
  "org.scalatest" %% "scalatest" % "3.0.5" % "test"
)