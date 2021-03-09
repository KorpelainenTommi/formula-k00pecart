import javax.swing._
import formula.io._
import java.awt.{Graphics, Color}
import java.awt.event._
package formula.application {

  trait PercentBounds {
    def component: JComponent
    private var pX: Double = 0
    private var pY: Double = 0
    private var pW: Double = 0
    private var pH: Double = 0

    def setPercentBounds(x: Double, y: Double, w: Double, h: Double) = {
      pX = x
      pY = y
      pW = w
      pH = h
    }

    def updateBounds(width: Double, height: Double) = {
      component.setBounds((pX*width).toInt, (pY*height).toInt, (pW*width).toInt, (pH*height).toInt)
    }
  }



  class GrayButton(val title: String) extends JButton(title) with PercentBounds {
    override def paintComponent(g: Graphics) = {
      val img = FormulaIO.getTexture(Textures.Button)
      g.drawImage(img, 0, 0, getWidth, getHeight, null)
      g.setColor(this.getBackground)
      g.fillRect(0, 0, getWidth, getHeight)
      super.paintComponent(g)
    }

    override def component = this

    override def updateBounds(width: Double, height: Double) = {
      super.updateBounds(width, height)
      this.setFont(FormulaIO.getFont(Fonts.Impact).deriveFont((width * 0.018).toFloat))
    }

    this.setBorderPainted(false)
    this.setFocusPainted(false)
    this.setContentAreaFilled(false)
    this.setBackground(new Color(0, 0, 0, 0))

    private val button = this
    this.addMouseListener(new MouseAdapter() {
      override def mouseEntered(e: MouseEvent) = {
        button.setBackground(new Color(100, 105, 150, 100))
      }
      override def mouseExited(e: MouseEvent) = {
        button.setBackground(new Color(0, 0, 0, 0))
      }
      override def mousePressed(e: MouseEvent) = {
        button.setBackground(new Color(120, 125, 155, 150))
      }
      override def mouseReleased(e: MouseEvent) = {
        button.setBackground(new Color(0, 0, 0, 0))
      }
    })
  }


}