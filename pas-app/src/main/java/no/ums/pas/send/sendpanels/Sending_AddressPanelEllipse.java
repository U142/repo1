package no.ums.pas.send.sendpanels;

import no.ums.pas.PAS;
import no.ums.pas.localization.Localization;
import no.ums.pas.maps.defines.MapPoint;
import no.ums.pas.maps.defines.MapPointLL;
import no.ums.pas.ums.errorhandling.Error;
import no.ums.pas.ums.tools.StdTextLabel;

import java.awt.Dimension;
/*
 * Show description of selected area and addresscount
 */
public class Sending_AddressPanelEllipse extends Sending_AddressPanel {
	public static final long serialVersionUID = 1;
	private StdTextLabel m_lbl_pointcount;
	
	public Sending_AddressPanelEllipse(PAS pas, SendWindow parentwin) {
		super(pas, parentwin);
		//m_lbl_pointcount = new StdTextLabel("Ellipse center: " + ((SendPropertiesPolygon)get_parent().get_sendobject().get_sendproperties()).get_polygon().get_size());
		double n_radius_width = 0;
		double n_radius_height = 0;
		MapPointLL mpll_width_start = get_parent().get_sendobject().get_sendproperties().typecast_ellipse().get_center().get_mappointll();
		MapPointLL mpll_width_end = new MapPointLL(get_parent().get_sendobject().get_sendproperties().typecast_ellipse().get_corner().get_lon(), get_parent().get_sendobject().get_sendproperties().typecast_ellipse().get_center().get_lat());
		MapPoint mp_width_start = new MapPoint(pas.get_navigation(), mpll_width_start);
		MapPoint mp_width_end   = new MapPoint(pas.get_navigation(), mpll_width_end);
		
		MapPointLL mpll_height_start = get_parent().get_sendobject().get_sendproperties().typecast_ellipse().get_center().get_mappointll();
		MapPointLL mpll_height_end   = new MapPointLL(get_parent().get_sendobject().get_sendproperties().typecast_ellipse().get_center().get_lon(), get_parent().get_sendobject().get_sendproperties().typecast_ellipse().get_corner().get_lat());
		MapPoint mp_height_start = new MapPoint(pas.get_navigation(), mpll_height_start);
		MapPoint mp_height_end   = new MapPoint(pas.get_navigation(), mpll_height_end);
		
		
		//n_radius_width = Math.abs(get_parent().get_sendobject().get_sendproperties().typecast_ellipse().get_corner().get_lon() - get_parent().get_sendobject().get_sendproperties().typecast_ellipse().get_center().get_lon();
		n_radius_width = pas.get_navigation().calc_distance(mp_width_start, mp_width_end);
		n_radius_height= pas.get_navigation().calc_distance(mp_height_start, mp_height_end);
        m_lbl_pointcount = new StdTextLabel(Localization.l("main_sending_adr_ellipse_center") + ": " + get_parent().get_sendobject().get_sendproperties().typecast_ellipse().get_center() + "  " + Localization.l("common_width") + ": " + n_radius_width + Localization.l("common_meters_short") + " " + Localization.l("common_height") + ": " + n_radius_height + Localization.l("common_meters_short"));
		m_lbl_pointcount.setPreferredSize(new Dimension(400, 16));
		add_controls();
	}
	public final void add_controls() {
		int n_width = 10;
		_add(m_lbl_pointcount, 0, inc_panels(), n_width, 1);
		super.add_controls();
	}
	protected final void exec_adrcount() {
		//HttpPostForm form = null;
		//XMLAdrCount xml = null;
		try {
			//NavStruct bounds = get_parent().get_sendobject().get_sendproperties().typecast_poly().get_polygon().calc_bounds();
			/*NavStruct bounds = get_parent().get_sendobject().get_sendproperties().typecast_ellipse().calc_bounds();
			form = new HttpPostForm(get_url());
			int newadrtypes = 0;
			newadrtypes = GetAdrTypesForCount(get_parent().get_sendobject().get_sendproperties().get_addresstypes());
			form.setParameter("n_addresstypes", new Integer(newadrtypes).toString());
			//form.setParameter("n_polypoints", new Integer(get_parent().get_sendobject().get_sendproperties().typecast_poly().get_polygon().get_size()).toString());
			form.setParameter("n_sendingtype", new Integer(get_parent().get_sendobject().get_sendproperties().get_sendingtype()).toString());
			form.setParameter("lbo", new Double(bounds._lbo).toString());
			form.setParameter("rbo", new Double(bounds._rbo).toString());
			form.setParameter("ubo", new Double(bounds._ubo).toString());
			form.setParameter("bbo", new Double(bounds._bbo).toString());
			
			SendPropertiesEllipse ellipse = get_parent().get_sendobject().get_sendproperties().typecast_ellipse();
			form.setParameter("center_x", new Float(ellipse.get_center().get_lon()).toString());
			form.setParameter("center_y", new Float(ellipse.get_center().get_lat()).toString());
			form.setParameter("radius_x", new Float(Math.abs(ellipse.get_corner().get_lon() - ellipse.get_center().get_lon())*10000).toString());
			form.setParameter("radius_y", new Float(Math.abs(ellipse.get_corner().get_lat() - ellipse.get_center().get_lat())*10000).toString());
			
			System.out.println(new Double(ellipse.get_center().get_lon()).toString());
			System.out.println(new Double(ellipse.get_center().get_lat()).toString());
			System.out.println(new Float(Math.abs(ellipse.get_corner().get_lon() - ellipse.get_center().get_lon())).toString());
			System.out.println(new Float(Math.abs(ellipse.get_corner().get_lat() - ellipse.get_center().get_lat())).toString());
			
			get_parent().set_comstatus("Performing addresscount");
			xml = new XMLAdrCount(form, get_url(), null, new HTTPReq(PAS.get_pas().get_sitename()), this, ADRCOUNT_CALLBACK_ACTION_);
			try {
				xml.start();
			} catch(Exception e) {
				PAS.get_pas().add_event("ERROR: exec_adrcount().xml.start() " + e.getMessage(), e);
				PAS.get_pas().printStackTrace(e.getStackTrace());
				Error.getError().addError("SendOptionToolbar","Exception in exec_adrcount",e,1);
			}*/
			get_parent().get_sendobject().get_sendproperties().typecast_ellipse().PerformAdrCount(this, ADRCOUNT_CALLBACK_ACTION_);

		} catch(Exception e) {
			PAS.get_pas().add_event("Error exec_adrcount() " + e.getMessage(), e);
			PAS.get_pas().printStackTrace(e.getStackTrace());
			Error.getError().addError("SendOptionToolbar","Exception in exec_adrcount",e,1);
		}
	}
}