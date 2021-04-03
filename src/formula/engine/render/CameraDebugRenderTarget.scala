package formula.engine.render
import formula.engine._
import java.awt._
class CameraDebugRenderTarget(camera: Camera, lineColor: Color) extends RenderTarget {

  private def translatePos(pos: V2D) = {
    V2D(absoluteBounds.x + (absoluteBounds.width * pos.x / Track.TRACK_WIDTH),
    absoluteBounds.y + (absoluteBounds.height * pos.y / Track.TRACK_HEIGHT)
    )
  }

  override protected def personalRender(g: Graphics2D) = {

    val camPos = translatePos(camera.position)

    val camStartL = translatePos(camera.position - camera.scanPerpendicular * (Camera.MIN_SCAN_WIDTH/2))
    val camStartR = translatePos(camera.position + camera.scanPerpendicular * (Camera.MIN_SCAN_WIDTH/2))

    val camEndL = translatePos(camera.position + camera.scanVector - camera.scanPerpendicular * (Camera.MAX_SCAN_WIDTH/2))
    val camEndR = translatePos(camera.position + camera.scanVector + camera.scanPerpendicular * (Camera.MAX_SCAN_WIDTH/2))

    g.setStroke(new BasicStroke(2))
    g.setColor(lineColor)

    def drawL(pos1: V2D, pos2: V2D) = {
      g.drawLine(math.round(pos1.x).toInt, math.round(pos1.y).toInt, math.round(pos2.x).toInt, math.round(pos2.y).toInt)
    }

    drawL(camStartL, camStartR)
    drawL(camStartL, camEndL)
    drawL(camStartR, camEndR)
    drawL(camEndL, camEndR)


    /* Debug show Camera bounding box
    val c1 = translatePos(camera._boundingBoxCorner1)
    val c2 = translatePos(V2D(camera._boundingBoxCorner2.x, camera._boundingBoxCorner1.y))
    val c3 = translatePos(V2D(camera._boundingBoxCorner1.x, camera._boundingBoxCorner2.y))
    val c4 = translatePos(camera._boundingBoxCorner2)

    drawL(c1, c2)
    drawL(c1, c3)
    drawL(c3, c4)
    drawL(c2, c4)
    */


    g.setColor(Color.YELLOW)
    g.fillRect(math.round(camStartL.x).toInt, math.round(camStartL.y).toInt, 2, 2)
    g.fillRect(math.round(camStartR.x).toInt, math.round(camStartR.y).toInt, 2, 2)

    g.fillRect(math.round(camEndL.x).toInt, math.round(camEndL.y).toInt, 2, 2)
    g.fillRect(math.round(camEndR.x).toInt, math.round(camEndR.y).toInt, 2, 2)

  }
}

class PathDebugRenderTarget(lineColor: Color) extends RenderTarget {
  val path = formula.io.FormulaIO.loadDemoPath("test2.png")

  private def translatePos(pos: V2D) = {
    V2D(absoluteBounds.x + (absoluteBounds.width * pos.x / Track.TRACK_WIDTH),
    absoluteBounds.y + (absoluteBounds.height * pos.y / Track.TRACK_HEIGHT)
    )
  }

  override protected def personalRender(g: Graphics2D) = {


    def drawL(pos1: V2D, pos2: V2D) = {
      g.drawLine(math.round(pos1.x).toInt, math.round(pos1.y).toInt, math.round(pos2.x).toInt, math.round(pos2.y).toInt)
    }

    def drawP(pos: V2D) = {
      g.fillRect(math.round(pos.x).toInt, math.round(pos.y).toInt, 2, 2)
    }

    g.setStroke(new BasicStroke(2))
    g.setColor(lineColor)

    for(i <- path.indices) {
      drawL(translatePos(path(i)), translatePos(path(i+1)))
    }

    g.setColor(Color.GREEN)
    path.foreach(p => drawP(translatePos(p)))

  }

}