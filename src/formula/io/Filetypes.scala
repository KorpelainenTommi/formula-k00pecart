package formula.io {

  import formula.engine.V2D

  trait Serializable[T] {
    def save(saveable: T): Seq[Char]
    def load(bytes: Seq[Char], start: Int, count: Int): T
    def load(bytes: Seq[Char]): T = load(bytes, 0, bytes.size)
  }

  //TODO: implement settings load and save

  case class Settings(screenSize: V2D, fullScreen: Boolean)

  object Settings extends Serializable[Settings] {
    def defaultSettings = Settings(V2D(800, 600), false)
    override def save(saveable: Settings) = {Seq[Char]()}
    override def load(bytes: Seq[Char], start: Int, count: Int) = defaultSettings
  }



}