package formula.engine
import formula.io._

//A closed path is an ordered collection of points
//A closed loop is an ordered collection of points, where indexes loop


object ClosedPath extends Serializer[ClosedPath] {

  override def save(saveable: ClosedPath) = FormulaIO.saveInt(saveable.length) ++ saveable._points.flatMap(V2D.save)
  override def load(bytes: Array[Byte], start: Int) = {
    val len = FormulaIO.loadInt(bytes, start)
    new ClosedPath(Array.tabulate(len)(i => V2D.load(bytes, start+4+i*16)))
  }

}

class ClosedPath(points: IndexedSeq[V2D]) extends IndexedSeq[V2D] {
  protected val _points: Array[V2D] = points.toArray
  protected val _perpPoints = Array.tabulate(_points.length)(directionNormalized(_).rotDeg(90))
  override def length = _points.length
  override def iterator = _points.iterator
  override def apply(i: Int) = _points.apply(i)

  def direction(i: Int) = {
    if(i == length - 1) V2D(0,0)
    else _points(i+1) - _points(i)
  }

  def directionNormalized(i: Int) = {
    if(direction(i) == V2D(0, 0)) V2D(0, 0)
    else direction(i).normalized
  }

  def perpendicular(i: Int) = _perpPoints(i)

  def toClosedLoop = new ClosedLoop(_points)
}

class ClosedLoop(points: IndexedSeq[V2D]) extends ClosedPath(points) {
  override def apply(i: Int) = super.apply(i % length)
  override def direction(i: Int) = apply(i+1) - apply(i)
  override def perpendicular(i: Int): V2D = super.perpendicular(i % length)
}