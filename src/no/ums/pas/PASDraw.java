package no.ums.pas;

import java.awt.Component;
import java.awt.Graphics2D;
import java.util.List;

import no.ums.pas.core.logon.DeptArray;
import no.ums.pas.core.logon.DeptInfo;
import no.ums.pas.core.logon.DeptArray.POINT_DIRECTION;
import no.ums.pas.maps.MapFrame;
import no.ums.pas.maps.defines.PolygonStruct;
import no.ums.pas.maps.defines.ShapeStruct;
import no.ums.pas.ums.errorhandling.Error;


public class PASDraw extends Draw {
	PAS m_pas;
	UpdateThread m_checkforupdates_thread;
	protected PAS get_pas() { return m_pas; }
	
	PASDraw(PAS pas, Component component, int l_pri, int x, int y) {
		super(component, l_pri, x, y);
		m_pas = pas;
		m_checkforupdates_thread = new UpdateThread(1000); //check every one second
	}
	
	protected void calc_new_coors() {
		get_pas().get_statuscontroller().calcHouseCoords();
		get_pas().get_housecontroller().calcHouseCoords();
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
			for(int i=0; i < get_pas().get_sendcontroller().get_sendings().size(); i++)
			{
				try
				{
					get_pas().get_sendcontroller().get_sendings().get(i).get_sendproperties().calc_coortopix();
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
		try
		{
			DeptArray depts = get_pas().get_userinfo().get_departments();
			for(int i=0; i < depts.size(); i++)
			{
				((DeptInfo)depts.get(i)).CalcCoorRestrictionShapes();
			}
			List<ShapeStruct> list = get_pas().get_userinfo().get_departments().get_combined_restriction_shape();
			for(int i=0; i < list.size(); i++)
			{
				list.get(i).calc_coortopix(get_pas().get_navigation());
			}
			//get_pas().get_userinfo().get_current_department().CalcCoorRestrictionShapes();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		super.calc_new_coors();
	}
	protected void draw_layers() {
		
		try
		{
			
			DeptArray depts = get_pas().get_userinfo().get_departments();
			//depts.ClearCombinedRestrictionShapelist();
			//depts.CreateCombinedRestrictionShape(null, null, 0, POINT_DIRECTION.UP, -1);
			//depts.test();
			for(int i=0; i < depts.size(); i++)
			{
				((DeptInfo)depts.get(i)).drawRestrictionShapes(m_gfx_buffer, PAS.get_pas().get_navigation());
			}
			List<ShapeStruct> list = get_pas().get_userinfo().get_departments().get_combined_restriction_shape();
			for(int i=0; i < list.size(); i++)
			{
				list.get(i).draw(m_gfx_buffer, get_pas().get_navigation(), false, true, false, null, true, true, 1, false);
			}

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		if(get_pas().get_parmcontroller()!=null)
			get_pas().get_parmcontroller().drawLayers(m_gfx_buffer);
		try {
			get_pas().get_sendcontroller().draw_polygons(m_gfx_buffer, PAS.get_pas().get_mappane().get_current_mousepos());
		} catch(Exception e) { Error.getError().addError("PASDraw","Exception in draw_layers",e,1); }
		if(get_pas().get_mainmenu().get_selectmenu().get_bar().get_show_houses())
			get_pas().get_housecontroller().drawItems(m_gfx_buffer);
		try {
			get_pas().get_mappane().draw_pinpoint(m_gfx_buffer);
		} catch(Exception e) { Error.getError().addError("PASDraw","Exception in draw_layers",e,1); }
		try {
			get_pas().get_mappane().draw_adredit(m_gfx_buffer);
		} catch(Exception e) { Error.getError().addError("PASDraw","Exception in draw_layers",e,1); }
		try {
			if(get_pas().get_mappane().get_mode()==MapFrame.MAP_MODE_HOUSEEDITOR_) {
				switch(get_pas().get_mappane().get_submode()) {
					case MapFrame.MAP_HOUSEEDITOR_SET_PRIVATE_COOR:
					case MapFrame.MAP_HOUSEEDITOR_SET_COMPANY_COOR:
						get_pas().get_mappane().draw_moveinhab_text(m_gfx_buffer);
						break;
				}
						
			}
		} catch(Exception e) { }
		try {
			get_pas().get_statuscontroller().drawItems(m_gfx_buffer);
		} catch(Exception e) { Error.getError().addError("PASDraw","Exception in draw_layers",e,1); }
		//get_pas().get_mappane().drawOnEvents(m_gfx_buffer);
		try {
			get_pas().get_gpscontroller().drawItems(m_gfx_buffer);
		} catch(Exception e) { Error.getError().addError("PASDraw","Exception in draw_layers",e,1); }
		try {
			get_pas().get_housecontroller().draw_details(m_gfx_buffer);
		} catch(Exception e) { Error.getError().addError("PASDraw","Exception in draw_layers",e,1); }
		try {
			if(get_pas().get_eastcontent().get_taspanel()!=null)
			{
				get_pas().get_eastcontent().get_taspanel().drawItems((Graphics2D)m_gfx_buffer);
				get_pas().get_eastcontent().get_taspanel().drawLog((Graphics2D)m_gfx_buffer);
			}
		} catch(Exception e) { 
			e.printStackTrace();
		}
		super.draw_layers();
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
		if(get_pas().get_gpscontroller()!=null)
			get_pas().get_gpscontroller().check_needupdate();
		if(get_pas().get_statuscontroller()!=null)
			get_pas().get_statuscontroller().check_needupdate();
		super.checkforupdates();
		//if(m_checkforupdates_thread!=null)
		//	if(m_checkforupdates_thread.isAlive())
		//		return;
		m_checkforupdates_thread = new UpdateThread(1000); //check every one second
	}
	
	class UpdateThread extends Thread implements Runnable {
		int m_n_msec;
		UpdateThread(int ms) {
			super("PASDraw.Update Thread");
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