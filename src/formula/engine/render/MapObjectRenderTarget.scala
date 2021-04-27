package formula.engine.render
import java.awt._
import formula.io._
import formula.engine._

class MapObjectRenderTarget(val game: Game, val camera: Camera) extends RenderTarget {


  //For normalizing sprites
  private var screenRatio = 1D

  override def updateBounds(width: Double, height: Double, xOffset: Int, yOffset: Int): Unit = {
    super.updateBounds(width, height, xOffset, yOffset)
    screenRatio = 1D * absoluteBounds.width / absoluteBounds.height
  }

  override protected def personalRender(g: Graphics2D) = {

    val renderables = game.track.mapObjects.filter(obj => camera.cameraSees(obj.position))

    //Distance sort, then render
    renderables.map(obj => (obj, camera.translateSpriteNormalized(obj, screenRatio))).
      filter(_._2.nonEmpty).map(t => (t._1, t._2.get)).sortBy(_._2._2.y).foreach(t => {

        val (obj, bounds) = t
        val x = bounds._1.x
        val y = bounds._1.y
        val w = bounds._2.x - x
        val h = bounds._2.y - y

        g.drawImage(FormulaIO.getTexture(obj.texture),
          math.round(absoluteBounds.x + absoluteBounds.width * x).toInt,
          math.round(absoluteBounds.y + absoluteBounds.height * y).toInt,
          math.round(absoluteBounds.width * w).toInt,
          math.round(absoluteBounds.height * h).toInt, null)

      })

  }

}