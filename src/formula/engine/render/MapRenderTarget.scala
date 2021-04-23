package formula.engine.render
import formula.application.GUIConstants
import formula.engine._
import formula.io._
import java.awt._

class MapRenderTarget(private val game: Game) extends RenderTarget {

  private var font: Option[Font] = None

  private val goalPos = game.track.primaryPath(0)
  private val goalPerp = game.track.primaryPath.perpendicular(0)
  private val rW = game.track.roadWidth
  private var goalX1 = 0
  private var goalY1 = 0
  private var goalX2 = 0
  private var goalY2 = 0




  override def updateBounds(width: Double, height: Double, xOffset: Int, yOffset: Int): Unit = {
    super.updateBounds(width, height, xOffset, yOffset)
    font = Some(FormulaIO.getFont(GUIConstants.DEFAULT_FONT).deriveFont((absoluteBounds.width * GUIConstants.FONT_SIZE * 7).toFloat))

    goalX1 = math.round(absoluteBounds.x + (absoluteBounds.width * (goalPos.x - (goalPerp.x * 0.5 * rW)) / Track.TRACK_WIDTH)).toInt
    goalY1 = math.round(absoluteBounds.y + (absoluteBounds.height * (goalPos.y - (goalPerp.y * 0.5 * rW)) / Track.TRACK_HEIGHT)).toInt
    goalX2 = math.round(absoluteBounds.x + (absoluteBounds.width * (goalPos.x + (goalPerp.x * 0.5 * rW)) / Track.TRACK_WIDTH)).toInt
    goalY2 = math.round(absoluteBounds.y + (absoluteBounds.height * (goalPos.y + (goalPerp.y * 0.5 * rW)) / Track.TRACK_HEIGHT)).toInt

  }

  override def personalRender(g: Graphics2D): Unit = {
    g.setColor(GUIConstants.COLOR_CELL_BORDER)
    g.setStroke(new BasicStroke(3))
    g.drawRect(absoluteBounds.x, absoluteBounds.y, absoluteBounds.width, absoluteBounds.height)
    g.drawImage(game.track.previewImage, absoluteBounds.x+1, absoluteBounds.y+1, absoluteBounds.width-2, absoluteBounds.height-2, null)

    g.setColor(Color.GREEN)
    g.setStroke(new BasicStroke(2))
    g.drawLine(goalX1, goalY1, goalX2, goalY2)

    font.foreach(g.setFont)

    game.players.foreach(player => {
      val pX = absoluteBounds.x + (absoluteBounds.width * player.position.x / Track.TRACK_WIDTH) - (absoluteBounds.width * 0.05)
      val pY = absoluteBounds.y + (absoluteBounds.height * player.position.y / Track.TRACK_HEIGHT) + (absoluteBounds.width * 0.05)
      g.setColor(player.color)
      g.drawString(s"P${player.playerNumber+1}", math.round(pX).toInt, math.round(pY).toInt)
    })

  }
}