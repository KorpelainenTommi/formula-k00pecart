package formula.engine.render
import formula.engine._
import formula.application.MainApplication
import formula.io._

import java.awt._
class GameRenderTarget(val game: Game, playerNumber: Int) extends RenderTarget {

  private val playerCamera = game.players(playerNumber).camera

  private val grass = new GrassRenderTarget
  grass.percentPosition = (0, 0.32)
  grass.percentSize = (1, 0.68)
  subTargets += grass

  private val road = new RoadRenderTarget(playerCamera)
  road.percentBounds = (0, 0, 1, 1)
  subTargets += road

  val sky = new SkyRenderTarget
  sky.percentSize = (1, 0.33)
  subTargets += sky

  val playerRenderTarget = new PlayerRenderTarget(game.players, playerCamera)
  playerRenderTarget.percentBounds = (0, 0, 1, 1)
  subTargets += playerRenderTarget

  //Hud elements will have their positions set with more specific rules
  val map = new MapRenderTarget(game)
  protected val mapSize = 0.18
  subTargets += map

  val playerGear = new GearRenderTarget(game.players(playerNumber))
  protected val gearSize = 0.12
  subTargets += playerGear

  val gearText = new TextRenderTarget("GEAR", fontSize = 28, fontColor = Color.BLACK)
  protected val gearTextHeight = 0.06
  subTargets += gearText

  override protected def personalRender(g: Graphics2D): Unit = { }

  override def updateBounds(width: Double, height: Double, xOffset: Int, yOffset: Int): Unit = {

    val screenSize = MainApplication.settings.screenSize
    val screenFactor =  screenSize.x / screenSize.y * pW * width / MainApplication.windowWidth
    map.percentPosition = (0.025, 0.025)
    map.percentSize = (mapSize, mapSize * screenFactor)
    playerGear.percentPosition = (0.03, 0.78 - (gearSize * screenFactor - gearSize))
    playerGear.percentSize = (gearSize, gearSize * screenFactor)
    gearText.percentPosition = (0.03, 0.9 - (gearTextHeight * screenFactor - gearTextHeight))
    gearText.percentSize = (gearSize, gearTextHeight * screenFactor)
    super.updateBounds(width, height, xOffset, yOffset)
  }


}