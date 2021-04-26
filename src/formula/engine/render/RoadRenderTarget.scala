package formula.engine.render
import formula.engine._
import formula.io._
import java.awt._

//Render the road from the perspective of this camera
class RoadRenderTarget(camera: Camera, track: Track) extends RenderTarget {

  private var roadTexturePaint: Option[TexturePaint] = None
  private var goalTexturePaint: Option[TexturePaint] = None


  private val path = track.primaryPath
  private val renderIndexes = path.indices.toArray

  //Constants that don't need to be calculated every frame
  private val d = track.roadWidth / 2
  private val goalPos = path(0)
  private val goalDir = path.directionNormalized(0)
  private val goalPerp = path.perpendicular(0)
  private val perp = goalPerp * d
  private val dir = goalDir * 3


  override def updateBounds(width: Double, height: Double, xOffset: Int, yOffset: Int): Unit = {

    super.updateBounds(width, height, xOffset, yOffset)

    roadTexturePaint = Some(new TexturePaint(FormulaIO.getTexture(Textures.Road),
      new Rectangle(0, 0, absoluteBounds.width/2, absoluteBounds.height/2)))

    goalTexturePaint = Some(new TexturePaint(FormulaIO.getTexture(Textures.Goal),
      new Rectangle(0, 0, absoluteBounds.width/2, absoluteBounds.height/2)))

  }




  override def personalRender(g: Graphics2D): Unit = {

    val w = absoluteBounds.width
    val h = absoluteBounds.height
    val x = absoluteBounds.x
    val y = absoluteBounds.y

    roadTexturePaint.foreach(p => {
      g.setPaint(p)
      renderIndexes.foreach(i => {

        val start = path(i)
        val end = path(i+1)
        val startPerp = path.perpendicular(i)
        val endPerp = path.perpendicular(i+1)

        //Calculate the corners for road polygons

        val p1 = camera.translatePoint(start - (startPerp * d))
        val p2 = camera.translatePoint(start + (startPerp * d))
        val p3 = camera.translatePoint(end - (startPerp * d))
        val p4 = camera.translatePoint(end + (startPerp * d))
        val p5 = camera.translatePoint(end - (endPerp * d))
        val p6 = camera.translatePoint(end + (endPerp * d))


        //Draw three polygons for each road section:
        //1. A rectangle from section beginning to end
        //2. A triangle filling the left gap of this and the next rectangle
        //3. A triangle filling the right gap of this and the next rectangle

        g.fillPolygon(
          Array(p1.x, p2.x, p4.x, p3.x).map(d => math.round(x + w * d).toInt),
          Array(p1.y, p2.y, p4.y, p3.y).map(d => math.round(y + h * d).toInt),
          4)

        g.fillPolygon(
          Array(p3.x, p4.x, p5.x).map(d => math.round(x + w * d).toInt),
          Array(p3.y, p4.y, p5.y).map(d => math.round(y + h * d).toInt),
          3)

        g.fillPolygon(
          Array(p3.x, p4.x, p6.x).map(d => math.round(x + w * d).toInt),
          Array(p3.y, p4.y, p6.y).map(d => math.round(y + h * d).toInt),
          3)


      })
    })


    //Draw the goal line as a single polygon
    goalTexturePaint.foreach(p => {
      g.setPaint(p)

      val p1 = camera.translatePoint(goalPos - perp - dir)
      val p2 = camera.translatePoint(goalPos + perp - dir)
      val p3 = camera.translatePoint(goalPos - perp + dir)
      val p4 = camera.translatePoint(goalPos + perp + dir)

      g.fillPolygon(
          Array(p1.x, p2.x, p4.x, p3.x).map(d => math.round(x + w * d).toInt),
          Array(p1.y, p2.y, p4.y, p3.y).map(d => math.round(y + h * d).toInt),
          4)

    })

  }


}