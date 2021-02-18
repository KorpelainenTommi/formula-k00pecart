package formula

import java.awt.Graphics
import javax.swing._
import java.awt.Dimension
object MainApplication extends App {

  val topWindow = new JFrame("K00PECART")
  topWindow.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)
  topWindow.setMinimumSize(new Dimension(800, 600))
  topWindow.setPreferredSize(new Dimension(800, 600))
  topWindow.setResizable(false)
  topWindow.add(new JLabel("Hello formula", SwingConstants.CENTER){ this.setFont(this.getFont.deriveFont(32F)) })
  topWindow.setVisible(true)

}
