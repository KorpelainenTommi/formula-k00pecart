package formula.engine.render
import formula.application.GUIConstants
import formula.engine._
import formula.io._
import java.awt._

class MapRenderTarget(private val game: Game) extends RenderTarget {

  private var font: Option[Font] = None

  override def updateBounds(width: Double, height: Double, xOffset: Int, yOffset: Int): Unit = {
    super.updateBounds(width, height, xOffset, yOffset)
    font = Some(FormulaIO.getFont(GUIConstants.DEFAULT_FONT).deriveFont((absoluteBounds.width * GUIConstants.FONT_SIZE * 7).toFloat))
  }

  override def personalRender(g: Graphics2D): Unit = {
    g.setColor(GUIConstants.COLOR_CELL_BORDER)
    g.setStroke(new BasicStroke(3))
    g.drawRect(absoluteBounds.x, absoluteBounds.y, absoluteBounds.width, absoluteBounds.height)
    g.drawImage(game.track.previewImage, absoluteBounds.x+1, absoluteBounds.y+1, absoluteBounds.width-2, absoluteBounds.height-2, null)

    font.foreach(g.setFont)

    game.players.foreach(player => {
      val pX = absoluteBounds.x + (absoluteBounds.width * player.position.x / Track.TRACK_WIDTH) - (absoluteBounds.width * 0.05)
      val pY = absoluteBounds.y + (absoluteBounds.height * player.position.y / Track.TRACK_HEIGHT) + (absoluteBounds.width * 0.05)
      g.setColor(player.color)
      g.drawString(s"P${player.playerNumber+1}", math.round(pX).toInt, math.round(pY).toInt)
    })

  }
}