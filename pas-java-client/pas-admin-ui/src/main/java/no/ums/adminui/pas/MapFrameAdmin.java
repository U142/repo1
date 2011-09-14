package no.ums.adminui.pas;

//import no.ums.log.Log;
//import no.ums.log.UmsLog;
import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.pas.Draw;
import no.ums.pas.PAS;
import no.ums.pas.core.Variables;
import no.ums.pas.core.dataexchange.HTTPReq;
import no.ums.pas.core.logon.Settings.MAPSERVER;
import no.ums.pas.localization.Localization;
import no.ums.pas.maps.MapFrame;
import no.ums.pas.maps.defines.Navigation;
import no.ums.pas.maps.defines.PolygonStruct;

import java.awt.*;

public class MapFrameAdmin extends MapFrame {

    private static final Log log = UmsLog.getLogger(MapFrameAdmin.class);

    //private static final Log logger = UmsLog.getLogger(MapFrameAdmin.class);
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
					SetIsLoading(true, Localization.l("common_loading") + " " + Localization.l("common_map"));
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

        set_cursor(new Cursor(Cursor.WAIT_CURSOR));
		get_drawthread().set_need_imageupdate();
		//m_img_onscreen = null;
		try {
			switch(Variables.getSettings().getMapServer())
			{
			case DEFAULT:
				m_img_loading = m_maploader.load_map(get_navigation().getNavLBO(), get_navigation().getNavRBO(), get_navigation().getNavUBO(), get_navigation().getNavBBO(), this.getSize(), get_mapsite(), get_mapportrayal());
				break;
			case WMS:
				m_img_loading = m_maploader.load_map_wms(get_navigation().getNavLBO(), get_navigation().getNavRBO(), get_navigation().getNavUBO(), get_navigation().getNavBBO(), this.getSize(), Variables.getSettings().getWmsSite());
			}
			
		} catch(Exception e) {
			//log.debug("Error loading map " + e.getMessage());
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
			//log.debug("prepareImage " + e.getMessage());
			//Error.getError().addError("MapFrame","Exception in load_map",e,1);
		}
		try {
			set_cursor(m_current_cursor);
		} catch(Exception e) {
			//Error.getError().addError("MapFrame","Exception in load_map",e,1);
		}
		//log.debug("KICKREPAINT");
		kickRepaint();
		
	}
	@Override
	public void set_mode(MapMode n_mode)
	{
		//let settings decide
		if(n_mode== MapMode.PAN || n_mode== MapMode.PAN_BY_DRAG)
		{
			if(PAS.get_pas() != null && PAS.get_pas().get_settings().getPanByDrag())
				n_mode = MapMode.PAN_BY_DRAG;
			else
				n_mode = MapMode.PAN;
		}
		switch(n_mode) {
			case PAN:
				set_cursor(new Cursor(Cursor.HAND_CURSOR));
				m_n_prev_mode = m_n_current_mode;
				if(PAS.get_pas() != null)
					PAS.get_pas().get_mainmenu().clickMapMode(n_mode, true);
				break;
			case PAN_BY_DRAG:
				set_cursor(new Cursor(Cursor.MOVE_CURSOR));
				m_n_prev_mode = m_n_current_mode;
				if(PAS.get_pas() != null)
					PAS.get_pas().get_mainmenu().clickMapMode(n_mode, true);
				break;
			case ZOOM:
				set_cursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
				m_n_prev_mode = m_n_current_mode;
				if(PAS.get_pas() != null)
					PAS.get_pas().get_mainmenu().clickMapMode(n_mode, true);
				break;
			case HOUSESELECT:
				set_cursor(new Cursor(Cursor.DEFAULT_CURSOR));
				m_n_prev_mode = m_n_current_mode;
				break;
			case SENDING_POLY:
			case PAINT_RESTRICTIONAREA:
				try {
					//setCursor(PAS.get_pas().get_mainmenu().get_cursor_draw());
					//setCursor(get_cursor_draw());
					if(get_active_shape() == null)
						set_active_shape(new PolygonStruct(Variables.getNavigation().getDimension()));
					set_cursor(get_cursor_draw());
				} catch(Exception e) {
					log.error("MapFrame", "Exception in set_mode", e);
				}
				break;
			case OBJECT_MOVE:
				try {
					get_current_object().setMoving(true);
					//setCursor(new Cursor(Cursor.MOVE_CURSOR));
					set_cursor(new Cursor(Cursor.MOVE_CURSOR));
				} catch(Exception e) {
					
				}
				break;
			case HOUSEEDITOR:
				//setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
				//set_submode(m_n_current_submode);
				//new Core.MainUI.HouseEditorDlg(null);
				m_n_prev_mode = m_n_current_mode;
				break;
			case SENDING_ELLIPSE:
			case SENDING_ELLIPSE_POLYGON:
				//setCursor(new Cursor(Cursor.SE_RESIZE_CURSOR));
				set_cursor(new Cursor(Cursor.SE_RESIZE_CURSOR));
				break;
			case ASSIGN_EPICENTRE:
				//setCursor(get_cursor_epicentre());
				set_cursor(MapMode.ASSIGN_EPICENTRE.getCursor());
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
			g.drawImage(get_drawthread().get_buff_image(), get_actionhandler().getPanDragPoint().get_x(), get_actionhandler().getPanDragPoint().get_y(), getMinimumSize().width, getMinimumSize().height, this);
		} catch(Exception e) {
            //logger.warn("Failed to paint component", e);
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
			get_active_shape().draw(gfx, Variables.getMapFrame().getMapModel(), Variables.getMapFrame().getZoomLookup(), false, true, false, null, true, true, 1, false);
			m_gfx = gfx;
		}
		catch(Exception e) {
            //logger.warn("Failed to draw shape", e);
        }
		super.drawOnEvents(gfx);
	}
}
