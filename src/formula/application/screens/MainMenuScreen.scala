package formula.application.screens

import formula.application.{FontLabel, GrayButton, MainApplication, StaticScreen}
import formula.io._

import java.awt.event.KeyEvent
class MainMenuScreen extends StaticScreen("background0.png", Textures.Button) {

  var exitButton: GrayButton      = null
  var raceButton: GrayButton      = null
  var settingsButton: GrayButton  = null
  var trackToolButton: GrayButton = null
  var versionLabel: FontLabel   = null

  override def activate() = {
    super.activate()

    raceButton = new GrayButton("Race")
    raceButton.setPercentBounds(0.55, 0.05, 0.14, 0.07)
    panel.add(raceButton)

    trackToolButton = new GrayButton("Track tool", () => MainApplication.transition(new TrackSelectScreen))
    trackToolButton.setPercentBounds(0.6, 0.15, 0.14, 0.07)
    panel.add(trackToolButton)

    settingsButton = new GrayButton("Settings", () => MainApplication.transition(new ResultScreen))
    settingsButton.setPercentBounds(0.65, 0.25, 0.14, 0.07)
    panel.add(settingsButton)

    exitButton = new GrayButton("Exit the game", () => MainApplication.close())
    exitButton.setPercentBounds(0.7, 0.35, 0.14, 0.07)
    panel.add(exitButton)

    versionLabel = new FontLabel("ver1.0 Tommi Korpelainen", fontColor = java.awt.Color.BLACK)
    versionLabel.setPercentBounds(0.34, 0.88, 0.2, 0.05)
    panel.add(versionLabel)

    redraw()
  }

  override def redraw() = {
    raceButton.updateBounds(MainApplication.windowWidth, MainApplication.windowHeight)
    trackToolButton.updateBounds(MainApplication.windowWidth, MainApplication.windowHeight)
    settingsButton.updateBounds(MainApplication.windowWidth, MainApplication.windowHeight)
    exitButton.updateBounds(MainApplication.windowWidth, MainApplication.windowHeight)
    versionLabel.updateBounds(MainApplication.windowWidth, MainApplication.windowHeight)

    super.redraw()
  }


  override def handleKey(e: KeyEvent): Unit = {
    if(e.getID == KeyEvent.KEY_PRESSED && e.getKeyCode == KeyEvent.VK_F11) {
      MainApplication.maximize()
    }

    if(e.getID == KeyEvent.KEY_PRESSED && e.getKeyCode == KeyEvent.VK_F12) {
      MainApplication.normalize()
    }
  }

}