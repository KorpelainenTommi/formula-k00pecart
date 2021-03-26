package formula.engine.render
import formula.application.GUIConstants
import formula.engine._
import formula.io._
import java.awt._

class MapRenderTarget(private val game: Game) extends RenderTarget {

  private var font: Option[Font] = None


  val debugCamera1 = new CameraDebugRenderTarget(game.player(0).camera, Color.RED)
  debugCamera1.percentBounds = (0,0,1,1)
  subTargets += debugCamera1

  val debugCamera2 = new CameraDebugRenderTarget(game.player(1).camera, Color.BLUE)
  debugCamera2.percentBounds = (0,0,1,1)
  subTargets += debugCamera2

  val debugPath = new PathDebugRenderTarget(Color.MAGENTA)
  debugPath.percentBounds = (0,0,1,1)
  subTargets += debugPath


  override def updateBounds(width: Double, height: Double, xOffset: Int, yOffset: Int): Unit = {
    super.updateBounds(width, height, xOffset, yOffset)
    font = Some(FormulaIO.getFont(Fonts.Impact).deriveFont((absoluteBounds.width * GUIConstants.FONT_SIZE * 7).toFloat))
  }

  override def personalRender(g: Graphics2D): Unit = {
    g.setColor(GUIConstants.COLOR_CELL_BORDER)
    g.setStroke(new BasicStroke(3))
    g.drawRect(absoluteBounds.x, absoluteBounds.y, absoluteBounds.width, absoluteBounds.height)
    g.drawImage(game.track.previewImage, absoluteBounds.x+1, absoluteBounds.y+1, absoluteBounds.width-2, absoluteBounds.height-2, null)

    font.foreach(g.setFont)

    val player1Pos = game.player(0).position
    val player2Pos = game.player(1).position

    val player1X = absoluteBounds.x + (absoluteBounds.width * player1Pos.x / Track.TRACK_WIDTH) - (absoluteBounds.width * 0.05)
    val player1Y = absoluteBounds.y + (absoluteBounds.height * player1Pos.y / Track.TRACK_HEIGHT) + (absoluteBounds.width * 0.05)
    val player2X = absoluteBounds.x + (absoluteBounds.width * player2Pos.x / Track.TRACK_WIDTH) - (absoluteBounds.width * 0.05)
    val player2Y = absoluteBounds.y + (absoluteBounds.height * player2Pos.y / Track.TRACK_HEIGHT) + (absoluteBounds.width * 0.05)

    g.setColor(Color.RED)
    g.drawString("P1", math.round(player1X).toInt, math.round(player1Y).toInt)

    g.setColor(Color.BLUE)
    g.drawString("P2", math.round(player2X).toInt, math.round(player2Y).toInt)

  }
}