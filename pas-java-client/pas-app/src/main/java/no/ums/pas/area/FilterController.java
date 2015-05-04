package no.ums.pas.area;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.tree.DefaultMutableTreeNode;

import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.pas.PAS;
import no.ums.pas.area.main.MainFilterController;
import no.ums.pas.area.voobjects.AddressFilterInfoVO;
import no.ums.pas.core.Variables;
import no.ums.pas.importer.gis.GISList;
import no.ums.pas.localization.Localization;
import no.ums.pas.maps.MapFrame;
import no.ums.pas.maps.defines.GISShape;
import no.ums.pas.maps.defines.NavStruct;
import no.ums.pas.maps.defines.Navigation;
import no.ums.pas.maps.defines.ShapeStruct;
import no.ums.pas.parm.exception.ParmException;
import no.ums.pas.send.SendProperties;
import no.ums.pas.send.SendPropertiesGIS;
import no.ums.pas.ums.tools.Col;

/**
 * @author abhinava
 */
public class FilterController implements ActionListener{

	private static final Log log = UmsLog.getLogger(FilterController.class);
	
	private MainFilterController main = null;
	private Navigation nav = null;
	private AddressFilterInfoVO currentFilter = new AddressFilterInfoVO();
	private FilterUI gui;
	private AddressFilterInfoVO addressFilterVO = null;
	private int tempPk;
	private boolean isFilterFolder;
	private DefaultMutableTreeNode currentNode;
	private DefaultMutableTreeNode parentNode;
	
	private boolean lock = false;
	private boolean importMore = false;
	private boolean gotoFlag = false;
	private boolean sosImport = false;
	private Col m_default_color = new Col(new Color(1.0f, 0.0f, 0.0f, 0.2f), new Color(1.0f, 0.0f, 0.0f, 0.9f));
	
	private boolean editMode = false;
	private FilterSource source;
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

	public FilterSource getSource() { return source; }
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
		PAS.get_pas().getPredefinedFilterController().setDrawMode(s);
	}
	
	public void setActiveShape(ShapeStruct s)
	{
	
		 if(s instanceof GISShape)
		{
			this.gui.getTbImportPoly().setEnabled(true);
			this.gui.getTbImportPoly().doClick();
		}
		setEditShape(s);
	}
	
	public FilterController(MainFilterController main, Navigation nav){
		this.nav = nav;
		this.main = main;
		
		if(PAS.get_pas()!=null)
		{
			PAS.get_pas().get_mappane().addActionListener(this);
		}
	}
	
	public FilterUI getGui() {
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

		else if(e.getActionCommand().equals("act_gis_imported"))
		{
			log.debug("callback act_gis_imported from FilterController");

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
				PAS.get_pas().getPredefinedFilterController().clearDrawQueue();
		}
		else if("act_save_predefined_area".equals(e.getActionCommand())){

			this.getGui().clearErrorMessage();

			if(!isEditMode())
			{
				//currentFilter = new AddressFilterInfoVO();
				if(addressFilterVO!=null)
					currentFilter.setParent(addressFilterVO.getPk());
				else
					currentFilter.setParent("0");
			}

			currentFilter.setFilterName((this.getGui().getTxtName().getText().trim()));
		
			currentFilter.setDescription((this.getGui().getTxtDescription().getText().trim()));
		

			if(isEditMode())
				main.getTreeCtrl().updateFilterToTree(currentFilter, currentNode);
			else
				main.getTreeCtrl().addFilterToTree(currentFilter, parentNode);

			this.getGui().dispose();
			lock = false;
			if(this.getSource().equals(FilterSource.LIBRARY))
				clearMap();
			else if(this.getSource().equals(FilterSource.NEW_ALERT))
			{
				try {
					PAS.get_pas().get_sendcontroller().get_activesending().get_toolbar().getBtnSaveArea().setEnabled(false);
				}
				catch(Exception ex)
				{ }
				//sourceCallback.actionPerformed(new ActionEvent("", ActionEvent.ACTION_PERFORMED, "act_save_predefined_area_complete"));
			}
			else if(this.getSource().equals(FilterSource.STATUS))
			{
				//pas event back to statussending
				sourceCallback.actionPerformed(new ActionEvent("", ActionEvent.ACTION_PERFORMED, "act_save_predefined_area_complete"));
			}
			resetImportOption();
		}
		else if("act_cancel_predefined_area".equals(e.getActionCommand())){
//			log.debug("inside FilterController Cancel btn from area gui clicked1");
//			this.setEditShape(m_edit_shape_original);
			if(editMode)
				currentFilter.submitShape(m_old_shape);
			this.getGui().dispose();
			lock = false;
			if(this.getSource().equals(FilterSource.LIBRARY))
				clearMap();
			else if(this.getSource().equals(FilterSource.NEW_ALERT) || this.getSource().equals(FilterSource.STATUS))
			{
				//sourceCallback.actionPerformed(new ActionEvent("", ActionEvent.ACTION_PERFORMED, "act_save_predefined_area_cancel"));
//				PAS.get_pas().get_sendcontroller().get_activesending().get_toolbar().actionPerformed(new ActionEvent("", ActionEvent.ACTION_PERFORMED, "act_save_predefined_area_complete"));
			}
			else if(this.getSource().equals(FilterSource.STATUS))
			{
//				//pas event back to statussending
				sourceCallback.actionPerformed(new ActionEvent("", ActionEvent.ACTION_PERFORMED, "act_save_predefined_area_cancel"));
			}
			resetImportOption();
		}
		validate();
	}
	
	private boolean filterNameAlreadyExists(String filterName)
	{
		ArrayList<AddressFilterInfoVO> filterList = Variables.getFilterList();
		for(int i=0;i<filterList.size();i++)
		{
			if(filterList.get(i).getFilterName().equalsIgnoreCase(filterName))
				return true;
		}
		return false;
	}

	public void createNewFilter(ActionListener callback, DefaultMutableTreeNode currentNode,boolean editMode, FilterSource source) throws ParmException {
		this.currentFilter = null;
		this.sourceCallback = callback;
		this.source = source;
		if(currentNode!=null)
		{
			this.parentNode = currentNode;
			Object object = currentNode.getUserObject();
			if (object.getClass().equals(AddressFilterInfoVO.class)) {
				this.addressFilterVO = (AddressFilterInfoVO) object;
			}
		}
		else
			this.parentNode = null;
		
		this.setEditMode(editMode);
		openFilterFrame(Localization.l("mainmenu_libraries_predefined_filter_name"));
		
		if(this.getSource().equals(FilterSource.NEW_ALERT) || this.getSource().equals(FilterSource.STATUS))
		{
		
			this.gui.getTbImportPoly().setEnabled(false);
		}
		this.actionPerformed(new ActionEvent("", ActionEvent.ACTION_PERFORMED, "act_add_polypoint"));
		
	}
	
	private void openFilterFrame(String areaFrameTitle)
	{
        this.gui = new FilterUI(this,areaFrameTitle,nav);
		activateAreaBtnListener();
		lock = true;
	}
	
	public void editArea(DefaultMutableTreeNode currentNode, boolean editMode, FilterSource source) throws ParmException {
		
		this.setEditMode(editMode);
		this.source = source;
		this.currentNode = currentNode;
		Object object = currentNode.getUserObject();
		if (object.getClass().equals(AddressFilterInfoVO.class)) {
			this.currentFilter = (AddressFilterInfoVO) object;
		}
		
		this.openFilterFrame(Localization.l("mainmenu_libraries_predefined_filter_edit"));
		
		this.gui.getTxtName().setText(currentFilter.getFilterName());
		this.gui.getTxtDescription().setText(currentFilter.getDescription());
		AddressFilterInfoVO filterVo = PAS.get_pas()
				.getPredefinedFilterController().getFilterCtrl()
				.getCurrentFilter();
		filterVo.getGisFilterList().clear();
		
		this.setEditShape(currentFilter.getShape());
		
	
	}
	
	public void deleteFilter(AddressFilterInfoVO areaToBeDeleted, DefaultMutableTreeNode currentNode) throws ParmException {
		this.currentNode = currentNode;
		main.getTreeCtrl().removeFilterFromTree(areaToBeDeleted,currentNode);
	}

	private void validate() {
		if (this.getGui() != null) {
			boolean lockShape = false;
			boolean nameNotNull = false;
			boolean nameAlreadyUsed = false;
			boolean filterNameOwner = false;

			AddressFilterInfoVO filterVo = PAS.get_pas()
					.getPredefinedFilterController().getFilterCtrl()
					.getCurrentFilter();
			if (filterVo != null && filterVo.getGisFilterList().size() > 0) {
				lockShape = true;
			} else {
				lockShape = false;
			}

			nameNotNull = !(this.getGui().getTxtName().getText().equals("")
					|| this.getGui().getTxtName().getText() == null || this
					.getGui().getTxtName().getText().length() == 0);
			nameAlreadyUsed = filterNameAlreadyExists(this.getGui().getTxtName()
					.getText());
			filterNameOwner = (editMode && currentFilter.getFilterName()
					.equalsIgnoreCase(this.getGui().getTxtName().getText()));

			if (nameNotNull && nameAlreadyUsed && (!filterNameOwner)) {
				this.getGui()
						.setErrorMessage(
								Localization
										.l("predefined_filter_name_must_be_unique"));
			} else {
				this.getGui().setErrorMessage("");
			}

			this.getGui()
					.getBtnSave()
					.setEnabled(
							lockShape & nameNotNull
									& ((!nameAlreadyUsed) | filterNameOwner));
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
		PAS.get_pas().getPredefinedFilterController().updateShape(null);
		PAS.get_pas().getPredefinedFilterController().updateShapeFilled(null);
		PAS.get_pas().getPredefinedFilterController().clearShapesFromDrawQueue();
		
		PAS.get_pas().get_mappane().set_mode((MapFrame.MapMode) MapFrame.MapMode.PAN);
		PAS.get_pas().kickRepaint();
	}
	
	private void resetImportOption()
	{
		setImportMore(false);
		m_sendproperties = null;
	}

	public enum FilterSource
	{
		LIBRARY, NEW_ALERT, PARM, STATUS
	}

	public AddressFilterInfoVO getCurrentFilter() {
		return currentFilter;
	}

	public void setCurrentFilter(AddressFilterInfoVO currentFilter) {
		this.currentFilter = currentFilter;
	}
}
