package formula.engine.render
import formula.engine._
import java.awt.Graphics2D

class BaseRenderTarget(val onRenderComplete: () => Unit) extends RenderTarget {

  private var cleaned = false

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
    val d = 0.9875 / subTargets.length
    val offs = 0.0125 / (subTargets.length+1)
    for(i <- subTargets.indices) {
      subTargets(i).percentPosition = ((i+1)*offs+i*d, 0)
      subTargets(i).percentSize = (d, 1)
    }
  }

}