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

    val trackWidthSlider = new Slider(20, isVertical = true, minValue = math.ceil(Player.PLAYER_SIZE * 2).toInt, maxValue = 30, onchange = (value: Int) => {
      if(!trackTool.drawingTrack && !trackTool.roadCompleted) {
        trackTool.roadWidth = value
        renderPanel.repaint()
      }
    })
    trackWidthSlider.percentBounds = (0.45, 0.2, 0.1, 0.4)
    components += trackWidthSlider
    trackTool.roadWidth = trackWidthSlider.getValue

    val mapObjectName = new FontLabel("", fontColor = GUIConstants.COLOR_ACCENT)
    mapObjectName.percentPosition = (0.45, 0.6)
    components += mapObjectName


    val mapObjects = MapObjects.objectIDList.map(MapObjects.createMapObject(_, V2D(0, 0)))
    val mapObjectList = new ImageDisplayArea(mapObjects.map(obj => FormulaIO.getTexture(obj.texture)),
      onSelect = ID => {
        trackTool.selectedMapObject = ID
        mapObjectName.setText(if(ID == -1) "" else mapObjects(ID).name)
      })
    mapObjectList.percentBounds = (0.45, 0.2, 0.25, 0.4)
    components += mapObjectList




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

        if(trackTool.mode == TrackTool.PlaceGoal) {
          trackTool.placeGoal()
          renderPanel.repaint()
        }
      }

      override def mouseClicked(e: MouseEvent): Unit = {
        if(trackTool.mode == TrackTool.PlaceObjects) {

          if(e.getButton == MouseEvent.BUTTON1) trackTool.placeObject()
          if(e.getButton == MouseEvent.BUTTON3) trackTool.removeObject()
          renderPanel.repaint()

        }
      }

      override def mouseWheelMoved(e: MouseWheelEvent): Unit = {
        if(!trackTool.drawingTrack && (trackTool.mode == TrackTool.DrawRoad) && !trackTool.roadCompleted) {
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

      if(trackTool.mode == TrackTool.DrawRoad) {
        trackWidthLabel.setVisible(true)
        trackWidthSlider.setVisible(true)
      }
      else {
        trackWidthLabel.setVisible(false)
        trackWidthSlider.setVisible(false)
      }

      if(trackTool.mode == TrackTool.PlaceObjects) {
        mapObjectName.setVisible(true)
        mapObjectList.setVisible(true)
      }
      else {
        mapObjectName.setVisible(false)
        mapObjectList.setVisible(false)
      }

    }

    val toggleButtons = Array.ofDim[ToggleButton](3)
    def deactivateOthers(n: Int) = {
      toggleButtons.zipWithIndex.filter(_._2 != n).foreach(_._1.active = false)
      updateVisibleControls()
      this.panel.repaint()
    }

    //There was a lot of copy-paste code, this generalizes that
    //In hindsight, togglebuttons should have been a radio button group instead
    def makeToggleFunction(n: Int, targetMode: TrackTool.Mode) = {
      (active: Boolean) => {
        trackTool.mode = if(active) targetMode else TrackTool.NoSelection
        deactivateOthers(n)
      }
    }

    val roadButton = new ToggleButton("Draw road", makeToggleFunction(0, TrackTool.DrawRoad))
    roadButton.percentPosition = (0.025, 0.05)
    components += roadButton
    toggleButtons(0) = roadButton

    val objectButton = new ToggleButton("Place objects", makeToggleFunction(1, TrackTool.PlaceObjects))
    objectButton.percentPosition = (0.2, 0.05)
    components += objectButton
    toggleButtons(1) = objectButton

    val goalButton = new ToggleButton("Move goal", makeToggleFunction(2, TrackTool.PlaceGoal))
    goalButton.percentPosition = (0.375, 0.05)
    components += goalButton
    toggleButtons(2) = goalButton

    val dirButton = new GrayButton("Reverse track", () => {trackTool.reverseTrack(); panel.repaint()})
    dirButton.percentPosition = (0.55, 0.05)
    components += dirButton

    trackTool.onTrackCompleted = () => {

      toggleButtons.foreach(_.active = false)
      updateVisibleControls()
      panel.repaint()

    }


    val nameLabel = new FontLabel("Track name")
    nameLabel.percentPosition = (0.76, 0.1)
    components += nameLabel

    val nameInput = new TextInput
    nameInput.percentPosition = (0.75, 0.18)
    nameInput.percentSize = (GUIConstants.TEXTFIELD_WIDTH * 0.8, GUIConstants.TEXTFIELD_HEIGHT)
    components += nameInput

    val creatorLabel = new FontLabel("Track creator")
    creatorLabel.percentPosition = (0.76, 0.28)
    components += creatorLabel

    val creatorInput = new TextInput
    creatorInput.percentPosition = (0.75, 0.35)
    creatorInput.percentSize = (GUIConstants.TEXTFIELD_WIDTH * 0.8, GUIConstants.TEXTFIELD_HEIGHT)
    components += creatorInput

    val descriptionLabel = new FontLabel("Description")
    descriptionLabel.percentPosition = (0.76, 0.46)
    descriptionLabel.percentSize = (GUIConstants.TEXTFIELD_WIDTH * 0.8, GUIConstants.TEXTFIELD_HEIGHT)
    components += descriptionLabel

    val descriptionInput = new TextArea
    descriptionInput.percentPosition = (0.75, 0.54)
    descriptionInput.percentSize = (GUIConstants.TEXTFIELD_WIDTH, GUIConstants.TEXTFIELD_HEIGHT * 2)
    components += descriptionInput


    val backButton = new GrayButton("Back", () => MainApplication.transition(new TrackSelectScreen(TrackSelectScreen.TrackTool)))
    backButton.percentPosition = (0.8, 0.85)
    components += backButton

    val saveButton = new GrayButton("Save", () => {

      val trackName = nameInput.getText.filterNot(_ == FormulaIO.STRING_SEP_CHAR).trim.take(Track.NAME_MAX_LENGTH)
      val trackCreator = creatorInput.getText.filterNot(_ == FormulaIO.STRING_SEP_CHAR).trim.take(Track.CREATOR_MAX_LENGTH)
      val trackDescription = descriptionInput.getText.filterNot(_ == FormulaIO.STRING_SEP_CHAR).trim

      def saveTrack() = {
        val newTrack = new Track(trackName, trackDescription, if(trackCreator.isEmpty) "Unknown" else trackCreator,
          trackTool.roadWidth, trackTool.mapObjects.toVector)
        newTrack.rewriteRoad(trackTool.trackImage, new ClosedLoop(trackTool.pathPositions))
        val success = FormulaIO.saveTrack(newTrack)

        if(success) {
          MainApplication.transition(new TrackSelectScreen(TrackSelectScreen.TrackTool))
        }

        else {
          MainApplication.messageBox("Failed to save track")
        }
      }

      if(!trackTool.roadCompleted) {
        MainApplication.messageBox("This track needs a road")
      }

      else if(trackName.isEmpty) {
        MainApplication.messageBox("This track needs a name")
      }

      else if(FormulaIO.listTrackFiles.contains(trackName+".trck")) {
        if(MainApplication.confirmBox("Track with this name already exists. Overwrite it?")) {
          saveTrack()
        }
      }

      else if(MainApplication.confirmBox(s"Save track named: $trackName?")) {
        saveTrack()
      }
    })
    saveButton.percentPosition = (0.5, 0.85)
    components += saveButton


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

  override def deactivate(): Unit = {
    tool.foreach(_.exit())
    super.deactivate()
    FormulaIO.unloadAllTextures()
  }

}