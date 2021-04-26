package formula.engine

class ComputerPlayer
(game: Game,
 initialPosition: V2D,
 initialDirection: V2D,
 playerNumber: Int)
 extends Player(game, initialPosition, initialDirection, playerNumber) {

  protected val AI_TURN_BONUS = 10D
  protected val CENTRE_CUTOFF = 0D
  protected val TURN_ANIMATION_COOLDOWN = 0.2
  protected val CURVINESS_FACTOR = 6.1D

  protected var lastTurnSwitch          = 0L
  protected var turnSwitchFrequency     = 0D


  protected var _turningLeft = false
  protected var _turningRight = false


  override def turnLeft  = if(turnSwitchFrequency < TURN_ANIMATION_COOLDOWN) false else _turningLeft
  override def turnRight = if(turnSwitchFrequency < TURN_ANIMATION_COOLDOWN) false else _turningRight

  override def calculateTurnMult: Double = {
    super.calculateTurnMult + AI_TURN_BONUS / game.track.roadWidth + (if(gear <= 2) AI_TURN_BONUS / 2 else 0)
  }

  protected def decideGear(curviness: Double) = {

    //These are experimental values from running the AI on a few tracks
    val mult = CURVINESS_FACTOR / game.track.roadWidth
    if(mult * curviness > 0.15) 1
    else if(mult * curviness > 0.014) 2
    else if(mult * curviness > 0.013) 3
    else if(mult * curviness > 0.0085) 4
    else 5

  }


  override def handleInput(time: Long, deltaT: Double): Unit = {

    val lastTurnLeft  = _turningLeft
    val lastTurnRight = _turningRight

    //Find out where we are on the track
    val trackIndex = V2D.locateIndex(position, game.track.primaryPath)
    val trackPos = game.track.primaryPath(trackIndex)
    val trackDir = game.track.primaryPath.directionNormalized(trackIndex)
    val trackPerp = game.track.primaryPath.perpendicular(trackIndex)

    val roadDirs = Vector.tabulate(4)(i => game.track.primaryPath.directionNormalized(i + trackIndex))
    val roadCurviness = roadDirs.sliding(2).foldLeft[Double](0D)((a, b) => a + (b(0) ang b(1))) / roadDirs.length

    val desiredGear = decideGear(roadCurviness)

    if(gear < desiredGear) shiftGearUp(time, deltaT)
    if(gear > desiredGear) shiftGearDown(time, deltaT)


    //Follow the track curves
    val directionDot = trackDir dot direction
    turnCarLeft(time, deltaT)
    val leftDot = trackDir dot direction
    turnCarRight(time, deltaT)
    turnCarRight(time, deltaT)
    val rightDot = trackDir dot direction

    if(directionDot > rightDot && directionDot > leftDot) {
      turnCarLeft(time, deltaT)
      _turningLeft = false
      _turningRight = false
    }
    else if(leftDot > directionDot && leftDot > rightDot) {
      turnCarLeft(time, deltaT)
      turnCarLeft(time, deltaT)
      _turningLeft = true
      _turningRight = false
    }
    else {
      _turningLeft = false
      _turningRight = true
    }


    //Try to stay in the centre
    val difference = V2D.changeBasis(position, trackPos, trackPerp * game.track.roadWidth / 2, trackDir)

    if(difference.x > CENTRE_CUTOFF) {

      if(_turningRight) {
        turnCarLeft(time, deltaT)
        _turningRight = false
      }

      else if(!_turningLeft) {
        turnCarLeft(time, deltaT)
        _turningLeft = true
      }

    }
    else if(difference.x < -CENTRE_CUTOFF) {

      if(_turningLeft) {
        turnCarRight(time, deltaT)
        _turningLeft = false
      }

      else if(!_turningRight) {
        turnCarRight(time, deltaT)
        _turningRight = true
      }

    }


    if(lastTurnLeft != _turningLeft || lastTurnRight != _turningRight) {
      turnSwitchFrequency = (time - lastTurnSwitch) / Game.TIME_PRECISION
      lastTurnSwitch = time
    }

  }

}