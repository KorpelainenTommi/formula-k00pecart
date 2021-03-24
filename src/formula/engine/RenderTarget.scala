package formula.engine
import java.awt.Rectangle
import java.awt.Graphics2D
import formula.application.PercentBounds

abstract class RenderTarget extends PercentBounds {
  protected val subTargets = scala.collection.mutable.ArrayBuffer[RenderTarget]()
  protected var absoluteBounds: Rectangle = new Rectangle(0,0,0,0)

  override def updateBounds(width: Double, height: Double): Unit = {
    absoluteBounds = new Rectangle(math.round(pX*width).toInt, math.round(pY*height).toInt, math.round(pW*width).toInt, math.round(pH*height).toInt)
    subTargets.foreach(_.updateBounds(absoluteBounds.width, absoluteBounds.height, absoluteBounds.x, absoluteBounds.y))
  }

  def updateBounds(width: Double, height: Double, xOffset: Int, yOffset: Int): Unit = {
    absoluteBounds = new Rectangle(xOffset + math.round(pX*width).toInt, yOffset + math.round(pY*height).toInt, math.round(pW*width).toInt, math.round(pH*height).toInt)
    subTargets.foreach(_.updateBounds(absoluteBounds.width, absoluteBounds.height, absoluteBounds.x, absoluteBounds.y))
  }

  def render(g: Graphics2D): Unit = {
    personalRender(g)
    subTargets.foreach(_.render(g))
  }

  protected def personalRender(g: Graphics2D)
}