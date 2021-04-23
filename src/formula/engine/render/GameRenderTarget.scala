package formula.engine.render
import formula.engine._
import formula.application.{GUIConstants, MainApplication}
import formula.io._

import java.awt._
class GameRenderTarget(val game: Game, playerNumber: Int) extends RenderTarget {

  private val playerCamera = game.players(playerNumber).camera

  private val grass = new GrassRenderTarget
  grass.percentPosition = (0, 0.32)
  grass.percentSize = (1, 0.68)
  subTargets += grass

  private val road = new RoadRenderTarget(playerCamera, game.track)
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

  val gearText = new TextRenderTarget("GEAR", fontSize = 26, fontColor = Color.BLACK)
  protected val gearTextHeight = 0.08
  subTargets += gearText

  val mainText = new InfoRenderTarget(fontsize = 7, infoFunction = () => game.screenText(playerNumber))
  mainText.percentBounds = (0.1, 0.25, 0.8, 0.2)
  subTargets += mainText

  val timeText = new InfoRenderTarget(textfont = Fonts.TimesNewRoman, fontsize = 10, infoFunction = () => game.clockTime)
  timeText.percentBounds = (0.3, 0.025, 0.4, 0.1)
  subTargets += timeText

  val lapText = new InfoRenderTarget(textfont = Fonts.TimesNewRoman, fontsize = 6, fontcolor = Color.BLACK, infoFunction = () => game.lap(playerNumber))
  lapText.percentBounds = (0.3, 0.1, 0.4, 0.1)
  subTargets += lapText


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