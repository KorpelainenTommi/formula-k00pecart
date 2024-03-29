package formula.io
import java.io._
import Fonts.Font
import Sounds.Sound
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


  //Hashmaps for caching resources
  val loadedFonts    = HashMap[Font, java.awt.Font]()
  val loadedTextures = HashMap[Texture, BufferedImage]()
  val loadedSounds = HashMap[Sound, Option[File]]()


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


  //In JDK 11+ FileInputStream has a method called readAllBytes
  //This method is used instead since it makes the application compatible
  //with older java versions
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

  def loadSound(filename: String) = {

    val path = resolvePath("data", "sounds", filename)
    try {
      new File(path)
    }
    catch {
      case _: IOException => throw new ResourceLoadException(path)
    }

  }




  //Loading music works with generic filenames
  def getMusic(filename: String) = {

    val path = resolvePath("data", "music", filename)
    try {
      Some(new File(path))
    }
    catch {
      case _: IOException => None
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


  def getSound(s: Sound) = {

    if(!loadedSounds.contains(s)) {
      try {
        loadedSounds(s) = Some(loadSound(Sounds.path(s)))
      }
      catch {
        case e: ResourceLoadException => {
          loadedSounds(s) = None
          MainApplication.messageBox(e.getMessage)
        }
      }
    }

    loadedSounds(s)
  }


  def unloadAllSounds() = {

    SoundSystem.stopAllSounds()
    SoundSystem.clearSoundSources()
    loadedSounds.clear()

  }





  def listWavFiles = {
    val musicDir = new File(resolvePath("data", "music"))
    if(musicDir.exists()) {
      musicDir.listFiles(new FilenameFilter {
        override def accept(dir: File, name: String) = name.endsWith(".wav")
      }).map(_.getName).toVector
    }
    else Vector()
  }

  def listTrackFiles = {
    val trackDir = new File(resolvePath("data", "tracks"))
    if(trackDir.exists()) {
      trackDir.listFiles(new FilenameFilter {
        override def accept(dir: File, name: String) = name.endsWith(".trck")
      }).map(_.getName).toVector
    }
    else Vector()
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

}