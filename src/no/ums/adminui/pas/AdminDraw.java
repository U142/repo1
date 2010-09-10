package no.ums.adminui.pas;

import java.awt.Component;
import java.awt.Point;

import no.ums.pas.Draw;
import no.ums.pas.PAS;
import no.ums.pas.PASDraw;
import no.ums.pas.core.variables;
import no.ums.pas.core.logon.DeptArray;
import no.ums.pas.core.logon.DeptInfo;

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
		variables.SENDCONTROLLER.get_activesending().get_sendproperties().calc_coortopix();
	}

	@Override
	protected void draw_layers() {
		DeptArray da = variables.USERINFO.get_departments();
		for(int i=0; i < da.size(); i++) {
			DeptInfo di = (DeptInfo)da.get(i);
			di.drawRestrictionShapes(get_offscreen(), variables.NAVIGATION);
		}
		variables.SENDCONTROLLER.get_activesending().get_sendproperties().draw(get_offscreen(), new Point(0,0));
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
