package formula.io
import java.io._
import javax.imageio._
import java.nio.{ByteBuffer, ByteOrder}
import java.awt.image.BufferedImage
import scala.collection.mutable.HashMap
import Textures.Texture
import Fonts.Font
import formula.engine.TrackPreview
import formula.application.MainApplication

object FormulaIO {

  //Explicitly use little endian for cross platform compatibility of files
  private val ENCODING = java.nio.charset.StandardCharsets.UTF_16LE
  private val ENDIAN = ByteOrder.LITTLE_ENDIAN
  private val BUFFER_SIZE = 1024
  val STRING_SEP_CHAR = '\u00b6'
  val STRING_SEP_BYTES = "\u00b6".getBytes(ENCODING)
  class ResourceLoadException(val resourcePath: String) extends Exception(s"No resource at path $resourcePath could be loaded")


  val loadedTextures = HashMap[Texture, BufferedImage]()
  val loadedFonts = HashMap[Font, java.awt.Font]()
  val missingTexture = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)
  val defaultFont = new javax.swing.JLabel().getFont


  //Using absolute filepaths instead of relative paths gives debugging more clarity
  private val _cwd = java.nio.file.Paths.get("").toAbsolutePath.toString
  def resolvePathS(parts: Seq[String]) = parts.foldLeft(_cwd)(_ + File.separator + _)
  def resolvePath(parts: String*) = parts.foldLeft(_cwd)(_ + File.separator + _)
  def currentDirectory = _cwd

  def saveDouble(d: Double)                     = ByteBuffer.allocate(8).order(ENDIAN).putDouble(d).array
  def loadDouble(buf: Array[Byte], offset: Int) = ByteBuffer.wrap(buf, offset, 8).order(ENDIAN).getDouble

  def saveInt(i: Int)                           = ByteBuffer.allocate(4).order(ENDIAN).putInt(i).array
  def loadInt(buf: Array[Byte], offset: Int)    = ByteBuffer.wrap(buf, offset, 4).order(ENDIAN).getInt

  def saveString(s: String) = (s.filter(_ != STRING_SEP_CHAR) + STRING_SEP_CHAR).getBytes(ENCODING)
  def loadString(buf: Array[Byte], offset: Int) = {
    var idx = offset
    while(idx+1 < buf.size && (buf(idx) != STRING_SEP_BYTES(0) || buf(idx+1) != STRING_SEP_BYTES(1))) {
      idx += 2
    }
    (bytesToChar(buf.slice(offset, idx)).mkString, (idx-offset) + 2)
  }


  private def readAll(rdr: Reader) = {
    var total = 0
    var charsRead = 0
    val data = scala.collection.mutable.ArrayBuffer[Char]()
    val buffer = Array.ofDim[Char](BUFFER_SIZE)
    while({charsRead = rdr.read(buffer); charsRead != -1}) {
      data.appendAll(buffer)
      total += charsRead
    }
    data.take(total).mkString.getBytes(ENCODING)
  }

  def bytesToChar(bytes: Array[Byte]) = {
    //Since a char is 2 bytes wide, we want an even number of bytes
    val data: Array[Byte] = if(bytes.size % 2 == 0) bytes else bytes :+ 0
    new String(data, ENCODING).toCharArray
  }

  def saveSettings(settings: Settings) = {
    var wtr: Option[BufferedWriter] = None

    try {
      wtr = Some(new BufferedWriter(new FileWriter(resolvePath("data", "settings.dat"))))
      wtr.get.write(bytesToChar(Settings.save(settings)))
      true
    }

    catch {
      case _: FileNotFoundException | _: IOException => false
    }

    finally {
      wtr.foreach(_.close())
    }
  }


  def loadSettings = {
    var rdr: Option[BufferedReader] = None

    try {
      rdr = Some(new BufferedReader(new FileReader(resolvePath("data", "settings.dat"))))
      Settings.load(readAll(rdr.get))
    }

    catch {
      //Initialize with default settings
      case _: FileNotFoundException | _: IOException => Settings.defaultSettings
    }

    finally {
      rdr.foreach(_.close())
    }
  }


  //Load an image, but don't cache it
  def loadImage(name: String) = {
    val path = resolvePath("data", "textures", name)
    try {
      ImageIO.read(new File(path))
    }

    catch {
      case _: IOException => throw new ResourceLoadException(path)
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
          loadedTextures(t) = missingTexture
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
        loadedFonts(f) = java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT, new File(resolvePath("data", "fonts", Fonts.path(f))))
      }
      catch {
        case _: IOException | _: java.awt.FontFormatException => {
          loadedFonts(f) = defaultFont
          MainApplication.messageBox(new ResourceLoadException(Fonts.path(f)).getMessage)
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

  def loadTrackPreview(name: String) = {
    var rdr: Option[BufferedReader] = None

    try {
      rdr = Some(new BufferedReader(new FileReader(resolvePath("data", "tracks", name))))
      Some(TrackPreview.load(readAll(rdr.get)))
    }

    catch {
      //Loading track preview failed
      case _: FileNotFoundException | _: IOException => None
    }

    finally {
      rdr.foreach(_.close())
    }
  }

  def saveTrackPreview(track: TrackPreview) = {
    var wtr: Option[BufferedWriter] = None

    try {
      wtr = Some(new BufferedWriter(new FileWriter(resolvePath("data", "tracks", (track.trackName+".trck")))))
      wtr.get.write(bytesToChar(TrackPreview.save(track)))
      true
    }

    catch {
      case _: FileNotFoundException | _: IOException => false
    }

    finally {
      wtr.foreach(_.close())
    }
  }


  //FOR DEBUGGING
  def readFile(pathParts: String*) = {
    var rdr: Option[BufferedReader] = None

    try {
      rdr = Some(new BufferedReader(new FileReader(resolvePathS(pathParts))))
      readAll(rdr.get)
    }

    catch {
      //Initialize with default settings
      case _: FileNotFoundException | _: IOException => Array[Char]()
    }

    finally {
      rdr.foreach(_.close())
    }
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


}