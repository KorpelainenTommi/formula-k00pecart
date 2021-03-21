package formula.application
import formula.io.Textures.Texture
abstract class Screen {
  def panel: javax.swing.JPanel
  def activate() = {
    MainApplication.topWindow.getContentPane.add(panel)
    panel.setLayout(null)
  }

  def deactivate() = {
    MainApplication.topWindow.getContentPane.remove(panel)
  }

  def redraw() = {
    MainApplication.topWindow.revalidate()
    MainApplication.topWindow.repaint()
  }

  def handleKey(e: java.awt.event.KeyEvent) = {}
}

abstract class StaticScreen(backgroundTexture: Texture, textureList: Texture*) extends Screen with BackgroundPanel with TextureLoader {
  override protected def background = backgroundTexture
  override protected def textures = textureList
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