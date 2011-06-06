package no.ums.pas.sound;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.TargetDataLine;



public class OscillatorProperties {
	  float fSignalFrequency;
	  float fAmplitude;
	  AudioFormat audioFormat;
	  TargetDataLine dataLine;
	  
	  public float getSignalFrequency() { return fSignalFrequency; }
	  public float getAmplitude() { return fAmplitude; }
	  public AudioFormat getAudioFormat() { return audioFormat; }
	  public TargetDataLine getDataLine() { return dataLine; }
	  
	public OscillatorProperties(TargetDataLine dataLine, float fSignalFrequency, float fAmplitude, AudioFormat audioFormat) {
		this.fSignalFrequency	= fSignalFrequency;
		this.fAmplitude			= fAmplitude;
		this.audioFormat		= audioFormat;
		this.dataLine			= dataLine;
	}
	
}