package formula.application
import java.awt.Color

//Color and size constants defining the style for the Graphical User Interface
object GUIConstants {

  val DEFAULT_FONT = formula.io.Fonts.Impact

  val FONT_SIZE            = 0.018
  val BUTTON_WIDTH         = 0.140
  val BUTTON_HEIGHT        = 0.070
  val CHECKBOX_SIZE        = 0.040
  val TEXTFIELD_WIDTH      = 0.200
  val TEXTFIELD_HEIGHT     = 0.070
  val IMAGE_CELL_WIDTH     = 0.220
  val IMAGE_CELL_MARGIN    = 10
  val IMAGE_CELL_BORDER    = 8

  val COLOR_FONT           = Color.DARK_GRAY
  val COLOR_ACCENT         = Color.YELLOW

  val COLOR_BLANK          = new Color(0,0,0,0)
  val COLOR_AREA           = new Color(127, 127, 127, 255)
  val COLOR_HEADER         = new Color(200, 200, 200, 255)
  val COLOR_HEADER2        = new Color(220, 220, 220, 255)
  val COLOR_SCROLLBAR      = new Color(163, 184, 204, 255)
  val COLOR_TRACK_GRASS    = new Color(38 , 127,  0 , 255)
  val COLOR_CELL_BORDER    = new Color(255, 215,  0 , 255)
  val COLOR_BUTTON_HOVER   = new Color(100, 105, 150, 100)
  val COLOR_BUTTON_ACTIVE  = new Color(80 , 180, 105, 100)
  val COLOR_BUTTON_PRESSED = new Color(120, 125, 155, 150)

}