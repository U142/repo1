package no.ums.adminui.pas;

import com.google.common.collect.ImmutableList;
import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.map.tiled.*;
import no.ums.map.tiled.component.MapComponent;
import no.ums.pas.Draw;
import no.ums.pas.core.Variables;
import no.ums.pas.core.dataexchange.HTTPReq;
import no.ums.pas.core.logon.Settings;
import no.ums.pas.core.logon.Settings.MAPSERVER;
import no.ums.pas.core.ws.vars;
import no.ums.pas.maps.defines.PolygonStruct;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;

public class MapImageDownload extends JApplet {

    private static final Log log = UmsLog.getLogger(MapImageDownload.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String lat,lon;
	private PolygonStruct shape;
    public MapComponent mapComponent;

    private int applet_width;
    private int applet_height;

	public void init() {

        applet_height = Integer.parseInt(getParameter("applet_height"));
        applet_width = Integer.parseInt(getParameter("applet_width"));

        Variables.setDraw(new Draw(this, Thread.NORM_PRIORITY, applet_width, applet_height));
        Variables.setMapFrame(new MapFrameAdmin(applet_width, applet_height, Variables.getDraw(), Variables.getNavigation(), new HTTPReq("http://vb4utv"), true));

        Settings m_settings = new Settings();
        vars.init(getParameter("w"));
        String OVERRIDE_WMS_SITE = getParameter("mapinfo");

        if(OVERRIDE_WMS_SITE.toLowerCase().equals("default"))
        {
            m_settings.setMapServer(MAPSERVER.DEFAULT);
        }
        else
        {
            String [] arr = OVERRIDE_WMS_SITE.split(";");
            if(arr!=null && arr.length>=3)
            {
                m_settings.setMapServer(MAPSERVER.WMS);

                m_settings.setWmsSite(arr[0]);
                m_settings.setSelectedWmsFormat(arr[1]);
                m_settings.setSelectedWmsLayers(arr[2]);

                if(arr.length>=4)
                    m_settings.setWmsEpsg(arr[3]);
                else
                    m_settings.setWmsEpsg("4326"); //default to lon/lat WGS84
                if(arr.length>=5)
                    m_settings.setWmsUsername(arr[4]);
                else
                    m_settings.setWmsUsername("");
                if(arr.length>=6)
                    m_settings.setWmsPassword(arr[5]);
                else
                    m_settings.setWmsPassword("");
            }
        }
        Variables.setSettings(m_settings);

        lat = getParameter("lat");
        lon = getParameter("lon");
        log.debug("lat: " + lat);
        log.debug("lon: " + lon);

        String[] clat = lat.split("\\|");
        String[] clon = lon.split("\\|");

        List<LonLat> shape = addLonLatToShape(clon,clat);
        LonLat[] bounds = getBounds(shape);

        mapComponent = new MapComponent();
        mapComponent.setPreferredSize(new Dimension(applet_width,applet_height));
        final TileCacheOsm osmTileCache = new TileCacheOsm(TileCacheOsm.Layer.MAPNIK);
        mapComponent.setTileLookup(new TileLookupImpl(osmTileCache));

        final TileLookup.BoundsMatch tileLookup = mapComponent.getTileLookup().getBestMatch(bounds[0], bounds[1], new Dimension(applet_width,applet_height));
        Variables.setZoomLevel(tileLookup.getZoom());

        mapComponent.getModel().setTopLeft(bounds[0]);
        mapComponent.getModel().setZoom(tileLookup.getZoom());
        
        mapComponent.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(final MouseEvent e) {
                final MapComponent map = (MapComponent) e.getComponent();
                map.setSize(applet_width, applet_height);
                final ZoomLookup zoomLookup = map.getTileLookup().getZoomLookup(tileLookup.getZoom());
                final LonLat ll = zoomLookup.getLonLat(map.getModel().getTopLeft(), e.getX(), e.getY());
                final TileLookup tileLookup = map.getTileLookup();
            }
        });
        mapComponent.addLayer(new MapComponent.DrawingLayer(mapComponent));

        ZoomLookup zoomLookup = mapComponent.getTileLookup().getZoomLookup(Variables.getZoomLevel());

        Path2D.Double path = mapComponent.convertLonLatToPath2D(shape, zoomLookup);
        mapComponent.getDrawlayLayer().setShape(shape);
        mapComponent.getDrawlayLayer().setPath(path);

        getContentPane().add(mapComponent);
        //setVisible(false);

        mapComponent.repaint();
        mapComponent.getDrawlayLayer().recalculate();

        Thread thread = new WaitThread(this, mapComponent.getDrawlayLayer());
        thread.start();

	}
	
    private LonLat[] getBounds(List<LonLat> shape) {
        LonLat topLeft = new LonLat(180,-90), bottomRight = new LonLat(-180,90);
        
        for(LonLat lonLat: shape) {
            if(topLeft.getLon() > lonLat.getLon()) {
                topLeft = new LonLat(lonLat.getLon(), topLeft.getLat());
            }
            if(topLeft.getLat() < lonLat.getLat()) {
                topLeft = new LonLat(topLeft.getLon(), lonLat.getLat());
            }
            if(bottomRight.getLon() < lonLat.getLon()) {
                bottomRight = new LonLat(lonLat.getLon(), bottomRight.getLat());
            }
            if(bottomRight.getLat() > lonLat.getLat()) {
                bottomRight = new LonLat(bottomRight.getLon(), lonLat.getLat());
            }
        }
        
        return new LonLat[] { topLeft, bottomRight };
    }
    
	public void paint(Graphics g) {
		super.paint(g);
	}
    
    public void saveImage() {
        Variables.getMapFrame().save_map(mapComponent.getDrawlayLayer().getBufferedImage(applet_width, applet_height));
        System.exit(0);
    }
    
    public boolean mapDownloaded(MapComponent mapComponent) {
        ImmutableList<TileData> tileDataList = mapComponent.getTileLookup().getTileInfo(Variables.getZoomLevel(),
                mapComponent.getModel().getTopLeft(), new Dimension(applet_width,applet_height)).getTileData();

        boolean downloaded = true;

        for(TileData tileData: tileDataList) {
            if(!mapComponent.getTileLookup().exists(tileData)) {
                downloaded = false;
            }
        }

        if(downloaded) {
            mapComponent.repaint();
        }
        
        return downloaded;
    }
    
    private List<LonLat> addLonLatToShape(String[] lat, String[] lon) {
        List<LonLat> lonLatList = new ArrayList<LonLat>();
        
        for(int i=0;i<lat.length;i++) {
            lonLatList.add(new LonLat(Double.parseDouble(lat[i].replace(',','.')),Double.parseDouble(lon[i].replace(',','.'))));
        }
        return lonLatList;
    }
    
    private class WaitThread extends Thread {

        MapComponent.DrawingLayer layer;
        MapImageDownload mapImageDownload;

        public WaitThread(MapImageDownload mapImageDownload, MapComponent.DrawingLayer layer) {
            this.layer = layer;
            this.mapImageDownload = mapImageDownload;
        }
        @Override
        public void run() {
            while(!layer.isDoneLoading() || !mapDownloaded(mapComponent)) {
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
            saveImage();
        }
    }
}
