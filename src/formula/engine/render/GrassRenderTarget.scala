package formula.engine.render
import formula.engine._
import formula.io._
import java.awt._

class GrassRenderTarget extends RenderTarget {

  private var grassTexturePaint: Option[TexturePaint] = None
  private var distanceGradient: Option[GradientPaint] = None
  private val startColor = new Color(0,69,19,240)
  private val endColor   = new Color(0,0,0,0)

  override def updateBounds(width: Double, height: Double, xOffset: Int, yOffset: Int): Unit = {

    super.updateBounds(width, height, xOffset, yOffset)
    grassTexturePaint = Some(new TexturePaint(FormulaIO.getTexture(Textures.Grass), new Rectangle(0, 0, absoluteBounds.width/2, absoluteBounds.height/2)))
    distanceGradient = Some(new GradientPaint(absoluteBounds.x, absoluteBounds.y, startColor, absoluteBounds.x, absoluteBounds.y+absoluteBounds.height, endColor))
  }

  override def personalRender(g: Graphics2D): Unit = {

    grassTexturePaint.foreach(p => {
      g.setPaint(p)
      g.fillRect(absoluteBounds.x, absoluteBounds.y, absoluteBounds.width, absoluteBounds.height)
    })

    distanceGradient.foreach(p => {
      g.setPaint(p)
      g.fillRect(absoluteBounds.x, absoluteBounds.y, absoluteBounds.width, absoluteBounds.height)
    })
  }
}