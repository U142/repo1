package no.ums.pas.sound.soundinfotypes;

public abstract class SoundInfo extends Object {
	private String m_sz_name;
	public String toString() { return m_sz_name; } 
	public SoundInfo(String sz_name) {
		super();
		m_sz_name = sz_name;
	}

}