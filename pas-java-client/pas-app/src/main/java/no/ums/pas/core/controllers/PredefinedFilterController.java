package no.ums.pas.core.controllers;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.pas.PAS;
import no.ums.pas.PredefinedFilterPanel;
import no.ums.pas.area.FilterController;
import no.ums.pas.area.main.MainFilterController;
import no.ums.pas.area.voobjects.AreaVO;
import no.ums.pas.core.Variables;
import no.ums.pas.core.logon.UserInfo;
import no.ums.pas.core.storage.StorageController;
import no.ums.pas.maps.defines.GISShape;
import no.ums.pas.maps.defines.Navigation;
import no.ums.pas.maps.defines.PolySnapStruct;
import no.ums.pas.maps.defines.ShapeStruct;
import no.ums.pas.ums.errorhandling.Error;

/**
 * @author sachinn
 */
public class PredefinedFilterController extends MainFilterController{
	
	private static final Log log = UmsLog.getLogger(PredefinedFilterController.class);

	private PredefinedFilterPanel PredefinedFilterPanel;
	public PredefinedFilterPanel getPredefinedFilterPanel() { return PredefinedFilterPanel; }
	
	protected ShapeStruct m_shape;
	protected ShapeStruct m_shape_filled;
	protected ArrayList<ShapeStruct> m_arr_shapes = new ArrayList<ShapeStruct>();
	public ArrayList<ShapeStruct> get_shapelist() { return m_arr_shapes; }
	public ShapeStruct get_shape() { return m_shape; }
	public ShapeStruct get_shape_filled() { return m_shape_filled; }
	
	private boolean inBackground;

	public PredefinedFilterController(String sz_sitename, UserInfo userinfo) {
		super(sz_sitename, userinfo);
		PredefinedFilterPanel = new PredefinedFilterPanel(this);
		m_shape   = null;//new PolygonStruct(Variables.NAVIGATION, PAS.get_pas().get_mapsize());
	}
	public PredefinedFilterController(String sz_sitename, UserInfo userinfo, boolean inBackground) {
		super(sz_sitename, userinfo,inBackground);
		this.inBackground = inBackground;
//		PredefinedFilterPanel = new PredefinedFilterPanel(this);
		m_shape = null;//new PolygonStruct(Variables.NAVIGATION, PAS.get_pas().get_mapsize());
	}
	
	public void createGUI() {
		log.debug(StorageController.StorageElements.get_path(StorageController.PATH_PREDEFINEDAREA_).concat(String.valueOf(PAS.get_pas().get_userinfo().get_current_department().get_deptpk()) + File.separator));
		try {
			StorageController.StorageElements.create_path(StorageController.StorageElements.get_path(StorageController.PATH_PREDEFINEDAREA_).concat(String.valueOf(PAS.get_pas().get_userinfo().get_current_department().get_deptpk()) + File.separator));
		} catch(IOException e) {
			Error.getError().addError("PredefinedFilterController","IOException in StorageController.create_path",e,1);
		}
		/*PredefinedAreaConstants.init(PAS.get_pas().get_sitename(), StorageController.StorageElements.get_path(StorageController.PATH_PREDEFINEDAREA_).concat(String.valueOf(PAS.get_pas().get_userinfo().get_current_department().get_deptpk()) + File.separator));
		File cleanExit = new File(PredefinedAreaConstants.cleanExit);
		if(cleanExit.exists()) {
			File dir = new File(PredefinedAreaConstants.homePath);
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
			Error.getError().addError("PredefinedFilterController","IOException in createGUI",e,Error.SEVERITY_ERROR);
		}*/
		initGUI();
	}
	
	public void mapClear() { //overridden
		clearDrawQueue();
	}
	protected void showAlertShape(AreaVO e) { //to be overridden
		log.debug("Area shapes " + e.getName());
		/*Iterator it = e.getAlertListe().iterator();
		//clearDrawQueue();
		while(it.hasNext()){
			AlertVO a = (AlertVO)it.next();
			ShapeStruct s = a.getM_shape();
			
			try {
				addShapeToDrawQueue(s);
			} catch(Exception err) {
				
			}
		}*/
//		**check
		mapRedraw();

	}
	public Navigation getMapNavigation() {
		//return null;
		return Variables.getNavigation();
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
//		log.debug("inside predefined area controller drawLayers(Graphics g) called");
		
		try {				
			FilterController ac = getFilterCtrl();
            final ShapeStruct originalShape = ac != null ? ac.get_m_edit_shape_original() : null;
            final ShapeStruct editShape = ac !=null ? ac.get_m_edit_shape() : null;
            for (ShapeStruct shapeStruct : get_shapelist()) {
                if (shapeStruct != null
                        && (!shapeStruct.equals(originalShape) || shapeStruct.equals(editShape))
                        && getMapNavigation().bboxOverlap(shapeStruct.getFullBBox())) {
                    ShapeStruct selectedShape = getSelectedObject() == null ? null : ((AreaVO) getSelectedObject()).getShape();
                    boolean b_focus = selectedShape == shapeStruct;
                    shapeStruct.draw(g, Variables.getMapFrame().getMapModel(), Variables.getMapFrame().getZoomLookup(), !b_focus, true, false, null, true, true, 1, true, b_focus);
                }
            }
		} catch(Exception e) {
			Error.getError().addError("PredefinedFilterController","Exception in drawLayers",e,1);
		}
		if(get_shape()!=null) {
			get_shape().draw(g, Variables.getMapFrame().getMapModel(), Variables.getMapFrame().getZoomLookup(), false, false, true, PAS.get_pas().get_mappane().get_current_mousepos(), true, true, 2, false);
		}
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
						log.debug(e.getMessage());
						log.warn(e.getMessage(), e);
						Error.getError().addError("PredefinedFilterController","Exception in calc_coortopix",e,1);
					}
				//}
			}
		}
		if(get_shape()!=null) {
			get_shape().calc_coortopix(getMapNavigation());
		}
		
	}
	public PolySnapStruct snap_to_point(Point p1, int d) {
		PolySnapStruct snap = null, snaptemp = null;
		//prioritize active polygon
		if(get_shape()!=null) {
			snap = get_shape().snap_to_point(p1, d, true, PAS.get_pas().get_mapsize(), Variables.getNavigation());
			if(snap!=null)
				return snap;
		}
		if(get_shapelist()!=null) {
			for(int i=0; i < get_shapelist().size(); i++) {
				ShapeStruct obj = (ShapeStruct)get_shapelist().get(i);
				long n_distance = 0;
				if(obj != null) {
					snaptemp = obj.snap_to_point(p1, d, false, PAS.get_pas().get_mapsize(), Variables.getNavigation());
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
	
	public void clearMap()
	{
		this.updateShape(null);
		this.updateShapeFilled(null);
		this.clearShapesFromDrawQueue();
	}
	
	public void addShapeToDrawQueue(ShapeStruct s) {
		get_shapelist().add(s);
	}
	public void removeShapeFromDrawQueue(ShapeStruct s) {
		get_shapelist().remove(s);
	}
	public void clearShapesFromDrawQueue()
	{
		get_shapelist().clear();
	}
	protected void mapRedraw() {
		PAS.get_pas().kickRepaint();
	}
	
	public void updateShape(ShapeStruct s) { //override
		m_shape = s;
		if(getFilterCtrl()!=null && m_shape==null)
		{
			getFilterCtrl().resetEditShape();
		}
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
		if (checkObject(object, AreaVO.class)) {
			//this.event = (EventVO) object;
			try {
				if(((AreaVO)object).getM_shape()!=null) {
					ShapeStruct gisShape = null;
					no.ums.pas.maps.defines.NavStruct nav = null;
					if(((AreaVO)object).getM_shape() instanceof GISShape)
					{
						gisShape = ((AreaVO)object).getM_shape();
						nav = ((GISShape) gisShape).get_gislist().GetBounds();
					}
					else
					{
						nav = ((AreaVO)object).getM_shape().calc_bounds();//((AlertVO)object).getM_polygon().calc_bounds();
					}
					if(nav!=null)
						PAS.get_pas().actionPerformed(new ActionEvent(nav, ActionEvent.ACTION_PERFORMED, "act_map_goto_area"));
				}
			} catch(Exception e) {
				log.debug(e.getMessage());
				log.warn(e.getMessage(), e);
				Error.getError().addError("PredefinedFilterController","Exception in gotoMap",e,1);
			}
		}
	}
	
	public void setDrawMode(ShapeStruct s) {
		if(s!=null) {
			PAS.get_pas().actionPerformed(new ActionEvent(s, ActionEvent.ACTION_PERFORMED, "act_activate_area_drawmode"));
			updateShapeFilled(null);
			updateShape(s);
		}
		else
			log.debug("ShapeStruct == null");
	}
	
	public void setFilled(ShapeStruct s) {
//		if(s!=null) {
			updateShape(null);
			updateShapeFilled(s);
//		}
//		else
//			log.debug("ShapeStruct == null");
	}
	
	public void actionPerformed(ActionEvent e) {
		if("act_set_active_shape".equals(e.getActionCommand())) {
			ShapeStruct s = (ShapeStruct)e.getSource();
			setDrawMode(s);
			
		}
		super.actionPerformed(e);
	}
}
