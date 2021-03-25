package formula.engine.render
import formula.engine._
import formula.io._

import java.awt._
class GameRenderTarget(val game: Game, playerNumber: Int) extends RenderTarget {

  private val grass = new GrassRenderTarget
  grass.percentPosition = (0, 0.25)
  grass.percentSize = (1, 0.75)
  subTargets += grass

  val sky = new SkyRenderTarget
  sky.percentSize = (1, 0.33)
  subTargets += sky

  val map = new MapRenderTarget(game)
  map.percentPosition = (0.025, 0.025)
  map.percentSize = (0.18, 0.18)
  subTargets += map

  private val playerCamera = game.player(playerNumber).camera

  val playerRenderTarget = new PlayerRenderTarget(game.players, playerCamera)
  playerRenderTarget.percentBounds = (0, 0, 1, 1)
  subTargets += playerRenderTarget


  override protected def personalRender(g: Graphics2D): Unit = {
    //Debug test


  }


}