package no.ums.adminui.pas;

import no.ums.adminui.pas.ws.WSGetRestrictionShapes;
import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.map.tiled.*;
import no.ums.map.tiled.component.MapComponent;
import no.ums.map.tiled.component.MapTools;
import no.ums.pas.PAS;
import no.ums.pas.core.Variables;
import no.ums.pas.core.logon.DeptInfo;
import no.ums.pas.core.logon.LogonInfo;
import no.ums.pas.core.logon.Settings;
import no.ums.pas.core.logon.Settings.MAPSERVER;
import no.ums.pas.core.logon.UserInfo;
import no.ums.pas.core.ws.vars;
import no.ums.pas.maps.defines.MapPointLL;
import no.ums.pas.maps.defines.Navigation;
import no.ums.pas.maps.defines.PolygonStruct;
import no.ums.pas.maps.defines.ShapeStruct;
import no.ums.pas.send.SendPropertiesPolygon;
import no.ums.ws.common.PASHAPETYPES;
import no.ums.ws.common.ULOGONINFO;
import no.ums.ws.common.parm.ArrayOfUShape;
import no.ums.ws.common.parm.UDEPARTMENT;
import no.ums.ws.common.parm.UPolygon;
import no.ums.ws.common.parm.UPolypoint;
import no.ums.ws.pas.ArrayOfUDEPARTMENT;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class MapApplet extends JApplet implements ActionListener {

    private static final Log log = UmsLog.getLogger(MapApplet.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    private transient ZoomLookup zoomLookup;

    public MapFrameAdmin m_mappane;
    public MapComponent mapComponent;
	public Navigation m_navigation;
	public Image m_image;
	public String coors;
	public SendPropertiesPolygon sp;
	public UserInfo m_info;
	private ULOGONINFO logoninfo;
	private LogonInfo info;
	public static String OVERRIDE_WMS_SITE = null;
	public int applet_width;
	public int applet_height;
    
    public int zoom_level = 7;
    public int max_zoom_level = 12;
	
	public void init() {
		try {
			System.setSecurityManager(null);
		}
		catch(Exception e) {
			
		}
		
	}

	public void start() {
		
		vars.init(getParameter("w"));
		PAS.setLocale("en","GB");

		ULOGONINFO logon = new ULOGONINFO();

		applet_height = Integer.parseInt(getParameter("applet_height"));
		applet_width = Integer.parseInt(getParameter("applet_width"));
		logon.setLDeptpk(Integer.parseInt(getParameter("deptid")));
		logon.setLComppk(Integer.parseInt(getParameter("compid")));
		logon.setLUserpk(Long.parseLong(getParameter("userid")));
		logon.setSzPassword(getParameter("password"));
		logon.setSessionid(getParameter("session"));
		OVERRIDE_WMS_SITE = getParameter("mapinfo");
		
		m_info = new UserInfo(logon.getSzUserid(), logon.getLComppk(),logon.getSzUserid(), logon.getSzCompid(), "", "", logon.getSessionid(), "", -1, "");
		Settings m_settings = new Settings();
        
		if(OVERRIDE_WMS_SITE.toLowerCase().equals("default"))
		{

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
	
		WSGetRestrictionShapes wsGetRestrictionShapes = new WSGetRestrictionShapes(this, "act_logon", logon, PASHAPETYPES.PADEPARTMENTRESTRICTION);
		resize(applet_width,applet_height);
		
		try {
            wsGetRestrictionShapes.run();
		} catch(Exception e) {
            new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "act_logon");
			log.warn(e.getMessage(), e);
		}

	}
	
	private void afterLogon() {
		final Container contentpane = getContentPane();
		contentpane.setLayout(new BorderLayout());

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                contentpane.setSize(applet_width, applet_height);
                contentpane.add(mapComponent, BorderLayout.CENTER);
                contentpane.paint(contentpane.getGraphics());
            }
        });

        setFocusable(true);
        requestFocus();
	}

    @Override
    public void paint(Graphics g) {
    }
    
	@Override
	public void actionPerformed(final ActionEvent e) {
			
		final ActionEvent event = e;
			
		if("act_logon".equals(e.getActionCommand()))
		{
			
			boolean b_results_ready;
			ArrayOfUDEPARTMENT wsdept = (ArrayOfUDEPARTMENT)e.getSource();
			
			List<UDEPARTMENT> depts = wsdept.getUDEPARTMENT();
			for(int i=0; i < depts.size(); i++)
			{
				UDEPARTMENT d = depts.get(i);
				
				m_info.add_department(d.getLDeptpk(), d.getSzDeptid(), d.getSzStdcc(), d.getLbo(), d.getRbo(), 
					d.getUbo(), d.getBbo(), d.isFDefault(), d.getLDeptpri(), d.getLMaxalloc(), 
					d.getSzUserprofilename(), d.getSzUserprofiledesc(), d.getLStatus(), 
					d.getLNewsending(), d.getLParm(), d.getLFleetcontrol(), d.getLLba(), 
					d.getLHouseeditor(), d.getLAddresstypes(), d.getSzDefaultnumber(), 
					d.getMunicipals().getUMunicipalDef(), d.getLPas(), d.getLLangpk(), d.getFSms(), 
					d.getRestrictionShapes(),
					d.getLMessagelib(),
					d.getLPhonebook(),
					d.getLLists(),
					d.getLModules(),
					d.getLProfiles(),
					d.getLPhonebookShare(),
					d.getLListsShare(),
					d.getLModulesShare(),
					d.getLProfilesShare(),
					d.getLPbName(),
					d.getLPbPhone(),
					d.getLPbPin(),
					d.getLPbLocality(),
					d.getLLocality(),
					d.getLSched(),
					d.getLSchedRetry(),
					d.getLSchedCancel(),
					d.getLSchedPause(),
					d.getLSchedShare(),
					d.getLMessagelibShare(),
					d.getFSuperuser(),
					d.getLResource(),
					d.getLEmail());
			}


			m_info.get_departments().CreateCombinedRestrictionShape();
			
			Variables.setUserInfo(m_info);
            mapComponent = new MapComponent();
            mapComponent.setPreferredSize(new Dimension(applet_width, applet_height));

            // Sets the map somewhere just to be able to calculate restriction area
            mapComponent.getModel().setTopLeft(new LonLat(2.180480787158013,54.76231045722961));
            mapComponent.getModel().setZoom(Variables.getZoomLevel());

            mapComponent.getMapController().setZoomLevel(zoom_level);
            mapComponent.getMapController().setMaxZoomLevel(max_zoom_level);

            String scheme="", host="", path="";
            final URI base = URI.create(Variables.getSettings().getWmsSite());
            try {
                scheme = base.getScheme();
                host = base.getHost();
                path = base.getPath();
            } catch (Exception ex) {
                log.warn("Failed to fetch WMS version", ex);
            }

            final TileCacheWms wmsTileCache = new TileCacheWms(scheme,host,path,"1.1.1",Variables.getSettings().getSelectedWmsFormat(),Integer.valueOf(Variables.getSettings().getWmsEpsg()),Variables.getSettings().getSelectedWmsLayers());
            //final TileCacheOsm osmTileCache = new TileCacheOsm(TileCacheOsm.Layer.MAPNIK);
            mapComponent.setTileLookup(new TileLookupImpl(wmsTileCache));

            zoomLookup = mapComponent.getTileLookup().getZoomLookup(Variables.getZoomLevel());

            Path2D.Double restrictionShapesPath = null;
            List<LonLat> lonLats = null;
            LonLat[] bounds = new LonLat[2];
            if(m_info.get_departments().get_combined_restriction_shape() != null) {
                List<ShapeStruct> list = m_info.get_departments().get_combined_restriction_shape();
                lonLats = convertCombinedRestrictionShapeCoordinatesToLonLat(list);
                restrictionShapesPath = mapComponent.convertLonLatToPath2D(lonLats, zoomLookup);

                if(MapTools.getNumberOfPathPoints(restrictionShapesPath) == 0) {
                    bounds[0] = new LonLat(5.180480787158013, 51.76231045722961);
                    bounds[1] = new LonLat(5.680480787158013, 51.96231045722961);
                }
                else {
                    bounds = mapComponent.getBounds(lonLats);
                }
            }



            mapComponent.getModel().setTopLeft(bounds[0]);
            final TileLookup.BoundsMatch tileLookup = mapComponent.getTileLookup().getBestMatch(bounds[0], bounds[1], new Dimension(applet_width,applet_height),max_zoom_level);
            Variables.setZoomLevel(tileLookup.getZoom());

            // Sets bounds based on restriction area if no area then set default

            mapComponent.getModel().setZoom(tileLookup.getZoom());

            //mapComponent.getModel().setTopLeft(new LonLat(2.180480787158013,54.76231045722961));
            //mapComponent.getModel().setZoom(Variables.getZoomLevel());



            mapComponent.addMouseMotionListener(new MouseAdapter() {
                @Override
                public void mouseMoved(final MouseEvent e) {
                    final MapComponent map = (MapComponent) e.getComponent();
                    map.setSize(applet_width,applet_height);
                    final ZoomLookup zoomLookup = map.getTileLookup().getZoomLookup(map.getModel().getZoom());
                    final LonLat ll = zoomLookup.getLonLat(map.getModel().getTopLeft(), e.getX(), e.getY());
                    final TileLookup tileLookup = map.getTileLookup();
                    //final TileInfo tileInfo = tileLookup.getTileInfo(map.getModel().getZoom(), map.getModel().getTopLeft(), new Dimension(applet_width,applet_height));
                }
            });



            MapComponent.DrawingLayer drawingLayer = new MapComponent.DrawingLayer(mapComponent);

            mapComponent.addLayer(drawingLayer);
            
            mapComponent.addLayer(new MapComponent.RestrictionLayer(mapComponent, restrictionShapesPath, lonLats));


            for(DeptInfo dept: m_info.get_departments()) {
                for(ShapeStruct shape: dept.get_restriction_shapes()) {
                    if(!shape.isObsolete()) {
                        List<ShapeStruct> tmpShape = new ArrayList<ShapeStruct>();
                        tmpShape.add(shape);
                        List<LonLat> tmplls = convertCombinedRestrictionShapeCoordinatesToLonLat(tmpShape);
                        Path2D.Double tmpPath = mapComponent.convertLonLatToPath2D(tmplls, zoomLookup);
                        shape.calc_bounds();
                        MapPointLL ll = shape.getCenter();

                        mapComponent.addLayer(new MapComponent.IndividualRestriction(mapComponent,tmpPath, tmplls,
                                shape.shapeName, new LonLat(ll.get_lon(),ll.get_lat())));
                    }
                }
            }

			add(mapComponent);
			b_results_ready = true;
			afterLogon();
		}
			
    }

    private List<LonLat> convertCombinedRestrictionShapeCoordinatesToLonLat(List<ShapeStruct> combinedRestrictionShape) {
        List<LonLat> lonLats = new ArrayList<LonLat>();
        
        if(combinedRestrictionShape.get(0) instanceof PolygonStruct) {
            PolygonStruct polygonStruct = (PolygonStruct) combinedRestrictionShape.get(0);
            polygonStruct.POINT_PRECISION = 10000.0;//Double.MAX_VALUE;

            for(int i=0;i<polygonStruct.get_coors_lat().size();i++) {

                lonLats.add(new LonLat(polygonStruct.get_coors_lon().get(i),polygonStruct.get_coors_lat().get(i)));
            }
        }
        return lonLats;
    }

    public void clear() {
        List<LonLat> shape = new ArrayList<LonLat>();
        mapComponent.getDrawLayer().setShape(shape);
        mapComponent.getDrawLayer().setPath(mapComponent.convertLonLatToPath2D(shape,zoomLookup));
        mapComponent.getDrawLayer().recalculate();
        mapComponent.repaint();
    }

    public void put(String id) {
		PolygonStruct s = null;
        List<LonLat> shape;

		if(!id.equals("-1")) {
            for(int i=0;i< Variables.getUserInfo().get_departments().size();++i){
                DeptInfo deptinfo = (DeptInfo)m_info.get_departments().get(i);
                if(deptinfo.get_deptpk() == Integer.parseInt(id)) {
                    List<ShapeStruct> rshapes = deptinfo.get_restriction_shapes();
                    s = rshapes.get(0).typecast_polygon();
                    break;
                }
            }
            if(s != null) {
                shape = mapComponent.addLonLatToShape(s.get_coors_lon(), s.get_coors_lat());
            }
            else {
                shape = new ArrayList<LonLat>();
            }
        }
        else {
            shape = new ArrayList<LonLat>();
        }
        // Coordinates are the wrong way round

        
        mapComponent.getDrawLayer().setShape(shape);
        mapComponent.getDrawLayer().setPath(mapComponent.convertLonLatToPath2D(shape,zoomLookup));
        mapComponent.getDrawLayer().recalculate();
        mapComponent.repaint();
	}
	public void store(String name) {
		Variables.getMapFrame().get_active_shape();
		UPolygon shape = new UPolygon();
		PolygonStruct polygon = Variables.getMapFrame().get_active_shape().typecast_polygon();
		shape.setColAlpha(polygon.get_fill_color().getAlpha());
		shape.setColBlue(polygon.get_fill_color().getBlue());
		shape.setColGreen(polygon.get_fill_color().getGreen());
		shape.setColRed(polygon.get_fill_color().getRed());
		UPolypoint p;
		for(int i=0;i<polygon.get_coors_lat().size();++i) {
			p = new UPolypoint();
			p.setLat(polygon.get_coors_lat().get(i));
			p.setLon(polygon.get_coors_lon().get(i));
			shape.getPolypoint().add(p);
		}
		ArrayOfUShape arrShape = new ArrayOfUShape();
		arrShape.getUShape().add(shape);
		Variables.getUserInfo().get_current_department().get_restriction_shapes();
		Variables.getUserInfo().add_department(Variables.getUserInfo().get_default_dept().get_deptpk(),
                name, Variables.getUserInfo().get_default_dept().get_defaultnumber(),
                (float) Variables.getUserInfo().get_default_dept().get_nav_init()._lbo,
                (float) Variables.getUserInfo().get_default_dept().get_nav_init()._rbo,
                (float) Variables.getUserInfo().get_default_dept().get_nav_init()._ubo,
                (float) Variables.getUserInfo().get_default_dept().get_nav_init()._bbo,
                false, 3, 150, "NLAlertAdmin", "", 1, 1, 1, 1, 1, 1, (long) 1, "", null, 2, 1, 0, arrShape,
                0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0);
		
		
		Variables.getUserInfo().get_current_department().CalcCoorRestrictionShapes();
	}
	public void generateRestrictionShape(String[] coors) {
	
	}
	private PolygonStruct parseCoors(String coors){
		String[] l = coors.split("€");
		
		PolygonStruct s = new PolygonStruct(new Dimension(m_mappane.get_dimension().width,m_mappane.get_dimension().height));
		//PolygonStruct s = new PolygonStruct(new Dimension(640,480));
		
		String[] clat = l[0].split("\\|");
		String[] clon = l[1].split("\\|");
		
		for(int i=0;i<clat.length;++i) {
			s.add_coor(Double.parseDouble(clon[i].replace(',', '.')),Double.parseDouble(clat[i].replace(',', '.')));
		}
		return s;
	}

    public void addRestriction(String coors) {
		PolygonStruct s = parseCoors(coors);
		m_info.get_departments().m_combined_shapestruct_list.add(s);
		m_info.get_departments().CreateCombinedRestrictionShape();
		//set restriction shape
	}
    public void requestFocus() {
        mapComponent.requestFocus();
    }

	public void draw() {
		mapComponent.getDrawLayer().setDrawingActivated(true);
	}
	public String get(){

        String lat = "";
        String lon = "";

        try {
            List<LonLat> shape = mapComponent.getDrawLayer().getShape();
            
            PolygonStruct p = new PolygonStruct(ShapeStruct.DETAILMODE.SHOW_POLYGON_FULL,10000.0);
            
            for( LonLat lonlat : shape) {
                p.add_coor(lonlat.getLon(),lonlat.getLat(),10000.0);
            }

            for(int i=0;i<p.get_coors_lon().size();++i) {
                lat += String.valueOf(p.get_coors_lat().get(i));
                if(i+1<p.get_coors_lat().size())
                    lat += "|";

                lon += String.valueOf(p.get_coors_lon().get(i));
                if(i+1<p.get_coors_lon().size())
                    lon += "|";
            }
            /*
            for(int i=0;i<shape.size();++i) {
                lat += String.valueOf(shape.get(i).getLat());
                if(i+1<shape.size())
                    lat += "|";

                lon += String.valueOf(shape.get(i).getLon());
                if(i+1<shape.size())
                    lon += "|";
            }*/
        } catch (Exception e) {
            System.out.println("Exception adding coordinates: " + e.getMessage());
        }

		return lat + "¤" + lon;
	}
	
	public boolean logon(String userid, String company, String password) {
		return true;
	}   
}
/*
class PutThread extends Thread {
    MapApplet m;
    
    PutThread(MapApplet m) {
        this.m = m;
    }
    
    @Override
    public void run() {
        try {
            sleep(15000);
            m.clear();
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}*/