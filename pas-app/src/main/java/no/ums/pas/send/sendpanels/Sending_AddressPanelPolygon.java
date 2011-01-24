package no.ums.pas.send.sendpanels;

import no.ums.pas.PAS;
import no.ums.pas.ums.errorhandling.Error;
import no.ums.pas.ums.tools.StdTextLabel;

import java.awt.*;


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
		try {
			get_parent().get_sendobject().get_sendproperties().PerformAdrCount(this, ADRCOUNT_CALLBACK_ACTION_);
		} catch(Exception e) {
			PAS.get_pas().add_event("Error exec_adrcount() " + e.getMessage(), e);
			PAS.get_pas().printStackTrace(e.getStackTrace());
			Error.getError().addError("SendOptionToolbar","Exception in exec_adrcount",e,1);
		}
	}
}