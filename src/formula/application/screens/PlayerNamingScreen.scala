package formula.application.screens
import formula.io._
import formula.engine._
import formula.engine.render.MapRenderTarget
import formula.application._

class PlayerNamingScreen(track: Track) extends StaticScreen(Textures.Background_Generic, Textures.Button) {

  protected override def createComponents() = {

    val trackTitle = new FontLabel("Track: "+track.trackName, fontSize = 2F)
    trackTitle.percentSize = (GUIConstants.TEXTFIELD_WIDTH*2, GUIConstants.TEXTFIELD_HEIGHT*1.5)
    trackTitle.percentPosition = (0.05, 0.025)
    components += trackTitle

    val creatorTitle = new FontLabel("Creator: "+track.creator, fontSize = 2F)
    creatorTitle.percentSize = (GUIConstants.TEXTFIELD_WIDTH*2, GUIConstants.TEXTFIELD_HEIGHT*1.5)
    creatorTitle.percentPosition = (0.05, 0.125)
    components += creatorTitle

    val trackRender = new MapRenderTarget(new Game(track, 0, 1))
    trackRender.percentBounds = (0, 0, 1, 1)
    val largeTrackPreview = new RenderPanel(trackRender)
    largeTrackPreview.percentBounds = (0.025, 0.3, 0.3, 0.55)
    components += largeTrackPreview


    if(track.description.trim.nonEmpty) {
      val trackDescription = new TextArea("\n\n"+track.description, editable = false)
      trackDescription.percentSize = (GUIConstants.TEXTFIELD_WIDTH, 0.55)
      trackDescription.percentPosition = (0.325, 0.3)
      components += trackDescription
    }


    val settingsLabel = new FontLabel("Game settings", fontSize = 2F, fontColor = GUIConstants.COLOR_HEADER2)
    settingsLabel.percentSize = (GUIConstants.TEXTFIELD_WIDTH*2, GUIConstants.TEXTFIELD_HEIGHT*1.5)
    settingsLabel.percentPosition = (0.6, 0.025)
    components += settingsLabel

    val playersLabel = new FontLabel("Number of players", fontSize = 1.3F)
    playersLabel.percentPosition = (0.55, 0.15)
    components += playersLabel

    val playersDropdown = new DropDown(Vector("Singleplayer", "Two player"))
    playersDropdown.percentPosition = (0.56, 0.23)
    components += playersDropdown


    val lapsLabel = new FontLabel("Number of laps", fontSize = 1.3F)
    lapsLabel.percentPosition = (0.75, 0.15)
    components += lapsLabel

    val lapsInput = new TextInput("3")
    lapsInput.percentSize = (GUIConstants.TEXTFIELD_WIDTH * 0.25, GUIConstants.TEXTFIELD_HEIGHT)
    lapsInput.percentPosition = (0.76, 0.23)
    components += lapsInput



    val player1Name = new FontLabel("Player 1 name")
    player1Name.percentPosition = (0.58, 0.5)
    components += player1Name

    val player2Name = new FontLabel("Player 2 name")
    player2Name.percentPosition = (0.78, 0.5)
    components += player2Name

    val player1NameInput = new TextInput("Player 1")
    player1NameInput.percentSize = (GUIConstants.TEXTFIELD_WIDTH * 0.8, GUIConstants.TEXTFIELD_HEIGHT * 0.8)
    player1NameInput.percentPosition = (0.55, 0.58)
    components += player1NameInput

    val player2NameInput = new TextInput("Player 2")
    player2NameInput.percentSize = (GUIConstants.TEXTFIELD_WIDTH * 0.8, GUIConstants.TEXTFIELD_HEIGHT * 0.8)
    player2NameInput.percentPosition = (0.75, 0.58)
    components += player2NameInput

    val player1AIDropdown = new DropDown(Vector("PL", "AI"))
    player1AIDropdown.percentSize = (GUIConstants.TEXTFIELD_WIDTH * 0.3, GUIConstants.TEXTFIELD_HEIGHT * 0.8)
    player1AIDropdown.percentPosition = (0.6, 0.45)
    components += player1AIDropdown

    val player2AIDropdown = new DropDown(Vector("PL", "AI"))
    player2AIDropdown.percentSize = (GUIConstants.TEXTFIELD_WIDTH * 0.3, GUIConstants.TEXTFIELD_HEIGHT * 0.8)
    player2AIDropdown.percentPosition = (0.8, 0.45)
    components += player2AIDropdown



    val raceButton = new GrayButton("RACE!", () => {

      val laps = math.max(lapsInput.getText.toIntOption.getOrElse(3), 1)
      val playerCount = playersDropdown.getSelectedIndex + 1
      val player1Name = player1NameInput.getText.filterNot(_ == FormulaIO.STRING_SEP_CHAR).trim
      val player2Name = player2NameInput.getText.filterNot(_ == FormulaIO.STRING_SEP_CHAR).trim
      val player1AI = player1AIDropdown.getSelectedIndex == 1
      val player2AI = player2AIDropdown.getSelectedIndex == 1

      val playerNames = Vector(
        if(player1Name.nonEmpty) player1Name else "Player 1",
        if(player2Name.nonEmpty) player2Name else "Player 2")

      val playerAI = Vector(player1AI, player2AI)

      MainApplication.transition(new GameScreen(track, playerCount, laps, playerNames, playerAI))

    })
    raceButton.percentPosition = (0.55, 0.85)
    components += raceButton

    val backButton = new GrayButton("Back", () => MainApplication.transition(new TrackSelectScreen(TrackSelectScreen.Race)))
    backButton.percentPosition = (0.8, 0.85)
    components += backButton

  }

}