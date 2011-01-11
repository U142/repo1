package no.ums.pas.sound.soundinfotypes;

import java.io.File;

public class SoundInfoLocal extends SoundInfo {
	private File m_f;
	public File get_file() { return m_f; }
	public SoundInfoLocal(File f) {
		super(f.getName());
		m_f = f;
	}
}	