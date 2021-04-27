package formula.application
import javax.swing._
import java.awt._
import formula.engine._
import java.awt.image.BufferedImage
class TrackPreviewPanel extends JPanel with ComponentPercentBounds {
  this.setLayout(null)
  override def component = this

  override def paintComponent(g: Graphics) = {
    super.paintComponent(g)

    if(previewImage.isDefined) {
      val bounds = g.getClipBounds
      g.setColor(java.awt.Color.DARK_GRAY)
      g.fillRect(math.round(bounds.x+0.4*bounds.width).toInt, math.round(bounds.y+0.3*bounds.height).toInt,
        math.round(0.55*bounds.width).toInt, math.round(0.01*bounds.height).toInt)

      val iconX = math.round(bounds.x+0.05*bounds.width).toInt
      val iconY = math.round(bounds.y+0.05*bounds.height).toInt
      val iconW = math.round(0.28*bounds.width).toInt
      val iconH = math.round(0.28*bounds.width).toInt
      val border = GUIConstants.IMAGE_CELL_MARGIN-GUIConstants.IMAGE_CELL_BORDER
      g.setColor(GUIConstants.COLOR_CELL_BORDER)
      g.fillRect(iconX, iconY, iconW, iconH)
      previewImage.foreach(img => {
        g.drawImage(img, iconX+border, iconY+border, iconW-2*border, iconH-2*border, null)
      })
    }

    //g.fillRect((bounds.x+0.05*bounds.width).toInt, (bounds.y+0.05*bounds.height).toInt, (0.28*bounds.width).toInt, (0.28*bounds.width).toInt)
  }

  this.setBackground(GUIConstants.COLOR_AREA)

  private var previewImage: Option[BufferedImage] = None
  private val trackLabel = new FontLabel("", fontSize = 3F)
  trackLabel.percentBounds = (0.4, 0.05, 0.5, 0.1)
  this.add(trackLabel)

  private val creatorLabel = new FontLabel("", fontSize = 3F)
  creatorLabel.percentBounds = (0.4, 0.15, 0.5, 0.1)
  this.add(creatorLabel)

  private val leaderBoardsLabel = new FontLabel("", fontSize = 3F)
  leaderBoardsLabel.percentBounds = (0.4, 0.32, 0.5, 0.1)
  this.add(leaderBoardsLabel)

  private val leaderBoards = Array.tabulate[FontLabel](formula.engine.Track.MAX_LEADERBOARD)(i => {
    val l = new FontLabel("", formula.io.Fonts.TimesNewRoman, fontSize = 3F, java.awt.Color.YELLOW)
    l.percentBounds = (0.2, 0.42+i*0.09, 0.9, 0.1)
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

  def updatePreview(track: TrackPreview) = {

    trackLabel.text = "Track: " + track.trackName
    creatorLabel.text = "Creator: " + track.creator
    leaderBoardsLabel.text = "L E A D E R B O A R D S"
    leaderBoards.foreach(_.text = "")
    for(i <- track.fastestTimes.indices) {
      val n = i+1
      leaderBoards(i).text = s"$n. "+Track.describeTrackTime(track.fastestTimes(i))
    }

    previewImage = Some(track.previewImage)
    this.repaint()

  }


}