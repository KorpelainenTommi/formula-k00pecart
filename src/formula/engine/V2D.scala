package formula.engine
import formula.io._

/** General purpose 2D vector class, for positions, directions, velocities etc.
 *  Exposes common vector operations like dot product, distance, angle and rotation
 *  Defines algebraic operators for vector calculations
 *
 * @param x X value as a Double
 * @param y Y value as a Double
 */
case class V2D(x: Double, y: Double) {
  private def rnd(d: Double) = V2D.rnd(d)

  def +(v: V2D)              = V2D(x + v.x, y + v.y)
  def -(v: V2D)              = V2D(x - v.x, y - v.y)
  def *(m: Double)           = V2D(m * x, m * y)
  def /(m: Double)           = V2D(x / m, y / m)
  def unary_-                = V2D(-x, -y)

  def length                 = math.sqrt(lengthSqr)
  def lengthR                = rnd(length)
  def lengthSqr              = x * x + y * y
  def normalized             = this / length

  def dot(v: V2D)            = V2D.dot(this, v)
  def dist(v: V2D)           = V2D.dist(this, v)
  def distR(v: V2D)          = V2D.distR(this, v)
  def distSqr(v: V2D)        = V2D.distSqr(this, v)

  def ang(v: V2D)            = V2D.ang(this, v)
  def angDeg(v: V2D)         = V2D.angDeg(this, v)

  //positive is clockwise
  def rot(d: Double)         = V2D(math.cos(d) * x - math.sin(d) * y, math.sin(d) * x + math.cos(d) * y)
  def rotDeg(d: Double)      = rot(math.Pi * d / 180.0)

  def lowerThan(v: V2D)      = V2D.lowerThan(this, v)
  def higherThan(v: V2D)     = V2D.higherThan(this, v)

  def changeBasis(origin: V2D, xDir: V2D, yDir: V2D) = V2D.changeBasis(this, origin, xDir, yDir)

  override def toString      = s"($x, $y)"
}

object V2D extends Serializer[V2D] {
  val r = V2D( 1, 0)
  val l = V2D(-1, 0)
  val u = V2D( 0,-1)
  val d = V2D( 0, 1)
  private def rnd(d: Double) = math.round(d: Double).toInt

  def apply(dim: java.awt.Dimension): V2D = V2D(dim.getWidth, dim.getHeight)
  def apply(p: java.awt.Point): V2D       = V2D(p.getX, p.getY)

  implicit def toDim(v: V2D)              = new java.awt.Dimension(rnd(v.x), rnd(v.y))
  implicit def toPoint(v: V2D)            = new java.awt.Point(rnd(v.x), rnd(v.y))

  def dot(a: V2D, b: V2D)                 = a.x * b.x + a.y * b.y
  def dist(a: V2D, b: V2D)                = (a - b).length
  def distR(a: V2D, b: V2D)               = (a - b).lengthR
  def distSqr(a: V2D, b: V2D)             = (a - b).lengthSqr

  def ang(a: V2D, b: V2D)                 = math.acos(dot(a, b) / (a.length * b.length))
  def angDeg(a: V2D, b: V2D)              = 180 * ang(a, b) / math.Pi
  def lowerThan(a: V2D, b: V2D)           = a.x <= b.x && a.y <= b.y
  def higherThan(a: V2D, b: V2D)          = a.x >= b.x && a.y >= b.y

  /**
   * Represents the target vector in a vector basis defined by origin, x and y
   * by giving the scalars t and u for x and y respectively by solving the equation
   * target = origin + t * xDir + u * yDir
   * @param target The target vector to represent
   * @param origin The origin of the new vector space
   * @param xDir the positive horizontal direction of the new vector space
   * @param yDir the positive vertical direction of the new vector space
   * @return V2D containing (t, u)
   */
  def changeBasis(target: V2D, origin: V2D, xDir: V2D, yDir: V2D) = {
    val d = xDir.x * yDir.y - xDir.y * yDir.x
    val dx = target.x - origin.x
    val dy = target.y - origin.y
    V2D((yDir.y * dx - yDir.x * dy) / d, (-xDir.y * dx + xDir.x * dy)/d)
  }

  /** Locates the index of a point in a collection,
   * that is closest to a target point
   * @param point Target point to compare to
   * @param points Collection of points to find the index from
   * @return
   */
  def locateIndex(point: V2D, points: IndexedSeq[V2D]) = {
    var closest, i = 0
    var sqrD, dist = Double.PositiveInfinity
    while(i < points.length) {
      sqrD = point distSqr points(i)
      if(sqrD < dist) {
        dist = sqrD
        closest = i
      }
      i += 1
    }
    closest
  }

  /** Locate the point in a collection, that is closest to a target point
   *
   * @param point Target point to compare to
   * @param points Collection of points to search
   * @return
   */
  def locate(point: V2D, points: IndexedSeq[V2D]) = {
    points(V2D.locateIndex(point, points))
  }


  override def save(saveable: V2D) = FormulaIO.saveDouble(saveable.x) ++ FormulaIO.saveDouble(saveable.y)
  override def load(bytes: Array[Byte], start: Int) = V2D(FormulaIO.loadDouble(bytes, start), FormulaIO.loadDouble(bytes, start+8))

}