name := "formula-k00pecart"

version := "0.1"

scalaVersion := "2.13.2"

lazy val root = (project in file("."))

.settings(
name := "formula-k00pecart",
Compile / scalaSource := baseDirectory.value / "src",
javacOptions ++= Seq("-source", "11.0.10", "-target", "11.0.10")
)