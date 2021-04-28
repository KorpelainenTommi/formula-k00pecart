package formula.engine.render
import java.awt._
import formula.io._
import formula.engine._
import formula.application.GUIConstants

//RenderTarget responsible for rendering the interactive view of the tracktool
class TrackToolRenderTarget(val tool: TrackTool) extends RenderTarget {


  //Color constants
  private val COLOR_DIR               = new Color( 0 , 255,  0 , 255)
  private val COLOR_GOAL              = new Color(255, 120,  0 , 255)
  private val COLOR_PATH              = new Color(255, 255,  0 , 100)
  private val COLOR_HIGHLIGHT         = new Color(255, 120,  0 , 120)
  private val COLOR_PLACEOBJECTS      = new Color( 0,  255, 225, 100)
  private val COLOR_DRAWTRACK_VALID   = new Color( 0 ,  0 , 255, 100)
  private val COLOR_DRAWTRACK_INVALID = new Color(255,  0 ,  0 , 100)



  private def drawCircle(pos: V2D, diameter: Double, g: Graphics2D) = {
    val d = diameter / Track.TRACK_WIDTH
    g.fillOval(
      math.round(absoluteBounds.x + (pos.x - 0.5 * d) * absoluteBounds.width).toInt,
      math.round(absoluteBounds.y + (pos.y - 0.5 * d) * absoluteBounds.height).toInt,
      math.round(absoluteBounds.width*d).toInt,
      math.round(absoluteBounds.height*d).toInt)
  }


  private def drawArrow(from: V2D, to: V2D, g: Graphics2D) = {

    //Draw the main line
    val x1 = math.round(absoluteBounds.x + from.x * absoluteBounds.width).toInt
    val y1 = math.round(absoluteBounds.y + from.y * absoluteBounds.height).toInt
    val x2 = math.round(absoluteBounds.x + to.x * absoluteBounds.width).toInt
    val y2 = math.round(absoluteBounds.y + to.y * absoluteBounds.height).toInt
    g.drawLine(x1, y1, x2, y2)

    //Draw the smaller arrow thingies
    val ding1 = to + (from - to).rotDeg(45) * 0.25
    val ding2 = to + (from - to).rotDeg(-45) * 0.25

    val dx1 = math.round(absoluteBounds.x + ding1.x * absoluteBounds.width).toInt
    val dy1 = math.round(absoluteBounds.y + ding1.y * absoluteBounds.height).toInt
    val dx2 = math.round(absoluteBounds.x + ding2.x * absoluteBounds.width).toInt
    val dy2 = math.round(absoluteBounds.y + ding2.y * absoluteBounds.height).toInt

    g.drawLine(x2, y2, dx1, dy1)
    g.drawLine(x2, y2, dx2, dy2)

  }



  override protected def personalRender(g: Graphics2D): Unit = {

    //Draw the current track
    g.setColor(GUIConstants.COLOR_TRACK_GRASS)
    g.fillRect(absoluteBounds.x, absoluteBounds.y, absoluteBounds.width, absoluteBounds.height)
    g.drawImage(tool.trackImage, absoluteBounds.x, absoluteBounds.y, absoluteBounds.width, absoluteBounds.height, null)


    //Draw placed mapObjects
    tool.mapObjects.foreach(obj => {

      val scale = 10
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


    //Highlight the cursor position
    if(tool.mode == TrackTool.DrawRoad) {
      g.setColor(if(tool.mousePositionValid) COLOR_DRAWTRACK_VALID else COLOR_DRAWTRACK_INVALID)
      drawCircle(tool.mousePosition, tool.roadWidth, g)
    }

    else if(tool.mode == TrackTool.PlaceObjects) {
      g.setColor(COLOR_PLACEOBJECTS)
      drawCircle(tool.mousePosition, TrackTool.MAP_OBJECT_SELECTION_RADIUS * 2, g)
    }


    //Highlight selected goal location
    if(tool.mode == TrackTool.PlaceGoal && tool.pathPositions.nonEmpty) {

      val pos = V2D(tool.mousePosition.x * Track.TRACK_WIDTH, tool.mousePosition.y * Track.TRACK_HEIGHT)
      val highlightPoint = V2D.locate(pos, tool.pathPositions)
      if((pos distSqr highlightPoint) <= TrackTool.PATH_POSITION_DIST_SQR) {
        g.setColor(COLOR_GOAL)
        drawCircle(V2D(highlightPoint.x / Track.TRACK_WIDTH, highlightPoint.y / Track.TRACK_HEIGHT), 5, g)
      }

    }


    //Hightlight actual goal location
    g.setColor(COLOR_GOAL)
    tool.goalPosition.foreach(p => {
      drawCircle(V2D(p.x / Track.TRACK_WIDTH, p.y / Track.TRACK_HEIGHT), 5, g)
    })


    //Show track direction with an arrow
    if(tool.roadCompleted && tool.pathPositions.nonEmpty) {
      g.setColor(COLOR_DIR)
      g.setStroke(new BasicStroke(2))
      val pos1 = V2D(tool.pathPositions(0).x / Track.TRACK_WIDTH, tool.pathPositions(0).y / Track.TRACK_HEIGHT)
      val pos2 = V2D(tool.pathPositions(1).x / Track.TRACK_WIDTH, tool.pathPositions(1).y / Track.TRACK_HEIGHT)
      drawArrow(pos1, pos2, g)
    }


    //Show track checkpoints
    g.setColor(COLOR_PATH)
    tool.pathPositions.foreach(p => {
      drawCircle(V2D(p.x / Track.TRACK_WIDTH, p.y / Track.TRACK_HEIGHT), 2, g)
    })

    //Draw border
    g.setColor(GUIConstants.COLOR_CELL_BORDER)
    g.setStroke(new BasicStroke(3))
    g.drawRect(absoluteBounds.x, absoluteBounds.y, absoluteBounds.width-2, absoluteBounds.height-2)

  }


}