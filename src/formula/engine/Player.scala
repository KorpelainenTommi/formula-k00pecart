package formula.engine
import formula.io._

object Player {

  val MAX_GEAR   = 5
  val TURN_RATE  = 50D
  val BASE_SPEED = 15D

  val GEAR_SHIFT_COOLDOWN  = 0.5D //seconds
  val DESTRUCTION_COOLDOWN = 1.3D

  //CAMERA_DISTANCE is the distance at which the camera follows the player
  //PLAYER_SIZE determines the scale of the player for rendering and game logic
  val CAMERA_DISTANCE = 5D
  val PLAYER_SIZE = 5D

}

class Player
(val game: Game,
 initialPosition: V2D,
 initialDirection: V2D,
 val playerNumber: Int) extends Sprite {


  //Constants
  private val carTextures = if(playerNumber == 0) Textures.CAR_RED_TEXTURES else Textures.CAR_ORANGE_TEXTURES
  protected val checkpointSqrDist = game.track.roadWidth * game.track.roadWidth * 1.7D //Give a bit of leeway for good measure
  protected val _camera = new Camera
  _camera.position = initialPosition - initialDirection * Player.CAMERA_DISTANCE


  //Player state for movement and track progress
  protected var _lap = 1
  protected var _gear = 0
  protected var _turnMult = 0D
  protected var _lastGearShift = game.startTime
  protected var _lastDestruction = 0L
  protected var _lastCheckpoint = 0

  protected var _position = initialPosition
  protected var _direction = initialDirection


  //Player state indicating visibility, and ability to move
  var destroyed = false
  var active = false




  //Player input
  def gearUp    = game.input(playerNumber, 2)
  def gearDown  = game.input(playerNumber, 3)
  def turnLeft  = game.input(playerNumber, 0)
  def turnRight = game.input(playerNumber, 1)

  //Player info
  def scale     = Player.PLAYER_SIZE
  def color     = game.playerColors(playerNumber)
  def position  = _position
  def direction = _direction

  def lap       = _lap
  def gear      = _gear
  def camera    = _camera
  def turnMult  = _turnMult


  protected def lap_=(value: Int) = {
    if(value < 1) _lap = 1
    else {
      _lap = value
      if(_lap > game.nOfLaps) {
        game.victory(playerNumber)
      }
    }
  }

  protected def gear_=(value: Int) = {
    _gear = value
    _turnMult = calculateTurnMult
  }

  protected def calculateTurnMult: Double = {
    if(gear == 0) 0D
    else if(gear <= 1) 0.5 * Player.MAX_GEAR
    else 0.5 * (1 + Player.MAX_GEAR - gear)
  }



  //texture isn't really used, since player textures should depend on direction
  //But since Player is a sprite, it needs to implement this.
  //Player rendering uses dirTexture instead
  def texture = {
    if((turnLeft && turnRight) || (!turnLeft && !turnRight)) carTextures(0)
    else if(turnLeft) carTextures(1)
    else carTextures(2)
  }

  def spriteRatio = {
    val img = FormulaIO.getTexture(texture)
    1D * img.getHeight() / img.getWidth()
  }


  //region spaghetti
  def dirTexture(lookingDirection: V2D, perpendicular: V2D) = {

    if(destroyed) {
      None
    }

    else {
      //Figure out the texture based on some dot products and stuff
      val dotP = direction dot lookingDirection
      val dotPerp = direction dot perpendicular

      if(dotP > 0.85) {
        Some(texture)
      }

      else if(dotP > 0.5) {
        if(dotPerp >= 0) Some(carTextures(2))
        else Some(carTextures(1))
      }

      else if(dotP > -0.5) {
        if(dotPerp >= 0) Some(carTextures(4))
        else Some(carTextures(3))
      }

      else if(dotP > -0.85) {
        if(dotPerp >= 0) Some(carTextures(6))
        else Some(carTextures(5))
      }

      else {
        Some(carTextures(7))
      }
    }

  }
  //endregion




  //Movement options
  def shiftOnCooldown(time: Long) = (time - _lastGearShift) / Game.TIME_PRECISION <= Player.GEAR_SHIFT_COOLDOWN

  def shiftGearUp(time: Long, deltaT: Double) = {

    if(!shiftOnCooldown(time)) {
      gear += 1
      if(gear > Player.MAX_GEAR) gear = Player.MAX_GEAR
      _lastGearShift = time
    }

  }

  def shiftGearDown(time: Long, deltaT: Double) = {

    if(!shiftOnCooldown(time)) {
      gear -= 1
      if(gear < -1) gear = -1
      _lastGearShift = time
    }

  }

  def turnCarLeft(time: Long, deltaT: Double) = {
    _direction = _direction.rotDeg(-(Player.TURN_RATE * turnMult) * deltaT).normalized
  }

  def turnCarRight(time: Long, deltaT: Double) = {
    _direction = _direction.rotDeg(turnMult * Player.TURN_RATE * deltaT).normalized
  }



  //Destroy the visible player car
  def destroy(destructionTime: Long) = {

    AnimatedSprites.spawnSprite(AnimatedSprites.Explosion, _position, 7D, destructionTime)
    destroyed = true
    active = false
    gear = 0
    _lastDestruction = destructionTime

  }


  //Respawn the player at the last checkpoint they crossed
  def respawn() = {

    _position = game.track.primaryPath(_lastCheckpoint)
    _direction = game.track.primaryPath.directionNormalized(_lastCheckpoint)
    destroyed = false
    active = true

  }




  def handleInput(time: Long, deltaT: Double) = {

    if(gearUp) shiftGearUp(time, deltaT)
    else if(gearDown) shiftGearDown(time, deltaT)

    if(turnLeft) turnCarLeft(time, deltaT)
    if(turnRight) turnCarRight(time, deltaT)

  }



  //Check other checkpoints by proximity
  //Check the goal more precisely by getting our y coordinate in the goal direction
  def handleCheckpoints() = {

    //Closest checkpoint to us
    val checkpoint = V2D.locateIndex(_position, game.track.primaryPath)

    if(_lastCheckpoint + 1 == checkpoint && game.track.primaryPath(checkpoint).distSqr(_position) <= checkpointSqrDist) {
      _lastCheckpoint = checkpoint
    }

    //If we are on the last checkpoint, test if we passed the goal line
    if(_lastCheckpoint == game.track.primaryPath.length - 1) {

      val goalDir = game.track.primaryPath.direction(0)
      val goalPerp = game.track.primaryPath.perpendicular(0)
      val coords = V2D.changeBasis(_position, game.track.primaryPath(0), goalPerp, goalDir)

      if(coords.y >= 0) {
        _lastCheckpoint = 0
        lap += 1
      }

    }

  }




  def update(time: Long, deltaT: Double) = {

    if(destroyed && (time - _lastDestruction) / Game.TIME_PRECISION > Player.DESTRUCTION_COOLDOWN) {
      respawn()
    }

    if(active) {

      handleInput(time, deltaT)

      //Update position and velocity
      val velocity = direction * Player.BASE_SPEED * deltaT * gear
      val newPosition = position + velocity
      _position = V2D(math.min(math.max(newPosition.x, 0), Track.TRACK_WIDTH), math.min(math.max(newPosition.y, 0), Track.TRACK_HEIGHT))

    }


    _camera.position = _position - direction * Player.CAMERA_DISTANCE
    _camera.scanVector = direction

    //Test if we are offroad
    if(!game.track.road(_position) && !destroyed) {
      destroy(time)
    }

    else {
      handleCheckpoints()
    }

  }


}