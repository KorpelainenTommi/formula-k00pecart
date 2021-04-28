package formula.engine.render
import formula.engine._
import formula.io._
import java.awt._

/** A RenderTarget that renders one view of the game,
 * from the perspective of one player
 *
 * @param game The game to render
 * @param playerNumber The player to follow
 */
class GameRenderTarget(val game: Game, val playerNumber: Int) extends RenderTarget {


  protected val playerCamera = game.players(playerNumber).camera

  //Ground
  protected val grass = new GrassRenderTarget
  grass.percentPosition = (0, 0.32)
  grass.percentSize = (1, 0.68)
  subTargets += grass

  //Road
  protected val road = new RoadRenderTarget(playerCamera, game.track)
  road.percentBounds = (0, 0, 1, 1)
  subTargets += road

  //Sky
  protected val sky = new SkyRenderTarget
  sky.percentSize = (1, 0.33)
  subTargets += sky

  //Objects
  protected val mapObjects = new MapObjectRenderTarget(game, playerCamera)
  mapObjects.percentBounds = (0, 0, 1, 1)
  subTargets += mapObjects

  //Cars
  protected val players = new PlayerRenderTarget(game.players, playerCamera)
  players.percentBounds = (0, 0, 1, 1)
  subTargets += players

  //Effects
  protected val animations = new AnimatedSpriteRenderTarget(game, playerCamera)
  animations.percentBounds = (0, 0, 1, 1)
  subTargets += animations



  //Hud elements will have their positions set with more specific rules
  protected val map = new MapRenderTarget(game)
  protected val mapSize = 0.18
  subTargets += map

  protected val playerGear = new GearRenderTarget(game.players(playerNumber))
  protected val gearSize = 0.12
  subTargets += playerGear

  protected val gearText = new TextRenderTarget("GEAR", fontSize = 26, fontColor = Color.BLACK)
  protected val gearTextHeight = 0.08
  subTargets += gearText


  protected val mainText = new InfoRenderTarget(fontsize = 7,
    infoFunction = () => game.screenText(playerNumber))

  mainText.percentBounds = (0.1, 0.25, 0.8, 0.2)
  subTargets += mainText



  protected val timeText = new InfoRenderTarget(textfont = Fonts.TimesNewRoman,
    fontsize = 10, infoFunction = () => game.clockTime)

  timeText.percentBounds = (0.3, 0.025, 0.4, 0.1)
  subTargets += timeText


  protected val lapText = new InfoRenderTarget(textfont = Fonts.TimesNewRoman, fontsize = 6,
    fontcolor = Color.BLACK, infoFunction = () => game.lap(playerNumber))

  lapText.percentBounds = (0.3, 0.1, 0.4, 0.1)
  subTargets += lapText

  protected val playerNameText = new TextRenderTarget(game.playerNames(playerNumber), fontSize = 8)
  playerNameText.percentBounds = (0.75, 0.025, 0.2, 0.1)
  subTargets += playerNameText



  //GameRenderTarget isn't abstract, so it needs to implement this
  override protected def personalRender(g: Graphics2D): Unit = {}


  override def updateBounds(width: Double, height: Double, xOffset: Int, yOffset: Int): Unit = {

    //Adjust the positions and sizes of hud elements for single player vs two-player
    val screenFactor =  (pW * width) / (pH * height)
    map.percentPosition = (0.025, 0.025)
    map.percentSize = (mapSize, mapSize * screenFactor)
    playerGear.percentPosition = (0.03, 0.78 - (gearSize * screenFactor - gearSize))
    playerGear.percentSize = (gearSize, gearSize * screenFactor)
    gearText.percentPosition = (0.03, 0.9 - (gearTextHeight * screenFactor - gearTextHeight))
    gearText.percentSize = (gearSize, gearTextHeight * screenFactor)

    super.updateBounds(width, height, xOffset, yOffset)
  }


}