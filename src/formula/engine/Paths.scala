package formula.engine
import formula.io._

object ClosedPath extends Serializable[ClosedPath] {

  override def save(saveable: ClosedPath): Array[Byte] = FormulaIO.saveInt(saveable.length) ++ saveable._points.flatMap(V2D.save)
  override def load(bytes: Array[Byte], start: Int): ClosedPath = {
    val len = FormulaIO.loadInt(bytes, start)
    new ClosedPath(Array.tabulate(len)(i => V2D.load(bytes, start+4+i*8)))
  }

  //def apply(points: V2D*) = new ClosedPath(points)

}

object ClosedLoop {
  //def apply(points: V2D*) = new ClosedLoop(points)
}

class ClosedPath(points: IndexedSeq[V2D]) extends IndexedSeq[V2D] {
  protected val _points: Array[V2D] = points.toArray
  protected val _perpPoints = Array.tabulate(_points.length)(directionN(_).rotDeg(-90))
  override def length = _points.length
  override def iterator = _points.iterator
  override def apply(i: Int) = _points.apply(i)

  def direction(i: Int) = {
    if(i == length - 1) V2D(0,0)
    else _points(i+1) - _points(i)
  }

  def directionN(i: Int) = {
    if(direction(i) == V2D(0, 0)) V2D(0, 0)
    else direction(i).normalized
  }

  def perpendicular(i: Int) = _perpPoints(i)

  def locate(point: V2D) = {
    var closest, i = 0
    var sqrD, dist = Double.PositiveInfinity
    while(i < _points.length) {
      sqrD = point distSqr _points(i)
      if(sqrD < dist) {
        dist = sqrD
        closest = i
      }
      i += 1
    }
    _points(closest)
  }
}

class ClosedLoop(points: IndexedSeq[V2D]) extends ClosedPath(points) {
  override def apply(i: Int) = super.apply(i % length)
  override def direction(i: Int) = apply(i+1) - apply(i)
  override def perpendicular(i: Int): V2D = super.perpendicular(i % length)
}