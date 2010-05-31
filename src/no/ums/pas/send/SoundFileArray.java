package no.ums.pas.send;

import java.util.ArrayList;

import no.ums.pas.sound.SoundFile;


public class SoundFileArray extends ArrayList<SoundFile> {
	public static final long serialVersionUID = 1;
	public static final int SOUND_FILETYPE_SOUNDLIB_ = 0;
	public static final int SOUND_FILETYPE_TTS_ = 1;
	public static final int SOUND_FILETYPE_REC_ = 2;
	
	public SoundFileArray() {
		super();
	}
	public boolean add(SoundFile file) {
		return super.add(file);
	}
	/*void add(int n_filenumber, int n_filetype, String sz_modulename, String sz_typename) {
		super.add(new SoundFile(n_filenumber, n_filetype, sz_modulename, sz_typename));
	}*/
	
}