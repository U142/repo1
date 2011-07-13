package no.ums.adminui.pas;

import no.ums.pas.PAS;
import no.ums.pas.PASDraw;
import no.ums.pas.core.Variables;
import no.ums.pas.core.logon.DeptArray;
import no.ums.pas.core.logon.DeptInfo;
import no.ums.pas.maps.defines.ShapeStruct;

import java.awt.*;
import java.util.List;

public class AdminDraw extends PASDraw {

	public AdminDraw(Component component, int l_pri, int x, int y) {
		super(null, component, l_pri, x, y);
	}

	@Override
	protected void calc_new_coors() {
		DeptArray da = Variables.getUserInfo().get_departments();
		for(int i=0; i < da.size(); i++) {
			DeptInfo di = (DeptInfo)da.get(i);
			di.CalcCoorRestrictionShapes();
		}
		if(Variables.getSendController() != null && Variables.getSendController().get_activesending() != null)
			Variables.getSendController().get_activesending().get_sendproperties().calc_coortopix();
		List<ShapeStruct> list = Variables.getUserInfo().get_departments().get_combined_restriction_shape();
		for(int j=0; j < list.size(); j++)
		{
			list.get(j).calc_coortopix(Variables.getNavigation());
		}
	}

	@Override
    public void draw_layers(Graphics g) {
		DeptArray da = Variables.getUserInfo().get_departments();
		for(int i=0; i < da.size(); i++) {
			DeptInfo di = (DeptInfo)da.get(i);
			di.drawRestrictionShapes(get_offscreen(), Variables.getNavigation());
		}
		List<ShapeStruct> list = Variables.getUserInfo().get_departments().get_combined_restriction_shape();
		for(int j=0; j < list.size(); j++)
		{
			list.get(j).set_fill_color(Color.black);
			list.get(j).draw(get_offscreen(), Variables.getMapFrame().getMapModel(), Variables.getMapFrame().getZoomLookup(), false, true, false, null, true, true, 1, false);
		}
		if(Variables.getSendController().get_activesending() != null)
			Variables.getSendController().get_activesending().get_sendproperties().draw(get_offscreen(), new Point(Variables.getMapFrame().get_current_mousepos().x, Variables.getMapFrame().get_current_mousepos().y));
		/*
		if(Variables.MAPPANE.get_actionhandler().get_isdragging() && Variables.MAPPANE.get_mode()==MapFrame.MAP_MODE_ZOOM)
		{
			super.get_mappane().drawOnEvents(Variables.MAPPANE.getGraphics());
		}
		if(Variables.MAPPANE.IsLoading())
		{
			super.get_mappane().drawOnEvents(Variables.MAPPANE.getGraphics());
		}*/
		
		super.draw_layers(g);
	}

	@Override
	protected synchronized void checkforupdates() {
	}

	@Override
	protected PAS get_pas() {
		return super.get_pas();
	}

	@Override
	protected void map_repaint() {
		super.map_repaint();
	}

}
