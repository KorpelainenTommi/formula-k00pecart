package formula.engine
import formula.io._
import formula.io.Textures.Texture

//Allows the creation of mapobjects based on ID
object MapObjects extends Serializer[MapObject] {

  val objectIDList = (0 to 5).toVector

  def createMapObject(ID: Int, pos: V2D) = {

    ID match {
      case 0 => new MapObject(ID, "Tree", Textures.OBJ_Tree, 5D, pos)
      case 1 => new MapObject(ID, "Rock", Textures.OBJ_Rock, 4D, pos)
      case 2 => new MapObject(ID, "Pebbles", Textures.OBJ_Tuft1, 3.3D, pos)
      case 3 => new MapObject(ID, "GrassTufts", Textures.OBJ_Tuft2, 2.4D, pos)
      case 4 => new MapObject(ID, "GrassTufts2", Textures.OBJ_Tuft0, 3.2D, pos)
      case 5 => new MapObject(ID, "OilSpill", Textures.OBJ_Oil, 5D, pos)
      case _ => new MapObject(-1, "", Textures.Button, 0D, V2D(0,0))
    }

  }


  override def save(saveable: MapObject) = {
    FormulaIO.saveInt(saveable.ID) ++ V2D.save(saveable.position)
  }

  override def load(bytes: Array[Byte], start: Int) = {
    val ID = FormulaIO.loadInt(bytes, start)
    val pos = V2D.load(bytes, start + 4)
    createMapObject(ID, pos)
  }

}

class MapObject
(val ID: Int, val name: String,
 val texture: Texture, val scale: Double,
 val position: V2D) extends Sprite {

  override def spriteRatio = {
    val img = FormulaIO.getTexture(texture)
    1D * img.getHeight / img.getWidth
  }

}