package formula.application
import formula.io._

class ResultScreen extends StaticScreen("results0.png", Textures.Button) {



  override def activate() = {
    super.activate()



    redraw()
  }

  override def redraw() = {
    MainApplication.topWindow.revalidate()
    MainApplication.topWindow.repaint()
  }
}