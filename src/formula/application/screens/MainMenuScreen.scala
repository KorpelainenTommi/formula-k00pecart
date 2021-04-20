package formula.application.screens
import java.awt.event.KeyEvent
import formula.application._
import formula.io._

class MainMenuScreen extends StaticScreen(Textures.Background_Mainmenu, Textures.Button) {

  protected override def createComponents() = {

    val raceButton = new GrayButton("Race", () => MainApplication.transition(new TrackSelectScreen(TrackSelectScreen.Race)))
    raceButton.percentPosition = (0.55, 0.05)
    components += raceButton

    val trackToolButton = new GrayButton("Track tool", () => MainApplication.transition(new TrackSelectScreen(TrackSelectScreen.TrackTool)))
    trackToolButton.percentPosition = (0.6, 0.15)
    components += trackToolButton

    val settingsButton = new GrayButton("Settings", () => MainApplication.transition(new SettingsScreen))
    settingsButton.percentPosition = (0.65, 0.25)
    components += settingsButton

    val exitButton = new GrayButton("Exit the game", () => MainApplication.close())
    exitButton.percentPosition = (0.7, 0.35)
    components += exitButton

    val versionLabel = new FontLabel("ver2.0 Tommi Korpelainen", fontColor = java.awt.Color.BLACK)
    versionLabel.percentBounds = (0.34, 0.88, 0.2, 0.05)
    components += versionLabel

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