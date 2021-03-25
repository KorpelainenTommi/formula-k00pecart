package formula.engine
import formula.io._

object Game {

  val TIME_PRECISION = 1000000000D

}

class Game(val track: Track) {

  private val player1 = new Player(this, V2D(100, 208), V2D.u, 0)
  private val player2 = new Player(this, V2D(2 * Track.TRACK_WIDTH / 3, 2 * Track.TRACK_HEIGHT / 3), V2D.u, 1)
  private val player1Input = Array.fill[Boolean](Settings.defaultPlayer1Controls.length)(false)
  private val player2Input = Array.fill[Boolean](Settings.defaultPlayer1Controls.length)(false)

  private val playerInput = Vector(player1Input, player2Input)
  val players = Vector(player1, player2)


  def player(playerNumber: Int) = players(playerNumber)

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

      val time = System.nanoTime()
      val elapsedTime = time - lastFrameTime
      lastFrameTime = time
      //println(elapsedTime/Game.TIME_PRECISION)

      players.foreach(_.update(time, elapsedTime / Game.TIME_PRECISION))




      // render
      _renderCallback()
    }
  }



}