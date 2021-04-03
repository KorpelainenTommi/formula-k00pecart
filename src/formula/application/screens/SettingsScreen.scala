package formula.application.screens
import java.awt.event.KeyEvent
import formula.application._
import formula.io._

class SettingsScreen extends StaticScreen(Textures.Background_Generic, Textures.Button) {

  private var choosingKey = false
  private var keyDialog:   Option[javax.swing.JDialog] = None
  private var keyCallBack: Option[Int => Unit]         = None


  protected override def createComponents() = {

    val screenLabel = new FontLabel("Settings", fontSize = 2)
    screenLabel.percentSize = (GUIConstants.TEXTFIELD_WIDTH, GUIConstants.TEXTFIELD_HEIGHT*2)
    screenLabel.percentPosition = (0.8, 0.05)
    components += screenLabel

    val subW = 0.75
    val subH = 0.8

    val subPanel = new SubPanel(1.5)
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
    val keyCodes1 = MainApplication.settings.player1Controls.clone()

    val keyNames2 = Array.ofDim[FontLabel](Settings.defaultPlayer2Controls.length)
    val keyCodes2 = MainApplication.settings.player2Controls.clone()

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


    //Navigation buttons

    val saveButton = new GrayButton("Save changes", () => {
      val newSettings = Settings(resolutionDropdown.getSelectedIndex, screenDropdown.getSelectedIndex == 1, keyCodes1, keyCodes2)
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
      val newSettings = Settings(resolutionDropdown.getSelectedIndex, screenDropdown.getSelectedIndex == 1, keyCodes1, keyCodes2)
      val controlsChanged = !keyCodes1.sameElements(MainApplication.settings.player1Controls) || !keyCodes2.sameElements(MainApplication.settings.player2Controls)

      if(controlsChanged || newSettings.resolution != MainApplication.settings.resolution || newSettings.fullScreen != MainApplication.settings.fullScreen) {
        if(MainApplication.confirmBox("You have unsaved settings that will be discarded. Exit anyway?")) MainApplication.transition(new MainMenuScreen)
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