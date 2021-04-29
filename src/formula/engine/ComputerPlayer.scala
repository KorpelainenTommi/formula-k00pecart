package formula.engine

//An AI controlled player
class ComputerPlayer
(game: Game,
 initialPosition: V2D,
 initialDirection: V2D,
 playerNumber: Int)
 extends Player(game, initialPosition, initialDirection, playerNumber) {

  //The AI is given a bonus in turning speed because it can only follow
  //discrete track directions. An alternative to this would be to slerp between
  //directions (so the AI can smoothly transition between directions)
  protected val AI_TURN_BONUS = 3.3D
  protected val TURN_ANIMATION_COOLDOWN = 0.2

  //Parameters for AI calculations
  protected val CENTRE_CUTOFF = 0D
  protected val CURVINESS_FACTOR = 9.8D
  protected val FUTURE_POINT_COUNT = 5

  //Variables for tracking how much the AI has turned
  protected var lastTurnSwitch          = 0L
  protected var turnSwitchFrequency     = 0D


  protected var _turningLeft = false
  protected var _turningRight = false

  //This is to stop the AI car from spamming turn animations, because it was annoying
  override def turnLeft  = if(turnSwitchFrequency < TURN_ANIMATION_COOLDOWN) false else _turningLeft
  override def turnRight = if(turnSwitchFrequency < TURN_ANIMATION_COOLDOWN) false else _turningRight


  override def calculateTurnMult: Double = {
    super.calculateTurnMult * AI_TURN_BONUS //(if(gear <= 2) AI_TURN_BONUS / 2 else 0)
  }




  protected def decideGear(curviness: Double) = {

    //These are handpicked experimental parameters from running the AI on a bunch of tracks
    //Would be fun to calculate these with a machine learning algorithm some time :)
    val mult = CURVINESS_FACTOR / game.track.roadWidth
    if(mult * curviness > 0.40) 1
    else if(mult * curviness > 0.13) 2
    else if(mult * curviness > 0.05) 3
    else if(mult * curviness > 0.003) 4
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


    //Approximate how curvy the road is
    val roadDirs = Vector.tabulate(FUTURE_POINT_COUNT)(i => game.track.primaryPath.directionNormalized(i + trackIndex))
    val angs = roadDirs.map(_ ang _direction).filterNot(_.isNaN) //For some reason there's NaNs, quick fix is to filter
    val roadCurviness = angs.map(d => d * d).sum / roadDirs.length

    val desiredGear = math.min(5, decideGear(roadCurviness) + 2)

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

    //Update turning frequency
    if(lastTurnLeft != _turningLeft || lastTurnRight != _turningRight) {
      turnSwitchFrequency = (time - lastTurnSwitch) / Game.TIME_PRECISION
      lastTurnSwitch = time
    }

  }

}