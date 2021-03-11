package formula.io
import formula.engine.V2D

trait Serializable[T] {
  def save(saveable: T): Array[Byte]
  def load(bytes: Array[Byte], start: Int, count: Int): T
  def load(bytes: Array[Byte]): T = load(bytes, 0, bytes.size)
}

case class Settings(screenSize: V2D, fullScreen: Boolean)

object Settings extends Serializable[Settings] {

  //Default 16:9 resolutions
  val resolutions = Vector(
    V2D(1024, 576),
    V2D(1280, 720),
    V2D(1366, 768),
    V2D(1600, 900),
    V2D(1920, 1080)
  )

  def defaultSettings = Settings(resolutions(0), false)
  override def save(saveable: Settings) = {
    val resBytes = FormulaIO.saveDouble(saveable.screenSize.x) ++ FormulaIO.saveDouble(saveable.screenSize.y)
    Array[Byte](if(saveable.fullScreen) 1 else 0) ++ resBytes
  }
  override def load(bytes: Array[Byte], start: Int, count: Int) = {
    if(bytes.size < start + 17) {
      defaultSettings
    }

    else {
      val fullScreen = bytes(start) == 1
      val screenW = FormulaIO.loadDouble(bytes, 1)
      val screenH = FormulaIO.loadDouble(bytes, 9)
      Settings(V2D(screenW, screenH), fullScreen)
    }
  }
}
