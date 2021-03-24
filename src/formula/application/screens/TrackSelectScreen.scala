package formula.application.screens
import formula.engine.TrackPreview
import formula.application._
import formula.io._

object TrackSelectScreen {

  sealed trait Mode
  final object Debug extends TrackSelectScreen.Mode
  final object Race extends TrackSelectScreen.Mode
  final object TrackTool extends TrackSelectScreen.Mode

}

class TrackSelectScreen(val purpose: TrackSelectScreen.Mode = TrackSelectScreen.Debug) extends StaticScreen(Textures.Background_Generic, Textures.Button) {

  var trackfileNames: Vector[String] = Vector()
  var trackPreviews: Vector[TrackPreview] = Vector()

  override def activate() = {

    trackfileNames = FormulaIO.listTrackFiles
    trackPreviews = trackfileNames.flatMap(FormulaIO.loadTrackPreview)

    super.activate()
  }

  protected override def createComponents(): Unit = {

    val screenTitle = new FontLabel("Select a track", fontSize = 2F)
    screenTitle.percentSize = (GUIConstants.TEXTFIELD_WIDTH*2, GUIConstants.TEXTFIELD_HEIGHT*1.5)
    screenTitle.percentPosition = (0.05, 0.025)
    components += screenTitle

    val backButton = new GrayButton("Back", () => MainApplication.transition(new MainMenuScreen))
    backButton.percentPosition = (0.8, 0.85)
    components += backButton

    if(purpose == TrackSelectScreen.TrackTool) {

      val editButton = new GrayButton("Edit", () => {/*Transition to tracktool in edit mode*/})
      editButton.percentPosition = (0.25, 0.85)
      components += editButton

      val newButton = new GrayButton("New track", () => {/*Transition to tracktool in new mode*/})
      newButton.percentPosition = (0.05, 0.85)
      components += newButton
    }

    if(purpose == TrackSelectScreen.Race) {
      val raceButton = new GrayButton("Select track", () => {
        //Transition to player naming
        //For now, directly go to gamescreen
        MainApplication.transition(new GameScreen(null))

      })
      raceButton.percentPosition = (0.05, 0.85)
      components += raceButton
    }



    val trackPreview = new TrackPreviewPanel
    trackPreview.percentBounds = (0.55, 0.1, 0.4, 0.7)
    components += trackPreview

    val trackImages = new ImageDisplayArea(trackPreviews.map(_.previewImage), index => {
      trackPreview.updatePreview(trackPreviews(index))
    })
    trackImages.percentBounds = (0.025, 0.15, 0.5, 0.6)
    components += trackImages
  }

}