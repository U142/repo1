package no.ums.pas.sound.soundinfotypes;

import no.ums.ws.pas.UCONVERTTTSRESPONSE;

public class SoundInfoTTS extends SoundInfo {
	//private String m_sz_serverfilename;
	//public String get_serverfilename() { return m_sz_serverfilename; }
	private String m_sz_text;
	public String get_tts_text() { return m_sz_text; }
	private int m_n_langpk;
	public int get_n_langpk() { return m_n_langpk; }
	private UCONVERTTTSRESPONSE response;
	public UCONVERTTTSRESPONSE getResponse() { return response; }
	public SoundInfoTTS(UCONVERTTTSRESPONSE response, int n_langpk, String sz_tts_text) {
		super(response.getSzServerFilename());
		//m_sz_serverfilename = sz_serverfilename;
		this.response = response;
		m_n_langpk = n_langpk;
		m_sz_text = sz_tts_text;
	}
}