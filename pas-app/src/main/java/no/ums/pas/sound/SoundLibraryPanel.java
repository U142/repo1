package no.ums.pas.sound;

import com.google.common.io.ByteStreams;
import no.ums.pas.PAS;
import no.ums.pas.core.defines.DefaultPanel;
import no.ums.pas.core.defines.SearchPanelResults;
import no.ums.pas.core.storage.StorageController;
import no.ums.pas.localization.Localization;
import no.ums.pas.send.sendpanels.SendWindow;
import no.ums.pas.send.sendpanels.Sending_Files;
import no.ums.pas.sound.soundinfotypes.SoundInfoLibrary;
import no.ums.pas.ums.errorhandling.Error;

import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Comparator;

public class SoundLibraryPanel extends DefaultPanel {
	public static final long serialVersionUID = 1;
	//private SendController m_controller;
	Sending_Files m_file;
	SendWindow m_parent;
	protected SendWindow get_parent() { return m_parent; }
	SearchPanelSoundlib m_liblist;
	private SoundRecorderPanel m_playpanel;
	public SoundRecorderPanel get_playpanel() { return m_playpanel; }
	public Sending_Files get_soundpanel() { return m_file; }
	
	public void start_progress() {
		get_playpanel().enable_player(false);
        get_parent().set_comstatus(Localization.l("sound_file_type_download_from_lib"));
        get_parent().get_loader().start_progress(0, Localization.l("common_downloading"));
	}
	public void stop_progress() {
		get_playpanel().enable_player(true);
		get_parent().reset_comstatus();
		get_parent().get_loader().reset_progress();
	}	
	
	public SoundLibraryPanel(Sending_Files file, SendWindow parent) {
		super();
		//m_controller = controller;
		m_file = file;
		m_parent = parent;

        String sz_columns[] = {Localization.l("sound_panel_library_file_table_head_name")};
		int n_width[] = { 325 };
		m_liblist = new SearchPanelSoundlib(PAS.get_pas(), this, sz_columns, n_width);
		m_liblist.setPreferredSize(new Dimension(400, 250));
		m_playpanel = new SoundRecorderPanel(file, StorageController.StorageElements.get_path(StorageController.PATH_TEMPWAV_), SoundRecorder.RECTYPE_FILE);
		m_playpanel.disable_record();
		add_controls();
	}
	
	public void actionPerformed(ActionEvent e) {
		if("act_download_finished".equals(e.getActionCommand())) {
			try {
				SoundlibFileWav f = (SoundlibFileWav)e.getSource();
				if(f.m_f.exists())
					m_playpanel.initialize_player(f.get_file().getPath(), true);
				else {
					URL url = new URL(PAS.get_pas().getVB4Url() + "/bbmessages/" + PAS.get_pas().get_userinfo().get_current_department().get_deptpk() + "/" + f.get_file().getName());
					URLConnection urlConn = url.openConnection();
					urlConn.setUseCaches(false);
                    
					ByteBuffer bb = ByteBuffer.wrap(ByteStreams.toByteArray(urlConn.getInputStream()));
					m_playpanel.initialize_player(bb, true);
				}
					
			} catch(Exception err) {
                Error.getError().addError(Localization.l("common_error"),"Exception in actionPerformed",err,1);}
			stop_progress();
		}
	}
	
	public void add_controls() {
		set_gridconst(0, 0, 1, 1, GridBagConstraints.WEST);
		add(m_liblist, m_gridconst);
		set_gridconst(0, 1, 1, 1, GridBagConstraints.WEST);
		add(m_playpanel, m_gridconst);
		init();
	}
	
	public void init() {
		populate_wav();
		setVisible(true);
	}
	public void populate_wav() {
		SoundlibFileWav wav;
		//URL url = new URL(MapObjectPicturepane.load_icon("play.gif"));
		try {
			//prioritet til de 3 siste innringt-wav
			//deretter sortert alfabetisk.
			Collections.sort(m_parent.get_soundlib(), new SoundlibFile.CompareMessagePk());
			int n_pri = 0;
			int n_pri_count = 0;
			for(SoundlibFile s : m_parent.get_soundlib())
			{
				s.setListPriority(s.get_name().startsWith("TLF:") ? ++n_pri : 0);
				if(s.getListPriority()>0)
				{
					System.out.println("type==1 pri="+ n_pri + " wav="+s.get_name());
					if(++n_pri_count>=3)
						break;
				}
			}
			Collections.sort(m_parent.get_soundlib());
			for(int i=0; i < m_parent.get_soundlib().size(); i++) {
				wav = (SoundlibFileWav)m_parent.get_soundlib().get(i);
				Object[] obj_insert = { wav };
				m_liblist.insert_row(obj_insert, -1);
			}
		} catch(Exception e) {
			PAS.get_pas().add_event("ERROR populate_soundlib() " + e.getMessage(), e);
            Error.getError().addError(Localization.l("common_error"),"Exception in populate_wav",e,1);
		}
	}	
	public void load_wav(SoundlibFileWav f) {
		start_progress();
		f.load_file(this, "act_download_finished"); //starts thread, and callback with SoundlibFile as object
		SoundInfoLibrary info = new SoundInfoLibrary(f.get_deptpk(), f.get_messagepk());
		get_soundpanel().set_soundfiletype(Sending_Files.SOUNDFILE_TYPE_LIBRARY_, info);
		// Her gjøres den ferdig og reloader parent for å enable next knappen
		get_parent().set_next_text();
		/*try {
			m_playpanel.initialize_player(f.get_file().getPath(), true);
		} catch(Exception e) {
			
		}
		stop_progress();*/
	}
	
	public class SearchPanelSoundlib extends SearchPanelResults {
		public static final long serialVersionUID = 1;

		private JPanel m_parent;
		private int n_refno_column = 1;
		private int m_player_col = 2;
		int m_n_objectcol = 0;

		
		public SearchPanelSoundlib(PAS pas, SoundLibraryPanel panel, String [] sz_columns, int [] n_width) {
			super(sz_columns, n_width, null, new Dimension(800, 200), ListSelectionModel.SINGLE_SELECTION);
			m_parent = panel;
		}
		
		protected void start_search() {
		}
		protected void valuesChanged() { }

		public boolean is_cell_editable(int row, int col) {
			return is_cell_editable(col);
		}
		protected void onMouseLClick(int n_row, int n_col, Object [] rowcontent, Point p) {
			try {
				//if(n_col==m_player_col) {
					if(get_playpanel().m_player != null && get_playpanel().m_player.player_ctrl != null && get_playpanel().m_player.player_ctrl.playing) {
						get_playpanel().m_player.player_ctrl.stop();
						get_playpanel().m_player.player_ctrl.reset();
					}
					SoundlibFileWav f = (SoundlibFileWav)rowcontent[m_n_objectcol];
					if(get_playpanel().m_player != null && get_playpanel().m_player.player_ctrl != null)
						get_playpanel().m_player.player_ctrl.skip(0);
					try {
						((SoundLibraryPanel)m_parent).load_wav(f);
					} catch(Exception e) {
                        Error.getError().addError(Localization.l("common_error"),"Exception in onMouseLClick",e,1);
					}
					//PAS.get_pas().add_event("Download " + f.get_localfile());
				//}
			} catch(Exception e) {
				PAS.get_pas().add_event("ERROR onMouseLClick " + e.getMessage(), e);
                Error.getError().addError(Localization.l("common_error"),"Exception in onMouseLClick",e,1);
			}
		}
		protected void onMouseLDblClick(int n_row, int n_col, Object [] rowcontent, Point p) {
		}
		protected void onMouseRClick(int n_row, int n_col, Object [] rowcontent, Point p) {
		}
		protected void onMouseRDblClick(int n_row, int n_col, Object [] rowcontent, Point p) {
		}
		public void onDownloadFinished() {
			
		}
	}	
}