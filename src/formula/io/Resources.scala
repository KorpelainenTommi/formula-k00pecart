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


  val Sky    = Value
  val Goal   = Value
  val Road   = Value
  val Grass  = Value
  val Button = Value


  val Background_Generic  = Value
  val Background_Mainmenu = Value


  //This is kind of verbose, could be refactored
  val CarRedBack          = Value
  val CarRedFront         = Value
  val CarRedLeft          = Value
  val CarRedRight         = Value
  val CarRedLeftFront     = Value
  val CarRedRightFront    = Value
  val CarRedTurnLeft      = Value
  val CarRedTurnRight     = Value

  val CarOrangeBack       = Value
  val CarOrangeFront      = Value
  val CarOrangeLeft       = Value
  val CarOrangeRight      = Value
  val CarOrangeLeftFront  = Value
  val CarOrangeRightFront = Value
  val CarOrangeTurnLeft   = Value
  val CarOrangeTurnRight  = Value


  val GearR = Value
  val Gear0 = Value
  val Gear1 = Value
  val Gear2 = Value
  val Gear3 = Value
  val Gear4 = Value
  val Gear5 = Value

  //Animated
  val ANIM_Oil   = Value
  val ANIM_Smoke = Value
  val ANIM_Speed = Value
  val ANIM_Explosion = Value


  val OBJ_Oil   = Value
  val OBJ_Tree  = Value
  val OBJ_Rock  = Value
  val OBJ_Tuft0 = Value
  val OBJ_Tuft1 = Value
  val OBJ_Tuft2 = Value

  //Texture lists for convinience

  val OBJ_TEXTURES   = Vector(OBJ_Oil, OBJ_Tree, OBJ_Rock, OBJ_Tuft0, OBJ_Tuft1, OBJ_Tuft2)
  val HUD_TEXTURES   = Vector(GearR, Gear0, Gear1, Gear2, Gear3, Gear4, Gear5)
  val ANIM_TEXTURES  = Vector(ANIM_Explosion, ANIM_Smoke, ANIM_Speed, ANIM_Oil)
  val TRACK_TEXTURES = Vector(Goal, Road, Grass, Sky)

  val CAR_RED_TEXTURES = Vector(
    CarRedBack, CarRedTurnLeft, CarRedTurnRight,
    CarRedLeft, CarRedRight, CarRedLeftFront, CarRedRightFront, CarRedFront)

  val CAR_ORANGE_TEXTURES = Vector(
    CarOrangeBack, CarOrangeTurnLeft, CarOrangeTurnRight,
    CarOrangeLeft, CarOrangeRight, CarOrangeLeftFront, CarOrangeRightFront, CarOrangeFront)


  val GAME_TEXTURES = TRACK_TEXTURES ++ CAR_RED_TEXTURES ++ CAR_ORANGE_TEXTURES ++ HUD_TEXTURES ++ OBJ_TEXTURES ++ ANIM_TEXTURES


  private def fold(parts: String*) = FormulaIO.resolveNameS(parts)

  def path(t: Texture) = {

    t match {
      case Sky    => fold("tracks", "sky0.png")
      case Goal   => fold("tracks", "goal0.png")
      case Road   => fold("tracks", "road0.png")
      case Grass  => fold("tracks", "grass0.png")
      case Button => "button0.png"

      case Background_Generic  => "screen0.png"
      case Background_Mainmenu => "background0.png"


      case CarRedBack          => fold("cars", "car_red_back.png")
      case CarRedFront         => fold("cars", "car_red_front.png")
      case CarRedLeft          => fold("cars", "car_red_left.png")
      case CarRedRight         => fold("cars", "car_red_right.png")
      case CarRedLeftFront     => fold("cars", "car_red_left_front.png")
      case CarRedRightFront    => fold("cars", "car_red_right_front.png")
      case CarRedTurnLeft      => fold("cars", "car_red_turnl.png")
      case CarRedTurnRight     => fold("cars", "car_red_turnr.png")

      case CarOrangeBack       => fold("cars", "car_orange_back.png")
      case CarOrangeFront      => fold("cars", "car_orange_front.png")
      case CarOrangeLeft       => fold("cars", "car_orange_left.png")
      case CarOrangeRight      => fold("cars", "car_orange_right.png")
      case CarOrangeLeftFront  => fold("cars", "car_orange_left_front.png")
      case CarOrangeRightFront => fold("cars", "car_orange_right_front.png")
      case CarOrangeTurnLeft   => fold("cars", "car_orange_turnl.png")
      case CarOrangeTurnRight  => fold("cars", "car_orange_turnr.png")


      case GearR => fold("hud", "gearR.png")
      case Gear0 => fold("hud", "gear0.png")
      case Gear1 => fold("hud", "gear1.png")
      case Gear2 => fold("hud", "gear2.png")
      case Gear3 => fold("hud", "gear3.png")
      case Gear4 => fold("hud", "gear4.png")
      case Gear5 => fold("hud", "gear5.png")

      case ANIM_Explosion => fold("animated", "explosion0.png")
      case ANIM_Smoke => fold("animated", "smoke0.png")
      case ANIM_Speed => fold("animated", "speed0.png")
      case ANIM_Oil   => fold("animated", "smoke1.png")

      case OBJ_Oil    => fold("objects", "oil0.png")
      case OBJ_Tree   => fold("objects", "tree0.png")
      case OBJ_Rock   => fold("objects", "rock0.png")
      case OBJ_Tuft0  => fold("objects", "tuft0.png")
      case OBJ_Tuft1  => fold("objects", "tuft1.png")
      case OBJ_Tuft2  => fold("objects", "tuft2.png")
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