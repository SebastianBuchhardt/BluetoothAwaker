package audio;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class MusicPlayer {
	private static MusicPlayer player = null;
	private static String selectedSong = "";
	
	private MusicPlayer() {
		//initialize music database
		
	}
	
	public static void loadSong(String song) {
		if (player == null) {
			player = new MusicPlayer();
		}
				
		//load the song if it exists
		selectedSong = song;
		debug("Song loaded");
	}
	
	public static void play() {
		if (!selectedSong.equals("")) {
			if (player == null) {
				player = new MusicPlayer();
			}
					
			//play music via audio jack
	        try {
	            AudioInputStream audioIn = AudioSystem.getAudioInputStream(new File(selectedSong));
	            Clip clip;
	            clip = AudioSystem.getClip();
	            clip.open(audioIn);
	            clip.start();
	            debug("Started playing music");
	            Thread.sleep(clip.getMicrosecondLength()/1000);
	        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException | InterruptedException  e1) {
	            e1.printStackTrace();
	        }
		}
	}
	
	public static void pause() {
		if (player == null) {
			player = new MusicPlayer();
		}
		
		//pause music if it is being played
		debug("Paused music");
		//resume music if it was being paused
		
	}
	
	public static void stop() {
		if (player == null) {
			player = new MusicPlayer();
		}
		
		//stop music if it is being played
		debug("Stopped music");
	}
	
	private static void debug(final String msg) {
		System.out.println(msg);
	}
}
