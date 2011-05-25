package no.ums.pas.sound;

import no.ums.pas.localization.Localization;

import javax.sound.sampled.AudioFormat;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;


public class Record {
	//private SendController m_controller;
	private ByteBuffer m_current_bytebuffer;
	public ByteBuffer get_bytebuffer() { return m_current_bytebuffer; }
	//private SendController get_sendcontroller() { return m_controller; }
	private SoundRecorder m_rec;
	public SoundRecorder get_recorder() { return m_rec; }
	private String m_sz_path;
	private int RECTYPE;
	private ActionListener m_osc_callback = null;
	AudioFormat m_audioformat;
	
	public Record(String sz_path, int RECTYPE, ActionListener osc_callback, AudioFormat format) 
			throws Exception {
		//m_controller = controller;
		try
		{
			SoundRecorder.InitTargetDataLine(format);
		}
		catch(IllegalArgumentException e)
		{
			throw e;
		}
		catch(Exception e)
		{
			e.printStackTrace();
        	throw e;
		}
		m_sz_path = sz_path;
		m_audioformat = format;
		this.RECTYPE = RECTYPE;
		m_osc_callback = osc_callback;
		if(RECTYPE==SoundRecorder.RECTYPE_OUTPUTSTREAM)
			m_rec = new SoundRecorder(m_sz_path, RECTYPE, m_osc_callback, m_audioformat);
	}
	
	void cleanUp() {
		get_recorder().finalizeRecording();
	}
	
	boolean start_recording() {
		try {
		    /*Timer timer = new javax.swing.Timer(100, new ActionListener( ) {
	            public void actionPerformed(ActionEvent e) { tick( ); }
	        });*/
			if(RECTYPE==SoundRecorder.RECTYPE_FILE)
				m_rec = new SoundRecorder(m_sz_path, RECTYPE, m_osc_callback, m_audioformat);
			else
				m_rec.start_saving_to_stream();
		} catch(Exception e) {
			//PAS.get_pas().add_event("Exception: SoundRecorder " + e.getMessage(), e);
			//Error.getError().addError("Record","Exception in start_recording",e,1);
			e.printStackTrace();
			return false;
		}
		return true;
	}
	void stop_recording() {
		if(RECTYPE==SoundRecorder.RECTYPE_FILE)
			get_recorder().stop_thread();
		else { //just stop storing to outputstream
			get_recorder().stop_saving_to_stream();
		}
		if(RECTYPE==SoundRecorder.RECTYPE_FILE) {
			File f = get_recorder().m_recorder_thread.get_file();
			m_current_bytebuffer = ByteBuffer.allocate((int)f.length());
			try {
				FileInputStream is = new FileInputStream(f);
				is.read(m_current_bytebuffer.array(), 0, (int)f.length()-1);
				is.close();
			} catch(Exception e) {
				//Error.getError().addError("Record","Exception in stop_recording",e,1);
				e.printStackTrace();
			}
		} else {
			//m_current_bytebuffer = get_recorder().get_outputstream();
			//OutputStream os = get_recorder().get_recorder_thread().get_outputstream();
			m_current_bytebuffer = ByteBuffer.wrap(get_recorder().get_recorder_thread().get_outputstream().toByteArray());
		}
			
	}
	public boolean toggle_recording(JComponent parent) {
		if(RECTYPE==SoundRecorder.RECTYPE_FILE) {
			if(get_recorder()==null)
			{
				start_recording();
				return true;
			}
			else if(get_recorder().get_recorder_thread().isRecording()) {
				stop_recording();
				return false;
			}
			else
			{
				start_recording();
				return true;
			}
		} else {
			if(get_recorder().isSaving()) {
				stop_recording();
				return false;
			} else {
				if(m_current_bytebuffer!=null) {
                    if(JOptionPane.showConfirmDialog(parent, Localization.l("sound_recorder_prompt_overwrite"), Localization.l("sound_recorder_prompt_overwrite_heading"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)==JOptionPane.YES_OPTION) {
						
					} else
						return false;
				}
				start_recording();
				return true;
			}
		}
	}
	

}