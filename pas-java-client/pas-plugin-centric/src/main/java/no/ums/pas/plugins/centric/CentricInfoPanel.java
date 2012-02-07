package no.ums.pas.plugins.centric;

import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.pas.core.mainui.InfoPanel;
import no.ums.pas.core.mainui.address_search.AddressSearchPanel;
import no.ums.pas.localization.Localization;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;

public class CentricInfoPanel extends InfoPanel
{
    private static final Log log = UmsLog.getLogger(CentricInfoPanel.class);
    
	protected AddressSearchPanel address_search = new AddressSearchPanel();
	
	protected class CentricCoorInfoUI extends CoorInfoUI
	{
		protected CentricCoorInfoUI()
		{
			super();
			address_search.init();
		}

		@Override
		public void add_controls() {
			int spacing = 10;
			set_gridconst(0, inc_panels(), 1, 1);
			add(m_lbl_coor, m_gridconst);
			set_gridconst(1, get_panel(), 1, 1);
			add(m_txt_coor_x, m_gridconst);
			set_gridconst(2, get_panel(), 1, 1);
			add(m_txt_coor_y, m_gridconst);
			add_spacing(DIR_VERTICAL, spacing);

			set_gridconst(0, inc_panels(), 1, 1);
			add(m_lbl_coor_dec, m_gridconst);
			set_gridconst(1, get_panel(), 1, 1);
			add(m_txt_coor_dec_x, m_gridconst);
			set_gridconst(2, get_panel(), 1, 1);
			add(m_txt_coor_dec_y, m_gridconst);
			add_spacing(DIR_VERTICAL, spacing);

			set_gridconst(0, inc_panels(), 1, 1);
			add(m_lbl_utm, m_gridconst);
			set_gridconst(1, get_panel(), 1, 1);
			add(m_txt_utm_x, m_gridconst);
			set_gridconst(2, get_panel(), 1, 1);
			add(m_txt_utm_y, m_gridconst);
			set_gridconst(3, get_panel(), 1, 1);
			add(m_txt_utm_zone, m_gridconst);
			add_spacing(DIR_VERTICAL, spacing);
		}
		
	}
	
	protected class CentricUserInfoUI extends InfoPanel.UserInfoUI
	{
		public CentricUserInfoUI()
		{
			super();
		}

		@Override
		public void add_controls() {
			int spacing = 10;
			set_gridconst(0, inc_panels(), 1, 1);
			add(m_lbl_name, get_gridconst());
			set_gridconst(1, get_panel(), 2, 1);
			add(m_lbl_name_output, get_gridconst());

			add_spacing(DIR_VERTICAL, spacing);

			set_gridconst(0, inc_panels(), 1, 1);
			add(m_lbl_userid, get_gridconst());
			set_gridconst(1, get_panel(), 2, 1);
			add(m_lbl_userid_output, get_gridconst());

			add_spacing(DIR_VERTICAL, spacing);

			set_gridconst(0, inc_panels(), 1, 1);
			add(m_lbl_deptid, get_gridconst());
			set_gridconst(1, get_panel(), 2, 1);
			add(m_lbl_deptid_output, get_gridconst());

			add_spacing(DIR_VERTICAL, spacing);

			set_gridconst(0, inc_panels(), 1, 1);
			add(m_lbl_compid, get_gridconst());
			set_gridconst(1, get_panel(), 2, 1);
			add(m_lbl_compid_output, get_gridconst());

			add_spacing(DIR_VERTICAL, spacing);

			set_gridconst(0, inc_panels(), 1, 1);
			add(m_lbl_userprofile, get_gridconst());
			set_gridconst(1, get_panel(), 2, 1);
			add(m_lbl_userprofile_output, get_gridconst());
		}
		
	}
	
	public class CentricInfoContainer extends InfoPanel.InfoContainer
	{
		public CentricInfoContainer()
		{
			super();
		}
		@Override
		public void add_controls() {
			coorinfo = new CoorInfoBox(new CentricCoorInfoUI())
			{
				@Override
				public int getWantedHeight()
				{
					return 120;
				}
			};
			coorinfo.init();
			userinfo = new UserInfoBox(new CentricUserInfoUI())
			{
				@Override
				public int getWantedHeight()
				{
					return 160;
				}
			};
			userinfo.init();
			
			m_coorsearch = new CoorSearchUI();

            address_search.setBorder(BorderFactory.createTitledBorder(Localization.l("adrsearch_dlg_title")));
            coorinfo.setBorder(BorderFactory.createTitledBorder(Localization.l("main_infotab_map_information")));
            userinfo.setBorder(BorderFactory.createTitledBorder(Localization.l("main_infotab_user_information")));
            m_coorsearch.setBorder(BorderFactory.createTitledBorder(Localization.l("main_infotab_coordinate_search")));

			
			set_gridconst(0,inc_panels(),1,1);
			add(address_search, m_gridconst);
			add_spacing(DIR_VERTICAL, 20);

			set_gridconst(0, inc_panels(), 1, 1);
			add(m_coorsearch, m_gridconst);
			add_spacing(DIR_VERTICAL, 20);

			set_gridconst(0,inc_panels(),1,1);
			add(coorinfo, m_gridconst);
			add_spacing(DIR_VERTICAL, 20);

			set_gridconst(0,inc_panels(),1,1);
			add(userinfo, m_gridconst);

		}
		
	}
	public CentricInfoPanel()
	{
		super();
		infocont = new CentricInfoContainer();
	}
	@Override
	public void componentResized(ComponentEvent e) {
		super.componentResized(e);

		final int w = getWidth();
		final int h = getHeight();
		if(w<=1 || h<=1)
			return;
		try
		{
			SwingUtilities.invokeLater(new Runnable() {
				public void run()
				{
					address_search.setPreferredSize(new Dimension(w, 250));
					address_search.get_searchpanelresults().getLayeredPane().setPreferredSize(new Dimension(w, 150));
					address_search.get_searchpanelresults().validate();
					revalidate();
				}
			});
		}
		catch(Exception err)
		{
			log.error(err.getMessage(), err);
		}

	}
	
}