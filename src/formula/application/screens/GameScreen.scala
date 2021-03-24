package formula.application.screens
import java.awt.event.KeyEvent
import formula.engine.render._
import formula.application._
import formula.engine._
import formula.io._


class GameScreen(val track: Track) extends Screen with TextureLoader {

  override protected def textures = Textures.GAME_TEXTURES


  val game = new Game

  val mainRenderTarget = new BaseRenderTarget(() => game.gameUpdate())
  mainRenderTarget.percentBounds = (0, 0, 1, 1)

  mainRenderTarget.addSubTarget(new GameRenderTarget(game))
  mainRenderTarget.addSubTarget(new GameRenderTarget(game))

  val _panel = new RenderPanel(mainRenderTarget)
  override def panel = _panel

  override def activate() = {
    super.activate()
    loadTextures()

    panel.percentBounds = (0, 0, 1, 1)
    redraw()
    game.beginGameLoop(() => {panel.repaint()})
  }

  override def deactivate() = {
    super.deactivate()
    FormulaIO.unloadAllTextures()
  }

  override def redraw() = {
    panel.updateBounds(MainApplication.windowWidth, MainApplication.windowHeight)
    super.redraw()
  }

  override def handleKey(e: KeyEvent) = {

    if(e.getID == KeyEvent.KEY_PRESSED && e.getKeyCode == KeyEvent.VK_ESCAPE) {
      MainApplication.transition(new MainMenuScreen)
    }

  }

}