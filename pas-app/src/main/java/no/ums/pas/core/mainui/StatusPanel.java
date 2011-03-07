package no.ums.pas.core.mainui;

//import no.ums.log.Log;
//import no.ums.log.UmsLog;

import no.ums.pas.PAS;
import no.ums.pas.cellbroadcast.CountryCodes;
import no.ums.pas.core.Variables;
import no.ums.pas.core.defines.ComboRow;
import no.ums.pas.core.defines.ComboRowCellRenderer;
import no.ums.pas.core.defines.DefaultPanel;
import no.ums.pas.core.laf.ULookAndFeel;
import no.ums.pas.localization.Localization;
import no.ums.pas.maps.defines.CommonFunc;
import no.ums.pas.maps.defines.NavStruct;
import no.ums.pas.maps.defines.ShapeStruct;
import no.ums.pas.status.LBASEND;
import no.ums.pas.status.LBATabbedPane;
import no.ums.pas.status.StatusSending;
import no.ums.pas.status.StatusSending.StatusSendingUI;
import no.ums.pas.status.StatusSendingList;
import no.ums.pas.ums.tools.ImageLoader;
import no.ums.pas.ums.tools.StdTextLabel;
import no.ums.ws.common.LBALanguage;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class StatusPanel extends DefaultPanel implements ComponentListener, ItemListener {
	public static final long serialVersionUID = 1;
    //private static final Log logger = UmsLog.getLogger(StatusPanel.class);

    StatuscodeFrame get_statuscodeframe() { return PAS.get_pas().get_statuscontroller().get_statuscodeframe(); }
	InhabitantFrame get_inhabitantframe() { return PAS.get_pas().get_inhabitantframe(); }
	LBATabbedPane get_lbatab() { return PAS.get_pas().get_statuscontroller().getLBATotalPane(); }
	private JProgressBar m_lba_total_progress = new JProgressBar();
	
	private StdTextLabel m_lbl_lba_delivered = new StdTextLabel(Localization.l("main_status_delivered") + ":", 90, 11, false);
    private StdTextLabel m_lbl_lba_expired = new StdTextLabel(Localization.l("main_status_expired") + ":", 90, 11, false);
    private StdTextLabel m_lbl_lba_failed = new StdTextLabel(Localization.l("main_status_failed") + ":", 90, 11, false);
    private StdTextLabel m_lbl_lba_recipients = new StdTextLabel(Localization.l("main_status_subscribers") + ":", 90, 11, false);

    private StdTextLabel m_txt_lba_delivered = new StdTextLabel("", 100, 11, false);
	private StdTextLabel m_txt_lba_expired = new StdTextLabel("", 100, 11, false);
	private StdTextLabel m_txt_lba_failed = new StdTextLabel("", 100, 11, false);
	private StdTextLabel m_txt_lba_recipients = new StdTextLabel("", 100, 11, false);
	
	private StatusSendingList ssl;
	public void set_ssl(StatusSendingList ssl) { this.ssl = ssl; }
	public StatusSendingList get_ssl() { return ssl; }
	
	private JComboBox m_combo_voice_filter = new JComboBox();
	public JComboBox get_combo_filter() { return m_combo_voice_filter; }
	private JProgressBar m_voice_total_progress = new JProgressBar();
	private StdTextLabel lbl_mainstatus;
	
	private JLabel m_lbl_proc_and_total = new JLabel("");

	private final int n_mainstatus_width = 200;
	public DefaultPanel LBAPANEL;
	public DefaultPanel VOICEPANEL;
	public DefaultPanel FILTERPANEL;
	
	boolean b_enable_lba_panel = false;
	protected LBASEND m_lbasend_total;
	
	private float n_lba_prev_perc = 0;
	
	int m_n_voice_items = 0;
	int m_n_lba_items = 0;
	int m_n_voice_proc = 0;
	int m_n_lba_proc = 0;
	
	int m_n_completion_percent = 0;
	
	public void enableLBAPanel(boolean b, LBASEND lbasend_total)
	{
		if(b)
			m_lbasend_total = lbasend_total;
		else
			m_lbasend_total = null;
		b_enable_lba_panel = b;
		LBAPANEL.setVisible(b);
		revalidate(); //resize
	}
	
	public void enableVoicePanel(boolean b)
	{
		VOICEPANEL.setVisible(b);
		revalidate(); //resize		
	}
	
	public int get_filter_type() {
		try {
			return ((StatusSending)((ComboRow)m_combo_voice_filter.getSelectedItem()).getId()).get_type();
		} catch(Exception e) { return -1; }
	}
	
	public void updateMainStatusStatistics()
	{
		int n = 0;
		//if(m_n_voice_items + m_n_lba_items > 0)
		//	n = new Float(((m_n_lba_proc + m_n_voice_proc)* 100) / (m_n_lba_items + m_n_voice_items)).intValue();
		float n_lba_percent, n_voice_percent;
		if(PAS.get_pas().get_userinfo().get_current_department().get_pas_rights()==4) {
			if(m_n_lba_items==0)
				n_lba_percent=100;
			else if(m_n_lba_items>0)
				n_lba_percent = m_n_lba_proc * 100.0f / m_n_lba_items;
			else
				n_lba_percent = 0;
		}
		else {
			if(m_n_lba_items>0)
				n_lba_percent = m_n_lba_proc * 100.0f / m_n_lba_items;
			else
				n_lba_percent = 0;
		}
		
		if(PAS.get_pas().get_userinfo().get_current_department().get_pas_rights()==4) {
			n = (int)Math.round(n_lba_percent);
		}
		else {
			if(m_n_voice_items>0)
				n_voice_percent = m_n_voice_proc * 100.0f / m_n_voice_items;
			else if(m_n_voice_items==0)
				n_voice_percent = 100;
			else 
				n_voice_percent = 0;
			
			if(n_lba_percent>0 && n_voice_percent>0)
				n = (int)(n_voice_percent + n_lba_percent) / 2;
			else if(n_lba_percent>0)
				n = (int)n_lba_percent;
			else if(n_voice_percent>0)
				n = (int)n_voice_percent;
		}
		if(ssl != null) {
			float total_percent = 0.0f; 
			for(int i=0;i<ssl.size();++i) {
				total_percent += ssl.get(i).get_percentage();
			}
			int final_perc = Math.round(total_percent * 100.0f / (100*ssl.size()));
			setMainStatusCompletionPercent(final_perc);
		}
		else
			setMainStatusCompletionPercent(n);
		// Reloads map to get updated GSM coverage
		if(n_lba_percent <= 100 && n_lba_prev_perc < 100) {
			if(n_lba_prev_perc+PAS.get_pas().get_settings().getLbaRefresh() < n_lba_percent || n_lba_percent == 100) {
				PAS.get_pas().get_pasactionlistener().actionPerformed(new ActionEvent(this,ActionEvent.ACTION_PERFORMED,"act_loadmap"));
				n_lba_prev_perc = n_lba_percent;
			}
		}
		
	}
	
	public void updateVoiceStatistics(int n_processed, int n_totitem)
	{
		m_n_voice_items = n_totitem;
		m_n_voice_proc = n_processed;
		m_voice_total_progress.setMinimum(0);
		m_voice_total_progress.setMaximum(n_totitem);
		m_voice_total_progress.setValue(n_processed);
		if(n_totitem<=0)
		{
			m_voice_total_progress.setStringPainted(false);
		}
		else
			m_voice_total_progress.setStringPainted(true);
		
		m_lbl_proc_and_total.setText(m_n_voice_proc + " / " + m_n_voice_items);
	}
	
	public void updateLBAStatistics()
	{
		if(m_lbasend_total==null)
			return;
		m_n_lba_items = m_lbasend_total.n_items;
		m_n_lba_proc = m_lbasend_total.n_proc;
		m_lba_total_progress.setMinimum(0);
		m_lba_total_progress.setMaximum(m_lbasend_total.n_items);
		m_lba_total_progress.setValue(m_lbasend_total.n_proc + m_lbasend_total.getCancelled());
		if(m_lba_total_progress.getMinimum()==m_lba_total_progress.getMaximum() && m_lba_total_progress.getMaximum() == 0) { // Måtte legge dette til for at den skal vise 100%
			m_lba_total_progress.setMaximum(m_lba_total_progress.getMaximum()+1);
			m_lba_total_progress.setValue(m_lba_total_progress.getMaximum());
		}
		m_txt_lba_recipients.setText(Integer.toString(m_lbasend_total.n_items));
		m_txt_lba_failed.setText(Integer.toString(m_lbasend_total.getFailed()));
		m_txt_lba_delivered.setText(Integer.toString(m_lbasend_total.getDelivered()));
		m_txt_lba_expired.setText(Integer.toString(m_lbasend_total.getExpired()));
		get_lbatab().UpdateData(m_lbasend_total);
		//if(m_lbasend_total.n_items < 0)
		if(m_lbasend_total.n_status<LBASEND.LBASTATUS_SENDING && !m_lbasend_total.HasFailedStatus() && !m_lbasend_total.HasFinalStatus())
		{
			m_lba_total_progress.setIndeterminate(true);
			m_lba_total_progress.setStringPainted(false);
		}
		else
		{
			m_lba_total_progress.setIndeterminate(false);
			m_lba_total_progress.setStringPainted(true);
		}
	}
	
	
	
	JTabbedPane m_tab;
	MainView m_main;
	public IconPanelMain m_icon_panel_main;
	public ImageIcon ico_updating;
	
	public void setStatusUpdating(final boolean b)
	{
		//SwingUtilities.invokeLater(new Runnable() {
		//	public void run()
			{
				/*if(b)
					lbl_mainstatus.setIcon(ico_updating);
				else
					lbl_mainstatus.setIcon(null);*/
				lbl_mainstatus.setIcon(null);
				if(b)
				{
					lbl_mainstatus.putClientProperty(ULookAndFeel.WINDOW_LOADING_GRADIENTVALUE, ULookAndFeel.WINDOW_LOADING_INITIAL_GRADIENTVALUE);
				}
				lbl_mainstatus.putClientProperty(ULookAndFeel.WINDOW_LOADING, b);
				m_main.putClientProperty(ULookAndFeel.WINDOW_LOADING, true);
				//m_main.putClientProperty(ULookAndFeel.WINDOW_LOADING, b);
				//this.putClientProperty(ULookAndFeel.WINDOW_LOADING, b);
			}
		//});
	}
	
	public StatusPanel(PAS pas, Dimension size) {
		super();

        lbl_mainstatus = new no.ums.pas.ums.tools.StdTextLabel(Localization.l("main_status"), n_mainstatus_width, 12, true);
		//lbl_mainstatus = new UMS.Tools.StdTextLabel("Status", 100, 12, true);
		ico_updating = ImageLoader.load_icon("remembermilk_orange.gif");
		lbl_mainstatus.setIconTextGap(4);
		lbl_mainstatus.set_height(20);
		lbl_mainstatus.setIcon(ico_updating);//
		
		m_main = new MainView(pas);
		m_tab = new JTabbedPane();
		m_tab.setUI(ULookAndFeel.newUTabbedPaneUI(m_tab, new ULookAndFeel.TabCallback() {
			@Override
			public void CloseButtonClicked(JComponent c) {
				
			}

			@Override
			public void CloseButtonHot(JComponent c) {
				
			}
		}));
		setPreferredSize(size);
		add_controls();
		m_combo_voice_filter.addItemListener(this);
		int height = m_combo_voice_filter.getPreferredSize().height;
		m_combo_voice_filter.setRenderer(new ComboRowCellRenderer(new Class [] { String.class, ImageIcon.class, JLabel.class, JLabel.class, JLabel.class }, new int [] { 40, 40, 250, 80, 60 }, height));
        m_combo_voice_filter.addItem(new ComboRow(null, new Object[] {Localization.l("main_status_filter") + ":", new ImageIcon(""), new JLabel(Localization.l("main_status_filter_none")), m_lbl_proc_and_total, new JLabel("")}));
		addComponentListener(this);
		
	}
	public void add_controls() {
		final StatusPanel panel = this;
		try
		{
			SwingUtilities.invokeLater(new Runnable() {
				public void run()
				{
                    m_tab.addTab(Localization.l("main_status"), null,
								m_main,
                            Localization.l("main_status"));
					set_gridconst(0, 0, 1, 1);
					add(m_tab, m_gridconst);
					m_tab.addComponentListener(panel);
					m_tab.setVisible(true);
				}
			});
		}
		catch(Exception e)
		{
			
		}
		
	}
	
	public void init() {
		setVisible(true);
	}
	public void clear() {
		get_statuscodeframe().clear();
		get_inhabitantframe().clear();
		
	}
	public void componentHidden(ComponentEvent e) { }
	public void componentMoved(ComponentEvent e) { }
	public void componentResized(ComponentEvent e) { 
		try
		{
			if(getWidth()<=0 || getHeight()<=0)
			{
				super.componentResized(e);
				return;
			}
	
			m_tab.setPreferredSize(new Dimension(getWidth(), getHeight()));
			m_tab.setSize(new Dimension(getWidth(), getHeight()));
			/*get_statuscodeframe().setPreferredSize(new Dimension(getWidth()-20,  getHeight()/3-70));
			get_inhabitantframe().setPreferredSize(new Dimension(getWidth()-20,  getHeight()/3-70));*/
			setStatusUpdating(true);
			float voice_percent = 1.0f/2.1f;
			float lba_percent = 1.0f/2.1f;
			int x = getWidth();
			int y = getHeight();
			int n_maxheight = 0;
			for(int tab=0; tab<m_tab.getTabCount(); tab++)
			{
				try
				{
					Rectangle rect = m_tab.getBoundsAt(0);
					if(rect!=null)
					{
						int n_tabheight = rect.y + rect.height;
						n_maxheight = Math.max(n_tabheight, n_maxheight);
					}
				}
				catch(Exception err)
				{
					
				}
			}
			int voicelba_count = 0;
			if(VOICEPANEL.isVisible())
				voicelba_count++;
			if(LBAPANEL.isVisible())
				voicelba_count++;
			float percent = 0.8f / voicelba_count;
			if(voicelba_count>1)
				percent = 0.9f / voicelba_count;
			
			//Dimension dim_voicepart = new Dimension(x-10, new Float(y*(b_enable_lba_panel ? voice_percent : 1)-n_maxheight-(b_enable_lba_panel ? 20 : 100)).intValue());
			//Dimension dim_voicepart = new Dimension(x-10, new Float(y*(percent)-n_maxheight-(b_enable_lba_panel ? 20 : 20)).intValue());
			Dimension dim_voicepart = new Dimension(x-10, new Float(y*(percent)-n_maxheight).intValue());
			VOICEPANEL.setPreferredSize(dim_voicepart);
			//VOICEPANEL.setSize(dim_voicepart);
			VOICEPANEL.validate();
			
			if(b_enable_lba_panel)
			{
				//Dimension dim_lbapart = new Dimension(new Dimension(x-10, new Float(y*lba_percent).intValue()-n_maxheight-20));
				//Dimension dim_lbapart = new Dimension(new Dimension(x-10, new Float(y*percent).intValue()-n_maxheight-50));
				Dimension dim_lbapart = new Dimension(new Dimension(x-10, new Float(y*percent).intValue()-n_maxheight));
				LBAPANEL.setPreferredSize(dim_lbapart);
				//LBAPANEL.setSize(dim_lbapart);
				LBAPANEL.validate();
			}
			setStatusUpdating(false);
		}
		catch(Exception err)
		{
			err.printStackTrace();
		}
		
	}
	
	public void setMainStatusText(String sz)
	{
		if(m_n_completion_percent<0) {
            sz = Localization.l("common_na");
        }
        lbl_mainstatus.setText(Localization.l("main_status") + "   "+m_n_completion_percent+"% " + Localization.l("common_completed") + "   " + sz);
		//lbl_mainstatus.setHorizontalTextPosition(StdTextLabel.CENTER);
		lbl_mainstatus.setHorizontalAlignment(StdTextLabel.CENTER);		
		
	}
	
	protected void setMainStatusCompletionPercent(int n)
	{
		m_n_completion_percent = n;
		setMainStatusText("");
		PAS.get_pas().get_eastcontent().get_statuspanel().setStatusUpdating(false);
	}
	
	int n_componentcount = 0;
	
	public void componentShown(ComponentEvent e) {
		//if(e.getSource().equals(m_tab))
		{
			try
			{
				n_componentcount++;
				//if(e.getSource().equals(m_main))
				{
					lbl_mainstatus.set_width(n_mainstatus_width);
					m_tab.setTabComponentAt(0, lbl_mainstatus);
					m_tab.revalidate();
				}
				//e.getComponent()
				Component comp = m_tab.getTabComponentAt(0);
				comp.setFont(new java.awt.Font(null, java.awt.Font.BOLD, 12));
				
			}
			catch(Exception err){ }
		}
	}
	
	/** Function for HTML-formatting LBALanguage message content*/
	public static String createMessageContentTooltipHtml(LBALanguage l, int n_sendinggroup)
	{
		String tooltip = "";
		String tip = "<html>";

		if(n_sendinggroup==4) //disable this for TAS, same message to all countries
		{
			switch(n_sendinggroup)
			{
			case 4: //LBAS
                tooltip = Localization.l("main_status_lba_sent_to_phones_from");
				break;
			case 5: //TAS
                tooltip = Localization.l("main_status_tas_sent_to_phones_in");
				break;
			}
			tip += "<font face=Verdana size=3><b>" + tooltip + "</b></font><br><font face=Arial size=2>";
			for(int i=0; i < l.getMCcodes().getLBACCode().size(); i++)
			{
				tip += CountryCodes.getCountryByCCode(l.getMCcodes().getLBACCode().get(i).getCcode());
				tip += "<br>";
			}
			tip+="</font>";
			tip+="<br>";
			tip+="<hr noshade>";
			
		}
		
		String temp = l.getSzText();
		temp = temp.replaceAll("\n", "<br>");
		String msgtext = "";
		String [] words = temp.split(" ");
		for(int i=0; i < words.length; i++)
		{
			if((i % 10) == 0)
				msgtext += "<br>";
			msgtext += words[i] + " ";
		}
		//tip += "<b><font face=Arial size=3>" + PAS.l("main_status_sms_content") + "</font></b><hr noshade><br>";
		tip += "<font face=Verdana size=3><b>" + l.getSzCbOadc() + "</b></font><br><font face=Verdana size=3><table wrap=hard><tr><td><b>" + msgtext + "</b></td></tr></table></font>";
		tip+="</html>";
		return tip;
	}
	
	
	public void actionPerformed(ActionEvent e) {
		if("act_add_sending".equals(e.getActionCommand())) { //when a new sending is added to StatusSendingList in StatusController
			StatusSending sending = (StatusSending)e.getSource();
			javax.swing.ImageIcon ico = null;
			String s_channel = "";
			
			switch(sending.get_type())
			{
			case 1: //voice
				{
					ico = ImageLoader.load_icon("sound_18.png");
                    s_channel = Localization.l("main_status_channel_voice") + " ";
				}
				break;
			case 2: //SMS
				ico = ImageLoader.load_icon("text_18.png");
                s_channel = Localization.l("main_status_channel_sms") + " ";
				break;
			case 3: //e-MAIL
				break;
			case 4: //LBA
				ico = ImageLoader.load_icon("lba_18.png");
                s_channel = Localization.l("main_status_channel_lba") + " ";
				break;
			case 5: //TAS
				ico = ImageLoader.load_icon("tas_18.png");
                s_channel = Localization.l("main_status_channel_tas") + " ";
				break;
			case 6: //PA
				ico = ImageLoader.load_icon("text_18.png");
				s_channel = "PA Centric";			}
			
			//m_tab.addTab("PATest", ico, sending.get_uipanel(), "jalla");
			
			if(sending.get_type()==0 || sending.get_type()==1 || sending.get_type()==2 || sending.get_type()==4 || sending.get_type()==5)
				
				sending.getTotalSendingnameLabel().setIcon(ico);
            String tip = "<html><font face=Arial size=4><b>" + Localization.l("main_status_for_refno") + " " + sending.get_refno() + "</b></font>";
				tip += "<br><font face=Arial size=4>\"" + sending.get_sendingname() + "\"</font>";
				if(sending.get_type()==2) //sms
				{
					String temp = sending.get_sms_message_text();
					temp = temp.replaceAll("\n", "<br>");
					String msgtext = "";
					String [] words = temp.split(" ");
					for(int i=0; i < words.length; i++)
					{
						if((i % 10) == 0)
							msgtext += "<br>";
						msgtext += words[i] + " ";
					}//<img src=\"file:PASIcons/text_18.png\">
                    tip += "<br><br><b><font face=Arial size=3>" + Localization.l("main_status_sms_content") + "</font></b><hr noshade><br><font face=Arial size=3><b>" + sending.get_oadc()+ "</b></font><br><font face=Arial size=3><table wrap=hard><tr><td><b>" + msgtext + "</b></td></tr></table><br>&nbsp;</font>";
				}
				tip+="</html>";
				m_tab.addTab(sending.get_sendingname(), ico,
							sending.get_uipanel(),
							tip);
							//"Status for refno " + sending.get_refno());
				//search for the newly added tab
				for(int i=0; i < m_tab.getTabCount(); i++)
				{
					Component c = m_tab.getComponent(i);
					
					//SwingUtilities.invokeLater(new Runnable() {
					//	public void run()
					//	{
							if(c.equals(sending.get_uipanel()))
							{
								//tmp.getTotalSendingnameLabel().setIcon(tmpico);
								m_tab.setTabComponentAt(i, sending.getTotalSendingnameLabel());
								m_tab.setIconAt(i, ico);
								//break;
							}							
					//	}
					//});
					/*try
					{
						StatusSendingUI tmp = (StatusSendingUI)m_tab.getComponent(i);
						if(tmp.get_status_sending().get_refno()==sending.get_refno())
						{
							sending.getTotalSendingnameLabel().setIcon(ico);
							m_tab.setTabComponentAt(i, sending.getTotalSendingnameLabel());							
						}
					}
					catch(Exception err)
					{
						
					}*/
				}
				/*switch(sending.get_type())
				{
				case 1: //voice
					ico = ImageLoader.load_icon("sound_24.gif");
					break;
				case 2: //SMS
					ico = ImageLoader.load_icon("text_24.gif");
					break;
				}*/
            m_combo_voice_filter.addItem(new ComboRow(sending, new Object [] { Localization.l("main_status_filter") + ":", ico, sending.getSendingnameLabel(), sending.getProcessedAndTotalLabel(), sending.getCompletionPercentLabel() } ));
				
			try
			{
				m_tab.setTabComponentAt(++n_componentcount, sending.getTotalSendingnameLabel());
				if(get_statuscodeframe().get_controller().get_sendinglist().size()>1)
					m_main.add_voice_filter_combo();
			}
			catch(Exception err)
			{
				
			}
				//sending.SetStatusNeedsAttention(true);
		}
		else if("act_rem_sending".equals(e.getActionCommand())) { //when a status is opened, close all old tabs
			StatusSending sending = (StatusSending)e.getSource();
			m_tab.remove(sending.get_uipanel());
			n_componentcount--;
			m_combo_voice_filter.removeItem(sending);
		}
		else if("act_rem_all".equals(e.getActionCommand())) {
			try
			{
				SwingUtilities.invokeLater(new Runnable() {
					public void run()
					{
						try
						{
							m_tab.removeAll();
							n_componentcount = 0;
							m_combo_voice_filter.removeAllItems();
						}
						catch(Exception e)
						{
							
						}
					}
				});
			}
			catch(Exception err)
			{
			}
			
			add_controls();
		}
	}
	
	public void setBorderTextLBA(String sz)
	{
		try
		{
			if(PAS.get_pas().get_userinfo().get_current_department().get_pas_rights()==4) {
                LBAPANEL.setBorder(no.ums.pas.ums.tools.TextFormat.CreateStdBorder(" " + Localization.l("main_status_traveller_alert") + "     - " + sz + " "));//BorderFactory.createTitledBorder(null, "Location Based Alert - " + sz, TitledBorder.CENTER, TitledBorder.TOP, new Font("Arial", Font.BOLD, 12)));
            }
			else {
                LBAPANEL.setBorder(no.ums.pas.ums.tools.TextFormat.CreateStdBorder(" " + Localization.l("main_status_locationbased_alert") + "     - " + sz + " "));//BorderFactory.createTitledBorder(null, "Location Based Alert - " + sz, TitledBorder.CENTER, TitledBorder.TOP, new Font("Arial", Font.BOLD, 12)));
            }
		}
		catch(Exception e) { }
	}
	public void setBorderTextVoice(String sz)
	{
		try
		{
            VOICEPANEL.setBorder(no.ums.pas.ums.tools.TextFormat.CreateStdBorder(" " + Localization.l("main_status_addressbased_alert") + "     - " + sz + " "));
		}
		catch(Exception e) {
            //logger.warn("Failed to set border text for voice (%s)", sz, e);
        }
	}
	
	public class MainView extends DefaultPanel {
		public static final long serialVersionUID = 1;
		protected int n_panel_for_voice_filter;
		protected boolean b_voice_filter_added = false;
		
		public MainView(PAS pas) {
			super();
			add_controls();
		}
		public void actionPerformed(ActionEvent e) {
			
		}
		
		public void add_voice_filter_combo()
		{
			if(!b_voice_filter_added)
			{
				try
				{
					//SwingUtilities.invokeLater(new Runnable() {
					//	public void run()
						{
							b_voice_filter_added = true;
							FILTERPANEL.set_gridconst(0, n_panel_for_voice_filter, 1, 1);
							FILTERPANEL.add(m_combo_voice_filter, FILTERPANEL.m_gridconst);
						}
					//});
				}
				catch(Exception e)
				{
					System.out.println(e.getMessage());
				}
			}
		}
		
		public void add_controls() {
			m_gridconst.fill = GridBagConstraints.BOTH;
			set_gridconst(0, 0, 1, 1);
			//add_spacing(DIR_VERTICAL, 10);
			
			
			set_gridconst(0, inc_panels(), 1, 1, GridBagConstraints.CENTER);
			m_icon_panel_main = new IconPanelMain(); 
			add(m_icon_panel_main, m_gridconst);
			add_spacing(DIR_VERTICAL, 10);
			
			add((FILTERPANEL = new DefaultPanel() {
				public static final long serialVersionUID = 1;
				public void add_controls()
				{
					int x_width = 1;
					super.add_spacing(DIR_VERTICAL, 10);
					set_gridconst(0, inc_panels(), x_width, 1, GridBagConstraints.NORTHWEST);
					add(m_combo_voice_filter, m_gridconst);
					addComponentListener(this);
					init();

				}
				public void actionPerformed(ActionEvent e) { }
				public void init() { }
				public void componentResized(ComponentEvent e)
				{
					int x = this.getWidth()-20;
					int y = this.getHeight()-10;
					m_combo_voice_filter.setPreferredSize(new Dimension(x, 26));
					m_combo_voice_filter.setSize(new Dimension(x,26));
					
				}
			}), m_gridconst);
			
			FILTERPANEL.add_controls();
			
			
			set_gridconst(0, inc_panels(), 1, 1, GridBagConstraints.SOUTHWEST);
									
			add((VOICEPANEL = new DefaultPanel() {
				public static final long serialVersionUID = 1;
				public void add_controls()
				{
					int x_width = 1;
					super.add_spacing(DIR_VERTICAL, 10);
					set_gridconst(0, inc_panels(), x_width, 1, GridBagConstraints.NORTHWEST);
					add(m_voice_total_progress, m_gridconst);
					super.add_spacing(DIR_VERTICAL, 10);
					
					n_panel_for_voice_filter = inc_panels();
					
					set_gridconst(0, inc_panels(), x_width, 1, GridBagConstraints.NORTHWEST);
					add(get_statuscodeframe().get_panel(), m_gridconst);
					add_spacing(DIR_VERTICAL, 10);
					set_gridconst(0, inc_panels(), x_width, 1, GridBagConstraints.NORTHWEST);
					add(PAS.get_pas().get_inhabitantframe().get_panel(), m_gridconst);
					addComponentListener(this);
					addComponentListener(StatusPanel.this);
					init();

				}
				public void actionPerformed(ActionEvent e) { }
				public void init() { }
				@Override
				public void setPreferredSize(Dimension d)
				{
					super.setPreferredSize(d);
					doResize();
				}
				void doResize()
				{
					try
					{
						int x = this.getWidth()-20;
						int y = this.getHeight()-10;
						m_voice_total_progress.setPreferredSize(new Dimension(x, 20));
						m_voice_total_progress.setSize(new Dimension(x,20));
						m_combo_voice_filter.setPreferredSize(new Dimension(x, 26));
						m_combo_voice_filter.setSize(new Dimension(x,26));
						m_icon_panel_main.setPreferredSize(new Dimension(x, 40));
						get_statuscodeframe().setPreferredSize(new Dimension(x, y/2-40));
						get_inhabitantframe().setPreferredSize(new Dimension(x, y/2-70));
						m_icon_panel_main.setSize(new Dimension(x, 40));
						get_statuscodeframe().setSize(new Dimension(x, y/2-40));
						get_inhabitantframe().setSize(new Dimension(x, y/2-70));
						
						m_voice_total_progress.invalidate();
						m_icon_panel_main.invalidate();
						get_statuscodeframe().invalidate();
						get_inhabitantframe().invalidate();
					}
					catch(Exception err)
					{
						err.printStackTrace();
					}					
				}
				public void componentResized(ComponentEvent e)
				{
					doResize();
				}
			}), m_gridconst);
			
			VOICEPANEL.add_controls();
			
			
			add_spacing(DIR_VERTICAL, 10);
			set_gridconst(0, inc_panels(), 1, 1, GridBagConstraints.SOUTHWEST);
			
			add((LBAPANEL = new DefaultPanel() {
				public static final long serialVersionUID = 1;
				public void add_controls()
				{
					
					super.add_spacing(DIR_VERTICAL, 10);
					
					set_gridconst(0, inc_panels(), 1, 1);
					add(m_lbl_lba_delivered, m_gridconst);
					set_gridconst(1, get_panel(), 1, 1);
					add(m_txt_lba_delivered, m_gridconst);
					set_gridconst(0, inc_panels(), 1, 1);
					add(m_lbl_lba_failed, m_gridconst);
					set_gridconst(1, get_panel(), 1, 1);
					add(m_txt_lba_failed, m_gridconst);
					set_gridconst(0, inc_panels(), 1, 1);
					add(m_lbl_lba_expired, m_gridconst);
					set_gridconst(1, get_panel(), 1, 1);
					add(m_txt_lba_expired, m_gridconst);
					set_gridconst(0, inc_panels(), 1, 1);
					add(m_lbl_lba_recipients, m_gridconst);
					set_gridconst(1, get_panel(), 1, 1);
					add(m_txt_lba_recipients, m_gridconst);
					
					

					super.add_spacing(DIR_VERTICAL, 10);

					set_gridconst(0, inc_panels(), 4, 1);
					add(m_lba_total_progress, m_gridconst);

					super.add_spacing(DIR_VERTICAL, 10);
					
					
					set_gridconst(0, inc_panels(), 4, 1);
					add(get_lbatab(), m_gridconst);
					addComponentListener(this);
					init();
				}
				public void actionPerformed(ActionEvent e) { }
				public void init() { }
				public void componentResized(ComponentEvent e)
				{
					int x = this.getWidth()-20;
					int y = this.getHeight()-10;
					//get_lbatab().setSize(x-20, y-30);
					m_lba_total_progress.setPreferredSize(new Dimension(x, 20));
					m_lba_total_progress.setSize(new Dimension(x, 20));
					m_lba_total_progress.revalidate();
					get_lbatab().setPreferredSize(new Dimension(x, y - 135));
					get_lbatab().setSize(new Dimension(x, y - 135));
					get_lbatab().revalidate();
				}
			}), m_gridconst);

			setBorderTextLBA("");
			
			LBAPANEL.add_controls();
			//enableOverlayButtons(false);
			init();
		}
		public void init() {
			setVisible(true);
		}
	}
	
	public class IconPanelMain extends DefaultPanel {

		public static final long serialVersionUID = 1;
		JButton m_btn_goto = null;
		
		public IconPanelMain() {
			//m_btn_goto = new JButton(ImageLoader.load_icon("gnome-searchtool_16x16.jpg"));
			if(PAS.icon_version==2)
				m_btn_goto = new JButton(ImageLoader.load_icon("find_32.png"));
			else
				m_btn_goto = new JButton(ImageLoader.load_icon("search_32.png"));
			m_btn_goto.setPreferredSize(new Dimension(40,40));
			m_btn_goto.addActionListener(this);
			add_controls();
            m_btn_goto.setToolTipText(Localization.l("main_status_show_map_of_sending"));
		}
		
		public void actionPerformed(ActionEvent e) {
			if(e.getSource().equals(m_btn_goto)) {
				//Variables.NAVIGATION.gotoMap(PAS.get_pas().get_mappane().get_active_shape().calc_bounds());
				getBoundsTing();
			}	
		}
		
		public NavStruct getBoundsTing() {
			try
			{
				if(m_tab.getTabCount()>1) {
					ShapeStruct[] shapes = new ShapeStruct[m_tab.getTabCount()-1];
					
					for(int i=1;i<m_tab.getTabCount();i++) {
						Component c = m_tab.getComponentAt(i);
						Class cls = c.getClass();
						StatusSendingUI ssu = (StatusSendingUI)m_tab.getComponentAt(i);
						shapes[i-1] = ssu.get_status_sending().get_shape();
					}
					try
					{
						Variables.getNavigation().gotoMap(CommonFunc.calc_bounds(shapes));
					}
					catch(Exception e)
					{
						
					}
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
				
			return null;
		}

		public void add_controls() {
			m_gridconst.fill = GridBagConstraints.BOTH;
			set_gridconst(0, inc_panels(), 1, 1);
			add(m_btn_goto, m_gridconst);
			init();
		}

		public void init() {
			setVisible(true);
		}
		
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		
		if(e.getItem().getClass().equals(ComboRow.class))
		{
			ComboRow row = (ComboRow)e.getItem();
			if(row.getId()!=null)
			{
				if(row.getId().getClass().equals(StatusSending.class))
				{
					StatusSending s = (StatusSending)row.getId();
					String sz_filterby = s.toString();
                    System.out.println(Localization.l("main_status_filter_codes_by") + " " + sz_filterby);
					get_statuscodeframe().setFilter(s);
					return;
				}
			}
			//assume turn off filter
            System.out.println(Localization.l("main_status_filter_codes_disable"));
			get_statuscodeframe().setFilter(null);
		}
	}


}