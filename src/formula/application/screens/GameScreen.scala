package formula.application.screens

import formula.application.{MainApplication, RenderPanel, Screen, TextureLoader}
import formula.engine._
import formula.io._
class GameScreen extends Screen with TextureLoader {
  override protected def textures = Textures.GAME_TEXTURES

  val mainRenderTarget: RenderTarget = null
  val _panel = new RenderPanel(mainRenderTarget)
  override def panel = _panel

  override def activate() = {
    super.activate()
    loadTextures()

    panel.setPercentBounds(1, 1, 1, 1)
    redraw()
  }

  override def deactivate() = {
    super.deactivate()
    FormulaIO.unloadAllTextures()
  }

  override def redraw() = {
    panel.updateBounds(MainApplication.windowWidth, MainApplication.windowHeight)
    super.redraw()
  }

}