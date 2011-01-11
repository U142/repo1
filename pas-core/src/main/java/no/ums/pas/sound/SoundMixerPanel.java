package no.ums.pas.sound;


import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import no.ums.pas.PAS;
import no.ums.pas.core.defines.*;

import java.awt.*;

public class SoundMixerPanel extends DefaultPanel implements ChangeListener {
	public static final long serialVersionUID = 1;
	JSlider m_slider_recvol = new JSlider(0, 100);
	JSlider m_slider_playvol = new JSlider(0, 100);
	JSlider m_slider_headvol = new JSlider(0, 100);
	JCheckBox m_chk_playmute = new JCheckBox(PAS.l("sound_mixer_mute"));
	JCheckBox m_chk_headmute = new JCheckBox(PAS.l("sound_mixer_mute"));
	SoundMixer m_mixer;
	JLabel m_lbl_record = new JLabel(PAS.l("sound_mixer_recording"));
	JLabel m_lbl_speaker = new JLabel(PAS.l("sound_mixer_volume"));
	JLabel m_lbl_head	=new JLabel(PAS.l("sound_mixer_heading"));
	
	public SoundMixer getMixer() { return m_mixer; }
	
	public SoundMixerPanel() {
		super();
		this.setBorder(BorderFactory.createTitledBorder(PAS.l("sound_mixer_heading")));
		m_mixer = new SoundMixer();
		init();
	}
	
	public void setColor(Color c) {
		this.setBackground(c);
		m_slider_playvol.setBackground(c);
		m_slider_recvol.setBackground(c);
		m_slider_headvol.setBackground(c);
		m_chk_playmute.setBackground(c);
		m_chk_headmute.setBackground(c);
	}
	
	public void init() {
		m_slider_recvol.addChangeListener(this);
		m_slider_playvol.addChangeListener(this);
		m_chk_playmute.addActionListener(this);
		m_slider_headvol.addChangeListener(this);

		m_slider_recvol.setMinorTickSpacing(10);
		m_slider_playvol.setMinorTickSpacing(10);
		m_slider_headvol.setMinorTickSpacing(10);
		m_slider_recvol.setMajorTickSpacing(20);
		m_slider_playvol.setMajorTickSpacing(20);
		m_slider_headvol.setMajorTickSpacing(20);

		m_slider_recvol.setPaintTicks(true);
		m_slider_recvol.setPaintTrack(true);

		m_slider_playvol.setPaintTicks(true);
		m_slider_playvol.setPaintTrack(true);

		m_slider_headvol.setPaintTicks(true);
		m_slider_headvol.setPaintTrack(true);
		
		
		Font f = new Font("Arial", Font.BOLD, 10);
		//FontSet m_slider_recvol.setFont(f);
		//FontSet m_slider_playvol.setFont(f);
		//FontSet m_slider_headvol.setFont(f);
		//FontSet m_chk_playmute.setFont(f);
		//FontSet m_chk_headmute.setFont(f);
		//FontSet m_lbl_record.setFont(f);
		//FontSet m_lbl_speaker.setFont(f);
		//FontSet m_lbl_head.setFont(f);
		
		m_slider_recvol.setValue(m_mixer.getRecVolume());
		m_slider_playvol.setValue(m_mixer.getSpeakerVolume());
		m_chk_playmute.setSelected(m_mixer.getSpeakerMute());
		m_slider_headvol.setValue(m_mixer.getHeadVolume());
		m_chk_headmute.setSelected(m_mixer.getHeadMute());
		
		m_slider_recvol.setOrientation(JSlider.VERTICAL);
		m_slider_playvol.setOrientation(JSlider.VERTICAL);
		m_slider_headvol.setOrientation(JSlider.VERTICAL);
		Dimension d = new Dimension(30, 100);
		m_slider_recvol.setPreferredSize(d);
		m_slider_playvol.setPreferredSize(d);
		m_slider_headvol.setPreferredSize(d);
		m_slider_recvol.setName("Rec");
		m_slider_playvol.setName("Master Volume");
		m_slider_headvol.setName("Wav");

		
		if(!m_mixer.isRecInited()) {
			m_slider_recvol.setEnabled(false);
		}
		if(!m_mixer.isVolInited()) {
			m_slider_playvol.setEnabled(false);
			m_chk_playmute.setEnabled(false);
		}
		if(!m_mixer.isHeadInited()) {
			m_slider_headvol.setEnabled(false);
			m_chk_headmute.setEnabled(false);
		}
		add_controls();
	}
	public void setOscillator(JProgressBar s) {
		s.setOrientation(JProgressBar.VERTICAL);
		s.setPreferredSize(new Dimension(10, 85));
		set_gridconst(9, 1, 1, 1, GridBagConstraints.CENTER);
		add(s, m_gridconst);
	}
	public void add_controls() {
		set_gridconst(0, 0, 3, 1, GridBagConstraints.CENTER);
		add(m_lbl_speaker, m_gridconst);
		set_gridconst(0, 1, 3, 1, GridBagConstraints.CENTER);
		add(m_slider_playvol, m_gridconst);
		set_gridconst(0, 2, 2, 1, GridBagConstraints.CENTER);
		add(m_chk_playmute, m_gridconst);
		
		if(m_mixer.isHeadInited()) {
			set_gridconst(3, 0, 3, 1, GridBagConstraints.CENTER);
			add(m_lbl_head, m_gridconst);
			set_gridconst(3, 1, 3, 1, GridBagConstraints.CENTER);
			add(m_slider_headvol, m_gridconst);
			set_gridconst(4, 2, 2, 1, GridBagConstraints.CENTER);
			add(m_chk_headmute, m_gridconst);
		}
		
		set_gridconst(6, 0, 2, 1, GridBagConstraints.CENTER);
		add(m_lbl_record, m_gridconst);
		set_gridconst(7, 1, 1, 1, GridBagConstraints.CENTER);
		add(m_slider_recvol, m_gridconst);
	}
	
	public void actionPerformed(ActionEvent e) {
		if(e.getSource().equals(m_chk_playmute)) {
			JCheckBox c = (JCheckBox)e.getSource();
			m_mixer.setSpeakerMute(c.isSelected());
		} else if(e.getSource().equals(m_chk_headmute)) {
			JCheckBox c = (JCheckBox)e.getSource();
			m_mixer.setHeadMute(c.isSelected());
		}
	}
	public void stateChanged(ChangeEvent e) {
		if(e.getSource().equals(m_slider_recvol)) {
			JSlider s = (JSlider)e.getSource();
			m_mixer.setRecordingVolume((float)(s.getValue() / 100.0f));
		} else if(e.getSource().equals(m_slider_playvol)) {
			JSlider s = (JSlider)e.getSource();
			m_mixer.setSpeakerVolume((float)(s.getValue() / 100.0f));
		} else if(e.getSource().equals(m_slider_headvol)) {
			JSlider s = (JSlider)e.getSource();
			m_mixer.setHeadVolume((float)(s.getValue() / 100.0f));
		}
	}
	
	
}