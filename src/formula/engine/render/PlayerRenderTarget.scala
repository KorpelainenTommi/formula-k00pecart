package formula.engine.render
import formula.engine._
import java.awt.Graphics2D
import formula.io._
import formula.application.MainApplication

class PlayerRenderTarget(private val players: Vector[Player], private val camera: Camera) extends RenderTarget {

  //For normalizing sprite ratios
  protected var screenFactor = 1D

  override def updateBounds(width: Double, height: Double, xOffset: Int, yOffset: Int): Unit = {
    super.updateBounds(width, height, xOffset, yOffset)
    val screenSize = MainApplication.settings.screenSize
    screenFactor = screenSize.x / screenSize.y * absoluteBounds.width / MainApplication.windowWidth
  }

  override def personalRender(g: Graphics2D): Unit = {

    val closest = (V2D(1,1), V2D(1,1))
    val renderables = players.map(player => {
      val spriteBounds = camera.translateSprite(player)
      (player, spriteBounds)
    }).sortBy(_._2.getOrElse(closest)._2.y)

    renderables.foreach(r => {

      r._2.foreach(bounds => {

      val x = bounds._1.x
      val y = bounds._2.y
      val w = bounds._2.x - x
      val h = (y - bounds._1.y) * screenFactor



      val playerTexture = r._1.dirTexture(camera.scanVector.normalized, camera.scanPerpendicular)

      playerTexture.foreach(tex => g.drawImage(FormulaIO.getTexture(tex),
        math.round(absoluteBounds.x + absoluteBounds.width * x).toInt,
        math.round(absoluteBounds.y + absoluteBounds.height * (y - h)).toInt,
        math.round(absoluteBounds.width * w).toInt,
        math.round(absoluteBounds.height * h).toInt, null))
      })
    })
  }
}