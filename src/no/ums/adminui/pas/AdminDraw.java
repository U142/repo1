package no.ums.adminui.pas;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.util.List;

import no.ums.pas.Draw;
import no.ums.pas.PAS;
import no.ums.pas.PASDraw;
import no.ums.pas.core.variables;
import no.ums.pas.core.logon.DeptArray;
import no.ums.pas.core.logon.DeptInfo;
import no.ums.pas.maps.MapFrame;
import no.ums.pas.maps.defines.ShapeStruct;

public class AdminDraw extends PASDraw {

	public AdminDraw(Component component, int l_pri, int x, int y) {
		super(null, component, l_pri, x, y);
	}

	@Override
	protected void calc_new_coors() {
		DeptArray da = variables.USERINFO.get_departments();
		for(int i=0; i < da.size(); i++) {
			DeptInfo di = (DeptInfo)da.get(i);
			di.CalcCoorRestrictionShapes();
		}
		if(variables.SENDCONTROLLER != null && variables.SENDCONTROLLER.get_activesending() != null)
			variables.SENDCONTROLLER.get_activesending().get_sendproperties().calc_coortopix();
		List<ShapeStruct> list = variables.USERINFO.get_departments().get_combined_restriction_shape();
		for(int j=0; j < list.size(); j++)
		{
			list.get(j).calc_coortopix(variables.NAVIGATION);
		}
	}

	@Override
	protected void draw_layers() {
		DeptArray da = variables.USERINFO.get_departments();
		for(int i=0; i < da.size(); i++) {
			DeptInfo di = (DeptInfo)da.get(i);
			di.drawRestrictionShapes(get_offscreen(), variables.NAVIGATION);
		}
		List<ShapeStruct> list = variables.USERINFO.get_departments().get_combined_restriction_shape();
		for(int j=0; j < list.size(); j++)
		{
			list.get(j).set_fill_color(Color.black);
			list.get(j).draw(get_offscreen(), variables.NAVIGATION, false, true, false, null, true, true, 1, false);
		}
		if(variables.SENDCONTROLLER.get_activesending() != null)
			variables.SENDCONTROLLER.get_activesending().get_sendproperties().draw(get_offscreen(), new Point(variables.MAPPANE.get_current_mousepos().x,variables.MAPPANE.get_current_mousepos().y));
		/*
		if(variables.MAPPANE.get_actionhandler().get_isdragging() && variables.MAPPANE.get_mode()==MapFrame.MAP_MODE_ZOOM)
		{
			super.get_mappane().drawOnEvents(variables.MAPPANE.getGraphics());
		}
		if(variables.MAPPANE.IsLoading())
		{
			super.get_mappane().drawOnEvents(variables.MAPPANE.getGraphics());
		}*/
		
		super.draw_layers();
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
