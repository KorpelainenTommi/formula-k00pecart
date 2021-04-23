package formula.engine
import scala.collection.mutable.BitSet
import java.awt.image.BufferedImage
import formula.io._

object TrackPreview extends Serializer[TrackPreview] {
  override def load(bytes: Array[Byte], start: Int): TrackPreview = {
    Track.loadHeader(bytes, start)._1
  }

  override def save(saveable: TrackPreview): Array[Byte] = {
    val fastestTimesArray = scala.collection.mutable.ArrayBuffer[Byte]()
    saveable.fastestTimes.foreach(time => {
      fastestTimesArray.appendAll(FormulaIO.saveInt(time._1))
      fastestTimesArray.appendAll(FormulaIO.saveString(time._2))
    })

    Array[Byte](saveable.version) ++
    FormulaIO.saveString(saveable.trackName) ++
    FormulaIO.saveString(saveable.creator) ++
    FormulaIO.saveString(saveable.description) ++
    Array[Byte](saveable.fastestTimes.length.toByte) ++
    fastestTimesArray.toArray
  }
}

class TrackPreview(val trackName: String, val description: String = "", val creator: String = "Unknown", val version: Byte = 0) {
  protected var _roadBytes: Option[Array[Byte]] = None
  protected var _previewImage: Option[BufferedImage]    = None
  def previewImage = _previewImage.getOrElse(FormulaIO.missingTexture)

  protected var _fastestTimes: Vector[(Int, String)] = Vector()
  def fastestTimes = _fastestTimes
  def updateFastestTimes(time: Int, name: String) = {
    val times = fastestTimes :+ (time, name)
    _fastestTimes = times.sortBy(_._1).take(Track.MAX_LEADERBOARD)
  }

  protected def createPreviewImage() = {
    _roadBytes.foreach(imageBytes => {
      _previewImage = Some(new BufferedImage(Track.TRACK_WIDTH, Track.TRACK_HEIGHT, BufferedImage.TYPE_INT_ARGB))
      val raster = _previewImage.get.getRaster
      val pixels = Array.ofDim[Int](Track.TRACK_WIDTH * Track.TRACK_HEIGHT)
      var idx = 0
      var b: Byte = 0
      while(idx < imageBytes.length) {
        b = imageBytes(idx)
        pixels(idx*8+0) = if((b & 0x01) != 0) 0xff404040 else 0xff267f00
        pixels(idx*8+1) = if((b & 0x02) != 0) 0xff404040 else 0xff267f00
        pixels(idx*8+2) = if((b & 0x04) != 0) 0xff404040 else 0xff267f00
        pixels(idx*8+3) = if((b & 0x08) != 0) 0xff404040 else 0xff267f00
        pixels(idx*8+4) = if((b & 0x10) != 0) 0xff404040 else 0xff267f00
        pixels(idx*8+5) = if((b & 0x20) != 0) 0xff404040 else 0xff267f00
        pixels(idx*8+6) = if((b & 0x40) != 0) 0xff404040 else 0xff267f00
        pixels(idx*8+7) = if((b & 0x80) != 0) 0xff404040 else 0xff267f00
        idx += 1
      }

      //Set imagedata directly
      raster.setDataElements(0, 0, Track.TRACK_WIDTH, Track.TRACK_HEIGHT, pixels)
    })
  }
}

object Track extends Serializer[Track] {

  def describeTrackTime(time: (Int, String)) = {

    var t = time._1
    val hours = t / 3600000
    t -= hours * 3600000
    val minutes = t / 60000
    t -= minutes * 60000
    val seconds = t / 1000
    val centi = (t - seconds * 1000) / 10

    val minuteString = if(minutes > 9) minutes.toString else "0"+minutes
    val secondString = if(seconds > 9) seconds.toString else "0"+seconds
    val centiString  = if(centi > 9) centi.toString else "0"+centi

    if(hours > 9 || time._1 < 0) s"-:- ${time._2}"
    else if(hours > 0) s"$hours:$minuteString:$secondString.$centiString ${time._2}"
    else s"$minutes:$secondString.$centiString ${time._2}"
  }

  val MAX_LEADERBOARD = 6
  val TRACK_WIDTH = 256
  val TRACK_HEIGHT = 256
  val NAME_MAX_LENGTH = 12
  val CREATOR_MAX_LENGTH = 11

  def loadHeader(bytes: Array[Byte], start: Int): (Track, Int) = {

    var idx = start
    val version = bytes(idx)
    idx += 1
    val nameData = FormulaIO.loadString(bytes, idx)
    idx += nameData._2
    val creatorData = FormulaIO.loadString(bytes, idx)
    idx += creatorData._2
    val descriptionData = FormulaIO.loadString(bytes, idx)
    idx += descriptionData._2
    val fastestTimesSize = bytes(idx)
    idx += 1
    val fastestTimes = Array.fill(fastestTimesSize)({
      val t = FormulaIO.loadInt(bytes, idx)
      idx += 4
      val descData = FormulaIO.loadString(bytes, idx)
      idx += descData._2
      (t, descData._1)
    })
    val trc = new Track(nameData._1, descriptionData._1, creatorData._1, version = version)
    trc._fastestTimes = fastestTimes.toVector
    trc._roadBytes = Some(bytes.slice(idx, idx+Track.TRACK_WIDTH*Track.TRACK_HEIGHT/8))
    idx += Track.TRACK_WIDTH*TRACK_HEIGHT/8
    trc.createPreviewImage()
    (trc, idx)
  }

  override def load(bytes: Array[Byte], start: Int): Track = {
    val trackHeader = loadHeader(bytes, start)
    val track = trackHeader._1
    var idx = trackHeader._2
    track.initializeRoad()
    track._roadWidth = FormulaIO.loadDouble(bytes, idx)
    idx += 8
    track._primaryPath = ClosedPath.load(bytes, idx).toClosedLoop
    idx += track.primaryPath.length * 16 + 4
    track
  }

  override def save(saveable: Track): Array[Byte] = {

    val headerBytes = TrackPreview.save(saveable)
    saveable.serializeRoad()
    val roadBytes = saveable._roadBytes.get
    headerBytes ++
    roadBytes ++
    FormulaIO.saveDouble(saveable.roadWidth) ++
    ClosedPath.save(saveable.primaryPath)
  }
}


class Track(trackName: String, description: String = "", creator: String = "Unknown", protected var _roadWidth: Double = 25D, version: Byte = 0) extends TrackPreview(trackName, description, creator, version) {

  private var _primaryPath = new ClosedLoop(Vector())
  private var _road = BitSet()


  def road(point: V2D): Boolean = {
    road(math.round(point.x).toInt, math.round(point.y).toInt)
  }

  def road(x: Int, y: Int): Boolean = {
    _road(x+y*Track.TRACK_WIDTH)
  }

  def primaryPath = _primaryPath
  def roadWidth = _roadWidth

  def rewriteRoad(roadPixels: BufferedImage, roadPath: ClosedLoop) = {

    _primaryPath = roadPath
    _road.clear()

    val data = roadPixels.getRaster.getDataElements(0, 0, Track.TRACK_WIDTH, Track.TRACK_HEIGHT, null).asInstanceOf[Array[Int]]
    for(i <- 0 until (Track.TRACK_WIDTH*Track.TRACK_HEIGHT)) {
      if(data(i) == 0xFF000000) _road.add(i)
    }
  }


  protected def initializeRoad() = {

    _roadBytes.foreach(bytes => {
      var idx = 0
      var b: Byte = 0x0
      while(idx < bytes.length) {
        b = bytes(idx)
        if((b & 0x01) != 0) _road += (idx*8+0)
        if((b & 0x02) != 0) _road += (idx*8+1)
        if((b & 0x04) != 0) _road += (idx*8+2)
        if((b & 0x08) != 0) _road += (idx*8+3)
        if((b & 0x10) != 0) _road += (idx*8+4)
        if((b & 0x20) != 0) _road += (idx*8+5)
        if((b & 0x40) != 0) _road += (idx*8+6)
        if((b & 0x80) != 0) _road += (idx*8+7)
        idx += 1
      }
    })

  }

  protected def serializeRoad() = {

    _roadBytes = Some(Array.tabulate[Byte](Track.TRACK_WIDTH*Track.TRACK_HEIGHT/8)(idx => {
      var b = 0x0
      if(_road(idx*8+0)) b |= 0x01
      if(_road(idx*8+1)) b |= 0x02
      if(_road(idx*8+2)) b |= 0x04
      if(_road(idx*8+3)) b |= 0x08
      if(_road(idx*8+4)) b |= 0x10
      if(_road(idx*8+5)) b |= 0x20
      if(_road(idx*8+6)) b |= 0x40
      if(_road(idx*8+7)) b |= 0x80
      b.toByte
    }))

  }
}