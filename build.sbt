name := "formula-k00pecart"

version := "3.5"

scalaVersion := "2.13.2"

lazy val root = (project in file("."))

.settings(
name := "formula-k00pecart",
Compile / scalaSource := baseDirectory.value / "src"
)
