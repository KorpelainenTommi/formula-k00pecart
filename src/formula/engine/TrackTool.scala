package formula.engine
import java.awt._
import formula.application._

import java.awt.image.BufferedImage
import scala.collection.mutable.ArrayBuffer

object TrackTool {

  sealed trait Mode

  final object NoSelection extends TrackTool.Mode
  final object DrawRoad extends TrackTool.Mode
  final object DrawShortcut extends TrackTool.Mode
  final object PlaceObjects extends TrackTool.Mode
  final object PlaceGoal extends TrackTool.Mode

  val PATH_POSITION_DIST = 10
  val PATH_POSITION_DIST_SQR = PATH_POSITION_DIST * PATH_POSITION_DIST

}

class TrackTool {

  val trackImage = new BufferedImage(Track.TRACK_WIDTH, Track.TRACK_HEIGHT, BufferedImage.TYPE_INT_ARGB)
  protected val trackGraphics = trackImage.getGraphics.asInstanceOf[Graphics2D]


  protected var _mousePosition = V2D(-1, -1)
  def mousePosition = _mousePosition
  def mousePosition_=(value: V2D) = {
    val lastPos = _mousePosition
    _mousePosition = value

    if(mode == TrackTool.DrawRoad && drawingTrack) {
      trackDrawUpdate(lastPos, _mousePosition)
    }
  }

  protected var _roadWidth = 25D
  def roadWidth = _roadWidth
  def roadWidth_=(value: Double) = _roadWidth = value

  protected var _mode: TrackTool.Mode = TrackTool.NoSelection
  def mode = _mode
  def mode_=(value: TrackTool.Mode) = _mode = value

  protected var _drawingTrack = false
  def drawingTrack = _drawingTrack


  protected var _goalPosition: Option[V2D] = None
  def goalPosition = _goalPosition

  protected val primaryPathPositions = ArrayBuffer[V2D]()
  def pathPositions = primaryPathPositions.toVector

  var onTrackCompleted: () => Unit = () => {}


  def beginTrackDraw() = {
    if(mousePositionValid) {
      primaryPathPositions.clear()
      clearArea()
      trackGraphics.setColor(Color.BLACK)
      trackGraphics.setStroke(new BasicStroke(roadWidth.toFloat, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND))
      _drawingTrack = true
      val gX = math.round(mousePosition.x * Track.TRACK_WIDTH).toInt
      val gY = math.round(mousePosition.y * Track.TRACK_HEIGHT).toInt
      _goalPosition = Some(V2D(gX, gY))
      primaryPathPositions += _goalPosition.get
    }
  }

  protected def placePathPoints(pos: V2D) = {

    val p1 = primaryPathPositions.last
    val p2 = V2D(pos.x * Track.TRACK_WIDTH, pos.y * Track.TRACK_HEIGHT)

    val n = (p2.distSqr(p1) / TrackTool.PATH_POSITION_DIST_SQR).toInt
    val dir = (p2 - p1).normalized
    for(i <- 1 to n) {
      val p = p1 + dir * i * TrackTool.PATH_POSITION_DIST
      primaryPathPositions += p
    }
  }

  def trackDrawUpdate(pos1: V2D, pos2: V2D) = {
    val x1 = math.round(pos1.x * Track.TRACK_WIDTH).toInt
    val y1 = math.round(pos1.y * Track.TRACK_HEIGHT).toInt
    val x2 = math.round(pos2.x * Track.TRACK_WIDTH).toInt
    val y2 = math.round(pos2.y * Track.TRACK_HEIGHT).toInt
    if(mousePositionValid && !checkProximity) {

      if(primaryPathPositions.last.distSqr(V2D(x2, y2)) >= TrackTool.PATH_POSITION_DIST_SQR) {
        placePathPoints(pos2)
      }

      trackGraphics.drawLine(x1, y1, x2, y2)
      attemptLoopComplete()
    }
    else {
      abortTrackDraw()
    }
  }

  def abortTrackDraw() = {
    MainApplication.messageBox("Track path has become invalid. Please try again")
    stopTrackDraw()
  }

  def stopTrackDraw() = {
    _drawingTrack = false
    _goalPosition = None
    primaryPathPositions.clear()
    clearArea()
  }

  protected def clearArea() = {
    trackGraphics.setColor(GUIConstants.COLOR_BLANK)
    val comp = trackGraphics.getComposite
    trackGraphics.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR))
    trackGraphics.fillRect(0, 0, Track.TRACK_WIDTH, Track.TRACK_HEIGHT)
    trackGraphics.setComposite(comp)
  }

  def completeTrackDraw() = {
    _drawingTrack = false
    mode = TrackTool.NoSelection
    onTrackCompleted()
  }

  def mousePositionValid = {
    val d = roadWidth / Track.TRACK_WIDTH
    ((mousePosition - V2D(0.5*d, 0.5*d)) higherThan V2D(0, 0)) &&
    ((mousePosition + V2D(0.5*d, 0.5*d)) lowerThan  V2D(1, 1))
  }

  protected def checkProximity = {
    //Do not check proximity for goal
    val pos = V2D(mousePosition.x * Track.TRACK_WIDTH, mousePosition.y * Track.TRACK_HEIGHT)
    val d = roadWidth * roadWidth + 2
    var i = 2
    var tooClose = false
    while(i < primaryPathPositions.length - 4) {

      if(primaryPathPositions(i).distSqr(pos) <= d) {
        tooClose = true
        i = primaryPathPositions.length
      }

      i += 1
    }
    tooClose
  }

  protected def attemptLoopComplete() = {
    if(primaryPathPositions.length > 6) {
      val d = roadWidth * roadWidth + 30
      val p1 = primaryPathPositions.last
      val p2 = primaryPathPositions(0)
      if(p1.distSqr(p2) <= d) {
        val x1 = math.round(p1.x).toInt
        val y1 = math.round(p1.y).toInt
        val x2 = math.round(p2.x).toInt
        val y2 = math.round(p2.y).toInt
        trackGraphics.drawLine(x1, y1, x2, y2)
        primaryPathPositions += p1 + ((p2 - p1) * 0.5)
        completeTrackDraw()
      }
    }
  }

}