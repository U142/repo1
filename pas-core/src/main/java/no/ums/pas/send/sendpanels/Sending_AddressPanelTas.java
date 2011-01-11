package no.ums.pas.send.sendpanels;

import no.ums.pas.PAS;
import no.ums.pas.core.ws.WSTasCount;
import no.ums.pas.send.AddressCount;
import no.ums.pas.send.SendPropertiesTAS;
import no.ums.pas.tas.TasHelpers;
import no.ums.pas.tas.TasPanel;
import no.ums.pas.ums.tools.StdTextLabel;
import no.ums.ws.pas.tas.UTASREQUEST;
import no.ums.ws.pas.tas.UTOURISTCOUNT;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;


public class Sending_AddressPanelTas extends Sending_AddressPanel
{
	StdTextLabel lbl_destination = new StdTextLabel(PAS.l("main_tas_destination_country") , true, 150);
	StdTextLabel txt_destination = new StdTextLabel("", true, 200);
	StdTextLabel lbl_addresses = new StdTextLabel(PAS.l("main_tas_touristcount"), true, 150);
	StdTextLabel txt_addresses = new StdTextLabel("", true, 200);
	//StdTextLabel lbl_operators = new StdTextLabel("", true, new Dimension(410, 100));
	JLabel lbl_operators = new JLabel();
	StdTextLabel lbl_runupdate = new StdTextLabel("", true, 350);
	JScrollPane scroll_statistics;
	
	protected List<no.ums.ws.pas.tas.ULBACOUNTRY> m_country = null;
	public Sending_AddressPanelTas(PAS pas, SendWindow parentwin)
	{
		super(pas, parentwin);
		scroll_statistics = new JScrollPane(lbl_operators, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroll_statistics.setPreferredSize(new Dimension(500, 200));
		add_controls();
	}

	@Override
	public final void add_controls() {
		lbl_destination.setFont(lbl_destination.getFont().deriveFont(Font.BOLD));
		txt_destination.setFont(lbl_destination.getFont().deriveFont(Font.BOLD));
		
		set_gridconst(0, inc_panels(), 1, 1);
		add(lbl_destination, m_gridconst);
		set_gridconst(1, get_panel(), 1, 1);
		add(txt_destination, m_gridconst);
		
		add_spacing(DIR_VERTICAL, 10);

		lbl_addresses.setFont(lbl_addresses.getFont().deriveFont(Font.BOLD));
		set_gridconst(0, inc_panels(), 1, 1);
		add(lbl_addresses, m_gridconst);
		set_gridconst(1, get_panel(), 1, 1);
		add(txt_addresses, m_gridconst);

		add_spacing(DIR_VERTICAL, 20);

		set_gridconst(0, inc_panels(), 2, 1);
		add(lbl_runupdate, m_gridconst);

		
		add_spacing(DIR_VERTICAL, 20);

		set_gridconst(0, inc_panels(), 2, 1);
		add(scroll_statistics, m_gridconst);
		
	}

	protected void update_adrcount(boolean callback) 
	{
		SendPropertiesTAS tas = get_parent().get_sendobject().get_sendproperties().typecast_tas();
		m_country = tas.getCountry();
		AddressCount adrcount = new AddressCount();
		int touristcount = 0;
		for(int country = 0; country < tas.getCountry().size(); country++)
		{
			touristcount += tas.getCountry().get(country).getNTouristcount();
		}
		adrcount.set_privatesms(touristcount);
		txt_addresses.setText(new Integer(adrcount.get_privatesms()).toString());
		String sendto = "";
		if(tas.getCountry().size()==1)
			sendto = tas.getCountry().get(0).getSzName();
		else if(tas.getCountry().size()>1)
			sendto = PAS.l("main_tas_send_multiple_countries") + " (" + m_country.size() + ")";
		else
			sendto = PAS.l("common_none");
		txt_destination.setText(sendto);
		String str_operators = "<html><font color=red>" + PAS.l("main_tas_send_no_touristcount_updates") + "</font></html>";
		int n_max_seconds = (int)(TasPanel.TAS_ADRCOUNT_TIMESTAMP_EXPIRED_SECONDS);
		//if(tas.getCountry().getOperators()!=null && tas.getCountry().getOperators().getUTOURISTCOUNT().size()>0)
		{
			str_operators = "<html>";
			str_operators += "<table>";
			for(int country=0; country < tas.getCountry().size(); country++)
			{
				no.ums.ws.pas.tas.ULBACOUNTRY countryitem = tas.getCountry().get(country); 
				for(int i=0; i < tas.getCountry().get(country).getOperators().getUTOURISTCOUNT().size(); i++)
				{
					UTOURISTCOUNT c = tas.getCountry().get(country).getOperators().getUTOURISTCOUNT().get(i);
					str_operators += "<tr>";
						str_operators += "<td width=200>";
						str_operators += "<b><font size=1>" + countryitem.getSzName() + "</font></b>";
						str_operators += "<td width=70>";
							str_operators += "<b><font size=1>" + c.getSzOperator() + "</font></b>";
						str_operators += "</td>";
						str_operators += "<td width=70>";
							str_operators += "<b><font size=1>" + c.getLTouristcount() + "</font></b>";
						str_operators += "</td>";
						//str_operators += "<td width=170>";
						//String color = TasHelpers.getOutdatedColorHex(tas.getCountry().get(country));
						//String datetext = TasHelpers.getOutdatedText(tas.getCountry().get(country));
						String color = TasHelpers.getOutdatedColorHex(c);
						String datetext = TasHelpers.getOutdatedText(c);
						//str_operators += "<font color=\"" + color + "\">";
						//str_operators += UMS.Tools.TextFormat.format_datetime(c.getLLastupdate());
						//str_operators += "</font>";
						str_operators += "<td width=130 color=\"" + color + "\">";
						str_operators += "<font size=1>";
						str_operators += datetext;
						str_operators += "</font>";
						str_operators += "</td>";
						
						if(!TasHelpers.getCountIsOutdated(tas.getCountry().get(country)))
						{
							lbl_runupdate.setText("");
						}
	
					str_operators += "</tr>";
				}
			}
			str_operators += "</table>";
			str_operators += "</html>";
		}
		lbl_operators.setText(str_operators);
		if(callback)
			actionPerformed(new ActionEvent(adrcount, ActionEvent.ACTION_PERFORMED, ADRCOUNT_CALLBACK_ACTION_));
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		if("act_tascount_finished".equals(e.getActionCommand()))
		{
			UTASREQUEST b = (UTASREQUEST)e.getSource();
			System.out.println("TAS adrcount request sent=" + b.isBSuccess());
		}
		super.actionPerformed(e);
	}
	
	@Override
	protected void exec_adrcount() {
		try
		{
			update_adrcount(true);
			List<no.ums.ws.pas.tas.ULBACOUNTRY> list = m_country; //new ArrayList<no.ums.ws.pas.tas.ULBACOUNTRY>();
			//list.add(m_country);
			WSTasCount count = new WSTasCount(this, list);
			//PAS.get_pas().get_eastcontent().get_taspanel().actionPerformed(new ActionEvent(list, ActionEvent.ACTION_PERFORMED, "act_request_touristcount"));
			lbl_runupdate.setText("<html><font color=red>" + PAS.l("main_tas_count_request_sent") + "</font></html>");
		}
		catch(Exception e)
		{
			
		}
	}
	@Override
	public void adrCountChanged(Object o)
	{
		try
		{
			//expect ULBACOUNTRY
			//AddressCount c = (AddressCount)o;
			//txt_addresses.setText(new Integer(c.get_privatesms()).toString());
			//ULBACOUNTRY c = (ULBACOUNTRY)
			update_adrcount(false);
			
		}
		catch(Exception e)
		{
			
		}
	}
}