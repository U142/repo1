package no.ums.adminui.pas;

import java.awt.Cursor;
import java.awt.Graphics;

import no.ums.pas.core.variables;
import no.ums.pas.core.dataexchange.HTTPReq;
import no.ums.pas.core.logon.Settings.MAPSERVER;
import no.ums.pas.maps.defines.Navigation;
import no.ums.pas.maps.defines.ShapeStruct;
import no.ums.pas.Draw;
import no.ums.pas.PAS;
import no.ums.pas.maps.*;
import no.ums.pas.maps.defines.*;
import no.ums.pas.ums.errorhandling.Error;

public class MapFrameAdmin extends MapFrame {

	private Graphics m_gfx;
	public Graphics get_gfx() { return m_gfx; }
	
	public MapFrameAdmin(int n_width, int n_height, Draw drawthread,
			Navigation nav, HTTPReq http, boolean b_enable_snap) {
		super(n_width, n_height, drawthread, nav, http, b_enable_snap);
	}
	public synchronized void load_map(boolean b_threaded) {
		//ensures that nothing is drawn until this new map is ready
		if(b_threaded)
		{
			
			Thread th = new Thread() {
				
				public void run()
				{
					//b_loading_in_progress = true;
					SetIsLoading(true, PAS.l("common_loading") + " " + PAS.l("common_map"));
					kickRepaint();
					load_map();
					SetIsLoading(false, "");
					/*while(get_mapimage()==null)
					{
						try
						{
							Thread.sleep(10);
						}
						catch(Exception e) { }
					}*/
					kickRepaint();
				}
			};
			th.setPriority(Thread.NORM_PRIORITY);
			th.start();
		} else
			load_map();
	}
	
	public void load_map() {
		//m_img_loading.flush();

		if(PAS.get_pas() != null)
			PAS.get_pas().get_mainmenu().enableUglandPortrayal((PAS.get_pas().get_settings().getMapServer()==MAPSERVER.DEFAULT ? true : false));
		if(m_maploader.IsLoadingMapImage())
			return;
		setAllOverlaysDirty();
		set_cursor(new Cursor(Cursor.WAIT_CURSOR));
		get_drawthread().set_need_imageupdate();
		//m_img_onscreen = null;
		try {
			switch(variables.SETTINGS.getMapServer())
			{
			case DEFAULT:
				m_img_loading = m_maploader.load_map(get_navigation().getNavLBO(), get_navigation().getNavRBO(), get_navigation().getNavUBO(), get_navigation().getNavBBO(), this.getSize(), get_mapsite(), get_mapportrayal());
				break;
			case WMS:
				m_img_loading = m_maploader.load_map_wms(get_navigation().getNavLBO(), get_navigation().getNavRBO(), get_navigation().getNavUBO(), get_navigation().getNavBBO(), this.getSize(), variables.SETTINGS.getWmsSite());
			}
			
			//m_img_loading = wmsimg;
			if(m_img_loading==null)
			{
			}
			
			for(int i=0;i<getOverlays().size();++i) {
				/*if(PAS.get_pas().get_eastcontent().get_statuspanel().get_chk_layers_gsm().isSelected())
					showAllOverlays(1, true);
				else
					showAllOverlays(1, false);
				
				if(PAS.get_pas().get_eastcontent().get_statuspanel().get_chk_layers_umts().isSelected())
					showAllOverlays(4, true);
				else
					showAllOverlays(4, false);
					*/
				start_gsm_coverage_loader();
			}
		} catch(Exception e) {
			//System.out.println("Error loading map " + e.getMessage());
			//Error.getError().addError("MapFrame","Exception in load_map",e,1);
		}
		try
		{
			if(m_img_onscreen != null)
				m_img_onscreen.flush();
		}
		catch(Exception e)
		{
		
		}
		m_img_onscreen= m_img_loading;
		/*m_img_onscreen = new BufferedImage(m_img_loading.getWidth(null), m_img_loading.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		Graphics tempg = m_img_onscreen.createGraphics();
		tempg.drawImage(m_img_loading, 0, 0, null);
		tempg.dispose();*/
		
		//PAS.get_pas().prepareImage(m_img_onscreen, PAS.get_pas().get_drawthread());
		try {
			prepareImage(m_img_onscreen, null); //get_drawthread());
		} catch(Exception e) {
			//System.out.println("prepareImage " + e.getMessage());
			//Error.getError().addError("MapFrame","Exception in load_map",e,1);
		}
		try {
			set_cursor(m_current_cursor);
		} catch(Exception e) {
			//Error.getError().addError("MapFrame","Exception in load_map",e,1);
		}
		//System.out.println("KICKREPAINT");
		kickRepaint();
		
	}
	@Override
	public void set_mode(int n_mode)
	{
		//let settings decide
		if(n_mode==MAP_MODE_PAN || n_mode==MAP_MODE_PAN_BY_DRAG)
		{
			if(PAS.get_pas() != null && PAS.get_pas().get_settings().getPanByDrag())
				n_mode = MAP_MODE_PAN_BY_DRAG;
			else
				n_mode = MAP_MODE_PAN;
		}
		switch(n_mode) {
			case MAP_MODE_PAN:
				set_cursor(new Cursor(Cursor.HAND_CURSOR));
				m_n_prev_mode = m_n_current_mode;
				if(PAS.get_pas() != null)
					PAS.get_pas().get_mainmenu().clickMapMode(n_mode, true);
				break;
			case MAP_MODE_PAN_BY_DRAG:
				set_cursor(new Cursor(Cursor.MOVE_CURSOR));
				m_n_prev_mode = m_n_current_mode;
				if(PAS.get_pas() != null)
					PAS.get_pas().get_mainmenu().clickMapMode(n_mode, true);
				break;
			case MAP_MODE_ZOOM:
				set_cursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
				m_n_prev_mode = m_n_current_mode;
				if(PAS.get_pas() != null)
					PAS.get_pas().get_mainmenu().clickMapMode(n_mode, true);
				break;
			case MAP_MODE_HOUSESELECT:
				set_cursor(new Cursor(Cursor.DEFAULT_CURSOR));
				m_n_prev_mode = m_n_current_mode;
				break;
			case MAP_MODE_SENDING_POLY:
			case MAP_MODE_PAINT_RESTRICTIONAREA:
				try {
					//setCursor(PAS.get_pas().get_mainmenu().get_cursor_draw());
					//setCursor(get_cursor_draw());
					if(get_active_shape() == null)
						set_active_shape(new PolygonStruct(variables.NAVIGATION.getDimension()));
					set_cursor(get_cursor_draw());
				} catch(Exception e) {
					Error.getError().addError("MapFrame","Exception in set_mode",e,1);
				}
				break;
			case MAP_MODE_OBJECT_MOVE:
				try {
					get_current_object().setMoving(true);
					//setCursor(new Cursor(Cursor.MOVE_CURSOR));
					set_cursor(new Cursor(Cursor.MOVE_CURSOR));
				} catch(Exception e) {
					
				}
				break;
			case MAP_MODE_HOUSEEDITOR_:
				//setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
				//set_submode(m_n_current_submode);
				//new Core.MainUI.HouseEditorDlg(null);
				m_n_prev_mode = m_n_current_mode;
				break;
			case MAP_MODE_SENDING_ELLIPSE:
			case MAP_MODE_SENDING_ELLIPSE_POLYGON:
				//setCursor(new Cursor(Cursor.SE_RESIZE_CURSOR));
				set_cursor(new Cursor(Cursor.SE_RESIZE_CURSOR));
				break;
			case MAP_MODE_ASSIGN_EPICENTRE:
				//setCursor(get_cursor_epicentre());
				set_cursor(get_cursor_epicentre());
				break;
		}
		m_n_current_mode = n_mode;

	}
	
	@Override
	public void paintComponent(Graphics g) {
		try
		{
			//super.paint(g);
			//get_drawthread().set_neednewcoors(true);
			get_drawthread().create_image();
			if(get_mode()==MAP_MODE_PAN_BY_DRAG && get_actionhandler().get_isdragging())
			{
				{
				}
			}
			g.drawImage(get_drawthread().get_buff_image(), get_actionhandler().getPanDragPoint().get_x(), get_actionhandler().getPanDragPoint().get_y(), getMinimumSize().width, getMinimumSize().height, this);
		}
		catch(Exception e)
		{
			
		}
	}
	@Override
	public void paint(Graphics g)
	{
		super.paint(g);
	}
	
	//skal egentlig tegnes opp i draw_layers i Draw thread.
	//Draw thread har også ansvar for å kjøre calc_coortopix på alle shapes
	@Override
	public void drawOnEvents(Graphics gfx) {
		
		try
		{
			get_active_shape().draw(gfx, variables.NAVIGATION, false, true, false, null, true, true, 1, false);
			m_gfx = gfx;
		}
		catch(Exception e) { }
		super.drawOnEvents(gfx);
	}
}
