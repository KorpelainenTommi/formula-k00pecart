package formula

import java.awt.event.KeyEvent
import java.awt.{Dimension, Graphics, KeyEventDispatcher}
import javax.swing._
object MainApplication extends App {

  val topWindow = new JFrame("K00PECART")
  topWindow.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)
  topWindow.setMinimumSize(new Dimension(800, 600))
  topWindow.setPreferredSize(new Dimension(800, 600))
  topWindow.setResizable(false)
  topWindow.add(new JLabel("Hello formula", SwingConstants.CENTER){ this.setFont(this.getFont.deriveFont(32F)) })
  topWindow.setVisible(true)

  //Example of globally catching key events
  java.awt.KeyboardFocusManager.getCurrentKeyboardFocusManager.addKeyEventDispatcher(new KeyEventDispatcher {
    override def dispatchKeyEvent(e: KeyEvent): Boolean = {
      if(e.getID == java.awt.event.KeyEvent.KEY_PRESSED) {
      JOptionPane.showMessageDialog(topWindow, "Key: " + e.getKeyChar)
      }
      false
    }
  })

  while(true) {
    //This should not block keyhandling
  }


}
