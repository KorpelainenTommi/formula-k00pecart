package formula.application
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

abstract class StaticScreen(background: String, textureList: formula.io.Textures.Texture*) extends Screen with BackgroundPanel with TextureLoader {
  override protected def backgroundName = background
  override protected def textures = textureList
  override def panel = _panel

  override def activate() = {
    super.activate()
    loadBackground()
    loadTextures()
  }
}