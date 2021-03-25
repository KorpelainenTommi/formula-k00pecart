package formula.engine.render
import formula.engine._
import java.awt.Graphics2D
import formula.io._

class PlayerRenderTarget(private val players: Vector[Player], private val camera: Camera) extends RenderTarget {

  override def personalRender(g: Graphics2D): Unit = {

    val closest = (V2D(1,1), V2D(1,1))
    val renderables = players.map(player => {
      val spriteBounds = camera.translateSprite(player)
      (player, spriteBounds)
    }).sortBy(_._2.getOrElse(closest)._2.y)

    renderables.foreach(r => {

      r._2.foreach(bounds => {

      val x = bounds._1.x
      val y = bounds._1.y
      val w = bounds._2.x - x
      val h = bounds._2.y - y



      val width = absoluteBounds.width
      val height = absoluteBounds.height
      val playerTexture = r._1.dirTexture(camera.scanVector.normalized, camera.scanPerpendicular)

      g.drawImage(FormulaIO.getTexture(playerTexture), math.round(absoluteBounds.x + width * x).toInt, math.round(absoluteBounds.y + height * y).toInt,
        math.round(absoluteBounds.width * w).toInt, math.round(absoluteBounds.height * h).toInt, null)
      })

    })
  }
}