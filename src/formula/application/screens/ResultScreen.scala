package formula.application.screens
import formula.io._
import formula.engine._
import formula.application._

class ResultScreen(track: Track, timeEntry: (Int, String)) extends StaticScreen(Textures.Background_Generic, Textures.Button) {

  protected override def createComponents() = {

    val screenTitle = new FontLabel("R E S U L T S", fontSize = 3F)
    screenTitle.percentSize = (GUIConstants.TEXTFIELD_WIDTH*2, GUIConstants.TEXTFIELD_HEIGHT*1.5)
    screenTitle.percentPosition = (0.4, 0.05)
    components += screenTitle


    val trackPreview = new TrackPreviewPanel
    trackPreview.percentBounds = (0.025, 0.2, 0.4, 0.7)
    components += trackPreview
    trackPreview.updatePreview(track)

    val raceButton = new GrayButton("Race again", () => MainApplication.transition(new TrackSelectScreen(TrackSelectScreen.Race)))
    raceButton.percentPosition = (0.6, 0.85)
    components += raceButton

    val backButton = new GrayButton("Back to menu", () => MainApplication.transition(new MainMenuScreen))
    backButton.percentPosition = (0.8, 0.85)
    components += backButton
  }

}