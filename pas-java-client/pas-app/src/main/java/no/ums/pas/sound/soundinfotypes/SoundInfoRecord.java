package no.ums.pas.sound.soundinfotypes;

import no.ums.pas.localization.Localization;

import java.io.File;
import java.io.OutputStream;


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
        super(Localization.l("main_sending_audio_use_recorded"));
		m_os = os;
	}
}