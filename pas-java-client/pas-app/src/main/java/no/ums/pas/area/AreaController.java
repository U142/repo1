package no.ums.pas.area;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.tree.DefaultMutableTreeNode;

import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.pas.PAS;
import no.ums.pas.area.main.MainAreaController;
import no.ums.pas.area.voobjects.AreaVO;
import no.ums.pas.core.Variables;
import no.ums.pas.importer.gis.GISList;
import no.ums.pas.localization.Localization;
import no.ums.pas.maps.MapFrame;
import no.ums.pas.maps.defines.EllipseStruct;
import no.ums.pas.maps.defines.GISShape;
import no.ums.pas.maps.defines.NavStruct;
import no.ums.pas.maps.defines.Navigation;
import no.ums.pas.maps.defines.PolygonStruct;
import no.ums.pas.maps.defines.ShapeStruct;
import no.ums.pas.parm.exception.ParmException;
import no.ums.pas.send.SendProperties;
import no.ums.pas.send.SendPropertiesGIS;
import no.ums.pas.ums.tools.Col;

/**
 * @author sachinn
 */
public class AreaController implements ActionListener{

	private static final Log log = UmsLog.getLogger(AreaController.class);
	
	private MainAreaController main = null;
	private Navigation nav = null;
	private AreaVO currentArea = null;
	private AreaUI gui;
	private AreaVO parentArea = null;
	private int tempPk;
	private boolean isAreaFolder;
	private DefaultMutableTreeNode currentNode;
	private DefaultMutableTreeNode parentNode;
	
	private boolean lock = false;
	private boolean importMore = false;
	private boolean gotoFlag = false;
	private boolean sosImport = false;
	private Col m_default_color = new Col(new Color(1.0f, 0.0f, 0.0f, 0.2f), new Color(1.0f, 0.0f, 0.0f, 0.9f));
	
	private boolean editMode = false;
	private AreaSource source;
	private ActionListener sourceCallback;
	protected ShapeStruct m_edit_shape;
	protected ShapeStruct m_edit_shape_original;
	protected ShapeStruct m_old_shape;

	private SendProperties m_sendproperties;
	
	public SendProperties getSendProperties(){
		return m_sendproperties;
	}

	public boolean isEditMode() { return editMode; }
	
	public void setEditMode(boolean editMode) {
		this.editMode = editMode;
	}
	
	public boolean isLock() {
		return lock;
	}

	public void setImportMore(boolean importMore) {
		this.importMore = importMore;
	}

	public boolean isImportMore() {
		return importMore;
	}

	public void setGotoFlag(boolean gotoFlag) {
		this.gotoFlag = gotoFlag;
	}

	public boolean isGotoFlag() {
		return gotoFlag;
	}
	public void setSosImport(boolean sosImport) {
		this.sosImport = sosImport;
	}
	public boolean isSosImport() {
		return sosImport;
	}

	public AreaSource getSource() { return source; }
	public ShapeStruct get_m_edit_shape() { return m_edit_shape; }
	public ShapeStruct get_m_edit_shape_original() { return m_edit_shape_original; }
	public Navigation get_navigation() { return nav; }
	public void resetEditShape()
	{
		m_edit_shape = null;
		m_edit_shape_original = null;
	}
	public void setEditShape(ShapeStruct s)
	{
		m_edit_shape = s;
		m_edit_shape_original = s;
		PAS.get_pas().getPredefinedAreaController().setDrawMode(s);
	}
	
	public void setActiveShape(ShapeStruct s)
	{
		if(s instanceof PolygonStruct)
		{
			this.gui.getTbPolygon().setEnabled(true);
			this.gui.getTbPolygon().doClick();
		}
		else if(s instanceof EllipseStruct)
		{
			this.gui.getTbEllipse().setEnabled(true);
			this.gui.getTbEllipse().doClick();
		}
		else if(s instanceof GISShape)
		{
			this.gui.getTbImportPoly().setEnabled(true);
			this.gui.getTbImportPoly().doClick();
		}
		setEditShape(s);
	}
	
	public AreaController(MainAreaController main, Navigation nav){
		this.nav = nav;
		this.main = main;
		
		if(PAS.get_pas()!=null)
		{
			PAS.get_pas().get_mappane().addActionListener(this);
		}
	}
	
	public AreaUI getGui() {
		return gui;
	}
	
	public SendProperties getSendProperties(boolean createIfNotExist)
	{
		if(createIfNotExist && m_sendproperties == null)
			m_sendproperties = new SendPropertiesGIS(this);
		return m_sendproperties;
	}

	public synchronized void actionPerformed(ActionEvent e) {
		if("act_add_polypoint".equals(e.getActionCommand()) || "act_area_name_changed".equals(e.getActionCommand())) {

		}
		else if("act_sendingtype_polygon".equals(e.getActionCommand())) {
			log.debug("inside AreaController Sendingtype polygon");

			Col col_default = new Col(m_default_color);
			m_edit_shape.set_fill_color(col_default.get_fill());

			PAS.get_pas().get_mappane().set_mode((MapFrame.MapMode) MapFrame.MapMode.SENDING_POLY);
			PAS.get_pas().kickRepaint();

			PAS.get_pas().actionPerformed(new ActionEvent(m_edit_shape, ActionEvent.ACTION_PERFORMED, "act_set_active_shape"));

			PAS.get_pas().get_mainmenu().actionPerformed(new ActionEvent(m_edit_shape, ActionEvent.ACTION_PERFORMED, "act_activate_parm_drawmode"));
			PAS.get_pas().kickRepaint();

			if(isEditMode())
				PAS.get_pas().getPredefinedAreaController().clearDrawQueue();
			resetImportOption();
		}
		else if("act_sendingtype_ellipse".equals(e.getActionCommand())) {
			log.debug("inside AreaController Sendingtype ellipse");			

			Col col_default = new Col(m_default_color);
			m_edit_shape.set_fill_color(col_default.get_fill());

			PAS.get_pas().get_mappane().set_mode((MapFrame.MapMode) no.ums.pas.maps.MapFrame.MapMode.SENDING_ELLIPSE);
			PAS.get_pas().kickRepaint();

			PAS.get_pas().actionPerformed(new ActionEvent(m_edit_shape, ActionEvent.ACTION_PERFORMED, "act_set_active_shape"));

			if(isEditMode())
				PAS.get_pas().getPredefinedAreaController().clearDrawQueue();
			resetImportOption();
		}
		else if(e.getActionCommand().equals("act_gis_imported"))
		{
			log.debug("callback act_gis_imported from areacontroller");

			final GISList list = (GISList)e.getSource();
			ShapeStruct gisShape = null;

			if(this.isImportMore())
			{
				GISList prevGISList = m_sendproperties.typecast_gis().get_gislist();
				if(prevGISList != null)
					prevGISList.addAll(list);
				gisShape = new GISShape(prevGISList);
			}
			else
			{
				m_sendproperties = new SendPropertiesGIS(this);
				gisShape = new GISShape(list);
			}
			NavStruct nav = list.GetBounds();
			gisShape.SetBounds(nav._lbo, nav._rbo, nav._ubo, nav._bbo);

			PAS.get_pas().get_mappane().set_mode((MapFrame.MapMode) MapFrame.MapMode.SENDING_POLY);
			PAS.get_pas().kickRepaint();

			m_sendproperties.typecast_gis().set_gislist(list);
			m_sendproperties.set_shapestruct(gisShape);
			m_sendproperties.goto_area();

//			Col col_default = new Col(m_default_color);
//			gisShape.set_fill_color(col_default.get_fill());

			this.setEditShape(gisShape);

			//PAS.get_pas().actionPerformed(new ActionEvent(m_edit_shape, ActionEvent.ACTION_PERFORMED, "act_set_active_shape"));

			if(isEditMode())
				PAS.get_pas().getPredefinedAreaController().clearDrawQueue();
		}
		else if("act_save_predefined_area".equals(e.getActionCommand())){
//			log.debug("inside AreaController Save btn from area gui clicked");

			//add validation logic here
			//validate();

			this.getGui().clearErrorMessage();

			if(!isEditMode())
			{
				currentArea = new AreaVO();
				if(parentArea!=null)
					currentArea.setParent(parentArea.getPk());
				else
					currentArea.setParent("0");
			}

			currentArea.setName(this.getGui().getTxtName().getText().trim());
			m_edit_shape.shapeName = currentArea.getName();
			currentArea.setDescription(this.getGui().getTxtDescription().getText().trim());
			currentArea.submitShape(m_edit_shape);

			if(isEditMode())
				main.getTreeCtrl().updateAreaToTree(currentArea, currentNode);
			else
				main.getTreeCtrl().addAreaToTree(currentArea, parentNode);

			this.getGui().dispose();
			lock = false;
			if(this.getSource().equals(AreaSource.LIBRARY))
				clearMap();
			else if(this.getSource().equals(AreaSource.NEW_ALERT))
			{
				try {
					PAS.get_pas().get_sendcontroller().get_activesending().get_toolbar().getBtnSaveArea().setEnabled(false);
				}
				catch(Exception ex)
				{ }
				//sourceCallback.actionPerformed(new ActionEvent("", ActionEvent.ACTION_PERFORMED, "act_save_predefined_area_complete"));
			}
			else if(this.getSource().equals(AreaSource.STATUS))
			{
				//pas event back to statussending
				sourceCallback.actionPerformed(new ActionEvent("", ActionEvent.ACTION_PERFORMED, "act_save_predefined_area_complete"));
			}
			resetImportOption();
		}
		else if("act_cancel_predefined_area".equals(e.getActionCommand())){
//			log.debug("inside AreaController Cancel btn from area gui clicked1");
//			this.setEditShape(m_edit_shape_original);
			if(editMode)
				currentArea.submitShape(m_old_shape);
			this.getGui().dispose();
			lock = false;
			if(this.getSource().equals(AreaSource.LIBRARY))
				clearMap();
			else if(this.getSource().equals(AreaSource.NEW_ALERT) || this.getSource().equals(AreaSource.STATUS))
			{
				//sourceCallback.actionPerformed(new ActionEvent("", ActionEvent.ACTION_PERFORMED, "act_save_predefined_area_cancel"));
//				PAS.get_pas().get_sendcontroller().get_activesending().get_toolbar().actionPerformed(new ActionEvent("", ActionEvent.ACTION_PERFORMED, "act_save_predefined_area_complete"));
			}
			else if(this.getSource().equals(AreaSource.STATUS))
			{
//				//pas event back to statussending
				sourceCallback.actionPerformed(new ActionEvent("", ActionEvent.ACTION_PERFORMED, "act_save_predefined_area_cancel"));
			}
			resetImportOption();
		}
		validate();
	}
	
	private boolean areaNameAlreadyExists(String areaName)
	{
		ArrayList<AreaVO> areaList = Variables.getAreaList();
		for(int i=0;i<areaList.size();i++)
		{
			if(areaList.get(i).getName().equalsIgnoreCase(areaName))
				return true;
		}
		return false;
	}

	public void createNewArea(ActionListener callback, DefaultMutableTreeNode currentNode,boolean editMode, AreaSource source) throws ParmException {
		this.currentArea = null;
		this.sourceCallback = callback;
		this.source = source;
		if(currentNode!=null)
		{
			this.parentNode = currentNode;
			Object object = currentNode.getUserObject();
			if (object.getClass().equals(AreaVO.class)) {
				this.parentArea = (AreaVO) object;
			}
		}
		else
			this.parentNode = null;
		
		this.setEditMode(editMode);
		openAreaFrame(Localization.l("mainmenu_libraries_predefined_areas_new"));
		
		if(this.getSource().equals(AreaSource.NEW_ALERT) || this.getSource().equals(AreaSource.STATUS))
		{
			this.gui.getTbPolygon().setEnabled(false);
			this.gui.getTbEllipse().setEnabled(false);
			this.gui.getTbImportPoly().setEnabled(false);
		}
		this.actionPerformed(new ActionEvent("", ActionEvent.ACTION_PERFORMED, "act_add_polypoint"));
		
	}
	
	private void openAreaFrame(String areaFrameTitle)
	{
        this.gui = new AreaUI(this,areaFrameTitle,nav);
		activateAreaBtnListener();
		lock = true;
	}
	
	public void editArea(DefaultMutableTreeNode currentNode, boolean editMode, AreaSource source) throws ParmException {
		
		this.setEditMode(editMode);
		this.source = source;
		this.currentNode = currentNode;
		Object object = currentNode.getUserObject();
		if (object.getClass().equals(AreaVO.class)) {
			this.currentArea = (AreaVO) object;
		}
		
		this.openAreaFrame(Localization.l("mainmenu_libraries_predefined_areas_edit"));
		
		this.gui.getTxtName().setText(currentArea.getName());
		this.gui.getTxtDescription().setText(currentArea.getDescription());
		if(currentArea.getShape() instanceof PolygonStruct)
		{
			this.gui.getTbPolygon().setEnabled(true);
			this.gui.getTbPolygon().doClick();
			this.actionPerformed(new ActionEvent("", ActionEvent.ACTION_PERFORMED, "act_sendingtype_polygon"));
		}
		else if(currentArea.getShape() instanceof EllipseStruct)
		{
			this.gui.getTbEllipse().setEnabled(true);
			this.gui.getTbEllipse().doClick();
			this.actionPerformed(new ActionEvent("", ActionEvent.ACTION_PERFORMED, "act_sendingtype_ellipse"));
		}
		this.setEditShape(currentArea.getShape());
		
		try
		{
			m_old_shape = (ShapeStruct) currentArea.getShape().clone();
		}
		catch(CloneNotSupportedException ex)
		{
			log.error("error creating shape clone", ex);
		}
		
		this.actionPerformed(new ActionEvent("", ActionEvent.ACTION_PERFORMED, "act_add_polypoint"));
	}
	
	public void deleteArea(AreaVO areaToBeDeleted, DefaultMutableTreeNode currentNode) throws ParmException {
		this.currentNode = currentNode;
		main.getTreeCtrl().removeAreaFromTree(areaToBeDeleted,currentNode);
	}

	private void validate()
	{
		if(this.getGui()!=null)
		{
			boolean lockShape = false;
			boolean nameNotNull = false;
			boolean nameAlreadyUsed = false;
			boolean areaNameOwner = false;
			
			if(m_edit_shape!=null)
				lockShape = m_edit_shape.can_lock(null);
			if((!lockShape) && isSosImport())
				lockShape = true;
			
			nameNotNull = !(this.getGui().getTxtName().getText().equals("") || this.getGui().getTxtName().getText()==null ||this.getGui().getTxtName().getText().length()==0);
			nameAlreadyUsed = areaNameAlreadyExists(this.getGui().getTxtName().getText());
			areaNameOwner = (editMode && currentArea.getName().equalsIgnoreCase(this.getGui().getTxtName().getText()));
			
			if(nameNotNull && nameAlreadyUsed && (!areaNameOwner))
			{
				this.getGui().setErrorMessage(Localization.l("predefined_area_name_must_be_unique"));
			}
			else
			{
				this.getGui().setErrorMessage("");
			}
			
			this.getGui().getBtnSave().setEnabled(lockShape & nameNotNull & ((!nameAlreadyUsed)|areaNameOwner));
		}
	}
	public void activateAreaBtnListener() {
		this.getGui().getBtnSave().setActionCommand("act_save_predefined_area");
		this.getGui().getBtnCancel().setActionCommand("act_cancel_predefined_area");
		this.getGui().getBtnSave().addActionListener(this);
		this.getGui().getBtnCancel().addActionListener(this);
	}
	
	private void clearMap()
	{
		PAS.get_pas().getPredefinedAreaController().updateShape(null);
		PAS.get_pas().getPredefinedAreaController().updateShapeFilled(null);
		PAS.get_pas().getPredefinedAreaController().clearShapesFromDrawQueue();
		
		PAS.get_pas().get_mappane().set_mode((MapFrame.MapMode) MapFrame.MapMode.PAN);
		PAS.get_pas().kickRepaint();
	}
	
	private void resetImportOption()
	{
		setImportMore(false);
		m_sendproperties = null;
	}

	public enum AreaSource
	{
		LIBRARY, NEW_ALERT, PARM, STATUS
	}
}
