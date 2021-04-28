package formula.engine
import formula.io._
import formula.io.Textures.Texture
import java.awt.image.BufferedImage
import scala.collection.mutable.PriorityQueue

object AnimatedSprites {

  val animating = PriorityQueue[AnimatedSprite]()(Ordering.by(_.endTime))
  val Explosion = Textures.ANIM_Explosion
  val Smoke = Textures.ANIM_Smoke
  val Speed = Textures.ANIM_Speed
  val Oil   = Textures.ANIM_Oil

  def spawnSprite(texture: Texture, pos: V2D, scale: Double, time: Long) = {

    val animation = texture match {
      case Explosion => new AnimatedSprite(7, 4, 88, 88, 60, Explosion, pos, scale, time)
      case Smoke => new AnimatedSprite(3, 1, 100, 100, 30, Smoke, pos, scale, time)
      case Speed => new AnimatedSprite(3, 1, 100, 100, 60, Speed, pos, scale, time)
      case Oil   => new AnimatedSprite(3, 1, 100, 100, 30, Oil, pos, scale, time)
    }

    animating.enqueue(animation)

  }

  def despawnSprites(time: Long) = {

    while(animating.nonEmpty && animating.head.endTime < time) {
      animating.dequeue()
    }

  }


}


class AnimatedSprite
(protected val rowLength: Int,
 protected val rowCount: Int,
 protected val spriteWidth: Int,
 protected val spriteHeight: Int,
 protected val frameRate: Double,
 val texture: Texture,
 val position: V2D,
 val scale: Double,
 val startTime: Long) extends Sprite {

  override def spriteRatio: Double = 1D * spriteHeight / spriteWidth
  val frameCount = rowLength * rowCount
  val frameTime = 1D / frameRate
  val endTime = startTime + (Game.TIME_PRECISION * frameCount * frameTime).toLong

  protected val sprites = Array.ofDim[BufferedImage](frameCount)
  private val img = FormulaIO.getTexture(texture)
  for(i <- 0 until frameCount) {
      sprites(i) = img.getSubimage(spriteWidth * (i % rowLength), spriteHeight * (i / rowLength) ,spriteWidth, spriteHeight)
  }

  def image(time: Long) = {

    val t = (time - startTime) / Game.TIME_PRECISION
    sprites(math.min((t / frameTime).toInt, frameCount - 1))

  }


}