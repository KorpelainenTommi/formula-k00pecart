package formula.application

import javax.swing._
class SubPanel(heightMultiplier: Double, protected val subpanel: JPanel = new JPanel()) extends JScrollPane(subpanel) with ComponentPercentBounds {

  protected val components = scala.collection.mutable.ArrayBuffer[javax.swing.JComponent with PercentBounds]()
  override def component = this
  subpanel.setLayout(null)
  subpanel.setBackground(GUIConstants.COLOR_AREA)

  def addComponent(c: javax.swing.JComponent with PercentBounds) = {
    components += c
    subpanel.add(c)
  }

  this.getVerticalScrollBar.setUI(new javax.swing.plaf.basic.BasicScrollBarUI(){
      override protected def configureScrollBarColors() = this.thumbColor = GUIConstants.COLOR_SCROLLBAR
  })

  this.getVerticalScrollBar.setUnitIncrement(20)

    this.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER)

    override def updateBounds(width: Double, height: Double) = {
      super.updateBounds(width, height)
      subpanel.setPreferredSize(new java.awt.Dimension(math.round(pW*width).toInt, math.round(pH*height*heightMultiplier).toInt))
      val w = pW*width
      val h = pH*height
      components.foreach(_.updateBounds(w, h))
    }
}