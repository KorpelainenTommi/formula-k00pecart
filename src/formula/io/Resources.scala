package formula.io

trait Resource extends Enumeration {
  def path(r: Value): String
}

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

  val CarBlueBack = Value
  val CarBlueFront = Value
  val CarBlueLeft = Value
  val CarBlueRight = Value
  val CarBlueLeftFront = Value
  val CarBlueRightFront = Value
  val CarBlueTurnLeft = Value
  val CarBlueTurnRight = Value

  val Background_Mainmenu = Value
  val Background_Generic = Value

  val TRACK_TEXTURES = Vector(Goal, Road, Grass, Sky)
  val CAR_RED_TEXTURES = Vector(CarRedBack, CarRedTurnLeft, CarRedTurnRight, CarRedLeft, CarRedRight, CarRedLeftFront, CarRedRightFront, CarRedFront)
  val CAR_BLUE_TEXTURES = Vector(CarBlueBack, CarBlueTurnLeft, CarBlueTurnRight, CarBlueLeft, CarBlueRight, CarBlueLeftFront, CarBlueRightFront, CarBlueFront)

  val GAME_TEXTURES = TRACK_TEXTURES ++ CAR_RED_TEXTURES ++ CAR_BLUE_TEXTURES

  def path(t: Texture) = {
    t match {
      case Button => "button0.png"
      case Background_Mainmenu => "background0.png"
      case Background_Generic  => "screen0.png"

      case Goal => "goal0.png"
      case Road => "road0.png"
      case Grass => "grass0.png"
      case Sky => "sky0.png"

      case CarRedBack => "car_red_back.png"
      case CarRedTurnLeft => "car_red_turnl.png"
      case CarRedTurnRight => "car_red_turnr.png"
      case CarRedLeft => "car_red_left.png"
      case CarRedRight => "car_red_right.png"
      case CarRedLeftFront => "car_red_left_front.png"
      case CarRedRightFront => "car_red_right_front.png"
      case CarRedFront => "car_red_front.png"

      case CarBlueBack => "car_red_back.png"
      case CarBlueTurnLeft => "car_red_turnl.png"
      case CarBlueTurnRight => "car_red_turnr.png"
      case CarBlueLeft => "car_red_left.png"
      case CarBlueRight => "car_red_right.png"
      case CarBlueLeftFront => "car_red_left_front.png"
      case CarBlueRightFront => "car_red_right_front.png"
      case CarBlueFront => "car_red_front.png"
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