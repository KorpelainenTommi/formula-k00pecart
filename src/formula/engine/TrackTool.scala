package formula.engine
import java.awt._
import formula.application._
import java.awt.image.BufferedImage
import scala.collection.mutable.ArrayBuffer

object TrackTool {

  sealed trait Mode
  final object NoSelection extends TrackTool.Mode
  final object DrawRoad extends TrackTool.Mode
  final object PlaceObjects extends TrackTool.Mode
  final object PlaceGoal extends TrackTool.Mode

  //Constants for placing path checkpoints and mapobjects
  val PATH_POSITION_DIST = 10
  val PATH_POSITION_DIST_SQR = PATH_POSITION_DIST * PATH_POSITION_DIST
  val MAP_OBJECT_SELECTION_RADIUS = 5D

}

class TrackTool {

  //Track road graphics
  val trackImage = new BufferedImage(Track.TRACK_WIDTH, Track.TRACK_HEIGHT, BufferedImage.TYPE_INT_ARGB)
  protected val trackGraphics = trackImage.createGraphics()


  var selectedMapObject = -1



  protected var _mousePosition = V2D(-1, -1)
  def mousePosition = _mousePosition
  def mousePosition_=(value: V2D) = {
    val lastPos = _mousePosition
    _mousePosition = value

    mode match {
      case TrackTool.DrawRoad if drawingTrack => trackDrawUpdate(lastPos, _mousePosition)
      case _ =>
    }
  }

  protected var _roadWidth = 25D
  def roadWidth = _roadWidth
  def roadWidth_=(value: Double) = {
    if(!roadCompleted) {
      _roadWidth = value
    }
  }

  protected var _mode: TrackTool.Mode = TrackTool.NoSelection
  def mode = _mode
  def mode_=(value: TrackTool.Mode) = _mode = value

  protected var _drawingTrack = false
  def drawingTrack = _drawingTrack


  protected val _mapObjects = ArrayBuffer[MapObject]()
  def mapObjects = _mapObjects

  def goalPosition = if(pathPositions.isEmpty) None else Some(pathPositions(0))

  protected val _primaryPathPositions = ArrayBuffer[V2D]()
  def pathPositions = _primaryPathPositions.toVector
  protected var _roadCompleted = false
  def roadCompleted = _roadCompleted

  var onTrackCompleted: () => Unit = () => {}


  //Placing and removing mapobjects

  def placeObject() = {
    if(selectedMapObject != -1) {
      if(mousePosition.higherThan(V2D(0, 0)) && mousePosition.lowerThan(V2D(1, 1))) {

        val pos = V2D(mousePosition.x * Track.TRACK_WIDTH, mousePosition.y * Track.TRACK_HEIGHT)
        mapObjects += MapObjects.createMapObject(selectedMapObject, pos)

      }
    }
  }

  def removeObject() = {
    if(mapObjects.nonEmpty) {

      val pos = V2D(mousePosition.x * Track.TRACK_WIDTH, mousePosition.y * Track.TRACK_HEIGHT)
      val i = V2D.locateIndex(pos, mapObjects.map(_.position).toVector)

      if((pos dist mapObjects(i).position) <= TrackTool.MAP_OBJECT_SELECTION_RADIUS) {
        mapObjects.remove(i)
      }

    }
  }


  def placeGoal() = {
    if(pathPositions.nonEmpty) {

      val i = V2D.locateIndex(V2D(mousePosition.x * Track.TRACK_WIDTH, mousePosition.y * Track.TRACK_HEIGHT), pathPositions)
      val positions = pathPositions.takeRight(pathPositions.length - i) ++ pathPositions.take(i)
      _primaryPathPositions.clear()
      _primaryPathPositions.appendAll(positions)

    }
  }


  def reverseTrack() = {

    if(pathPositions.nonEmpty) {
      val goal = _primaryPathPositions.head
      val rev = _primaryPathPositions.tail.reverse
      _primaryPathPositions.clear()
      _primaryPathPositions.append(goal)
      _primaryPathPositions.appendAll(rev)
    }
  }

  //Start drawing the track in road draw mode
  def beginTrackDraw() = {
    if(mousePositionValid) {

      if(roadCompleted) {
        if(MainApplication.confirmBox("This will clear the previous track and all map objects. Continue?")) {
          _roadCompleted = false
          _primaryPathPositions.clear()
          clearArea()
        }
      }

      else {
        _roadCompleted = false
        _primaryPathPositions.clear()
        clearArea()
        trackGraphics.setColor(Color.BLACK)
        trackGraphics.setStroke(new BasicStroke(roadWidth.toFloat, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND))
        _drawingTrack = true
        val gX = math.round(mousePosition.x * Track.TRACK_WIDTH).toInt
        val gY = math.round(mousePosition.y * Track.TRACK_HEIGHT).toInt
        _primaryPathPositions += V2D(gX, gY)
      }
    }
  }

  //Place checkpoints based on mouse movement
  protected def placePathPoints(pos: V2D) = {

    val p1 = _primaryPathPositions.last
    val p2 = V2D(pos.x * Track.TRACK_WIDTH, pos.y * Track.TRACK_HEIGHT)

    val n = (p2.distSqr(p1) / TrackTool.PATH_POSITION_DIST_SQR).toInt
    val dir = (p2 - p1).normalized
    for(i <- 1 to n) {
      val p = p1 + (dir * i * TrackTool.PATH_POSITION_DIST)
      _primaryPathPositions += p
    }
  }

  //Mouse movement causes this update function to be called
  def trackDrawUpdate(pos1: V2D, pos2: V2D) = {
    val x1 = math.round(pos1.x * Track.TRACK_WIDTH).toInt
    val y1 = math.round(pos1.y * Track.TRACK_HEIGHT).toInt
    val x2 = math.round(pos2.x * Track.TRACK_WIDTH).toInt
    val y2 = math.round(pos2.y * Track.TRACK_HEIGHT).toInt
    if(mousePositionValid && !checkProximity) {

      if(_primaryPathPositions.last.distSqr(V2D(x2, y2)) >= TrackTool.PATH_POSITION_DIST_SQR) {
        placePathPoints(pos2)
      }

      trackGraphics.drawLine(x1, y1, x2, y2)
      attemptLoopComplete()
    }
    else {
      abortTrackDraw()
    }
  }

  //Track drawing was forcefully stopped
  def abortTrackDraw() = {
    MainApplication.messageBox("Track path has become invalid. Please try again")
    stopTrackDraw()
  }


  def stopTrackDraw() = {
    _drawingTrack = false
    _primaryPathPositions.clear()
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
    _roadCompleted = true
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
    while(i < _primaryPathPositions.length - 4) {

      if(_primaryPathPositions(i).distSqr(pos) <= d) {
        tooClose = true
        i = _primaryPathPositions.length
      }

      i += 1
    }
    tooClose
  }

  protected def attemptLoopComplete() = {
    if(_primaryPathPositions.length > 6) {
      val d = roadWidth * roadWidth + 30
      val p1 = _primaryPathPositions.last
      val p2 = _primaryPathPositions(0)
      if(p1.distSqr(p2) <= d) {
        val x1 = math.round(p1.x).toInt
        val y1 = math.round(p1.y).toInt
        val x2 = math.round(p2.x).toInt
        val y2 = math.round(p2.y).toInt
        trackGraphics.drawLine(x1, y1, x2, y2)
        _primaryPathPositions += p1 + ((p2 - p1) * 0.5)
        completeTrackDraw()
      }
    }
  }


  def exit() = {
    //Release resources
    trackGraphics.dispose()
  }

}