package formula.engine
import formula.engine.Player.MAX_GEAR
import formula.io._

object Player {

  val MAX_GEAR   = 5
  val TURN_RATE  = 50D
  val BASE_SPEED = 15D

  //Cooldowns in seconds
  val GEAR_SHIFT_COOLDOWN  = 0.5D
  val DESTRUCTION_COOLDOWN = 1.3D
  val EFFECT_COOLDOWN = 0.1D
  val OIL_COOLDOWN = 1D

  //Volumes
  val ENGINE_VOLUME = 0.1D
  val SKID_VOLUME = 0.15

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


  //SoundSource
  protected val soundSource = new SoundSource(Sounds.CAR_SOUNDS)


  //Also cache all oil spills on the track
  protected val oilSpills = game.track.mapObjects.filter(_.name == "OilSpill")


  //Player state for movement and track progress
  protected var _lap = 1
  protected var _gear = 0
  protected var _turnMult = 0D

  //Keep track of different cooldowns
  protected var _lastGearShift = game.startTime
  protected var _lastDestruction = 0L
  protected var _lastCheckpoint = 0
  protected var _lastEffect = 0L
  protected var _lastOil = 0L

  protected var _position = initialPosition
  protected var _direction = initialDirection


  //Player state indicating visibility, and ability to move
  var destroyed = false
  var active = false
  var oiled  = false




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



  def cooldownOff(time: Long, start: Long, duration: Double) = (time - start) / Game.TIME_PRECISION  > duration

  //Movement options
  def shiftOnCooldown(time: Long) = !cooldownOff(time, _lastGearShift, Player.GEAR_SHIFT_COOLDOWN)

  def shiftGearUp(time: Long, deltaT: Double) = {

    if(!shiftOnCooldown(time)) {
      soundSource.turnOff(Sounds.ENGINE_SOUNDS(math.abs(gear)))
      gear += 1
      if(gear > Player.MAX_GEAR) gear = Player.MAX_GEAR
      _lastGearShift = time
      soundSource.turnOn(Sounds.ENGINE_SOUNDS(math.abs(gear)), Player.ENGINE_VOLUME)
    }

  }

  def shiftGearDown(time: Long, deltaT: Double) = {

    if(!shiftOnCooldown(time)) {
      soundSource.turnOff(Sounds.ENGINE_SOUNDS(math.abs(gear)))
      gear -= 1
      if(gear < -1) gear = -1
      _lastGearShift = time
      soundSource.turnOn(Sounds.ENGINE_SOUNDS(math.abs(gear)), Player.ENGINE_VOLUME)
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

    soundSource.turnAllOff()
    soundSource.playOnce(Sounds.Explosion, 1)
    AnimatedSprites.spawnSprite(AnimatedSprites.Explosion, _position, 7D, destructionTime)
    destroyed = true
    active = false
    gear = 0
    _lastDestruction = destructionTime

  }


  //Respawn the player at the last checkpoint they crossed
  def respawn() = {

    soundSource.turnOn(Sounds.Engine0, Player.ENGINE_VOLUME)
    _position = game.track.primaryPath(_lastCheckpoint)
    _direction = game.track.primaryPath.directionNormalized(_lastCheckpoint)
    destroyed = false
    active = true

  }




  def handleInput(time: Long, deltaT: Double) = {

    if(gearUp) shiftGearUp(time, deltaT)
    else if(gearDown) shiftGearDown(time, deltaT)

    if(turnLeft && !oiled) turnCarLeft(time, deltaT)
    if(turnRight && !oiled) turnCarRight(time, deltaT)

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

  //Begin the game with the engine idle sound
  soundSource.turnOn(Sounds.Engine0, Player.ENGINE_VOLUME)
  def update(time: Long, deltaT: Double) = {

    if(destroyed && cooldownOff(time, _lastDestruction, Player.DESTRUCTION_COOLDOWN)) {
      respawn()
    }

    if(oiled && cooldownOff(time, _lastOil, Player.OIL_COOLDOWN)) {
      oiled = false
    }

    if(active) {

      handleInput(time, deltaT)

      //Update position and velocity
      val velocity = direction * Player.BASE_SPEED * deltaT * gear
      val newPosition = position + velocity
      _position = V2D(math.min(math.max(newPosition.x, 0), Track.TRACK_WIDTH), math.min(math.max(newPosition.y, 0), Track.TRACK_HEIGHT))


      //Render animated particle effects
      if(formula.application.MainApplication.settings.effects && cooldownOff(time, _lastEffect, Player.EFFECT_COOLDOWN)) {

        if((turnLeft || turnRight) && math.abs(gear) > 0) {
          _lastEffect = time
          val sprite = if(oiled) AnimatedSprites.Oil else AnimatedSprites.Smoke
          AnimatedSprites.spawnSprite(sprite, _position + _direction.rotDeg(100) * scale * 0.35, 4D, time)
          AnimatedSprites.spawnSprite(sprite, _position + _direction.rotDeg(-100) * scale * 0.35, 4D, time)
        }

        else if(gear > 2) {
          _lastEffect = time
          AnimatedSprites.spawnSprite(AnimatedSprites.Speed, _position - _direction * scale * 0.1, 4D, time)
        }

      }

      //Play skid sound
      if((turnLeft || turnRight) && math.abs(gear) > 0 && oiled) {
        soundSource.turnOn(Sounds.Skid, Player.SKID_VOLUME)
      }
      else {
        soundSource.turnOff(Sounds.Skid)
      }

    }


    _camera.position = _position - direction * Player.CAMERA_DISTANCE
    _camera.scanVector = direction


    //Test if we hit an oil spill
    oilSpills.foreach(obj => {
      if((obj.position distSqr _position) < obj.scale * obj.scale) {
        _lastOil = time
        oiled = true
      }
    })

    //Test if we are offroad
    if(!game.track.road(_position) && !destroyed) {
      destroy(time)
    }

    else {
      handleCheckpoints()
    }

  }


}