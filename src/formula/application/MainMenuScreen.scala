package formula.application
import java.awt.Graphics
import java.awt.image.BufferedImage
import javax.swing._
import formula.io.FormulaIO
class MainMenuScreen() extends Screen {

  private var backgroundImage: Option[BufferedImage] = None
  val _panel = new JPanel() {
    override def paintComponent(g: Graphics): Unit = {
      super.paintComponent(g)
      backgroundImage.foreach(img => g.drawImage(img, 0, 0, getWidth, getHeight, null))
    }
  }
  override def panel = _panel

  override def activate() = {
    panel.setLayout(new GroupLayout(panel))
    MainApplication.topWindow.getContentPane.add(panel)
    try {
      backgroundImage = Some(FormulaIO.loadImage("background0.png"))
    }

    catch {
      case e: FormulaIO.ResourceLoadException => MainApplication.messageBox(e.getMessage)
    }
    MainApplication.topWindow.revalidate()
    MainApplication.topWindow.repaint()
  }

  override def redraw(): Unit = {

  }
}