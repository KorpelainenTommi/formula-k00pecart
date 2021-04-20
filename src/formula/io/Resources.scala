package formula.io

/** Generic resource to be loaded and cached.
 *  Textures, Fonts, Sound files etc.
 */
trait Resource extends Enumeration {
  def path(r: Value): String
}

/** Working with predefined named textures is easier than remembering path names.
 *  In addition, two same values of Texture will always refer to the same texture image,
 *  and that image will only be loaded once. If the file can't be loaded, the Texture will
 *  refer to a hardcoded missing texture.
 */
object Textures extends Resource {
  type Texture = Value
  val Goal = Value
  val Road = Value
  val Button = Value
  val Grass = Value
  val Sky = Value

  val CarRedBack = Value
  val CarRedFront = Value
  val CarRedLeft = Value
  val CarRedRight = Value
  val CarRedLeftFront = Value
  val CarRedRightFront = Value
  val CarRedTurnLeft = Value
  val CarRedTurnRight = Value

  val CarOrangeBack = Value
  val CarOrangeFront = Value
  val CarOrangeLeft = Value
  val CarOrangeRight = Value
  val CarOrangeLeftFront = Value
  val CarOrangeRightFront = Value
  val CarOrangeTurnLeft = Value
  val CarOrangeTurnRight = Value

  val GearR = Value
  val Gear0 = Value
  val Gear1 = Value
  val Gear2 = Value
  val Gear3 = Value
  val Gear4 = Value
  val Gear5 = Value

  val Background_Mainmenu = Value
  val Background_Generic = Value

  val TRACK_TEXTURES = Vector(Goal, Road, Grass, Sky)
  val HUD_TEXTURES = Vector(GearR, Gear0, Gear1, Gear2, Gear3, Gear4, Gear5)

  val CAR_RED_TEXTURES = Vector(
    CarRedBack, CarRedTurnLeft, CarRedTurnRight,
    CarRedLeft, CarRedRight, CarRedLeftFront, CarRedRightFront, CarRedFront)

  val CAR_ORANGE_TEXTURES = Vector(
    CarOrangeBack, CarOrangeTurnLeft, CarOrangeTurnRight,
    CarOrangeLeft, CarOrangeRight, CarOrangeLeftFront, CarOrangeRightFront, CarOrangeFront)

  val GAME_TEXTURES = TRACK_TEXTURES ++ CAR_RED_TEXTURES ++ CAR_ORANGE_TEXTURES ++ HUD_TEXTURES

  private def fold(parts: String*) = FormulaIO.resolveNameS(parts)
  def path(t: Texture) = {

    t match {
      case Button => "button0.png"
      case Background_Mainmenu => "background0.png"
      case Background_Generic  => "screen0.png"

      case Sky   => fold("tracks", "sky0.png")
      case Goal  => fold("tracks", "goal0.png")
      case Road  => fold("tracks", "road0.png")
      case Grass => fold("tracks", "grass0.png")

      case CarRedBack => fold("cars", "car_red_back.png")
      case CarRedTurnLeft => fold("cars", "car_red_turnl.png")
      case CarRedTurnRight => fold("cars", "car_red_turnr.png")
      case CarRedLeft => fold("cars", "car_red_left.png")
      case CarRedRight => fold("cars", "car_red_right.png")
      case CarRedLeftFront => fold("cars", "car_red_left_front.png")
      case CarRedRightFront => fold("cars", "car_red_right_front.png")
      case CarRedFront => fold("cars", "car_red_front.png")

      case CarOrangeBack => fold("cars", "car_orange_back.png")
      case CarOrangeTurnLeft => fold("cars", "car_orange_turnl.png")
      case CarOrangeTurnRight => fold("cars", "car_orange_turnr.png")
      case CarOrangeLeft => fold("cars", "car_orange_left.png")
      case CarOrangeRight => fold("cars", "car_orange_right.png")
      case CarOrangeLeftFront => fold("cars", "car_orange_left_front.png")
      case CarOrangeRightFront => fold("cars", "car_orange_right_front.png")
      case CarOrangeFront => fold("cars", "car_orange_front.png")

      case GearR => fold("hud", "gearR.png")
      case Gear0 => fold("hud", "gear0.png")
      case Gear1 => fold("hud", "gear1.png")
      case Gear2 => fold("hud", "gear2.png")
      case Gear3 => fold("hud", "gear3.png")
      case Gear4 => fold("hud", "gear4.png")
      case Gear5 => fold("hud", "gear5.png")
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