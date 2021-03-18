package formula.engine
import java.awt.Rectangle
import java.awt.Graphics2D
import formula.application.PercentBounds
abstract class RenderTarget extends PercentBounds {
  protected val subTargets = scala.collection.mutable.ArrayBuffer[RenderTarget]()
  protected var absoluteBounds: Rectangle = new Rectangle(0,0,0,0)

  override def updateBounds(width: Double, height: Double): Unit = {
    absoluteBounds = new Rectangle((pX*width).toInt, (pY*height).toInt, (pW*width).toInt, (pH*height).toInt)
    subTargets.foreach(_.updateBounds(width, height))
  }

  def render(g: Graphics2D): Unit = {
    personalRender(g)
    subTargets.foreach(_.render(g))
  }

  protected def personalRender(g: Graphics2D)
}