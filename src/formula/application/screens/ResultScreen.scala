package formula.application.screens
import formula.io._
import formula.engine._
import formula.application._

class ResultScreen
(track: Track,
 totalTime: Int,
 nOfLaps: Int,
 playerNames: Vector[String],
 winner: Int) extends StaticScreen(Textures.Background_Generic, Textures.Button) {

  protected override def createComponents() = {

    val oldTime = track.fastestTimes.headOption.map(_._1).getOrElse(Int.MaxValue)
    val time = math.round(1D * totalTime / nOfLaps).toInt
    track.updateFastestTimes(time, playerNames(winner))
    val isNewRecord = track.fastestTimes.head._1 < oldTime

    val screenTitle = new FontLabel("R E S U L T S", fontSize = 3F)
    screenTitle.percentSize = (GUIConstants.TEXTFIELD_WIDTH*2, GUIConstants.TEXTFIELD_HEIGHT*1.5)
    screenTitle.percentPosition = (0.4, 0.05)
    components += screenTitle


    val trackPreview = new TrackPreviewPanel
    trackPreview.percentBounds = (0.025, 0.2, 0.4, 0.7)
    components += trackPreview
    trackPreview.updatePreview(track)

    val raceButton = new GrayButton("Race again", () => MainApplication.transition(new TrackSelectScreen(TrackSelectScreen.Race)))
    raceButton.percentPosition = (0.6, 0.85)
    components += raceButton

    val backButton = new GrayButton("Back to menu", () => MainApplication.transition(new MainMenuScreen))
    backButton.percentPosition = (0.8, 0.85)
    components += backButton


    val raceResultsTitle = new FontLabel("Race results", fontSize = 2F)
    raceResultsTitle.percentPosition = (0.6, 0.22)
    components += raceResultsTitle

    if(isNewRecord) {
      val fastestTimeText = new FontLabel("New record", fontSize = 2F, fontColor = java.awt.Color.GREEN)
      fastestTimeText.percentPosition = (0.6, 0.3)
      components += fastestTimeText
    }

    val totalTimeTitle = new FontLabel("Total time:")
    totalTimeTitle.percentPosition = (0.6, 0.4)
    components += totalTimeTitle

    val totalTimeValue = new FontLabel(Track.describeTrackTime((totalTime, "")),
      textFont = Fonts.TimesNewRoman, fontColor = GUIConstants.COLOR_ACCENT)
    totalTimeValue.percentPosition = (0.7, 0.4)
    components += totalTimeValue

    val lapTimeTitle = new FontLabel("Lap time:")
    lapTimeTitle.percentPosition = (0.6, 0.45)
    components += lapTimeTitle

    val lapTimeValue = new FontLabel(Track.describeTrackTime((time, "")),
      textFont = Fonts.TimesNewRoman, fontColor = GUIConstants.COLOR_ACCENT)
    lapTimeValue.percentPosition = (0.7, 0.45)
    components += lapTimeValue


    val players = playerNames(winner) +: playerNames.zipWithIndex.filterNot(_._2 == winner).map(_._1)


    players.zipWithIndex.foreach(x => {
      val (name, i) = x

      val placement = new FontLabel(s"${i+1}.", fontColor = if(i==0) java.awt.Color.GREEN else java.awt.Color.RED)
      placement.percentPosition = (0.6, 0.55+i*0.05)
      components += placement

      val playerName = new FontLabel(name)
      playerName.percentPosition = (0.63, 0.55+i*0.05)
      components += playerName

    })
  }

  override def redraw(): Unit = {
    super.redraw()

    //Attempt to save the track file after the result screen has fully loaded
    var success = FormulaIO.saveTrack(track)


    //TODO: Confirmbox doesn't get properly disposed
    //Maybe because it's called in a while loop here?
    //Cleaning up the screen afterwards doesn't fix this.
    //The dialog would have to created and disposed manually (similar to MainApplication.modalActionBox)
    while(!success) {
      if(MainApplication.confirmBox("Unfortunately, there was an error while trying to save track times. Retry?")) {
        success = FormulaIO.saveTrack(track)
      }
      else {
        success = true
      }
    }

    SoundSystem.playSound(Sounds.Results)

  }

  override def deactivate(): Unit = {
    super.deactivate()
    FormulaIO.unloadAllSounds()
  }

}