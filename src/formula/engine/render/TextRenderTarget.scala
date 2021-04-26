package formula.engine.render
import formula.application.GUIConstants
import formula.engine._
import formula.io._
import java.awt._

//RenderTarget for rendering text
class TextRenderTarget
(var text: String,
 val textFont: Fonts.Font = GUIConstants.DEFAULT_FONT,
 val fontSize: Float = 1F,
 val fontColor: Color = GUIConstants.COLOR_FONT)
 extends RenderTarget {


  protected var font: Option[Font] = None

  override def updateBounds(width: Double, height: Double, xOffset: Int, yOffset: Int): Unit = {
    super.updateBounds(width, height, xOffset, yOffset)
    font = Some(FormulaIO.getFont(textFont).deriveFont((absoluteBounds.width * GUIConstants.FONT_SIZE * fontSize).toFloat))
  }

  override protected def personalRender(g: Graphics2D) = {

    //Draw text centered to the RenderTarget bounds
    font.foreach(f => {
      g.setFont(f)
      g.setColor(fontColor)
      val w = g.getFontMetrics.stringWidth(text)
      val h = g.getFontMetrics.getDescent
      g.drawString(text, math.round(absoluteBounds.x + 0.5 * (absoluteBounds.width - w)).toInt, absoluteBounds.y + absoluteBounds.height - h)
    })

  }


}