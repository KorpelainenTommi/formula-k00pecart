package formula.engine.render
import formula.engine._
import java.awt.Graphics2D

/** A RenderTarget that divides the space given to it
 * evenly between its subtargets. Renders black bars between its
 * subtargets
 * @param onRenderComplete Callback function used for the game-render loop
 */
class SplitscreenRenderTarget(val onRenderComplete: () => Unit) extends RenderTarget {


  override protected def personalRender(g: Graphics2D): Unit = {
    g.setColor(java.awt.Color.BLACK)
    g.fillRect(absoluteBounds.x, absoluteBounds.y, absoluteBounds.width, absoluteBounds.height)
  }

  override def render(g: Graphics2D): Unit = {
    super.render(g)
    onRenderComplete()
  }


  def addSubTarget(t: RenderTarget) = {
    subTargets += t

    //Single player
    if(subTargets.length == 1) {
      subTargets(0).percentPosition = (0.1, 0)
      subTargets(0).percentSize = (0.8, 1)
    }

    //Splitscreen (2 or more)
    else {
      val d = 0.9875 / subTargets.length
      val offs = 0.0125 / (subTargets.length+1)
      for(i <- subTargets.indices) {
        subTargets(i).percentPosition = ((i+1)*offs+i*d, 0)
        subTargets(i).percentSize = (d, 1)
      }
    }
  }

}