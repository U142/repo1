package no.ums.pas.send.sendpanels;

import no.ums.pas.PAS;
import no.ums.pas.core.defines.DefaultPanel;
import no.ums.pas.core.storage.StorageController;
import no.ums.pas.send.SendController;
import no.ums.pas.sound.*;
import no.ums.pas.sound.soundinfotypes.SoundInfo;
import no.ums.pas.sound.soundinfotypes.SoundInfoRecord;
import no.ums.pas.ums.errorhandling.Error;
import no.ums.pas.ums.tools.ImageLoader;
import no.ums.pas.ums.tools.StdTextLabel;
import org.jvnet.substance.SubstanceLookAndFeel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;

public class Sending_Files extends DefaultPanel {
	public static final long serialVersionUID = 1;
	public static final int SOUNDFILE_TYPE_RECORD_	= 1;
	public static final int SOUNDFILE_TYPE_TTS_		= 2;
	public static final int SOUNDFILE_TYPE_LIBRARY_	= 4;
	public static final int SOUNDFILE_TYPE_LOCAL_	= 8;

	@Override
	public void componentShown(ComponentEvent e) {
		m_rec.get_recorder().get_recorder().startRecording();
		super.componentShown(e);
	}
	@Override
	public void componentHidden(ComponentEvent e) {
		m_rec.get_recorder().get_recorder().pauseRecording();
		super.componentHidden(e);
	}
	private int m_n_filetype = -1;
	public int get_filetype() { return m_n_filetype; }
	//private File m_localfile;
	private SoundInfo m_current_file = null;
	public SoundInfo get_current_fileinfo() { return m_current_file; }
	
	public void set_fileinfo(int TYPE, SoundInfo local) {
		m_n_filetype = TYPE;
		m_current_file = local;
	}
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if(get_parent().get_bgimg()!=null)
			g.drawImage(get_parent().get_bgimg(),0,0,getWidth(),getHeight(),this);
	}

	private SendWindow parent = null;
	private SoundRecorderPanel m_rec;
	private SoundTTSPanel m_tts;
	private SoundLibraryPanel m_lib;
	private SoundOpenPanel m_open;
	private SoundFile m_soundfile = null;
	public SendWindow get_parent() { return parent; }
	public SoundFile get_soundfile() { return m_soundfile; }
	public Record get_recorder() { return m_rec.get_recorder(); }
	public SoundRecorderPanel get_recorderpanel() { return m_rec; }
	private JTabbedPane m_tabbedpane;
	private StdTextLabel m_lbl_soundmodule = new StdTextLabel("", 300, 12, true);
	private StdTextLabel m_lbl_soundtype = new StdTextLabel("", 550, 12, true);
	public SendController get_sendcontroller() { return get_parent().get_sendcontroller(); }
	protected Oscillator m_oscillator = null;
	
	
	
	public Sending_Files(PAS pas, SendWindow parentwin, SoundFile file) {
		super();
		this.setPreferredSize(new Dimension(500, 420));
		parent = parentwin;
		m_soundfile = file;
		m_tabbedpane = new JTabbedPane();
		m_tabbedpane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		//Substance 3.3
		m_tabbedpane.putClientProperty(SubstanceLookAndFeel.TABBED_PANE_VERTICAL_ORIENTATION, Boolean.TRUE);
		
		//Substance 5.2
		//m_tabbedpane.putClientProperty(SubstanceLookAndFeel.TABBED_PANE_CONTENT_BORDER_KIND, Boolean.TRUE);
		boolean rec = true;
		try {
			int x = 500; //450
			int y = 400; //300
			m_rec = new SoundRecorderPanel(this, StorageController.StorageElements.get_path(StorageController.PATH_TEMPWAV_), SoundRecorder.RECTYPE_OUTPUTSTREAM); //, parent); //get_parent().get_sendcontroller());
			m_rec.setPreferredSize(new Dimension(x, y));
			m_rec.showMixer();
			try {
				m_rec.get_recorder().get_recorder().init_oscilliator();
			}
			catch(Exception npe) {
				rec = false;
			}
			m_tts = new SoundTTSPanel(this, parent); //get_parent().get_sendcontroller(), parent);
			m_tts.setPreferredSize(new Dimension(x, y));
			m_lib = new SoundLibraryPanel(this, parent); //get_parent().get_sendcontroller(), parent);
			m_lib.setPreferredSize(new Dimension(x, y));
			m_open = new SoundOpenPanel(this, parent); //get_parent().get_sendcontroller());
			m_open.setPreferredSize(new Dimension(x, y));
		} catch(Exception e) {
			PAS.get_pas().add_event("ERROR new SoundRecorderPanel : " + e.getMessage(), e);
			Error.getError().addError(PAS.l("common_error"),"Exception in Sending_Files",e,1);
		}
		
		//if(rec)
			if(PAS.icon_version==2)
				m_tabbedpane.addTab(PAS.l("main_sending_audio_type_record"), ImageLoader.load_and_scale_icon("mic_transparent.gif", 15, 15), m_rec, PAS.l("main_sending_audio_type_record_tooltip"));
			else
				m_tabbedpane.addTab(PAS.l("main_sending_audio_type_record"), ImageLoader.load_icon("mic_20x20.gif"), m_rec, PAS.l("main_sending_audio_type_record_tooltip"));

		if(PAS.icon_version==2)
			m_tabbedpane.addTab(PAS.l("main_sending_audio_type_tts"), ImageLoader.load_and_scale_icon("tts.gif",15,15), m_tts, PAS.l("main_sending_audio_type_tts_tooltip"));
		else
			m_tabbedpane.addTab(PAS.l("main_sending_audio_type_tts"), ImageLoader.load_icon("tts_20x20.gif"), m_tts, PAS.l("main_sending_audio_type_tts_tooltip"));
		
		if(PAS.icon_version==2)
			m_tabbedpane.addTab(PAS.l("main_sending_audio_type_library"), ImageLoader.load_and_scale_icon("library.gif",15,15), m_lib, PAS.l("main_sending_audio_type_library_tooltip"));
		else
			m_tabbedpane.addTab(PAS.l("main_sending_audio_type_library"), ImageLoader.load_icon("library_20x20.png"), m_lib, PAS.l("main_sending_audio_type_library_tooltip"));
			
		if(PAS.icon_version==2)
			m_tabbedpane.addTab(PAS.l("main_sending_audio_type_open"), ImageLoader.load_and_scale_icon("folder_open_24.png",15,15), m_open, PAS.l("main_sending_audio_type_open_tooltip"));
		else
			m_tabbedpane.addTab(PAS.l("main_sending_audio_type_open"), ImageLoader.load_icon("open.gif"), m_open, PAS.l("main_sending_audio_type_open_tooltip"));
		m_lbl_soundmodule.setText(PAS.l("main_sending_info_soundfile_for_module") + " " + get_soundfile().get_modulename());
		
		if(file.get_template() == 1) {
			m_tabbedpane.setSelectedComponent(m_tts);
			for(int i=0;i<m_tts.get_txtlib().getItemCount();++i)
				if(Integer.parseInt(((SoundlibFileTxt)m_tts.get_txtlib().getItemAt(i)).get_messagepk()) == file.get_messagepk())
					m_tts.get_txtlib().setSelectedIndex(i);
		}
		add_controls();
		this.addComponentListener(this);
	} 
	public void add_controls() {
		int x_width = 10;
		set_gridconst(0, inc_panels(), x_width, 1, GridBagConstraints.WEST);
		add(m_lbl_soundmodule, m_gridconst);
		set_gridconst(0, inc_panels(), x_width, 1, GridBagConstraints.WEST);
		add(m_lbl_soundtype, m_gridconst);
		set_gridconst(0, inc_panels(), x_width, 1, GridBagConstraints.WEST);
		add(m_tabbedpane, m_gridconst);
		init();
	}
	public void init() {
		m_tabbedpane.setTabPlacement(JTabbedPane.LEFT);
		/*m_tabbedpane.putClientProperty(
		        SubstanceLookAndFeel.TABBED_PANE_VERTICAL_ORIENTATION_ROTATE_ICONS,
		        Boolean.TRUE);*/
		//TabNumberedPanel tnp1 = new TabNumberedPanel(jtp, 1);
		//tnp1.putClientProperty(
		 // SubstanceLookAndFeel.TABBED_PANE_VERTICAL_ORIENTATION_ROTATE_ICONS,
		//  Boolean.TRUE);
		//m_tabbedpane.addTab("tab1", SubstanceImageCreator.getThemeIcon(null));		
		setVisible(true);
	}
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("act_set_soundfiletype")) {
			set_soundfiletype(Sending_Files.SOUNDFILE_TYPE_RECORD_, new SoundInfoRecord(get_recorder().get_recorder().get_outputstream()));			
		} else if(e.getActionCommand().equals("act_set_nexttext")) {
			parent.set_next_text();
		} else if(e.getActionCommand().equals("act_oscillator_init")) {
			if(m_oscillator==null) {
				try {
					OscillatorProperties prop = (OscillatorProperties)e.getSource();
					m_oscillator = new Oscillator(prop.getSignalFrequency(), prop.getAmplitude(), prop.getAudioFormat(),500, 100);
					m_rec.getMixer().setOscillator(m_oscillator.get_ampl());   
					
				} catch(Exception err) {
					err.printStackTrace();
				}
			}
			m_oscillator.get_ampl().setVisible(true);
		} else if(e.getActionCommand().equals("act_oscillate")) {
			if(m_oscillator!=null) {
				byte [] data = (byte [])e.getSource();
				m_oscillator.Oscillate(Oscillator.WAVEFORM_SINE, data.length, data);
			}
		}
	}
	public void set_soundfiletype(int n_soundtype, SoundInfo f) {
		set_soundtypetext(n_soundtype, f);
	}
	public void set_soundtypetext(int n_soundtype, SoundInfo f) {
		String sz_use = PAS.l("common_using") + ": ";
		switch(n_soundtype) {
			case Sending_Files.SOUNDFILE_TYPE_LIBRARY_:
				sz_use += PAS.l("main_sending_audio_type_library");//"Sound Library File";
			break;
			case Sending_Files.SOUNDFILE_TYPE_LOCAL_:
				sz_use += PAS.l("main_sending_audio_type_open");
			break;
			case Sending_Files.SOUNDFILE_TYPE_RECORD_:
				sz_use += PAS.l("main_sending_audio_use_recorded");
			break;
			case Sending_Files.SOUNDFILE_TYPE_TTS_:
				sz_use += PAS.l("main_sending_audio_use_tts");
			break;
		}
		if(f!=null) {
			sz_use += " (" + f.toString() + ")";
		}
		set_soundtypetext(sz_use);
		set_fileinfo(n_soundtype, f);
	}
	private void set_soundtypetext(String sz_text) {
		m_lbl_soundtype.setText(sz_text);
	}
}