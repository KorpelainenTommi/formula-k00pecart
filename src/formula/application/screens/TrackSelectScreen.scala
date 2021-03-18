package formula.application.screens

import formula.application._
import formula.io._

import javax.swing._
class TrackSelectScreen extends StaticScreen("trackselect0.png", Textures.Button) {

  var backButton: GrayButton = null
  var trackPanel: JScrollPane = null
  var trackList: JList[String] = null
  var trackPreview: TrackPreviewPanel = null
  var testTBox: TextArea = null

  override def activate() = {
    super.activate()

    backButton = new GrayButton("Back", () => MainApplication.transition(new MainMenuScreen))
    backButton.setPercentBounds(0.8, 0.8, 0.14, 0.07)
    panel.add(backButton)

    trackPreview = new TrackPreviewPanel
    trackPreview.setPercentBounds(0.55, 0.05, 0.4, 0.7)
    panel.add(trackPreview)

    testTBox = new TextArea("Insert text here")
    testTBox.setPercentBounds(0.2, 0.4, 0.28, 0.28)
    panel.add(testTBox)


    trackList = new JList[String]()
    trackPanel = new JScrollPane(trackList)




    redraw()
  }

  override def redraw() = {
    backButton.updateBounds(MainApplication.windowWidth, MainApplication.windowHeight)
    trackPreview.updateBounds(MainApplication.windowWidth, MainApplication.windowHeight)
    testTBox.updateBounds(MainApplication.windowWidth, MainApplication.windowHeight)
    super.redraw()
  }


}