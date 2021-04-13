package formula.engine.render
import formula.application.GUIConstants
import formula.engine._
import java.awt._


class TrackToolRenderTarget(val tool: TrackTool) extends RenderTarget {

  private val COLOR_DRAWTRACK_INVALID = new Color(255, 0, 0, 100)
  private val COLOR_DRAWTRACK_VALID = new Color(0, 0, 255, 100)
  private val COLOR_GOAL = new Color(255, 120, 0, 255)
  private val COLOR_PATH = new Color(255, 255, 0, 100)

  private def drawCircle(pos: V2D, diameter: Double, g: Graphics2D) = {
    val d = diameter / Track.TRACK_WIDTH
    g.fillOval(
      math.round(absoluteBounds.x + (pos.x - 0.5 * d) * absoluteBounds.width).toInt,
      math.round(absoluteBounds.y + (pos.y - 0.5 * d) * absoluteBounds.height).toInt,
      math.round(absoluteBounds.width*d).toInt,
      math.round(absoluteBounds.height*d).toInt)
  }


  override protected def personalRender(g: Graphics2D): Unit = {


    g.setColor(GUIConstants.COLOR_TRACK_GRASS)
    g.fillRect(absoluteBounds.x, absoluteBounds.y, absoluteBounds.width, absoluteBounds.height)
    g.drawImage(tool.trackImage, absoluteBounds.x, absoluteBounds.y, absoluteBounds.width, absoluteBounds.height, null)


    if(tool.mode == TrackTool.DrawRoad) {
      g.setColor(if(tool.mousePositionValid) COLOR_DRAWTRACK_VALID else COLOR_DRAWTRACK_INVALID)
      drawCircle(tool.mousePosition, tool.roadWidth, g)
    }



    g.setColor(COLOR_GOAL)
    tool.goalPosition.foreach(p => {
      drawCircle(V2D(p.x / Track.TRACK_WIDTH, p.y / Track.TRACK_HEIGHT), 5, g)
    })

    g.setColor(COLOR_PATH)
    tool.pathPositions.foreach(p => {
      drawCircle(V2D(p.x / Track.TRACK_WIDTH, p.y / Track.TRACK_HEIGHT), 2, g)
    })

    g.setColor(GUIConstants.COLOR_CELL_BORDER)
    g.setStroke(new BasicStroke(3))
    g.drawRect(absoluteBounds.x, absoluteBounds.y, absoluteBounds.width-2, absoluteBounds.height-2)
  }
}