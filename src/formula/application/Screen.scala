package formula.application
import formula.io.Sounds
import formula.io.Textures.Texture

//Base class for screens
abstract class Screen {

  def panel: javax.swing.JPanel

  //Activate is the first method called when a screen becomes active
  def activate() = {
    MainApplication.topWindow.getContentPane.add(panel)
    panel.setLayout(null)
  }

  //Deactivate is called before the next screen becomes active
  def deactivate() = {
    MainApplication.topWindow.getContentPane.remove(panel)
  }

  //Redraw is the last method called when a screen has been loaded
  def redraw() = {
    MainApplication.topWindow.revalidate()
    MainApplication.topWindow.repaint()
  }

  def handleKey(e: java.awt.event.KeyEvent) = {}
}





//Base class for screens that have little to no active rendering
abstract class StaticScreen(backgroundTexture: Texture, textureList: Texture*)
  extends Screen with BackgroundPanel with TextureLoader with SoundLoader {

  override protected def background = backgroundTexture
  override protected def textures   = textureList
  override protected def sounds     = Vector(Sounds.Click, Sounds.Hover)
  override def panel = _panel


  protected val components = scala.collection.mutable.ArrayBuffer[javax.swing.JComponent with PercentBounds]()
  protected def createComponents()


  override def activate() = {
    super.activate()
    loadBackground()
    loadTextures()
    createComponents()
    components.foreach(panel.add)
    redraw()
  }


  override def redraw(): Unit = {
    components.foreach(_.updateBounds(MainApplication.windowWidth, MainApplication.windowHeight))
    super.redraw()
  }

}