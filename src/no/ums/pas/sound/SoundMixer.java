package no.ums.pas.sound;

import java.awt.Component;
import java.util.ArrayList;
import javax.sound.sampled.*;
import javax.swing.JOptionPane;

import no.ums.pas.PAS;
import no.ums.pas.ums.errorhandling.Error;



public class SoundMixer implements LineListener {
	public final int MIXER_TYPE_MIC = 0;
	public final int MIXER_TYPE_SPEAKER = 1;
	public static boolean b_has_reported_error = false;
	
	Mixer.Info [] m_mixerinfo;
	Control [] m_MicVolCtrl;
	Control [] m_MicMuteCtrl;
	Control [] m_SpeakerVolCtrl;
	Control [] m_SpeakerMuteCtrl;
	Control [] m_HeadVolCtrl;
	Control [] m_HeadMuteCtrl;
	
//	Line line_mic;
//	Line line_speaker;

	public String COMPACT_DISC = "COMPACT_DISC";
	public String HEADPHONE = "HEADPHONE";
	public String LINE_IN = "LINE_IN";
	public String LINE_OUT = "LINE_OUT";
	public String MICROPHONE = "MICROPHONE";
	public String SPEAKER = "SPEAKER";
	public float RECORD_VOLUME_LEVEL_MAX = 1.0f;

	private float Volume = RECORD_VOLUME_LEVEL_MAX;	
	
	Port.Info recPortInfo;
	Port.Info volPortInfo;
	Port.Info headPortInfo;
	Port recPort;
	Port speakerPort;
	Port headphonePort;
	
	boolean m_b_rec_inited = false;
	boolean m_b_vol_inited = false;
	boolean m_b_head_inited = false;
	public boolean isRecInited() { return m_b_rec_inited; }
	public boolean isVolInited() { return m_b_vol_inited; }
	public boolean isHeadInited() { return m_b_head_inited; }
	
	public boolean error=false;
	public String str_error_msg="";
	
	public SoundMixer() {
		initMixer();
	}
	
	public void update(LineEvent e) {
		System.out.println("LineEvent " + e.getType());
	}
	
	
	boolean initMixer() {
		boolean result=false;
		try
		{
			result = AudioSystem.getMixerInfo().length > 0;
			System.out.println(AudioSystem.getMixerInfo().length);
		}
		catch (Exception e)
		{
			System.out.println("Could not fetch AudioSystem.getMixerInfo()");
		}
		if(!result)
			return false;
		
		m_mixerinfo = AudioSystem.getMixerInfo();
		
		recPortInfo = new Port.Info(Port.class, MICROPHONE, true);
		volPortInfo = new Port.Info(Port.class, SPEAKER, true);	
		headPortInfo= new Port.Info(Port.class, LINE_OUT, true);
		
		// Used to get only one pop-up message on errors
		
		SoundRecorderPanel.b_line_ok = true;
		
		try {
			recPort = (Port)AudioSystem.getLine(recPortInfo);
			m_b_rec_inited = true;

			//recPort.addLineListener(this);
			/*setControlValues(recPort, MIXER_TYPE_MIC);
			recPort.open();
			setVolume(m_MicVolCtrl, 1.0f);
			recPort.close();*/
		} catch(Exception e) {
			error=true;
			//JOptionPane.showMessageDialog(PAS.get_pas(), PAS.l("sound_mixer_no_mic_lines_found"), PAS.l("common_warning"), JOptionPane.WARNING_MESSAGE);
			str_error_msg = PAS.l("sound_mixer_no_mic_lines_found");
			System.out.println("No MIC lines found");
			SoundRecorderPanel.b_line_ok = false;
		}
		try {
			//Port volPort = (Port)AudioSystem.getLine(volPortInfo);
			//setRecControlValue(volPort);
			  //Mixer mixer = AudioSystem.getMixer(null);
			  Port lineIn = (Port)AudioSystem.getLine(Port.Info.SPEAKER);
			  lineIn.open();
			  
			  //lineIn.addLineListener(this);
			  m_SpeakerVolCtrl = new Control[1];
			  m_SpeakerMuteCtrl = new Control[1];
			  m_SpeakerVolCtrl[0] = (FloatControl) lineIn.getControl(FloatControl.Type.VOLUME);
			  m_SpeakerMuteCtrl[0] = (BooleanControl) lineIn.getControl(BooleanControl.Type.MUTE);
			  ((BooleanControl)m_SpeakerMuteCtrl[0]).setValue(false);
			  m_b_vol_inited = true;
		} catch(Exception e) {
			System.out.println("No Speaker lines found");
			error = true;
			if(str_error_msg.length() > 0)
				str_error_msg += " & " + PAS.l("sound_mixer_no_speaker_lines_found");
			else
				str_error_msg = PAS.l("sound_mixer_no_speaker_lines_found");
			//JOptionPane.showMessageDialog(PAS.get_pas(), PAS.l("sound_mixer_no_speaker_lines_found"), PAS.l("common_warning"), JOptionPane.WARNING_MESSAGE);
			//e.printStackTrace();
			SoundRecorderPanel.b_line_ok = false;
		}
		/*
		if(error && !b_has_reported_error) {
			JOptionPane.showMessageDialog(PAS.get_pas(), str_error_msg, PAS.l("common_warning"), JOptionPane.WARNING_MESSAGE);
			b_has_reported_error = true;
		}*/
		/*try {
			Port headIn = (Port)AudioSystem.getLine(headPortInfo);
			headIn.open();
			m_HeadVolCtrl = new Control[1];
			m_HeadMuteCtrl = new Control[1];
			m_HeadVolCtrl[0] = (FloatControl) headIn.getControl(FloatControl.Type.VOLUME);
			m_HeadMuteCtrl[0] = (BooleanControl) headIn.getControl(BooleanControl.Type.MUTE);
			((BooleanControl)m_HeadMuteCtrl[0]).setValue(false);
		} catch(Exception e) {
			System.out.println("No Headphone lines found");
		}*/
		//setMute(m_MicMuteCtrl, true);

		return true;
	}
	
	public void checkError(Component window) {
		if(error && !b_has_reported_error) {
			JOptionPane.showMessageDialog(window, str_error_msg, PAS.l("common_warning"), JOptionPane.WARNING_MESSAGE);
			b_has_reported_error = true;
		}
	}
	
	void setRecordingVolume(float f) {
		this.Volume = f;
		if(recPort!=null) {
			try {
				setRecControlValue(recPort);
			} catch(Exception e) {
				
			}
		}
	}
	void setSpeakerVolume(float f) {
		try {
			if(m_SpeakerVolCtrl[0]!=null)
			  ((FloatControl)m_SpeakerVolCtrl[0]).setValue(f);			
		} catch(Exception e) {
			
		}
	}
	void setSpeakerMute(boolean b) {
		try {
			if(m_SpeakerVolCtrl[0]!=null)
			  ((BooleanControl)m_SpeakerMuteCtrl[0]).setValue(b);		
		} catch(Exception e) {
			
		}
	}
	void setHeadVolume(float f) {
		try {
			if(m_HeadVolCtrl[0]!=null)
				((FloatControl)m_HeadVolCtrl[0]).setValue(f);
		} catch(Exception e) {
			
		}
	}
	void setHeadMute(boolean b) {
		try {
			if(m_HeadMuteCtrl[0]!=null)
				((BooleanControl)m_HeadMuteCtrl[0]).setValue(b);
		} catch(Exception e) {
			
		}
	}
	public int getRecVolume() {
		//if(recPort!=null)
		//return (int)(0.0f * 100.0f);
		if(recPort==null)
			return 0;
		int n_ret = 0;
		try {
	        recPort.open();
	        Control [] controls = recPort.getControls();
	        for(int i=0; i<controls.length; i++) {
	            if(controls[i] instanceof CompoundControl) { // error
	                Control[] members = ((CompoundControl)controls[i]).getMemberControls();
	                for(int j=0; j<members.length; j++) { // error
	                    //setCtrl(members[j]);
	                	try {
	                		return ((Integer)(getCtrl(members[j]))).intValue();
	                	} catch(Exception e) {
	                		//wait for record volume control
	                	}
	                }
	            } else {
	            	try {
	            		return ((Integer)(getCtrl(controls[i]))).intValue();
	            	} catch(Exception e) {
                		//wait for record volume control	            		
	            	}
	            }
	        }
            recPort.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return n_ret;
	}
	public int getSpeakerVolume() {
		try {
			if(m_SpeakerVolCtrl[0]!=null)
				return (int)(((FloatControl)m_SpeakerVolCtrl[0]).getValue() * 100.0f);						
		} catch(Exception e) {
			
		}
		return (int)(0.0f * 100.0f);
	}
	public boolean getSpeakerMute() {
		try {
			if(m_SpeakerVolCtrl[0]!=null)
				return ((BooleanControl)m_SpeakerMuteCtrl[0]).getValue();		
		} catch(Exception e) {
			
		}
		return false;
	}
	public int getHeadVolume() {
		try {
			if(m_HeadVolCtrl[0]!=null)
				return (int)(((FloatControl)m_HeadVolCtrl[0]).getValue() * 100.0f);
		} catch(Exception e) {
			
		}
		return (int)(0.0f * 100.0f);
	}
	public boolean getHeadMute() {
		try {
			if(m_HeadMuteCtrl[0]!=null)
				return ((BooleanControl)m_HeadMuteCtrl[0]).getValue();
		} catch(Exception e) {
		}
		return false;
	}

	
	void setControlValues(Port inPort, int TYPE) throws Exception {
		inPort.open();
		Control [] controls = inPort.getControls();
		ArrayList<Control> arrBool = new ArrayList<Control>();
		ArrayList<Control> arrFloat = new ArrayList<Control>();
		
		Control [] ctlTempBool;
		Control [] ctlTempFloat;
		
		for(int i=0; i<controls.length; i++) {
			if(controls[i] instanceof CompoundControl) {
				Control[] members = ((CompoundControl)controls[i]).getMemberControls();
				if(members.length > 0) {
					ctlTempFloat = getControls(members, "Volume");
					ctlTempBool  = getControls(members, "Select");
					for(int c=0; c < ctlTempFloat.length; c++) {
						Control control = (Control)ctlTempFloat[c];
						arrFloat.add(control);
					}
					for(int c=0; c < ctlTempBool.length; c++) {
						Control control = (Control)ctlTempBool[c];
						arrBool.add(control);
					}
				}
			} // if
			else {
				ctlTempFloat = getControls(controls, "Volume");
				ctlTempBool  = getControls(controls, "Select");		
				for(int c=0; c < ctlTempFloat.length; c++) {
					Control control = (Control)ctlTempFloat[c];
					arrFloat.add(control);
				}
				for(int c=0; c < ctlTempBool.length; c++) {
					Control control = (Control)ctlTempBool[c];
					arrBool.add(control);
				}
			}
				//setCtrl(controls[i]);
		} // for i
		switch(TYPE) {
		case MIXER_TYPE_MIC:
			if(arrFloat.size()>0) {
				//m_MicVolCtrl = (Control[])arrFloat.toArray();
				Object [] obj = arrFloat.toArray();
				//m_MicVolCtrl = (Control[]) obj;
				m_MicVolCtrl = new Control[obj.length];
				for(int x=0; x < obj.length; x++) {
					m_MicVolCtrl[x] = (Control)obj[x];
				}
			}
			if(arrBool.size()>0) {
				//m_MicMuteCtrl = (Control[])arrBool.toArray();
				Object [] obj = arrBool.toArray();
				m_MicMuteCtrl = new Control[obj.length];
				for(int x=0; x < obj.length; x++) {
					m_MicMuteCtrl[x] = (Control)obj[x];
				}
			}
			break;
		case MIXER_TYPE_SPEAKER:
			m_SpeakerVolCtrl = (Control[])arrFloat.toArray();
			m_SpeakerMuteCtrl = (Control[])arrBool.toArray();
			break;
		}
		inPort.close();
	}
	
	private Control [] getControls(Control [] ctl, String type) {
		//ArrayList arr = new ArrayList();
		int n_count = 0;
		for(int i=0; i < ctl.length; i++) {
			if(ctl[i].getType().toString().equals(type)) {
				n_count++;
				//arr.add(ctl[i]);
			}
		}
		Control [] ret = new Control[n_count];
		int x = 0;
		for(int i=0; i < ctl.length; i++) {
			if(ctl[i].getType().toString().equals(type)) {
				ret[x] = ctl[i];
				x++;
			}
		}
		return ret;
	}

	protected void setMute(Control [] ctl, boolean b) {
		for(int i=0; i < ctl.length; i++) {
			//if(ctl[i].getType().toString().equals("Select")) {
				((BooleanControl)ctl[i]).setValue(b);
			//}
		}
	}
	protected void setVolume(Control []ctl, float val) {
		System.out.println("setVolume");
		for(int i=0; i < ctl.length; i++) {
			System.out.println(ctl[i].getType().toString());
			//if(ctl[i].getType().toString().equals("Volume")) {
				FloatControl vol = (FloatControl) ctl[i];
				float setVal = vol.getMinimum() + (vol.getMaximum() - vol.getMinimum()) * Volume;
				vol.setValue(setVal);
			//}
		}
	}













    public void select(String Zrodlo, float Volume) {        
        this.Volume = Volume;
        try{
            Port.Info recPortInfo =
                    new Port.Info(Port.class,
                    Zrodlo, true);
            
            Port recPort = (Port)
            AudioSystem.getLine(
                    recPortInfo);
            
            setRecControlValue(recPort);
            System.out.println("Selected " + Zrodlo);
            
        }catch (Exception e){}
    }
    private void setRecControlValue(Port inPort) throws Exception {
        inPort.open();
        Control [] controls = inPort.getControls();
        for(int i=0; i<controls.length; i++) {
            if(controls[i] instanceof CompoundControl) { // error
                Control[] members = ((CompoundControl)controls[i]).getMemberControls();
                for(int j=0; j<members.length; j++) { // error
                    setCtrl(members[j]);
                }
            } else {
                setCtrl(controls[i]);
            }
            inPort.close();
        }
    }
    
    private void setCtrl(Control ctl) {
        if(ctl.getType().toString().equals("Select")) {
        	((BooleanControl)ctl).setValue(true);
        }
        if(ctl.getType().toString(
                ).equals("Volume")) {
            FloatControl vol =
                    (FloatControl) ctl;
            float setVal = vol.getMinimum()
            + (vol.getMaximum()
            - vol.getMinimum())
            * Volume;
            vol.setValue(setVal);
        } 
    }
    private Object getCtrl(Control ctl) {
        if(ctl.getType().toString().equals("Select")) {
        	return new Boolean(((BooleanControl)ctl).getValue());
        }
        if(ctl.getType().toString().equals("Volume")) {
            FloatControl vol = (FloatControl) ctl;
            System.out.println(vol.getValue());
            return new Integer((int)(vol.getValue()*100.0f));
        }
        return null;
    }
}

