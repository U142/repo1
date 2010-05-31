package no.ums.pas.send.sendpanels;

import java.awt.Dimension;
import java.io.IOException;

import no.ums.pas.PAS;
import no.ums.pas.core.dataexchange.*;
import no.ums.pas.core.webdata.*;
import no.ums.pas.core.ws.WSAdrcount;
import no.ums.pas.maps.defines.*;
import no.ums.pas.send.SendController;
import no.ums.pas.send.SendPropertiesPolygon;
import no.ums.pas.ums.errorhandling.Error;
import no.ums.pas.ums.tools.StdTextLabel;


/*
 * Show description of selected area and addresscount
 */
public class Sending_AddressPanelPolygon extends Sending_AddressPanel {
	public static final long serialVersionUID = 1;
	private StdTextLabel m_lbl_pointcount;

	public Sending_AddressPanelPolygon(PAS pas, SendWindow parentwin) {
		super(pas, parentwin);
		m_lbl_pointcount = new StdTextLabel("");//"Polygon points: " + 
								//((SendPropertiesPolygon)get_parent().get_sendobject().get_sendproperties()).get_shapestruct().typecast_polygon().get_size() +
								//" Simplified: " + ((SendPropertiesPolygon)get_parent().get_sendobject().get_sendproperties()).get_shapestruct().typecast_polygon().get_show_size());
		m_lbl_pointcount.setPreferredSize(new Dimension(200, 16));
		add_controls();
	}
	

    
	public final void add_controls() {
		int n_width = 10;
		_add(m_lbl_pointcount, 0, inc_panels(), n_width, 1);
		super.add_controls();
	}
	protected final void exec_adrcount() {
		HttpPostForm form = null;
		//XMLAdrCount xml = null;
		try {
			/*NavStruct bounds = get_parent().get_sendobject().get_sendproperties().get_shapestruct().typecast_polygon().calc_bounds();
			form = new HttpPostForm(get_url());
			
			get_parent().get_sendobject().get_sendproperties().get_shapestruct().typecast_polygon().setCurrentViewMode(PolygonStruct.SHOW_POLYGON_SIMPLIFIED_PRMETERS, 50, PAS.get_pas().get_navigation());
			m_lbl_pointcount.setText("Polygon points: " + 
					((SendPropertiesPolygon)get_parent().get_sendobject().get_sendproperties()).get_shapestruct().typecast_polygon().get_size() +
					" Simplified: " + ((SendPropertiesPolygon)get_parent().get_sendobject().get_sendproperties()).get_shapestruct().typecast_polygon().get_show_size());
			PAS.get_pas().kickRepaint();
			//reform addresstypes
			int newadrtypes = 0;
			newadrtypes = GetAdrTypesForCount(get_parent().get_sendobject().get_sendproperties().get_addresstypes());
			form.setParameter("n_addresstypes", new Integer(newadrtypes).toString());
			//form.setParameter("n_polypoints", new Integer(get_parent().get_sendobject().get_sendproperties().typecast_poly().get_polygon().get_size()).toString());
			form.setParameter("n_sendingtype", new Integer(get_parent().get_sendobject().get_sendproperties().get_sendingtype()).toString());
			form.setParameter("lbo", new Double(bounds._lbo).toString());
			form.setParameter("rbo", new Double(bounds._rbo).toString());
			form.setParameter("ubo", new Double(bounds._ubo).toString());
			form.setParameter("bbo", new Double(bounds._bbo).toString());
			
			SendPropertiesPolygon poly = get_parent().get_sendobject().get_sendproperties().typecast_poly();
			poly.create_paramvals();
			for(int i=0; i < poly.get_polygon_params().length; i++) {
				form.setParameter(poly.get_polygon_params()[i], poly.get_polygon_vals()[i]);
			}
			get_parent().set_comstatus("Performing addresscount");
			xml = new XMLAdrCount(form, get_url(), null, new HTTPReq(PAS.get_pas().get_sitename()), this, ADRCOUNT_CALLBACK_ACTION_);
			try {
				xml.start();
			} catch(Exception e) {
				PAS.get_pas().add_event("ERROR: exec_adrcount().xml.start() " + e.getMessage(), e);
				PAS.get_pas().printStackTrace(e.getStackTrace());
				Error.getError().addError("SendOptionToolbar","Exception in exec_adrcount",e,1);
			}*/
			get_parent().get_sendobject().get_sendproperties().PerformAdrCount(this, ADRCOUNT_CALLBACK_ACTION_);
		} catch(Exception e) {
			PAS.get_pas().add_event("Error exec_adrcount() " + e.getMessage(), e);
			PAS.get_pas().printStackTrace(e.getStackTrace());
			Error.getError().addError("SendOptionToolbar","Exception in exec_adrcount",e,1);
		}
	}
}