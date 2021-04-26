package formula.engine.render
import formula.engine._
import java.awt.Graphics2D
import formula.io._
import formula.application.MainApplication

//Render players from the perspective of one player camera
class PlayerRenderTarget(val players: Vector[Player], val camera: Camera) extends RenderTarget {

  //For normalizing sprites
  private var screenRatio = 1D

  override def updateBounds(width: Double, height: Double, xOffset: Int, yOffset: Int): Unit = {
    super.updateBounds(width, height, xOffset, yOffset)
    screenRatio = 1D * absoluteBounds.width / absoluteBounds.height
  }


  override def personalRender(g: Graphics2D): Unit = {

    val closest = (V2D(1,1), V2D(1,1))
    val renderables = players.map(player => {

      val spriteBounds = camera.translateSpriteNormalized(player, screenRatio)
      (player, spriteBounds)

    }).sortBy(_._2.getOrElse(closest)._2.y) //Complicated way to say, sort by distance to camera


    renderables.foreach(r => {
      r._2.foreach(bounds => {

        val x = bounds._1.x
        val y = bounds._1.y
        val w = bounds._2.x - x
        val h = bounds._2.y - y

        //A slight oversight, unlike other sprites, the player texture needs to change based on direction
        //So we use dirTexture instead of texture
        val playerTexture = r._1.dirTexture(camera.scanVector.normalized, camera.scanPerpendicular)

        playerTexture.foreach(tex => g.drawImage(FormulaIO.getTexture(tex),
          math.round(absoluteBounds.x + absoluteBounds.width * x).toInt,
          math.round(absoluteBounds.y + absoluteBounds.height * y).toInt,
          math.round(absoluteBounds.width * w).toInt,
          math.round(absoluteBounds.height * h).toInt, null))

      })
    })

  }

}