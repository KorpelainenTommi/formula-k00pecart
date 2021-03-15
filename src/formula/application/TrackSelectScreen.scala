package formula.application
import formula.io._
import javax.swing._
class TrackSelectScreen extends StaticScreen("trackselect0.png", Textures.Button) {

  var backButton: GrayButton = null
  var trackPanel: JScrollPane = null
  var trackList: JList[String] = null
  var trackPreview: TrackPreviewPanel = null

  override def activate() = {
    super.activate()

    backButton = new GrayButton("Back", () => MainApplication.transition(new MainMenuScreen))
    backButton.setPercentBounds(0.8, 0.8, 0.14, 0.07)
    panel.add(backButton)

    trackPreview = new TrackPreviewPanel
    trackPreview.setPercentBounds(0.55, 0.05, 0.4, 0.7)
    panel.add(trackPreview)

    trackList = new JList[String]()
    trackPanel = new JScrollPane(trackList)




    redraw()
  }

  override def redraw() = {
    backButton.updateBounds(MainApplication.windowWidth, MainApplication.windowHeight)
    trackPreview.updateBounds(MainApplication.windowWidth, MainApplication.windowHeight)
    MainApplication.topWindow.revalidate()
    MainApplication.topWindow.repaint()
  }


}