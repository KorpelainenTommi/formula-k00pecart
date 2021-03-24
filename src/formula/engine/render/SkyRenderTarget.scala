package formula.engine.render
import formula.engine._
import formula.io._
import java.awt._
class SkyRenderTarget extends RenderTarget {

  override protected def personalRender(g: Graphics2D) = {
    g.drawImage(FormulaIO.getTexture(Textures.Sky), absoluteBounds.x, absoluteBounds.y, absoluteBounds.width, absoluteBounds.height, null)
  }

}