package no.ums.pas.send;

import no.ums.pas.PAS;
import no.ums.pas.core.Variables;
import no.ums.pas.core.dataexchange.soap.SoapExecAlert;
import no.ums.pas.core.defines.LightPanel;
import no.ums.pas.core.logon.LogonDialog;
import no.ums.pas.core.logon.UserInfo;
import no.ums.pas.core.mainui.EastContent;
import no.ums.pas.core.mainui.LoadingFrame;
import no.ums.pas.core.menus.defines.CheckItem;
import no.ums.pas.core.project.Project;
import no.ums.pas.core.project.ProjectDlg;
import no.ums.pas.core.ws.WSProgressPoller;
import no.ums.pas.core.ws.WSThread;
import no.ums.pas.core.ws.vars;
import no.ums.pas.importer.SosiFile;
import no.ums.pas.importer.gis.PreviewFrame;
import no.ums.pas.maps.MapFrame;
import no.ums.pas.maps.defines.*;
import no.ums.pas.parm.voobjects.AlertVO;
import no.ums.pas.parm.voobjects.EventVO;
import no.ums.pas.send.sendpanels.SendWindow;
import no.ums.pas.send.sendpanels.Sending_Cell_Broadcast_text;
import no.ums.pas.status.StatusSending;
import no.ums.pas.ums.errorhandling.Error;
import no.ums.pas.ums.tools.Col;
import no.ums.pas.ums.tools.StdTextArea;
import no.ums.pas.ums.tools.Utils;
import no.ums.ws.parm.AlertResultLine;
import no.ums.ws.parm.ExecResponse;
import no.ums.ws.parm.Parmws;
import no.ums.ws.parm.ULOGONINFO;
import no.ums.ws.pas.ProgressJobType;
import no.ums.ws.pas.tas.ULBACOUNTRY;

import javax.swing.*;
import javax.xml.namespace.QName;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

//import Core.DataExchange.SOAP.Service1Client;

//import javax.swing.plaf.basic.BasicToolBarUI;






public class SendController implements ActionListener {
	public static final int SENDTO_NOPHONE_PRIVATE	= 1 << 0;
	public static final int SENDTO_NOPHONE_COMPANY	= 1 << 1;
	public static final int SENDTO_FIXED_PRIVATE	= 1 << 2;
	public static final int SENDTO_FIXED_COMPANY	= 1 << 3;
	public static final int SENDTO_MOBILE_PRIVATE	= 1 << 4;
	public static final int SENDTO_MOBILE_COMPANY	= 1 << 5;
	public static final int SENDTO_MOVED_RECIPIENT_PRIVATE  = 1 << 6;
	public static final int SENDTO_MOVED_RECIPIENT_COMPANY  = 1 << 7;
	public static final int SENDTO_CELL_BROADCAST_TEXT = 1 << 8;
	public static final int SENDTO_CELL_BROADCAST_VOICE = 1 << 9;
	public static final int SENDTO_SMS_PRIVATE = 1 << 10;
	public static final int SENDTO_SMS_COMPANY = 1 << 11;
	public static final int SENDTO_SMS_PRIVATE_ALT_FIXED = 1 << 12;
	public static final int SENDTO_SMS_COMPANY_ALT_FIXED = 1 << 13;
	public static final int SENDTO_FIXED_PRIVATE_ALT_SMS = 1 << 14;
	public static final int SENDTO_FIXED_COMPANY_ALT_SMS = 1 << 15;
	public static final int SENDTO_MOBILE_PRIVATE_AND_FIXED = 1 << 16;
	public static final int SENDTO_MOBILE_COMPANY_AND_FIXED = 1 << 17;
	public static final int SENDTO_FIXED_PRIVATE_AND_MOBILE = 1 << 18;
	public static final int SENDTO_FIXED_COMPANY_AND_MOBILE = 1 << 19;
	public static final int SENDTO_TAS_SMS = 1 << 20;
	public static final int SENDTO_USE_NOFAX_COMPANY = 1 << 27;
	public static final int SENDTO_USE_NOFAX_DEPARTMENT = 1 << 28; //reserved for future use
	public static final int SENDTO_USE_NOFAX_GLOBAL = 1 << 29; //should always be off
	public static final int SENDTO_ONLY_HEAD_OF_HOUSEHOLD = 1 << 30;
	
	/*
	 * Mobile recipient type have the following combinations.
	 * SENDTO_MOBILE_XXXXX => send voice message to mobile phone 
	 * SENDTO_MOBILE_XXXXX_SMS => send sms to mobile phone
	 * SENDTO_MOBILE_XXXXX | SENDTO_MOBILE_XXXXX_SMS => send both voice and sms to mobile phone
	 * SENDTO_FIXED_PRIVATE_IF_NO_MOBILESMS => send voice message to fixed phone if recipient don't have a mobile phone. This will disable the fixed phone icon
	 * 
	 */
	//public static final int SENDTO_MOBILE_PRIVATE_SMS_ALT_FIXED = 1 << 12; //send voicemsg to fixed phone if the recipient don't have a mobile phone
	//public static final int SENDTO_MOBILE_COMPANY_SMS_ALT_FIXED = 1 << 13;
	
	
	public static final int SENDTO_ALL = SENDTO_NOPHONE_PRIVATE | SENDTO_NOPHONE_COMPANY | SENDTO_FIXED_PRIVATE | SENDTO_FIXED_COMPANY | SENDTO_MOBILE_PRIVATE | SENDTO_MOBILE_COMPANY; // | SENDTO_CELL_BROADCAST_TEXT | SENDTO_CELL_BROADCAST_VOICE; 
	
	public static final boolean HasType(int TYPES, int TYPE) {
		if((TYPES & TYPE) == TYPE)
			return true;
		return false;
	}
	
	private PAS m_pas;
	private int m_n_send_id = 0;
	public void reset_send_id () {
		m_n_send_id = 0;
	}
	//private Send m_sendsetup;
	private ArrayList<SendObject> m_sendings;
	public PAS get_pas() { return m_pas; }
	public ArrayList<SendObject> get_sendings() { return m_sendings; }
	private int m_n_activesending_number = -1;
	public int get_activesending_number() { return m_n_activesending_number; }
	private SendObject m_active_sending = null;
	public SendObject get_activesending() { return m_active_sending; }
	private ButtonGroup m_btngroup_activate = null;
	public ButtonGroup get_btngroup_activate() { return m_btngroup_activate; }
/*	private PUPolyPoint m_polypoint_popup = null;
	private PolySnapStruct m_polysnapstruct = null;
	private void set_current_snappoint(PolySnapStruct p) { m_polysnapstruct = p; }
	public PolySnapStruct get_current_snappoint() { return m_polysnapstruct; } */
	private boolean m_b_ignore_project = false;
	public void remove_all_sendings() {
		for(int i=0; i < get_sendings().size(); i++) {
			try {
				PAS.get_pas().get_eastcontent().get_sendingpanel().remove(((SendObject)get_sendings().get(i)).get_toolbar());
			} catch(Exception e) {
				Error.getError().addError(PAS.l("common_warning"), "Error removing sending. Non-fatal Error in SendController", e, 2);
			}
		}
		get_sendings().clear();
	}
	
	public String m_sz_projectpk = "-1";
	public void setActiveProject(Project p) {
		m_sz_projectpk = p.get_projectpk();
		m_b_ignore_project = true;
	}
	public void resetActiveProject() {
		m_sz_projectpk = "-1";
		m_b_ignore_project = false;
		remove_all_sendings();
	}
	
	public SendController(PAS pas) {
		m_pas = pas;
		//m_sendsetup = new Send(this);
		m_sendings = new ArrayList<SendObject>();
		m_btngroup_activate = new ButtonGroup();
		//m_polypoint_popup = new PUPolyPoint(m_pas, "Menu", this);
	}
	
	public SendController() {
		m_sendings = new ArrayList<SendObject>();
		m_btngroup_activate = new ButtonGroup();
	}
	
	public boolean insert_alert_sending(AlertVO alert) {
		create_new_sending(alert);
		return true;
	}
	public boolean insert_event_sending(EventVO event) {
		Iterator i = event.getAlertListe().iterator();
		while(i.hasNext()) {
			AlertVO alert = (AlertVO)i.next();
			insert_alert_sending(alert);
		}
		return true;
	}
	public boolean insert_tas_sending(ULBACOUNTRY c)
	{
		List<ULBACOUNTRY> l = new ArrayList<ULBACOUNTRY>();
		l.add(c);
		create_tas_sending(l);
		return true;
	}
	public boolean insert_tas_multiple_sending(List<ULBACOUNTRY> l)
	{
		create_tas_sending(l);
		return true;
	}
	public SendObject createSendingFromAlert(AlertVO alert) {
		int n_type = -1;
		if(alert.getM_shape().getClass().equals(PolygonStruct.class))
			n_type = SendProperties.SENDING_TYPE_POLYGON_;
		else if(alert.getM_shape().getClass().equals(EllipseStruct.class))
			n_type = SendProperties.SENDING_TYPE_CIRCLE_;
		else if(alert.getM_shape().getClass().equals(GISShape.class)) {
			n_type = SendProperties.SENDING_TYPE_GEMINI_STREETCODE_;
			// Tror det er her jeg må kjøre på å lage temp fil med alle kodene også hente dem ned
		}
		else if(alert.getM_shape().getClass().equals(MunicipalStruct.class))
			n_type = SendProperties.SENDING_TYPE_MUNICIPAL_;
		if(n_type==-1)
			Error.getError().addError(PAS.l("common_error"), "Could not identify sending type = -1", 0, 1);
			//else if(alert.getM_shape().getClass().equals(UnknownShape.class))
			//n_type = SendProperties.SENDING_TYPE_ADRLIST_;
		//else if(alert.getM_shape().getClass().equals()) //GIS import
		SendObject obj = new SendObject(alert.getName(), n_type, m_n_send_id, this, Variables.getNavigation());
		//obj.set_sendwindow(new SendWindow(this));
		
		try {
			if(alert.getM_shape() != null) {
				if(alert.getM_shape().getType() == ShapeStruct.SHAPE_POLYGON)
					obj.get_sendproperties().set_shapestruct((PolygonStruct)alert.getM_shape().clone());
				else if(alert.getM_shape().getType() == ShapeStruct.SHAPE_ELLIPSE){
					obj.get_sendproperties().set_shapestruct((EllipseStruct)alert.getM_shape().clone());
					//obj.get_toolbar().m_radio_sendingtype_ellipse.doClick();
					//obj.set_type(SendProperties.SENDING_TYPE_CIRCLE_);
				}
				else if(alert.getM_shape().getType() == ShapeStruct.SHAPE_GISIMPORT) {
					obj.get_sendproperties().set_shapestruct((GISShape)alert.getM_shape().clone());
					PreviewFrame pf = new PreviewFrame(((GISShape)alert.getM_shape()).get_gislist(), obj);
					//obj.get_toolbar().add(pf.get_previewpanel().get_loader());
				}
				else if(alert.getM_shape().getType() == ShapeStruct.SHAPE_MUNICIPAL) {
					obj.get_sendproperties().set_shapestruct((MunicipalStruct)alert.getM_shape().clone());
					
				}
			}
		} catch(Exception e) {
			Error.getError().addError(PAS.l("common_error"), "Could not clone Alert polygon", e, Error.SEVERITY_ERROR);
		}
		obj.get_toolbar().set_addresstypes(alert.getAddresstypes());
		obj.get_toolbar().init_addresstypes(alert.getAddresstypes());
		obj.get_sendproperties().set_sendingname(alert.getName(), alert.getDescription());
		obj.get_sendproperties().set_validity(alert.getValidity());
		obj.get_sendproperties().set_oadc_number(alert.getOadc());
		obj.get_sendproperties().set_profilepk(alert.getProfilepk());
		obj.get_sendproperties().set_schedprofilepk(alert.getSchedpk());
		obj.get_sendproperties().set_maxchannels(alert.getMaxChannels());
		obj.get_sendproperties().set_sms_broadcast_message(alert.get_sms_message());
		obj.get_sendproperties().set_sms_broadcast_oadc(alert.get_sms_oadc());
		
		//obj.setLocked(true);

		if(alert != null && alert.getArea() != null) {
				//if(obj.get_sendproperties().get_cell_broadcast_text() == null) {
			//SendWindow sw = new SendWindow(this, obj, obj.get_sendproperties().get_cell_broadcast_text(), obj.get_sendproperties().get_sms_broadcast_text());
			//SendWindow sw = new SendWindow(this, obj, null, null);
				obj.get_sendproperties().set_cell_broadcast_text(new Sending_Cell_Broadcast_text(m_pas, obj.get_sendwindow()));
				//obj.get_sendproperties().set_cell_broadcast_text(new Sending_Cell_Broadcast_text(m_pas, sw));
				//}
				//obj.get_sendproperties().get_cell_broadcast_text().get_txt_localtext().setText(alert.getLocalLanguage()[1]);
				//obj.get_sendproperties().get_cell_broadcast_text().set_size_label(alert.getLocalLanguage()[1], obj.get_sendproperties().get_cell_broadcast_text().get_lbl_localsize());
				//obj.get_sendproperties().get_cell_broadcast_text().get_txt_internationaltext().setText(alert.getInternationalLanguage()[1]);
				//obj.get_sendproperties().get_cell_broadcast_text().set_size_label(alert.getInternationalLanguage()[1], obj.get_sendproperties().get_cell_broadcast_text().get_lbl_internationalsize());
			obj.get_sendproperties().get_cell_broadcast_text().get_combo_area().setSelectedItem(alert.getArea());
			obj.get_sendproperties().get_cell_broadcast_text().set_expiryMinutes(alert.get_LBAExpiry());
			obj.get_sendproperties().get_cell_broadcast_text().set_requesttype(alert.getRequestType());
			//obj.get_sendproperties().get_cell_broadcast_text().get_txt_oadc_text().setText(alert.getCBOadc());
			for(int i=0;i<alert.getCBMessages().size();i++)
				obj.get_sendproperties().get_cell_broadcast_text().get_cbx_messages().addItem(alert.getCBMessages().get(i));
		
		}
		
		return obj;
	}
	
	public SendObject create_new_sending() {
		return create_new_sending(null);
	}
	
	protected SendObject create_tas_sending(List<ULBACOUNTRY> c)
	{
		try
		{
			if(!CheckForProject())
				return null;
			SendObject obj;
			obj = new SendObject("New TAS sending", SendProperties.SENDING_TYPE_TAS_COUNTRY_, m_n_send_id, this, Variables.getNavigation());
			obj.set_type(SendProperties.SENDING_TYPE_TAS_COUNTRY_);
			obj.get_sendproperties().get_toolbar().set_addresstypes(SendController.SENDTO_TAS_SMS);
			obj.get_sendproperties().typecast_tas().setCountryList(c);
			set_activesending(obj);
			
			actionPerformed(new ActionEvent(obj, ActionEvent.ACTION_PERFORMED, "act_send_one"));
			obj.get_sendwindow().get_sms_broadcast_text().m_maxSize = 160;
			return obj;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	protected boolean CheckForProject()
	{
		try {
			int action = -1;
			if(!m_b_ignore_project && PAS.get_pas().get_current_project() == null) {
			//if(!m_b_ignore_project) {
				action = PAS.get_pas().invoke_project(true);
				if(action == ProjectDlg.ACT_PROJECTDLG_CLOSE)
					return false;
				//PAS.get_pas().actionPerformed(new ActionEvent("", ActionEvent.ACTION_PERFORMED, "act_invoke_project"));
			}
			m_b_ignore_project = true;
		} catch(Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			Error.getError().addError(PAS.l("common_error"),"SendController Exception in create_new_sending",e,1);
			return false;
		}
		return true;
		
	}
	
	public SendObject create_new_sending(AlertVO alert) {
		try {
			try {
				if(!CheckForProject())
					return null;
			} catch(Exception e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
				Error.getError().addError(PAS.l("common_error"),"SendController Exception in create_new_sending",e,1);
			}

				SendObject obj;
				if(alert!=null) {
					obj = createSendingFromAlert(alert);
				} else {
					obj = new SendObject("New sending", SendProperties.SENDING_TYPE_POLYGON_, m_n_send_id, this, Variables.getNavigation());
				}
			m_n_send_id++;
			add_sending(obj);
			System.out.println("New sending created");
			return obj;
			
		} catch(Exception err) {
			System.out.println(err.getMessage());
			err.printStackTrace();
			Error.getError().addError(PAS.l("common_error"),"SendController Exception in create_new_sending",err,1);
			return null;
		}
	}
	public SendObject create_resend(StatusSending sending) {
		try {
			SendObject obj;
			obj = new SendObject("Resend refno " + sending.get_refno(), sending.get_sendingtype(), m_n_send_id, this, Variables.getNavigation());
			switch(sending.get_sendingtype()) {
				case SendProperties.SENDING_TYPE_POLYGON_:
					SendPropertiesPolygon poly = new SendPropertiesPolygon((PolygonStruct)sending.get_polygon().clone(), obj.get_toolbar(), new Col(new Color(0, 0, 0), new Color(255, 0, 0)));
					poly.set_resend(sending.get_refno());
					obj.set_sendproperties(poly);
					obj.get_toolbar().set_addresstypes(sending.get_addresstypes());
					break;
				case SendProperties.SENDING_TYPE_CIRCLE_:
					SendPropertiesEllipse ell = new SendPropertiesEllipse((EllipseStruct)sending.get_ellipse().clone(), obj.get_toolbar(), new Col(new Color(0, 0, 0), new Color(255, 0, 0)));
					ell.set_resend(sending.get_refno());
					obj.set_sendproperties(ell);
					obj.get_toolbar().set_addresstypes(sending.get_addresstypes());
					break;
				case SendProperties.SENDING_TYPE_GEMINI_STREETCODE_:
					SendPropertiesGIS gis = new SendPropertiesGIS(obj.get_toolbar());
					gis.set_resend(sending.get_refno());
					obj.set_sendproperties(gis);
					obj.get_toolbar().set_addresstypes(sending.get_addresstypes());
					break;
				case SendProperties.SENDING_TYPE_MUNICIPAL_:
					SendPropertiesMunicipal mun = new SendPropertiesMunicipal((MunicipalStruct)sending.get_municipal().clone(), obj.get_toolbar());
					mun.set_resend(sending.get_refno());
					obj.set_sendproperties(mun);
					obj.get_toolbar().set_addresstypes(sending.get_addresstypes());
					break;
				case SendProperties.SENDING_TYPE_TAS_COUNTRY_:
					SendPropertiesTAS tas = new SendPropertiesTAS(obj.get_toolbar());
					System.out.println("TAS created objid: " + System.identityHashCode(tas));
					TasStruct ts = sending.get_shape().typecast_tas();
					tas.set_resend(sending.get_refno());
					obj.set_sendproperties(tas);
					obj.get_toolbar().set_addresstypes(sending.get_addresstypes());
					tas.setCountryList(ts.getCountryList());
					tas.setSmsInStats(sending.getSmsInStats());
					tas.set_sms_broadcast_message(sending.get_sms_message_text());
					tas.set_sms_broadcast_oadc(sending.getLbaLanguages().get(0).getSzCbOadc()); // Eneste måten jeg fant for å hente ut oadc ved tas resend siden sending.get_oadc() bare gir N/A
					break;
			}
			System.out.println(sending.get_addresstypes());
			// Få tak i gislist
			//PAS.get_pas().get_statuscontroller().get_houses().get_houses().size();
			obj.get_toolbar().init_addresstypes(sending.get_addresstypes());
			obj.get_sendproperties().set_profilepk(sending.get_profilepk());
			obj.get_sendproperties().set_oadc_number(sending.get_oadc());
			obj.get_sendproperties().set_sendchannels(sending.get_type());
			if(obj.get_sendproperties().get_sendingtype() == SendProperties.SENDING_TYPE_TAS_COUNTRY_)
				obj.get_sendproperties().set_sendingname(PAS.l("main_tas_panel_new_message")+" - " + sending.get_sendingname() + "(" + sending.get_refno() + ")", PAS.l("main_resend_default_sendingname") + " - " + sending.get_refno());
			else
				obj.get_sendproperties().set_sendingname(PAS.l("main_resend_default_sendingname")+" - " + sending.get_sendingname() + "(" + sending.get_refno() + ")", PAS.l("main_resend_default_sendingname") + " - " + sending.get_refno());
			actionPerformed(new ActionEvent(obj, ActionEvent.ACTION_PERFORMED, "act_send_one"));
			//m_n_send_id++;
			//add_sending(obj);
			return obj;
		} catch(Exception err) {
			Error.getError().addError(PAS.l("common_error"), "SendController Exception in create_resend", err, 1);
			return null;
		}
	}
	public synchronized void actionPerformed(ActionEvent e) {
		if("act_new_sending".equals(e.getActionCommand())) {
			create_new_sending();
			get_pas().get_mappane().set_mode(MapFrame.MAP_MODE_SENDING_POLY);
		}
		else if("act_resend".equals(e.getActionCommand())) {
			StatusSending sending = (StatusSending)e.getSource();
			create_resend(sending);
		}

		else if("act_register_activation_btn".equals(e.getActionCommand())) {
			get_btngroup_activate().add((JRadioButton)e.getSource());
		}
		else if("act_activate_sending".equals(e.getActionCommand())) {
			set_activesending((SendObject)e.getSource());
		}
		else if("act_send_one".equals(e.getActionCommand())) { //send one single sending (not many in a project)
			try {
				SendObject obj = (SendObject)e.getSource();
				this.set_activesending(obj); // Setter denne her pga expiry valgene skal bli lagt til for TAS
				SendWindow win = new SendWindow(this, obj, obj.get_sendproperties().get_cell_broadcast_text(), obj.get_sendproperties().get_sms_broadcast_text());
				win.setLocation(no.ums.pas.ums.tools.Utils.get_dlg_location_centered(350, 300));
			} catch(Exception err) {
				PAS.get_pas().add_event("ERROR SendController.act_send_one - " + err.getMessage(), err);
				Error.getError().addError(PAS.l("common_error"),"SendController Exception in actionPerformed",err,1);
			}
		}
		else if("act_sending_close".equals(e.getActionCommand())) {
			SendObject so = (SendObject)e.getSource();
			ShapeStruct ss = so.get_sendproperties().get_shapestruct();
			if(ss.equals(Variables.getMapFrame().get_active_shape()))
				Variables.getMapFrame().set_active_shape(null);
			//((SendObject)e.getSource()).destroy_all();
			so.destroy_all();
			get_sendings().remove(((SendObject)e.getSource()));
			PAS.get_pas().kickRepaint();
			//PAS.get_pas().get_mappane().set_mode(MapFrame.MAP_MODE_PAN);
			//Integer n_mode = new Integer(MapFrame.MAP_MODE_PAN);
			//actionPerformed(new ActionEvent(n_mode, ActionEvent.ACTION_PERFORMED, "act_set_mappane_mode"));
			PAS.get_pas().get_mainmenu().set_pan();
			//PAS.get_pas().get_eastcontent().get_sendingpanel().updateUI();
			//PAS.get_pas().get_eastcontent().get_sendingpanel().revalidate();

		}
		else if("act_polygon_imported_eof".equals(e.getActionCommand())) {
			SosiFile f = (SosiFile)e.getSource();
			Variables.getNavigation().gotoMap(f.get_flater().get_current_flate().get_polygon().calc_bounds());
		}
		else if("act_gis_imported_eof".equals(e.getActionCommand())) {
			PAS.get_pas().add_event("act_gis_imported_eof", null);
		}
		else if("act_set_mappane_mode".equals(e.getActionCommand())) {
			PAS.get_pas().get_mappane().set_mode(((Integer)e.getSource()).intValue());
			PAS.get_pas().kickRepaint();
		}
		else if("act_set_addresstypes".equals(e.getActionCommand())) {
			if(e.getSource().getClass().equals(ToggleAddresstype.class))
			{
				/*if(((ToggleAddresstype)e.getSource()).isSelected())
					PAS.get_pas().get_housecontroller().add_addresstype(((ToggleAddresstype)e.getSource()).get_adrtype());
				else
					PAS.get_pas().get_housecontroller().rem_addresstype(((ToggleAddresstype)e.getSource()).get_adrtype());*/
				try
				{
					int adrtypes = get_activesending().get_sendproperties().get_addresstypes();
					PAS.get_pas().get_housecontroller().set_addresstypes(adrtypes);
				}
				catch(Exception err) { 
					err.printStackTrace();
				}
			}
			else if(e.getSource().getClass().equals(CheckItem.class))
			{
				
			}
			PAS.get_pas().kickRepaint();
		}
		else if("act_map_goto_area".equals(e.getActionCommand())) {
			PAS.get_pas().actionPerformed(e);
		}
		else if("act_kick_repaint".equals(e.getActionCommand())) {
			PAS.get_pas().kickRepaint();
		}
		else if("act_set_active_shape".equals(e.getActionCommand())) {
			PAS.get_pas().actionPerformed(e); //send active shape to MapFrame
		}
	}
	
	
	public void add_sending(final SendObject obj) {
		try
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					try
					{
						if(PAS.get_pas() != null) {
							PAS.get_pas().get_eastcontent().flip_to(EastContent.PANEL_SENDING_);
							if(PAS.get_pas().get_eastcontent().get_sendingpanel()!=null &&
									obj.get_toolbar()!=null)
								PAS.get_pas().get_eastcontent().get_sendingpanel().add(obj.get_toolbar());		
						}
						get_sendings().add(obj);
						set_activesending(obj);
						if(PAS.get_pas() != null)
							PAS.get_pas().repaint();
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
			});
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public void set_activesending(SendObject obj) {
		if(get_activesending()!=null)
			get_activesending().setActive(false);
		//m_n_activesending_number = get_sendings().size() - 1;		
		m_active_sending = obj;
		get_activesending().setActive(true);
		m_active_sending.activate_sendwindow();
		try {
			if(!obj.get_toolbar().getClass().equals(SendOptionToolbar.class))
				show_addresstypes(obj.get_sendproperties().get_addresstypes());
		} catch(Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			Error.getError().addError(PAS.l("common_error"),"SendController Exception in set_activesending",e,1);
		}
		if(!m_active_sending.isLocked()) {
			if(get_pas() != null)
				get_pas().get_mainmenu().actionPerformed(new ActionEvent(obj, ActionEvent.ACTION_PERFORMED, "act_draw"));
		}
		else
			get_pas().get_mainmenu().set_pan();
		if(get_pas() != null)
			PAS.get_pas().kickRepaint();
	}
	public void show_addresstypes(int n_types) {
		try {
			PAS.get_pas().get_housecontroller().show_addresstypes(n_types);
		} catch(Exception e) {
			System.out.println(e.getMessage());
			Error.getError().addError(PAS.l("common_error"),"SendController Exception in show_addresstypes",e,1);
		}
	}
	
	/*
	 * Check points from all polygons, snap within distance d
	 */
	public PolySnapStruct snap_to_point(Point p1, int d) {
		PolySnapStruct snap = null, snaptemp = null;		
		for(int i=0; i < get_sendings().size(); i++) {
			SendObject obj = (SendObject)get_sendings().get(i);
			long n_distance = 0;
			//Point p2 = null;
			//if((snap = obj.snap_to_point(p1, d)) != null) 
			snaptemp = obj.snap_to_point(p1, d);
			if(snaptemp != null) {
				snap = snaptemp;
				if(snap.isActive())//prioritize active polygon
					return snap;
			}
		}
		return snap;
	}
	public void draw_polygons(Graphics g, Point mousepos) {
		//get_pas().add_event("draw_polygons " + get_sendings().size());
		try {
			for(int i=0; i < get_sendings().size(); i++) {
				SendObject obj = (SendObject)get_sendings().get(i);
				if(obj.get_sendproperties().get_shapestruct()==PAS.get_pas().get_mappane().get_active_shape())
					continue;
				if(obj!=null)
					obj.draw(g, mousepos);
			}
		} catch(Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			Error.getError().addError(PAS.l("common_error"),"SendController Exception in draw_polygons",e,1);
		}
	}
	
	//Object o kan være enten alert eller event
	JPasswordField pass = new JPasswordField("");
	public void execSoapSnapSendings(Object o, String sz_function)
	{
		/*boolean livesending = false;
		if(sz_function.equals("live"))
			livesending = true;*/
		
		pass.setText("");
		pass.setPreferredSize(new Dimension(150, 16));
		pass.setEchoChar('*');
		LightPanel panel = new LightPanel();
		
		LoadingFrame loader = new LoadingFrame("[" + sz_function.toUpperCase() + "] "+PAS.l("common_preparing") + " ", null);
		loader.setAlwaysOnTop(true);
		//loader.set_totalitems(0, "Sending");
		
		no.ums.ws.pas.ULOGONINFO logon = new no.ums.ws.pas.ULOGONINFO();
		UserInfo u = PAS.get_pas().get_userinfo();
		logon.setLComppk(u.get_comppk());
		logon.setLDeptpk(u.get_current_department().get_deptpk());
		logon.setLUserpk(new Long(u.get_userpk()));
		logon.setSzCompid(u.get_compid());
		logon.setSzUserid(u.get_userid());
		logon.setSzDeptid(u.get_current_department().get_deptid());
		logon.setSzPassword(u.get_passwd());
		logon.setSzStdcc(u.get_current_department().get_stdcc());
		logon.setLDeptpk(u.get_current_department().get_deptpk());
		logon.setSessionid(u.get_sessionid());
		String sz_job = WSThread.GenJobId();
		logon.setJobid(sz_job);
		
		no.ums.ws.parm.ULOGONINFO logon_exec = new ULOGONINFO();
		logon_exec.setLComppk(logon.getLComppk());
		logon_exec.setLDeptpk(logon.getLDeptpk());
		logon_exec.setLUserpk(logon.getLUserpk());
		logon_exec.setSzCompid(logon.getSzCompid());
		logon_exec.setSzUserid(logon.getSzUserid());
		logon_exec.setSzDeptid(logon.getSzDeptid());
		logon_exec.setSzPassword(logon.getSzPassword());
		logon_exec.setSzStdcc(logon.getSzStdcc());
		logon_exec.setLDeptpk(logon.getLDeptpk());
		logon_exec.setJobid(sz_job);
		logon_exec.setSessionid(logon.getSessionid());

		WSProgressPoller progress = new WSProgressPoller(loader, ProgressJobType.PARM_SEND, logon, "", "Finished", true);
		String cnf;
		SoapExecAlert.SnapAlertResults res = null;
		boolean b = false;
		
		
		//ExecResponse response;
		no.ums.pas.core.logon.UserInfo ui = PAS.get_pas().get_userinfo();
		//String sz_execute_asmx = PAS.get_pas().get_sitename() + "/ExecAlert/WS/ExternalExec.asmx";
		
		java.net.URL wsdl;
		try
		{			
			//wsdl = new java.net.URL("http://localhost/WS/ExternalExec.asmx?WSDL");
			wsdl = new java.net.URL(vars.WSDL_EXTERNALEXEC); //PAS.get_pas().get_sitename() + "/ExecAlert/WS/ExternalExec.asmx?WSDL"); 
		} catch(Exception e)
		{
			Error.getError().addError(PAS.l("common_error"), e.getLocalizedMessage(), e, 1);
			return ;
		}
		QName service = null;
		Parmws myService = null;
		try
		{
			service = new QName("http://ums.no/ws/parm/", "parmws");
			myService = new Parmws(wsdl, service); //wsdlLocation, new QName("https://secure.ums2.no/vb4utv/ExecAlert/ExternalExec.asmx"));
		}
		catch(Exception e)
		{
			Error.getError().addError(PAS.l("common_error"), "Could not run Quick Send", e, Error.SEVERITY_ERROR);
			return ;
		}
	
		
		
		if(o.getClass().equals(AlertVO.class))
		{
			AlertVO a = (AlertVO)o;
			
			cnf = "<html>";
			if(sz_function.equals("live"))
				cnf += "<font color=\"red\">";
			//cnf += "Are you sure you want to send the Alert [" + a.getName() + "] as a <u>[" + sz_function.toUpperCase() + "]</u> sending?<br><br>Confirm using your password<br>";
			cnf += String.format(PAS.l("quicksend_alert_dlg_are_you_sure"), a.getName(),sz_function.toUpperCase() );
			cnf += "</font>";
			cnf += "</html>";
			JLabel label = new JLabel(cnf);
			panel.add(label, panel.m_gridconst);
			panel.set_gridconst(0, panel.inc_panels(), 1, 1);
			panel.add(pass, panel.m_gridconst);
			
			panel.set_gridconst(0, panel.inc_panels(), 1, 1);
			if(sz_function.equals("live"))
				panel.add(new JLabel(String.format(PAS.l("quicksend_alert_dlg_confirm"),"LIVE")), panel.m_gridconst);
			else
				panel.add(new JLabel(String.format(PAS.l("quicksend_alert_dlg_confirm"),"SIMULATE")), panel.m_gridconst);
			panel.set_gridconst(0, panel.inc_panels(), 1, 1);
			StdTextArea confirm = new StdTextArea("",false);
			confirm.setPreferredSize(new Dimension(150,16));
			panel.add(confirm, panel.m_gridconst);
			panel.set_gridconst(0, panel.inc_panels(), 1, 1);
			
			SwingUtilities.invokeLater(new Runnable() {
			      public void run() {
			    	  pass.requestFocus(false);
			    	  pass.requestFocusInWindow();
			      }
			});
			

			int dlg_ret = JOptionPane.showConfirmDialog(PAS.get_pas(), panel, PAS.l("common_confirm"), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
			if(dlg_ret != JOptionPane.YES_OPTION)
			{
				JOptionPane.showMessageDialog(PAS.get_pas(), PAS.l("quicksend_dlg_aborted"), PAS.l("common_information"), JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			try {
				
				String pwd;
				try {
					pwd = Utils.encrypt(new String(pass.getPassword()));
				}
				catch (Exception e){
					pwd="";
				}
				if(!pwd.equals(logon.getSzPassword())) {
					JOptionPane.showMessageDialog(PAS.get_pas(), PAS.l("quicksend_dlg_error_password"), PAS.l("common_information"), JOptionPane.INFORMATION_MESSAGE);
					return;
				}
				
				if(sz_function.equals("live")) {
					if(!confirm.getText().equals("LIVE")) {
						JOptionPane.showMessageDialog(PAS.get_pas(), String.format(PAS.l("quicksend_alert_dlg_confirm_err"),confirm.getText(), "LIVE"), PAS.l("common_information"), JOptionPane.INFORMATION_MESSAGE);
						return;
					}
				}
				else {
					if(!confirm.getText().equals("SIMULATE")) {
						JOptionPane.showMessageDialog(PAS.get_pas(), String.format(PAS.l("quicksend_alert_dlg_confirm_err"),confirm.getText(), "SIMULATE"), PAS.l("common_information"), JOptionPane.INFORMATION_MESSAGE);
						return;
					}
				}
						
				loader.setLocation(no.ums.pas.ums.tools.Utils.get_dlg_location_centered(300, 100));
				loader.setVisible(true);
				//ExecResponse ar = myService.getParmwsSoap12().execAlertV2(Long.parseLong(a.getAlertpk().substring(1)), ui.get_comppk(), ui.get_current_department().get_deptpk(), Long.parseLong(ui.get_userpk()), 
				//		ui.get_compid(), ui.get_userid(), ui.get_current_department().get_deptid(), 
				//		new String(pass.getPassword()), sz_function, "0", "0");
				progress.start();
				System.out.println("AlertPK: " + a.getAlertpk().substring(1));
				System.out.println("Logon: " + logon_exec.toString());
				System.out.println("sz_function: " + sz_function);
				ExecResponse ar = myService.getParmwsSoap12().execAlertV3(Long.parseLong(a.getAlertpk().substring(1)), logon_exec, sz_function, "0", "0");
				//ExecResponse ar = myService.getParmwsSoap12().execAlertV3(Long.parseLong(a.getAlertpk().substring(1)), logon_exec, sz_function, no.ums.pas.ums.tools.Utils.get_current_date_formatted(), Long.toString(no.ums.pas.ums.tools.Utils.get_current_datetime()).substring(8,14));
				res = new SoapExecAlert("0", "0", null).newSnapAlertResult();
				res.l_execpk = ar.getLExecpk();
				
					
				//res.l_projectpk = ar.getLProjectpk();
				res.setProjectPk(ar.getLProjectpk());
				
				res.sz_sendfunction = ar.getSzFunction();
				Iterator it = ar.getArrAlertresults().getAlertResultLine().iterator();
				while(it.hasNext())
				{
					AlertResultLine line = (AlertResultLine)it.next();
					res.addExecAlertResult(new Long(line.getLAlertpk()).toString(), line.getSzName(), new Integer(line.getLRefno()).toString(), line.getSzResult(), line.getSzText(), line.getSzExtendedInfo());
				}
				b = true;

				/*if(!res.hasOneOrMoreSuccess()) {
					String error = "";
					for(int i=0;i<ar.getArrAlertresults().getAlertResultLine().size();++i) {
						AlertResultLine arl = ar.getArrAlertresults().getAlertResultLine().get(i);
						if(arl.getLResultCode() == 0)
							error += arl.getSzName() + ", " + arl.getLAlertpk() + ": " + arl.getSzText() + "\n";
					}
					throw new Exception(error);
				}*/
				
			}
			catch(Exception e)
			{
				no.ums.pas.ums.errorhandling.Error.getError().addError(PAS.l("common_error"), e.getMessage(), e, 1);
				System.out.println(e.getMessage());
				loader.setVisible(false);
				return;
			}
		}
		else if(o.getClass().equals(EventVO.class))
		{
			EventVO a = (EventVO)o;
									
			cnf = "<html>";
			if(sz_function.equals("live"))
				cnf += "<font color=\"red\">";
			//cnf += "Are you sure you want to send the Event [" + a.getName() + "] as a <u>[" + sz_function.toUpperCase() + "]</u> sending?<br>Containing " + a.getAlertListe().size() + " alerts<br><br>Confirm using your password<br>";
			cnf += String.format(PAS.l("quicksend_event_dlg_are_you_sure"), a.getName(), sz_function.toUpperCase(), a.getAlertListe().size());
			cnf += "</font>";
			cnf += "</html>";
			JLabel label = new JLabel(cnf);
			panel.add(label, panel.m_gridconst);
			panel.set_gridconst(0, panel.inc_panels(), 1, 1);
			panel.add(pass, panel.m_gridconst);
			
			panel.set_gridconst(0, panel.inc_panels(), 1, 1);
			if(sz_function.equals("live"))
				panel.add(new JLabel(String.format(PAS.l("quicksend_alert_dlg_confirm"),"LIVE")), panel.m_gridconst);
			else
				panel.add(new JLabel(String.format(PAS.l("quicksend_alert_dlg_confirm"),"SIMULATE")), panel.m_gridconst);
			panel.set_gridconst(0, panel.inc_panels(), 1, 1);
			StdTextArea confirm = new StdTextArea("",false);
			confirm.setPreferredSize(new Dimension(150,16));
			panel.add(confirm, panel.m_gridconst);
			panel.set_gridconst(0, panel.inc_panels(), 1, 1);
			
			SwingUtilities.invokeLater(new Runnable() {
			      public void run() {
			    	  pass.requestFocus(false);
			    	  pass.requestFocusInWindow();
			      }
			});

			int dlg_ret = JOptionPane.showConfirmDialog(PAS.get_pas(), panel, PAS.l("common_confirm"), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
			if(dlg_ret != JOptionPane.YES_OPTION)
			{
				JOptionPane.showMessageDialog(PAS.get_pas(), PAS.l("quicksend_dlg_aborted"), PAS.l("common_information"), JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			try {
				if(sz_function.equals("live")) {
					if(!confirm.getText().equals("LIVE")) {
						JOptionPane.showMessageDialog(PAS.get_pas(), String.format(PAS.l("quicksend_alert_dlg_confirm_err"),confirm.getText(), "LIVE"), PAS.l("common_information"), JOptionPane.INFORMATION_MESSAGE);
						return;
					}
				}
				else {
					if(!confirm.getText().equals("SIMULATE")) {
						JOptionPane.showMessageDialog(PAS.get_pas(), String.format(PAS.l("quicksend_alert_dlg_confirm_err"),confirm.getText(), "SIMULATE"), PAS.l("common_information"), JOptionPane.INFORMATION_MESSAGE);
						return;
					}
				}
				loader.setVisible(true);
				/*System.out.println("SOAP = " + sz_execute_asmx);
				SoapExecEvent soap = new SoapExecEvent(a.getEventPk(), sz_function, PAS.get_pas().get_userinfo());
				b = soap.post(sz_execute_asmx, new String(pass.getPassword()), "0", "0");
				res = soap.getResults();*/
				progress.start();
				ExecResponse ar = myService.getParmwsSoap12().execEventV2(Long.parseLong(a.getEventPk().substring(1)), ui.get_comppk(), ui.get_current_department().get_deptpk(), Long.parseLong(ui.get_userpk()), 
						ui.get_compid(), ui.get_userid(), ui.get_current_department().get_deptid(), 
						new String(pass.getPassword()), sz_function, "0", "0");
				res = new SoapExecAlert("0", "0", null).newSnapAlertResult();
				res.l_execpk = ar.getLExecpk();
				//res.l_projectpk = ar.getLProjectpk();
				res.setProjectPk(ar.getLProjectpk());
				
				res.sz_sendfunction = ar.getSzFunction();
				Iterator it = ar.getArrAlertresults().getAlertResultLine().iterator();
				while(it.hasNext())
				{
					AlertResultLine line = (AlertResultLine)it.next();
					res.addExecAlertResult(new Long(line.getLAlertpk()).toString(), line.getSzName(), new Integer(line.getLRefno()).toString(), line.getSzResult(), line.getSzText(), line.getSzExtendedInfo());
				}
				b = true;				
			}
			catch(Exception e)
			{
				no.ums.pas.ums.errorhandling.Error.getError().addError(PAS.l("common_error"), e.getMessage(), e, 1);
				System.out.println(e.getMessage());
				loader.setVisible(false);
				return;
			}
		}
		progress.SetFinished();
		loader.setVisible(false);
		if(b)
		{
			//if(res.hasProject())
			showSnapResults(res, true);
		}
	}
	
	
	protected void showSnapResults(final SoapExecAlert.SnapAlertResults res, boolean b_openstatus_question)
	{
		if(res.hasProject() && res.hasOneOrMoreSuccess())
		{
			if(PAS.get_pas().get_rightsmanagement().status())
			{
				Component parent_to_popup = PAS.get_pas();
				try
				{
					if(PAS.get_pas().get_sendcontroller().get_activesending()!=null && PAS.get_pas().get_sendcontroller().get_activesending().get_sendwindow()!=null)
						parent_to_popup = PAS.get_pas().get_sendcontroller().get_activesending().get_sendwindow();
				}
				catch(Exception e)
				{
					//a null pointer exception, use PAS mainframe as parent
				}
				if(res.getSendFunction().equals("test"))
				{
					JOptionPane.showMessageDialog(parent_to_popup, res.toString(b_openstatus_question), PAS.l("quicksend_dlg_results"), JOptionPane.INFORMATION_MESSAGE);
				}
				else
				{
					int answer= JOptionPane.NO_OPTION;
					if(PAS.get_pas().get_statuscontroller().get_sendinglist().size() > 0) {
						JOptionPane.showMessageDialog(parent_to_popup,res.toString(b_openstatus_question),PAS.l("quicksend_dlg_results"),JOptionPane.INFORMATION_MESSAGE);
						openStatus(res);
					}
					else if(b_openstatus_question && (answer = JOptionPane.showConfirmDialog(parent_to_popup, res.toString(), PAS.l("quicksend_dlg_results"), JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE))==JOptionPane.YES_OPTION)
						openStatus(res);
					else if(b_openstatus_question && answer==JOptionPane.NO_OPTION)
						get_activesending().get_sendwindow().dispose();
					else if(!b_openstatus_question)
						JOptionPane.showMessageDialog(parent_to_popup, res.toString(b_openstatus_question), PAS.l("quicksend_dlg_results"), JOptionPane.INFORMATION_MESSAGE);
				}
				return;
			}
		}
		if(PAS.get_pas().get_sendcontroller().get_activesending() != null)
			JOptionPane.showMessageDialog(PAS.get_pas().get_sendcontroller().get_activesending().get_sendwindow(), res.toString(b_openstatus_question), PAS.l("quicksend_dlg_results"), JOptionPane.ERROR_MESSAGE);
		else
			JOptionPane.showMessageDialog(PAS.get_pas(), res.toString(b_openstatus_question), PAS.l("quicksend_dlg_results"), JOptionPane.ERROR_MESSAGE);
	}
	
	private void openStatus(final SoapExecAlert.SnapAlertResults res) {
		//Open status?
		try
		{
			new Thread("Open status thread")
			{
				public void run()
				{	
					PAS.get_pas().close_active_project(true, false);
					PAS.get_pas().get_statuscontroller().retrieve_statusitems(PAS.get_pas().get_statuscontroller().get_statusframe(), res.getProjectpk(), -1, true /*init*/);
				}
			}.start();
			
			//get_activesending().get_sendwindow().actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED,"act_finish"));
			get_activesending().get_sendwindow().dispose();
						
		}
		catch(Exception e)
		{
			
		}
	}
	
}
