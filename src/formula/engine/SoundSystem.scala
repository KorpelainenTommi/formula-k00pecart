package formula.engine
import formula.io._
import javax.sound.sampled._
import formula.io.Sounds.Sound
import scala.collection.mutable.ArrayBuffer
import formula.application.MainApplication

//A simple system for playing sounds and managing sound clip lifetime
object SoundSystem {

  protected var openClips = ArrayBuffer[Clip]()
  protected var soundSources = ArrayBuffer[SoundSource]()

  //Play a sound file based on filename
  def playMusic(filename: String) = {

    performCleanUp()
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
        clip.loop(Clip.LOOP_CONTINUOUSLY)
      }

      catch {
        case _: Exception =>
      }
    })

  }

  //Play a sound
  def playSound(s: Sound, absoluteVolume: Int = MainApplication.settings.volume) = {

    performCleanUp()
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


  def stopAllSounds() = {
    openClips.foreach(_.stop())
    soundSources.foreach(_.turnAllOff())
  }

  //Register a sound source so it can be properly cleaned up
  def registerSoundSource(ss: SoundSource) = {
    performCleanUp()
    soundSources += ss
  }

  //Mark all current sound sources for cleanup
  def clearSoundSources() = {
    soundSources.foreach(_.deactivate())
  }

  def performCleanUp() = {
    if(openClips.length > 12) {
      openClips.filterNot(_.isRunning).foreach(_.close())
      openClips = openClips.filter(_.isRunning)
    }

    if(soundSources.length > 4) {
      soundSources.filterNot(_.active).foreach(_.cleanUp())
      soundSources = soundSources.filter(_.active)
    }
  }

}




//A SoundSource can manage and play a selection of clips simultaneously, possibly looping them
class SoundSource(sounds: Vector[Sound]) {

  protected var _active = true
  def active = _active

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
    if(active) {
      turnOff(sound)
      setVolume(sound, volume)
      clips.get(sound).foreach(c => {
        c.loop(0)
      })
    }
  }

  def turnOn(sound: Sound, volume: Double) = {
    if(active && !clips.get(sound).exists(_.isRunning)) {
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


  def deactivate() = {
    turnAllOff()
    _active = false
  }

  def cleanUp() = {
    clips.foreach(_._2.close())
  }

}