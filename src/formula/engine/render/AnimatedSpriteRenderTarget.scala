package formula.engine.render
import formula.engine._

import java.awt._
class AnimatedSpriteRenderTarget(val game: Game, val camera: Camera) extends RenderTarget {


  //For normalizing sprites
  private var screenRatio = 1D

  override def updateBounds(width: Double, height: Double, xOffset: Int, yOffset: Int): Unit = {
    super.updateBounds(width, height, xOffset, yOffset)
    screenRatio = 1D * absoluteBounds.width / absoluteBounds.height
  }

  override protected def personalRender(g: Graphics2D) = {

    val t = game.time

    AnimatedSprites.animating.foreach(s => {
      camera.translateSpriteNormalized(s, screenRatio).foreach(bounds => {

        val x = bounds._1.x
        val y = bounds._1.y
        val w = bounds._2.x - x
        val h = bounds._2.y - y

        g.drawImage(s.image(t),
          math.round(absoluteBounds.x + absoluteBounds.width * x).toInt,
          math.round(absoluteBounds.y + absoluteBounds.height * y).toInt,
          math.round(absoluteBounds.width * w).toInt,
          math.round(absoluteBounds.height * h).toInt, null)

      })
    })

    AnimatedSprites.despawnSprites(t)

  }

}