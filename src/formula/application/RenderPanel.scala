package formula.application
import javax.swing._
import formula.engine._
import java.awt.{Graphics, Graphics2D}

//A panel that gets its graphics by rendering content
class RenderPanel(val renderTarget: RenderTarget) extends JPanel with ComponentPercentBounds {

  this.setLayout(null)
  override def component = this

  override def paintComponent(g: Graphics) = {
    renderTarget.render(g.asInstanceOf[Graphics2D])
  }

  override def updateBounds(width: Double, height: Double) = {
    super.updateBounds(width, height)
    renderTarget.updateBounds(getBounds().width, getBounds().height, 0, 0)
  }

}