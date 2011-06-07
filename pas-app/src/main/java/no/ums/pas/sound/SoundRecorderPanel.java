package no.ums.pas.sound;

import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.pas.PAS;
import no.ums.pas.core.defines.DefaultPanel;
import no.ums.pas.localization.Localization;
import no.ums.pas.sound.soundinfotypes.SoundInfoRecord;
import no.ums.pas.ums.errorhandling.Error;
import no.ums.pas.ums.tools.ImageLoader;
import no.ums.pas.ums.tools.StdTextLabel;

import javax.sound.sampled.AudioFormat;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JSlider;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.nio.ByteBuffer;
import java.util.Calendar;
import java.util.Date;
import javax.swing.Timer;



public class SoundRecorderPanel extends DefaultPanel  {
	private static final Log log = UmsLog.getLogger(SoundRecorderPanel.class);
	public static final long serialVersionUID = 1;
	public static final int MODE_INIT_ = 0;
    public static final int MODE_PLAY_ = 1;
    public static final int MODE_PAUSE_ = 2;
    public static final int MODE_RECORD_ = 3;
    public static final int MODE_RECSTOP_ = 4;
	
	@Override
	public void componentHidden(ComponentEvent e) {
		super.componentHidden(e);
	}
	@Override
	public void componentShown(ComponentEvent e) {
		super.componentShown(e);
	}
	public static boolean b_line_ok = false;
	
	private Date date_start;
	private Date date_stop;
	private Record m_rec = null;
	public Record get_recorder() { return m_rec; }
	public SoundPlayer get_player() { return m_player; }
	JButton m_btn_record;
	JButton m_btn_play;
	StdTextLabel m_txt_seconds = new StdTextLabel("");
	public void setSeconds(double n)
	{
		m_txt_seconds.setText(String.format("%.1f " + Localization.l("common_seconds_short"), n));
	}
	JSlider m_slider;
	public JSlider get_slider() { return m_slider; }
	StdTextLabel m_txt_sampleinfo = new StdTextLabel("", 320, 9, false);
	//Sending_Files m_file;
	//SendController m_controller;
	SoundPlayer m_player;
	ImageIcon icon_play;
	ImageIcon icon_record;
	ImageIcon icon_pause;
	ImageIcon icon_stop;
	//Sending_Files get_soundpanel() { return m_file; }
	ActionListener m_callback;
	SoundMixerPanel m_mixer;
	public SoundMixerPanel getMixer() { return m_mixer; }
	public void setMixerColor(Color c) {
		if(m_mixer!=null)
			m_mixer.setColor(c);
	}
	
	public SoundRecorderPanel(ActionListener f, String sz_storagepath, int RECTYPE) {
		this(f, sz_storagepath, RECTYPE, 22050.0F, 16, 1);
	}
	
	public SoundRecorderPanel(ActionListener f, String sz_storagepath, int RECTYPE, float f_samplerate, int n_bits, int n_channels) {
		super();
		AudioFormat format = null;
		try {
			format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, f_samplerate, n_bits, n_channels, n_channels*2, f_samplerate, false);
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
		try {
			m_mixer = new SoundMixerPanel();
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
		m_callback = f;
		//m_controller = controller;
		init();
		setVisible(true);
		//SoundRecorder.LINE_AVAILABLE = true;
		try {
			m_rec = new Record(sz_storagepath/*controller*/, RECTYPE, f, format);
		}
		catch(IllegalArgumentException e)
		{
            JOptionPane.showMessageDialog(this.getRootPane(), e.getMessage(), Localization.l("common_error"), JOptionPane.ERROR_MESSAGE);
            MixerLinesController controller = new MixerLinesController();
            String soundBoard = controller.queryMixersAndLines();
            log.error(e.getMessage());
            log.error(soundBoard);
        	//new MixerLinesView(null, controller).showDlg();
        	//Transferable str = new StringSelection(soundBoard);
        	//Toolkit.getDefaultToolkit().getSystemClipboard().setContents(str, null);
            this.setEnabled(false);
		}
		catch(Exception e)
		{
			//unable to get recording line
			this.setEnabled(false);
			System.out.println(e.getMessage());
		}
		b_line_ok = SoundRecorder.LINE_AVAILABLE;
			
		
		m_txt_seconds.set_width(90);
		m_txt_sampleinfo.setPreferredSize(new Dimension(350,30));

		setAudioFormatText(SoundRecorder.AUDIOFORMAT);
		
		if(b_line_ok) {
			m_btn_play.setEnabled(true);
			m_btn_record.setEnabled(true);
		}
		else {
			m_btn_play.setEnabled(false);
			m_btn_record.setEnabled(false);
		}
		this.addComponentListener(this);
	}
	
	public void setAudioFormatText(AudioFormat af)
	{
        m_txt_sampleinfo.setText("<html>" + 
        		Localization.l("sound_panel_recorder_samplerate") + ": " + 
        						(int)af.getSampleRate() + "Hz " + 
        						//Localization.l("sound_panel_recorder_samplesize") + ": " + 
        						af.getSampleSizeInBits() + "bit " + 
        						//Localization.l("sound_panel_recorder_channels") + ": " + 
        						(af.getChannels()==1 ? Localization.l("sound_panel_recorder_mono") : Localization.l("sound_panel_recorder_stereo")) + "</html>");		
	}
	
	public void setAudioFormatText_Error(String s)
	{
		m_txt_sampleinfo.setText("<html><font color=red>" + s + "</font></html>");
	}
/*	public SoundRecorderPanel(ActionListener f, SendWindow parent) {
		this(f);
		m_parent = parent;		
	}*/
	public void disable_record() {
		m_btn_record.setVisible(false);
	}
	public void cleanUp() {
		get_recorder().cleanUp();
	}
    public void set_mode(int n_mode) {
    	switch(n_mode) {
    		case MODE_INIT_:
    			m_btn_record.setIcon(icon_record);
    			m_btn_play.setIcon(icon_play);
    			m_btn_play.setEnabled(false);
    			break;
    		case MODE_PLAY_:
    			m_btn_play.setIcon(icon_pause);
    			break;
    		case MODE_PAUSE_:
    			m_btn_play.setIcon(icon_play);
    			break;
    		case MODE_RECORD_:
    			m_btn_play.setIcon(icon_play);
    			m_btn_play.setEnabled(false);
    			m_btn_record.setIcon(icon_stop);
    			break;
    		case MODE_RECSTOP_:
    			m_btn_play.setIcon(icon_play);
    			m_btn_play.setEnabled(true);
    			m_btn_record.setIcon(icon_record);
    			break;
    	}
    }
	
    protected final Timer timer_seconds = new Timer(100, this);
	
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() instanceof Timer)
		{
			date_stop = Calendar.getInstance().getTime();
			double diff = (date_stop.getTime() - date_start.getTime()) / 1000.0;
			setSeconds(diff);
			
		}
		if("act_record".equals(e.getActionCommand())) {
			if(get_recorder().toggle_recording(this)) {
				//start timer
				date_start = Calendar.getInstance().getTime();
				timer_seconds.start();
				set_mode(MODE_RECORD_);
				m_btn_play.setEnabled(false);
				if(m_callback!=null)
					m_callback.actionPerformed(e);
			}
			else {
				//stop timer
				timer_seconds.stop();

				set_mode(MODE_RECSTOP_);
				if(get_recorder().get_recorder().RECTYPE==SoundRecorder.RECTYPE_FILE) {
					m_player = new SoundPlayer(get_recorder().get_recorder().get_filename(), 
											   m_slider, this, m_txt_seconds, true);
				} else {
					m_player = new SoundPlayer(get_recorder().get_bytebuffer(),
												m_slider, this, m_txt_seconds, true);
				}
				m_btn_play.setEnabled(true);
				//get_recorder().get_recorder().get_filename()

				
				if(m_callback!=null) {
					if(get_recorder().get_recorder().RECTYPE==SoundRecorder.RECTYPE_FILE)
						m_callback.actionPerformed(new ActionEvent(new SoundInfoRecord(get_recorder().get_recorder().get_file()), ActionEvent.ACTION_PERFORMED, "act_set_soundfiletype"));
					else
						m_callback.actionPerformed(new ActionEvent(new SoundInfoRecord(get_recorder().get_recorder().get_outputstream()), ActionEvent.ACTION_PERFORMED, "act_set_soundfiletype"));
						
				}
					//get_soundpanel().set_soundfiletype(Sending_Files.SOUNDFILE_TYPE_RECORD_, new SoundInfoRecord(get_recorder().get_recorder().get_file()));
				// Dette brukes ved sending, når den har lagret så må det reloades for å kunne gå videre
				//if(get_parent() != null)
				//	get_parent().set_next_text();
				if(m_callback!=null) {
					if(get_recorder().get_recorder().RECTYPE==SoundRecorder.RECTYPE_FILE)
						m_callback.actionPerformed(new ActionEvent(new SoundInfoRecord(get_recorder().get_recorder().get_file()), ActionEvent.ACTION_PERFORMED, "act_set_nexttext"));
					else
						m_callback.actionPerformed(new ActionEvent(new SoundInfoRecord(get_recorder().get_recorder().get_outputstream()), ActionEvent.ACTION_PERFORMED, "act_set_nexttext"));
				}
					
			}
		}
		else if("act_play".equals(e.getActionCommand())) {
			//PAS.get_pas().add_event("act_play");
			if(m_player!=null) {
				if(!m_player.player_ctrl.playing) {
					set_mode(MODE_PLAY_);
					m_player.player_ctrl.play();
				}
				else {
					set_mode(MODE_PAUSE_);
					m_player.player_ctrl.stop();
				}
			}
		}
	}
	public void initialize_player(String sz_filename, boolean b_default_path) {
		try {
			m_player = new SoundPlayer(sz_filename, m_slider, this, m_txt_seconds, b_default_path);
			//this.m_btn_play.setEnabled(true);
			enable_player(true);
			//m_player.reset();
		} catch(Exception e) {
            Error.getError().addError(Localization.l("common_error"),"Exception in initialize_player",e,1);
		}
	}
	public void initialize_player(ByteBuffer buffer, boolean b_default_path) {
		try {
			m_player = new SoundPlayer(buffer, m_slider, this, m_txt_seconds, b_default_path);
			//this.m_btn_play.setEnabled(true);
			enable_player(true);
			//m_player.reset();
		} catch(Exception e) {
            Error.getError().addError(Localization.l("common_error"),"Exception in initialize_player",e,1);
		}
	}
	
	public void enable_player(boolean b_enable) {
		if(b_line_ok)
			m_btn_play.setEnabled(b_enable);
	}
	public void add_controls() {
		//set_gridconst(0, 0, 10, 1);
		//add(new JPanel(), m_gridconst);
		set_gridconst(0, 0, 10, 1, GridBagConstraints.CENTER);
		add(m_slider, m_gridconst);
		set_gridconst(0, 1, 13, 1, GridBagConstraints.SOUTHEAST);
		add(m_txt_sampleinfo, m_gridconst);
		set_gridconst(10, 0, 1, 1, GridBagConstraints.CENTER);
		add(m_txt_seconds, m_gridconst);
		set_gridconst(11, 0, 1, 1, GridBagConstraints.CENTER);
		add(m_btn_record, m_gridconst);
		set_gridconst(12, 0, 1, 1, GridBagConstraints.CENTER);
		add(m_btn_play, m_gridconst);
		//set_gridconst(15, 0, 1, 3, GridBagConstraints.CENTER);
		//add(m_mixer, m_gridconst);
		revalidate();
		doLayout();
	}
	public void showMixer() {
		set_gridconst(13, 0, 1, 1, GridBagConstraints.CENTER);
		add(m_mixer, m_gridconst);		
	}

	public void init() {
		try {
			if(PAS.icon_version==2)
				icon_play	= ImageLoader.load_icon("play_media_24.png");
			else
				icon_play	= ImageLoader.load_icon("play.gif");
			if(PAS.icon_version==2)
				icon_pause	= ImageLoader.load_icon("pause_media_24.png");				
			else
				icon_pause	= ImageLoader.load_icon("pause_all_24.png");
			if(PAS.icon_version==2)
				icon_record = ImageLoader.load_icon("record_media_24.png");
			else
				icon_record = ImageLoader.load_icon("record.gif");
			if(PAS.icon_version==2)
				icon_stop	= ImageLoader.load_icon("stop_media_24.png");
			else
				icon_stop	= ImageLoader.load_icon("stop.gif");
		
			m_btn_record = new JButton(icon_record);
			m_btn_record.addActionListener(this);
			m_btn_record.setActionCommand("act_record");
			m_btn_play = new JButton(icon_play);
			m_btn_play.addActionListener(this);
			m_btn_play.setActionCommand("act_play");
			m_btn_play.setEnabled(false);
			m_btn_record.setPreferredSize(new Dimension(30, 30));
			m_btn_play.setPreferredSize(new Dimension(30, 30));
			if(PAS.icon_version==2)
			{
				m_btn_record.setBorder(null);
				m_btn_play.setBorder(null);
			}
			set_mode(MODE_INIT_);
			m_slider = new JSlider(0, 1, 0);
			m_slider.setPreferredSize(new Dimension(200, 25));
			add_controls();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}