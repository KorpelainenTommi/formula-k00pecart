package formula.engine
import formula.io._
import javax.sound.sampled._
import formula.io.Sounds.Sound
import scala.collection.mutable.ArrayBuffer
import formula.application.MainApplication

//A simple system for playing sounds and managing sound clip lifetime
object SoundSystem {

  protected var openClips = ArrayBuffer[Clip]()
  protected val soundSources = ArrayBuffer[SoundSource]()

  //Play a sound file based on filename
  def playMusic(filename: String) = {

    val vol = MainApplication.settings.volume / 100D
    val db = if(vol <= 0D) Float.NegativeInfinity else (math.log10(vol) * 10).toFloat
    val file = FormulaIO.getMusic(filename)

    file.foreach(f => {

      try {
        val stream = AudioSystem.getAudioInputStream(f)
        val clip = AudioSystem.getClip
        clip.open(stream)
        openClips += clip
        val gain = clip.getControl(FloatControl.Type.MASTER_GAIN).asInstanceOf[FloatControl]
        gain.setValue(db)
        clip.start()
      }

      catch {
        case _: Exception =>
      }
    })

  }

  //Play a sound
  def playSound(s: Sound, absoluteVolume: Int = MainApplication.settings.volume) = {

    //Repeatedly closing expired clips causes delay, so perform cleanup in larger batches
    if(openClips.length > 12) {
      cleanUp()
    }

    val vol = absoluteVolume / 100D
    val db = if(vol <= 0D) Float.NegativeInfinity else (math.log10(vol) * 10).toFloat
    val file = FormulaIO.getSound(s)

    file.foreach(f => {

      try {
        val stream = AudioSystem.getAudioInputStream(f)
        val clip = AudioSystem.getClip
        clip.open(stream)
        openClips += clip
        val gain = clip.getControl(FloatControl.Type.MASTER_GAIN).asInstanceOf[FloatControl]
        gain.setValue(db)
        clip.start()
      }

      catch {
        case _: Exception =>
      }
    })

  }

  //Register a sound source so it can be properly cleaned up
  def registerSoundSource(ss: SoundSource) = {
    soundSources += ss
  }


  def cleanUp() = {
    openClips.filterNot(_.isRunning).foreach(_.close())
    openClips = openClips.filter(_.isRunning)
  }

  def cleanUpAll() = {
    openClips.foreach(_.close())
    openClips.clear()
    soundSources.foreach(_.cleanUpAll())
    soundSources.clear()
  }

}




//A SoundSource can manage and play a selection of clips simultaneously, possibly looping them
class SoundSource(sounds: Vector[Sound]) {

  SoundSystem.registerSoundSource(this)

  //Preload clips
  protected val clips = sounds.flatMap(s => {
    val file = FormulaIO.getSound(s)
    file.flatMap(f => {
      try {
        val stream = AudioSystem.getAudioInputStream(f)
        val clip = AudioSystem.getClip
        clip.open(stream)
        Some((s, clip))
      }

      catch {
        case e: UnsupportedAudioFileException => None
      }
    })
  }).toMap


  protected def setVolume(sound: Sound, volume: Double) = {
    val vol = volume * MainApplication.settings.volume / 100D
    val db = if(vol <= 0D) Float.NegativeInfinity else (math.log10(vol) * 10).toFloat
    clips.get(sound).foreach(c => {
      c.getControl(FloatControl.Type.MASTER_GAIN).asInstanceOf[FloatControl].setValue(db)
    })
  }


  def playOnce(sound: Sound, volume: Double) = {
    turnOff(sound)
    setVolume(sound, volume)
    clips.get(sound).foreach(c => {
      c.loop(0)
    })
  }

  def turnOn(sound: Sound, volume: Double) = {
    if(!clips.get(sound).exists(_.isRunning)) {
      turnOff(sound)
      setVolume(sound, volume)
      clips.get(sound).foreach(c => {
        c.loop(Clip.LOOP_CONTINUOUSLY)
      })
    }
  }

  def turnOff(sound: Sound) = {
    clips.get(sound).foreach(c => {
      if(c.isRunning) {
        c.stop()
      }
      c.setFramePosition(0)
    })
  }

  def turnAllOff() = {
    clips.foreach(x => turnOff(x._1))
  }

  def cleanUpAll() = {
    clips.foreach(_._2.close())
  }

}