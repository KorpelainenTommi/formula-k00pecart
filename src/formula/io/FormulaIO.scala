package formula.io
import java.io._
import Fonts.Font
import javax.imageio._
import formula.engine._
import Textures.Texture
import java.awt.image.BufferedImage
import java.nio.{ByteBuffer, ByteOrder}
import scala.collection.mutable.HashMap
import formula.application.MainApplication

/** Main object for the game's IO system. Wraps up common serialization/deserialization
 * for primitive types like Int, Double, and String, abstracts loading tracks, and resources
 * parses paths.
 */
object FormulaIO {

  class ResourceLoadException(val resourcePath: String)
    extends Exception(s"No resource at path $resourcePath could be loaded")


  //Using absolute filepaths instead of relative paths can give exceptions more clarity
  //Current working directory
  private val _cwd = java.nio.file.Paths.get("").toAbsolutePath.toString


  //Explicitly use little endian for cross platform compatibility of files
  private val ENDIAN   = ByteOrder.LITTLE_ENDIAN
  private val ENCODING = java.nio.charset.StandardCharsets.UTF_16LE
  private val BUFFER_SIZE = 1024


  //Separate strings with a special unicode character when serializing
  val STRING_SEP_CHAR  = '\u00b6'
  val STRING_SEP_BYTES = "\u00b6".getBytes(ENCODING)


  val loadedFonts    = HashMap[Font, java.awt.Font]()
  val loadedTextures = HashMap[Texture, BufferedImage]()


  val defaultFont = new javax.swing.JLabel().getFont
  val defaultTexture = {
    val img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)
    val g = img.createGraphics()
    g.setColor(java.awt.Color.MAGENTA)
    g.fillRect(0,0,1,1)
    g.dispose()
    img
  }




  /* resolvePath with variable length arguments is nice, but has downsides for overloading:
  *  1. In order to be able to pass a Seq as an argument, the parameter type needs to be Seq,
  *  but Seq[String] and String* have the same type after type erasure, so these method signatures are
  *  identical after type erasure.
  *
  *  2. The method cannot have parameters with default values, so resolveName cannot be an overload either.
  *  Aww man...
  * */
  def resolvePathS(parts: Seq[String]) = parts.foldLeft(_cwd)(_ + File.separator + _)
  def resolveNameS(parts: Seq[String]) = parts.foldLeft("")(_ + File.separator + _)
  def resolvePath(parts: String*)      = parts.foldLeft(_cwd)(_ + File.separator + _)
  def resolveName(parts: String*)      = parts.foldLeft("")(_ + File.separator + _)
  def currentDirectory = _cwd


  //Generic serialization/deserializtion of double, integer and string types

  def saveDouble(d: Double)                     = ByteBuffer.allocate(8).order(ENDIAN).putDouble(d).array
  def loadDouble(buf: Array[Byte], offset: Int) = ByteBuffer.wrap(buf, offset, 8).order(ENDIAN).getDouble

  def saveInt(i: Int)                           = ByteBuffer.allocate(4).order(ENDIAN).putInt(i).array
  def loadInt(buf: Array[Byte], offset: Int)    = ByteBuffer.wrap(buf, offset, 4).order(ENDIAN).getInt

  def saveString(s: String) = (s.filter(_ != STRING_SEP_CHAR) + STRING_SEP_CHAR).getBytes(ENCODING)
  def loadString(buf: Array[Byte], offset: Int) = {

    //Step through memory until bytes corresponding to a STRING_SEP_CHAR are encountered
    var idx = offset
    while(idx+1 < buf.length && (buf(idx) != STRING_SEP_BYTES(0) || buf(idx+1) != STRING_SEP_BYTES(1))) {
      idx += 2
    }
    def bytesToChar(bytes: Array[Byte]) = new String(bytes, ENCODING).toCharArray
    (bytesToChar(buf.slice(offset, idx)).mkString, (idx-offset) + 2)

  }

  private def readAll(in: FileInputStream) = {

    val data = scala.collection.mutable.ArrayBuffer[Byte]()
    val buffer = Array.ofDim[Byte](BUFFER_SIZE)
    var len = 0

    while({len = in.read(buffer); data.appendAll(buffer.take(len)); len != -1}) {}
    data.toArray

  }


  private def saveFile(bytes: Array[Byte], fullpath: String) = {

    var outStream: Option[FileOutputStream] = None
    try {
      outStream = Some(new FileOutputStream(new File(fullpath), false)) //append = false
      outStream.get.write(bytes)
      true
    }
    catch {
      case _: FileNotFoundException | _: IOException => false
    }
    finally {
      outStream.foreach(_.close())
    }

  }

  private def loadFile(fullpath: String) = {

    var inStream: Option[FileInputStream] = None
    try {
      inStream = Some(new FileInputStream(new File(fullpath)))
      Some(readAll(inStream.get))
    }
    catch {
      case _: FileNotFoundException | _: IOException => None
    }
    finally {
      inStream.foreach(_.close())
    }

  }

  private def deleteFile(fullpath: String) = {

    val f = new File(fullpath)
    if(!f.exists()) false
    else {
      try {
        f.delete()
      }
      catch {
        case _: IOException | _: java.lang.SecurityException => false
      }
    }

  }




  def saveSettings(settings: Settings) = {
    saveFile(Settings.save(settings), resolvePath("data", "settings.dat"))
  }

  def loadSettings = {
    loadFile(resolvePath("data", "settings.dat")) match {
      case Some(data) => Settings.load(data)
      case None => Settings.defaultSettings
    }
  }




  //Load an image, but don't cache it
  def loadImage(filename: String) = {

    val path = resolvePath("data", "textures", filename)
    try {
      ImageIO.read(new File(path))
    }
    catch {
      case _: IOException => throw new ResourceLoadException(path)
    }

  }

  def loadFont(filename: String) = {

    val path = resolvePath("data", "fonts", filename)
    try {
      java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT, new File(path))
    }
    catch {
      case _: IOException | _: java.awt.FontFormatException => throw new ResourceLoadException(path)
    }

  }




  //Load a texture, and cache it for future access
  def getTexture(t: Texture) = {

    if(!loadedTextures.contains(t)) {
      try {
        loadedTextures(t) = loadImage(Textures.path(t))
      }

      catch {
        case e: ResourceLoadException => {
          loadedTextures(t) = defaultTexture
          MainApplication.messageBox(e.getMessage)
        }
      }

    }

    loadedTextures(t)
  }

  def unloadTexture(t: Texture) = {
    loadedTextures.remove(t)
  }

  def unloadAllTextures() = {
    loadedTextures.clear()
  }




  def getFont(f: Font) = {

    if(!loadedFonts.contains(f)) {
      try {
        loadedFonts(f) = loadFont(Fonts.path(f))
      }
      catch {
        case e: ResourceLoadException => {
          loadedFonts(f) = defaultFont
          MainApplication.messageBox(e.getMessage)
        }
      }
    }

    loadedFonts(f)
  }




  def listTrackFiles = {
    val trackDir = new File(resolvePath("data", "tracks"))
    trackDir.listFiles(new FilenameFilter {
      override def accept(dir: File, name: String) = name.endsWith(".trck")
    }).map(_.getName).toVector
  }

  def loadTrackPreview(filename: String) = {
    loadFile(resolvePath("data", "tracks", filename)).map(TrackPreview.load)
  }

  def loadTrack(filename: String) = {
    loadFile(resolvePath("data", "tracks", filename)).map(Track.load)
  }

  def saveTrack(track: Track) = {
    saveFile(Track.save(track), resolvePath("data", "tracks", (track.trackName+".trck")))
  }

  def deleteTrack(filename: String) = {
    deleteFile(resolvePath("data", "tracks", filename))
  }




  def loadDemoBitSet(name: String) = {
    val demoimg = FormulaIO.loadImage(name)
    val raster = demoimg.getRaster
    val buffer = raster.getDataElements(0, 0, 256, 256, null).asInstanceOf[Array[Byte]]
    val bitSet = scala.collection.mutable.BitSet()
    for(i <- 0 until (256*256)) {
      if(buffer(i*4) == 0x0) bitSet.add(i)
    }
    bitSet
  }

  def loadDemoPath(name: String) = {
    val demoimg = FormulaIO.loadImage(name)
    val raster = demoimg.getRaster
    val buffer = raster.getDataElements(0, 0, 256, 256, null).asInstanceOf[Array[Byte]]

    val redDots = scala.collection.mutable.ArrayBuffer[V2D]()
    val blueDots = scala.collection.mutable.ArrayBuffer[V2D]()
    val greenDots = scala.collection.mutable.ArrayBuffer[V2D]()
    var whiteDot = formula.engine.V2D(0,0)

    val FULL_BITS: Byte = -1
    val EMPTY_BITS: Byte = 0

    var r: Byte = 0
    var g: Byte = 0
    var b: Byte = 0

    for(i <- 0 until (256*256)) {
      r = buffer(i*4)
      g = buffer(i*4+1)
      b = buffer(i*4+2)

      if(r == FULL_BITS && g == FULL_BITS && b == FULL_BITS) {
        whiteDot = V2D(i % Track.TRACK_WIDTH, i / Track.TRACK_HEIGHT)
      }

      else if(r == FULL_BITS) {
        redDots += V2D(i % Track.TRACK_WIDTH, i / Track.TRACK_HEIGHT)
      }

      else if(g == FULL_BITS) {
        greenDots += V2D(i % Track.TRACK_WIDTH, i / Track.TRACK_HEIGHT)
      }

      else if(b == FULL_BITS) {
        blueDots += V2D(i % Track.TRACK_WIDTH, i / Track.TRACK_HEIGHT)
      }
    }

    val points = Array.ofDim[formula.engine.V2D](redDots.length*3+1)
    points(0) = whiteDot
    var lastPoint = whiteDot
    for(i <- redDots.indices) {
      points(i*3+1) = V2D.locate(lastPoint, redDots.toVector)
      lastPoint = points(i*3+1)
      points(i*3+2) = V2D.locate(lastPoint, greenDots.toVector)
      lastPoint = points(i*3+2)
      points(i*3+3) = V2D.locate(lastPoint, blueDots.toVector)
      lastPoint = points(i*3+3)
    }

    new ClosedLoop(points)

  }

}