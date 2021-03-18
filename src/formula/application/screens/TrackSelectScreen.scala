package formula.application.screens

import formula.application._
import formula.io._

import java.awt.image.BufferedImage
import javax.swing._
class TrackSelectScreen extends StaticScreen("screen0.png", Textures.Button) {

  var backButton: GrayButton = null
  var trackPanel: JScrollPane = null
  var trackList: JList[String] = null
  var trackPreview: TrackPreviewPanel = null
  var testTBox: TextArea = null
  var testDropdown: DropDown = null

  var testImageArray: ImageDisplayArea = null

  override def activate() = {
    super.activate()

    backButton = new GrayButton("Back", () => MainApplication.transition(new MainMenuScreen))
    backButton.setPercentBounds(0.8, 0.8, 0.14, 0.07)
    panel.add(backButton)

    trackPreview = new TrackPreviewPanel
    trackPreview.setPercentBounds(0.55, 0.05, 0.4, 0.7)
    panel.add(trackPreview)

    //testTBox = new TextArea("Insert text here")
    //testTBox.setPercentBounds(0.2, 0.4, 0.28, 0.28)
    //panel.add(testTBox)

    testImageArray = new ImageDisplayArea(Vector(new BufferedImage(128, 128, BufferedImage.TYPE_INT_ARGB)))
    testImageArray.setPercentBounds(0.2, 0.4, 0.28, 0.28)
    panel.add(testImageArray)

    testDropdown = new DropDown(Settings.resolutions.map(v => s"${v.x.toInt} x ${v.y.toInt}"))
    testDropdown.setPercentBounds(0.2, 0.15, 0.14, 0.07)
    panel.add(testDropdown)






    redraw()
  }

  override def redraw() = {
    backButton.updateBounds(MainApplication.windowWidth, MainApplication.windowHeight)
    trackPreview.updateBounds(MainApplication.windowWidth, MainApplication.windowHeight)
    //testTBox.updateBounds(MainApplication.windowWidth, MainApplication.windowHeight)
    testDropdown.updateBounds(MainApplication.windowWidth, MainApplication.windowHeight)
    testImageArray.updateBounds(MainApplication.windowWidth, MainApplication.windowHeight)
    super.redraw()
  }


}