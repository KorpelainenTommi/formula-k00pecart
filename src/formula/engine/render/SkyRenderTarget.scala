package formula.engine.render
import java.awt._
import formula.io._
import formula.engine._

class SkyRenderTarget extends RenderTarget {
  override protected def personalRender(g: Graphics2D) = {
    g.drawImage(FormulaIO.getTexture(Textures.Sky),
      absoluteBounds.x, absoluteBounds.y, absoluteBounds.width, absoluteBounds.height, null)
  }
}