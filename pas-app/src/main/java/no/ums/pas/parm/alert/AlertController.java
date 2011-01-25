package no.ums.pas.parm.alert;


import no.ums.pas.PAS;
import no.ums.pas.ParmController;
import no.ums.pas.cellbroadcast.Area;
import no.ums.pas.importer.SubsetSelect;
import no.ums.pas.importer.gis.GISList;
import no.ums.pas.maps.defines.*;
import no.ums.pas.parm.exception.ParmException;
import no.ums.pas.parm.main.MainController;
import no.ums.pas.parm.voobjects.AlertVO;
import no.ums.pas.parm.voobjects.EventVO;
import no.ums.pas.send.*;
import no.ums.pas.ums.errorhandling.Error;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;




public class AlertController implements ActionListener {

	private AlertGUI gui;
	private AlertVO alert;
	private EventVO eventParent, event;
	public Object getAlertParent() { return eventParent; }
	private int tmptPk;
	private boolean toObjectList;
//	private PolygonStruct m_shape;
	private ShapeStruct m_edit_shape;
	public ShapeStruct get_m_edit_shape() { return m_edit_shape; }
	//private MapPanel map;
	private MainController main;
	public MainController get_main() { return main; }
	private Navigation nav;
	private SendObject sendObj;
	public SendObject getSendObj() { return sendObj; }
	private SendOptionToolbar toolbarPanel;
	private AlertWindow parent;
	public AlertWindow getParent() { return parent; }
	public void setParent(AlertWindow parent) { this.parent = parent; }

	public AlertController(MainController main, no.ums.pas.maps.defines.Navigation navig) {
		//this.map = m;
		this.main = main;
		nav = navig;

		sendObj = new SendObject("New sending",
				no.ums.pas.send.SendProperties.SENDING_TYPE_POLYGON_, 0, (ParmController)main, main.getMapNavigation());
		toolbarPanel = sendObj.get_toolbar();
		toolbarPanel.get_radio_ellipse().addActionListener(this);
		toolbarPanel.get_radio_polygon().addActionListener(this);

		toolbarPanel.show_buttons(SendOptionToolbar.BTN_ACTIVATE_, false);
		toolbarPanel.show_buttons(SendOptionToolbar.BTN_CENTER_ON_MAP_, false);
		toolbarPanel.show_buttons(SendOptionToolbar.BTN_FINALIZE_, false);
		toolbarPanel.show_buttons(SendOptionToolbar.BTN_SEND_, false);
		toolbarPanel.show_buttons(SendOptionToolbar.BTN_CLOSE_, false);
		toolbarPanel.show_buttons(SendOptionToolbar.TXT_SENDINGNAME_, false);
		toolbarPanel.show_buttons(SendOptionToolbar.BTN_SENDINGTYPE_MUNICIPAL_, false);
		toolbarPanel.set_import_callback(this);
		//toolbarPanel.show_buttons(SendOptionToolbar.BTN_OPEN_, false);
	}
	public AlertController(MainController main, no.ums.pas.maps.defines.Navigation navig, AlertWindow parent) {
		this(main, navig);
		this.parent = parent;
	}
	
	//command input from SendOptionToolbar
	public synchronized void actionPerformed(ActionEvent e) {
		if("act_sendingtype_polygon".equals(e.getActionCommand())) {
			System.out.println("Sendingtype polygon");
			m_edit_shape = (new PolygonStruct(main.getMapNavigation().getDimension()));
			PAS.get_pas().get_mappane().set_active_shape(m_edit_shape);
		}
		else if("act_sendingtype_ellipse".equals(e.getActionCommand())) {
			System.out.println("Sendingtype ellipse");			
			m_edit_shape = (new EllipseStruct());
			PAS.get_pas().get_mappane().set_active_shape(m_edit_shape);
		}
		else if("act_set_shape".equals(e.getActionCommand())) {
			m_edit_shape = (ShapeStruct)e.getSource();
			toolbarPanel.setActiveShape(m_edit_shape);
			PAS.get_pas().get_mappane().set_active_shape(m_edit_shape);

		}
		else if("act_sosi_parsing_complete".equals(e.getActionCommand())) {
			//SosiFile sosi = (SosiFile)e.getSource();
			//SosiFile.FlateArray sendings = sosi.get_flater();
			String [] sz_columns = { "ID", "Name" };
			int [] n_width = { 50, 250 };
			boolean [] b_edit = { false, false };
			Dimension d = new Dimension(400, 500);
			//if(e.getSource().getClass().equals(ArrayList.class))
			{
				ArrayList<SendObject> sendings = (ArrayList<SendObject>)e.getSource();
				
				//SELECT SOSI-KURVE FOR IMPORT
				//if(sendings.size()>1) {
					new SubsetSelect(sz_columns, n_width, b_edit, d, this, sendings);
			}
			
			if(m_edit_shape.getType() == ShapeStruct.SHAPE_POLYGON) {
				//System.out.println(o.toString());
				//parent.get_sendcontroller().get_activesending().get_toolbar().get_radio_polygon().doClick();
			}
			/*else if(e.getSource().getClass().equals(Object[].class))
			{
				int num = ((Object[])e.getSource()).length;
				if(num>0)
				{
					ShapeStruct [] sendings = new ShapeStruct[num];//(ShapeStruct[])e.getSource();
					for(int i=0; i < num; i++)
					{
						sendings[i] = (ShapeStruct)((Object[])e.getSource())[i];
					}
					new SubsetSelect(sz_columns, n_width, b_edit, d, this, sendings);
				}
			}*/
				if(m_edit_shape != null) {
					parent.get_btn_next().setEnabled(true);
				}
			/*} else if(sendings.size()==1){
				m_edit_shape = ((SendObject)sendings.get(0)).get_sendproperties().typecast_poly().get_shapestruct();
				PAS.get_pas().actionPerformed(new ActionEvent(m_edit_shape.calc_bounds(), ActionEvent.ACTION_PERFORMED, "act_goto_map"));
			}*/
			/*ActionFileLoaded event = (ActionFileLoaded)e;
			SosiFile sosi = (SosiFile)e.getSource();
			//PAS.get_pas().get_drawthread().set_suspended(true);
			try {
				//get_parent().get_sendproperties().typecast_poly().set_shapestruct(new PolygonStruct(PAS.get_pas().get_mappane().get_dimension(), sosi.get_polygon()));
				//get_parent().get_sendproperties().typecast_poly().set_polygon_color();
			} catch(Exception err) {
				Error.getError().addError("SendOptionToolbar","Exception in actionPerformed",err,1);
			}
			try {
				String sz_description = sosi.toString();
				System.out.println(sz_description);
				//get_parent().get_sendproperties().set_sendingname((sosi.get_flater().get_current_flate().get_name().length() > 0 ? sosi.get_flater().get_current_flate().get_name() : sosi.get_flater().get_current_flate().get_objecttype()), sz_description);
			} catch(Exception err) {
				System.out.println(err.getMessage());
				err.printStackTrace();
				Error.getError().addError("SendOptionToolbar","Exception in actionPerformed",err,1);
			}*/
		}
		else if("act_gis_imported".equals(e.getActionCommand())) {// || "act_gis_finish".equals(e.getActionCommand())) {
			GISList list = (GISList)e.getSource();
			//m_alert.getSendObj().get_sendproperties()
			m_edit_shape = new GISShape(list);
			sendObj.set_type(SendProperties.SENDING_TYPE_GEMINI_STREETCODE_);
			sendObj.set_sendproperties(new SendPropertiesGIS(toolbarPanel));
			sendObj.get_sendproperties().typecast_gis().set_gislist(list);
			toolbarPanel.setActiveShape(m_edit_shape);
			parent.get_btn_next().setEnabled(true);
			//PAS.get_pas().get_mappane().set_active_shape(sendObj.get_sendproperties().typecast_gis().get_shapestruct());
			//PAS.get_pas().get_mappane().set_active_shape(m_edit_shape = (ShapeStruct)e.getSource());
			//PAS.get_pas().actionPerformed(new ActionEvent(m_edit_shape.calc_bounds(), ActionEvent.ACTION_PERFORMED, "act_map_goto_area"));
			//sendObj.get_sendproperties().goto_area();
			System.out.println("SendOptionToolbar: " + list.size() + " lines");
		}
		else if("act_sending_selected".equals(e.getActionCommand())) {
			try {
				if(e.getSource().getClass().equals(SendObject.class))
				{
					PAS.get_pas().get_mappane().set_active_shape(m_edit_shape = (ShapeStruct)((SendObject)e.getSource()).get_sendproperties().typecast_poly().get_shapestruct());
				}
				else if(e.getSource().getClass().equals(PolygonStruct.class))
				{
					PAS.get_pas().get_mappane().set_active_shape(m_edit_shape = (ShapeStruct)e.getSource());
				}
				PAS.get_pas().actionPerformed(new ActionEvent(m_edit_shape.calc_bounds(), ActionEvent.ACTION_PERFORMED, "act_map_goto_area"));
				//new ExecAlert().SendAlert("1");
				//toolbarPanel.setActiveShape(m_edit_shape = (ShapeStruct)((SendObject)e.getSource()).get_sendproperties().typecast_poly().get_shapestruct());
			} catch(Exception err) {
				
			}
		}
		else if("act_sending_preview".equals(e.getActionCommand())) {
			try {
				ShapeStruct shape = (ShapeStruct)((SendObject)e.getSource()).get_sendproperties().typecast_poly().get_shapestruct();
				//PAS.get_pas().get_mappane().set_active_shape(shape);
				//m_edit_shape = shape;
				PAS.get_pas().get_parmcontroller().addShapeToDrawQueue(shape);
				//this.toolbarPanel.setActiveShape(shape);
				PAS.get_pas().actionPerformed(new ActionEvent(shape.calc_bounds(), ActionEvent.ACTION_PERFORMED, "act_map_goto_area"));
			} catch(Exception err) {
				
			}
		}
		else if("act_enable_next".equals(e.getActionCommand())) {
			getParent().get_btn_next().setEnabled(true);
		}
		//toolbarPanel.setActiveShape(m_edit_shape);
		//main.mapClear();
		//main.setDrawMode(m_edit_shape);
	}

	public void createNewAlert(int highestTempPk, EventVO eventParent,ShapeStruct shape) throws ParmException {
		this.alert = null;
		this.tmptPk = highestTempPk;
		this.eventParent = eventParent;
		this.m_edit_shape = shape;

		this.toolbarPanel.init_addresstypes(SendController.SENDTO_FIXED_PRIVATE | SendController.SENDTO_FIXED_COMPANY | SendController.SENDTO_MOBILE_PRIVATE | SendController.SENDTO_MOBILE_COMPANY);
		gui = new AlertGUI("New Alert", this.toolbarPanel);
		//gui.setTitle("New Alert");
		try {
			toolbarPanel.setActiveShape(m_edit_shape);
			//toolbarPanel.setActivePolygon(PAS.get_pas().get_parmcontroller().getMapPolygon());
		} catch(Exception e){
			Error.getError().addError("AlertController","Exception in createAlert",e,1);
		}
		try {
			if(shape != null) {
				if(toolbarPanel.get_colorpicker()!=null && toolbarPanel.get_colorpicker().get_selected_color()!=null)
					shape.set_fill_color(toolbarPanel.get_colorpicker().get_selected_color());
				else
					shape.set_fill_color(toolbarPanel.get_colorbutton().getBackground());
			}
		} catch(Exception e) {
			Error.getError().addError("Error", "Set fill color failed on alertpolygon", e, 1);
		}
		main.setDrawMode(m_edit_shape);
	}
	
	public void submitPolygon(AlertVO alert) {
		alert.submitShape(m_edit_shape);
	}

	public void editAlert(AlertVO alert, MainController main, DefaultMutableTreeNode eventNode) throws ParmException {
		m_edit_shape = null;
		//m_polygon = PAS.get_pas().get_parmcontroller().getMapPolygon();
				
		this.alert = alert;
		this.eventParent = (EventVO) eventNode.getUserObject();
		if (alert.getSendObject() != null && alert.getSendObject().get_toolbar() != null)
			this.toolbarPanel = alert.getSendObject().get_toolbar();

		//if (this.toolbarPanel.get_addresstypes() != 0)
		//toolbarPanel.gen_addresstypes();
		toolbarPanel.set_addresstypes(alert.getAddresstypes());
		toolbarPanel.init_addresstypes(alert.getAddresstypes());
		toolbarPanel.show_buttons(SendOptionToolbar.BTN_FINALIZE_, false);

		gui = new AlertGUI(PAS.l("main_parm_alert_dlg_edit"), this.toolbarPanel);
		//gui.setTitle("Edit Alert");

		// Dette er for å gå over til edit mode på polygonen
		//main.mapClear();
		//if(this.m_polygon != null) {
			/*try{
				m_edit_polygon = (PolygonStruct) m_polygon.clone();
			} catch(Exception e){
				e.printStackTrace();
			}*/
		//}
		//else
		if(alert.getM_shape()==null) {
			//default to polygon
			m_edit_shape = (new PolygonStruct(main.getMapNavigation().getDimension()));
		}
		else {
			try {
				m_edit_shape = (ShapeStruct)alert.getM_shape().clone();
			} catch(Exception e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
				Error.getError().addError("AlertController","Exception in editAlert",e,1);
			}
		}
		try {
			toolbarPanel.setActiveShape(m_edit_shape);
			//toolbarPanel.setActivePolygon(PAS.get_pas().get_parmcontroller().getMapPolygon());
		} catch(Exception e){
			Error.getError().addError("AlertController","Exception in editAlert",e,1);
		}
		//main.mapClear();
		main.setDrawMode(m_edit_shape);
		//System.out.println("Coors: " + m_edit_shape.get_coors_lat().size());
	}

	public boolean deleteAlert(AlertVO alert, DefaultMutableTreeNode eventNode)
			throws ParmException {
		this.alert = alert;
		Object[] options = { PAS.l("common_yes"), PAS.l("common_cancel") };

		int n = JOptionPane.showOptionDialog(null,
				PAS.l("common_delete_are_you_sure") + " '" + alert.getName() + "'",
				PAS.l("common_delete") + " " + PAS.l("main_parmtab_popup_alert") + "?", JOptionPane.YES_NO_OPTION,
				JOptionPane.WARNING_MESSAGE, null, options, options[1]);
		// yes = 0, no = 1
		if (n == 0) {
			this.alert.setOperation("delete");
			this.eventParent = (EventVO) eventNode.getUserObject();
			this.eventParent.removeAlerts(this.alert);
			return true;
		} else {
			return false;
		}
	}

	public AlertVO storeAlert(AlertWindow aw) {
		if(alert != null)
			this.toObjectList = false;
		else
			this.toObjectList = true;
		// Her må jeg sjekke om det er en ny eller edit event, men i tillegg
		// så kan det være en edit før den blir satt inn og da skal det fortsatt være insert (dersom den ikke har a i pk)
		if (this.alert == null) {
			this.alert = new AlertVO();
			this.alert.setOperation("insert");
			this.alert.setStrAlertpk(Integer.toString(this.tmptPk));
			this.alert.setParent(this.eventParent.getEventPk());
		} else if(this.alert.getAlertpk().contains("a")) {
			this.alert.setOperation("update");
		}

		this.alert.setDescription(gui.getTxtDescription().getText());
		this.alert.setAddresstypes(this.getPanelToolbar().get_addresstypes());
		this.alert.setName(aw.get_alert_settings().get_sendingname());
		this.alert.setProfilepk(aw.get_alert_settings().get_current_profile().get_profilepk());
		this.alert.setValidity(aw.get_alert_settings().get_current_validity());
		this.alert.setOadc(aw.get_alert_settings().get_current_oadc().get_number());
		// Må sjekke at sendingen ikke har voice
		if(aw.get_alert_settings().get_current_schedprofile() == null) {
			if(!aw.hasVoice(this.getPanelToolbar().get_addresstypes()))
				this.alert.setSchedpk("0");
			else {
				Error.getError().addError("AlertController","Cannot store messages with voice without configuration profile",1,Error.SEVERITY_ERROR);
				return null;
			}
		}
		else
			this.alert.setSchedpk(aw.get_alert_settings().get_current_schedprofile().get_reschedpk());
		
		this.alert.setMaxChannels(aw.get_alert_settings().getMaxChannels());
		if(aw.get_cell_broadcast_text() != null)
			this.alert.setRequestType(aw.get_cell_broadcast_text().getRequestType());
		else
			this.alert.setRequestType(0);
		if(aw.get_alert_send().get_chk_execute_remote().isSelected())
			this.alert.setLocked(1);
		else
			this.alert.setLocked(0);
		
		if(toolbarPanel.get_cell_broadcast_text().isSelected() || toolbarPanel.get_cell_broadcast_voice().isSelected()) {
			// Her skal jeg gjøre noe kjekt faktisk hente info fra cell broadcast panel
			alert.setCBMessages(parent.get_cell_broadcast_text().getMessages());
			alert.setArea((Area)parent.get_cell_broadcast_text().get_combo_area().getSelectedItem());
			alert.setCBOadc(parent.get_cell_broadcast_text().get_txt_oadc_text().getText());
		}
		// If sms is selected
		alert.setLBAExpiry(parent.get_sms_broadcast_text().get_expiryMinutes());
		if(parent.get_sms_broadcast_text().get_expiryMinutes() < 1)
			alert.setLBAExpiry(parent.get_cell_broadcast_text().get_expiryMinutes());
		alert.setLBAExpiry(parent.get_cell_broadcast_text().get_expiryMinutes());
		alert.setSMSMessage(parent.get_sms_broadcast_text().get_txt_messagetext().getText());
		alert.setSMSOadc(parent.get_sms_broadcast_text().get_txt_oadc_text().getText());
		// Need to do something about expiry
		
		// System.out.println("adressetype value " +
		// this.getPanelToolbar().get_addresstypes() +" set");
		
		//m_edit_polygon.set_fill_color(toolbarPanel.get_colorpicker().get_selected_color());
		//this.alert.submitShape(m_edit_shape);
		if(sendObj.get_sendproperties().getClass().equals(SendPropertiesGIS.class)) {
			System.out.print("SendPropertiesGIS");
			SendPropertiesGIS spGIS = (SendPropertiesGIS)sendObj.get_sendproperties();
			if(PAS.get_pas().get_mappane().get_active_shape().getClass().equals(GISShape.class))
				this.alert.submitShape(new GISShape(((GISShape)PAS.get_pas().get_mappane().get_active_shape()).get_gislist()));//new GISShape(spGIS.get_gislist()));
			else
				this.alert.submitShape(new GISShape(spGIS.get_gislist()));
			
		}
		else
			this.alert.submitShape(PAS.get_pas().get_mappane().get_active_shape());
		
//		if(this.m_polygon == null) {
//			try {
//				this.alert.setM_polygon((PolygonStruct) main.getMapPolygon().clone()); //map.getM_polygon().clone());
//			} catch (CloneNotSupportedException e) {
//				e.printStackTrace();
//			}
//		}
		if (this.existInEventList(this.alert) == false) {
			this.eventParent.addAlerts(this.alert);
		}
		return this.alert;
	}

	public boolean existInEventList(AlertVO a) {
		boolean b = false;
		if (this.eventParent != null && this.eventParent.getAlertListe() != null) {
			for (int i = 0; i < this.eventParent.getAlertListe().size(); i++) {
				AlertVO temp = (AlertVO) this.eventParent.getAlertListe()
						.get(i);
				if (temp.getAlertpk() == a.getAlertpk()) {
					this.eventParent.getAlertListe().set(i, a);
					b = true;
				}
			}
		}
		return b;
	}

	// DefaultMutableTreeNode eventNode =
	// (DefaultMutableTreeNode)remNode.getParent();
	// EventVO tempEvent = (EventVO)eventNode.getUserObject();
	// tempEvent.removeAlerts(this.alert);

	public void insertAlert(MainController main) {
		Object object = main.getObjectFromTree();
		if (main.checkObject(object, EventVO.class)) {
			this.event = (EventVO) object;
		}
		try {
			main.tempPK++;
			createNewAlert(main.tempPK, this.event, new PolygonStruct(main.getMapSize()));/*main.getMapPolygon()*/ //main.getMap().getM_polygon());
			main.activateAlertBtnListener();
		} catch (Exception exception) {
			exception.printStackTrace();
			Error.getError().addError(PAS.l("common_error"),"Exception in editAlert",exception,1);
		}
	}

	public AlertGUI getGui() {
		return gui;
	}

	public AlertVO getAlert() {
		return this.alert;
	}
	
	public void setAlert(AlertVO alert) {
		this.alert = alert;
	}

	public boolean isToObjectList() {
		return toObjectList;
	}

	public SendOptionToolbar getPanelToolbar() {
		return this.toolbarPanel;
	}

}
