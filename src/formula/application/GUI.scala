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
    protected def background: formula.io.Textures.Texture
    protected def additionalPaint(g: Graphics) = {}
    protected def loadBackground() = {
      try {
        backgroundImage = Some(FormulaIO.loadImage(formula.io.Textures.path(background)))
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

    def percentPosition = (pX, pY)
    def percentPosition_=(value: (Double, Double)) = {
      pX = value._1
      pY = value._2
    }

    def percentSize = (pW, pH)
    def percentSize_=(value: (Double, Double)) = {
      pW = value._1
      pH = value._2
    }

    def percentBounds = (pX, pY, pW, pH)
    def percentBounds_=(value: (Double, Double, Double, Double)) = {
      pX = value._1
      pY = value._2
      pW = value._3
      pH = value._4
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
      component.setFont(FormulaIO.getFont(textFont).deriveFont((fontSize * width * GUIConstants.FONT_SIZE).toFloat))
    }
  }

  class FontLabel
  (txt: String,
  val textFont: Fonts.Font = GUIConstants.DEFAULT_FONT,
  val fontSize: Float = 1F,
  val fontColor: Color = GUIConstants.COLOR_FONT)
  extends JLabel(txt) with TextPercentBounds {

    override def component = this
    this.setForeground(fontColor)
    def text = this.getText
    def text_=(value: String) = this.setText(value)
  }

  class CheckBox extends JCheckBox with ComponentPercentBounds {
    override def component = this
    this.setOpaque(false)
  }

  class GrayButton
  (val title: String,
  val onclick: () => Unit = () => ())
  extends JButton(title) with ComponentPercentBounds {

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
      this.setFont(FormulaIO.getFont(GUIConstants.DEFAULT_FONT).deriveFont((width * GUIConstants.FONT_SIZE).toFloat))
    }

    this.setBorderPainted(false)
    this.setFocusPainted(false)
    this.setContentAreaFilled(false)
    this.setBackground(GUIConstants.COLOR_BLANK)

    private val button = this
    this.addMouseListener(new MouseAdapter() {
      override def mouseEntered(e: MouseEvent) = {
        button.setBackground(GUIConstants.COLOR_BUTTON_HOVER)
      }
      override def mouseExited(e: MouseEvent) = {
        button.setBackground(GUIConstants.COLOR_BLANK)
      }
      override def mousePressed(e: MouseEvent) = {
        button.setBackground(GUIConstants.COLOR_BUTTON_PRESSED)
        onclick()
      }
      override def mouseReleased(e: MouseEvent) = {
        button.setBackground(GUIConstants.COLOR_BLANK)
      }
    })

    this.percentSize = (GUIConstants.BUTTON_WIDTH, GUIConstants.BUTTON_HEIGHT)
  }

  class TextInput
  (txt: String,
  val textFont: Fonts.Font = GUIConstants.DEFAULT_FONT,
  val color: Color = GUIConstants.COLOR_FONT)
  extends JTextField(1) with TextPercentBounds {

    override protected def fontSize = 1F
    override def component = this
    this.setForeground(color)
    this.setText(txt)

    this.percentSize = (GUIConstants.TEXTFIELD_WIDTH, GUIConstants.TEXTFIELD_HEIGHT)
  }

  class TextArea
  (txt: String,
  val textFont: Fonts.Font = GUIConstants.DEFAULT_FONT,
  val color: Color = GUIConstants.COLOR_FONT,
  protected val textArea: JTextArea = new JTextArea)
  extends JScrollPane(textArea) with ComponentPercentBounds {

    protected val fontSize = 1F
    override def component = this
    textArea.setForeground(color)
    textArea.setLineWrap(true)
    textArea.setText(txt)

    this.getVerticalScrollBar.setUI(new javax.swing.plaf.basic.BasicScrollBarUI(){
      override protected def configureScrollBarColors() = this.thumbColor = GUIConstants.COLOR_SCROLLBAR
    })

    this.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER)

    override def updateBounds(width: Double, height: Double) = {
      super.updateBounds(width, height)
      textArea.setFont(FormulaIO.getFont(textFont).deriveFont((fontSize * width * GUIConstants.FONT_SIZE).toFloat))
    }
  }

  class DropDown
  (options: Seq[String],
  val textFont: Fonts.Font = GUIConstants.DEFAULT_FONT)
  extends JComboBox[String](options.toArray) with TextPercentBounds {

    override def component = this
    protected val fontSize = 1F

    this.percentSize = (GUIConstants.BUTTON_WIDTH, GUIConstants.BUTTON_HEIGHT)

  }

  class ImageDisplayArea
  (entries: Seq[BufferedImage], onSelect: Int => Unit = (_ => {}),
  protected val imageList: JList[ImageIcon] = new JList[ImageIcon]())
  extends JScrollPane(imageList) with ComponentPercentBounds {

    override def component = this

    imageList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION)
    imageList.setLayoutOrientation(JList.HORIZONTAL_WRAP)
    imageList.setVisibleRowCount(-1)
    imageList.setBackground(GUIConstants.COLOR_AREA)

    imageList.addListSelectionListener(e => if (!e.getValueIsAdjusting) onSelect(imageList.getSelectedIndex))

    this.getVerticalScrollBar.setUI(new javax.swing.plaf.basic.BasicScrollBarUI(){
      override protected def configureScrollBarColors() = this.thumbColor = GUIConstants.COLOR_SCROLLBAR
    })

    this.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER)
    this.getVerticalScrollBar.setUnitIncrement(10)

    override def updateBounds(width: Double, height: Double) = {
      super.updateBounds(width, height)
      val w = (pW*width*GUIConstants.IMAGE_CELL_WIDTH).toInt
      imageList.setFixedCellWidth(w)
      imageList.setFixedCellHeight(w)

      imageList.setListData(entries.map(img => {
        val iconImage = new BufferedImage(w, w, BufferedImage.TYPE_INT_ARGB)
        val g = iconImage.createGraphics()
        val margin = GUIConstants.IMAGE_CELL_MARGIN
        val border = GUIConstants.IMAGE_CELL_BORDER
        g.setColor(GUIConstants.COLOR_CELL_BORDER)
        g.fillRect(border, border, w-2*border, w-2*border)
        g.drawImage(img, margin, margin,w-2*margin,w-2*margin, null)
        g.dispose()
        new ImageIcon(iconImage)
      }).toArray)
    }
  }


}