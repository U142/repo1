package no.ums.pas.send.sendpanels;

import java.awt.Dimension;

import java.awt.event.*;

import no.ums.pas.PAS;
import no.ums.pas.importer.gis.GISList;
import no.ums.pas.importer.gis.GISRecord;
import no.ums.pas.maps.defines.Inhabitant;
import no.ums.pas.maps.defines.InhabitantBasics;
import no.ums.pas.send.*;
import no.ums.pas.ums.tools.StdTextLabel;
/*
 * Show description of selected area and addresscount
 */
public class Sending_AddressPanelGIS extends Sending_AddressPanel {
	public static final long serialVersionUID = 1;
	private StdTextLabel m_lbl_pointcount;
	
	public Sending_AddressPanelGIS(PAS pas, SendWindow parentwin) {
		super(pas, parentwin); // Houses er null, må få tak i husene. Må kjøre set_gislist
		if(get_parent().get_sendobject().get_sendproperties().typecast_gis().get_houses() != null)
			m_lbl_pointcount = new StdTextLabel(PAS.l("main_sending_adr_housecount") + " - " + get_parent().get_sendobject().get_sendproperties().typecast_gis().get_houses().get_houses().size());
		else
			m_lbl_pointcount = new StdTextLabel(PAS.l("main_sending_adr_housecount") + " - " + PAS.get_pas().get_statuscontroller().get_houses().get_houses().size());
		m_lbl_pointcount.setPreferredSize(new Dimension(200, 16));
		add_controls();
	}
	public final void add_controls() {
		int n_width = 10;
		_add(m_lbl_pointcount, 0, inc_panels(), n_width, 1);
		super.add_controls();
	}
	protected final void exec_adrcount() {
		SendPropertiesGIS gis = get_parent().get_sendobject().get_sendproperties().typecast_gis();
		/*int newadrtypes = 0;
		newadrtypes = GetAdrTypesForCount(get_parent().get_sendobject().get_sendproperties().get_addresstypes());

		AddressCount adrcount = new AddressCount(gis.get_adrcount(SendController.SENDTO_FIXED_PRIVATE), 
				 gis.get_adrcount(SendController.SENDTO_FIXED_COMPANY), 
				 gis.get_adrcount(SendController.SENDTO_MOBILE_PRIVATE),
				 gis.get_adrcount(SendController.SENDTO_MOBILE_COMPANY),
				 gis.get_adrcount(SendController.SENDTO_NOPHONE_PRIVATE),
				 gis.get_adrcount(SendController.SENDTO_NOPHONE_COMPANY));*/
		AddressCount adrcount = new AddressCount();
		GISList list = gis.get_gislist();
		for(int i=0; i < list.size(); i++)
		{
			GISRecord gr = list.get_gisrecord(i);
			for(int j=0; j < gr.get_inhabitantcount(); j++)
			{
				InhabitantBasics inh = gr.get_inhabitant(j);
				_AddToAdrcount(adrcount, inh, get_parent().get_sendobject().get_sendproperties().get_addresstypes());
			}
		}
		
		
		actionPerformed(new ActionEvent(adrcount, ActionEvent.ACTION_PERFORMED, ADRCOUNT_CALLBACK_ACTION_));
		
	}
	
	/*
	 * used for calculating total amount of voice and sms recipients (after GIS import)
	 * function should be equal to PASWS.UAdrDb.cs._AddToAdrcount() (polygon/ellipse)
	 */
	protected void _AddToAdrcount(AddressCount c, InhabitantBasics i, int adrtypes)
	{
        if ((adrtypes & (long)SendController.SENDTO_FIXED_PRIVATE)>0 && i.hasfixed() && !i.bedrift())
        	c.inc_private(1);
        if ((adrtypes & (long)SendController.SENDTO_FIXED_COMPANY) > 0 && i.hasfixed() && i.bedrift())
            c.inc_company(1);
        if ((adrtypes & (long)SendController.SENDTO_MOBILE_PRIVATE) > 0 && i.hasmobile() && !i.bedrift())
        	c.inc_privatemobile(1);
        if ((adrtypes & (long)SendController.SENDTO_MOBILE_COMPANY) > 0 && i.hasmobile() && i.bedrift())
        	c.inc_companymobile(1);
        if ((adrtypes & (long)SendController.SENDTO_SMS_PRIVATE) > 0 && i.hasmobile() && !i.bedrift())
        	c.inc_privatesms(1);
        if ((adrtypes & (long)SendController.SENDTO_SMS_COMPANY) > 0 && i.hasmobile() && i.bedrift())
        	c.inc_companysms(1);
        if ((adrtypes & (long)SendController.SENDTO_SMS_PRIVATE_ALT_FIXED) > 0 && !i.bedrift())
        {
            if (i.hasmobile())
            	c.inc_privatesms(1);
            else if (i.hasfixed())
            	c.inc_private(1);
        }
        if ((adrtypes & (long)SendController.SENDTO_SMS_COMPANY_ALT_FIXED) > 0 && i.bedrift())
        {
            if (i.hasmobile())
            	c.inc_companysms(1);
            else if (i.hasfixed())
            	c.inc_company(1);
        }
        if ((adrtypes & (long)SendController.SENDTO_FIXED_PRIVATE_ALT_SMS) > 0 && !i.bedrift())
        {
            if (i.hasfixed())
            	c.inc_private(1);
            else if(i.hasmobile())
            	c.inc_privatesms(1);
        }
        if ((adrtypes & (long)SendController.SENDTO_FIXED_COMPANY_ALT_SMS) > 0 && i.bedrift())
        {
            if(i.hasfixed())
            	c.inc_company(1);
            else if(i.hasmobile())
            	c.inc_companysms(1);
        }
        if ((adrtypes & (long)SendController.SENDTO_FIXED_PRIVATE_AND_MOBILE) > 0 && !i.bedrift())
        {
            if (i.hasfixed())
            	c.inc_private(1);
            if (i.hasmobile())
            	c.inc_privatemobile(1);
        }
        if ((adrtypes & (long)SendController.SENDTO_FIXED_COMPANY_AND_MOBILE) > 0 && i.bedrift())
        {
            if (i.hasfixed())
            	c.inc_company(1);
            if (i.hasmobile())
            	c.inc_companymobile(1);
        }
        if (!i.hasfixed() && !i.hasmobile())
        {
            if (!i.bedrift())
            	c.inc_privatenonumber(1);
            else if (i.bedrift())
            	c.inc_companynonumber(1);
        }	
	}	
}