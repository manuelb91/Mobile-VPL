package it.unibz.mobilevpl.block;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.andengine.audio.music.Music;
import org.andengine.audio.music.MusicFactory;
import org.andengine.audio.music.MusicManager;
import org.andengine.audio.sound.SoundManager;

public class SoundBlock {

	public static Music playSound(MusicManager musicManager, String path) {
		Music music = null;
		try {
			music = MusicFactory.createMusicFromFile(musicManager, new File(path));
			music.play();
		} catch(IOException e) {
			e.printStackTrace();
		}
		return music;
	}
	
	public static void stopSound(Music music) {
		music.stop();
	}
	
	public static void stopSound(List<Music> musicList) {
		for (Music music : musicList) {
			music.stop();
		}
	}
	
	public static void stopSound(MusicManager musicManager, SoundManager soundManager) {
		soundManager.onPause();
	}
	
	public static void setVolumeToPercentage(Music music, float percentage) {
		music.setVolume(percentage / 100);
	}
	
	public static void setVolumeToPercentage(MusicManager musicManager, SoundManager soundManager, float percentage) {
		musicManager.setMasterVolume(percentage / 100);
		soundManager.setMasterVolume(percentage / 100);
	}
	
	public static void changeVolumeBy(Music music, float change) {
		music.setVolume(music.getVolume() + (change / 100));
	}
	
	public static void changeVolumeBy(MusicManager musicManager, SoundManager soundManager, float percentage) {
		musicManager.setMasterVolume(musicManager.getMasterVolume() + (percentage / 100));
		soundManager.setMasterVolume(soundManager.getMasterVolume() + (percentage / 100));
	}
}
