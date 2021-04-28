package formula.application.screens
import java.awt.event.KeyEvent
import formula.engine.render._
import formula.application._
import formula.engine._
import formula.io._


class GameScreen
(track: Track,
 playerCount: Int,
 laps: Int,
 playerNames: Vector[String],
 playerAI: Vector[Boolean], musicFilename: Option[String] = None) extends Screen with TextureLoader with SoundLoader {

  override protected def textures = Textures.GAME_TEXTURES
  override protected def sounds = Sounds.GAME_SOUNDS


  val game = new Game(track, playerCount, laps, playerNames, playerAI, musicFilename: Option[String])

  //Create a splitscreen for game rendering
  val mainRenderTarget = new SplitscreenRenderTarget(() => game.gameUpdate())
  mainRenderTarget.percentBounds = (0, 0, 1, 1)
  for(i <- 0 until game.nOfPlayers) {
    mainRenderTarget.addSubTarget(new GameRenderTarget(game, i))
  }


  val _panel = new RenderPanel(mainRenderTarget)
  override def panel = _panel

  override def activate() = {
    super.activate()
    loadTextures()

    panel.percentBounds = (0, 0, 1, 1)
    redraw()
    game.beginGameLoop(() => panel.repaint())
  }

  override def deactivate() = {
    super.deactivate()
    FormulaIO.unloadAllTextures()
    FormulaIO.unloadAllSounds()
  }

  override def redraw() = {
    panel.updateBounds(MainApplication.windowWidth, MainApplication.windowHeight)
    super.redraw()
  }

  override def handleKey(e: KeyEvent) = {

    if(e.getID == KeyEvent.KEY_PRESSED && e.getKeyCode == KeyEvent.VK_ESCAPE) {
      MainApplication.transition(new MainMenuScreen)
    }

    if(e.getID == KeyEvent.KEY_PRESSED) {

      val player1Key = MainApplication.settings.player1Controls.indexWhere(_ == e.getKeyCode)
      val player2Key = MainApplication.settings.player2Controls.indexWhere(_ == e.getKeyCode)

      if(player1Key != -1) {
        game.input(0, player1Key, true)
      }
      if(player2Key != -1) {
        game.input(1, player2Key, true)
      }
    }

    if(e.getID == KeyEvent.KEY_RELEASED) {
      val player1Key = MainApplication.settings.player1Controls.indexWhere(_ == e.getKeyCode)
      val player2Key = MainApplication.settings.player2Controls.indexWhere(_ == e.getKeyCode)

      if(player1Key != -1) {
        game.input(0, player1Key, false)
      }
      if(player2Key != -1) {
        game.input(1, player2Key, false)
      }
    }

  }

}