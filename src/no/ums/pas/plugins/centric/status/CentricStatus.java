package no.ums.pas.plugins.centric.status;

import java.awt.Button;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import no.ums.pas.PAS;
import no.ums.pas.core.defines.DefaultPanel;
import no.ums.pas.core.mainui.EastContent;
import no.ums.pas.core.ws.WSGetStatusItems;
import no.ums.pas.maps.defines.ShapeStruct;
import no.ums.pas.maps.defines.converters.UShapeToShape;
import no.ums.pas.plugins.centric.CentricEastContent;
import no.ums.pas.plugins.centric.CentricSendOptionToolbar;
import no.ums.pas.plugins.centric.CentricVariables;
import no.ums.pas.plugins.centric.status.CentricOperatorStatus.OPERATOR_STATUS;
import no.ums.pas.plugins.centric.ws.WSCentricStatus;
import no.ums.pas.ums.tools.ImageLoader;
import no.ums.pas.ums.tools.StdTextArea;
import no.ums.pas.ums.tools.StdTextLabel;
import no.ums.pas.ums.tools.TextFormat;
import no.ums.ws.parm.CBORIGINATOR;
import no.ums.ws.parm.CBSENDINGRESPONSE;
import no.ums.ws.pas.status.CBPROJECTSTATUSREQUEST;
import no.ums.ws.pas.status.CBPROJECTSTATUSRESPONSE;
import no.ums.ws.pas.status.CBSTATUS;
import no.ums.ws.pas.status.ULBAHISTCELL;
import no.ums.ws.pas.status.ULBASENDING;
import no.ums.ws.pas.status.ULOGONINFO;

public class CentricStatus extends DefaultPanel implements ComponentListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTabbedPane m_status_tabbed;
	public JTabbedPane getTabbedPane() { return m_status_tabbed; }
	
	private CentricEventStatus m_event;
	private CentricMessages m_messages;
	
	public CentricMessages get_messages() { return m_messages; }
	public CentricEventStatus get_event() { return m_event; }
	
	private CBPROJECTSTATUSREQUEST cbsreq = new CBPROJECTSTATUSREQUEST();
	private CBPROJECTSTATUSRESPONSE cbsres = null;
	
	public CBPROJECTSTATUSRESPONSE getResultSet()
	{
		return cbsres;
	}
	
	private boolean ready = true;
	public boolean isReady() { return ready; }
	private WSCentricStatus ws;
	private boolean b_active = true;
	public void setClosed() { b_active = false; }
	public boolean isClosed() { return !b_active; }
	
	private Hashtable<Long, CentricMessageStatus> hash_messagestatus;

	
	@Override
	public void componentResized(ComponentEvent e) {
		//super.componentResized(e);
		if(getWidth()<=0||getHeight()<=0) {
			
		}
		setPreferredSize(new Dimension(PAS.get_pas().get_eastcontent().getWidth()-10,PAS.get_pas().get_eastcontent().getHeight()-20));
		m_status_tabbed.setPreferredSize(new Dimension(getPreferredSize().width-10,getPreferredSize().height-10));
		m_event.componentResized(e);
		//m_messages.componentResized(e);
	}

	private CBSENDINGRESPONSE res;
	
	public void set_cbsendingresponse(CBSENDINGRESPONSE res) { 
		this.res = res;
		// update infoting
		getCBStatus(res);
	}
	
	private void getCBStatus(CBSENDINGRESPONSE res) {
		//ImageIcon icon = ImageLoader.load_icon("remembermilk_orange.gif");
		//for(int i=0;i<m_status_tabbed.getComponentCount();++i)
		//	m_status_tabbed.setIconAt(i, icon);
		ready = false;

		ULOGONINFO l = new ULOGONINFO();
		l.setLComppk(PAS.get_pas().get_userinfo().get_comppk());
		l.setLDeptpk(PAS.get_pas().get_userinfo().get_current_department().get_deptpk());
		l.setLUserpk(new Long(PAS.get_pas().get_userinfo().get_userpk()));
		l.setSzPassword(PAS.get_pas().get_userinfo().get_passwd());
		l.setSzStdcc(PAS.get_pas().get_userinfo().get_current_department().get_stdcc());
		l.setSessionid(PAS.get_pas().get_userinfo().get_sessionid());
		
		//cbsreq = new CBPROJECTSTATUSREQUEST();
		cbsreq.setLProjectpk(res.getLProjectpk());
		cbsreq.setLogon(l);
		
		try {
			if(isClosed())
			{
				ready = true;
				return;
			}
			ws = new WSCentricStatus(this,"act_status_downloaded",cbsreq);
			ws.start();
			//WSCentricStatus getStatus = new WSCentricStatus(this,"act_status_downloaded",cbsreq,this);
			//getStatus.start();
		} catch(Exception ex) {
			ready = true;
		}
		finally
		{			
		}
	}
	
	public CentricStatus(CBSENDINGRESPONSE res) {
		super();
		this.res = res;
		hash_messagestatus = new Hashtable<Long, CentricMessageStatus>();
		add_controls();
		cbsreq = new CBPROJECTSTATUSREQUEST();
		cbsreq.setLTimefilter(0);
		getCBStatus(res);
		
	}
	
	public void putMessageStatus(long refno, CentricMessageStatus cms)
	{
		hash_messagestatus.put(refno, cms);
	}
	
	public Hashtable<Long, CentricMessageStatus> getHashMessageStatus()
	{
		return hash_messagestatus;
	}
	
	public boolean containsMessageStatus(long refno)
	{
		if(hash_messagestatus.containsKey(refno))
			return true;
		return false;
	}

	@Override
	public void add_controls() {
		//DefaultPanel.ENABLE_GRID_DEBUG = true;
		m_status_tabbed = new JTabbedPane();
		
		
		m_gridconst.fill = GridBagConstraints.BOTH;
		
		
		set_gridconst(0, inc_panels(), 1, 1);
		add(m_status_tabbed, m_gridconst);
		
		setPreferredSize(new Dimension(PAS.get_pas().get_eastcontent().getWidth()-10,PAS.get_pas().get_eastcontent().getHeight()-50));
		m_status_tabbed.setPreferredSize(new Dimension(getPreferredSize().width-10,getPreferredSize().height-10));
		
		m_event = new CentricEventStatus(res, this);
		m_status_tabbed.addTab("Event Name", m_event);
		
		
		m_messages = new CentricMessages(this);
		m_status_tabbed.addTab(PAS.l("main_sending_lba_heading_messages"), m_messages);
		
		/*
		set_gridconst(0, inc_panels(), 1, 1);
		add(m_lbl_message, m_gridconst);
		set_gridconst(1, get_panel(), 2, 1);
		add(m_txt_message, m_gridconst);
		
		set_gridconst(0, inc_panels(), 2, 1);
		add(m_message_tabbed, m_gridconst);
		
		set_gridconst(0, inc_panels(), 2, 1);
		add(m_lbl_status, m_gridconst);
		set_gridconst(1, get_panel(), 2, 1);
				
		set_gridconst(0, inc_panels(), 1, 1); //5
		m_btn_kill.setPreferredSize(new Dimension(30,20));
		add(m_btn_kill, m_gridconst);
		add_spacing(DIR_HORIZONTAL, 10);
		set_gridconst(1, get_panel(), 1, 1);
		m_btn_update.setPreferredSize(new Dimension(50,20));
		add(m_btn_update, m_gridconst);
		add_spacing(DIR_HORIZONTAL, 10);
		set_gridconst(2, get_panel(), 1, 1);
		m_btn_resend.setPreferredSize(new Dimension(50,20));
		add(m_btn_resend, m_gridconst);
		add_spacing(DIR_HORIZONTAL, 10);
		set_gridconst(3, get_panel(), 1, 1);
		m_btn_send_to_address_book.setPreferredSize(new Dimension(150,20));
		add(m_btn_send_to_address_book, m_gridconst);
		*/
		
		this.revalidate();
		repaint();
		init();
	}

	@Override
	public void init() {
		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("act_status_downloaded")){
			cbsres = (CBPROJECTSTATUSRESPONSE)e.getSource();
			if(cbsres.getProject()!=null && Long.parseLong(cbsres.getProject().getLProjectpk())>0)
			{
				updateProjectStatus(cbsres);
				cbsreq.setLTimefilter(cbsres.getLDbTimestamp());
				for(int i=0;i<m_status_tabbed.getComponentCount();++i)
					m_status_tabbed.setIconAt(i, null);
				System.out.println("Status update complete");
				
			}
			else
			{
				//Status update failed
				System.out.println("Status update failed");
			}
			
			ready = true;
		}
	}
	
	
	protected void updateProjectStatus(CBPROJECTSTATUSRESPONSE cbp)
	{
		if(isClosed())
			return;
		getTabbedPane().setTitleAt(0, cbp.getProject().getSzProjectname());
		JTabbedPane tp = get_messages().get_tpane();
		CentricSendOptionToolbar csend = CentricVariables.centric_send;
		csend.set_projectpk(
				Long.parseLong(cbp.getProject().getLProjectpk()), 
				cbp.getProject().getSzProjectname());

		CentricMessageStatus ms;
		//CBSTATUS cbs;
		
		boolean found;
		int n_sendings = 0;
		int n_sendings_active = 0;
		Hashtable<Long, Long> sendings = new Hashtable<Long, Long>();
		Hashtable<Long, Long> active = new Hashtable<Long, Long>();

		
		List<CBSTATUS> cbstatuslist = cbp.getStatuslist().getCBSTATUS();
		for(int j=0; j < cbstatuslist.size(); ++j)
		{
			CBSTATUS currentstatus = cbstatuslist.get(j);
			CentricMessageStatus currentui = null;
			
			//get or create a UI pane
			if(containsMessageStatus(currentstatus.getLRefno())) //Already added to tabbed pane, only update data
			{
				currentui = getHashMessageStatus().get(currentstatus.getLRefno());
			}
			else //new status, needs to be added to tabbed pane
			{
				currentui = new CentricMessageStatus(get_messages(), currentstatus);
				putMessageStatus(currentstatus.getLRefno(), currentui); //add refno and pointer to hash
				currentui.get_txt_message().setText(currentstatus.getMdv().getSzMessagetext());
				tp.add(currentstatus.getSzSendingname(), currentui);
			}
			//Update data in UI pane
			if(currentui!=null) //just to be sure we have an existing or new pointer
				currentui.UpdateStatus(currentstatus, cbp.getLDbTimestamp());
			sendings.put(new Long(currentstatus.getLRefno()), new Long(currentstatus.getLRefno()));
			OPERATOR_STATUS status = currentui.getOperatorStatus();
			String lbl_pane = "";
			switch(status)
			{
			case INITIALIZING:
			case ACTIVE:
				lbl_pane = "A ";
				active.put(currentstatus.getLRefno(), currentstatus.getLRefno());
				break;
			case FINISHED:
				lbl_pane = "F ";
				break;
			}
			lbl_pane += currentstatus.getMdv().getSzSendingname();
			try
			{
				int n = tp.indexOfComponent(currentui);
				if(n>=0)
					tp.setTitleAt(n, lbl_pane);
			}
			catch(Exception e)
			{
				
			}
			ShapeStruct shape = UShapeToShape.ConvertUShape_to_ShapeStruct(currentstatus.getShape());
			if(shape!=null)
			{
				shape.setShapeId(currentstatus.getLRefno());
				shape.shapeName = currentstatus.getSzSendingname();
				shape.set_fill_color(new Color(255, 50, 50, 100));
				shape.set_border_color(new Color(255, 50, 50, 200));
				shape.set_text_color(new Color(255, 255, 255, 255));
				shape.set_text_bg_color(new Color(50, 0, 0, 100));
				PAS.pasplugin.addShapeToPaint(shape);
			}

		}
		/*Hashtable<Long, Long> sendings = new Hashtable<Long, Long>();
		Hashtable<Long, Long> active = new Hashtable<Long, Long>();
		
		for(int j=0;j<cbp.getStatuslist().getCBSTATUS().size();++j) {
			found = false;
			cbs = cbp.getStatuslist().getCBSTATUS().get(j);
			sendings.put(new Long(cbs.getLRefno()), new Long(cbs.getLRefno()));
			
			List<ULBASENDING> arr_operators = cbs.getOperators().getULBASENDING();
			
			for(int oper=0; oper < arr_operators.size(); oper++)
			{
				ULBASENDING operator = arr_operators.get(oper);
				
				if(operator.getLStatus()<1000) // All statuses under 1000 are still active
					active.put(new Long(cbs.getLRefno()), new Long(cbs.getLRefno()));
				
				for(int i=0;i<tp.getComponentCount();++i) {
					ms = (CentricMessageStatus)tp.getComponentAt(i);
					
					// Does the message already exist?
					if(ms.get_refno() == cbs.getLRefno()) {
						ms.get_txt_message().setText(cbs.getMdv().getSzMessagetext());
						ms.setName(cbs.getSzSendingname());
						
						if(operator.getLStatus() >= 540)  // Active
							tp.setTitleAt(i,"A " + cbs.getSzSendingname());
						if(operator.getLStatus() == 1000) // Finished
							tp.setTitleAt(i,"F " + cbs.getSzSendingname());

						CentricOperatorStatus cos;
						
						
						boolean operator_found = false;
						for(int k=0;k<ms.get_tpane().getComponentCount();++k) {
							//cos.get_lbl_channel().setText(histcell.g);
							cos = (CentricOperatorStatus)ms.get_tpane().getComponentAt(k);
							if(cos.get_operator() == operator.getLOperator()) {
								setOperatorValues(cbp, cbs, operator, cos);
								operator_found = true;
							}
						}
						if(!operator_found) {
							cos = new CentricOperatorStatus(ms, false, operator.getLOperator());
							setOperatorValues(cbp, cbs, operator, cos);
							ms.get_tpane().add(operator.getSzOperator(), cos);
						}
						
						found = true;
					}
				}
			}
			if(!found) {
				CentricMessageStatus tmp = new CentricMessageStatus(get_messages(), cbp.getStatuslist().getCBSTATUS().get(j).getLRefno());
				tmp.get_txt_message().setText(cbs.getMdv().getSzMessagetext());

				if(cbs.getLCombinedStatus() < 1000)  // Active
					tp.add("A " + cbs.getSzSendingname(), tmp);
				if(cbs.getLCombinedStatus() >= 1000) // Finished
					tp.add("F " + cbs.getSzSendingname(), tmp);
				
				tp.setSelectedComponent(tmp);
				for(int oper = 0; oper < arr_operators.size(); ++oper)
				{
					ULBASENDING operator = arr_operators.get(oper);
					CentricOperatorStatus cos = new CentricOperatorStatus(tmp, false, operator.getLOperator());

					setOperatorValues(cbp, cbs, operator, cos);
					tmp.get_tpane().add(operator.getSzOperator(),cos);
				}
			}
			ShapeStruct shape = UShapeToShape.ConvertUShape_to_ShapeStruct(cbs.getShape());
			if(shape!=null)
			{
				shape.setShapeId(cbs.getLRefno());
				shape.shapeName = cbs.getSzSendingname();
				shape.set_fill_color(new Color(255, 50, 50, 100));
				shape.set_border_color(new Color(255, 50, 50, 200));
				shape.set_text_color(new Color(255, 255, 255, 255));
				shape.set_text_bg_color(new Color(50, 0, 0, 100));
				PAS.pasplugin.addShapeToPaint(shape);
			}
		}
		*/
		
		get_event().get_sent().setText(String.valueOf(sendings.size()));
		get_event().get_active().setText(String.valueOf(active.size()));
		
		
		PAS.get_pas().kickRepaint();
		
		//this is the first status download. Load map
		if(cbsreq.getLTimefilter()<=0)
		{
			PAS.pasplugin.onSetAppTitle(PAS.get_pas(), "", PAS.get_pas().get_userinfo());
			PAS.pasplugin.onMapGotoShapesToPaint();
		}

	}
	
}




