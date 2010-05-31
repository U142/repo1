package no.ums.pas.sound;

import javax.sound.sampled.AudioFormat;



public class OscillatorProperties {
	  float fSignalFrequency;
	  float fAmplitude;
	  AudioFormat audioFormat;
	  
	  public float getSignalFrequency() { return fSignalFrequency; }
	  public float getAmplitude() { return fAmplitude; }
	  public AudioFormat getAudioFormat() { return audioFormat; }
	  
	public OscillatorProperties(float fSignalFrequency, float fAmplitude, AudioFormat audioFormat) {
		this.fSignalFrequency	= fSignalFrequency;
		this.fAmplitude			= fAmplitude;
		this.audioFormat		= audioFormat;
	}
	
}