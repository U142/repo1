package no.ums.pas.sound.soundinfotypes;

import java.io.*;

import no.ums.pas.PAS;


public class SoundInfoRecord extends SoundInfo {
	private File m_f = null; 
	private OutputStream m_os = null;
	public File get_file() { return m_f; }
	public OutputStream get_os() { return m_os; }
	
	public SoundInfoRecord(File f) {
		super(f.getName());
		m_f = f;
	}
	public SoundInfoRecord(OutputStream os) {
		super(PAS.l("main_sending_audio_use_recorded"));
		m_os = os;
	}
}