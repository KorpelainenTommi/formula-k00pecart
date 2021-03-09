package formula.io
object Textures extends Enumeration {
  type Texture = Value
  val Goal = Value
  val Road = Value
  val Button = Value

  def path(t: Texture) = {
    t match {
      case Button => "Button0.png"
      case Goal => "Goal0.png"
      case Road => "Road0.png"
    }
  }
}

object Fonts extends Enumeration {
  type Font = Value
  val Impact = Value

  def path(f: Font) = {
    f match {
      case Impact => "impact.ttf"
    }
  }
}