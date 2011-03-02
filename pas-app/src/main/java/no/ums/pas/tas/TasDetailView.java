package no.ums.pas.tas;

import no.ums.pas.PAS;
import no.ums.pas.core.defines.DefaultPanel;
import no.ums.pas.tas.statistics.UMSChartFrame;
import no.ums.pas.tas.treenodes.CommonTASListItem;
import no.ums.pas.tas.treenodes.CountryListItem;
import no.ums.pas.ums.tools.ImageLoader;
import no.ums.pas.ums.tools.StdTextLabel;
import no.ums.ws.common.ULBACOUNTRY;
import no.ums.ws.common.UTOURISTCOUNT;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;



public class TasDetailView extends DefaultPanel
{
	//countrycode, iso, name, continent, isonumeric, touristcount, lastupdate
	//send show history
	
	//ULBACOUNTRY m_selected_country = null;
	CommonTASListItem m_selected_item = null;
	CommonTASListItem sent_to_sendwindow = null;
	List<CountryListItem> m_list_sendlist = new ArrayList<CountryListItem>();
	public List<CountryListItem> getSendList() { return m_list_sendlist; }
	public void clearSendList()
	{
		for(int i=0; i < getSendList().size(); i++)
		{
			m_list_sendlist.get(i).setAddedToSendList(false);
		}
		getSendList().clear();
		setCountry(m_selected_item);
	}
	public void addToSendList(final CountryListItem c)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				getSendList().add(c);
				c.setAddedToSendList(true);
				setCountry(m_selected_item);
			}
		});
		//btn_add_to_sendlist.setEnabled(false);
	}
	public void removeFromSendList(final CountryListItem c)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				getSendList().remove(c);
				c.setAddedToSendList(false);
				setCountry(m_selected_item);
			}
		});

		//tn_add_to_sendlist.setEnabled(true);
	}
	public CommonTASListItem getCountrySentToSendWindow() { return sent_to_sendwindow; }

	
	StdTextLabel lbl_countrycode	= new StdTextLabel(PAS.l("common_countrycode"), true, 150);
	StdTextLabel txt_countrycode	= new StdTextLabel("", true, 150);
	StdTextLabel lbl_iso			= new StdTextLabel(PAS.l("common_iso3166"), true, 150);
	StdTextLabel txt_iso			= new StdTextLabel("", true, 150);
	StdTextLabel lbl_name			= new StdTextLabel(PAS.l("common_name"), true, 150);
	StdTextLabel txt_name			= new StdTextLabel("", true, 150);
	StdTextLabel lbl_continent		= new StdTextLabel(PAS.l("common_continent"), true, 150);
	StdTextLabel txt_continent		= new StdTextLabel("", true, 150);
	StdTextLabel lbl_isonumeric		= new StdTextLabel(PAS.l("common_iso3166_num"), true, 150);
	StdTextLabel txt_isonumeric		= new StdTextLabel("", true, 150);
	StdTextLabel lbl_touristcount	= new StdTextLabel(PAS.l("main_tas_touristcount"), true, 150);
	StdTextLabel txt_touristcount	= new StdTextLabel("", true, 150);
	StdTextLabel lbl_lastupdate		= new StdTextLabel(PAS.l("main_tas_lastupdate"), true, 150);
	StdTextLabel txt_lastupdate		= new StdTextLabel("", true, 150);
	StdTextLabel lbl_lastupdate_diff= new StdTextLabel("", true, 150);
	JButton btn_sendmessage			= new JButton(no.ums.pas.ums.tools.ImageLoader.load_icon("outbox_32.png"));
	JButton btn_add_to_sendlist		= new JButton(no.ums.pas.ums.tools.ImageLoader.load_icon("add_32.png"));
	JButton btn_showhistory			= new JButton(no.ums.pas.ums.tools.ImageLoader.load_icon("history_32.png"));
	JButton btn_request_touristcount 	= new JButton(no.ums.pas.ums.tools.ImageLoader.load_icon("refresh_32.png"));

	ActionListener m_callback = null;
	public TasDetailView(ActionListener callback)
	{
		super();
		m_callback = callback;
		//setBackground(SubstanceLookAndFeel.getActiveColorScheme().getUltraLightColor());
		setBorder(no.ums.pas.ums.tools.TextFormat.CreateStdBorder(""));
		add_controls();
		init();
		setCountry(null);
		btn_sendmessage.addActionListener(this);
		btn_sendmessage.setActionCommand("act_send_tas");
		btn_add_to_sendlist.addActionListener(this);
		btn_add_to_sendlist.setActionCommand("act_add_to_sendlist"); //act_add_to_sendlist");
		btn_showhistory.addActionListener(this);
		btn_showhistory.setActionCommand("act_show_history");
		btn_request_touristcount.addActionListener(m_callback);
		btn_request_touristcount.setActionCommand("act_request_touristcount");
		btn_request_touristcount.setToolTipText(PAS.l("main_tas_panel_request_touristcount"));
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if("act_send_tas".equals(e.getActionCommand()))
		{
			if(PAS.get_pas().get_current_project() != null && PAS.get_pas().get_current_project().is_saved()){
				PAS.get_pas().keep_using_current_tas();
			}
			if(m_selected_item!=null && m_selected_item.getClass().equals(CountryListItem.class))
			{
				System.out.println("Send TAS message");
				PAS.get_pas().actionPerformed(new ActionEvent(((CountryListItem)m_selected_item).getCountry(), ActionEvent.ACTION_PERFORMED, e.getActionCommand()));
				sent_to_sendwindow = m_selected_item;
			}
		}
		else if("act_send_tas_multiple".equals(e.getActionCommand()))
		{
			if(getSendList().size()>0)
			{
				System.out.println("Send TAS message");
				List<ULBACOUNTRY> list = new ArrayList<ULBACOUNTRY>();
				for(int i=0; i < getSendList().size(); i++)
					list.add(getSendList().get(i).getCountry());
				PAS.get_pas().actionPerformed(new ActionEvent(list, ActionEvent.ACTION_PERFORMED, e.getActionCommand()));	
				sent_to_sendwindow = m_selected_item;				
			}
		}
		else if("act_add_to_sendlist".equals(e.getActionCommand()))
		{
			CountryListItem ci = (CountryListItem)m_selected_item;
			if(ci.isAddedToSendList())
			{
				removeFromSendList(ci);
				//setCountry(ci);
				m_callback.actionPerformed(new ActionEvent(ci, ActionEvent.ACTION_PERFORMED, e.getActionCommand()));
			}
			else
			{
				addToSendList(ci);
				//setCountry(ci);
				m_callback.actionPerformed(new ActionEvent(ci, ActionEvent.ACTION_PERFORMED, e.getActionCommand()));	
			}
			System.out.println("Add to sending list");
		}
		else if("act_remove_from_sendlist".equals(e.getActionCommand()))
		{
			CountryListItem ci = (CountryListItem)m_selected_item;
			removeFromSendList(ci);
			//setCountry(ci);
			m_callback.actionPerformed(new ActionEvent(ci, ActionEvent.ACTION_PERFORMED, e.getActionCommand()));
		}
		else if("act_clear_sendlist".equals(e.getActionCommand()))
		{
			clearSendList();
			//setCountry(m_selected_item);
			m_callback.actionPerformed(new ActionEvent(m_selected_item, ActionEvent.ACTION_PERFORMED, e.getActionCommand()));
		}
		else if("act_show_history".equals(e.getActionCommand()))
		{
			System.out.println("Show TAS history");
			List<ULBACOUNTRY> list = new ArrayList<ULBACOUNTRY>();
			list.add(((CountryListItem)m_selected_item).getCountry());
			//TasChart chart = new ChartOverTime(list);
			//chart.UpdateChart();
			UMSChartFrame frame = new UMSChartFrame(PAS.l("main_tas_stats_heading_over_time"), true, list);
			//UMSChartFrame frame = new UMSChartFrame("Statistics", histogram.getChart(), true);
		}
		else if("act_tas_show_statistics_multiple".equals(e.getActionCommand()))
		{
			List<ULBACOUNTRY> list = new ArrayList<ULBACOUNTRY>();
			for(int i=0; i < getSendList().size(); i++)
			{
				list.add(getSendList().get(i).getCountry());
			}
			//TasChart chart = new ChartOverTime(list);
			//chart.UpdateChart();
			UMSChartFrame frame = new UMSChartFrame(PAS.l("main_tas_stats_heading_over_time"), true, list);

		}

		/*else if("act_request_touristcount".equals(e.getActionCommand()))
		{
			try
			{
				if(m_selected_country!=null)
				{
					List<ULBACOUNTRY> list = new ArrayList<ULBACOUNTRY>();
					list.add(m_selected_country);
					WSTasCount count = new WSTasCount(this, list);
				}
			}
			catch(Exception err)
			{
				err.printStackTrace();
			}
		}
		else if("act_tascount_finished".equals(e.getActionCommand()))
		{
			try
			{
				UTASREQUEST req = (UTASREQUEST)e.getSource();
				if(req.isBSuccess())
				{
					System.out.println("Count started="+req.getNRequestpk() + " for " + req.getList().getULBACOUNTRY().get(0).getSzName());
				}
			}
			catch(Exception err)
			{
				err.printStackTrace();
			}
		}*/

	}

	@Override
	public void add_controls() {
		set_gridconst(0, inc_panels(), 3, 1);
		add(new JPanel() {
			protected JPanel init()
			{
				this.setLayout(new FlowLayout());
				add(btn_add_to_sendlist);
				add(btn_sendmessage);
				add(btn_request_touristcount);
				add(btn_showhistory);
				return this;
			}
		}.init(), m_gridconst);
		/*add(btn_sendmessage, m_gridconst);
		set_gridconst(1, get_panel(), 1, 1);
		add(btn_request_touristcount, m_gridconst);
		set_gridconst(2, get_panel(), 1, 1);
		add(btn_showhistory, m_gridconst);*/

		add_spacing(DIR_VERTICAL, 10);

		set_gridconst(0, inc_panels(), 1, 1);
		add(lbl_countrycode, m_gridconst);
		set_gridconst(1, get_panel(), 2, 1);
		add(txt_countrycode, m_gridconst);

		add_spacing(DIR_VERTICAL, 10);
		
		set_gridconst(0, inc_panels(), 1, 1);
		add(lbl_iso, m_gridconst);
		set_gridconst(1, get_panel(), 2, 1);
		add(txt_iso, m_gridconst);

		add_spacing(DIR_VERTICAL, 10);
		
		set_gridconst(0, inc_panels(), 1, 1);
		add(lbl_isonumeric, m_gridconst);
		set_gridconst(1, get_panel(), 2, 1);
		add(txt_isonumeric, m_gridconst);

		add_spacing(DIR_VERTICAL, 30);
	
		set_gridconst(0, inc_panels(), 1, 1);
		add(lbl_touristcount, m_gridconst);
		set_gridconst(1, get_panel(), 2, 1);
		add(txt_touristcount, m_gridconst);

		add_spacing(DIR_VERTICAL, 10);

		set_gridconst(0, inc_panels(), 1, 1);
		add(lbl_lastupdate, m_gridconst);
		set_gridconst(1, get_panel(), 2, 1);
		add(txt_lastupdate, m_gridconst);
		set_gridconst(1, inc_panels(), 2, 1);
		add(lbl_lastupdate_diff, m_gridconst);

	
	}

	@Override
	public void init() {
		btn_sendmessage.setToolTipText(PAS.l("main_tas_send_message"));
		btn_showhistory.setToolTipText(PAS.l("main_tas_show_country_history"));
		Font f = txt_touristcount.getFont();
		f = f.deriveFont(Font.BOLD);
		lbl_touristcount.setFont(f);
		txt_touristcount.setFont(f);
		lbl_lastupdate.setFont(f);
		txt_lastupdate.setFont(f);
	}
	
	public void updateCurrentCountry()
	{
		setCountry(m_selected_item);
	}
	
	public void setCountry(CommonTASListItem item)
	{
		m_selected_item = item;
		ULBACOUNTRY c = null;
		CountryListItem ci = (CountryListItem)item;
		if(m_selected_item!=null && m_selected_item.getClass().equals(CountryListItem.class))
			c = ((CountryListItem)item).getCountry();
		
		boolean b_selected = true;
		String szcountrycode = "";
		String sziso = "";
		String szname = "";
		String szcontinent = "";
		String szisonumeric ="";
		String sztouristcount = "";
		String szlastupdate ="";
		String szlastupdatediff = "";
		if(c==null)
			b_selected = false;
		if(!b_selected)
			setBorder(no.ums.pas.ums.tools.TextFormat.CreateStdBorder(""));//PAS.l("adrsearch_dlg_country")));
		else
		{
			szcountrycode = new Long(c.getLCc()).toString();
			sziso = c.getSzIso();
			szname = c.getSzName();
			szcontinent = new Integer(c.getLContinentpk()).toString();
			szisonumeric = new Integer(c.getLIsoNumeric()).toString();
			sztouristcount = new Integer(c.getNTouristcount()).toString();
			szlastupdate = no.ums.pas.ums.tools.TextFormat.format_datetime(c.getNOldestupdate());
			if(c.getNOldestupdate()>0)
			{
				/*long diff = UMS.Tools.TextFormat.datetime_diff_minutes(c.getNOldestupdate(), TasPanel.SERVER_CLOCK);
				if(diff > TasPanel.TAS_ADRCOUNT_TIMESTAMP_EXPIRED_SECONDS/60.0)
					txt_lastupdate.setForeground(Color.red);
				else
					txt_lastupdate.setForeground(Color.black);
				txt_lastupdate.repaint();
				
				if(diff>60*24)
					szlastupdatediff += diff /60/24 + " " + PAS.l("common_days_maybe") + " " + PAS.l("common_ago");
				else if(diff>60)
					szlastupdatediff += diff /60 + " " + PAS.l("common_hours_maybe") + " " + PAS.l("common_ago");
				else
					szlastupdatediff += diff + " " + PAS.l("common_minutes_maybe") + " " + PAS.l("common_ago");*/
				txt_lastupdate.setForeground(TasHelpers.getOutdatedColor(c));
				szlastupdatediff = TasHelpers.getOutdatedText(c);
			}
			else
				szlastupdatediff = PAS.l("common_na");
			setBorder(no.ums.pas.ums.tools.TextFormat.CreateStdBorder(szname));
			if(c.getOperators()!=null)
			{
				String tooltip = "<html><table>";
				List<UTOURISTCOUNT> operators = c.getOperators().getUTOURISTCOUNT();
				int i=0;
				for(i=0; i < operators.size(); i++)
				{
					tooltip += "<tr><td><b>" + operators.get(i).getSzOperator() + "</b></td><td><b>" + operators.get(i).getLTouristcount() + "</b></td><td>" + no.ums.pas.ums.tools.TextFormat.format_datetime(operators.get(i).getLLastupdate()) + "</td></tr>";
				}
				tooltip += "</html>";
				if(i>0)
					txt_lastupdate.setToolTipText(tooltip);
				else
					txt_lastupdate.setToolTipText(null);
				
				/*javax.swing.Action toolTipAction = txt_lastupdate.getActionMap().get("postTip");
				if (toolTipAction != null)
				{
					ActionEvent postTip = new ActionEvent(txt_lastupdate, ActionEvent.ACTION_PERFORMED, "");
					toolTipAction.actionPerformed( postTip );
				}*/				
				
			}
		}
		txt_countrycode.setText("+" + szcountrycode);
		txt_iso.setText(sziso);
		txt_name.setText(szname);
		txt_continent.setText(szcontinent);
		txt_isonumeric.setText(szisonumeric);
		txt_touristcount.setText(sztouristcount);
		txt_lastupdate.setText(szlastupdate);
		lbl_lastupdate_diff.setText(szlastupdatediff);
		btn_sendmessage.setEnabled(b_selected && c.getLCc()>0 && m_list_sendlist.size() == 0 && (PAS.get_pas().get_rightsmanagement().cansend() || PAS.get_pas().get_rightsmanagement().cansimulate()) && !ci.isAddedToSendList());
		if(ci==null)
		{
			btn_add_to_sendlist.setEnabled(false);
		}
		else if(!ci.isAddedToSendList())
		{
			btn_add_to_sendlist.setEnabled(true);
			btn_add_to_sendlist.setIcon(ImageLoader.load_icon("add_32.png"));
			btn_add_to_sendlist.setToolTipText(PAS.l("main_tas_add_to_sendlist"));
		}
		else
		{
			btn_add_to_sendlist.setEnabled(true);
			btn_add_to_sendlist.setIcon(ImageLoader.load_icon("subtract_32.png"));			
			btn_add_to_sendlist.setToolTipText(PAS.l("main_tas_remove_from_sendlist"));
		}
		//btn_add_to_sendlist.setEnabled(b_selected && c.getLCc()>0 && (PAS.get_pas().get_rightsmanagement().cansend() || PAS.get_pas().get_rightsmanagement().cansimulate()) && !ci.isAddedToSendList());
		btn_request_touristcount.setEnabled(b_selected && !item.getCountInProgress());
		btn_showhistory.setEnabled(ci!=null); //b_selected);
		try
		{
			//AddressCount count = new AddressCount();
			//count.set_privatesms(c.getNTouristcount());
			PAS.get_pas().get_sendcontroller().get_activesending().get_sendwindow().actionPerformed(new ActionEvent(c, ActionEvent.ACTION_PERFORMED, "act_adrcount_changed"));
		}
		catch(Exception e)
		{
			
		}
		
	}
	
	
}