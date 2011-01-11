package no.ums.pas.sound;

import javax.swing.*;

import no.ums.pas.*;
import no.ums.pas.core.defines.*;
import no.ums.pas.core.storage.StorageController;
import no.ums.pas.send.sendpanels.*;
import no.ums.pas.sound.soundinfotypes.*;
import no.ums.pas.ums.errorhandling.Error;
import no.ums.pas.ums.tools.FilePicker;

import java.io.*;
import java.awt.*;
import java.awt.event.*;

public class SoundOpenPanel extends DefaultPanel {
	public static final long serialVersionUID = 1;
	//private SendController m_controller;
	private JTextField m_txt_file = new JTextField("");
	private JButton m_btn_open = new JButton(PAS.l("common_browse"));
	private File m_file;
	public File get_file() { return m_file; }
	private SoundRecorderPanel m_play;
	private Sending_Files m_soundfile;
	public Sending_Files get_soundpanel() { return m_soundfile; }
	private SendWindow m_parent = null;
	public SendWindow get_parent() { return m_parent; }
	
	private String sz_filter [][] = new String[][] {
			{ PAS.l("sound_panel_open_filefilter"), "wav" }
	};
	
	public SoundOpenPanel(Sending_Files f) {
		super();
		m_soundfile = f;
		//m_controller = controller;
		m_txt_file.setEditable(false);
		m_txt_file.setPreferredSize(new Dimension(250, 20));
		m_btn_open.setActionCommand("act_open_file");
		m_btn_open.addActionListener(this);
		m_play = new SoundRecorderPanel(m_soundfile, StorageController.StorageElements.get_path(StorageController.PATH_TEMPWAV_), SoundRecorder.RECTYPE_FILE);
		m_play.disable_record();
		add_controls();
	}
	public SoundOpenPanel(Sending_Files f, SendWindow parent) {
		this(f);
		m_parent = parent;
	}
	public void actionPerformed(ActionEvent e) {
		if("act_open_file".equals(e.getActionCommand())) {
			if(open_file()) {
				m_txt_file.setText(m_file.getPath());
				SoundInfoLocal info = new SoundInfoLocal(m_file);
				get_soundpanel().set_soundfiletype(Sending_Files.SOUNDFILE_TYPE_LOCAL_, info);
				// Her gjøres den ferdig og reloader parent for å enable next knappen
				if(getParent() != null)
					get_parent().set_next_text();
				play_file();
			}
		}
	}
	public void play_file() {
		try {
			m_play.initialize_player(m_file.getPath(), false);
		} catch(Exception e) {
			PAS.get_pas().add_event("Could not initialize SoundPlayer", e);
			Error.getError().addError(PAS.l("common_error"),"Exception in play_file",e,1);
		}
	}
	public boolean open_file() {
		FilePicker picker = new FilePicker(this, 
				StorageController.StorageElements.get_path(StorageController.PATH_TEMPWAV_),
				PAS.l("sound_panel_open_file_title"), sz_filter, FilePicker.MODE_OPEN_);
		m_file = picker.getSelectedFile();
		try
		{
			if(m_file == null)
				return false;
			if(m_file.canRead())
				return true;
			else
				return false;
		}
		catch(Exception e)
		{
			return false;
		}
	}
	public void init() {
		setVisible(true);
	}
	public void add_controls() {
		set_gridconst(0, 0, 1, 1, GridBagConstraints.WEST);
		add(m_txt_file, m_gridconst);
		set_gridconst(2, 0, 1, 1, GridBagConstraints.WEST);
		add(m_btn_open, m_gridconst);
		set_gridconst(0, 2, 2, 1, GridBagConstraints.WEST);
		add(m_play, m_gridconst);
		init();
	}
	
}