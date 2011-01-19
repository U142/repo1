package no.ums.pas.sound.soundinfotypes;

public class SoundInfoLibrary extends SoundInfo {
	private int m_n_deptpk = -1;
	private String m_sz_messagepk;
	public int get_deptpk() { return m_n_deptpk; }
	public String get_messagepk () { return m_sz_messagepk; }
	
	public SoundInfoLibrary(int n_deptpk, String sz_messagepk) {
		super(sz_messagepk);
		m_n_deptpk = n_deptpk;
		m_sz_messagepk = sz_messagepk;
	}
}