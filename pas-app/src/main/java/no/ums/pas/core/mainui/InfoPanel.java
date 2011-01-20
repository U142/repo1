package no.ums.pas.core.mainui;

import no.ums.pas.PAS;
import no.ums.pas.core.Variables;
import no.ums.pas.core.controllers.HouseController;
import no.ums.pas.core.defines.DefaultPanel;
import no.ums.pas.core.logon.DeptInfo;
import no.ums.pas.core.logon.UserInfo;
import no.ums.pas.core.ws.WSWeatherReport;
import no.ums.pas.maps.defines.CommonFunc;
import no.ums.pas.maps.defines.MapPoint;
import no.ums.pas.maps.defines.MapPointLL;
import no.ums.pas.maps.defines.Navigation;
import no.ums.pas.ums.errorhandling.Error;
import no.ums.pas.ums.tools.*;
import no.ums.pas.ums.tools.calendarutils.DateTimePicker;
import no.ums.ws.pas.UWeatherSearch;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.util.regex.Pattern;

//import Core.WebData.XMLWeatherData;

public class InfoPanel extends GeneralPanel {
	public static final long serialVersionUID = 1;
	BoxLayout m_boxlayout;

	@Override
	public void componentResized(ComponentEvent e) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				int w = getWidth();
				int h = getHeight();
				if (w <= 0 || h <= 0)
					return;
				sp.setPreferredSize(new Dimension(w, h));
				if (m_weatherinfo != null) {
					m_weatherinfo.setPreferredSize(new Dimension(w,
							m_weatherinfo.getWantedHeight()));
					m_weatherinfo.validate();
				}
				if (m_coorsearch != null) {
					m_coorsearch.setPreferredSize(new Dimension(w, m_coorsearch
							.getWantedHeight()));
				}
				if (userinfo != null) {
					userinfo.setPreferredSize(new Dimension(w, userinfo
							.getWantedHeight()));
					userinfo.validate();
				}
				if (coorinfo != null) {
					coorinfo.setPreferredSize(new Dimension(w, coorinfo
							.getWantedHeight()));
					coorinfo.validate();
				}
				sp.validate();
			}
		});
		super.componentResized(e);
	}

	public static Font f1 = new Font("Arial", Font.BOLD, 12);
	public static Font f2 = new Font("Arial", Font.PLAIN, 12);

	public StdTextLabel m_lbl_xy;
	public StdTextLabel m_lbl_coor;
	public StdTextLabel m_lbl_coor_dec; // decimals
	public StdTextLabel m_lbl_utm;
	public StdTextLabel m_lbl_mapdimpix;
	public StdTextLabel m_lbl_mapdimmeters;
	public StdTextLabel m_lbl_housesdownload;
	public StdTextLabel m_txt_housesdownload;

	public StdTextLabel m_txt_xy_x;
	public StdTextLabel m_txt_xy_y;
	public StdTextLabel m_txt_coor_x;
	public StdTextLabel m_txt_coor_y;
	public StdTextLabel m_txt_coor_dec_x;
	public StdTextLabel m_txt_coor_dec_y;
	public StdTextLabel m_txt_utm_x;
	public StdTextLabel m_txt_utm_y;
	public StdTextLabel m_txt_utm_zone;
	public StdTextLabel m_txt_mapdimpix_x;
	public StdTextLabel m_txt_mapdimpix_y;
	public StdTextLabel m_txt_mapdimmeters_x;
	public StdTextLabel m_txt_mapdimmeters_y;
	public StdTextLabel m_txt_housedownload_progress;

	// account information
	public StdTextLabel m_lbl_name;
	public StdTextLabel m_lbl_name_output;
	public StdTextLabel m_lbl_userid;
	public StdTextLabel m_lbl_userid_output;
	public StdTextLabel m_lbl_deptid;
	public StdTextLabel m_lbl_deptid_output;
	public StdTextLabel m_lbl_compid;
	public StdTextLabel m_lbl_compid_output;
	public StdTextLabel m_lbl_userprofile;
	public StdTextLabel m_lbl_userprofile_output;
	public StdTextLabel m_lbl_adrdatabase;
	public StdTextLabel m_lbl_adrdatabase_output;
	public StdTextLabel m_lbl_adrdatabase_municipals;

	public WeatherInfoUI m_weatherinfo;
	public CoorSearchUI m_coorsearch;
	public UserInfoBox userinfo;
	public CoorInfoBox coorinfo;

	JScrollPane sp = null;
	protected InfoContainer infocont = new InfoContainer();

	public class InfoContainer extends DefaultPanel {

		public InfoContainer() {
			super();
		}

		/*
		 * @Override protected void do_layout() {
		 * 
		 * }
		 * 
		 * @Override protected void init_controls() {
		 * 
		 * }
		 * 
		 * @Override protected void set_layout() {
		 * 
		 * }
		 */

		@Override
		public void actionPerformed(ActionEvent e) {

		}

		@Override
		public void add_controls() {
			userinfo = new UserInfoBox();
			coorinfo = new CoorInfoBox();
			userinfo.init();
			coorinfo.init();

			coorinfo.setBorder(BorderFactory.createTitledBorder(PAS
					.l("main_infotab_map_information")));
			set_gridconst(0, inc_panels(), 1, 1);
			add(coorinfo, m_gridconst);
			// add(Box.createRigidArea(new Dimension(5,20)));
			add_spacing(DIR_VERTICAL, 20);
			// coorinfo.doLayout();

			userinfo.setBorder(BorderFactory.createTitledBorder(PAS
					.l("main_infotab_user_information")));
			// userinfo.doLayout();
			set_gridconst(0, inc_panels(), 1, 1);
			add(userinfo, m_gridconst);
			// add(Box.createRigidArea(new Dimension(5,20)));
			add_spacing(DIR_VERTICAL, 20);

			m_coorsearch = new CoorSearchUI();
			m_coorsearch.setBorder(BorderFactory.createTitledBorder(PAS
					.l("main_infotab_coordinate_search")));
			set_gridconst(0, inc_panels(), 1, 1);
			add(m_coorsearch, m_gridconst);

			/*
			 * if(PAS.get_pas().get_rightsmanagement().weather()) {
			 * //add(Box.createRigidArea(new Dimension(5,20)));
			 * add_spacing(DIR_VERTICAL, 20); m_weatherinfo = new
			 * WeatherInfoUI();
			 * m_weatherinfo.setBorder(BorderFactory.createTitledBorder
			 * (PAS.l("main_infotab_weather_information"))); set_gridconst(0,
			 * inc_panels(), 1, 1); add(m_weatherinfo, m_gridconst);
			 * //getWeatherData(); }
			 */

		}

		@Override
		public void init() {

		}
	}

	public InfoPanel() {
		this(new Dimension(1, 1));
	}

	public InfoPanel(Dimension dim) {
		super(dim);
		this.setBorder(null);
		// sp = new
		// JScrollPane(infocont,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
		// JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		// sp.setBorder(null);
		// JScrollPane sp = new JScrollPane(m_infopanel, JScrollPane.);
		// sp.setPreferredSize(new Dimension(PAS.get_pas().get_eastwidth(),
		// 300));
		// doInit();
		addComponentListener(this);

	}

	@Override
	public void add_controls() {
		sp = new JScrollPane(infocont,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		sp.setBorder(null);
		super.add_controls();

	}

	public void actionPerformed(ActionEvent e) {
		if ("act_maploaded".equals(e.getActionCommand())) {
			try {
				Navigation nav = (Navigation) e.getSource();
				set_mapdimpix(nav.getDimension().width,
						nav.getDimension().height);
				set_mapdimmeters(nav.get_mapwidthmeters().intValue(), nav
						.get_mapheightmeters().intValue());
			} catch (Exception err) {

			}
			if (m_weatherinfo != null)
				m_weatherinfo.updateWeatherData();
			// Update weather data
		} else if ("act_download_houses_report".equals(e.getActionCommand())) {
			Integer i = (Integer) e.getSource();
			set_housesdownload_status(i.intValue());
		} else if ("act_update_accountinfo".equals(e.getActionCommand())) {
			UserInfo info = (UserInfo) e.getSource();
			update_accountinfo(info);
		}
		super.actionPerformed(e);
	}

	public void update_accountinfo(UserInfo info) {
		DeptInfo dept = info.get_current_department();
		m_lbl_name_output.setText(info.get_realname());
		m_lbl_userid_output.setText(info.get_userid());
		m_lbl_deptid_output.setText(dept.get_deptid());
		m_lbl_compid_output.setText(info.get_compid());
		m_lbl_userprofile_output.setText(dept.get_userprofile().get_name());
		String db = dept.get_stdcc();
		String municipals = "";
		System.out.println("PAS Rights (stdcc=" + dept.get_stdcc() + ") = " + info.get_current_department().get_pas_rights());
		try {
			db = "<html>";
			db += ""
					+ no.ums.pas.cellbroadcast.CountryCodes.getCountryByCCode(
							dept.get_stdcc()).getCountry();
			switch (info.get_current_department().get_pas_rights()) {
			case 1:
				db += "";
				// normal address db rights
				m_lbl_adrdatabase_output.setToolTipText("");
				if (m_weatherinfo != null)
					m_weatherinfo.setVisible(true); // Turn back on if it was
													// disabled by TAS
				break;
			case 2:
				// folkereg address
				db += " [" + PAS.l("common_national_register") + "] - ";
				db += "";
				municipals = "<html>";
				for (int i = 0; i < info.get_current_department()
						.get_municipals().size(); i++) {
					if (i > 0)
						municipals += "<br>";
					municipals += info.get_current_department()
							.get_municipals().get(i).getSzMunicipalname();
					// db += "<br>" +
					// info.get_current_department().get_municipals().get(i).getSzMunicipalname();
				}
				municipals += "</html>";
				m_lbl_adrdatabase_output.setToolTipText(municipals);
				if (m_weatherinfo != null)
					m_weatherinfo.setVisible(true); // Turn back on if it was
													// disabled by TAS
				break;
			case 4:
				// TAS
				db += " [" + PAS.l("main_tas_title") + "]";
				if (m_weatherinfo != null)
					m_weatherinfo.setVisible(false); // Hide weather info for
														// TAS
				break;
			default:
				db += PAS.l("main_infotab_no_db_rights");
				// db+="</b>";
				m_lbl_adrdatabase_output.setToolTipText("");
				if (m_weatherinfo != null)
					m_weatherinfo.setVisible(true); // Turn back on if it was
													// disabled by TAS
				break;
			}
			db += "</html>";
		} catch (Exception e) {
			
			System.out.println("PAS rights failed");
			e.printStackTrace();
		}
		// m_lbl_adrdatabase_output.setText(db);
		m_lbl_adrdatabase_output.setText(db);
		m_lbl_adrdatabase_municipals.setText(municipals);
		System.out.println(db);
		System.out.println(municipals);
	}

	protected void init_controls() {
		m_lbl_xy = new StdTextLabel(PAS.l("main_infotab_xy_coors"), true, 150);
		m_lbl_coor = new StdTextLabel(PAS.l("common_lon") + " "
				+ PAS.l("common_lat"), true, 150);
		m_lbl_coor_dec = new StdTextLabel("", true, 150);
		m_lbl_utm = new StdTextLabel(PAS.l("common_utm"), true, 150);
		m_lbl_mapdimpix = new StdTextLabel(PAS.l("main_infotab_mapdimensions"),
				true, 150);
		m_lbl_mapdimmeters = new StdTextLabel(PAS
				.l("main_infotab_mapdimensions"), true, 150);
		m_lbl_housesdownload = new StdTextLabel(PAS
				.l("main_infotab_housedownload"), true, 150);

		m_txt_xy_x = new StdTextLabel("", true, 125);
		m_txt_xy_y = new StdTextLabel("", true, 125);
		m_txt_coor_x = new StdTextLabel("", true, 125);
		m_txt_coor_y = new StdTextLabel("", true, 125);
		m_txt_coor_dec_x = new StdTextLabel("", true, 125);
		m_txt_coor_dec_y = new StdTextLabel("", true, 125);
		m_txt_utm_x = new StdTextLabel("", true, 125);
		m_txt_utm_y = new StdTextLabel("", true, 125);
		m_txt_utm_zone = new StdTextLabel("", true, 30);
		m_txt_mapdimpix_x = new StdTextLabel("", true, 125);
		m_txt_mapdimpix_y = new StdTextLabel("", true, 125);
		m_txt_mapdimmeters_x = new StdTextLabel("", true, 125);
		m_txt_mapdimmeters_y = new StdTextLabel("", true, 125);
		m_txt_housesdownload = new StdTextLabel("", true, 150);
		m_txt_housedownload_progress = new StdTextLabel("", true, 155);

		m_lbl_name = new StdTextLabel(PAS.l("common_name"), true, 150);
		m_lbl_name_output = new StdTextLabel("", true, 280);
		m_lbl_userid = new StdTextLabel(PAS.l("logon_userid"), true, 150);
		m_lbl_userid_output = new StdTextLabel("", true, 280);
		m_lbl_deptid = new StdTextLabel(PAS.l("main_infotab_departmentid"),
				true, 150);
		m_lbl_deptid_output = new StdTextLabel("", true, 280);
		m_lbl_compid = new StdTextLabel(PAS.l("logon_company"), true, 150);
		m_lbl_compid_output = new StdTextLabel("", true, 280);
		m_lbl_userprofile = new StdTextLabel(PAS.l("main_infotab_userprofile"),
				true, 150);
		m_lbl_userprofile_output = new StdTextLabel("", true, 280);
		m_lbl_adrdatabase = new StdTextLabel(PAS
				.l("main_infotab_addressdatabase"), true, 150, 170);
		m_lbl_adrdatabase_output = new StdTextLabel("", true, 140, 170);
		m_lbl_adrdatabase_municipals = new StdTextLabel("", true, 140, 170);

		// FontSet m_lbl_xy.setFont(f1);
		// FontSet m_lbl_coor.setFont(f1);
		// FontSet m_lbl_coor_dec.setFont(f1);
		// FontSet m_lbl_utm.setFont(f1);
		// FontSet m_lbl_mapdimpix.setFont(f1);
		// FontSet m_lbl_mapdimmeters.setFont(f1);
		// FontSet m_lbl_housesdownload.setFont(f1);

		/*
		 * m_lbl_adrdatabase_output.set_height(300);
		 * m_lbl_adrdatabase_output.set_width(100);
		 * m_lbl_adrdatabase_output.init(false);
		 * m_lbl_adrdatabase_output.revalidate();
		 */
		// m_lbl_adrdatabase_output.setPreferredSize(new Dimension(150, 200));
		// m_lbl_adrdatabase_output.setSize(new Dimension(150, 200));
	}

	abstract class BoxPanel extends JPanel {
		private BoxLayout m_box;

		BoxPanel() {
			super();
			m_box = new BoxLayout(this, BoxLayout.X_AXIS);
			setLayout(m_box);
			setAlignmentX(LEFT_ALIGNMENT);
			// init();
		}

		abstract void init();
	}

	/*
	 * class HousesDownload extends BoxPanel { public static final long
	 * serialVersionUID = 1; HousesDownload() { super(); } void init() {
	 * add(m_lbl_housesdownload); add(Box.createRigidArea(new Dimension(25,0)));
	 * add(m_txt_housesdownload); } } class MapDimMeters extends BoxPanel {
	 * public static final long serialVersionUID = 1; MapDimMeters() { super();
	 * } void init() { add(m_lbl_mapdimmeters); add(Box.createRigidArea(new
	 * Dimension(25,0))); add(m_txt_mapdimmeters); } } class MapDimPix extends
	 * BoxPanel { public static final long serialVersionUID = 1; MapDimPix() {
	 * super(); } void init() { add(m_lbl_mapdimpix);
	 * add(Box.createRigidArea(new Dimension(25,0))); add(m_txt_mapdimpix); } }
	 * 
	 * class RowXY extends BoxPanel { public static final long serialVersionUID
	 * = 1; RowXY() { super(); } void init() { try { add(m_lbl_xy);
	 * add(Box.createRigidArea(new Dimension(25,0))); add(m_txt_xy); }
	 * catch(Exception e) { System.out.println(e.getMessage());
	 * e.printStackTrace();
	 * Error.getError().addError("InfoPanel","Exception in RowXY",e,1); } } }
	 * class RowLL extends BoxPanel { public static final long serialVersionUID
	 * = 1; RowLL() { super(); } void init() { try { add(m_lbl_coor);
	 * add(Box.createRigidArea(new Dimension(25,0))); add(m_txt_coor); }
	 * catch(Exception e) { System.out.println(e.getMessage());
	 * e.printStackTrace();
	 * Error.getError().addError("InfoPanel","Exception in RowLL",e,1); }
	 * 
	 * } } class RowLLDec extends BoxPanel { public static final long
	 * serialVersionUID = 1; RowLLDec() { super(); } void init() { try {
	 * add(m_lbl_coor_dec); add(Box.createRigidArea(new Dimension(25,0)));
	 * add(m_txt_coor_dec); } catch(Exception e) {
	 * System.out.println(e.getMessage()); e.printStackTrace();
	 * Error.getError().addError("InfoPanel","Exception in RowLLDec",e,1); } } }
	 * class RowUTM extends BoxPanel { public static final long serialVersionUID
	 * = 1; RowUTM() { super(); } void init() { try { add(m_lbl_utm);
	 * add(Box.createRigidArea(new Dimension(25,0))); add(m_txt_utm); }
	 * catch(Exception e) { System.out.println(e.getMessage());
	 * e.printStackTrace();
	 * Error.getError().addError("InfoPanel","Exception in RowUTM",e,1); } } }
	 */
	public class UserInfoBox extends DefaultPanel {
		public static final long serialVersionUID = 1;
		protected UserInfoUI ui = null;

		public UserInfoBox() {
			super();
		}

		public UserInfoBox(UserInfoUI ui) {
			super();
			this.ui = ui;
		}

		public void init() {
			try {
				if (ui == null)
					ui = new UserInfoUI();
				add(ui);

			} catch (Exception e) {
				Error.getError().addError("InfoPanel",
						"Exception in UserInfoUI", e, 1);
			}
		}

		@Override
		public void actionPerformed(ActionEvent e) {

		}

		@Override
		public void add_controls() {

		}

		@Override
		public int getWantedHeight() {
			return 380;
		}

		@Override
		public int getWantedWidth() {
			return super.getWantedWidth();
		}

	}

	public class CoorInfoBox extends DefaultPanel {
		public static final long serialVersionUID = 1;
		protected CoorInfoUI ui = null;

		public CoorInfoBox() {
			super();
		}

		public CoorInfoBox(CoorInfoUI ui) {
			this.ui = ui;
		}

		public void init() {
			try {
				if (ui == null)
					ui = new CoorInfoUI();
				add(ui);
			} catch (Exception e) {
				Error.getError().addError("InfoPanel",
						"Exception in CoorInfoUI", e, 1);
			}
		}

		@Override
		public void actionPerformed(ActionEvent e) {

		}

		@Override
		public void add_controls() {

		}

		@Override
		public int getWantedHeight() {
			return 220;
		}

		@Override
		public int getWantedWidth() {
			return super.getWantedWidth();
		}
	}

	public class CoorInfoUI extends DefaultPanel {
		public static final long serialVersionUID = 1;

		public CoorInfoUI() {
			super();
			init();
		}

		public void actionPerformed(ActionEvent e) {
		}

		public void add_controls() {
			int spacing = 10;
			set_gridconst(0, inc_panels(), 1, 1);
			add(m_lbl_xy, m_gridconst);
			set_gridconst(1, get_panel(), 1, 1);
			add(m_txt_xy_x, m_gridconst);
			set_gridconst(2, get_panel(), 1, 1);
			add(m_txt_xy_y, m_gridconst);
			add_spacing(DIR_VERTICAL, spacing);

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

			set_gridconst(0, inc_panels(), 1, 1);
			add(m_lbl_mapdimpix, m_gridconst);
			set_gridconst(1, get_panel(), 1, 1);
			add(m_txt_mapdimpix_x, m_gridconst);
			set_gridconst(2, get_panel(), 1, 1);
			add(m_txt_mapdimpix_y, m_gridconst);
			add_spacing(DIR_VERTICAL, spacing);

			set_gridconst(0, inc_panels(), 1, 1);
			add(m_lbl_mapdimmeters, m_gridconst);
			set_gridconst(1, get_panel(), 1, 1);
			add(m_txt_mapdimmeters_x, m_gridconst);
			set_gridconst(2, get_panel(), 1, 1);
			add(m_txt_mapdimmeters_y, m_gridconst);
			add_spacing(DIR_VERTICAL, spacing);

			set_gridconst(0, inc_panels(), 1, 1);
			add(m_lbl_housesdownload, m_gridconst);
			set_gridconst(1, get_panel(), 1, 1);
			add(m_txt_housesdownload, m_gridconst);

			/*
			 * add(rowll); add(rowlldec); add(rowutm); add(dimpix);
			 * add(dimmeters); add(housesdownload);
			 */

		}

		public void init() {
			add_controls();
			setVisible(true);
		}
	}

	public class UserInfoUI extends DefaultPanel {
		public static final long serialVersionUID = 1;

		public UserInfoUI() {
			super();
			init();
		}

		public void actionPerformed(ActionEvent e) {
		}

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

			add_spacing(DIR_VERTICAL, spacing);

			set_gridconst(0, inc_panels(), 1, 1);
			add(m_lbl_adrdatabase, get_gridconst());
			set_gridconst(1, get_panel(), 1, 1);
			add(m_lbl_adrdatabase_output, get_gridconst());
			set_gridconst(2, get_panel(), 1, 1);
			add(m_lbl_adrdatabase_municipals, get_gridconst());

		}

		public void init() {
			// FontSet m_lbl_name.setFont(f1);
			// FontSet m_lbl_name_output.setFont(f2);
			// FontSet m_lbl_userid.setFont(f1);
			// FontSet m_lbl_userid_output.setFont(f2);
			// FontSet m_lbl_deptid.setFont(f1);
			// FontSet m_lbl_deptid_output.setFont(f2);
			// FontSet m_lbl_compid.setFont(f1);
			// FontSet m_lbl_compid_output.setFont(f2);
			// FontSet m_lbl_userprofile.setFont(f1);
			// FontSet m_lbl_userprofile_output.setFont(f2);
			// FontSet m_lbl_adrdatabase.setFont(f1);
			// FontSet m_lbl_adrdatabase_output.setFont(f2);
			// FontSet m_lbl_adrdatabase_municipals.setFont(f2);

			add_controls();
			setVisible(true);
		}
	}

	public void set_mapdimpix(int x, int y) {
		// m_txt_mapdimpix.setText(x + ", " + y + " (pixels)");
		m_txt_mapdimpix_x.setText(Integer.toString(x));
		m_txt_mapdimpix_y.setText(Integer.toString(y) + " ("
				+ PAS.l("common_pixels") + ")");
	}

	public void set_mapdimmeters(int width, int height) {
		// m_txt_mapdimmeters.setText(width + ", " + height + " (meters)");
		m_txt_mapdimmeters_x.setText(width + "");
		m_txt_mapdimmeters_y.setText(height + " (" + PAS.l("common_meters")
				+ ")");
	}

	public void set_housesdownload_status(int STATUS) {
		String sz_text;
		switch (STATUS) {
		case HouseController.HOUSE_DOWNLOAD_DISABLED_:
			sz_text = PAS.l("common_disabled");
			break;
		case HouseController.HOUSE_DOWNLOAD_FINISHED_:
			sz_text = PAS.l("common_finished");
			break;
		case HouseController.HOUSE_DOWNLOAD_IN_PROGRESS_:
			sz_text = PAS.l("common_inprogress");
			break;
		case HouseController.HOUSE_DOWNLOAD_NO_:
			sz_text = PAS.l("main_infotab_mapwidth_exceeded");
			break;
		default:
			sz_text = PAS.l("common_unknown_status");
			break;
		}

		try {
			m_txt_housesdownload.setText(sz_text);
			m_txt_housedownload_progress.setText(PAS
					.l("main_infotab_housedownload")
					+ " - " + sz_text);
		} catch (Exception e) {

		}
	}

	protected void update_ui(MapPoint p) {
		// Navigation nav = get_pas().get_navigation();
		try {
			// m_txt_xy.setText(p.get_x() + " , " + p.get_y());
			m_txt_xy_x.setText(Integer.toString(p.get_x()));
			m_txt_xy_y.setText(Integer.toString(p.get_y()));
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			Error.getError().addError("InfoPanel", "Exception in update_ui", e,
					1);
		}
		try {
			MapPointLL ll = p.get_mappointll();// nav.screen_to_coor(p.x, p.y);
			/*
			 * 
			 * // Convert to Degree Minutes Seconds Representation LatDeg =
			 * Math.floor(lat); LatMin = Math.floor((lat-LatDeg)60); LatSec =
			 * (Math.round((((lat - LatDeg) - (LatMin/60)) 60 60) 100) / 100 ) ;
			 * LonDeg = Math.floor(lon); LonMin = Math.floor((lon-LonDeg)60);
			 * LonSec = (Math.round((((lon - LonDeg) - (LonMin / 60 )) 60 60)
			 * 100 ) / 100);
			 */
			/*
			 * d_deg = new Double(Math.floor(ll.get_lat())); d_min = new
			 * Double(Math.floor(new Double((ll.get_lat() - d_deg.doubleValue())
			 * 60).doubleValue())); d_sec = new Double(((((ll.get_lat() -
			 * d_deg.doubleValue()) - (d_min.doubleValue()/60)) 60 60) 100) /
			 * 100); sz_lat = d_deg.intValue() + "˚ " + d_min.intValue() + "' "
			 * + d_sec.toString() + "''";
			 * 
			 * d_deg = new Double(Math.floor(ll.get_lon())); d_min = new
			 * Double(Math.floor(new Double((ll.get_lon() - d_deg.doubleValue())
			 * 60).doubleValue())); d_sec = new Double(((((ll.get_lon() -
			 * d_deg.doubleValue()) - (d_min.doubleValue()/60)) 60 60) 100) /
			 * 100); sz_lon = d_deg.intValue() + "˚ " + d_min.intValue() + "' "
			 * + d_sec.toString() + "''";
			 */
			if (ll != null) {
				String sz_lat = CommonFunc.convert_decimal_to_dms(ll.get_lat());
				String sz_lon = CommonFunc.convert_decimal_to_dms(ll.get_lon());
				String ns = "N";
				if (ll.get_lat() < 0)
					ns = "S";
				String ew = "E";
				if (ll.get_lon() < 0)
					ew = "W";
				sz_lat += " " + ns;
				sz_lon += " " + ew;
				// sz_lat = ns + " " + sz_lat;
				// sz_lon = ew + " " + sz_lon;

				// m_txt_coor.setText(sz_lat + "   ,   " + sz_lon);
				m_txt_coor_x.setText(sz_lon);
				m_txt_coor_y.setText(sz_lat);
				// m_txt_coor_dec.setText(UMS.Tools.TextFormat.round(ll.get_lat(),
				// 4) + "   ,   " + UMS.Tools.TextFormat.round(ll.get_lon(),
				// 4));
				m_txt_coor_dec_x.setText(no.ums.pas.ums.tools.TextFormat.round(
						ll.get_lon(), 5).toString()
						+ "˚");
				m_txt_coor_dec_y.setText(no.ums.pas.ums.tools.TextFormat.round(
						ll.get_lat(), 5).toString()
						+ "˚");
				String sz_zone = "33V";
				double n_north = 0;
				double n_east = 0;
				CoorConverter.UTMCoor c = new CoorConverter().LL2UTM(23, ll
						.get_lat(), ll.get_lon(), n_north, n_east, sz_zone, 0);
				// m_txt_utm.setText(Math.round(c.getEasting()) + "   " +
				// Math.round(c.getNorthing()) + " " + c.getZone());
				m_txt_utm_x.setText(no.ums.pas.ums.tools.TextFormat.round(
						c.getEasting(), 1).toString());
				m_txt_utm_y.setText(no.ums.pas.ums.tools.TextFormat.round(
						c.getNorthing(), 1).toString());
				m_txt_utm_zone.setText(c.getZone());
			}
			// m_txt_coor.setText((double)(Math.round(ll.get_lon() * 100000)) /
			// 100000 + " , " + (double)(Math.round(ll.get_lat() * 100000)) /
			// 100000);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			Error.getError().addError("InfoPanel", "Exception in update_ui", e,
					1);
		}
	}

	protected void set_layout() {
		// already gridbag from DefaultPanel
		// setLayout(m_gridlayout);
		m_boxlayout = new BoxLayout(this, BoxLayout.Y_AXIS);
		setLayout(m_boxlayout);
	}

	protected void do_layout() {

		infocont.add_controls();
		this.add(sp);

	}

	public class CoorSearchUI extends DefaultPanel implements ActionListener {
		public static final long serialVersionUID = 1;
		private CoorSearchLL m_search_ll;
		private CoorSearchUTM m_search_utm;
		private CoorSearchLLDec m_search_ll_dec;
		private JRadioButton m_radio_ll;
		private JRadioButton m_radio_ll_dec;
		private JRadioButton m_radio_utm;
		private ButtonGroup m_group_search = new ButtonGroup();

		protected String regexpDouble = "\\b(\\w+)\\s+\\1\\b";
		protected String regexpInt = "\\1-9";
		protected Pattern doublePattern = Pattern.compile(regexpDouble);
		protected Pattern intPattern = Pattern.compile(regexpInt);

		public CoorSearchUI() {
			this.setPreferredSize(new Dimension(500, 100));
			m_search_ll = new CoorSearchLL(this);
			m_search_utm = new CoorSearchUTM(this);
			m_search_ll_dec = new CoorSearchLLDec(this);
			m_search_ll.setPreferredSize(new Dimension(500, 80));
			m_search_utm.setPreferredSize(new Dimension(500, 80));
			m_search_ll_dec.setPreferredSize(new Dimension(500, 80));

			m_radio_ll = new JRadioButton(PAS.l("common_lon") + "/"
					+ PAS.l("common_lat"));
			m_radio_ll_dec = new JRadioButton(PAS.l("common_lon") + "/"
					+ PAS.l("common_lat") + "(" + PAS.l("common_decimal") + ")");
			m_radio_utm = new JRadioButton(PAS.l("common_utm"));
			// FontSet m_radio_ll.setFont(f1);
			// FontSet m_radio_ll_dec.setFont(f1);
			// FontSet m_radio_utm.setFont(f1);
			m_radio_ll.addActionListener(this);
			m_radio_utm.addActionListener(this);
			m_radio_ll_dec.addActionListener(this);
			m_radio_ll.setActionCommand("act_set_search_ll");
			m_radio_utm.setActionCommand("act_set_search_utm");
			m_radio_ll_dec.setActionCommand("act_set_search_ll_dec");
			m_group_search.add(m_radio_ll);
			m_group_search.add(m_radio_utm);
			m_group_search.add(m_radio_ll_dec);
			add_controls();

		}

		public void add_controls() {
			set_gridconst(0, 0, 1, 1);
			add(m_radio_ll, get_gridconst());
			set_gridconst(inc_xpanels(), 0, 1, 1);
			add(m_radio_ll_dec, get_gridconst());
			set_gridconst(inc_xpanels(), 0, 1, 1);
			add(m_radio_utm, get_gridconst());
			m_search_ll.setVisible(false);
			m_search_utm.setVisible(false);
			m_search_ll_dec.setVisible(false);

			set_gridconst(0, 1, 3, 1);
			add(m_search_ll, get_gridconst());
			set_gridconst(0, 1, 3, 1);
			add(m_search_ll_dec, get_gridconst());
			set_gridconst(0, 1, 3, 1);
			add(m_search_utm, get_gridconst());

			m_radio_ll.doClick();
			init();
		}

		public void init() {
			setVisible(true);
		}

		public void actionPerformed(ActionEvent e) {
			if ("act_set_search_ll".equals(e.getActionCommand())) {
				m_search_utm.setVisible(false);
				m_search_ll_dec.setVisible(false);
				m_search_ll.setVisible(true);
			} else if ("act_set_search_ll_dec".equals(e.getActionCommand())) {
				m_search_ll.setVisible(false);
				m_search_utm.setVisible(false);
				m_search_ll_dec.setVisible(true);
			} else if ("act_set_search_utm".equals(e.getActionCommand())) {
				m_search_ll.setVisible(false);
				m_search_ll_dec.setVisible(false);
				m_search_utm.setVisible(true);
			} else if ("act_goto_map".equals(e.getActionCommand())) {
				CoorConverter.LLCoor llcoor = (CoorConverter.LLCoor) e
						.getSource();
				MapPointLL center = new MapPointLL(llcoor.get_lon(), llcoor
						.get_lat());
				PAS.get_pas().actionPerformed(
						new ActionEvent(center, ActionEvent.ACTION_PERFORMED,
								"act_set_pinpoint"));
				Variables.getNavigation().exec_adrsearch(llcoor.get_lon(), llcoor
                        .get_lat(), 500.0f);
			}

		}

		@Override
		public int getWantedHeight() {
			return 150;
		}

		@Override
		public int getWantedWidth() {
			return super.getWantedWidth();
		}

		public class CoorSearchLLDec extends CoorSearchLL implements
				ActionListener {
			public static final long serialVersionUID = 1;

			// protected StdIntegerArea m_txt_lon_deg = new StdIntegerArea("",
			// false, 30, StdIntegerArea.DOUBLE); //override
			// protected StdIntegerArea m_txt_lat_deg = new StdIntegerArea("",
			// false, 30, StdIntegerArea.DOUBLE); //override

			public CoorSearchLLDec(ActionListener a) {
				super(a);
				m_txt_lon_deg.setPreferredSize(new Dimension(65, 15));
				m_txt_lat_deg.setPreferredSize(new Dimension(65, 15));
				// m_btn_go_ll.setPreferredSize(new Dimension(75, 16));
				m_txt_lon_deg.setType(StdIntegerArea.DOUBLE);
				m_txt_lat_deg.setType(StdIntegerArea.DOUBLE);
			}

			public void add_controls() {

				// LONGITUDE
				set_gridconst(inc_xpanels(), get_panel(), 1, 1);
				add(m_lbl_lon, get_gridconst());
				super.add_spacing(DIR_HORIZONTAL, 5);

				set_gridconst(inc_xpanels(), get_panel(), 1, 1);
				add(m_txt_lon_deg, get_gridconst());
				super.add_spacing(DIR_HORIZONTAL, 5);
				set_gridconst(inc_xpanels(), get_panel(), 1, 1);
				add(m_lbl_lon_deg, get_gridconst());

				super.add_spacing(DIR_HORIZONTAL, 15);

				// LATITUDE
				set_gridconst(inc_xpanels(), get_panel(), 1, 1);
				add(m_lbl_lat, get_gridconst());
				super.add_spacing(DIR_HORIZONTAL, 5);

				set_gridconst(inc_xpanels(), get_panel(), 1, 1);
				add(m_txt_lat_deg, get_gridconst());
				super.add_spacing(DIR_HORIZONTAL, 5);
				set_gridconst(inc_xpanels(), get_panel(), 1, 1);
				add(m_lbl_lat_deg, get_gridconst());

				super.add_spacing(DIR_HORIZONTAL, 15);

				// GOTO
				set_gridconst(inc_xpanels(), get_panel(), 1, 1);
				add(m_btn_go_ll, get_gridconst());

			}

			public void init() {

			}

			public void exec() {
				double lon;
				double lat;
				try {
					lon = Double.parseDouble(m_txt_lon_deg.getText());
					lat = Double.parseDouble(m_txt_lat_deg.getText());
				} catch (Exception err) {
					return;
				}
				CoorConverter.LLCoor coor = new CoorConverter().newLLCoor(lon,
						lat);
				m_callback.actionPerformed(new ActionEvent(coor,
						ActionEvent.ACTION_PERFORMED, "act_goto_map"));
			}

		}

		public class CoorSearchLL extends DefaultPanel implements
				ActionListener {
			public static final long serialVersionUID = 1;
			protected JLabel m_lbl_lon = new JLabel(PAS.l("common_longitude"));
			protected JLabel m_lbl_lat = new JLabel(PAS.l("common_latitude"));
			protected JLabel m_lbl_lon_deg = new JLabel(PAS
					.l("common_degree_sign"));
			protected JLabel m_lbl_lat_deg = new JLabel(PAS
					.l("common_degree_sign"));
			private JLabel m_lbl_lon_min = new JLabel(PAS
					.l("common_minute_sign"));
			private JLabel m_lbl_lat_min = new JLabel(PAS
					.l("common_minute_sign"));
			private JLabel m_lbl_lon_sec = new JLabel(PAS
					.l("common_second_sign"));
			private JLabel m_lbl_lat_sec = new JLabel(PAS
					.l("common_second_sign"));
			protected StdIntegerArea m_txt_lon_deg = new StdIntegerArea("",
					false, 35, StdIntegerArea.INTEGER);
			protected StdIntegerArea m_txt_lat_deg = new StdIntegerArea("",
					false, 35, StdIntegerArea.INTEGER);
			private StdIntegerArea m_txt_lon_min = new StdIntegerArea("",
					false, 35, StdIntegerArea.INTEGER);
			private StdIntegerArea m_txt_lat_min = new StdIntegerArea("",
					false, 35, StdIntegerArea.INTEGER);
			private StdIntegerArea m_txt_lon_sec = new StdIntegerArea("",
					false, 35, StdIntegerArea.DOUBLE);
			private StdIntegerArea m_txt_lat_sec = new StdIntegerArea("",
					false, 35, StdIntegerArea.DOUBLE);

			protected JButton m_btn_go_ll = new JButton(PAS
					.l("main_infotab_goto_map"));
			protected ActionListener m_callback;

			public CoorSearchLL(ActionListener a) {
				m_callback = a;
				// m_btn_go_ll.setPreferredSize(new Dimension(75, 16));
				m_btn_go_ll.addActionListener(this);
				add_controls();
			}

			public void add_controls() {

				// LONGITUDE
				set_gridconst(inc_xpanels(), get_panel(), 1, 1);
				add(m_lbl_lon, get_gridconst());
				super.add_spacing(DIR_HORIZONTAL, 5);

				set_gridconst(inc_xpanels(), get_panel(), 1, 1);
				add(m_txt_lon_deg, get_gridconst());
				super.add_spacing(DIR_HORIZONTAL, 5);
				set_gridconst(inc_xpanels(), get_panel(), 1, 1);
				add(m_lbl_lon_deg, get_gridconst());

				super.add_spacing(DIR_HORIZONTAL, 15);

				set_gridconst(inc_xpanels(), get_panel(), 1, 1);
				add(m_txt_lon_min, get_gridconst());
				super.add_spacing(DIR_HORIZONTAL, 5);
				set_gridconst(inc_xpanels(), get_panel(), 1, 1);
				add(m_lbl_lon_min, get_gridconst());

				super.add_spacing(DIR_HORIZONTAL, 15);

				set_gridconst(inc_xpanels(), get_panel(), 1, 1);
				add(m_txt_lon_sec, get_gridconst());
				super.add_spacing(DIR_HORIZONTAL, 5);
				set_gridconst(inc_xpanels(), get_panel(), 1, 1);
				add(m_lbl_lon_sec, get_gridconst());

				super.add_spacing(DIR_HORIZONTAL, 15);

				// LATITUDE
				reset_xpanels();
				inc_panels();
				set_gridconst(inc_xpanels(), get_panel(), 1, 1);
				add(m_lbl_lat, get_gridconst());
				super.add_spacing(DIR_HORIZONTAL, 5);

				set_gridconst(inc_xpanels(), get_panel(), 1, 1);
				add(m_txt_lat_deg, get_gridconst());
				super.add_spacing(DIR_HORIZONTAL, 5);
				set_gridconst(inc_xpanels(), get_panel(), 1, 1);
				add(m_lbl_lat_deg, get_gridconst());

				super.add_spacing(DIR_HORIZONTAL, 15);

				set_gridconst(inc_xpanels(), get_panel(), 1, 1);
				add(m_txt_lat_min, get_gridconst());
				super.add_spacing(DIR_HORIZONTAL, 5);
				set_gridconst(inc_xpanels(), get_panel(), 1, 1);
				add(m_lbl_lat_min, get_gridconst());

				super.add_spacing(DIR_HORIZONTAL, 15);

				set_gridconst(inc_xpanels(), get_panel(), 1, 1);
				add(m_txt_lat_sec, get_gridconst());
				super.add_spacing(DIR_HORIZONTAL, 5);
				set_gridconst(inc_xpanels(), get_panel(), 1, 1);
				add(m_lbl_lat_sec, get_gridconst());

				// GOTO
				super.add_spacing(DIR_HORIZONTAL, 15);
				set_gridconst(inc_xpanels(), get_panel(), 1, 1);
				add(m_btn_go_ll, get_gridconst());

			}

			public void init() {

			}

			public void actionPerformed(ActionEvent e) {
				if (e.getSource().equals(m_btn_go_ll)) {
					exec();
				}
			}

			public void exec() {
				int lon_deg, lon_min;
				int lat_deg, lat_min;
				double lon_sec, lat_sec;
				double lon, lat;
				try {
					lon_deg = Integer.parseInt(m_txt_lon_deg.getText());
					lon_min = Integer.parseInt(m_txt_lon_min.getText());
					lon_sec = Double.parseDouble(m_txt_lon_sec.getText());
					lat_deg = Integer.parseInt(m_txt_lat_deg.getText());
					lat_min = Integer.parseInt(m_txt_lat_min.getText());
					lat_sec = Double.parseDouble(m_txt_lat_sec.getText());
					lon = lon_deg + (lon_min / 60.0) + (lon_sec / 3600.0);
					lat = lat_deg + (lat_min / 60.0) + (lat_sec / 3600.0);

				} catch (Exception err) {
					return;
				}
				CoorConverter.LLCoor coor = new CoorConverter().newLLCoor(lon,
						lat);
				m_callback.actionPerformed(new ActionEvent(coor,
						ActionEvent.ACTION_PERFORMED, "act_goto_map"));
			}

		}

		public class CoorSearchUTM extends DefaultPanel implements
				ActionListener {
			public static final long serialVersionUID = 1;
			private JLabel m_lbl_utm_easting = new JLabel(PAS
					.l("common_utm_easting"));
			private JLabel m_lbl_utm_northing = new JLabel(PAS
					.l("common_utm_northing"));
			private JLabel m_lbl_utm_zone = new JLabel(PAS.l("common_utm_zone"));
			// private JComboBox m_combo_utm_ellipsoid = new JComboBox();
			private StdIntegerArea m_txt_utm_easting = new StdIntegerArea("",
					false, 65, StdIntegerArea.DOUBLE);
			private StdIntegerArea m_txt_utm_northing = new StdIntegerArea("",
					false, 65, StdIntegerArea.DOUBLE);
			private StdTextUTMZone m_txt_utm_zone = new StdTextUTMZone("32V",
					false, 30);

			private JButton m_btn_go_utm = new JButton(PAS
					.l("main_infotab_goto_map"));
			private ActionListener m_callback;

			public CoorSearchUTM(ActionListener a) {
				// m_btn_go_utm.setPreferredSize(new Dimension(75, 16));
				m_callback = a;
				m_btn_go_utm.addActionListener(this);
				add_controls();
			}

			public void add_controls() {
				set_gridconst(0, 0, 1, 1);
				add(m_txt_utm_easting, get_gridconst());
				super.add_spacing(DIR_HORIZONTAL, 5);
				set_gridconst(inc_xpanels(), 0, 1, 1);
				add(m_lbl_utm_easting, get_gridconst());

				super.add_spacing(DIR_HORIZONTAL, 15);

				set_gridconst(inc_xpanels(), 0, 1, 1);
				add(m_txt_utm_northing, get_gridconst());
				super.add_spacing(DIR_HORIZONTAL, 5);
				set_gridconst(inc_xpanels(), 0, 1, 1);
				add(m_lbl_utm_northing, get_gridconst());

				super.add_spacing(DIR_HORIZONTAL, 15);

				set_gridconst(inc_xpanels(), 0, 1, 1);
				add(m_txt_utm_zone, get_gridconst());
				super.add_spacing(DIR_HORIZONTAL, 5);
				set_gridconst(inc_xpanels(), 0, 1, 1);
				add(m_lbl_utm_zone, get_gridconst());

				super.add_spacing(DIR_HORIZONTAL, 15);

				set_gridconst(inc_xpanels(), 0, 1, 1);
				add(m_btn_go_utm, get_gridconst());

			}

			public void init() {

			}

			public void actionPerformed(ActionEvent e) {
				if (e.getSource().equals(m_btn_go_utm)) {
					int ReferenceEllipsoid = 23;
					double UTMNorthing, UTMEasting;
					String UTMZone;
					// UTMZone = m_txt_utm_zone.getText();
					UTMZone = m_txt_utm_zone.getUTM();
					m_txt_utm_zone.setText(UTMZone);
					if (UTMZone.length() != 3) {
						JOptionPane.showMessageDialog(this, PAS
								.l("main_infotab_error_specify_utm_zone"),
								"Error", JOptionPane.ERROR_MESSAGE);
						return;
					}
					try {
						UTMNorthing = Double.parseDouble(m_txt_utm_northing
								.getText());
						UTMEasting = Double.parseDouble(m_txt_utm_easting
								.getText());
					} catch (Exception err) {
						return;
					}

					CoorConverter.LLCoor coor = new CoorConverter().UTM2LL(
							ReferenceEllipsoid, UTMNorthing, UTMEasting,
							UTMZone);
					m_callback.actionPerformed(new ActionEvent(coor,
							ActionEvent.ACTION_PERFORMED, "act_goto_map"));
				}
			}

		}

	}

	class WeatherInfoUI extends DefaultPanel implements ActionListener {
		public static final long serialVersionUID = 1;

		// private XMLWeatherData weatherData;
		private WSWeatherReport weatherData;
		private StdTextLabel lblLocalTime, lblCloudCover;
		private JLabel lblSymbol;
		private JLabel lblDirection;
		// private JLabel lblWeather;
		private StdTextLabel lblTemp;
		private StdTextLabel lblMinMaxTemp;
		private DateTimePicker dtp;
		private StdTextLabel lblSpeed;
		private JPanel pnlSymbols;
		protected UWeatherSearch params = new UWeatherSearch();

		public void actionPerformed(ActionEvent e) {
			if ("act_weatherdata_loaded".equals(e.getActionCommand())) {
				try {
					if (weatherData.getResult(0) != null) {
						set_symbol();
						set_wind_direction();
						set_temp();
						set_temp_min_max();
						set_local_time();
						set_wind_speed();
						set_cloud_cover();
					} else {
						set_error_symbols();
					}
				} catch (Exception er) {

				}

			}
		}

		// int, PAS, String, JFrame, ActionListener callback, String, float,
		// float, int, int, long)

		public WeatherInfoUI() {
			dtp = new DateTimePicker(PAS.get_pas().get_pasactionlistener());
			// weatherData = new XMLWeatherData(Thread.NORM_PRIORITY,
			// PAS.get_pas(), "", this, 5.7, 58.8, 1, 60, dtp.get_datetime());
			// weatherData.start();
			// weatherData = new WSWeatherReport(this, "act_weatherdata_loaded",
			// params);
			add_controls();
			this.setPreferredSize(new Dimension(300, 60));
			setSize(new Dimension(300, 60));
			// updateWeatherData();
		}

		public void add_controls() {
			lblLocalTime = new StdTextLabel("");
			lblLocalTime.setPreferredSize(new Dimension(200, 25));
			// lblLocalTime.setPreferredSize(new Dimension(100,400));
			pnlSymbols = new JPanel();
			lblSymbol = new JLabel();
			lblDirection = new JLabel();
			pnlSymbols.add(lblSymbol);
			pnlSymbols.add(lblDirection);
			lblSpeed = new StdTextLabel("");
			lblCloudCover = new StdTextLabel("");
			lblTemp = new StdTextLabel("");
			lblMinMaxTemp = new StdTextLabel("");

			set_gridconst(0, this.inc_panels(), 1, 1);
			JPanel pnlTing = new JPanel();
			GroupLayout layout = new GroupLayout(pnlTing);
			pnlTing.setLayout(layout);

			layout.setAutoCreateGaps(true);
			layout.setAutoCreateContainerGaps(true);

			StdTextLabel time = new StdTextLabel(PAS.l("common_time"));
			// time.setPreferredSize(new Dimension(200,25));

			StdTextLabel symbols = new StdTextLabel("");
			// symbols.setPreferredSize(new Dimension(50,25));

			StdTextLabel wind_speed = new StdTextLabel(PAS
					.l("main_infotab_weather_windspeed"));
			// wind_speed.setPreferredSize(new Dimension(40,25));

			StdTextLabel cloud_cover = new StdTextLabel(PAS
					.l("main_infotab_weather_cloudcover"));
			// cloud_cover.setPreferredSize(new Dimension(40,25));

			StdTextLabel temperature = new StdTextLabel(PAS
					.l("main_infotab_weather_temperature"));

			StdTextLabel temp_min_max = new StdTextLabel(PAS
					.l("main_infotab_weather_minmax"));

			// lblLocalTime.setPreferredSize(new Dimension(200,25));
			// FontSet time.setFont(font);
			// FontSet symbols.setFont(font);
			// FontSet wind_speed.setFont(font);
			// FontSet cloud_cover.setFont(font);
			// FontSet temperature.setFont(font);
			// FontSet temp_min_max.setFont(font);

			layout.setHorizontalGroup(layout.createSequentialGroup().addGroup(
					layout.createParallelGroup(GroupLayout.Alignment.LEADING)
							.addComponent(time).addComponent(lblLocalTime))
					.addGroup(
							layout.createParallelGroup(
									GroupLayout.Alignment.LEADING)
									.addComponent(symbols).addComponent(
											pnlSymbols)).addGroup(
							layout.createParallelGroup(
									GroupLayout.Alignment.LEADING)
									.addComponent(wind_speed).addComponent(
											lblSpeed)).addGroup(
							layout.createParallelGroup(
									GroupLayout.Alignment.LEADING)
									.addComponent(cloud_cover).addComponent(
											lblCloudCover)).addGroup(
							layout.createParallelGroup(
									GroupLayout.Alignment.LEADING)
									.addComponent(temperature).addComponent(
											lblTemp)).addGroup(
							layout.createParallelGroup(
									GroupLayout.Alignment.LEADING)
									.addComponent(temp_min_max).addComponent(
											lblMinMaxTemp)));
			layout.setVerticalGroup(layout.createSequentialGroup().addGroup(
					layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
							.addComponent(time).addComponent(symbols)
							.addComponent(wind_speed).addComponent(cloud_cover)
							.addComponent(temperature).addComponent(
									temp_min_max)).addGroup(
					layout.createParallelGroup(GroupLayout.Alignment.CENTER)
							.addComponent(lblLocalTime)
							.addComponent(pnlSymbols).addComponent(lblSpeed)
							.addComponent(lblCloudCover).addComponent(lblTemp)
							.addComponent(lblMinMaxTemp)));

			add(pnlTing);

			// set_gridconst(0, this.inc_panels(), 1, 1);
			// add(new StdTextLabel("Time"), this.get_gridconst());
			// this.get_gridconst().anchor = GridBagConstraints.LINE_END;
			// set_gridconst(1, this.get_panel(), 1, 1);
			// add(new StdTextLabel(""), this.get_gridconst());
			// set_gridconst(2, this.get_panel(), 1, 1);
			// add(new StdTextLabel("Wind speed"), this.get_gridconst());
			// set_gridconst(3, this.get_panel(), 1, 1);
			// add(new StdTextLabel("Cloud cover"), this.get_gridconst());
			// set_gridconst(4, this.get_panel(), 1, 1);
			// add(new StdTextLabel("Temperature"), this.get_gridconst());
			// set_gridconst(5, this.get_panel(), 1, 1);
			// add(new StdTextLabel("(Min / Max)"), this.get_gridconst());
			//			
			// set_gridconst(0, this.inc_panels(), 2, 1);
			// add(lblLocalTime, this.get_gridconst());
			// this.get_gridconst().anchor = GridBagConstraints.PAGE_START;
			// set_gridconst(1, this.get_panel(), 2, 1);
			// pnlSymbols.setAlignmentX(JPanel.TOP_ALIGNMENT);
			// pnlSymbols.setBackground(Color.RED);
			// add(pnlSymbols, this.get_gridconst());
			// set_gridconst(2, this.get_panel(), 2, 1);
			// add(lblSpeed, this.get_gridconst());
			// set_gridconst(3, this.get_panel(), 2, 1);
			// add(lblCloudCover, this.get_gridconst());
			// set_gridconst(4, this.get_panel(), 2, 1);
			// add(lblTemp, this.get_gridconst());
			// set_gridconst(5, this.get_panel(), 2, 1);
			// add(lblMinMaxTemp, this.get_gridconst());

			init();
		}

		public WSWeatherReport getWeatherData() {
			return weatherData;
		}

		private void updateWeatherData() {
			// weatherData.set_centerx(Variables.NAVIGATION.calc_centerpoint_x(PAS.get_pas().get_mappane().get_current_mousepos().x));
			// weatherData.set_centery(Variables.NAVIGATION.calc_centerpoint_y(PAS.get_pas().get_mappane().get_current_mousepos().y));
			// weatherData.set_date(dtp.get_datetime());
			// weatherData.updateForm();
			// weatherData.run();
			weatherData = new WSWeatherReport(this, "act_weatherdata_loaded",
					params);
			params.setLat(Variables.getNavigation().calc_centerpoint_y(PAS.get_pas()
                    .get_mappane().get_current_mousepos().y));
			params.setLon(Variables.getNavigation().calc_centerpoint_x(PAS.get_pas()
                    .get_mappane().get_current_mousepos().x));
			params.setDate(0);
			params.setForecasts(1);
			params.setInterval(0);
			weatherData.setParams(params);

			weatherData.start();
		}

		public void set_local_time() {
			String temp;
			try {
				String timetemp = Long.toString(weatherData.getResult(0)
                        .getLocaltime());// String.valueOf(weatherData.get_local_time());

				temp = timetemp.substring(6, 8) + ".";
				temp += timetemp.substring(4, 6) + ".";
				temp += timetemp.substring(0, 4) + " ";
				temp += timetemp.substring(8, 10) + ":";
				temp += timetemp.substring(11, 13);
				lblLocalTime.setText(temp);
			} catch (Exception e) {

			}
		}

		public void set_symbol() {
			// ImageIcon icon = ImageLoader.load_icon(weatherData.getSymbol());
			// Image image = icon.getImage();
			// image = createImage(new FilteredImageSource(image.getSource(),
			// new
			// CropImageFilter(0,5,icon.getIconWidth(),icon.getIconHeight())));
			// image = createImage(new FilteredImageSource(image.getSource(),
			// new GrayFilter(true, 10)));
			// lblSymbol.setIcon(icon = new ImageIcon(image));
			// lblSymbol.setAlignmentX(JLabel.TOP_ALIGNMENT);
			// lblSymbol.setIcon(ImageLoader.load_icon(weatherData.getSymbol()));
			try {
				lblSymbol.setIcon(ImageLoader.load_icon(weatherData
						.getResult(0).getSymbol()
						+ ".gif"));
			} catch (Exception e) {

			}

		}

		public void set_error_symbols() {
			ImageIcon unk = ImageLoader.load_icon("unknown_24.png");
			lblSymbol.setIcon(unk);
			lblCloudCover.setIcon(null);
			lblDirection.setIcon(null);
			lblLocalTime.setText("N/A");
			lblTemp.setText("");
			lblMinMaxTemp.setText("");
			lblCloudCover.setText("");
			lblDirection.setText("");
			lblSpeed.setText(PAS.l("main_infotab_weather_nodata"));

		}

		public void set_wind_direction() {
			// ImageIcon icon =
			// ImageLoader.load_icon(weatherData.getWindDirection());
			// Image image = icon.getImage();
			// image = createImage(new FilteredImageSource(image.getSource(),
			// new
			// CropImageFilter(0,3,icon.getIconWidth(),icon.getIconHeight()-5)));
			// lblDirection.setIcon(new ImageIcon(image));
			// lblDirection.setIcon(ImageLoader.load_icon(weatherData.getWindDirection()));
			try {
				lblDirection.setIcon(ImageLoader.load_icon(WSWeatherReport
						.getWindIcon(weatherData.getResult(0)
								.getWinddirection())));
			} catch (Exception e) {

			}
		}

		public void set_temp() {
			// lblTemp.setText(String.valueOf(weatherData.get_temp()) +
			// "\u00b0C");
			try {
				lblTemp.setText(String.valueOf(weatherData.getResult(0)
						.getTemperature())
						+ "\u00b0C");
			} catch (Exception e) {

			}
		}

		public void set_temp_min_max() {
			try {
				// lblMinMaxTemp.setText("(" +
				// String.valueOf(weatherData.getResult(0).getTemperaturemin())
				// + "\u00b0C / " +
				// String.valueOf(weatherData.getResult(0).getTemperaturemax())
				// + "\u00b0C)");
				lblMinMaxTemp.setText(String.valueOf(Math.round(weatherData
						.getResult(0).getTemperaturemax()))
						+ "\u00b0C");
			} catch (Exception e) {

			}
		}

		public void set_wind_speed() {
			try {
				lblSpeed.setText(String.valueOf(weatherData.getResult(0)
						.getWindspeed()
						+ "m/s"));
			} catch (Exception e) {

			}
		}

		public void set_cloud_cover() {
			try {
				lblCloudCover.setText(String.valueOf(weatherData.getResult(0)
						.getCloudcover()
						+ "%"));
			} catch (Exception e) {

			}
		}

		public void init() {
			setVisible(true);
		}

		@Override
		public int getWantedHeight() {
			return 130;
		}

		@Override
		public int getWantedWidth() {
			return super.getWantedWidth();
		}

	}

	/*
	 * public void getWeatherData() { try { HttpPostForm form = new
	 * HttpPostForm(
	 * "http://vb4utv.ums.no/PAS_weather.asp?f_centerx=5.7&f_centery=58.8&l_forecasts=1&l_interval=60&l_date=2007061412"
	 * ); //HttpPostForm form = new HttpPostForm(PAS.get_pas().get_sitename() +
	 * "/PAS_weather.asp?f_centerx=" + )
	 * 
	 * InputStream is = form.post(); BufferedReader bis= new BufferedReader(new
	 * InputStreamReader(is)); String line = bis.readLine(); String concated =
	 * ""; while(line != null) { concated += line; line = bis.readLine(); }
	 * System.out.println(concated); } catch(IOException ioe) {
	 * Error.getError().addError("Error during weather post", "", ioe,
	 * Error.SEVERITY_ERROR); } }
	 */

}