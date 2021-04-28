# K00PECART

**K00PECART** is a 1-2 player racing game written in Scala,
and implemented using Java Swing.

The game features realtime graphics, sound design, computer controlled opponents
and a track tool for creating your own racing tracks.


# Compiling
The game can be most easily compiled using IntelliJ, or Scala Build Tool.
The repository readily contains the associated .iml module file for compiling
with IntelliJ, and a build.sbt file for SBT version 1.3.2 and newer.

The program itself requires a minimum Java version of JDK 8,
and Scala version 2.13.2

For compiling with other build systems, simply make sure that the required
Java JDK and Scala SDK are available, and remember to include the `data`
folder in the same root directory as `src`


# Custom music and modding
*As of version 3.5*
To add custom music, include the music files in .wav format in `data/music/`
Almost all data files in the game are replaceable, with the exception of
animated spritesheets. The game can tolerate missing files, but it will notify
about them.