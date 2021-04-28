package formula.engine.render
import java.awt._
import formula.io._
import formula.engine._

class GearRenderTarget(val player: Player) extends RenderTarget {
  override protected def personalRender(g: Graphics2D) = {
    g.drawImage(FormulaIO.getTexture(Textures.HUD_TEXTURES(player.gear + 1)),
      absoluteBounds.x, absoluteBounds.y, absoluteBounds.width, absoluteBounds.height, null)
  }
}