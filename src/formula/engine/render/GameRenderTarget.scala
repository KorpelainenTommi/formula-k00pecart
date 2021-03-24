package formula.engine.render
import formula.engine._
import formula.io._

import java.awt._
class GameRenderTarget(val game: Game) extends RenderTarget {

  private val grass = new GrassRenderTarget
  grass.percentPosition = (0, 0.25)
  grass.percentSize = (1, 0.75)
  subTargets += grass

  val sky = new SkyRenderTarget
  sky.percentSize = (1, 0.33)
  subTargets += sky

  override protected def personalRender(g: Graphics2D): Unit = {
    //Debug test


  }


}