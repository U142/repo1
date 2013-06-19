package no.ums.adminui.pas;

//import no.ums.log.Log;
//import no.ums.log.UmsLog;
import com.google.common.base.Joiner;
import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.map.tiled.AbstractTileCacheWms;
import no.ums.map.tiled.TileCacheFleximap;
import no.ums.map.tiled.TileLookup;
import no.ums.map.tiled.TileLookupImpl;
import no.ums.pas.Draw;
import no.ums.pas.PAS;
import no.ums.pas.core.Variables;
import no.ums.pas.core.dataexchange.HTTPReq;
import no.ums.pas.core.logon.Settings.MAPSERVER;
import no.ums.pas.localization.Localization;
import no.ums.pas.maps.MapFrame;
import no.ums.pas.maps.defines.Navigation;
import no.ums.pas.maps.defines.PolygonStruct;
import org.geotools.data.wms.WebMapServer;
import org.geotools.ows.ServiceException;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URL;

public class MapFrameAdmin extends MapFrame {

    private static final Log log = UmsLog.getLogger(MapFrameAdmin.class);

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
					setIsLoading(true, Localization.l("common_loading") + " " + Localization.l("common_map"));
					kickRepaint();
					load_map();
					setIsLoading(false, "");
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
		if(m_maploader.isLoadingMapImage())
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

		try {
			prepareImage(m_img_onscreen, null); //get_drawthread());
		} catch(Exception e) {
			log.debug("prepareImage " + e.getMessage());
		}
		try {
			set_cursor(m_current_cursor);
		} catch(Exception e) {
            log.debug("prepareImage " + e.getMessage());
		}

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

    private final transient TileLookup defaultLookup = new TileLookupImpl(new TileCacheFleximap());
    private final transient TileLookup wmsLookup = new TileLookupImpl(new AbstractTileCacheWms() {
        private String lastLookup = null;
        private String scheme;
        private String host;
        private String path;
        private String version;
        private String wmsUser;
        private String wmsPassword;



        private void update() {
            final String wmsSite = Variables.getSettings().getWmsSite();
            if (lastLookup == null || !lastLookup.equals(wmsSite)) {
                try {
                    final URI base = URI.create(wmsSite);
                    scheme = base.getScheme();
                    host = base.getHost();
                    path = base.getPath();
                    wmsUser = Variables.getSettings().getWmsUsername();
                    wmsPassword = Variables.getSettings().getWmsPassword();

                    m_maploader.setWmsAuthenticator(Variables.getSettings().getWmsUsername(), Variables.getSettings().getWmsPassword().toCharArray());
                    WebMapServer wms = new WebMapServer(new URL(wmsSite));
                    version = wms.getCapabilities().getVersion();
                    lastLookup = wmsSite;
                } catch (IOException e) {
                    version = "1.1.1";
                    log.warn("Failed to fetch WMS version", e);
                } catch (ServiceException e) {
                    log.warn("Failed to fetch WMS version", e);
                }
            }
        }

        @Override
        public String getScheme() {
            update();
            return scheme;
        }

        @Override
        public String getHost() {
            update();
            return host;
        }

        @Override
        public String getPath() {
            update();
            return path;
        }

        @Override
        public String getVersion() {
            update();
            return version;
        }

        @Override
        public String getFormat() {
            return Variables.getSettings().getSelectedWmsFormat();
        }

        @Override
        public String getLayers() {
            return Joiner.on(",").join(Variables.getSettings().getSelectedWmsLayers());
        }

        @Override
        public String getWmsUser() {
            return wmsUser;
        }

        @Override
        public String getWmsPassword() {
            return wmsPassword;
        }

        @Override
		public int getSrs() {
			int srs = Integer.parseInt(Variables.getSettings().getWmsEpsg());
			if(srs<=0)
				srs = 4326;
			return srs;
		}
    });

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
