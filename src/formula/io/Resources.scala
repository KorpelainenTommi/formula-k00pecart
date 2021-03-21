package formula.io

trait Resource extends Enumeration {
  def path(r: Value): String
}

object Textures extends Resource {
  type Texture = Value
  val Goal = Value
  val Road = Value
  val Button = Value

  val Background_Mainmenu = Value
  val Background_Generic = Value

  val GAME_TEXTURES = Vector(Goal, Road)

  def path(t: Texture) = {
    t match {
      case Button => "Button0.png"
      case Goal => "Goal0.png"
      case Road => "Road0.png"
      case Background_Mainmenu => "background0.png"
      case Background_Generic  => "screen0.png"
    }
  }
}

object Fonts extends Resource {
  type Font = Value
  val Impact = Value
  val TimesNewRoman = Value

  def path(f: Font) = {
    f match {
      case Impact => "impact.ttf"
      case TimesNewRoman => "timesnewroman.ttf"
    }
  }
}