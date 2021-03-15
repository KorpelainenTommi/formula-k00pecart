package formula.application
import javax.swing._
import formula.engine.TrackPreview
import java.awt.Graphics
class TrackPreviewPanel extends JPanel with PercentBounds {
  this.setLayout(null)
  override def component = this

  override def paintComponent(g: Graphics) = {
    val bounds = g.getClipBounds
    g.setColor(java.awt.Color.DARK_GRAY)
    g.fillRect((bounds.x+0.4*bounds.width).toInt, (bounds.y+0.3*bounds.height).toInt, (0.55*bounds.width).toInt, (0.01*bounds.height).toInt)
    g.setColor(java.awt.Color.GREEN)
    g.fillRect((bounds.x+0.05*bounds.width).toInt, (bounds.y+0.05*bounds.height).toInt, (0.28*bounds.width).toInt, (0.28*bounds.width).toInt)
  }

  private val trackLabel = new FontLabel("Track: Loop1", fontSize = 3F)
  trackLabel.setPercentBounds(0.4, 0.05, 0.5, 0.1)
  this.add(trackLabel)

  private val creatorLabel = new FontLabel("Creator: K00PE", fontSize = 3F)
  creatorLabel.setPercentBounds(0.4, 0.15, 0.5, 0.1)
  this.add(creatorLabel)

  private val leaderBoardsLabel = new FontLabel("L E A D E R B O A R D S", fontSize = 3F)
  leaderBoardsLabel.setPercentBounds(0.4, 0.32, 0.5, 0.1)
  this.add(leaderBoardsLabel)

  private val leaderBoards = Array.tabulate[FontLabel](formula.engine.Track.MAX_LEADERBOARD)(i => {
    val l = new FontLabel("1. 9:59:59.00 Exampletime"+i, formula.io.Fonts.TimesNewRoman, fontSize = 3F, java.awt.Color.YELLOW)
    l.setPercentBounds(0.05, 0.42+i*0.09, 0.9, 0.1)
    l
  })
  leaderBoards.foreach(this.add)



  override def updateBounds(width: Double, height: Double) = {
    super.updateBounds(width, height)
    trackLabel.updateBounds(getBounds().width, getBounds().height)
    creatorLabel.updateBounds(getBounds().width, getBounds().height)
    leaderBoardsLabel.updateBounds(getBounds().width, getBounds().height)
    leaderBoards.foreach(_.updateBounds(getBounds().width, getBounds().height))
  }

  private var _trackPreview: TrackPreview = null
  def trackPreview = _trackPreview

  def updatePreview(track: TrackPreview) = {



  }


}