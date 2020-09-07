import java.io.BufferedInputStream;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class AudioPlayer {
	FloatControl gain;
	AudioInputStream ais;
	Clip clip;
	boolean isPlaying = false;
	public AudioPlayer(String file) {
		try {
			ais = AudioSystem.getAudioInputStream(new BufferedInputStream(AudioPlayer.class.getResourceAsStream(file)));
			clip = AudioSystem.getClip();
			clip.open(ais);
			gain = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
			
		} 
		catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (LineUnavailableException e) {
			e.printStackTrace();
		}
		
	}
	public void setGain(float value) {
		gain.setValue(value);
	}
	public void play() {
		isPlaying = true;
		
		//background thread to stop the clip once it has finished playing.
		Thread stopThread = new Thread(new Runnable() {
			public void run() {
				clip.start();
				while(true) {
					if(clip.getMicrosecondPosition() >= clip.getMicrosecondLength()) {
						clip.stop();
						clip.setMicrosecondPosition(0);
						isPlaying = false;
						break;
					}
				}
			}
		});
		stopThread.start();
	}
}
