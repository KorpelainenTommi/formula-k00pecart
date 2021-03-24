package formula.engine
class Game {

  private var _renderCallback = () => {}
  private var _startTime = 0L
  private var _started = false
  private var lastFrameTime = 0L
  def started = _started

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
      println(elapsedTime/1000)


      // render
      _renderCallback()
    }
  }
}