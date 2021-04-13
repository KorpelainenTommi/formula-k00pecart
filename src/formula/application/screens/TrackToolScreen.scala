package formula.application.screens
import formula.application._
import formula.engine._
import java.awt.event._
import formula.io._

class TrackToolScreen extends StaticScreen(Textures.Background_Generic, Textures.Button) {

  var tool : Option[TrackTool] = None

  override protected def createComponents(): Unit = {


    val trackTool = new TrackTool
    tool = Some(trackTool)
    val trackTarget = new formula.engine.render.TrackToolRenderTarget(trackTool)
    trackTarget.percentBounds = (0, 0, 1, 1)
    val renderPanel = new RenderPanel(trackTarget)
    renderPanel.percentSize = (0.4, 0.71)
    renderPanel.percentPosition = (0.025, 0.2)

    val trackWidthLabel = new FontLabel("Track width")
    trackWidthLabel.percentBounds = (0.475, 0.15, 0.15, 0.03)
    components += trackWidthLabel

    val trackWidthSlider = new Slider(isVertical = true, minValue = math.ceil(Player.PLAYER_SIZE).toInt, maxValue = 30, onchange = (value: Int) => {
      if(!trackTool.drawingTrack) {
        trackTool.roadWidth = value
        renderPanel.repaint()
      }
    })
    trackWidthSlider.percentBounds = (0.45, 0.2, 0.1, 0.4)
    components += trackWidthSlider
    trackTool.roadWidth = trackWidthSlider.getValue


    val mouseAdapter = new MouseAdapter {

      private def mouseUpdate(e: MouseEvent) = {
        val point = e.getPoint
        trackTool.mousePosition = V2D(point.getX / renderPanel.getWidth, point.getY / renderPanel.getHeight)
        renderPanel.repaint()
      }

      override def mouseMoved(e: MouseEvent): Unit = mouseUpdate(e)
      override def mouseDragged(e: MouseEvent): Unit = mouseUpdate(e)
      override def mouseExited(e: MouseEvent): Unit = {
        trackTool.mousePosition = V2D(-1, -1)
        renderPanel.repaint()
      }

      override def mousePressed(e: MouseEvent): Unit = {
        if(trackTool.mode == TrackTool.DrawRoad && !trackTool.drawingTrack) {
          trackTool.beginTrackDraw()
        }
      }

      override def mouseWheelMoved(e: MouseWheelEvent): Unit = {
        if(!trackTool.drawingTrack && (trackTool.mode == TrackTool.DrawRoad || trackTool.mode == TrackTool.DrawShortcut)) {
          val units = e.getUnitsToScroll
          if(units > 0) {
            trackWidthSlider.setValue(trackWidthSlider.getValue - 1)
          }
          else if(units < 0) {
            trackWidthSlider.setValue(trackWidthSlider.getValue + 1)
          }
        }
      }
    }

    renderPanel.addMouseListener(mouseAdapter)
    renderPanel.addMouseMotionListener(mouseAdapter)
    renderPanel.addMouseWheelListener(mouseAdapter)
    components += renderPanel


    def updateVisibleControls() = {
      if(trackTool.mode == TrackTool.DrawRoad || trackTool.mode == TrackTool.DrawShortcut) {
        trackWidthLabel.setVisible(true)
        trackWidthSlider.setVisible(true)
      }
      else {
        trackWidthLabel.setVisible(false)
        trackWidthSlider.setVisible(false)
      }
    }

    val toggleButtons = Array.ofDim[ToggleButton](3)

    val roadButton = new ToggleButton("Draw road", (active: Boolean) => {
      toggleButtons.zipWithIndex.filter(_._2 != 0).foreach(_._1.active = false)
      trackTool.mode = if(active) TrackTool.DrawRoad else TrackTool.NoSelection
      updateVisibleControls()
      this.panel.repaint()
    })
    roadButton.percentPosition = (0.025, 0.05)
    components += roadButton
    toggleButtons(0) = roadButton

    val shortcutButton = new ToggleButton("Draw shortcut", (active: Boolean) => {
      toggleButtons.zipWithIndex.filter(_._2 != 1).foreach(_._1.active = false)
      trackTool.mode = if(active) TrackTool.DrawShortcut else TrackTool.NoSelection
      updateVisibleControls()
      this.panel.repaint()
    })
    shortcutButton.percentPosition = (0.2, 0.05)
    components += shortcutButton
    toggleButtons(1) = shortcutButton

    val objectButton = new ToggleButton("Place objects", (active: Boolean) => {
      toggleButtons.zipWithIndex.filter(_._2 != 2).foreach(_._1.active = false)
      trackTool.mode = if(active) TrackTool.PlaceObjects else TrackTool.NoSelection
      updateVisibleControls()
      this.panel.repaint()
    })
    objectButton.percentPosition = (0.375, 0.05)
    components += objectButton
    toggleButtons(2) = objectButton

    trackTool.onTrackCompleted = () => {

      toggleButtons.foreach(_.active = false)
      updateVisibleControls()
      panel.repaint()

    }


    val backButton = new GrayButton("Back", () => MainApplication.transition(new TrackSelectScreen(TrackSelectScreen.TrackTool)))
    backButton.percentPosition = (0.8, 0.85)
    components += backButton

    updateVisibleControls()

  }

  override def handleKey(e: KeyEvent): Unit = {
    if(e.getID == KeyEvent.KEY_PRESSED && e.getKeyCode == KeyEvent.VK_ESCAPE) {
      tool.foreach(t => {
        if(t.mode == TrackTool.DrawRoad && t.drawingTrack) {
          t.stopTrackDraw()
          panel.repaint()
        }
      })
    }
  }

}