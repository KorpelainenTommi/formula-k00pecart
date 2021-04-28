package formula.io
import formula.engine.V2D
import java.awt.event.KeyEvent

/** Trait describing that this object or class provides a way to transform
 *  an instance of T to bytes, and from bytes
 * @tparam T The type this object/class can serialize/deserialize
 */
trait Serializer[T] {
  def save(saveable: T): Array[Byte]
  def load(bytes: Array[Byte], start: Int): T
  def load(bytes: Array[Byte]): T = load(bytes, 0)
}

/** Case class describing application settings
 *
 * @param resolution Index pointing to a preset screenResolution
 * @param fullScreen Boolean indicating fullscreen/windowed mode
 * @param player1Controls Array of virtual keys for player1
 * @param player2Controls Array of virtual keys for player2
 * @param targetFramerate Desired framerate for the game
 * @param effects Controls whether the game will render animated effects on cars
 * @param volume Master volume from 0-100
 */
case class Settings
(resolution: Int,
 fullScreen: Boolean,
 player1Controls: Vector[Int],
 player2Controls: Vector[Int],
 targetFramerate: Int,
 effects: Boolean,
 volume: Int) {
  def screenSize = Settings.resolutions(if(resolution < 0 || resolution >= Settings.resolutions.length) 0 else resolution)
}

object Settings extends Serializer[Settings] {

  //Default 16:9 resolutions
  val resolutions = Vector(
    V2D(1024, 576),
    V2D(1280, 720),
    V2D(1366, 768),
    V2D(1600, 900),
    V2D(1920, 1080)
  )

  /* Mapping of virtual keys to their names
   * There isn't an existing function for this (at least to my knowledge)
   * since virtual keys aren't officially named. It is dependent on the key layout.
   * For example, the virtual key S is actually the key O in a dvorak keyboard.
   * */
  def keyName(keyCode: Int) = {
    keyCode match {
      case KeyEvent.VK_0 => "0"
      case KeyEvent.VK_1 => "1"
      case KeyEvent.VK_2 => "2"
      case KeyEvent.VK_3 => "3"
      case KeyEvent.VK_4 => "4"
      case KeyEvent.VK_5 => "5"
      case KeyEvent.VK_6 => "6"
      case KeyEvent.VK_7 => "7"
      case KeyEvent.VK_8 => "8"
      case KeyEvent.VK_9 => "9"
      case KeyEvent.VK_A => "A"
      case KeyEvent.VK_B => "B"
      case KeyEvent.VK_C => "C"
      case KeyEvent.VK_D => "D"
      case KeyEvent.VK_E => "E"
      case KeyEvent.VK_F => "F"
      case KeyEvent.VK_G => "G"
      case KeyEvent.VK_H => "H"
      case KeyEvent.VK_I => "I"
      case KeyEvent.VK_J => "J"
      case KeyEvent.VK_K => "K"
      case KeyEvent.VK_L => "L"
      case KeyEvent.VK_M => "M"
      case KeyEvent.VK_N => "N"
      case KeyEvent.VK_O => "O"
      case KeyEvent.VK_P => "P"
      case KeyEvent.VK_Q => "Q"
      case KeyEvent.VK_R => "R"
      case KeyEvent.VK_S => "S"
      case KeyEvent.VK_T => "T"
      case KeyEvent.VK_U => "U"
      case KeyEvent.VK_V => "V"
      case KeyEvent.VK_W => "W"
      case KeyEvent.VK_X => "X"
      case KeyEvent.VK_Y => "Y"
      case KeyEvent.VK_Z => "Z"
      case KeyEvent.VK_UP => "UP"
      case KeyEvent.VK_DOWN => "DOWN"
      case KeyEvent.VK_LEFT => "LEFT"
      case KeyEvent.VK_RIGHT => "RIGHT"
      case default => "K"+keyCode
    }
  }

  val defaultPlayer1Controls = Vector(KeyEvent.VK_A, KeyEvent.VK_D, KeyEvent.VK_W, KeyEvent.VK_S)
  val defaultPlayer2Controls = Vector(KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_UP, KeyEvent.VK_DOWN)


  def defaultSettings = Settings(0, false, defaultPlayer1Controls, defaultPlayer2Controls, 150, true, 30)


  override def save(saveable: Settings) = {
    Array[Byte](if(saveable.fullScreen) 1 else 0) ++
    FormulaIO.saveInt(saveable.resolution) ++
    saveable.player1Controls.flatMap(FormulaIO.saveInt(_)) ++
    saveable.player2Controls.flatMap(FormulaIO.saveInt(_)) ++
    FormulaIO.saveInt(saveable.targetFramerate) ++
    Array[Byte](if(saveable.effects) 1 else 0) ++
    FormulaIO.saveInt(saveable.volume)
  }

  override def load(bytes: Array[Byte], start: Int) = {

    //Invalid settings file, use defaults
    if(bytes.length < start + 14 + defaultPlayer1Controls.length*4 + defaultPlayer2Controls.length*4) {
      defaultSettings
    }

    else {
      val fullScreen = bytes(start) != 0
      val resolution = FormulaIO.loadInt(bytes, 1)
      var idx = 5

      val player1Controls = Vector.tabulate[Int](defaultPlayer1Controls.length)(i => {
        val keycode = FormulaIO.loadInt(bytes, idx)
        idx += 4
        keycode
      })

      val player2Controls = Vector.tabulate[Int](defaultPlayer2Controls.length)(i => {
        val keycode = FormulaIO.loadInt(bytes, idx)
        idx += 4
        keycode
      })

      var targetFramerate = FormulaIO.loadInt(bytes, idx)
      idx += 4
      if(targetFramerate <= 0) targetFramerate = defaultSettings.targetFramerate
      val effects = bytes(start + idx) != 0
      idx += 1
      val volume = math.max(math.min(math.abs(FormulaIO.loadInt(bytes, idx)), 100), 0)
      idx += 4

      Settings(if(resolution<0 || resolution>resolutions.length-1) 0 else resolution,
        fullScreen, player1Controls, player2Controls, targetFramerate, effects, volume)
    }

  }

}
