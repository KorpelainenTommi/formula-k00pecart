package formula.application.screens
import formula.engine.TrackPreview
import formula.application._
import formula.io._

class TrackSelectScreen extends StaticScreen(Textures.Background_Generic, Textures.Button) {

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
    backButton.percentPosition = (0.8, 0.8)
    components += backButton

    val trackPreview = new TrackPreviewPanel
    trackPreview.percentBounds = (0.55, 0.05, 0.4, 0.7)
    components += trackPreview

    val trackImages = new ImageDisplayArea(trackPreviews.map(_.previewImage), index => {
      trackPreview.updatePreview(trackPreviews(index))
    })
    trackImages.percentBounds = (0.025, 0.13, 0.5, 0.6)
    components += trackImages
  }

}