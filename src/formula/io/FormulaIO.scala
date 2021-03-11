package formula.io
import java.io._
import javax.imageio._
import java.nio.{ByteBuffer, ByteOrder}
import java.awt.image.BufferedImage
import scala.collection.mutable.HashMap
import Textures.Texture
import Fonts.Font
import formula.application.MainApplication

object FormulaIO {

  //Explicitly use little endian for cross platform compatibility of files
  private val ENDIAN = ByteOrder.LITTLE_ENDIAN
  private val BUFFER_SIZE = 1024
  val STRING_SEP = '\u00b6'.toByte
  class ResourceLoadException(val resourcePath: String) extends Exception(s"No resource at path $resourcePath could be loaded")


  val loadedTextures = HashMap[Texture, BufferedImage]()
  val loadedFonts = HashMap[Font, java.awt.Font]()
  val missingTexture = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB)
  val defaultFont = new javax.swing.JLabel().getFont


  //Using absolute filepaths instead of relative paths gives debugging more clarity
  private val _cwd = java.nio.file.Paths.get("").toAbsolutePath.toString
  def resolvePathS(parts: Seq[String]) = parts.foldLeft(_cwd)(_ + File.separator + _)
  def resolvePath(parts: String*) = parts.foldLeft(_cwd)(_ + File.separator + _)
  def currentDirectory = _cwd

  def loadDouble(buf: Array[Byte], offset: Int) = ByteBuffer.wrap(buf, offset, 8).order(ENDIAN).getDouble
  def saveDouble(d: Double)                     = ByteBuffer.allocate(8).order(ENDIAN).putDouble(d).array

  def loadInt(buf: Array[Byte], offset: Int)    = ByteBuffer.wrap(buf, offset, 4).order(ENDIAN).getInt
  def saveInt(i: Int)                           = ByteBuffer.allocate(4).order(ENDIAN).putInt(i)


  private def readAll(rdr: Reader) = {
    var charsRead = 0
    val data = scala.collection.mutable.ArrayBuffer[Char]()
    val buffer = Array.ofDim[Char](BUFFER_SIZE)
    while({charsRead = rdr.read(buffer); charsRead != -1}) {
      data.appendAll(buffer)
    }
    data.take(charsRead).toArray.map(_.toByte)
  }

  def saveSettings(settings: Settings) = {
    var wtr: Option[BufferedWriter] = None

    try {
      wtr = Some(new BufferedWriter(new FileWriter(resolvePath("data", "settings.dat"))))
      wtr.get.write(Settings.save(settings).map(_.toChar))
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



  def listTracks = {
    val trackDir = new File(resolvePath("data", "tracks"))
    trackDir.listFiles(new FilenameFilter {
      override def accept(dir: File, name: String) = name.endsWith(".trck")
    }).map(_.getName).toVector
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


}