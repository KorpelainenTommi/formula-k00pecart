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

    //val camEnd = translatePos(camera.position + camera.scanVector * Camera.CAMERA_VANISH_LINEAR)

    val camEndL = translatePos(camera.position + camera.scanVector * Camera.CAMERA_VANISH_LINEAR - camera.scanPerpendicular * (Camera.lerp(Camera.CAMERA_VANISH_LINEAR)/2))
    val camEndR = translatePos(camera.position + camera.scanVector * Camera.CAMERA_VANISH_LINEAR + camera.scanPerpendicular * (Camera.lerp(Camera.CAMERA_VANISH_LINEAR)/2))

    g.setStroke(new BasicStroke(2))
    g.setColor(lineColor)

    def drawL(pos1: V2D, pos2: V2D) = {
      g.drawLine(math.round(pos1.x).toInt, math.round(pos1.y).toInt, math.round(pos2.x).toInt, math.round(pos2.y).toInt)
    }

    drawL(camStartL, camStartR)
    drawL(camStartL, camEndL)
    drawL(camStartR, camEndR)
    drawL(camEndL, camEndR)


    g.setColor(Color.YELLOW)
    g.fillRect(math.round(camStartL.x).toInt, math.round(camStartL.y).toInt, 2, 2)
    g.fillRect(math.round(camStartR.x).toInt, math.round(camStartR.y).toInt, 2, 2)

    g.fillRect(math.round(camEndL.x).toInt, math.round(camEndL.y).toInt, 2, 2)
    g.fillRect(math.round(camEndR.x).toInt, math.round(camEndR.y).toInt, 2, 2)

  }
}