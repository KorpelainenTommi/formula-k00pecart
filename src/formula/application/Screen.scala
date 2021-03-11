package formula.application
abstract class Screen {
  def panel: javax.swing.JPanel
  def activate()
  def deactivate()
  def redraw()
  def handleKey(e: java.awt.event.KeyEvent) = {}
}

abstract class StaticScreen(background: String, textureList: formula.io.Textures.Texture*) extends Screen with BackgroundPanel with TextureLoader {
  override protected def backgroundName = background
  override protected def textures = textureList
  override def panel = _panel

  override def activate() = {
    MainApplication.topWindow.getContentPane.add(panel)
    panel.setLayout(null)

    loadBackground()
    loadTextures()
  }

  override def deactivate() = {
    MainApplication.topWindow.getContentPane.remove(panel)
  }

}