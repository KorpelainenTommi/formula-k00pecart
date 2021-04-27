package formula.engine.render
import formula.application.GUIConstants
import formula.engine._
import formula.io._
import java.awt._

//RenderTarget that renders the minimap
class MapRenderTarget(val game: Game) extends RenderTarget {


  private var font: Option[Font] = None

  //Precalculate the screencoordinates for the goal line
  //since there's no reason to recalculate it every frame
  private val rW       = game.track.roadWidth
  private val goalPos  = game.track.primaryPath(0)
  private val goalPerp = game.track.primaryPath.perpendicular(0)

  //Calculate these when the absolute bounds get updated
  private var goalX1 = 0
  private var goalY1 = 0
  private var goalX2 = 0
  private var goalY2 = 0




  override def updateBounds(width: Double, height: Double, xOffset: Int, yOffset: Int): Unit = {
    super.updateBounds(width, height, xOffset, yOffset)
    font = Some(FormulaIO.getFont(GUIConstants.DEFAULT_FONT).deriveFont((absoluteBounds.width * GUIConstants.FONT_SIZE * 7).toFloat))

    //Calculate the coords for the goal line
    goalX1 = math.round(absoluteBounds.x + (absoluteBounds.width * (goalPos.x - (goalPerp.x * 0.5 * rW)) / Track.TRACK_WIDTH)).toInt
    goalY1 = math.round(absoluteBounds.y + (absoluteBounds.height * (goalPos.y - (goalPerp.y * 0.5 * rW)) / Track.TRACK_HEIGHT)).toInt
    goalX2 = math.round(absoluteBounds.x + (absoluteBounds.width * (goalPos.x + (goalPerp.x * 0.5 * rW)) / Track.TRACK_WIDTH)).toInt
    goalY2 = math.round(absoluteBounds.y + (absoluteBounds.height * (goalPos.y + (goalPerp.y * 0.5 * rW)) / Track.TRACK_HEIGHT)).toInt

  }

  override def personalRender(g: Graphics2D): Unit = {

    //Draw the map
    g.setColor(GUIConstants.COLOR_CELL_BORDER)
    g.setStroke(new BasicStroke(3))
    g.drawRect(absoluteBounds.x, absoluteBounds.y, absoluteBounds.width, absoluteBounds.height)
    g.drawImage(game.track.previewImage, absoluteBounds.x+1, absoluteBounds.y+1, absoluteBounds.width-2, absoluteBounds.height-2, null)

    //Draw map objects

    game.track.mapObjects.foreach(obj => {

      val scale = 20
      val scaleH = scale * obj.spriteRatio

      val x1 = (obj.position.x - 0.5 * scale) / Track.TRACK_WIDTH
      val x2 = (obj.position.x + 0.5 * scale) / Track.TRACK_WIDTH
      val y1 = (obj.position.y - 0.5 * scaleH) / Track.TRACK_HEIGHT
      val y2 = (obj.position.y + 0.5 * scaleH) / Track.TRACK_HEIGHT
      val w = x2 - x1
      val h = y2 - y1

      g.drawImage(FormulaIO.getTexture(obj.texture),
          math.round(absoluteBounds.x + absoluteBounds.width * x1).toInt,
          math.round(absoluteBounds.y + absoluteBounds.height * y1).toInt,
          math.round(absoluteBounds.width * w).toInt,
          math.round(absoluteBounds.height * h).toInt, null)

    })


    //Draw the green goal line
    g.setColor(Color.GREEN)
    g.setStroke(new BasicStroke(2))
    g.drawLine(goalX1, goalY1, goalX2, goalY2)

    //Draw the players icons on the map
    font.foreach(g.setFont)
    game.players.foreach(player => {
      val pX = absoluteBounds.x + (absoluteBounds.width * player.position.x / Track.TRACK_WIDTH) - (absoluteBounds.width * 0.05)
      val pY = absoluteBounds.y + (absoluteBounds.height * player.position.y / Track.TRACK_HEIGHT) + (absoluteBounds.width * 0.05)
      g.setColor(player.color)
      g.drawString(s"P${player.playerNumber+1}", math.round(pX).toInt, math.round(pY).toInt)
    })

  }

}