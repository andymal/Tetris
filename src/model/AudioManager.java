package model;

import java.io.IOException;

import javax.sound.sampled.*;

import ui.GameFrame;

// Singleton class to interface to the game's audio
public class AudioManager {
	
	// Provides a handle to the class directory of the AudioManger.class file.
	// Used to obtain audio file resources
	private static final Class<?> resources = new AudioManager().getClass();
	
	// Non-looping soundtrack clips
	private static final Clip gameOver = getAudioClip("audio/soundtrack/zelda-game-over.wav");
	private static final Clip victoryFanfare = getAudioClip("audio/soundtrack/ff1-victory-fanfare.wav");
	
	// Effects
	private static final Clip pause = getAudioClip("audio/effects/mario-64-pause.wav");
	private static final Clip placePiece = getAudioClip("audio/effects/pipe.wav");
	private static final Clip clearLine = getAudioClip("audio/effects/laser.wav");
	private static final Clip ultraLine = getAudioClip("audio/effects/explosion.wav");	
	private static final Clip swipeUp = getAudioClip("audio/effects/swish-up.wav");
	private static final Clip swipeDown = getAudioClip("audio/effects/swish-down.wav");
	private static final Clip hold = getAudioClip("audio/effects/clang.wav");
	private static final Clip release = getAudioClip("audio/effects/water-drop.wav");
	
	private AudioManager() {}
	
	public static void playGameOverSound() {
		if (GameFrame.settingsPanel.effectsOn() && gameOver != null) {
			gameOver.start();
			gameOver.setFramePosition(0);
		}
	}
	
	public static void playVictoryFanfare() {
		if (GameFrame.settingsPanel.effectsOn() && victoryFanfare != null) {
			victoryFanfare.start();
			victoryFanfare.setFramePosition(0);
		}
	}
	
	public static void playPauseSound() { playEffect(pause); }
	public static void playPiecePlacementSound() { playEffect(placePiece); }
	public static void playHoldSound() { playEffect(hold); }
	public static void playReleaseSound() { playEffect(release); }
	
	// Clear line sound is dependent on number of lines cleared
	public static void playClearLineSound(int lineCount) {
		if (lineCount == 4)
			playEffect(ultraLine);
		else
			playEffect(clearLine);
	}
	
	public static void playCWRotationSound() { playEffect(swipeUp); }
	public static void playCCWRotationSound() { playEffect(swipeDown); }
	
	// For playing small effect sounds. Resets the clip back to the starting
	// frame position after playing
	private static void playEffect(Clip effect) {

		if (GameFrame.settingsPanel.effectsOn() && effect != null) {
			effect.start();
			effect.setFramePosition(0);
		}

	}
	
	// Returns a clip audio output device input line from the specified file string
	private static Clip getAudioClip(String file) {
		
		// Attempt to initialize clip input object. If there are no supported lines,
		// null is returned for the clip
		Clip c;
		
		if (AudioSystem.isLineSupported(Port.Info.SPEAKER) || AudioSystem.isLineSupported(Port.Info.HEADPHONE)) {
		
			try {
				
				c = AudioSystem.getClip();
				
				// Add a new audio stream to the clip data line. Since I'm
				// using a clip object, all data is loaded into memory at
				// once as opposed to being read into a buffer and streamed
				c.open(AudioSystem.getAudioInputStream(resources.getResource(file)));
				return c;
				
			}
			catch (LineUnavailableException | IOException | UnsupportedAudioFileException e) {}
			
		}
		
		return null;
		
	}
	
}
