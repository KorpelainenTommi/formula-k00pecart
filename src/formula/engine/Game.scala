package formula.engine
import formula.io._
import java.awt.Color

object Game {

  val TIME_PRECISION = 1000000000D
  val CLOCK_PRECISION = 1000000D
  val TARGET_FRAMETIME = 1D / formula.application.MainApplication.settings.targetFramerate

}

class Game(val track: Track, val nOfPlayers: Int, val nOfLaps: Int) {

  private val player1Input = Array.fill[Boolean](Settings.defaultPlayer1Controls.length)(false)
  private val player2Input = Array.fill[Boolean](Settings.defaultPlayer2Controls.length)(false)

  private val playerInput = Vector(player1Input, player2Input, player2Input)
  val players = Vector.tabulate(nOfPlayers)(n => new Player(this,
    track.primaryPath(0) - track.primaryPath.perpendicular(0) * track.roadWidth * 0.5 + track.primaryPath.perpendicular(0) * track.roadWidth * 0.25 * (n * 2 + 1),
    track.primaryPath.directionNormalized(0), n))
  val playerColors = Vector(Color.RED, Color.ORANGE, Color.GREEN)

  def input(playerNumber: Int, inputNumber: Int, keyDown: Boolean) = {
    playerInput(playerNumber)(inputNumber) = keyDown
  }

  def input(playerNumber: Int, inputNumber: Int) = {
    playerInput(playerNumber)(inputNumber)
  }


  private var _renderCallback = () => {}

  private var lastFrameTime = 0L

  private var _startTime = 0L
  private var _gameStarted = false
  def gameStarted = _gameStarted

  protected var _clockStarted = false
  def clockStarted = _clockStarted

  def startTime = _startTime
  def time = lastFrameTime
  def clockTime = if(clockStarted) Track.describeTrackTime((((time - startTime) / Game.CLOCK_PRECISION).toInt, "")) else "0:00.00"

  def screenText(playerNumber: Int) = {
    val t = (time - startTime) / Game.TIME_PRECISION

    if(clockStarted) {
      if(t > 1.2) ""
      else "GO"
    }
    else {
      if(t > 1.2) "SET"
      else "READY"
    }
  }

  def lap(playerNumber: Int) = s"Lap ${players(playerNumber).lap}/$nOfLaps"


  def beginGameLoop(renderCallback: () => Unit) = {
    _startTime = System.nanoTime()
    lastFrameTime = _startTime
    _renderCallback = renderCallback
    _gameStarted = true
    gameUpdate()
  }

  def gameUpdate() = {
    if(gameStarted) {
      //Do game logic

      var time = System.nanoTime()
      var elapsedTime = time - lastFrameTime

      while(elapsedTime / Game.TIME_PRECISION < Game.TARGET_FRAMETIME) {
        //Release CPU slices so the game doesn't hog processing
        java.lang.Thread.`yield`()
        time = System.nanoTime()
        elapsedTime = time - lastFrameTime
      }

      lastFrameTime = time
      //println(elapsedTime/Game.TIME_PRECISION)

      players.foreach(_.update(time, elapsedTime / Game.TIME_PRECISION))

      if(!clockStarted && (time - startTime) / Game.TIME_PRECISION > 2.4) {
        players.foreach(_.active = true)
        _startTime = time
        _clockStarted = true
      }

      // render
      _renderCallback()
    }
  }

  def victory(playerNumber: Int) = {
    players.filterNot(_.playerNumber == playerNumber).foreach(_.active = false)


  }
}