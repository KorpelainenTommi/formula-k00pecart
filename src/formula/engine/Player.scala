package formula.engine
import formula.io._

object Player {

  val BASE_SPEED = 15D
  val TURN_RATE = 50D
  val MAX_GEAR = 5
  val GEAR_SHIFT_COOLDOWN = 0.5 //seconds
  val CAMERA_DISTANCE = 6D

}

class Player(val game: Game, initialPosition: V2D, initialDirection: V2D, val playerNumber: Int) extends Sprite {

  private val carTextures = if(playerNumber == 0) Textures.CAR_RED_TEXTURES else Textures.CAR_BLUE_TEXTURES

  protected var gear = 0
  protected var _position = initialPosition
  protected var _direction = initialDirection

  def turnLeft = game.input(playerNumber, 0)
  def turnRight = game.input(playerNumber, 1)
  def gearUp = game.input(playerNumber, 2)
  def gearDown = game.input(playerNumber, 3)

  def position = _position
  def direction = _direction
  def scale = 6D

  def texture = {
    if((turnLeft && turnRight) || (!turnLeft && !turnRight)) carTextures(0)
    else if(turnLeft) carTextures(1)
    else carTextures(2)
  }

  def dirTexture(lookingDirection: V2D, perpendicular: V2D) = {
    val dotP = direction dot lookingDirection
    val dotPerp = direction dot perpendicular

    if(dotP > 0.85) {
      texture
    }

    else if(dotP > 0.5) {
      if(dotPerp >= 0) carTextures(2)
      else carTextures(1)
    }

    else if(dotP > -0.5) {
      if(dotPerp >= 0) carTextures(4)
      else carTextures(3)
    }

    else if(dotP > -0.85) {
      if(dotPerp >= 0) carTextures(6)
      else carTextures(5)
    }

    else {
      carTextures(7)
    }
  }

  def spriteRatio = {
    val img = FormulaIO.getTexture(texture)
    1D * img.getHeight() / img.getWidth()
  }

  protected val _camera = new Camera
  _camera.position = initialPosition
  def camera = _camera

  protected var lastGearShift = game.startTime


  def update(time: Long, deltaT: Double) = {

    if((time - lastGearShift) / Game.TIME_PRECISION > Player.GEAR_SHIFT_COOLDOWN) {
      if(gearUp) {
        gear += 1
        if(gear > Player.MAX_GEAR) gear = Player.MAX_GEAR
        lastGearShift = time
      }
      else if(gearDown) {
        gear -= 1
        if(gear < -1) gear = -1
        lastGearShift = time
      }
    }

    if(turnLeft) {
      _direction = _direction.rotDeg(-Player.TURN_RATE * deltaT).normalized
    }

    if(turnRight) {
      _direction = _direction.rotDeg(Player.TURN_RATE * deltaT).normalized
    }

    val velocity = direction * Player.BASE_SPEED * deltaT * gear
    val newPosition = position + velocity
    _position = V2D(math.min(math.max(newPosition.x, 0), Track.TRACK_WIDTH), math.min(math.max(newPosition.y, 0), Track.TRACK_HEIGHT))

    _camera.position = _position - direction * Player.CAMERA_DISTANCE
    _camera.scanVector = direction

  }



}