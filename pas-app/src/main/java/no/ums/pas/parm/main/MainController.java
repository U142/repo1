package no.ums.pas.parm.main;

import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.pas.PAS;
import no.ums.pas.ParmController;
import no.ums.pas.cellbroadcast.CBMessage;
import no.ums.pas.cellbroadcast.CCode;
import no.ums.pas.core.Variables;
import no.ums.pas.core.logon.UserInfo;
import no.ums.pas.core.mainui.LoadingFrame;
import no.ums.pas.core.menus.OtherActions;
import no.ums.pas.core.ws.vars;
import no.ums.pas.importer.gis.GISRecord;
import no.ums.pas.localization.Localization;
import no.ums.pas.maps.MapFrame;
import no.ums.pas.maps.defines.EllipseStruct;
import no.ums.pas.maps.defines.GISShape;
import no.ums.pas.maps.defines.MapPoint;
import no.ums.pas.maps.defines.MapPointLL;
import no.ums.pas.maps.defines.Navigation;
import no.ums.pas.maps.defines.PolygonStruct;
import no.ums.pas.maps.defines.ShapeStruct;
import no.ums.pas.maps.defines.UnknownShape;
import no.ums.pas.parm.alert.AlertController;
import no.ums.pas.parm.alert.AlertWindow;
import no.ums.pas.parm.event.EventController;
import no.ums.pas.parm.exception.ParmException;
import no.ums.pas.parm.map.MapController;
import no.ums.pas.parm.map.MapPanel;
import no.ums.pas.parm.object.ObjectController;
import no.ums.pas.parm.threads.UpdateXML;
import no.ums.pas.parm.tree.TreeController;
import no.ums.pas.parm.tree.TreeGUI;
import no.ums.pas.parm.voobjects.AlertVO;
import no.ums.pas.parm.voobjects.CategoryVO;
import no.ums.pas.parm.voobjects.EventVO;
import no.ums.pas.parm.voobjects.ObjectVO;
import no.ums.pas.parm.voobjects.ParmVO;
import no.ums.pas.parm.xml.ListSorter;
import no.ums.pas.parm.xml.XmlReader;
import no.ums.pas.parm.xml.XmlWriter;
import no.ums.pas.send.SendController;
import no.ums.pas.send.SendObject;
import no.ums.pas.ums.errorhandling.Error;
import no.ums.ws.common.ArrayOfLBACCode;
import no.ums.ws.common.LBACCode;
import no.ums.ws.common.LBALanguage;
import no.ums.ws.common.ULOGONINFO;
import no.ums.ws.common.parm.ArrayOfLBALanguage;
import no.ums.ws.common.parm.ArrayOfPAALERT;
import no.ums.ws.common.parm.ArrayOfUGisImportResultLine;
import no.ums.ws.common.parm.PAALERT;
import no.ums.ws.common.parm.PAEVENT;
import no.ums.ws.common.parm.PAOBJECT;
import no.ums.ws.common.parm.PARMOPERATION;
import no.ums.ws.common.parm.UEllipse;
import no.ums.ws.common.parm.UGeminiStreet;
import no.ums.ws.common.parm.UGisImportResultLine;
import no.ums.ws.common.parm.ULocationBasedAlert;
import no.ums.ws.common.parm.UPolygon;
import no.ums.ws.common.parm.UPolypoint;
import no.ums.ws.common.parm.UShape;
import no.ums.ws.parm.admin.ParmAdmin;
import no.ums.ws.parm.admin.UPAALERTRESTULT;
import no.ums.ws.parm.admin.UPAEVENTRESULT;
import no.ums.ws.parm.admin.UPAOBJECTRESULT;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.xml.namespace.QName;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class MainController implements ActionListener, TreeModelListener,
		TreeSelectionListener {

    private static final Log log = UmsLog.getLogger(MainController.class);

	private MainGUI gui;
	private TreeController treeCtrl = new TreeController(this);
	public TreeController getTreeCtrl() { return treeCtrl; }
	private ObjectController objectCtrl;
	DefaultMutableTreeNode remNode;
	private ObjectVO object;
	private AlertController alertCtrl;
	private AlertVO alert;
	private EventController eventCtrl;
	private EventVO event;
	private XmlReader xmlreader;
	private Collection<Object> objectList;
	private ArrayList<CategoryVO> allCategorys;
	private ArrayList<Object> allElements;
	private UpdateXML updXml;
	public UpdateXML getUpdateXML() { return updXml; }
	private MapController mapCtrl;
	private MapPanel map;
	public UserInfo userinfo;
	private ScrollPane treeScrollPane;
	public ScrollPane getTreeScrollPane() { return treeScrollPane; }
	public int tempPK;
	public HashMap<Long, CategoryVO> m_categories;
	public AlertController getAlertController() { return alertCtrl; }
	private ParmVO selectedObject = null;
	public ParmVO getSelectedObject() { return selectedObject; }
	
	public class ScrollPane extends JScrollPane implements AdjustmentListener {
		public static final long serialVersionUID = 1;
		public ScrollPane(TreeGUI gui) {
			super(gui);
			getVerticalScrollBar().addAdjustmentListener(this);
			getHorizontalScrollBar().addAdjustmentListener(this);
			getVerticalScrollBar().setUnitIncrement(15);
		}

		//make sure to repaint tree when scrolling has occured
		@Override
		public void adjustmentValueChanged(AdjustmentEvent e) {
			gui.repaint();
			getTreeCtrl().get_treegui().repaint();
		}
		
	}
	

	/*public MainController(String[] args) throws FileNotFoundException,
			ParmException {
		initMap(args);
		this.tempPK = 0;
		this.updXml = new UpdateXML(this, "temp");
		// this.updXml.start();
		this.createGUI();
		this.updXml.start();
		
	}*/
	public MainController(String sz_sitename, UserInfo userinfo) {
		//initMap(new String [] { sz_sitename } );
		this.tempPK = 0;
		this.userinfo = userinfo;
		this.updXml = new UpdateXML(this, "temp");
		// this.updXml.start();
		try {
			this.createGUI();
		} catch(Exception e) {
			Error.getError().addError("MainController","Exception in MainController",e,1);
		}
		treeCtrl.SetInitializing(true);
	}

    public void start() {
        this.updXml.start();
    }
	
	public void initGUI() {
		treeCtrl = new TreeController(this);
		treeCtrl.getTreeController(); // must run first to initiate the tree
		try {
			treeCtrl.initiateTree(this.getAllElementsFromXmlFile(), false);
		} catch(Exception e){
			log.debug(e.getMessage());
			log.warn(e.getMessage(), e);
			//Error.getError().addError("MainController","Exception in initGUI",e,1);
		}
		
		gui = new MainGUI();
		
		// instansiating the tree-ScrollPane..
		treeScrollPane = new ScrollPane(treeCtrl.getGui());
		treeScrollPane.setMinimumSize((new Dimension(200, 200)));

		//gui.getItmExit().addActionListener(this);
		gui.getEdit().addActionListener(this);
		gui.getDelete().addActionListener(this);
		gui.getObject().addActionListener(this);
		gui.getObjectfolder().addActionListener(this);
//		gui.getEvent().addActionListener(this);
//		gui.getAlert().addActionListener(this);
		gui.getRefresh().addActionListener(this);

		treeCtrl.getGui().getObjectfolder().addActionListener(this);
		treeCtrl.getGui().getTree().addMouseListener(treeCtrl.getMuseLytter());
		treeCtrl.getGui().getTree().addTreeSelectionListener(this);
		treeCtrl.getGui().getTreeModel().addTreeModelListener(this);
		treeCtrl.getGui().getObject().addActionListener(this);
		treeCtrl.getGui().getAlert().addActionListener(this);
		treeCtrl.getGui().getEvent().addActionListener(this);
		treeCtrl.getGui().getDelete().addActionListener(this);
		treeCtrl.getGui().getEdit().addActionListener(this);
		treeCtrl.getGui().getGotoMap().addActionListener(this);
		treeCtrl.getGui().getGenerateSending().addActionListener(this);
		treeCtrl.getGui().getSnapSending().addActionListener(this);
		treeCtrl.getGui().getSnapSimulation().addActionListener(this);
		treeCtrl.getGui().getSnapTest().addActionListener(this);
		treeCtrl.getGui().getExportPolygon().addActionListener(this);

	}
	
	public void createGUI() throws FileNotFoundException, ParmException {
		gui = new MainGUI();
		initGUI();
		gui.setTitle("Population Alert & Risc Management");
		gui.getMFile().setText("File");
		gui.getMView().setText("View");

		gui.getItmExit().setText("Exit");
		gui.getItmExit().setMnemonic(java.awt.event.KeyEvent.VK_X);

		gui.getMFile().add(gui.getMenuNew());
		gui.getMFile().add(gui.getEdit());
		gui.getEdit().setEnabled(false);
		gui.getDelete().setEnabled(false);
		gui.getMFile().add(gui.getDelete());
		gui.getMFile().addSeparator();
		gui.getMFile().add(gui.getItmExit());

		gui.getMView().add(gui.getRefresh());

		gui.getMenubar().add(gui.getMFile());
		gui.getMenubar().add(gui.getMView());


		// instansiating the map-ScrollPane..
		JScrollPane mapScrollPane = new JScrollPane(map);

		mapScrollPane.setMinimumSize((new Dimension(0, 0)));
		JSplitPane splitpane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				treeScrollPane, mapScrollPane);
		splitpane.setOneTouchExpandable(true);
		splitpane.setDividerLocation(250);
		splitpane.setContinuousLayout(true);

		gui.getContentPane().add(splitpane);
		gui.getItmExit().addActionListener(this);
		gui.getEdit().addActionListener(this);
		gui.getDelete().addActionListener(this);
		gui.getObject().addActionListener(this);
		gui.getObjectfolder().addActionListener(this);
//		gui.getEvent().addActionListener(this);
//		gui.getAlert().addActionListener(this);
		gui.getRefresh().addActionListener(this);

		treeCtrl.getGui().getObjectfolder().addActionListener(this);
		treeCtrl.getGui().getTree().addMouseListener(treeCtrl.getMuseLytter());
		treeCtrl.getGui().getTree().addTreeSelectionListener(this);
		treeCtrl.getGui().getTreeModel().addTreeModelListener(this);
		treeCtrl.getGui().getObject().addActionListener(this);
		treeCtrl.getGui().getAlert().addActionListener(this);
		treeCtrl.getGui().getEvent().addActionListener(this);
		treeCtrl.getGui().getDelete().addActionListener(this);
		treeCtrl.getGui().getEdit().addActionListener(this);
		treeCtrl.getGui().getGotoMap().addActionListener(this);
		treeCtrl.getGui().getGenerateSending().addActionListener(this);
		treeCtrl.getGui().getSnapSending().addActionListener(this);
		treeCtrl.getGui().getSnapSimulation().addActionListener(this);
		treeCtrl.getGui().getSnapTest().addActionListener(this);
		treeCtrl.getGui().getExportPolygon().addActionListener(this);

		gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		gui.setSize(1300, 900);
		gui.setVisible(true);
	}

	//OVERRIDE
	protected void showAlertShape(EventVO e) { //to be overridden
		showAlertShape(e, this.map);
	}
	public Navigation getMapNavigation() {
		return map.getM_navigation();
	}
	public Dimension getMapSize() {
		return map.getSize();
	}
	protected void clearDrawQueue() {
		map.clearDrawQueue();
	}
	protected void drawLayers() {
		map.drawLayers();
	}
	protected void addShapeToDrawQueue(ShapeStruct s) {
		//map.addShapeToDrawQueue(s);
		PAS.pasplugin.addShapeToPaint(s);
	}
	protected void mapRedraw() {
		map.redraw();
	}
	public void mapClear() { //override
		this.map.clear();
	}
	public void updateShape(ShapeStruct p) { //override
		this.map.updateShape(p);
	}
	public void updateShapeFilled(ShapeStruct p) { //override
		this.map.updateShape(p);
	}
	protected Graphics getMapGraphics() {
		return map.getGraphics();
	}
	public ShapeStruct getMapShape() {
		return map.getM_shape();
	}
	public void gotoMap() {
		
	}
	//ProgressMonitor monitor;
	LoadingFrame m_progress;
	public void endSession(boolean bShowProgress) {
		try {
			new Thread("PARM endSession thread") {
				public void run() {
                    m_progress = new LoadingFrame(Localization.l("main_parmtab_closing_parm"), null);
                    m_progress.set_totalitems(0, Localization.l("main_parmtab_closing_parm"));
					m_progress.start_and_show();
					/*treeCtrl.SetInitializing(true);
					treeCtrl.get_treegui().loader.set_text("Closing PARM");
					treeCtrl.get_treegui().loader.set_totalitems(0, "Closing PARM");*/
				}
			}.start();
		} catch(Exception e) {
			log.debug(e.getMessage());
		}
				
		updXml.endSession();
		int n_count = 0;
		while(!updXml.hasExited() && n_count < 1000) {
			try {
				Thread.sleep(10);
				n_count++;
				//monitor.setProgress(n_count);
				//log.debug("n_count = " + n_count);
			} catch(Exception e) {
				//Error.getError().addError("MainController","Exception in endSession",e,1);
			}
		}
		m_progress.stop_and_hide();
	}
	
	private synchronized boolean isUpdateXMLReady() {
		//Timeout timer = new Timeout(10, 50); //10 second timeout, 50 msec wait interval
		if(getUpdateXML().inProgress()) {
			Object obj = new Object();
			getUpdateXML().interrupt();
			getUpdateXML().setWaitForObject(obj);
			try {
				synchronized(obj) {
					obj.wait(20000);
				}
			} catch(InterruptedException e) {
				log.debug("isUpdateXMLReady() interrupted");
			}
		}
		//while(!(getUpdateXML().getState()).equals(Thread.State.TIMED_WAITING) || !timer.timer_exceeded()
		//while(obj.wai)
		//{
/*			try {
				//Thread.sleep(timer.get_msec_interval());
			} catch(InterruptedException e) {
				Error.getError().addError("MainController","isUpdateXMLReady() timeout",e,1);
				return false;
			}
			//timer.inc_timer();
		}*/
		//log.debug("Waited " + timer.get_waited() + "seconds");
		return true;
	}
	// Execute Event Web Service
	public void executeEventWS(EventVO event) {
		try {
			// Kjør ws kall med event vo
			ULOGONINFO logon = new ULOGONINFO();
			logon.setSzUserid(PAS.get_pas().get_userinfo().get_userid());
			logon.setSzCompid(PAS.get_pas().get_userinfo().get_compid());
			logon.setSzPassword(PAS.get_pas().get_userinfo().get_passwd());
			logon.setLComppk(PAS.get_pas().get_userinfo().get_comppk());
			logon.setLDeptpk(PAS.get_pas().get_userinfo().get_current_department().get_deptpk());
			logon.setLUserpk(Long.parseLong(PAS.get_pas().get_userinfo().get_userpk()));
			
			PAEVENT ev = new PAEVENT();
			ev.setFEpiLat((float)event.getEpicentreY());
			ev.setFEpiLon((float)event.getEpicentreX());
			String cat = event.getCategorypk();
			String par = event.getParentpk();
			String pk = event.getEventPk();
			if(cat.length()>0 && cat.startsWith("c"))
				cat = cat.substring(1);
			if(par.length()>0 && par.startsWith("o"))
				par = par.substring(1);
			if(pk.length()>0 && pk.startsWith("e"))
				pk = pk.substring(1);
			String ts = event.getTimestamp();
			if(ts.length()==0) 
				ts = "0";
				
			ev.setLCategorypk(new Long(cat));
			
			ev.setLEventpk(new Long(pk));
			ev.setLParent(new Long(par));
			ev.setLTemppk(new Long(pk));
			ev.setLTimestamp(new Long(ts));
			ev.setSzDescription(event.getDescription());
			ev.setSzName(event.getName());
			ev.setAlerts(new ArrayOfPAALERT());
			if(this.event.getOperation().equals("insert")) {
				// Insert WS
				ev.setParmop(PARMOPERATION.INSERT);
			}
			else if(this.event.getOperation().equals("delete")) {
				ev.setParmop(PARMOPERATION.DELETE);
			}
			else {
				// Edit WS
				ev.setParmop(PARMOPERATION.UPDATE);
			}
			URL wsdl = new URL(vars.WSDL_PARMADMIN); //PAS.get_pas().get_sitename() + "/ExecAlert/WS/ParmAdmin.asmx?WSDL");
			//URL wsdl = new URL("http://localhost/WS/ParmAdmin.asmx?WSDL");
			QName service = new QName("http://ums.no/ws/parm/admin/", "ParmAdmin");
			UPAEVENTRESULT res = new ParmAdmin(wsdl, service).getParmAdminSoap12().execEventUpdate(logon, ev);
			
			if(this.event.getOperation().equals("delete")) {
				XmlWriter writer = new XmlWriter();
				writer.deleteFromTree(event.getPk());
			}
			
			if(res.getPk() > 0)
			{
				event.setEventPk("e" + Long.toString(res.getPk()));
				event.setTempPk("-1");
			}

		} catch(Exception ex) {
			Error.getError().addError("MainController","Exception saving Event",ex,Error.SEVERITY_ERROR);
        }
	}
	
	// Execute Object Web Service
	public void executeObjectWS(ObjectVO object) {
		try
		{
			ULOGONINFO logon = new ULOGONINFO();
			logon.setSzUserid(PAS.get_pas().get_userinfo().get_userid());
			logon.setSzCompid(PAS.get_pas().get_userinfo().get_compid());
			logon.setSzPassword(PAS.get_pas().get_userinfo().get_passwd());
			logon.setLComppk(PAS.get_pas().get_userinfo().get_comppk());
			logon.setLDeptpk(PAS.get_pas().get_userinfo().get_current_department().get_deptpk());
			logon.setLUserpk(new Long(PAS.get_pas().get_userinfo().get_userpk()));
			PAOBJECT obj = new PAOBJECT();
			String cat = this.object.getCategoryPK();
			String par = this.object.getParent();
			String pk = this.object.getPk();
			String test;
			if(cat.length()>0 && cat.startsWith("c"))
				cat = cat.substring(1);
			if(par.length()>0 && par.startsWith("o"))
				par = par.substring(1);
			if(pk.length()>0 && pk.startsWith("o"))
				pk = pk.substring(1);
			String ts = this.object.getTimestamp();
			if(ts.length()==0) 
				ts = "0";

			obj.setBIsobjectfolder(this.object.isObjectFolder());
			obj.setLCategorypk(new Long(cat));
			obj.setLDeptpk(new Long(this.object.getDeptPK()));
			obj.setLImportpk(new Long(this.object.getImportPK()));
			obj.setLObjectpk(new Long(pk));
			obj.setLParent(new Long(par));
			obj.setLTemppk(new Long(pk));
			obj.setLTimestamp(new Long(ts));
			obj.setSzAddress(this.object.getAddress());
			obj.setSzDescription(this.object.getDescription());
			obj.setSzMetadata(this.object.getMetadata());
			obj.setSzName(this.object.getName());
			obj.setSzPhone(this.object.getPhone());
			obj.setSzPlace(this.object.getPlace());
			obj.setSzPostno(this.object.getPostno());
			UPolygon polygon = new UPolygon();
			List<UPolypoint> points = new ArrayList<UPolypoint>();
			PolygonStruct p = (PolygonStruct)this.object.getM_shape();
			double lon, lat;
			if(p!= null) {
				for(int i=0; i < p.get_size(); i++)
				{
					lon = ( Math.round(p.get_coors_lon().get(i) * 1000000.0)) / 1000000.0;
					lat = ( Math.round(p.get_coors_lat().get(i) * 1000000.0)) / 1000000.0;
					UPolypoint point = new UPolypoint();
					point.setLat(lat);
					point.setLon(lon);
					points.add(point);
				}
				polygon.getPolypoint().addAll(points);

				polygon.setColAlpha(p.get_fill_color().getAlpha());
				polygon.setColBlue(p.get_fill_color().getBlue());
				polygon.setColGreen(p.get_fill_color().getGreen());
				polygon.setColRed(p.get_fill_color().getRed());
				obj.setMShape(polygon);
			}
			if(this.object.getOperation().equals("insert")) {
				// Insert WS
				obj.setParmop(PARMOPERATION.INSERT);
			}
			else if(this.object.getOperation().equals("delete")) {
				obj.setParmop(PARMOPERATION.DELETE);
			}
			else {
				// Edit WS
				obj.setParmop(PARMOPERATION.UPDATE);
			}
			
			URL wsdl = new URL(vars.WSDL_PARMADMIN); //PAS.get_pas().get_sitename() + "/ExecAlert/WS/ParmAdmin.asmx?WSDL");
			//URL wsdl = new URL("http://localhost/WS/ParmAdmin.asmx?WSDL");
			QName service = new QName("http://ums.no/ws/parm/admin/", "ParmAdmin");
			UPAOBJECTRESULT res = new ParmAdmin(wsdl, service).getParmAdminSoap12().execObjectUpdate(logon, obj);
			
			if(this.object.getOperation().equals("delete")) {
				XmlWriter writer = new XmlWriter();
				writer.deleteFromTree(object.getPk());
			}
			
				if(res.getPk() > 0)
			{
				this.object.setObjectPK("o" + Long.toString(res.getPk()));
				this.object.setTempPk("-1");
				
			}
		}
		catch(Exception err)
		{
			Error.getError().addError("MainController","Exception saving Object",err,Error.SEVERITY_ERROR);
        }
	}
	// Execute Alert Web Service
	public void executeAlertWS(AlertVO alert) {
		try
		{
			// Kjør ws kall med event vo
			ULOGONINFO logon = new ULOGONINFO();
			logon.setSzUserid(PAS.get_pas().get_userinfo().get_userid());
			logon.setSzCompid(PAS.get_pas().get_userinfo().get_compid());
			logon.setSzPassword(PAS.get_pas().get_userinfo().get_passwd());
			logon.setLComppk(PAS.get_pas().get_userinfo().get_comppk());
			logon.setLDeptpk(PAS.get_pas().get_userinfo().get_current_department().get_deptpk());
			logon.setLUserpk(new Long(PAS.get_pas().get_userinfo().get_userpk()));
			String tester;
			PAALERT save = new PAALERT();
			String par = alert.getParent();
			String pk = alert.getAlertpk();
			if(par.length()>0 && par.startsWith("e"))
				par = par.substring(1);
			if(pk.length()>0 && pk.startsWith("a"))
				pk = pk.substring(1);
			String ts = alert.getTimestamp();
			if(ts==null || ts.length()==0) 
				ts = "0";
			
			save.setFLocked(alert.getLocked());
			save.setLAddresstypes(alert.getAddresstypes());
			save.setLAlertpk(new Long(pk));
			save.setLParent(par);
			save.setLProfilepk(alert.getProfilepk());
			save.setLSchedpk(alert.getSchedpk());
			save.setLTemppk(new Long(pk));
			save.setLTimestamp(ts);
			save.setLValidity(alert.getValidity());
			save.setNExpiry(alert.get_LBAExpiry());
			save.setNMaxchannels(alert.getMaxChannels());
			if(alert.getArea()!=null)
				save.setSzAreaid(alert.getArea().get_id());
			save.setSzDescription(alert.getDescription());
			save.setSzName(alert.getName());
			save.setSzOadc(alert.getOadc());
			save.setSzSmsMessage(alert.get_sms_message());
			save.setSzSmsOadc(alert.get_sms_oadc());
			if(alert.getOperation().equals("insert"))
				save.setParmop(PARMOPERATION.INSERT);
			else if(alert.getOperation().equals("update"))
				save.setParmop(PARMOPERATION.UPDATE);
			else if(alert.getOperation().equals("delete"))
				save.setParmop(PARMOPERATION.DELETE);
			else
			{
				throw new Exception("Unknown operation " + alert.getOperation());
			}
			UShape shape = null;
			UShape lbashape = null;
			if(alert.getM_shape().getClass().equals(PolygonStruct.class))
			{
				PolygonStruct from = alert.getM_shape().typecast_polygon();
				shape = new UPolygon();
				UPolygon poly = (UPolygon)shape;
				List<UPolypoint> points = new ArrayList<UPolypoint>();
				for(int i=0; i < from.get_size(); i++)
				{
					UPolypoint p = new UPolypoint();
					p.setLat(from.get_coor_lat(i));
					p.setLon(from.get_coor_lon(i));
					points.add(p);
				}
				poly.getPolypoint().addAll(points);
				//poly.setMArrayPolypoints(points);
			}
			else if(alert.getM_shape().getClass().equals(EllipseStruct.class))
			{
				EllipseStruct from = alert.getM_shape().typecast_ellipse();
				shape = new UEllipse();
				UEllipse ell = (UEllipse)shape;
				ell.setLat(from.get_center().get_lat());
				ell.setLon(from.get_center().get_lon());
				ell.setX(Math.abs(from.get_corner().get_lon() - from.get_center().get_lon()));
				ell.setY(Math.abs(from.get_corner().get_lat() - from.get_center().get_lat()));
				
			}
			else if(alert.getM_shape().getClass().equals(GISShape.class))
			{
				shape = new UGeminiStreet();
				GISShape from = alert.getM_shape().typecast_gis();
				UGeminiStreet g = (UGeminiStreet)shape;
				ArrayOfUGisImportResultLine arr = new ArrayOfUGisImportResultLine();
				for(int i=0; i < from.get_gislist().size(); i++)
				{
					GISRecord fromline = from.get_gislist().get(i);
					UGisImportResultLine line = new UGisImportResultLine();
					line.setBIsvalid(true);
					line.setMunicipalid(fromline.get_municipal());
					line.setStreetid(fromline.get_streetid());
					line.setHouseno(fromline.get_houseno());
					line.setLetter(fromline.get_letter());
					line.setNamefilter1(fromline.get_name1());
					line.setNamefilter2(fromline.get_name2());
					line.setNLinenumber(fromline.get_lineno());
					arr.getUGisImportResultLine().add(line);
				}
				g.setLinelist(arr);
			}
			if(alert.getM_shape()!=null && SendController.HasType(alert.getAddresstypes(), SendController.SENDTO_CELL_BROADCAST_TEXT))
			{
				lbashape = new ULocationBasedAlert();
				ULocationBasedAlert lba = (ULocationBasedAlert)lbashape;
				ArrayOfLBALanguage languages = new ArrayOfLBALanguage();
				long expiry = alert.get_LBAExpiry();
				
				lba.setMLanguages(languages);
				lba.setNExpiryMinutes(expiry);
				
				for(int i=0; i < alert.getCBMessages().size(); i++)
				{
					CBMessage cb = (CBMessage)alert.getCBMessages().get(i);
					LBALanguage lang = new LBALanguage();
					lang.setSzCbOadc(cb.getCboadc());
					lang.setSzName(cb.getMessageName());
					lang.setSzOtoa("0");
					lang.setSzText(cb.getMessage());
					ArrayOfLBACCode ccodes = new ArrayOfLBACCode();
					for(int j=0; j < cb.getCcodes().size(); j++)
					{
						CCode ccode = cb.getCcodes().get(j);
						LBACCode lbaccode = new LBACCode();
						lbaccode.setCcode(ccode.getCCode());
						ccodes.getLBACCode().add(lbaccode);
					}
					lang.setMCcodes(ccodes);
					languages.getLBALanguage().add(lang);
				}
				lba.setMLanguages(languages);
				lba.setNRequesttype(alert.getRequestType());
				save.setNRequesttype(alert.getRequestType());
				
			}
			shape.setColAlpha(alert.getM_shape().get_fill_color().getAlpha());
			shape.setColBlue(alert.getM_shape().get_fill_color().getBlue());
			shape.setColGreen(alert.getM_shape().get_fill_color().getGreen());
			shape.setColRed(alert.getM_shape().get_fill_color().getRed());
			
			save.setMShape(shape);
			save.setMLbaShape(lbashape);
			
			URL wsdl = new URL(vars.WSDL_PARMADMIN); //PAS.get_pas().get_sitename() + "/ExecAlert/WS/ParmAdmin.asmx?WSDL");
			//URL wsdl = new URL("http://localhost/WS/ParmAdmin.asmx?WSDL");
			QName service = new QName("http://ums.no/ws/parm/admin/", "ParmAdmin");
			UPAALERTRESTULT res = new ParmAdmin(wsdl, service).getParmAdminSoap12().execAlertUpdate(logon, save);
			
			if(this.alert.getOperation().equals("delete")) {
				XmlWriter writer = new XmlWriter();
				writer.deleteFromTree(alert.getPk());
			}
			
			if(res.getPk() > 0)
			{
				alert.setPk("a" + Long.toString(res.getPk()));
				alert.setTempPk("-1");
			}
			
		}
		catch(Exception err)
		{
			Error.getError().addError("MainController","Exception saving Alert",err,Error.SEVERITY_ERROR);
			return;
		}

	}
	public void actionPerformed(ActionEvent e) {
		// '-- this --
		try {
			// '-- Exit --
			if (e.getSource() == this.gui.getItmExit()) {
				this.updXml.endSession();
				System.exit(0);
			}
			if (e.getSource() == this.gui.getRefresh()) {
				this.updXml.readXML();
			}
			// '-- treeCtrlController NEW OBJECTFOLDER --
			if (e.getSource() == treeCtrl.getGui().getObjectfolder()
					/*|| e.getSource() == this.gui.getObjectfolder()*/) {
				objectCtrl = null;
				objectCtrl = new ObjectController(this, getMapNavigation());
				objectCtrl.insertObjectFolder(this);
		}
			if (e.getSource() == gui.getObjectfolder()){
				this.treeCtrl.getGui().getTree().clearSelection();
				objectCtrl = null;
				objectCtrl = new ObjectController(this, getMapNavigation());
				objectCtrl.insertObjectFolder(this);
			}
			// '-- ObjectController NEW OBJECT --
			if (e.getSource() == treeCtrl.getGui().getObject()
					/*|| e.getSource() == this.gui.getObject()*/) {
				objectCtrl = null;
				objectCtrl = new ObjectController(this, getMapNavigation());
				objectCtrl.insertObject(this);
				
			}
			if (e.getSource() == gui.getObject()){
				this.treeCtrl.getGui().getTree().clearSelection();
				objectCtrl = null;
				objectCtrl = new ObjectController(this, getMapNavigation());
				objectCtrl.insertObject(this);
			}
			// '-- AlertController NEW ALERT --
			if (e.getSource() == treeCtrl.getGui().getAlert()
					/*|| e.getSource() == this.gui.getAlert()*/) {
//				alertCtrl = null;
//				alertCtrl = new AlertController(this, getMapNavigation());
//				alertCtrl.insertAlert(this);
				//while(!isUpdateXMLReady()) {
					//; // Dette hindrer koden å fortsette før den orginale pk'en er oppdatert
				//}
				new AlertWindow(new SendObject(PAS.get_pas(), this),null);
//				PAS.get_pas().get_sendcontroller().set_activesending(new ActionEvent(null,ac));
				
			}
			// '-- EventController NEW EVENT --
			if (e.getSource() == treeCtrl.getGui().getEvent()
					/*|| e.getSource() == this.gui.getEvent()*/) {
				eventCtrl = null;
				eventCtrl = new EventController();
				eventCtrl.insertEvent(this);
			}
			if (e.getSource() == treeCtrl.getGui().getGotoMap()) {
				gotoMap();
			}
			if (e.getActionCommand().equals("act_kick_repaint"))
				mapRedraw();
		} catch (FileNotFoundException ex) {
			log.warn(ex.getMessage(), ex);
			Error.getError().addError("MainController","FileNotFoundException in actionPerformed - new",ex,1);
		} catch (ParmException ex) {
			log.warn(ex.getMessage(), ex);
			Error.getError().addError("MainController","Exception in actionPerformed - new",ex,1);
		}

		// '-- DELETE Object, Alert & Event --
		if (e.getSource() == treeCtrl.getGui().getDelete() || e.getSource() == this.gui.getDelete()) {
			try {
				DefaultMutableTreeNode remNode = (DefaultMutableTreeNode) treeCtrl.getSelPath().getLastPathComponent();
				Object object = remNode.getUserObject();
				if (object.getClass().equals(ObjectVO.class)) {
					this.object = (ObjectVO) object;
					if (this.objectCtrl == null) {
						this.objectCtrl = new ObjectController(this, getMapNavigation());
					}
					ArrayList<Object> locked;
					locked = checkIfLocked(this.object);
					if(locked.size()>0) {
						Iterator<Object> it = locked.iterator();
						String errormsg = "Could not delete this object because this/these underlying alert(s) where locked: \n";
						while(it.hasNext()) {
							errormsg = errormsg.concat(((AlertVO)it.next()).getName()+ "\n");
						}
						JOptionPane.showMessageDialog(gui,errormsg);
					} else if (this.objectCtrl.deleteObject(this.object)) {
						this.object = this.objectCtrl.getObject();
						if(isUpdateXMLReady()) {
							if(PAS.g_n_parmversion < 2) {
								addToObjectList(this.object); // bruk boolean?
								treeCtrl.getGui().getTreeModel().removeNodeFromParent(remNode);
							}
							else {
								//if(this.object.getList().size()>0)
									//objectCtrl.deleteChildren(this.object.getList(), this);
								executeObjectWS(this.object); // oppdaterer synkront med webservice
								try {
									if(!this.object.getParent().equals("o-1"))
										treeCtrl.getGui().getTreeModel().removeNodeFromParent(remNode);
								}
								catch(Exception ex) {
									log.warn(ex.getMessage(), ex);
								}
							}
							
	//						Her blir PARM refreshet for at andre klienter skal få meldingen raskest mulig
							getUpdateXML().saveProject();
						}
					}
					mapClear();
					this.objectCtrl = null;
				}
				if (object.getClass().equals(AlertVO.class)) {
					this.alert = (AlertVO) object;
					if (this.alertCtrl == null) {
						this.alertCtrl = new AlertController(this, getMapNavigation()); //map.getM_navigation());
					}
					if (this.alert.getLocked() == 1)
						JOptionPane.showMessageDialog(gui,"This alert is locked and can't be deleted.");
					else {
						if (this.alertCtrl.deleteAlert(this.alert, (DefaultMutableTreeNode) remNode.getParent())) {
							this.alert = this.alertCtrl.getAlert();
							if(isUpdateXMLReady()) {
								if(PAS.g_n_parmversion < 2)
									addToObjectList(this.alert); // bruk boolean?
								else
									executeAlertWS(this.alert); // oppdaterer synkront med webservice 
								
								//treeCtrl.getGui().getTreeModel().removeNodeFromParent(remNode);
	//							 Her blir PARM refreshet for at andre klienter skal få meldingen raskest mulig
								getUpdateXML().saveProject();
							}
						}
					}
					this.alertCtrl = null;
				}
				if (object.getClass().equals(EventVO.class)) {
					this.event = (EventVO) object;
					if (this.eventCtrl == null) {
						this.eventCtrl = new EventController();
					}
					
					ArrayList<Object> locked;
					locked = checkIfLocked(this.event);
					if(locked.size()>0) {
						Iterator<Object> it = locked.iterator();
						String errormsg = "Could not delete event because this/these underlying alert(s) where locked: \n";
						while(it.hasNext()) {
							errormsg = errormsg.concat(((AlertVO)it.next()).getName()+ "\n");
						}
						JOptionPane.showMessageDialog(gui,errormsg);
					} else if (this.eventCtrl.deleteEvent(this.event, (DefaultMutableTreeNode) remNode.getParent())) {
						this.event = this.eventCtrl.getEvent();
						if(isUpdateXMLReady()) {
							if(PAS.g_n_parmversion < 2)
								addToObjectList(this.event); // bruk boolean?
							else {
								//if(this.event.getAlertListe().size()>0)
									//objectCtrl.deleteChildren(this.event.getAlertListe());
								executeEventWS(this.event); // oppdaterer synkront med webservice
							}
							//treeCtrl.getGui().getTreeModel().removeNodeFromParent(remNode);
	//						Her blir PARM refreshet for at andre klienter skal få meldingen raskest mulig
							getUpdateXML().saveProject();
						}
					}
					this.eventCtrl = null;
				}
				
			} catch (Exception ex) {
				log.warn(ex.getMessage(), ex);
				Error.getError().addError("MainController","Exception in actionPerformed - delete",ex,1);
			}
		}
		
		
		
		// '-- EDIT Object, Alert & Event --
		if (treeCtrl.getSelPath() == null)
			;
		else if (e.getSource() == treeCtrl.getGui().getEdit() || e.getSource() == this.gui.getEdit()) {
			remNode = (DefaultMutableTreeNode) treeCtrl.getSelPath().getLastPathComponent();
			try {
				Object object = remNode.getUserObject();
				if (object.getClass().equals(ObjectVO.class)) {
					this.object = (ObjectVO) object;
					if (this.objectCtrl == null) {
						this.objectCtrl = new ObjectController(this, getMapNavigation());
					}
					
					this.objectCtrl.editObject(this.object, this.getAllCategorys(), this);
					this.activateObjectBtnListener();
				}
				if (object.getClass().equals(AlertVO.class)) {
					
					this.alert = (AlertVO) object;
					if (this.alertCtrl == null) {
						this.alertCtrl = new AlertController(this, getMapNavigation());//map.getM_navigation());
					}
					alertCtrl.setAlert(alert);
					
					//if(this.alert.getLocked() == 1)
						//JOptionPane.showMessageDialog(gui,"This alert is locked and can't be edited.");
					//else {
						new AlertWindow(new SendObject(PAS.get_pas(), this), alertCtrl);
					//}
				}
				if (object.getClass().equals(EventVO.class)) {
					this.event = (EventVO) object;
					if (this.eventCtrl == null) {
						this.eventCtrl = new EventController();
					}
					this.eventCtrl.editEvent(this.event, this.getAllCategorys(), (DefaultMutableTreeNode) remNode.getParent());
					this.activateEventBtnListener();
				}

			} catch (FileNotFoundException e1) {
				log.warn(e1.getMessage(), e1);
				Error.getError().addError("MainController","FileNotFoundException in actionPerformed - edit",e1,1);
			} catch (ParmException e1) {
				log.warn(e1.getMessage(), e1);
				Error.getError().addError("MainController","ParmException in actionPerformed - edit",e1,1);
			}
		}
		// '-- AlertController Save & Cancel --
		if (e.getActionCommand() == "act_store_alert") {
			//this.alert = alertCtrl.storeAlert();
			//addToObjectList(this.alert);
			alertCtrl = (AlertController)e.getSource();
			alert = alertCtrl.getAlert();
			DefaultMutableTreeNode parent;
			if (this.treeCtrl.getGui().getDelete().isEnabled()) { // if
				// something
				// is
				// selected.
				parent = (DefaultMutableTreeNode) treeCtrl.getSelPath().getLastPathComponent();
			} else
				parent = this.treeCtrl.getGui().getRootnode();

			if ((this.alert.getOperation().equals("insert") && this.alertCtrl.isToObjectList()) || (PAS.g_n_parmversion >=2 && this.alert.getOperation().equals("update")) )
				try {
					if(PAS.g_n_parmversion>=2)
						executeAlertWS(this.alert);
					
					if(alert.getOperation().equals("insert"))
					{
						DefaultMutableTreeNode selectedNode = this.treeCtrl.addParentToTree(alert, parent);
						// Må legge alerten til i eventlisten, men kan vente med det til jeg har fikset objekt ting
						// select the new node (leaf).
						TreePath path = new TreePath(selectedNode.getPath());
						alert.setPath(selectedNode);
						this.treeCtrl.getGui().getTree().setSelectionPath(path);
						EventVO o = (EventVO)parent.getUserObject();
						if(PAS.g_n_parmversion<2)
							o.addAlerts(this.alert);
					}
					else if(PAS.g_n_parmversion>=2)
					{
						DefaultMutableTreeNode editNode = (DefaultMutableTreeNode) treeCtrl.getSelPath().getLastPathComponent();
						this.treeCtrl.getGui().getTreeModel().reload(editNode);						
					}
					DefaultMutableTreeNode node = findNodeByPk(alert.getAlertpk());
					if(node!=null)
					{
						log.debug("Node = " + node);
						node.setUserObject(alert);
					}
					
				} catch (ParmException e1) {
					log.warn(e1.getMessage(), e1);
					Error.getError().addError("MainController","Exception in actionPerformed - Alert save/cancel",e1,1);
				}
			//else if (this.alert.getOperation().equals("update")) {
			else {
				DefaultMutableTreeNode editNode = (DefaultMutableTreeNode) treeCtrl.getSelPath().getLastPathComponent();
				this.treeCtrl.getGui().getTreeModel().reload(editNode);
			}
			log.debug("Address-types: " + this.alertCtrl.getPanelToolbar().get_addresstypes());
//					alertCtrl.getGui().dispose();

			//PAS.pasplugin.removeShapeToPaint(this.alert.getM_shape());
			
			//addShapeToDrawQueue(this.alert.getM_shape()); //this.alert.getM_shape());
			getUpdateXML().saveProject();
			updateShape(null);
			try
			{
				this.alert.getShape().shapeName = this.alert.getName();
				setSelectedAlert(this.alert);
			}
			catch(Exception err)
			{
				log.warn(err.getMessage(), err);
			}
		}
		
		// '-- EventController Save & Cancel --
		if (eventCtrl != null) {
			
			if (e.getSource() == eventCtrl.getGui().getActionPanel().getBtnCancel()) {
				eventCtrl.getGui().dispose();
				if(event != null) {
					UnknownShape ushape = new UnknownShape();
					ushape.set_epicentre(new MapPoint(getMapNavigation(),
							new MapPointLL(event.getEpicentreX(),event.getEpicentreY())));
					addShapeToDrawQueue(ushape);
				}
				setFilled(null); // Bruker denne for å fjerne activeshape
				PAS.get_pas().get_mappane().set_mode(MapFrame.MapMode.PAN);
				}
			else if (e.getSource() == eventCtrl.getGui().getActionPanel().getBtnSave()) {
				// Is it new or edit?
				DefaultMutableTreeNode node = (DefaultMutableTreeNode)PAS.get_pas().get_parmcontroller().getTreeCtrl().getSelPath().getLastPathComponent();
				boolean wrongType = false;
				if(event == null || !node.getUserObject().equals(event)) {
					// We have to make sure it's an Object not an Object folder
					
				    ObjectVO obj;
					if(node.getUserObject().getClass().equals(ObjectVO.class)) {
						obj = (ObjectVO)node.getUserObject();
						if(obj.isObjectFolder())
							wrongType = true;
					}
					else if(!wrongType && !node.getUserObject().getClass().equals(ObjectVO.class))
						wrongType = true;
				}
				if(wrongType) {
					JOptionPane.showMessageDialog(gui,new String("Parent is not an Object, please make sure that the selected item in the tree structure is an Object"),"Parent is not an Object", JOptionPane.ERROR_MESSAGE);
				}
				if(!wrongType) {
					boolean stored;
					this.event = eventCtrl.storeEvent();
					if (this.eventCtrl.getGui().getEventInputPanel().getTxtName().getText().length() > 0) {
						if(PAS.g_n_parmversion<2) //debug
							addToObjectList(this.event);
						else 
							executeEventWS(this.event);
	
						DefaultMutableTreeNode parent;
						if (this.treeCtrl.getGui().getDelete().isEnabled()) { // if
							// something is selected.
							parent = (DefaultMutableTreeNode) treeCtrl.getSelPath().getLastPathComponent();
						} else
							parent = this.treeCtrl.getGui().getRootnode();
	
						if (this.event.getOperation().equals("insert") && this.eventCtrl.isToObjectList()) {
							try {
								log.debug("EventParentPK: " + event.getParentpk());
								log.debug("ParentPK: " + ((ObjectVO) parent.getUserObject()).getPk());
								DefaultMutableTreeNode selectedNode = this.treeCtrl.addParentToTree(event, parent);
								// select the new node (leaf).
								TreePath path = new TreePath(selectedNode.getPath());
								event.setPath(selectedNode);
								this.treeCtrl.getGui().getTree().setSelectionPath(path);
								ObjectVO o = (ObjectVO)parent.getUserObject();
								o.addEvents(this.event);
							} catch (ParmException e1) {
								log.warn(e1.getMessage(), e1);
								Error.getError().addError("MainController","Exception in actionPerformed - Event save/cancel",e1,1);
							}
						//} else if (this.event.getOperation().equals("update")) {
						} else {
							DefaultMutableTreeNode editNode = (DefaultMutableTreeNode) treeCtrl
									.getSelPath().getLastPathComponent();
							/* to show the new name in textfield */
							//this.treeCtrl.getGui().getTreeModel().reload(editNode);
							this.treeCtrl.getGui().getTreeModel().valueForPathChanged(treeCtrl.getSelPath(),editNode.getUserObject());
						}
					
						eventCtrl.getGui().dispose();
						setFilled(null); // Bruker denne for å fjerne activeshape
						getUpdateXML().saveProject();
					}
				}
			}
		}
		// '-- Object(& ObjecFolder)Controller Save & Cancel --
		if (objectCtrl != null) {
			if (e.getSource() == objectCtrl.getGui().getActionPanel().getBtnCancel()) {
				objectCtrl.getGui().dispose();
				if(object != null) {
					addShapeToDrawQueue(this.object.getM_shape());
				}
				PAS.get_pas().get_mappane().set_active_shape(null);
				PAS.get_pas().get_mappane().set_mode(MapFrame.MapMode.PAN);
			} else if (e.getSource() == objectCtrl.getGui().getActionPanel().getBtnSave()) {
				// Check if this is root or an object folder
				boolean wrongType = false;
				if(!this.treeCtrl.getGui().getTree().isSelectionEmpty()) {
					DefaultMutableTreeNode node = (DefaultMutableTreeNode)this.treeCtrl.getSelPath().getLastPathComponent();
					ObjectVO obj;
					
					if(node.getUserObject().getClass().equals(ObjectVO.class)) {
						obj = (ObjectVO)node.getUserObject();
						Object o1 = node.getParent();
						Object o2 = this.treeCtrl.getGui().getRootnode();
						DefaultMutableTreeNode parentnode = (DefaultMutableTreeNode)node.getParent();
						if(o1.equals(o2))
							;
						else if(!parentnode.getUserObject().getClass().equals(String.class))
							if(!((ObjectVO)parentnode.getUserObject()).getClass().equals(ObjectVO.class))
								wrongType = true;
					}
					else
						wrongType = true;
					if(wrongType) {
						JOptionPane.showMessageDialog(gui,new String("Parent is not root or an Object folder, please make sure that the selected item in the tree structure is an Object folder or that nothing is selected"),"Parent is not an Object", JOptionPane.ERROR_MESSAGE);
					}
				}
				if(!wrongType) {
					this.object = objectCtrl.storeObject(this);
					if (this.object != null) {
						if(PAS.g_n_parmversion<2) //debug
							addToObjectList(this.object);
						else
							executeObjectWS(this.object);
	
						DefaultMutableTreeNode parent;
						if (! this.treeCtrl.getGui().getTree().isSelectionEmpty()) { /* if something is selected */	
							parent = (DefaultMutableTreeNode) treeCtrl.getSelPath().getLastPathComponent();
						} else
							parent = this.treeCtrl.getGui().getRootnode();
	
						if (this.object.getOperation().equals("insert") && this.objectCtrl.isToObjectList()) {
							try {
								DefaultMutableTreeNode selectedNode = this.treeCtrl.addParentToTree(object, parent);
								// select the new node (leaf).
								TreePath path = new TreePath(selectedNode.getPath());
								this.treeCtrl.getGui().getTree().setSelectionPath(path);
								object.setPath(selectedNode);
								// Dette er en test
								if(parent != this.treeCtrl.getGui().getRootnode()) {
									ObjectVO o = (ObjectVO)parent.getUserObject();
									o.addObjects(this.object);
								}
							} catch (ParmException e1) {
								log.warn(e1.getMessage(), e1);
								Error.getError().addError("MainController","Exception in actionPerformed - Object save/cancel",e1,1);
							}
							
						} else {
							DefaultMutableTreeNode editNode = (DefaultMutableTreeNode) treeCtrl.getSelPath().getLastPathComponent();
							//this.treeCtrl.getGui().getTreeModel().reload(editNode);
							this.treeCtrl.getGui().getTreeModel().valueForPathChanged(treeCtrl.getSelPath(),editNode.getUserObject());
						}
						
						objectCtrl.getGui().dispose();
						// Her skal den i teorien fjerne drawmode og vise polygonet på vanlig måte
						addShapeToDrawQueue(this.object.getM_shape());
						PAS.get_pas().get_parmcontroller().setFilled(null);
						PAS.get_pas().get_mappane().set_mode(MapFrame.MapMode.PAN);
						getUpdateXML().saveProject();
					}
				}
			}	
		}
		
		if(e.getSource() == treeCtrl.getGui().getGenerateSending()) {
			SwingUtilities.invokeLater(new Runnable() 
			{
				public void run()
				{
					DefaultMutableTreeNode remNode = (DefaultMutableTreeNode) treeCtrl.getSelPath().getLastPathComponent();
					Object o = remNode.getUserObject();
					if(o.getClass().equals(EventVO.class)) {
						event = (EventVO)o;
						Iterator it = event.getAlertListe().iterator();
						boolean allSaved = true;
						while(it.hasNext())
							if(!((AlertVO)it.next()).getAlertpk().startsWith("a"))
								allSaved = false;
						
						if(allSaved) {
							PAS.get_pas().actionPerformed(new ActionEvent(event,ActionEvent.ACTION_PERFORMED,"act_send_scenario"));
							clearDrawQueue();
							setFilled(null);
							setDrawMode(null);
						}
						else 
						{
							if(JOptionPane.showConfirmDialog(gui,"One or more alerts not saved, do you want to save?","Alert not saved",JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
								try {
									updXml.readXML();
								} catch(Exception ex) {
									Error.getError().addError("MainController","Exception saving alert",ex,Error.SEVERITY_ERROR);
								}
							}
						}
					} else if(o.getClass().equals(AlertVO.class)) {
						alert = (AlertVO)o;
						if(alertSaved(alert))
						{
							PAS.get_pas().actionPerformed(new ActionEvent(alert,ActionEvent.ACTION_PERFORMED,"act_send_scenario"));
							clearDrawQueue();
							setFilled(null);
							setDrawMode(null);					
						}
					}
				}
			});
		} 
		else if(e.getSource() == treeCtrl.getGui().getSnapSimulation() ||
				e.getSource() == treeCtrl.getGui().getSnapSending() ||
				e.getSource() == treeCtrl.getGui().getSnapTest())
		{
			DefaultMutableTreeNode remNode = (DefaultMutableTreeNode) treeCtrl.getSelPath().getLastPathComponent();
			Object o = remNode.getUserObject();
			ArrayList<Object> sendto = new ArrayList<Object>(); //kun en hvis det er alert. 1 til flere for events
			
			if(o.getClass().equals(EventVO.class)) {
				event = (EventVO)o;
				Iterator<Object> it = event.getAlertListe().iterator();
				while(it.hasNext())
				{
					AlertVO a = (AlertVO)it.next();
					if(a!=null)
					{
						if(alertSaved(a))
						{
							sendto.add(a);
						} else { //user has been asked to save, just return
							return;
						}
					}
				}
			} else if(o.getClass().equals(AlertVO.class)) {
				alert = (AlertVO)o;
				if(alertSaved(alert))
				{
					sendto.add(alert);
				}
				else
					return;
			}
			if(sendto.size()>0) //there are one or more alerts to send
			{
				String sz_command;
				if(e.getSource() == treeCtrl.getGui().getSnapSending())
					sz_command = "act_exec_snapsending";
				else if(e.getSource() == treeCtrl.getGui().getSnapSimulation())
					sz_command = "act_exec_snapsimulation";
				else if(e.getSource() == treeCtrl.getGui().getSnapTest())
					sz_command = "act_exec_snaptest";
				else
					return;
				PAS.get_pas().actionPerformed(new ActionEvent(o, ActionEvent.ACTION_PERFORMED, sz_command));
			}
			else
				JOptionPane.showMessageDialog(gui, "No valid alerts found", "Notification", JOptionPane.WARNING_MESSAGE);
		}
		else if(e.getSource().equals(treeCtrl.getGui().getExportPolygon()))
		{
			DefaultMutableTreeNode remNode = (DefaultMutableTreeNode) treeCtrl.getSelPath().getLastPathComponent();
			Object o = remNode.getUserObject();			
			if(o.getClass().equals(AlertVO.class))
			{
				//AlertVO exp = (AlertVO)o;
				AlertVO [] exp = new AlertVO[1];
				exp[0] = (AlertVO)o;
				PAS.get_pas().actionPerformed(new ActionEvent(exp, ActionEvent.ACTION_PERFORMED, "act_export_alert_polygon"));
			}
			else if(o.getClass().equals(EventVO.class))
			{
				EventVO ev = (EventVO)o;
				ArrayList<AlertVO> vec = new ArrayList<AlertVO>();
				if(ev.getAlertListe()!=null)
				{
					Iterator it = ev.getAlertListe().iterator();
					while(it.hasNext())
					{
						AlertVO avo = (AlertVO)it.next();
						if(avo.getShape().getClass().equals(PolygonStruct.class))
							vec.add(avo);
					}
					if(vec.size()>0)
					{
						AlertVO[] arr = new AlertVO[vec.size()];
						for(int x = 0; x < vec.size(); x++)
							arr[x] = (AlertVO)vec.get(x);
						PAS.get_pas().actionPerformed(new ActionEvent(arr, ActionEvent.ACTION_PERFORMED, "act_export_alert_polygon"));
					}
				}
			}
			
		}
	}
	
	public boolean alertSaved(AlertVO o)
	{
		if(o.getAlertpk().startsWith("a")) {
			return true;
		}
		else {
			if(JOptionPane.showConfirmDialog(gui,"Alert not saved, do you want to save?","Alert not saved",JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
				try {
					updXml.readXML();
				} catch(Exception ex) {
					Error.getError().addError("MainController","Exception saving alert",ex,Error.SEVERITY_ERROR);
				}
			}
		}
		return false;
	}

	public void activateObjectBtnListener() {
		objectCtrl.getGui().getActionPanel().getBtnCancel().addActionListener(this);
		objectCtrl.getGui().getActionPanel().getBtnSave().addActionListener(this);
	}

	public void activateAlertBtnListener() {
		alertCtrl.getGui().getBtnCancel().addActionListener(this);
		alertCtrl.getGui().getBtnSave().addActionListener(this);
	}

	public void activateEventBtnListener() {
		eventCtrl.getGui().getActionPanel().getBtnCancel().addActionListener(this);
		eventCtrl.getGui().getActionPanel().getBtnSave().addActionListener(this);
	}

	public void addToObjectList(Object object) {
		this.objectList = new ArrayList<Object>();
		this.objectList.add(object);
		addToXml(this.objectList);
	}
	public void saveChanges(ParmVO object) {
		if(object.getPk().startsWith("a") || object.getPk().startsWith("e") || object.getPk().startsWith("o")) {
			object.setOperation("update");
			addToObjectList(object);
		}
		else {//insert
			object.setOperation("insert");
			addToObjectList(object);
		}
	}

	private void addToXml(Collection<Object> objectList) {
		XmlWriter w = new XmlWriter();
		XmlReader r = new XmlReader(this);
		try{
			w.setRootTimestamp(r.getRootTimestamp());
		}
		catch(Exception e){
			log.debug("Feil i addToXml" + e.getMessage());
			Error.getError().addError("MainController","Exception in addToXml",e,1);
		}
		// Må sende med alle objektene som skal slettes
		w.writeXml(w.extractObjects(objectList));
	}

	private ArrayList<Object> getAllElementsFromXmlFile() throws FileNotFoundException,
			ParmException {
		this.allElements = null;
		if(isUpdateXMLReady()) {
			this.xmlreader = new XmlReader(this);
			allElements = (ArrayList<Object>) this.xmlreader.readXml();
			return allElements;
		}
		else
			return null;
	}

	public HashMap<Long, CategoryVO> getAllCategorys() throws FileNotFoundException,
			ParmException {
		// Må kjøre denne for å hente ut kategoriene fra XML filen
		//Her er det jeg må loade inn kategoriene i minnet og ikke kjøre denne på alle alerts
		if (this.m_categories == null) {
			this.m_categories = new HashMap<Long, CategoryVO>();
            m_categories.put((long)-1, new CategoryVO("c-1", Localization.l("main_parm_category_select"), null, null, null));
			if (this.allElements == null || this.allElements.size() < 1) {
				this.getAllElementsFromXmlFile();
			}
			for (int i = 0; i < allElements.size(); i++) {
				if (allElements.get(i).getClass().equals(CategoryVO.class))
					m_categories.put(Long.parseLong(((CategoryVO)allElements.get(i)).getCategoryPK().substring(1)),(CategoryVO)allElements.get(i));
			}
			
		}
		return m_categories;
		
	}

	public void updateTree() {
		try {
			this.treeCtrl.initiateTree(this.getAllElementsFromXmlFile(), true);
		} catch (FileNotFoundException e) {
			log.warn(e.getMessage(), e);
			Error.getError().addError("MainController","Exception in updateTree",e,1);
		} catch (ParmException e) {
			log.warn(e.getMessage(), e);
			Error.getError().addError("MainController","ParmException in updateTree",e,1);
		}
	}

	public void refreshTree(Collection<Object> c) throws ParmException {
		// Tar imot en liste med oppdaterte objeter samt nye fra tråden
		try{
			this.getAllCategorys();
		}catch(Exception e){
			log.warn(e.getMessage(), e);
			Error.getError().addError("MainController","Exception in refreshTree",e,1);
		}
		ArrayList<Object> updatedObjects = null;
		if (this.treeCtrl.getGui().getRootnode().isLeaf()) {
			updateTree();
		}

		else {
			if (c == null)
				throw new ParmException("collection param. is null");
			else if (c.size() == 0)
				log.debug("Updating... no new elements!");

			if (c.size() > 0 && c != null) {
				//xmlreader.sortListAscending((ArrayList) c);
				ListSorter ls = new ListSorter();
				Collections.sort((ArrayList<Object>)c,ls);
				log.debug("Updating... " + c.size() + " new elements!");
				updatedObjects = (ArrayList<Object>) c;
				Object o;
				DefaultMutableTreeNode wantedNode = null;
				for (int i = 0; i < updatedObjects.size(); i++) { // for each
																	//element
																	// in list
					o = updatedObjects.get(i);
					
					if (o.getClass().equals(ObjectVO.class)) {
						ObjectVO oVO = (ObjectVO) updatedObjects.get(i);
						/*Går igjennom treet for å finne node basert på pk*/
						String tempPk = oVO.getTempPk();
						if(tempPk==null)
							tempPk = oVO.getObjectPK();
						DefaultMutableTreeNode rootNode = this.treeCtrl.getGui().getRootnode(); 
						int childCount = rootNode.getChildCount();
						DefaultMutableTreeNode almostRootNode;
						
						for (int u = 0; u < childCount; u++) { // for each "root" node in tree
							almostRootNode = (DefaultMutableTreeNode) rootNode.getChildAt(u) ;
							wantedNode = findNodeByPk(almostRootNode, tempPk);
							
							if (wantedNode != null){
								ObjectVO oo = (ObjectVO)wantedNode.getUserObject();
																
								//PolygonStruct ps = oo.getM_polygon();
								Set<Long> keyset = m_categories.keySet();
								Iterator it = keyset.iterator();
								
								while (it.hasNext()) {
									CategoryVO co = m_categories.get(it.next());
									if (oVO.getCategoryPK().compareTo(co.getCategoryPK()) == 0){
										oVO.setCategoryVO(co);
										break;
									}
								}
								oVO.setList(((ObjectVO)wantedNode.getUserObject()).getList());
								
								DefaultMutableTreeNode temp = (DefaultMutableTreeNode)wantedNode.getParent();
								if(temp != rootNode) {
									ObjectVO oTemp = (ObjectVO)temp.getUserObject();
									oTemp.removeObjects(oo);
									oTemp.addObjects((ObjectVO)o);
								}
								oo.setPath(wantedNode);
								break;
							}
						}
						
						if (wantedNode != null) // update, not new element.
							wantedNode.setUserObject(o);
						else { // new element.
							String parent = oVO.getParent();
							Set<Long> keyset = m_categories.keySet();
							Iterator it = keyset.iterator();
							
							while (it.hasNext()) {
								CategoryVO co = m_categories.get(it.next());
								if (oVO.getCategoryPK().compareTo(co.getCategoryPK()) == 0){
									oVO.setCategoryVO(co);
									break;
								}
							}
							log.debug("parent is " + parent);
							DefaultMutableTreeNode parentNode = getParentNode(
									parent, this.treeCtrl.getGui().getTree());
							this.treeCtrl.addParentToTree(oVO, parentNode);
						}
						
					} else if (o.getClass().equals(EventVO.class)) {
						EventVO eVO = (EventVO) updatedObjects.get(i);
						log.debug(eVO);

						String tempPk = eVO.getTempPk();
						if(tempPk==null)
							tempPk = eVO.getEventPk();
						DefaultMutableTreeNode rootNode = this.treeCtrl.getGui().getRootnode(); 
						int childCount = rootNode.getChildCount();
						DefaultMutableTreeNode almostRootNode;
						
						for (int u = 0; u < childCount; u++) { // for each "root" node in tree
							almostRootNode = (DefaultMutableTreeNode) rootNode.getChildAt(u) ;
							wantedNode = findNodeByPk(almostRootNode, tempPk);
							
							if (wantedNode != null){
								Set<Long> keyset = m_categories.keySet();
								Iterator it = keyset.iterator();
								
								while (it.hasNext()) {
									CategoryVO co = m_categories.get(it.next());
									if (eVO.getCategorypk().compareTo(co.getCategoryPK()) == 0){
										eVO.setCatVO(co);
										break;
									}
								}
								DefaultMutableTreeNode temp = (DefaultMutableTreeNode)wantedNode.getParent();
								ObjectVO oTemp = (ObjectVO)temp.getUserObject();
								oTemp.removeEvents((EventVO)wantedNode.getUserObject());
								oTemp.addEvents((EventVO)o);
								eVO.setPath(wantedNode);
								break;
							}
							
						}
						
						if (wantedNode != null) { // update, not new element.
							wantedNode.setUserObject(o);
						}
						else { // new element.
							String parent = eVO.getParentpk();
							DefaultMutableTreeNode parentNode = getParentNode(parent, this.treeCtrl.getGui().getTree());
							try {
								/*
							
								if (parentNode == null)
									throw new ParmException(
										"Parent node er null for Event insertion: PK=" + eVO.getPk() + " name: " + eVO.toString());
										*/
								Iterator itCat = allCategorys.iterator();
								while (itCat.hasNext()) {
									CategoryVO co = (CategoryVO) itCat.next();
									if (eVO.getCategorypk().compareTo(co.getCategoryPK()) == 0){
										eVO.setCatVO(co);
										break;
									}
								}
								this.treeCtrl.addParentToTree(eVO, parentNode);
							}
							catch(Exception e) { } // Det er ikke noe å gjøre med feilen uansett kan jo prøve å sette inn ved neste runde
						}
					} else if (o.getClass().equals(AlertVO.class)) {
						AlertVO aVO = (AlertVO) updatedObjects.get(i);
						log.debug(aVO);

						String tempPk = aVO.getTempPk();
						if(tempPk==null)
							tempPk = aVO.getAlertpk();
						DefaultMutableTreeNode rootNode = this.treeCtrl.getGui().getRootnode(); 
						int childCount = rootNode.getChildCount();
						DefaultMutableTreeNode almostRootNode;
						
						for (int u = 0; u < childCount; u++) { // for each "root" node in tree
							almostRootNode = (DefaultMutableTreeNode) rootNode.getChildAt(u) ;
							wantedNode = findNodeByPk(almostRootNode, tempPk);
							aVO.setPath(wantedNode);
							if (wantedNode != null){
								break;
							}
						}
						if (wantedNode != null) { // update, not new element.
							wantedNode.setUserObject(o);
							// Må også oppdatere alertlist til eventen
							DefaultMutableTreeNode parent = findNodeByPk(((AlertVO)o).getParent());
							EventVO event = (EventVO)parent.getUserObject();
							AlertVO alerttmp;
							if(event.getAlertListe().size()>0) { // Var kommentert ut
								for(int xxx=0;xxx<event.getAlertListe().size();xxx++) { // Var kommentert ut
									alerttmp = (AlertVO)event.getAlertListe().get(xxx); // Var kommentert ut
									if(alerttmp.getAlertpk().equals(((AlertVO)o).getTempPk())) { // Var kommentert ut
											event.getAlertListe().set(xxx,o); // Var kommentert ut
									} // Var kommentert ut
								} // Var kommentert ut
								if(!event.getAlertListe().contains(o))
								{
									log.debug("contains");
									event.addAlerts((AlertVO)o);
								}
								else
								{
									int idx = event.getAlertListe().indexOf(o);
									if(idx>=0)
									{
										event.getAlertListe().set(idx, o);
										AlertVO node = (AlertVO)event.getAlertListe().get(idx);
										this.getTreeCtrl().get_treegui().getTreeModel().nodeChanged(parent);
										//this.getTreeCtrl().get_treegui().getTreeModel().valueForPathChanged(parent., newValue)
									}
								}
							} else
								event.addAlerts((AlertVO)o);
						}
						else { // new element.
							String parent = aVO.getParent();
							DefaultMutableTreeNode parentNode = getParentNode(parent, this.treeCtrl.getGui().getTree());

							this.treeCtrl.addParentToTree(aVO, parentNode);
						}
					}

				}
			}
		}
		this.treeCtrl.getGui().repaint();
		treeCtrl.SetInitializing(false);
	}

	// 
	public DefaultMutableTreeNode getParentNode(String parentId,
			javax.swing.JTree tree) throws ParmException {
		if (parentId.length() < 1)
			throw new IllegalArgumentException("parentID has no length");
		DefaultMutableTreeNode returnNode = null;

		DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) tree
				.getModel().getRoot();
		int childCount = rootNode.getChildCount();

		DefaultMutableTreeNode almostRootNode;
		for (int i = 0; i < childCount; i++) { // for each "root" node in tree
			almostRootNode = (DefaultMutableTreeNode) rootNode.getChildAt(i);
			returnNode = findNodeByPk(almostRootNode, parentId);
			if (returnNode != null)
				return returnNode;
		}
		return returnNode;
	}

	private DefaultMutableTreeNode findNodeByPk(DefaultMutableTreeNode node,
			String parentPk) throws ParmException {
		if (node == null)
			node = treeCtrl.getGui().getRootnode();
		if (parentPk == null)
			throw new IllegalArgumentException("Passed parentPk is null");

		Object o = node.getUserObject();
		if (o.getClass().equals(ObjectVO.class)) {
			ObjectVO oVO = (ObjectVO) o;
			if (oVO.getObjectPK().equals(parentPk))
				return node;
		} else if (o.getClass().equals(EventVO.class)) {
			EventVO eVO = (EventVO) o;
			if (eVO.getEventPk().equals(parentPk))
				return node;
		} else if (o.getClass().equals(AlertVO.class)) {
			AlertVO aVO = (AlertVO) o;
			if (aVO.getAlertpk().equals(parentPk))
				return node;
		}

		int childCount = node.getChildCount();
		for (int i = 0; i < childCount; i++) {
			DefaultMutableTreeNode child = (DefaultMutableTreeNode) node
					.getChildAt(i);
			DefaultMutableTreeNode result = findNodeByPk(child, parentPk);
			if (result != null)
				return result;
		}

		return null;
	}
	
	public DefaultMutableTreeNode findNodeByPk(String pk) throws ParmException {
		
		DefaultMutableTreeNode node = PAS.get_pas().get_parmcontroller().getTreeCtrl().getGui().getRootnode();
		
		for(int i=0;i<node.getChildCount();i++){
			Object o = node.getUserObject();
			if (o.getClass().equals(ObjectVO.class)) {
				ObjectVO oVO = (ObjectVO) o;
				if (oVO.getObjectPK().equals(pk))
					return node;
			} else if (o.getClass().equals(EventVO.class)) {
				EventVO eVO = (EventVO) o;
				if (eVO.getEventPk().equals(pk))
					return node;
			} else if (o.getClass().equals(AlertVO.class)) {
				AlertVO aVO = (AlertVO) o;
				if (aVO.getAlertpk().equals(pk))
					return node;
			}
	
			int childCount = node.getChildCount();
			for (int j = 0; j < childCount; j++) {
				DefaultMutableTreeNode result = findNodeByPk(node, pk);
				if (result != null)
					return result;
			}
		}

		return null;
	}

	// methods for searching for teh node
	public static DefaultMutableTreeNode findNode(javax.swing.JTree aTree,
			Object aUserObject) {
		if (aTree == null)
			throw new IllegalArgumentException("Passed tree is null");
		if (aUserObject == null)
			throw new IllegalArgumentException("Passed user object is null");
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) aTree.getModel()
				.getRoot();
		return findNodeImpl(node, aUserObject);
	}

	private static DefaultMutableTreeNode findNodeImpl(
			DefaultMutableTreeNode aNode, Object aUserObject) {
		if (aNode.getUserObject().equals(aUserObject))
			return aNode;
		int childCount = aNode.getChildCount();
		for (int i = 0; i < childCount; i++) {
			DefaultMutableTreeNode child = (DefaultMutableTreeNode) aNode
					.getChildAt(i);
			DefaultMutableTreeNode result = findNodeImpl(child, aUserObject);
			if (result != null)
				return result;
		}
		return null;
	}

	public boolean checkObject(Object object, Object type) {
		boolean isValid = false;
		if (object.getClass().equals(type)) {
			isValid = true;
		}
		return isValid;
	}

	public Object getObjectFromTree() {
		if (treeCtrl.getSelPath() != null) {
			DefaultMutableTreeNode remNode = (DefaultMutableTreeNode) treeCtrl
					.getSelPath().getLastPathComponent();
			Object object = remNode.getUserObject();
			return object;
		} else
			return null;
	}

	private void initMap(String[] args) {
		mapCtrl = new MapController(args);
		map = mapCtrl.getMapPanel();
	}

	public int getHighestTemp() {
		this.tempPK++;
		return this.tempPK;
	}

	public MapPanel getMap() {
		return map;
	}
	

	public void valueChanged(TreeSelectionEvent e) { // fires when selecting
														// new nodes.
		// clear map
		mapClear(); // På alert må jeg finne parent og tegne opp alle for så å sette den valgte til filled
		updateShape(null);
		updateShapeFilled(null);
		
		// #1: Get selected Object.
		javax.swing.JTree tree = (javax.swing.JTree) e.getSource();

		TreePath path = tree.getSelectionPath();
		this.treeCtrl.setSelPath(path);

		if (path != null) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
			Object o = node.getUserObject();
			gui.getEdit().setEnabled(true);

			//PolygonStruct p = null;

			if (o.getClass().equals(ObjectVO.class)) {
				ParmVO p = (ParmVO)o;
				addShapeToDrawQueue(p.getM_shape());
			} else if(o.getClass().equals(AlertVO.class)) {
				setSelectedAlert(o);				
			} else if (o.getClass().equals(EventVO.class)) {
				// Av en eller annen merkelig grunn mister event innholdet i alertlisten, dette må jeg sjekke om skjer og fikse det
				EventVO event = (EventVO)o;
				setSelectedAlert(null);
				// Her må jeg nesten sjekke om childcount fra treet stemmer med childcount i selve objektet
				int count = node.getChildCount();
				if(count != event.getAlertListe().size()){				
					event.getAlertListe().clear();
					for(int i=0;i<count;i++){
						DefaultMutableTreeNode tempNode = (DefaultMutableTreeNode)node.getChildAt(i);
						event.getAlertListe().add(tempNode.getUserObject());
					}
				}
				
				if(event.getEpicentreX() != 0.0 && event.getEpicentreY() != 0.0) {
					UnknownShape epicentre = new UnknownShape();
					epicentre.set_epicentre(new MapPoint(getMapNavigation(),
							new MapPointLL(event.getEpicentreX(),event.getEpicentreY())));
					//addShapeToDrawQueue(epicentre);
					PAS.get_pas().get_parmcontroller().addShapeToDrawQueue(epicentre);
				}
				for(Object obj : event.getAlertListe())
				{
					AlertVO avo = (AlertVO)obj;
					avo.getM_shape().calc_coortopix(Variables.getNavigation());
					PAS.get_pas().get_parmcontroller().addShapeToDrawQueue(avo.getM_shape());
				}
				
				log.debug("Event = " + event.getName() + " " + event.getAlertListe().size());
				//showAlertShape(event);
			}
			// register the event invoker with map-panel..

			// enable/disable the correct menu-items..

			gui.getEdit().setEnabled(true);
			gui.getDelete().setEnabled(true);

			if (o.getClass().equals(EventVO.class)) { // if user right clicks
				//checkRightsManagement();										// Event
				//gui.getMenuNew().setEnabled(true);
//				gui.getEvent().setEnabled(false);
//				gui.getObject().setEnabled(false);
//				gui.getObjectfolder().setEnabled(false);
//				gui.getAlert().setEnabled(true);
			} else if (o.getClass().equals(ObjectVO.class)) { // if user right
																// clicks Object

				ObjectVO oVO = (ObjectVO) o;
				if (!oVO.isObjectFolder()) { // not folder, disable all but
												// new events.
					//checkRightsManagement();
					//gui.getMenuNew().setEnabled(true);
//					gui.getEvent().setEnabled(true);
//					gui.getObject().setEnabled(false);
//					gui.getObjectfolder().setEnabled(false);
//					gui.getAlert().setEnabled(false);
				} else {
					gui.getMenuNew().setEnabled(true);
//					gui.getEvent().setEnabled(false);
					gui.getObject().setEnabled(true);
					gui.getObjectfolder().setEnabled(true);
//					gui.getAlert().setEnabled(false);
				}
			} else if (o.getClass().equals(AlertVO.class)) { // if user right
																// clicks Alert
//				gui.getMenuNew().setEnabled(false);
			}

			else { // if user right-clicks on nothing
				gui.getEdit().setEnabled(false);
				gui.getDelete().setEnabled(false);

				gui.getMenuNew().setEnabled(true);
//				gui.getEvent().setEnabled(false);
				gui.getObject().setEnabled(true);
				gui.getObjectfolder().setEnabled(true);
//				gui.getAlert().setEnabled(false);
			}

			// #2: draw polygon.
//			if (p != null && !o.getClass().equals(EventVO.class)) {
//				updatePolygon(p);
//			}
			//mapRedraw();
			PAS.get_pas().kickRepaint();
			//hihi tiss tass
		}
	}
	

	
	private void showAlertShape(EventVO e, MapPanel map) {
		/*Iterator it = e.getAlertListe().iterator();
		clearDrawQueue();
		drawLayers();
		while(it.hasNext()){
			AlertVO a = (AlertVO)it.next();
			PolygonStruct p = new PolygonStruct(this.getMapSize(),Color.PINK,Color.PINK);
			//p.add_coor(a.getM_polygon().get)
			try {
				getPolystructCoords(p, a.getM_shape().typecast_polygon().get_coors_lon(), a.getM_shape().typecast_polygon().get_coors_lat());
						
				addShapeToDrawQueue(p);
			} catch(Exception err) {
				log.debug(err.getMessage());
				log.warn(err.getMessage(), err);
				Error.getError().addError("MainController","Exception in showAlertPolygon",err,1);
			}
			// draw(Graphics g, Navigation nav, boolean b_dashed, boolean b_finalized, boolean b_editmode, Point p)
			// PolygonStruct
		    // finalized = true => ikke siste linje tegnet
			// editmode => true => filled, false => skravur
		}
		mapRedraw();*/
	}
	
	private ArrayList<Object> checkIfLocked(Object o) {
		
		ArrayList<Object> lockedObjects = new ArrayList<Object>();
		checkThis(lockedObjects, o);
		
		return lockedObjects;
	}
	
	// Hører til checkIfLocked
	private void checkThis(ArrayList<Object> a, Object o) {
		Iterator<Object> it;
		if(o.getClass().equals(AlertVO.class)) {
			if(((AlertVO)o).getLocked()==1)
				a.add(o);
			
		} else if(o.getClass().equals(EventVO.class)) {
			it = ((EventVO)o).getAlertListe().iterator();
			while(it.hasNext())
				checkThis(a,it.next());
		
		} else if(o.getClass().equals(ObjectVO.class)) {
			it = ((ObjectVO)o).getList().iterator();
			while(it.hasNext())
				checkThis(a,it.next());
		}
	}
	
	public void setSelectedAlert(Object o) {
		PAS.get_pas().get_parmcontroller().clearShapesFromDrawQueue();
		ParmVO alert = (ParmVO)o;
		selectedObject = alert;
		if(o==null)
			return;
		try {
			ShapeStruct shape = null;
			if(alert.getM_shape() != null) {
				shape = (ShapeStruct)alert.getM_shape().clone();
			}
			
			DefaultMutableTreeNode dmt = findNodeByPk(((AlertVO)o).getParent());
			if(dmt == null) { // Denne er dersom det har skjedd en refresh mot databasen og parent har fått oppdatert pk
				dmt = findNodeByPk(((AlertVO)o).getAlertpk());
				dmt = (DefaultMutableTreeNode)dmt.getParent();
			}
			EventVO event = (EventVO)dmt.getUserObject();
			for(int i=0;i<event.getAlertListe().size();i++) {
				ParmVO p = (ParmVO)event.getAlertListe().get(i);
				/*if(alert.getPk().equals(p.getPk()) && shape != null)
				{
					p.getM_shape().calc_coortopix(getMapNavigation());
					addShapeToDrawQueue(p.getM_shape());
					setFilled(p.getM_shape());
				}
				else
				{
					p.getM_shape().calc_coortopix(getMapNavigation());
					addShapeToDrawQueue(p.getM_shape());
				}*/
				p.getM_shape().calc_coortopix(getMapNavigation());
				//addShapeToDrawQueue(p.getM_shape());
				PAS.get_pas().get_parmcontroller().addShapeToDrawQueue(p.getM_shape());
				
			}
			// Her må jeg legge til objektet i edit
			// Dette må gjøres i send option toolbar egentlig
		} catch(Exception ex) {
			Error.getError().addError("MainController","Exception in valueChanged, error finding parentnode",ex,Error.SEVERITY_ERROR);
		}
	}
	
	
	protected void getPolystructCoords(PolygonStruct p, ArrayList<Double> lon, ArrayList<Double> lat){
		for(int i=0;i<lon.size();i++)
			p.add_coor((Double)lon.get(i),(Double)lat.get(i));
	}
	public void setDrawMode(ShapeStruct p) {
		
	}
	
	public void setFilled(ShapeStruct p) {
		
	}
	
	public void treeNodesChanged(TreeModelEvent e) {

	}

	public void treeNodesInserted(TreeModelEvent e) {

	}

	public void treeNodesRemoved(TreeModelEvent e) {
//		DefaultMutableTreeNode[] parentList = (DefaultMutableTreeNode[]) e.getPath();
//		DefaultMutableTreeNode parent = parentList[0];
//		log.debug("Deleted node parent: " + parent.getUserObject());
	}

	public void treeStructureChanged(TreeModelEvent e) {
	
	}
	
	public void setExpandedNodes() {
		new XmlReader().readNodesExpandFromFile(treeCtrl.getGui().getTree(), (ParmController)this);
	}
	
	// Brukes sammen med userprofile for å sette restriksjoner ved lave tilgangsnivåer
	public void checkRightsManagement() {
		if(!PAS.get_pas().get_rightsmanagement().read_parm()) { // Da må vi lukke programmet
			OtherActions.PARM_CLOSE.actionPerformed(new ActionEvent(this,ActionEvent.ACTION_PERFORMED, "act_close_parm"));
		}
		else { // Skal kun a lese tilgang
			treeCtrl.get_treegui().getMenuNew().setEnabled(false);
			treeCtrl.get_treegui().getDelete().setEnabled(false);
		}
		
		if(PAS.get_pas().get_rightsmanagement().write_parm()) { // Skal ha skrivetilgang, men ikke slett
			treeCtrl.get_treegui().getMenuNew().setEnabled(true);
			treeCtrl.get_treegui().getDelete().setEnabled(false);
		}
		
		if(PAS.get_pas().get_rightsmanagement().delete_parm()) { // Skal ha alle rettigheter skriv + slett
			treeCtrl.get_treegui().getDelete().setEnabled(true);
		}
		if(!PAS.get_pas().get_rightsmanagement().cansend()) { //disable SnapSend hvis en ikke har sende-rettigheter
			treeCtrl.get_treegui().getSnapSending().setEnabled(false);
		}
		if(!PAS.get_pas().get_rightsmanagement().cansimulate()) { //disable SnapSimulation hvis en ikke har simulerings-rettigheter
			treeCtrl.get_treegui().getSnapSimulation().setEnabled(false);
		}
		if(!PAS.get_pas().get_rightsmanagement().cansend() && !PAS.get_pas().get_rightsmanagement().cansimulate())
			treeCtrl.get_treegui().getSnapTest().setEnabled(false);
	}
	/*
	public class PACategory {
		private String pk;
		private String name;
		
		public PACategory(String pk, String name) {
			this.pk = pk;
			this.name = name;
		}
		
		public String get_pk() {
			return pk;
		}
		public void set_pk(String pk) {
			this.pk = pk;
		}
		public String get_name() {
			return name;
		}
		public void set_name(String name) {
			this.name = name;
		}
	}*/
	
	/*public static void main(String[] args) {
		try {
			new MainController(args);
		} catch (FileNotFoundException e) {
			log.warn(e.getMessage(), e);
			Error.getError().addError("MainController","Exception in main",e,1);
		} catch (ParmException e) {
			log.warn(e.getMessage(), e);
			Error.getError().addError("MainController","Exception in main",e,1);
		}
	}*/
}
