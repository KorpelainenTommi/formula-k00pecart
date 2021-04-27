name := "formula-k00pecart"

version := "0.1"

scalaVersion := "2.13.2"

lazy val root = (project in file("."))

.settings(
name := "formula-k00pecart",
Compile / scalaSource := baseDirectory.value / "src"
)

initialize := {
  val _ = initialize.value // run the previous initialization
  val required = "1.8"
  val current  = sys.props("java.specification.version")
  assert(current == required, s"Unsupported JDK: java.specification.version $current != $required")
}