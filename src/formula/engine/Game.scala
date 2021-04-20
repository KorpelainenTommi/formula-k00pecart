package formula.engine
import formula.io._
import java.awt.Color

object Game {

  val TIME_PRECISION = 1000000000D
  val TARGET_FRAMERATE = 150
  val TARGET_FRAMETIME = 1D / TARGET_FRAMERATE

}

class Game(val track: Track, val nOfPlayers: Int) {

  private val player1Input = Array.fill[Boolean](Settings.defaultPlayer1Controls.length)(false)
  private val player2Input = Array.fill[Boolean](Settings.defaultPlayer2Controls.length)(false)

  private val playerInput = Vector(player1Input, player2Input, player2Input)
  val players = Vector.tabulate(nOfPlayers)(new Player(this, V2D(100, 208), V2D.u, _))
  val playerColors = Vector(Color.RED, Color.ORANGE, Color.GREEN)

  def input(playerNumber: Int, inputNumber: Int, keyDown: Boolean) = {
    playerInput(playerNumber)(inputNumber) = keyDown
  }

  def input(playerNumber: Int, inputNumber: Int) = {
    playerInput(playerNumber)(inputNumber)
  }


  private var _renderCallback = () => {}
  private var _startTime = 0L
  private var _started = false
  private var lastFrameTime = 0L
  def started = _started
  def startTime = _startTime

  def beginGameLoop(renderCallback: () => Unit) = {
    _startTime = System.nanoTime()
    lastFrameTime = _startTime
    _renderCallback = renderCallback
    _started = true
    gameUpdate()
  }

  def gameUpdate() = {
    if(started) {
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




      // render
      _renderCallback()
    }
  }



}