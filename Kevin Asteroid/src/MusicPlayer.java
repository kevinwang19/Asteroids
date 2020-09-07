import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
/**
 * Class for streaming music within a Java program.
 * Works for .wav formats only.
 * @author jordan
 *
 */
public class MusicPlayer {
	SourceDataLine line;
	AudioInputStream ais;
	AudioFormat format;
	AudioFileFormat fileFormat;
	int bufferSize;
	public static final int PLAY = 0;
	public static final int LOOP = 1;
	int mode;
	public boolean isPlaying = false;
	/**
	 * The position within the track, in frames.
	 */
	long offsetPosition = 0;
	
	public MusicPlayer(String path) {
		try {
			InputStream stream = new BufferedInputStream(MusicPlayer.class.getResourceAsStream(path));
			fileFormat = AudioSystem.getAudioFileFormat(stream);
			ais = AudioSystem.getAudioInputStream(stream);
			ais.mark(Integer.MAX_VALUE);
			
		    format = ais.getFormat();
		   
			
			DataLine.Info datalineInfo = new DataLine.Info(SourceDataLine.class,format);
			line = (SourceDataLine)AudioSystem.getLine(datalineInfo);
			line.open(format);
			bufferSize = line.getBufferSize();
			
			//System.out.println(bufferSize);
		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
			e.printStackTrace();
		}
	}
	public void setMode(int mode) {
		this.mode = mode;
	}
	public void skip(int minutes,int seconds) {
		long byteSkip = (long)((seconds + minutes * 60) * format.getFrameRate() * format.getFrameSize());
		offsetPosition = byteSkip / format.getFrameSize();
		try {
			ais.skip(byteSkip);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void skip(String time) {
		try {
			int colonPosition = time.indexOf(':');
			int mins = Integer.parseInt(time.substring(0,colonPosition));
			int seconds = Integer.parseInt(time.substring(colonPosition+1,time.length()));
			skip(mins,seconds);
		}
		catch(Exception e) {
			
		}
	}
	public void stop() {
		if(isPlaying == false)return;
		try {
			line.stop();
			line.flush();
			ais.reset();
		} catch (IOException e) {
			e.printStackTrace();
		}
		isPlaying = false;
	}
	public String currentTrackPosition() {
		long trackPosition = (line.getFramePosition() + offsetPosition) % ais.getFrameLength();
		return framesToTime(trackPosition);
	}
	public void play() {
		if(isPlaying == true)return;
		line.start();
		isPlaying = true;
		
		//Thread to pump the byte data from the audio input stream into the source data line.
		Thread playThread = new Thread(new Runnable() {
			public void run() {
				//Making the byte buffer that will be used.
				byte [] dataBuffer = new byte[bufferSize];
				while(true) {
					try {
						int readCount = ais.read(dataBuffer,0,bufferSize);
						//Ensuring the player adjusts as necessary when audio input stream has run out of data.
						if(readCount <= 0) {
							if(mode == PLAY) {
								stop();
								break;
							}
							else if(mode == LOOP) {
								ais.reset();
								continue;
							}
						}
						line.write(dataBuffer,0,readCount);
					}
					catch(IOException e) {
						e.printStackTrace();
					}
					if(isPlaying == false) {
						return;
					}
				}
			}
		});
		playThread.start();
	}
	
	/**
	 * Converts the number of frames specified into a string time.
	 * @param frames - the number of frames to be converted into time.
	 * @return time, a string formatted into minutes:seconds.
	 */
	public String framesToTime(long frames) {
		double length = (double)frames / format.getFrameRate();
		int minutes = (int)(length / 60);
		int seconds = (int) (length % 60);
		return String.format("%d:%02d",minutes,seconds);
	}
	/**
	 * @return the length of the track being played.
	 */
	public String getTrackLength() {
		return framesToTime(ais.getFrameLength());
	}
	public static class AudioFrame implements ActionListener{
		public JFrame frame;
		public JPanel panel;
		public JLabel positionLabel;
		public JLabel nameLabel;
		public MusicPlayer player;
		public JButton playButton;
		public JButton stopButton;
		public AudioFrame() {
			frame = new JFrame("Soundplayer");
			frame.setSize(200,200);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setVisible(true);
			panel = (JPanel)frame.getContentPane();
			panel.setLayout(new GridLayout(4,1));
			nameLabel = new JLabel();
			panel.add(nameLabel);
			positionLabel = new JLabel();
			panel.add(positionLabel);
			
			player = new MusicPlayer("bgm.wav");
			player.setMode(LOOP);
			
			playButton = new JButton("Play");
			playButton.addActionListener(this);
			panel.add(playButton);
			
			stopButton = new JButton("Stop");
			stopButton.addActionListener(this);
			panel.add(stopButton);
		
			
			nameLabel.setText("Name: " + player.fileFormat.getProperty("title"));
			System.out.println(player.getTrackLength());
			
			while(true) {
				positionLabel.setText(player.currentTrackPosition() + ":" + player.getTrackLength());
			}
		}
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == playButton)player.play();
			else if(e.getSource() == stopButton)player.stop();
			
		}
	}
	public static void main(String [] args) {
		new AudioFrame();
		
	}
	
}
