package formula.engine
import formula.io._
import java.awt.Color
import formula.application.MainApplication
import formula.application.screens.ResultScreen

object Game {

  //TIME_PRECISION is the units per second for the high precision timer
  //CLOCK_PRECISION is the units per second for the clock timer

  val TIME_PRECISION = 1000000000D
  val CLOCK_PRECISION = 1000000D
  def TARGET_FRAMETIME = 1D / formula.application.MainApplication.settings.targetFramerate

  val GAME_COUNTDOWN = 2.4D
  val PLAYER_NAME_MAX_LENGTH = 13

}


/** Represents a game, and offers access to the track and players in it
 * handles updating players and rendering in a game loop
 *
 * @param track The track to race on
 * @param nOfPlayers The number of players to spawn
 * @param nOfLaps The number of laps to victory
 */
class Game
(val track: Track,
 val nOfPlayers: Int,
 val nOfLaps: Int,
 val playerNames: Vector[String] = Vector("Player 1", "Player 2"),
 val playerAI: Vector[Boolean] = Vector(false, false),
 val musicFilename: Option[String] = None) {


  //Player variables and methods

  //List of booleans indicating the currently pressed keys
  private val player1Input = Array.fill[Boolean](Settings.defaultPlayer1Controls.length)(false)
  private val player2Input = Array.fill[Boolean](Settings.defaultPlayer2Controls.length)(false)


  val playerColors = Vector(Color.RED, Color.ORANGE)
  private val playerInput = Vector(player1Input, player2Input)

  val players = Vector.tabulate(nOfPlayers)(n => {

    val goalPos = track.primaryPath(0)
    val goalPerp = track.primaryPath.perpendicular(0)
    val d = track.roadWidth / 2

    if(playerAI.length > n && playerAI(n)) {
      new ComputerPlayer(this,
      goalPos - goalPerp * d + goalPerp * d * 0.5 * (n * 2 + 1), //Place players side by side on the goal line
      track.primaryPath.directionNormalized(0), n)
    }

    else {
      new Player(this,
      goalPos - goalPerp * d + goalPerp * d * 0.5 * (n * 2 + 1), //Place players side by side on the goal line
      track.primaryPath.directionNormalized(0), n)
    }

  })


  //Read and write input booleans
  def input(playerNumber: Int, inputNumber: Int) = {
    playerInput(playerNumber)(inputNumber)
  }

  def input(playerNumber: Int, inputNumber: Int, keyDown: Boolean) = {
    playerInput(playerNumber)(inputNumber) = keyDown
  }





  //Game timing and state

  private var _renderCallback = () => {}

  private var _startTime     = 0L
  private var _lastFrameTime = 0L

  private var _gameStarted   = false
  private var _clockStarted  = false
  private var _countDown = 0


  def time         = _lastFrameTime
  def startTime    = _startTime
  def clockTime    = if(clockStarted) Track.describeTrackTime((((time - startTime) / Game.CLOCK_PRECISION).toInt, "")) else "0:00.00"

  def gameStarted  = _gameStarted
  def clockStarted = _clockStarted





  //Lap text for a player
  def lap(playerNumber: Int) = s"Lap ${players(playerNumber).lap}/$nOfLaps"

  //Information for each player (currently used for "Ready, set, go")
  def screenText(playerNumber: Int) = {

    val t = (time - startTime) / Game.TIME_PRECISION

    if(clockStarted) {
      if(t > Game.GAME_COUNTDOWN / 2) ""
      else "GO"
    }
    else {
      if(t > Game.GAME_COUNTDOWN / 2) "SET"
      else "READY"
    }

  }





  //Begin rendering, and start the race countdown
  def beginGameLoop(renderCallback: () => Unit) = {
    _startTime = System.nanoTime()
    _lastFrameTime = _startTime
    _renderCallback = renderCallback
    _gameStarted = true
    gameUpdate()
  }



  def gameUpdate() = {

    if(gameStarted) {
      //Do game logic

      var time = System.nanoTime()
      var elapsedTime = time - _lastFrameTime


      //When we have achieved the target framerate,
      //return CPU timeslices so the game doesn't hog processing
      while(elapsedTime / Game.TIME_PRECISION < Game.TARGET_FRAMETIME) {
        java.lang.Thread.`yield`()
        time = System.nanoTime()
        elapsedTime = time - _lastFrameTime
      }


      //Player update
      _lastFrameTime = time
      players.foreach(_.update(time, elapsedTime / Game.TIME_PRECISION))


      //Countdown

      if(_countDown == 0) {
        SoundSystem.playSound(Sounds.CountDown0)
        _countDown = 1
      }

      else if(!clockStarted && _countDown == 1 && (time - startTime) / Game.TIME_PRECISION > Game.GAME_COUNTDOWN / 2) {
        SoundSystem.playSound(Sounds.CountDown0)
        _countDown = 2
      }


      //Countdown over
      //Activate the players and start the clock
      if(!clockStarted && (time - startTime) / Game.TIME_PRECISION > Game.GAME_COUNTDOWN) {
        SoundSystem.playSound(Sounds.CountDown1)
        musicFilename.foreach(SoundSystem.playMusic)
        players.foreach(_.active = true)
        _startTime = time
        _clockStarted = true
      }


      //render
      _renderCallback()
    }

  }


  def victory(playerNumber: Int) = {

    players.filterNot(_.playerNumber == playerNumber).foreach(_.active = false)
    MainApplication.transition(new ResultScreen(track, ((time - startTime) / Game.CLOCK_PRECISION).toInt, nOfLaps, playerNames.take(nOfPlayers), playerNumber))

  }
}