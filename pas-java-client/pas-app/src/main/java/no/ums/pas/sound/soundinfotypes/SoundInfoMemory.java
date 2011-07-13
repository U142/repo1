package no.ums.pas.sound.soundinfotypes;

import no.ums.ws.pas.UCONVERTTTSRESPONSE;

public class SoundInfoMemory extends SoundInfo {
	byte [] wave;
	public byte [] getWave() { return wave; }
	
	public SoundInfoMemory(UCONVERTTTSRESPONSE info)
	{
		super(info.getSzServerFilename());
		this.wave = info.getWav();
	}
}
