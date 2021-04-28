package formula.application.screens
import java.awt.event.KeyEvent
import formula.application._
import formula.io._

class SettingsScreen extends StaticScreen(Textures.Background_Generic, Textures.Button) {

  private var choosingKey = false
  private var keyDialog:   Option[javax.swing.JDialog] = None
  private var keyCallBack: Option[Int => Unit]         = None


  protected override def createComponents() = {

    val screenLabel = new FontLabel("Settings", fontSize = 2, fontColor = GUIConstants.COLOR_HEADER2)
    screenLabel.percentSize = (GUIConstants.TEXTFIELD_WIDTH, GUIConstants.TEXTFIELD_HEIGHT*2)
    screenLabel.percentPosition = (0.8, 0.05)
    components += screenLabel

    val subW = 0.75
    val subH = 0.8

    val subPanel = new SubPanel(2)
    subPanel.percentBounds = (0.025, 0.1, subW, subH)
    components += subPanel

    //Subpanel components

    val graphicsLabel = new FontLabel("G R A P H I C S", fontSize = 2.3F, fontColor = GUIConstants.COLOR_HEADER)
    graphicsLabel.percentSize = (2*GUIConstants.TEXTFIELD_WIDTH/subW, GUIConstants.TEXTFIELD_HEIGHT/subH)
    graphicsLabel.percentPosition = (0.2, 0.05)
    subPanel.addComponent(graphicsLabel)

    val resolutionLabel = new FontLabel("Windowed Screen Resolution", fontSize = 1.8F)
    resolutionLabel.percentSize = (2*GUIConstants.TEXTFIELD_WIDTH/subW, GUIConstants.TEXTFIELD_HEIGHT/subH)
    resolutionLabel.percentPosition = (0.05, 0.2)
    subPanel.addComponent(resolutionLabel)

    val resolutionDropdown = new DropDown(Settings.resolutions.map(res => s"${res.x.toInt} x ${res.y.toInt}"))
    resolutionDropdown.percentSize = (GUIConstants.TEXTFIELD_WIDTH / subW, GUIConstants.TEXTFIELD_HEIGHT/subH)
    resolutionDropdown.percentPosition = (0.05, 0.3)
    resolutionDropdown.setSelectedIndex(MainApplication.settings.resolution)
    subPanel.addComponent(resolutionDropdown)

    val fullscreenLabel = new FontLabel("Screen mode", fontSize = 1.8F)
    fullscreenLabel.percentSize = (2*GUIConstants.TEXTFIELD_WIDTH/subW, GUIConstants.TEXTFIELD_HEIGHT/subH)
    fullscreenLabel.percentPosition = (0.6, 0.2)
    subPanel.addComponent(fullscreenLabel)

    val screenDropdown = new DropDown(Vector("Windowed", "Fullscreen borderless"))
    screenDropdown.percentSize = (GUIConstants.TEXTFIELD_WIDTH / subW, GUIConstants.TEXTFIELD_HEIGHT/subH)
    screenDropdown.percentPosition = (0.6, 0.3)
    screenDropdown.setSelectedIndex(if(MainApplication.settings.fullScreen) 1 else 0)
    subPanel.addComponent(screenDropdown)

    val fpsLabel = new FontLabel("Target framerate", fontSize = 1.8F)
    fpsLabel.percentSize = (2*GUIConstants.TEXTFIELD_WIDTH/subW, GUIConstants.TEXTFIELD_HEIGHT/subH)
    fpsLabel.percentPosition = (0.05, 0.4)
    subPanel.addComponent(fpsLabel)

    val fpsValue = new TextInput(MainApplication.settings.targetFramerate.toString)
    fpsValue.percentPosition = (0.05, 0.5)
    subPanel.addComponent(fpsValue)

    val effectsLabel = new FontLabel("Particle effects", fontSize = 1.8F)
    effectsLabel.percentSize = (2*GUIConstants.TEXTFIELD_WIDTH/subW, GUIConstants.TEXTFIELD_HEIGHT/subH)
    effectsLabel.percentPosition = (0.6, 0.4)
    subPanel.addComponent(effectsLabel)

    val effectsDropdown = new DropDown(Vector("On", "Off"))
    effectsDropdown.percentSize = (GUIConstants.TEXTFIELD_WIDTH / subW / 3, GUIConstants.TEXTFIELD_HEIGHT/subH)
    effectsDropdown.percentPosition = (0.6, 0.5)
    effectsDropdown.setSelectedIndex(if(MainApplication.settings.effects) 0 else 1)
    subPanel.addComponent(effectsDropdown)


    val controlsLabel = new FontLabel("C O N T R O L S", fontSize = 2.3F, fontColor = GUIConstants.COLOR_HEADER)
    controlsLabel.percentSize = (2*GUIConstants.TEXTFIELD_WIDTH/subW, GUIConstants.TEXTFIELD_HEIGHT/subH)
    controlsLabel.percentPosition = (0.3, 0.7)
    subPanel.addComponent(controlsLabel)


    val player1Label = new FontLabel("Player 1 controls", fontSize = 1.8F)
    player1Label.percentSize = (2*GUIConstants.TEXTFIELD_WIDTH/subW, GUIConstants.TEXTFIELD_HEIGHT/subH)
    player1Label.percentPosition = (0.05, 0.85)
    subPanel.addComponent(player1Label)

    val player2Label = new FontLabel("Player 2 controls", fontSize = 1.8F)
    player2Label.percentSize = (2*GUIConstants.TEXTFIELD_WIDTH/subW, GUIConstants.TEXTFIELD_HEIGHT/subH)
    player2Label.percentPosition = (0.6, 0.85)
    subPanel.addComponent(player2Label)

    val keyLabels = Vector("Steer left:", "Steer right:", "Gear up:", "Gear down:")
    val keyNames1 = Array.ofDim[FontLabel](Settings.defaultPlayer1Controls.length)
    val keyCodes1 = MainApplication.settings.player1Controls.toArray

    val keyNames2 = Array.ofDim[FontLabel](Settings.defaultPlayer2Controls.length)
    val keyCodes2 = MainApplication.settings.player2Controls.toArray

    for(i <- Settings.defaultPlayer1Controls.indices) {

      val keyLabel1 = new FontLabel(keyLabels(i), fontSize = 1.3F)
      keyLabel1.percentSize = (GUIConstants.TEXTFIELD_WIDTH/subW, GUIConstants.TEXTFIELD_HEIGHT/subH)
      keyLabel1.percentPosition = (0.03, 1.0+i*0.1)
      subPanel.addComponent(keyLabel1)

      val keyLabel2 = new FontLabel(keyLabels(i), fontSize = 1.3F)
      keyLabel2.percentSize = (GUIConstants.TEXTFIELD_WIDTH/subW, GUIConstants.TEXTFIELD_HEIGHT/subH)
      keyLabel2.percentPosition = (0.58, 1.0+i*0.1)
      subPanel.addComponent(keyLabel2)

      val keyName1 = new FontLabel(Settings.keyName(MainApplication.settings.player1Controls(i)), fontSize = 1.5F, fontColor = GUIConstants.COLOR_ACCENT)
      keyName1.percentSize = (GUIConstants.TEXTFIELD_HEIGHT/subW, GUIConstants.TEXTFIELD_HEIGHT/subH)
      keyName1.percentPosition = (0.23, 1.0+i*0.1)
      subPanel.addComponent(keyName1)
      keyNames1(i) = keyName1

      val keyName2 = new FontLabel(Settings.keyName(MainApplication.settings.player2Controls(i)), fontSize = 1.5F, fontColor = GUIConstants.COLOR_ACCENT)
      keyName2.percentSize = (GUIConstants.TEXTFIELD_HEIGHT/subW, GUIConstants.TEXTFIELD_HEIGHT/subH)
      keyName2.percentPosition = (0.78, 1.0+i*0.1)
      subPanel.addComponent(keyName2)
      keyNames2(i) = keyName2

      val keyEdit1 = new GrayButton("Edit", () => {
        choosingKey = true
        keyCallBack = Some((key) => {
          keyNames1(i).text = Settings.keyName(key)
          keyCodes1(i) = key
        })

        MainApplication.modalActionBox("Input a key", (dialog) => keyDialog = Some(dialog))
      })
      keyEdit1.percentSize = (GUIConstants.BUTTON_WIDTH/subW/2, GUIConstants.BUTTON_HEIGHT/subH/2)
      keyEdit1.percentPosition = (0.3, 1.025+i*0.1)
      subPanel.addComponent(keyEdit1)

      val keyEdit2 = new GrayButton("Edit", () => {
        choosingKey = true
        keyCallBack = Some((key) => {
          keyNames2(i).text = Settings.keyName(key)
          keyCodes2(i) = key
        })

        MainApplication.modalActionBox("Input a key", (dialog) => keyDialog = Some(dialog))
      })
      keyEdit2.percentSize = (GUIConstants.BUTTON_WIDTH/subW/2, GUIConstants.BUTTON_HEIGHT/subH/2)
      keyEdit2.percentPosition = (0.85, 1.025+i*0.1)
      subPanel.addComponent(keyEdit2)

    }


    val audioLabel = new FontLabel("A U D I O", fontSize = 2.3F, fontColor = GUIConstants.COLOR_HEADER)
    audioLabel.percentSize = (2*GUIConstants.TEXTFIELD_WIDTH/subW, GUIConstants.TEXTFIELD_HEIGHT/subH)
    audioLabel.percentPosition = (0.4, 1.5)
    subPanel.addComponent(audioLabel)

    val volumeLabel = new FontLabel("Volume", fontSize = 1.8F)
    volumeLabel.percentSize = (2*GUIConstants.TEXTFIELD_WIDTH/subW, GUIConstants.TEXTFIELD_HEIGHT/subH)
    volumeLabel.percentPosition = (0.4, 1.7)
    subPanel.addComponent(volumeLabel)

    def tickSound(volume: Int) = {
      formula.engine.SoundSystem.playSound(Sounds.Hover, volume)
    }

    val volumeSlider = new Slider(MainApplication.settings.volume, minValue = 0, maxValue = 100, majorSpacing = 10, onchange = tickSound)
    volumeSlider.percentSize = (0.8, GUIConstants.TEXTFIELD_HEIGHT/subH)
    volumeSlider.percentPosition = (0.05, 1.8)
    subPanel.addComponent(volumeSlider)



    //Navigation buttons

    val restoreButton = new GrayButton("Default", () => {
      if(MainApplication.confirmBox("This will restore default settings. Continue?")) {
        fpsValue.setText(Settings.defaultSettings.targetFramerate.toString)
        val success = FormulaIO.saveSettings(Settings.defaultSettings)
        if(success) {
          MainApplication.updateSettings(Settings.defaultSettings)
          MainApplication.messageBox("Settings saved succesfully")
          MainApplication.transition(new SettingsScreen)
        }

        else {
          MainApplication.messageBox("Failed to save settings")
        }
      }
    })
    restoreButton.percentPosition = (0.8, 0.6)
    components += restoreButton

    val saveButton = new GrayButton("Save changes", () => {

      var targetFramerate = math.abs(fpsValue.getText.trim.toIntOption.getOrElse(MainApplication.settings.targetFramerate))
      if(targetFramerate == 0) targetFramerate = MainApplication.settings.targetFramerate
      fpsValue.setText(targetFramerate.toString)


      val newSettings = Settings(resolutionDropdown.getSelectedIndex,
        screenDropdown.getSelectedIndex == 1, keyCodes1.toVector, keyCodes2.toVector, targetFramerate,
        effectsDropdown.getSelectedIndex == 0, volumeSlider.getValue)

      val success = FormulaIO.saveSettings(newSettings)

      if(success) {
        MainApplication.updateSettings(newSettings)
        MainApplication.messageBox("Settings saved succesfully")
      }
      else {
        MainApplication.messageBox("Failed to save settings")
      }

    })
    saveButton.percentPosition = (0.8, 0.7)
    components += saveButton

    val backButton = new GrayButton("Back", () => {

      var targetFramerate = math.abs(fpsValue.getText.trim.toIntOption.getOrElse(MainApplication.settings.targetFramerate))
      if(targetFramerate == 0) targetFramerate = MainApplication.settings.targetFramerate
      fpsValue.setText(targetFramerate.toString)

      val newSettings = Settings(resolutionDropdown.getSelectedIndex,
        screenDropdown.getSelectedIndex == 1, keyCodes1.toVector, keyCodes2.toVector, targetFramerate,
        effectsDropdown.getSelectedIndex == 0, volumeSlider.getValue)

      //Compare case classes
      if(newSettings != MainApplication.settings) {
        if(MainApplication.confirmBox("You have unsaved settings that will be discarded. Exit anyway?")) {
          MainApplication.transition(new MainMenuScreen)
        }
      }

      else {
        MainApplication.transition(new MainMenuScreen)
      }
    })
    backButton.percentPosition = (0.8, 0.8)
    components += backButton

  }


  override def handleKey(e: KeyEvent) = {

    if(e.getID == KeyEvent.KEY_PRESSED && choosingKey) {
      choosingKey = false
      keyDialog.foreach(_.dispose())
      keyCallBack.foreach(_(e.getKeyCode))
    }

  }

}