package formula.application

import formula.application.screens.MainMenuScreen
import formula.io.FormulaIO
import formula.io.Settings

import java.awt.KeyboardFocusManager
import java.awt.event.WindowEvent
import javax.swing._



object MainApplication extends App {

  //public members and functions
  private var _currentScreen: Option[Screen] = None
  def currentScreen = _currentScreen
  def transition(screen: Screen) = {
    _currentScreen.foreach(_.deactivate())
    _currentScreen = Some(screen)
    screen.activate()
  }

  private var _settings: Settings = Settings.defaultSettings
  def settings = _settings

  //Initialization

  private val graphicsEnvironment = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment
  private val configuration = graphicsEnvironment.getDefaultScreenDevice.getDefaultConfiguration

  //Register fonts
  formula.io.Fonts.values.foreach(f => {
    graphicsEnvironment.registerFont(FormulaIO.getFont(f))
  })


  val _topWindow = new JFrame("K00PECART", configuration)
  def topWindow = _topWindow

  try {
    topWindow.setIconImage(FormulaIO.loadImage("icon0.png"))
  }

  catch {
    case _: FormulaIO.ResourceLoadException =>
  }


  //Set bounds for maximized borderless
  topWindow.setMaximizedBounds(configuration.getBounds)
  transition(new MainMenuScreen())

  var maximized = false

  private def setState(i: Int) = {
    topWindow.setExtendedState(topWindow.getExtendedState | i)
  }
  def minimize() = setState(java.awt.Frame.ICONIFIED)

  def normalize() = {
    topWindow.dispose()
    topWindow.setUndecorated(false)
    setState(java.awt.Frame.NORMAL)
    topWindow.setMinimumSize(settings.screenSize)
    topWindow.setSize(settings.screenSize)
    topWindow.setExtendedState(topWindow.getExtendedState & ~java.awt.Frame.MAXIMIZED_BOTH)
    topWindow.setVisible(true)
    maximized = false
    currentScreen.foreach(_.redraw())
  }

  def maximize() = {
    topWindow.dispose()
    topWindow.setUndecorated(true)
    topWindow.setSize(settings.screenSize)
    setState(java.awt.Frame.MAXIMIZED_BOTH)
    topWindow.setVisible(true)
    maximized = true
    currentScreen.foreach(_.redraw())
  }

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
    }
  }

  updateSettings(FormulaIO.loadSettings)


  def windowWidth  = if(maximized) configuration.getBounds.width else topWindow.getContentPane.getWidth
  def windowHeight = if(maximized) configuration.getBounds.height else topWindow.getContentPane.getHeight


  def messageBox(message: String) = {
    JOptionPane.showMessageDialog(topWindow, message)
  }

  def confirmBox(message: String) = {
    JOptionPane.showConfirmDialog(topWindow, message, "Are you sure?", JOptionPane.YES_NO_OPTION) == JOptionPane.OK_OPTION
  }

  def modalActionBox(message: String, action: JDialog => Unit) = {
    val pane = new JOptionPane()
    pane.setMessageType(JOptionPane.INFORMATION_MESSAGE)
    pane.setMessage(message)
    val dialog = pane.createDialog(topWindow, "Input")
    action(dialog)
    dialog.setVisible(true)
  }

  //Hook up keyEvents
  KeyboardFocusManager.getCurrentKeyboardFocusManager.addKeyEventDispatcher(e => {
    currentScreen.foreach(_.handleKey(e))
    false
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
