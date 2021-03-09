package formula.application

import formula.io.FormulaIO
import formula.io.Settings

import java.awt.KeyboardFocusManager
import java.awt.KeyEventDispatcher
import java.awt.event.KeyEvent
import java.awt.event.WindowEvent
import javax.swing._



object MainApplication extends App {

  //public members and functions
  private var _currentScreen: Screen = null
  def currentScreen = _currentScreen
  def transition(screen: Screen) = {
    _currentScreen = screen
    screen.activate()
  }

  private var _settings: Settings = null
  def settings = _settings




  //Initialization
  private val graphicsEnvironment = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment.getDefaultScreenDevice.getDefaultConfiguration
  val topWindow = new JFrame("K00PECART", graphicsEnvironment)
  transition(new MainMenuScreen())

  private def setState(i: Int) = {
    topWindow.setExtendedState(topWindow.getExtendedState | i)
  }
  def normalize() = setState(java.awt.Frame.NORMAL)
  def minimize()  = setState(java.awt.Frame.ICONIFIED)
  def maximize()  = setState(java.awt.Frame.MAXIMIZED_BOTH)
  def close() = {
    //Close with dispatchEvent instead of System.exit or dispose, so closing is not brute force
    topWindow.dispatchEvent(new WindowEvent(topWindow, WindowEvent.WINDOW_CLOSING))
  }

  topWindow.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)
  topWindow.setResizable(false)

  def updateSettings(s: Settings) = {
    _settings = s
    if(settings.fullScreen) {
      maximize()
    }

    else {
      normalize()
      topWindow.setPreferredSize(settings.screenSize)
      topWindow.setMinimumSize(settings.screenSize)
    }
    currentScreen.redraw()
  }

  updateSettings(FormulaIO.loadSettings)
  topWindow.setVisible(true)


  //Hook up keyEvents
  KeyboardFocusManager.getCurrentKeyboardFocusManager.addKeyEventDispatcher(new KeyEventDispatcher {
    override def dispatchKeyEvent(e: KeyEvent): Boolean = {
      currentScreen.handleKey(e)
      false
    }
  })

  /*
  val bufImg = javax.imageio.ImageIO.read(new java.io.File(FormulaIO.resolvePath("data", "textures", "goal0.png")))
  var offs = 0


  topWindow.getContentPane.add(new JPanel(){
    override def paintComponent(g: Graphics): Unit = {
      super.paintComponent(g)
      val g2d = g.asInstanceOf[Graphics2D]
      g2d.setPaint(new TexturePaint(bufImg, new Rectangle(0, offs, bufImg.getWidth, bufImg.getHeight)))
      g2d.fill(new Rectangle(0, 0, getWidth, getHeight))
    }
  })
  */


}
