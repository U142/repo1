package no.ums.pas;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.event.*;

import no.ums.pas.core.logon.UserInfo;
import no.ums.pas.core.storage.*;
import no.ums.pas.maps.defines.CommonFunc;
import no.ums.pas.maps.defines.GISShape;
import no.ums.pas.maps.defines.Navigation;
import no.ums.pas.maps.defines.PolySnapStruct;
import no.ums.pas.maps.defines.ShapeStruct;
import no.ums.pas.parm.constants.ParmConstants;
import no.ums.pas.parm.main.*;
import no.ums.pas.parm.voobjects.*;
import no.ums.pas.ums.errorhandling.Error;


public class ParmController extends MainController {
	
	private ParmPanel m_parmpanel;
	public ParmPanel get_parmpanel() { return m_parmpanel; }
	/*protected PolygonStruct m_polygon;
	protected ArrayList m_arr_polygons = new ArrayList();
	public ArrayList get_polygonlist() { return m_arr_polygons; }
	public PolygonStruct get_polygon() { return m_polygon; }*/
	protected ShapeStruct m_shape;
	protected ShapeStruct m_shape_filled;
	protected ArrayList<ShapeStruct> m_arr_shapes = new ArrayList<ShapeStruct>();
	public ArrayList<ShapeStruct> get_shapelist() { return m_arr_shapes; }
	public ShapeStruct get_shape() { return m_shape; }
	public ShapeStruct get_shape_filled() { return m_shape_filled; }
	
	public ParmController(String sz_sitename, UserInfo userinfo) {
		super(sz_sitename, userinfo);
		m_parmpanel = new ParmPanel(this);
		m_shape   = null;//new PolygonStruct(PAS.get_pas().get_navigation(), PAS.get_pas().get_mapsize());
	}
	
	public void createGUI() {
		System.out.println(StorageController.StorageElements.get_path(StorageController.PATH_PARM_).concat(String.valueOf(PAS.get_pas().get_userinfo().get_current_department().get_deptpk()) + "\\"));
		try {
			StorageController.StorageElements.create_path(StorageController.StorageElements.get_path(StorageController.PATH_PARM_).concat(String.valueOf(PAS.get_pas().get_userinfo().get_current_department().get_deptpk()) + "\\"));
		} catch(IOException e) {
			Error.getError().addError("ParmController","IOException in StorageController.create_path",e,1);
		}
		new ParmConstants(PAS.get_pas().get_sitename(), StorageController.StorageElements.get_path(StorageController.PATH_PARM_).concat(String.valueOf(PAS.get_pas().get_userinfo().get_current_department().get_deptpk()) + "\\"));
//		 Kjører denne testen for å se om programmet ble avsluttet riktig, dersom filen eksisterer var det noe feil
		File cleanExit = new File(ParmConstants.cleanExit);
		if(cleanExit.exists()) {
			File dir = new File(ParmConstants.homePath);
			if(dir.isDirectory()){
				String[] children = dir.list();
				for(int i=0;i<children.length;i++){
					(new File(dir,children[i])).delete();
				}
			}
		}
		try {
			cleanExit.createNewFile();
		} catch(IOException e) {
			Error.getError().addError("ParmController","IOException in createGUI",e,Error.SEVERITY_ERROR);
		}
		initGUI();
		checkRightsManagement();
	}
	
	public void mapClear() { //overridden
		clearDrawQueue();
	}
	protected void showAlertShape(EventVO e) { //to be overridden
		System.out.println("Event shapes " + e.getAlertListe().size());
		Iterator it = e.getAlertListe().iterator();
		//clearDrawQueue();
		while(it.hasNext()){
			AlertVO a = (AlertVO)it.next();
			ShapeStruct s = a.getM_shape();
			
			try {
				addShapeToDrawQueue(s);
			} catch(Exception err) {
				
			}
		}
		mapRedraw();

	}
	public Navigation getMapNavigation() {
		//return null;
		return PAS.get_pas().get_navigation();
	}
	public Dimension getMapSize() {
		//return null;
		return PAS.get_pas().get_mapsize();
	}
//	 Hvorfor satte du denne til protected? Jeg forandrer den til public
	public void clearDrawQueue() {
		get_shapelist().clear();
	}
	protected void drawLayers() {
		
	}
	public void drawLayers(Graphics g) {
		long n_start = System.currentTimeMillis();
		try {
			Iterator it = get_shapelist().iterator();
			while(it.hasNext()) {
				ShapeStruct s = (ShapeStruct)it.next();
				if(s!=null) {
					try {
						if(!s.equals(get_shape())) {
							if(s!=null)
							{
								if(getMapNavigation().bboxOverlap(s.getFullBBox()))
									s.draw(g, getMapNavigation(), true, true, false, null);
							}
						}
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
		} catch(Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			Error.getError().addError("ParmController","Exception in drawLayers",e,1);
		}
		// Bruker updatePolygon for å sette til editerbar, funket ikke helt
		if(get_shape()!=null) {
			get_shape().draw(g, getMapNavigation(), false, false, true, PAS.get_pas().get_mappane().get_current_mousepos());
		}
		if(get_shape_filled()!=null & get_shape()==null) {
			get_shape_filled().draw(g, getMapNavigation(), false, false, false, PAS.get_pas().get_mappane().get_current_mousepos());
		}
		long n_stop = System.currentTimeMillis();
		//System.out.println("drawLayers in " + (n_stop-n_start) + "msecs");
			
	}
	
	public void calc_coortopix() {
		Iterator it = get_shapelist().iterator();
		while(it.hasNext()) {
			ShapeStruct p = (ShapeStruct)it.next();
			if(p!=null) {
				//if(!p.equals(get_shape())){
					try {
						p.calc_coortopix(getMapNavigation());
					} catch(Exception e) {
						System.out.println(e.getMessage());
						e.printStackTrace();
						Error.getError().addError("ParmController","Exception in calc_coortopix",e,1);
					}
				//}
			}
		}
		// Bruker updatePolygon for å sette til editerbar, funket ikke helt
		if(get_shape()!=null) {
			get_shape().calc_coortopix(getMapNavigation());
		}
		
	}
	public PolySnapStruct snap_to_point(Point p1, int d) {
		PolySnapStruct snap = null, snaptemp = null;
		//prioritize active polygon
		if(get_shape()!=null) {
			snap = get_shape().snap_to_point(p1, d, true, PAS.get_pas().get_mapsize(), PAS.get_pas().get_navigation());
			if(snap!=null)
				return snap;
		}
		if(get_shapelist()!=null) {
			for(int i=0; i < get_shapelist().size(); i++) {
				ShapeStruct obj = (ShapeStruct)get_shapelist().get(i);
				long n_distance = 0;
				if(obj != null) {
					snaptemp = obj.snap_to_point(p1, d, false, PAS.get_pas().get_mapsize(), PAS.get_pas().get_navigation());
					if(snaptemp != null) {
						snap = snaptemp;
						if(snap.isActive())//prioritize active polygon
							return snap;
					}
				}
			}
		}
		return snap;
	}	
	
//	 Hvorfor satte du denne til protected? Jeg forandrer den til public
	// Fordi du er teh søkk, jeg setter den til private :p
	public void addShapeToDrawQueue(ShapeStruct s) {
		get_shapelist().add(s);
	}
	protected void mapRedraw() {
		PAS.get_pas().kickRepaint();
	}
	public void updateShape(ShapeStruct s) { //override
		m_shape = s;
	}
	public void updateShapeFilled(ShapeStruct s) {
		m_shape_filled = s;
	}
	protected Graphics getMapGraphics() {
		//return null;
		return PAS.get_pas().get_mappane().getGraphics();
	}	
	public ShapeStruct getMapShape() {
		return m_shape;
	}
	public void gotoMap() {
		Object object = getObjectFromTree();
		if (checkObject(object, AlertVO.class)) {
			//this.event = (EventVO) object;
			try {
				if(((AlertVO)object).getM_shape()!=null) {
					no.ums.pas.maps.defines.NavStruct nav = ((AlertVO)object).getM_shape().calc_bounds();//((AlertVO)object).getM_polygon().calc_bounds();
					if(nav!=null)
						PAS.get_pas().actionPerformed(new ActionEvent(nav, ActionEvent.ACTION_PERFORMED, "act_map_goto_area"));
				}
			} catch(Exception e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
				Error.getError().addError("ParmController","Exception in gotoMap",e,1);
			}
		}
		else if (checkObject(object, EventVO.class)) {
			//this.event = (EventVO) object;
			try {
				if(((EventVO)object).getAlertListe()!=null) {
					// Jeg må finne ut hvor mange av alertene som ikke er null
					int notNullPoly = 0;
					Iterator it = ((EventVO)object).getAlertListe().iterator();
					AlertVO a;
					while(it.hasNext()) {
						a = (AlertVO)it.next();
						if(a.getM_shape() != null)
								notNullPoly++;
					}
					
					Object[] shapes = new Object[notNullPoly];
					// Her må jeg bruke en egen index for arraylisten, siden den kan inneholde mer elementer enn det som skal settes inn
					int index = 0;
					for(int i=0;i<((EventVO)object).getAlertListe().size();i++){
						if(((AlertVO)((EventVO)object).getAlertListe().get(i)).getM_shape() != null){
							shapes[index] = ((AlertVO)((EventVO)object).getAlertListe().get(i)).getM_shape();
							index++;
						}		
					}
					no.ums.pas.maps.defines.NavStruct nav = CommonFunc.calc_bounds(shapes);
					if(nav!=null)
						PAS.get_pas().actionPerformed(new ActionEvent(nav, ActionEvent.ACTION_PERFORMED, "act_map_goto_area"));
				}
			} catch(Exception e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
				Error.getError().addError("ParmController","Exception in gotoMap",e,1);
			}
		}
		else if (checkObject(object, ObjectVO.class)){
			try {
				if(((ObjectVO)object).getM_shape()!=null) {
					no.ums.pas.maps.defines.NavStruct nav = ((ObjectVO)object).getM_shape().calc_bounds();//((AlertVO)object).getM_polygon().calc_bounds();
					if(nav!=null)
						PAS.get_pas().actionPerformed(new ActionEvent(nav, ActionEvent.ACTION_PERFORMED, "act_map_goto_area"));
				}
			} catch(Exception e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
				Error.getError().addError("ParmController","Exception in gotoMap",e,1);
			}
		}
	}
	
	public void setDrawMode(ShapeStruct s) {
		if(s!=null) {
			PAS.get_pas().actionPerformed(new ActionEvent(s, ActionEvent.ACTION_PERFORMED, "act_activate_parm_drawmode"));
			updateShapeFilled(null);
			updateShape(s);
		}
		else
			System.out.println("ShapeStruct == null");
	}
	
	public void setFilled(ShapeStruct s) {
//		if(s!=null) {
			updateShape(null);
			updateShapeFilled(s);
//		}
//		else
//			System.out.println("ShapeStruct == null");
	}
	
	public void actionPerformed(ActionEvent e) {
		if("act_set_active_shape".equals(e.getActionCommand())) {
			ShapeStruct s = (ShapeStruct)e.getSource();
			setDrawMode(s);
			
		}
		super.actionPerformed(e);
	}
	
}