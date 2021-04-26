package formula.engine.render
import formula.application.GUIConstants
import formula.io._
import java.awt._

//Render text that gets its value by calling the given function
//Useful for presenting game stats like time, lap number etc.
class InfoRenderTarget
(textfont: Fonts.Font = GUIConstants.DEFAULT_FONT,
fontsize: Float = 1F,
fontcolor: Color = GUIConstants.COLOR_ACCENT, val infoFunction: () => String)
extends TextRenderTarget("", textfont, fontsize, fontcolor) {

  override protected def personalRender(g: Graphics2D): Unit = {
    text = infoFunction()
    super.personalRender(g)
  }

}