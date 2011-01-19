package no.ums.pas.sound.soundinfotypes;

public class SoundInfoTTS extends SoundInfo {
	private String m_sz_serverfilename;
	public String get_serverfilename() { return m_sz_serverfilename; }
	private String m_sz_text;
	public String get_tts_text() { return m_sz_text; }
	private int m_n_langpk;
	public int get_n_langpk() { return m_n_langpk; }
		
	public SoundInfoTTS(String sz_serverfilename, int n_langpk, String sz_tts_text) {
		super(sz_serverfilename);
		m_sz_serverfilename = sz_serverfilename;
		m_n_langpk = n_langpk;
		m_sz_text = sz_tts_text;
	}
}