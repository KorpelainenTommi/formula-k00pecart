package formula.application
abstract class Screen {
  def panel: javax.swing.JPanel
  def activate()
  def redraw()
  def handleKey(e: java.awt.event.KeyEvent) = {}
}