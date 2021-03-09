package formula.io
object Textures extends Enumeration {
  type Texture = Value
  val Goal = Value
  val Road = Value

  def path(t: Texture) = {
    t match {
      case Goal => "Goal0.png"
      case Road => "Road0.png"
    }
  }
}