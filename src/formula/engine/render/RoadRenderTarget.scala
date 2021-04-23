package formula.engine.render
import formula.engine._
import formula.io._
import java.awt._

class RoadRenderTarget(camera: Camera, track: Track) extends RenderTarget {

  private var roadTexturePaint: Option[TexturePaint] = None
  private var goalTexturePaint: Option[TexturePaint] = None
  //private var distanceGradient: Option[GradientPaint] = None
  private val startColor = new Color(0,69,19,240)
  private val endColor   = new Color(0,0,0,0)

  override def updateBounds(width: Double, height: Double, xOffset: Int, yOffset: Int): Unit = {

    super.updateBounds(width, height, xOffset, yOffset)
    roadTexturePaint = Some(new TexturePaint(FormulaIO.getTexture(Textures.Road), new Rectangle(0, 0, absoluteBounds.width/2, absoluteBounds.height/2)))
    goalTexturePaint = Some(new TexturePaint(FormulaIO.getTexture(Textures.Goal), new Rectangle(0, 0, absoluteBounds.width/2, absoluteBounds.height/2)))
    //distanceGradient = Some(new GradientPaint(absoluteBounds.x, absoluteBounds.y, startColor, absoluteBounds.x, absoluteBounds.y+absoluteBounds.height, endColor))
  }

  override def personalRender(g: Graphics2D): Unit = {

    val path = track.primaryPath
    val renderIndexes = path.indices.toArray//.filter(i => camera.cameraSees(path(i))).toArray

    val w = absoluteBounds.width
    val h = absoluteBounds.height
    val x = absoluteBounds.x
    val y = absoluteBounds.y

    /*
    g.setColor(Color.RED)

    renderIndexes.foreach(i => {

      val start = path(i)
      val end = path(i+1)
      val startPerp = path.perpendicular(i)
      val endPerp = path.perpendicular(i+1)

      val p1 = camera.translatePoint(start)
      val p2 = camera.translatePoint(end)

      g.drawLine(math.round(x + w * p1.x).toInt, math.round(y + h * p1.y).toInt, math.round(x + w * p2.x).toInt, math.round(y + h * p2.y).toInt)


    })*/



    roadTexturePaint.foreach(p => {
      g.setPaint(p)
      renderIndexes.foreach(i => {

        val start = path(i)
        val end = path(i+1)
        val startPerp = path.perpendicular(i)
        val endPerp = path.perpendicular(i+1)

        /*
        val startPoints = camera.translateLine(start, DEBUG_ROAD_WIDTH)
        val endPoints = camera.translateLine(end, DEBUG_ROAD_WIDTH)
        */

        val d = track.roadWidth / 2
        val p1 = camera.translatePoint(start - (startPerp * d))
        val p2 = camera.translatePoint(start + (startPerp * d))
        val p3 = camera.translatePoint(end - (startPerp * d))
        val p4 = camera.translatePoint(end + (startPerp * d))
        val p5 = camera.translatePoint(end - (endPerp * d))
        val p6 = camera.translatePoint(end + (endPerp * d))




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

    goalTexturePaint.foreach(p => {
      g.setPaint(p)

      val goalPos = path(0)
      val goalDir = path.directionNormalized(0)
      val goalPerp = path.perpendicular(0)
      val d = track.roadWidth / 2

      val perp = goalPerp * d
      val dir = goalDir * 3

      val p1 = camera.translatePoint(goalPos - perp - dir)
      val p2 = camera.translatePoint(goalPos + perp - dir)
      val p3 = camera.translatePoint(goalPos - perp + dir)
      val p4 = camera.translatePoint(goalPos + perp + dir)

      g.fillPolygon(
          Array(p1.x, p2.x, p4.x, p3.x).map(d => math.round(x + w * d).toInt),
          Array(p1.y, p2.y, p4.y, p3.y).map(d => math.round(y + h * d).toInt),
          4)

    })


/*
        g.fillPolygon(
          Array(startPoints._2.x, startPoints._1.x, endPoints._1.x, endPoints._2.x).map(d => math.round(x + w * d).toInt),
          Array(startPoints._2.y, startPoints._1.y, endPoints._1.y, endPoints._2.y).map(d => math.round(x + h * d).toInt),
          4
        )*/

        //g.fillRect(math.round(x + w * startPoints._1.x).toInt, math.round(y + h * startPoints._1.y).toInt, 6, 6)
        //g.fillRect(math.round(x + w * startPoints._2.x).toInt, math.round(y + h * startPoints._2.y).toInt, 6, 6)
        //g.fillRect(math.round(x + w * endPoints._1.x).toInt, math.round(y + h * endPoints._1.y).toInt, 6, 6)
        //g.fillRect(math.round(x + w * endPoints._2.x).toInt, math.round(y + h * endPoints._2.y).toInt, 6, 6)

        /*
        g.fillRect(math.round(x + w * p1.x).toInt, math.round(y + h * p1.y).toInt, 6, 6)
        g.fillRect(math.round(x + w * p2.x).toInt, math.round(y + h * p2.y).toInt, 6, 6)
        g.fillRect(math.round(x + w * p3.x).toInt, math.round(y + h * p3.y).toInt, 6, 6)
        g.fillRect(math.round(x + w * p4.x).toInt, math.round(y + h * p4.y).toInt, 6, 6)
        */

    /*roadTexturePaint.foreach(p => {
      g.setPaint(p)
      g.fillRect(absoluteBounds.x, absoluteBounds.y, absoluteBounds.width, absoluteBounds.height)
    })*/

  }
}