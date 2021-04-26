package formula.engine
import formula.io.Textures._


trait Sprite {
  def scale: Double
  def texture: Texture
  def position: V2D
  def spriteRatio: Double //Sprite aspect ratio as height / width
}

object Camera {

  //SCAN_DIST is the length of the camera frustrum
  //MAX_SCAN_WIDTH is the width at the end of the frustrum
  //MIN_SCAN_WIDTH is the width at the start of the frustrum

  val MAX_SCAN_WIDTH = 600.0D
  val MIN_SCAN_WIDTH = 6.0D
  val SCAN_DIST = 380.0D

  //CAMERA_FOV_ANGLE defines the vertical field of view
  //CAMERA_HEIGHT is the height off the ground
  //CAMERA_ANGLE is the vertical angle of the camera (0 points at the ground, 90 to the horizon, 180 to the sky)

  val CAMERA_FOV_ANGLE = math.Pi * (110.0D / 180D)
  val CAMERA_HEIGHT = 12.0D
  val CAMERA_ANGLE = math.Pi * (70.0D / 180D)

  //Precalculate some values, that will be constant in camera transformations
  val CAMERA_THETA = CAMERA_ANGLE - CAMERA_FOV_ANGLE / 2
  val CAMERA_COMPLEMENT = (math.Pi - CAMERA_FOV_ANGLE) / 2

  val CAMERA_CONSTANT1 = math.sin(CAMERA_FOV_ANGLE) / math.sin(CAMERA_COMPLEMENT)
  val CAMERA_CONSTANT2 = math.sin(CAMERA_THETA) / (Camera.CAMERA_CONSTANT1 * math.sin(Camera.CAMERA_COMPLEMENT - CAMERA_THETA))


  //Linear interpolation
  def lerp(min: Double, max: Double, t: Double): Double = min + t * (max - min)
  def lerp(t: Double): Double = lerp(MIN_SCAN_WIDTH, MAX_SCAN_WIDTH, t)

}

/** A pseudo-3D rectilinear perspective camera.
 *  This is a simplified model that assumes all objects (except the camera itself)
 *  are located at ground level
 *
 */
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


  //Calculate the bounding box for the camera in one pass
  private def updateBoundingBox() = {

    val corners = Vector(
      position - scanPerpendicular * (Camera.MIN_SCAN_WIDTH/2),
      position + scanPerpendicular * (Camera.MIN_SCAN_WIDTH/2),
      position + scanVector - scanPerpendicular * (Camera.MAX_SCAN_WIDTH/2),
      position + scanVector + scanPerpendicular * (Camera.MAX_SCAN_WIDTH/2)
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

  def cameraSees(pos: V2D) = (pos higherThan _boundingBoxCorner1) && (pos lowerThan _boundingBoxCorner2)


  /** Translate a sprite's world position, to relative screen coordinates from (0, 0) to (1, 1)
   * Also checks if the sprite is offscreen, so it isn't unnecessarily rendered.
   * In some cases, we can decide this very quickly (e.g. sprite is behind the camera)
   * but in others, the full algorithm must be run to know this.
   *
   * @param sp The sprite to translate
   * @return Some(V2D) containing the relative x and y in screen coordinates, None if the sprite is offscreen
   */
  def translateSprite(sp: Sprite) = {

    val translation = V2D.changeBasis(sp.position, position, scanPerpendicular, scanVector)

    if(translation.y < 0) None //Sprite is behind the camera
    else {
      val dist = translation.y * Camera.SCAN_DIST
      val yAngle = math.atan(dist / Camera.CAMERA_HEIGHT) - Camera.CAMERA_THETA

      if(yAngle < 0) None //Sprite is under the camera field of view
      else {
        val scanWidth = Camera.lerp(translation.y)
        val xOffset = translation.x / scanWidth + 0.5D
        val spriteWidth = sp.scale / scanWidth
        val spriteHeight = sp.spriteRatio * spriteWidth
        val yRatio = math.sin(yAngle) / math.sin(Camera.CAMERA_FOV_ANGLE - yAngle)
        val y = yRatio / (yRatio + 1)

        val x1 = xOffset - 0.5 * spriteWidth
        val x2 = xOffset + 0.5 * spriteWidth
        val y1 = 1 - y - spriteHeight
        val y2 = 1 - y

        //Sprite bounds are offscreen
        if(x1 >= 1 || x2 <= 0 || y1 >= 1 || y2 <= 0) None
        else Some((V2D(x1, y1), V2D(x2, y2)))
      }
    }

  }

  /** When a sprite is translated to screen coordinates, it gives us relative coordinates
   * for a screen (or the drawing area of a RenderTarget). However, if the target area isn't square,
   * the sprite will be stretched, which isn't ideal.
   * In order to preserve the sprite's aspect ratio, it needs to be normalized.
   * This requires information about the target area's aspect ratio.
   *
   * @param sp Sprite to translate and normalize
   * @param screenRatio The aspect ratio of the drawing area, used for normalizing
   * @return
   */
  def translateSpriteNormalized(sp: Sprite, screenRatio: Double) = {

    val relativeBounds = translateSprite(sp)
    relativeBounds.map(bounds => {
      val x1 = bounds._1.x
      val x2 = bounds._2.x
      val y2 = bounds._2.y
      val h = y2 - bounds._1.y
      val y1 = y2 - h * screenRatio
      (V2D(x1, y1), V2D(x2, y2))
    })

  }

  /** Translate a point in world space, to relative screen coordinates.
   * This also maps points outside the camera frustrum, meaning that it can give positions
   * outside of the (0, 0) (1, 1) range.
   *
   * @param pos Point in world space to translate
   * @return The relative screen coordinates of the point
   */
  def translatePoint(pos: V2D) = {

    val translation = V2D.changeBasis(pos, position, scanPerpendicular, scanVector)

    if(translation.y < 0) {
      val scanWidth = Camera.MIN_SCAN_WIDTH
      val x = translation.x / scanWidth + 0.5D
      V2D(x, 1 + Camera.CAMERA_CONSTANT2)
    }

    else {
      val dist = translation.y * Camera.SCAN_DIST
      val yAngle = math.atan(dist / Camera.CAMERA_HEIGHT) - Camera.CAMERA_THETA
      val scanWidth = Camera.lerp(translation.y)
      val x = translation.x / scanWidth + 0.5D

      if(yAngle < 0) {
        val yRatio = math.sin(-yAngle) / (Camera.CAMERA_CONSTANT1 * math.sin(Camera.CAMERA_COMPLEMENT + yAngle))
        val y = 1 + yRatio
        V2D(x, y)
      }
      else {
        val yRatio = math.sin(yAngle) / math.sin(Camera.CAMERA_FOV_ANGLE - yAngle)
        val y = yRatio / (yRatio + 1)
        V2D(x, 1 - y)
      }
    }

  }

}