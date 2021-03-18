package formula.engine
import java.awt.image.BufferedImage
import formula.io._

object TrackPreview extends Serializable[TrackPreview] {
  override def load(bytes: Array[Byte], start: Int, count: Int): TrackPreview = {
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
    val trc = new TrackPreview(nameData._1, descriptionData._1, creatorData._1, version)
    trc._fastestTimes = fastestTimes.toVector
    trc
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
    Array[Byte](saveable.fastestTimes.size.toByte) ++
    fastestTimesArray.toArray
  }
}

class TrackPreview(val trackName: String, val description: String = "", val creator: String = "Unknown", val version: Byte = 0) {
  private var _previewImage: BufferedImage = null
  def previewImage = _previewImage

  private var _fastestTimes: Vector[(Int, String)] = Vector()
  def fastestTimes = _fastestTimes
  def updateFastestTimes(entry: (Int, String)) = {
    val times = fastestTimes :+ entry
    _fastestTimes = times.sortBy(_._1).take(Track.MAX_LEADERBOARD)
  }

  protected def createPreviewImage(imageBytes: Array[Byte]) = {
    _previewImage = new BufferedImage(Track.TRACK_WIDTH, Track.TRACK_HEIGHT, BufferedImage.TYPE_INT_ARGB)
    val raster = _previewImage.getRaster
    val pixels = Array.ofDim[Int](Track.TRACK_WIDTH * Track.TRACK_HEIGHT)
    var idx = 0
    var b: Byte = 0
    while(idx < imageBytes.size) {
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
  }
}

object Track extends Serializable[Track] {
  val MAX_LEADERBOARD = 6
  val TRACK_WIDTH = 256
  val TRACK_HEIGHT = 256
  override def load(bytes: Array[Byte], start: Int, count: Int): Track = ???
  override def save(saveable: Track): Array[Byte] = ???
}


class Track(trackName: String, description: String = "", creator: String = "Unknown", version: Byte = 0) extends TrackPreview(trackName, description, creator, version) {

}