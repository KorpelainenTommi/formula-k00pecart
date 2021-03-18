import javax.swing._
import formula.io._
import java.awt.{Graphics, Color}
import java.awt.image.BufferedImage
import java.awt.event._
package formula.application {


  trait TextureLoader {
    protected def textures: Seq[Textures.Texture]
    protected def loadTextures() = {
      textures.foreach(t => FormulaIO.getTexture(t))
    }
  }

  trait BackgroundPanel {
    protected def backgroundName: String
    protected def additionalPaint(g: Graphics) = {}
    protected def loadBackground() = {
      try {
        backgroundImage = Some(FormulaIO.loadImage(backgroundName))
      }
      catch {
        case e: FormulaIO.ResourceLoadException => MainApplication.messageBox(e.getMessage)
      }
    }

    private var backgroundImage: Option[BufferedImage] = None
    protected val _panel = new JPanel() {
      override def paintComponent(g: Graphics): Unit = {
        super.paintComponent(g)
        backgroundImage.foreach(img => g.drawImage(img, 0, 0, getWidth, getHeight, null))
        additionalPaint(g)
      }
    }
  }

  trait PercentBounds {
    protected var pX: Double = 0
    protected var pY: Double = 0
    protected var pW: Double = 0
    protected var pH: Double = 0

    def setPercentBounds(x: Double, y: Double, w: Double, h: Double) = {
      pX = x
      pY = y
      pW = w
      pH = h
    }

    def updateBounds(width: Double, height: Double)
  }

  trait ComponentPercentBounds extends PercentBounds {
    def component: JComponent
    override def updateBounds(width: Double, height: Double) = {
      component.setBounds((pX*width).toInt, (pY*height).toInt, (pW*width).toInt, (pH*height).toInt)
    }
  }

  trait TextPercentBounds extends ComponentPercentBounds {
    protected def textFont: Fonts.Font
    protected def fontSize: Float
    override def updateBounds(width: Double, height: Double) = {
      super.updateBounds(width, height)
      component.setFont(FormulaIO.getFont(textFont).deriveFont((fontSize * width * 0.018).toFloat))
    }
  }

  class FontLabel(txt: String, val textFont: Fonts.Font = Fonts.Impact, val fontSize: Float = 1F, val fontColor: Color = Color.DARK_GRAY) extends JLabel(txt) with TextPercentBounds {
    override def component = this
    this.setForeground(fontColor)
    def text = this.getText
    def text_=(value: String) = this.setText(value)

  }

  class GrayButton(val title: String, val onclick: () => Unit = () => ()) extends JButton(title) with ComponentPercentBounds {

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
        onclick()
      }
      override def mouseReleased(e: MouseEvent) = {
        button.setBackground(new Color(0, 0, 0, 0))
      }
    })
  }

  class TextInput(txt: String, val textFont: Fonts.Font = Fonts.Impact, val color: Color = Color.DARK_GRAY) extends JTextField(1) with TextPercentBounds {
    override protected def fontSize = 1F
    override def component = this
    this.setForeground(color)
    this.setText(txt)
  }

  class TextArea(txt: String, val textFont: Fonts.Font = Fonts.Impact, val color: Color = Color.DARK_GRAY, protected val textArea: JTextArea = new JTextArea) extends JScrollPane(textArea) with ComponentPercentBounds {
    protected val fontSize = 1F
    override def component = this
    textArea.setForeground(color)
    textArea.setLineWrap(true)
    textArea.setText(txt)

    this.getVerticalScrollBar.setUI(new javax.swing.plaf.basic.BasicScrollBarUI(){
      override protected def configureScrollBarColors() = this.thumbColor = new Color(163, 184, 204, 255)
    })

    this.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER)

    override def updateBounds(width: Double, height: Double) = {
      super.updateBounds(width, height)
      textArea.setFont(FormulaIO.getFont(textFont).deriveFont((fontSize * width * 0.018).toFloat))
    }
  }




}