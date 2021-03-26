package formula.engine
import formula.io.Textures._
import java.awt.Rectangle

trait Sprite {
  def scale: Double
  def texture: Texture
  def position: V2D
  def spriteRatio: Double
}

object Camera {

  val MAX_SCAN_WIDTH = 120.0D
  val MIN_SCAN_WIDTH = 6.0D
  val SCAN_DIST = 160.0D
  val CAMERA_VANISH = 0.47D
  val CAMERA_VANISH_LINEAR = 0.6855D

  /*
  val MAX_SCAN_WIDTH = 120.0D
  val MIN_SCAN_WIDTH = 6.0D
  val SCAN_DIST = 160.0D
  val CAMERA_VANISH = 0.47D
  val CAMERA_VANISH_LINEAR = 0.6855D
  */


  def lerp(min: Double, max: Double, t: Double): Double = min + t * (max - min)
  def lerp(t: Double): Double = lerp(MIN_SCAN_WIDTH, MAX_SCAN_WIDTH, t)

}

class Camera {

  private var _position = V2D(0, 0)
  private var _scanVector = V2D.u
  private var _scanPerpendicular = V2D.r

  private var _boundingBoxCorner1 = V2D(0, 0)
  private var _boundingBoxCorner2 = V2D(0, 0)


  def position = _position
  def scanVector = _scanVector
  def scanPerpendicular = _scanPerpendicular

  def position_=(value: V2D) = {
    _position = value
    updateBoundingBox()
  }

  def scanVector_=(value: V2D): Unit = {
    if(value == V2D(0,0)) scanVector = V2D.u
    else {
      _scanVector = value.normalized
      _scanPerpendicular = _scanVector.rotDeg(90)
      _scanVector *= Camera.SCAN_DIST
    }

    updateBoundingBox()
  }

  private def updateBoundingBox() = {

    val corners = Vector(
      position - scanPerpendicular * (Camera.MIN_SCAN_WIDTH/2),
      position + scanPerpendicular * (Camera.MIN_SCAN_WIDTH/2),
      position + scanVector * Camera.CAMERA_VANISH_LINEAR - scanPerpendicular * (Camera.lerp(Camera.CAMERA_VANISH_LINEAR)/2),
      position + scanVector * Camera.CAMERA_VANISH_LINEAR + scanPerpendicular * (Camera.lerp(Camera.CAMERA_VANISH_LINEAR)/2)
    )

    var maxX, maxY = Double.NegativeInfinity
    var minX, minY = Double.PositiveInfinity

    corners.foreach(c => {
      if(c.x < minX) minX = c.x
      if(c.x > maxX) maxX = c.x
      if(c.y < minY) minY = c.y
      if(c.y > maxY) maxY = c.y
    })

    _boundingBoxCorner1 = V2D(minX, minY)
    _boundingBoxCorner2 = V2D(maxX, maxY)
  }

  def translateSprite(sp: Sprite) = {
    val translation = V2D.changeBasis(sp.position, position, scanPerpendicular, scanVector)
    if(translation.y < 0) None
    else {
      val scanWidth = Camera.lerp(translation.y)
      val xOffset = translation.x / scanWidth + 0.5D

      //Calculate sprite bounds

      var vanish = (Camera.CAMERA_VANISH - translation.y * translation.y)
      if(vanish < 0) vanish = 0

      val spriteWidth = vanish * sp.scale / scanWidth
      val spriteHeight = sp.spriteRatio * spriteWidth

      val x1 = xOffset - 0.5 * spriteWidth
      val x2 = xOffset + 0.5 * spriteWidth
      val y = 0.33 + (Camera.CAMERA_VANISH_LINEAR - translation.y) / Camera.CAMERA_VANISH_LINEAR * 0.67
      val y1 = y - spriteHeight
      val y2 = y

      if(x1 >= 1 || x2 <= 0 || y1 >= 1 || y2 <= 0) None
      else Some((V2D(x1, y1), V2D(x2, y2)))
    }
  }

  def translatePoint(pos: V2D) = {
    val translation = V2D.changeBasis(pos, position, scanPerpendicular, scanVector)
    if(translation.y < 0) {
      if(translation.x > 0) V2D(1, 1)
      else V2D(0, 1)
    }
    else {

      var vanish = (Camera.CAMERA_VANISH - translation.y * translation.y)
      if(vanish < 0) vanish = 0

      val scanWidth = Camera.lerp(translation.y)
      val xOffset = translation.x / scanWidth + 0.5D

      V2D(xOffset, 0.33 + (Camera.CAMERA_VANISH_LINEAR - translation.y) / Camera.CAMERA_VANISH_LINEAR * 0.67)
    }
  }


  def cameraSees(pos: V2D) = (pos higherThan _boundingBoxCorner1) && (pos lowerThan _boundingBoxCorner2)


}