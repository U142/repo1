package no.ums.pas.plugins.centric.ws;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.JTabbedPane;
import javax.xml.namespace.QName;

import com.sun.xml.internal.bind.v2.schemagen.xmlschema.List;

import no.ums.pas.PAS;
import no.ums.pas.core.ws.WSThread;
import no.ums.pas.core.ws.vars;
import no.ums.pas.plugins.centric.status.CentricMessageStatus;
import no.ums.pas.plugins.centric.status.CentricMessages;
import no.ums.pas.plugins.centric.status.CentricOperatorStatus;
import no.ums.pas.plugins.centric.status.CentricStatus;
import no.ums.pas.tas.statistics.UMSChartFrame;
import no.ums.pas.ums.tools.TextFormat;
import no.ums.ws.pas.status.CBSTATUS;
import no.ums.ws.pas.status.ULBAHISTCELL;
import no.ums.ws.pas.status.ULOGONINFO;
import no.ums.ws.pas.status.CBPROJECTSTATUSREQUEST;
import no.ums.ws.pas.status.CBPROJECTSTATUSRESPONSE;
import no.ums.ws.pas.status.PasStatus;

import java.util.Collection;
import java.util.Hashtable;

public class WSCentricStatus extends WSThread {

	private String action;
	private CBPROJECTSTATUSREQUEST cbpreq;
	private CBPROJECTSTATUSRESPONSE cbpres;
	private CentricStatus status_panel;
	
	public WSCentricStatus(ActionListener callback, String action, CBPROJECTSTATUSREQUEST request, CentricStatus status) {
		super(callback);
		this.cbpreq = request;
		this.action = action;
		this.status_panel = status;
	}

	@Override
	public void OnDownloadFinished() {
		if(m_callback!=null && cbpres!=null) {
			status_panel.getTabbedPane().setTitleAt(0, cbpres.getProject().getSzProjectname());
			JTabbedPane tp = status_panel.get_messages().get_tpane();
			
			CentricMessageStatus ms;
			CBSTATUS cbs;
			
			boolean found;
			
			Hashtable<Long, Long> sendings = new Hashtable<Long, Long>();
			Hashtable<Long, Long> active = new Hashtable<Long, Long>();
			
			for(int j=0;j<cbpres.getStatuslist().getCBSTATUS().size();++j) {
				found = false;
				cbs = cbpres.getStatuslist().getCBSTATUS().get(j);
				sendings.put(new Long(cbs.getLRefno()), new Long(cbs.getLRefno()));
				
				if(cbs.getLStatus()<1000) // All statuses under 1000 are still active
					active.put(new Long(cbs.getLRefno()), new Long(cbs.getLRefno()));
				
				for(int i=0;i<tp.getComponentCount();++i) {
					ms = (CentricMessageStatus)tp.getComponentAt(i);
					
					// Does the message already exist?
					if(ms.get_refno() == cbs.getLRefno()) {
						ms.get_txt_message().setText(cbs.getMdv().getSzMessagetext());
						ms.setName(cbs.getSzSendingname());
						
						if(cbs.getLStatus() >= 540)  // Active
							tp.setTitleAt(i,"A " + cbs.getSzSendingname());
						if(cbs.getLStatus() == 1000) // Finished
							tp.setTitleAt(i,"F " + cbs.getSzSendingname());

						CentricOperatorStatus cos;
						
						
						boolean operator_found = false;
						for(int k=0;k<ms.get_tpane().getComponentCount();++k) {
							//cos.get_lbl_channel().setText(histcell.g);
							cos = (CentricOperatorStatus)ms.get_tpane().getComponentAt(k);
							if(cos.get_operator() == cbs.getLOperator()) {
								setOperatorValues(cbs, cos);
								operator_found = true;
							}
						}
						if(!operator_found) {
							cos = new CentricOperatorStatus(ms, false, cbs.getLOperator());
							setOperatorValues(cbs, cos);
							ms.get_tpane().add(cbs.getSzOperator(), cos);
						}
						
						found = true;
					}
				}
				if(!found) {
					CentricMessageStatus tmp = new CentricMessageStatus(status_panel.get_messages(), cbpres.getStatuslist().getCBSTATUS().get(j).getLRefno());
					
					if(cbs.getLStatus() >= 540)  // Active
						tp.add("A " + cbs.getSzSendingname(), tmp);
					if(cbs.getLStatus() == 1000) // Finished
						tp.add("F " + cbs.getSzSendingname(), tmp);
					
					tp.setSelectedComponent(tmp);
					
					CentricOperatorStatus cos = new CentricOperatorStatus(tmp, false, cbs.getLOperator());

					setOperatorValues(cbs, cos);
					tmp.get_tpane().add(cbs.getSzOperator(),cos);
				}
				
			}
			status_panel.get_event().get_sent().setText(String.valueOf(sendings.size()));
			status_panel.get_event().get_active().setText(String.valueOf(active.size()));
			updateTotal();
			m_callback.actionPerformed(new ActionEvent(cbpres, ActionEvent.ACTION_PERFORMED, action));
		}
		
	}
	
	private void updateTotal() {
		
		CentricOperatorStatus cos;
		JTabbedPane tp = status_panel.get_messages().get_tpane();
		
		for(int j=0;j<tp.getComponentCount();++j) {
			int total=0;
			int unknown=0;
			int total_ok=0;
			float total_percent=0;
			long start = 0;
			long timestamp = 0;
			int channel=0;
			
			CentricMessageStatus ms = (CentricMessageStatus)tp.getComponentAt(j);
			
			for(int i=1;i<ms.get_tpane().getComponentCount();++i) {
				cos = (CentricOperatorStatus)ms.get_tpane().getComponentAt(i);
				total += cos.total<0?0:cos.total;
				unknown += cos.unknown<0?0:cos.unknown;
				total_ok += cos.total_ok<0?0:cos.total_ok;
				total_percent += cos.percent;
				start = cos.start;
				timestamp = cos.timestamp;
				channel = cos.channel;
			}
			
			cos = (CentricOperatorStatus)ms.get_tpane().getComponentAt(0);
			cos.total = total;
			cos.unknown = unknown;
			cos.total_ok = total_ok;
			total_percent = (total_percent/(ms.get_tpane().getComponentCount()-1));
			cos.percent = total_percent;
			cos.get_lbl_channel().setText(String.valueOf(channel));
			cos.get_lbl_completed().setText(String.valueOf(total_ok<0?"N/A":total_ok));
			cos.get_lbl_unknown().setText(String.valueOf(total<0?"N/A":unknown));
			cos.get_lbl_total().setText(String.valueOf(total<0?"N/A":total));
			cos.get_lbl_percent().setText(String.valueOf(total_percent));
			// Start
			cos.get_lbl_start().setText(no.ums.pas.plugins.centric.tools.TextFormat.format_datetime(String.valueOf(start)));
			// Duration
			cos.get_lbl_duration().setText(String.valueOf(TextFormat.datetime_diff_minutes(start,timestamp) + " " + PAS.l("common_minutes_maybe")));
		}
	}
	
	private void setOperatorValues(CBSTATUS cbs, CentricOperatorStatus cos) {
		
		ULBAHISTCELL histcell = null;
		if(cbs.getHistcell().getULBAHISTCELL().size() > 0)
			histcell = cbs.getHistcell().getULBAHISTCELL().get(cbs.getHistcell().getULBAHISTCELL().size()-1);
		
		int total_ok = 0;
		int total_unknown = 0;
		int total = 0;
		float percent = 0;
		
		if(histcell != null) {
			total_ok = histcell.getL2Gok() + histcell.getL3Gok() + histcell.getL4Gok();
			total = histcell.getL2Gtotal() + histcell.getL3Gtotal() + histcell.getL4Gtotal();
			total_unknown = total - total_ok;
			cos.total = total;
			cos.total_ok = total_ok;
			cos.unknown = total_unknown;
			percent = histcell.getLSuccesspercentage();
			
			
		}
		cos.percent = percent;
		cos.get_lbl_completed().setText(String.valueOf(total_ok<0?"N/A":total_ok));
		cos.get_lbl_unknown().setText(String.valueOf(total<0?"N/A":total_unknown));
		cos.get_lbl_total().setText(String.valueOf(total<0?"N/A":total));
		cos.get_lbl_percent().setText(String.valueOf(percent));
		// Channel
		cos.get_lbl_channel().setText(String.valueOf(cbs.getLChannel()));
		cos.channel = cbs.getLChannel();
		// Start
		cos.start = cbs.getLCreatedTs();
		cos.get_lbl_start().setText(no.ums.pas.plugins.centric.tools.TextFormat.format_datetime(String.valueOf(cbs.getLCreatedTs())));
		// Duration
		cos.timestamp = cbpres.getLDbTimestamp();
		cos.get_lbl_duration().setText(String.valueOf(TextFormat.datetime_diff_minutes(cbs.getLCreatedTs(),cbpres.getLDbTimestamp())) + " " + PAS.l("common_minutes_maybe"));
	}

	@Override
	public void call() throws Exception {
		URL wsdl = new URL(vars.WSDL_PASSTATUS);
		QName service = new QName("http://ums.no/ws/pas/status", "PasStatus");
				
		cbpres = new PasStatus(wsdl, service).getPasStatusSoap12().getCBStatus(cbpreq);
	}

	@Override
	protected String getErrorMessage() {
		return "Error getting CB status";
	}

}
