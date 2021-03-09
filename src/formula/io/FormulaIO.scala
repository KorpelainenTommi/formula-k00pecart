package formula.io
import java.io._
import javax.imageio._
import java.awt.image.BufferedImage
import scala.collection.mutable.HashMap
import Textures.Texture

object FormulaIO {

  private val BUFFER_SIZE = 1024
  class ResourceLoadException(val resourcePath: String) extends Exception(s"No resource at path $resourcePath could be loaded")


  val loadedTextures = HashMap[Texture, BufferedImage]()


  //Using absolute filepaths instead of relative paths gives debugging more clarity
  private val _cwd = java.nio.file.Paths.get("").toAbsolutePath.toString
  def resolvePathS(parts: Seq[String]) = parts.foldLeft(_cwd)(_ + File.separator + _)
  def resolvePath(parts: String*) = parts.foldLeft(_cwd)(_ + File.separator + _)
  def currentDirectory = _cwd


  private def readAll(rdr: Reader) = {
    val data = scala.collection.mutable.ArrayBuffer[Char]()
    val buffer = Array.ofDim[Char](BUFFER_SIZE)
    while(rdr.read(buffer) != -1) {
      data.appendAll(buffer)
    }
    data.toArray
  }

  def saveSettings(settings: Settings) = {
    var wtr: Option[BufferedWriter] = None

    try {
      wtr = Some(new BufferedWriter(new FileWriter(resolvePath("data", "settings.dat"))))
      wtr.get.write(Settings.save(settings).toArray)
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

  //When loading images, the caller is responsible for handling missing files and other exceptions

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
      loadedTextures(t) = loadImage(Textures.path(t))
    }
    loadedTextures(t)
  }

  def unloadTexture(t: Texture) = {
    loadedTextures.remove(t)
  }

  def unloadAllTextures() = {
    loadedTextures.clear()
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