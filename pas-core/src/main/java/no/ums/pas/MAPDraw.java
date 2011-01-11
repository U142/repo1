package no.ums.pas;

import java.awt.Component;
import java.awt.Graphics2D;
import java.util.List;

import no.ums.pas.core.variables;
import no.ums.pas.core.controllers.HouseController;
import no.ums.pas.core.logon.DeptArray;
import no.ums.pas.core.logon.DeptInfo;
import no.ums.pas.maps.MapFrame;
import no.ums.pas.maps.defines.ShapeStruct;
import no.ums.pas.send.SendController;
import no.ums.pas.ums.errorhandling.Error;

public class MAPDraw extends Draw {
	
	ParmController m_parmcontroller;
	SendController m_sendcontroller;
	HouseController m_housecontroller;
	
	UpdateThread m_checkforupdates_thread;
	
	public void set_parmcontroller(ParmController parmcontroller) { m_parmcontroller = parmcontroller; }
	public void set_sendcontroller(SendController sendcontroller) { m_sendcontroller = sendcontroller; }
	public void set_housecontroller(HouseController housecontroller) { m_housecontroller = housecontroller; }
	
	public MAPDraw(Component component, int l_pri, int x, int y) {
		super(component, l_pri, x, y);
		
		m_checkforupdates_thread = new UpdateThread(1000); //check every one second
	}
	/*
	protected void calc_new_coors() {
		get_pas().get_statuscontroller().calcHouseCoords();
		m_housecontroller.calcHouseCoords();
		if(get_pas().get_statuscontroller().get_sendinglist()!=null) {
			for(int i=0; i < get_pas().get_statuscontroller().get_sendinglist().size(); i++) {
				try {
					if(get_pas().get_statuscontroller().get_sendinglist().get_sending(i).get_shape()!=null)
						get_pas().get_statuscontroller().get_sendinglist().get_sending(i).get_shape().calc_coortopix(get_pas().get_navigation());
				} catch(Exception e) {
					
				}
			}
		}
		
		try
		{
			for(int i=0; i < m_sendcontroller.get_sendings().size(); i++)
			{
				try
				{
					m_sendcontroller.get_sendings().get(i).get_sendproperties().calc_coortopix();
				}
				catch(Exception e)
				{
					
				}
				
			}
		}
		catch(Exception e)
		{
			
		}
		get_pas().get_gpscontroller().calcGpsCoords();	
		if(get_pas().get_parmcontroller()!=null)
			get_pas().get_parmcontroller().calc_coortopix();
		if(get_pas().get_eastcontent().get_taspanel()!=null)
			get_pas().get_eastcontent().get_taspanel().calc_coortopix();
		super.calc_new_coors();
	}*/
	protected void draw_layers() {
		try
		{
			
			DeptArray depts = variables.USERINFO.get_departments();
			//depts.ClearCombinedRestrictionShapelist();
			//depts.CreateCombinedRestrictionShape(null, null, 0, POINT_DIRECTION.UP, -1);
			//depts.test();
			for(int i=0; i < depts.size(); i++)
			{
				((DeptInfo)depts.get(i)).drawRestrictionShapes(m_gfx_buffer, variables.NAVIGATION);
			}
			List<ShapeStruct> list = variables.USERINFO.get_departments().get_combined_restriction_shape();
			for(int i=0; i < list.size(); i++)
			{
				list.get(i).draw(m_gfx_buffer, variables.NAVIGATION, false, true, false, null, true, true, 1, false);
			}

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		if(m_parmcontroller!=null)
			m_parmcontroller.drawLayers(m_gfx_buffer);
		try {
			m_sendcontroller.draw_polygons(m_gfx_buffer, m_mappane.get_current_mousepos());
		} catch(Exception e) { Error.getError().addError("PASDraw","Exception in draw_layers",e,1); }
		try {
			m_mappane.draw_pinpoint(m_gfx_buffer);
		} catch(Exception e) { Error.getError().addError("PASDraw","Exception in draw_layers",e,1); }
		try {
			m_mappane.draw_adredit(m_gfx_buffer);
		} catch(Exception e) { Error.getError().addError("PASDraw","Exception in draw_layers",e,1); }
		try {
			if(m_mappane.get_mode()==MapFrame.MAP_MODE_HOUSEEDITOR_) {
				switch(m_mappane.get_submode()) {
					case MapFrame.MAP_HOUSEEDITOR_SET_PRIVATE_COOR:
					case MapFrame.MAP_HOUSEEDITOR_SET_COMPANY_COOR:
						m_mappane.draw_moveinhab_text(m_gfx_buffer);
						break;
				}
						
			}
		} catch(Exception e) { }
		super.draw_layers();
	}
	protected void calc_new_coors() {
				
		try
		{
			for(int i=0; i < variables.SENDCONTROLLER.get_sendings().size(); i++)
			{
				try
				{
					variables.SENDCONTROLLER.get_sendings().get(i).get_sendproperties().calc_coortopix();
				}
				catch(Exception e)
				{
					
				}
				
			}
		}
		catch(Exception e)
		{
			
		}
		
		try
		{
			DeptArray depts = variables.USERINFO.get_departments();
			for(int i=0; i < depts.size(); i++)
			{
				((DeptInfo)depts.get(i)).CalcCoorRestrictionShapes();
			}
			List<ShapeStruct> list = variables.USERINFO.get_departments().get_combined_restriction_shape();
			for(int i=0; i < list.size(); i++)
			{
				list.get(i).calc_coortopix(variables.NAVIGATION);
			}
			//get_pas().get_userinfo().get_current_department().CalcCoorRestrictionShapes();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		super.calc_new_coors();
	}
	protected void map_repaint() {
		//get_pas().get_mappane().repaint();
		//super.map_repaint();
	}
	protected synchronized void checkforupdates() {
		try {
			if(m_checkforupdates_thread.isAlive())
				m_checkforupdates_thread.interrupt();
		} catch(Exception e) { }
		super.checkforupdates();
		//if(m_checkforupdates_thread!=null)
		//	if(m_checkforupdates_thread.isAlive())
		//		return;
		m_checkforupdates_thread = new UpdateThread(1000); //check every one second
	}
	
	class UpdateThread extends Thread implements Runnable {
		int m_n_msec;
		UpdateThread(int ms) {
			super("MAPDraw.Update Thread");
			m_n_msec = ms;
			start();
		}
		
		public void run() {
			try {
				Thread.sleep(m_n_msec);
			} catch(InterruptedException e) {
				//no longer need to wait for autoupdate. this thread will restart when updates are checked
				return;
			}
			checkforupdates();
		}
	}
	
}