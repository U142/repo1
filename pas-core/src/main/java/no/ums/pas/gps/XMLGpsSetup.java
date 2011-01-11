package no.ums.pas.gps;

import no.ums.pas.PAS;
import no.ums.pas.core.dataexchange.HTTPReq;
import no.ums.pas.core.dataexchange.HttpPostForm;
import no.ums.pas.core.mainui.LoadingPanel;
import no.ums.pas.core.webdata.XMLThread;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;



public class XMLGpsSetup extends XMLThread {
	private GpsSetupReturnCode m_returncode = null;
	protected GpsSetupReturnCode get_returncode() { return m_returncode; }
	
	public XMLGpsSetup(HttpPostForm form, ActionListener callback, LoadingPanel loader) {
		super(form, Thread.NORM_PRIORITY, PAS.get_pas(), null, loader, "GPS Setup", false, new HTTPReq(PAS.get_pas().get_sitename()));
		get_loadingpanel().start_progress(0, "Connecting to GPS Unit");
		m_callbackclass = callback;
	}
	
	public void parseDoc(Document doc) {
		if(doc==null)
			return;
		NodeList list_itemlist = doc.getElementsByTagName("GPSSETUP");
		if (list_itemlist == null) {
			get_pas().add_event("XMLGpsSetup.parseDoc() - list_itemlist == null", null);
			return;
		}
		Node current = list_itemlist.item(0);
		if(current == null) {
			get_pas().add_event("XMLGpsSetup.parseDoc() - current == null", null);
		}
		NamedNodeMap nnm_itemlist = current.getAttributes();
		if(nnm_itemlist == null) {
			get_pas().add_event("XMLGpsSetup.parseDoc() - nnm_itemlist == null", null);
		}
		Node node_objectpk = nnm_itemlist.getNamedItem("sz_objectpk");
		Node node_msgpk = nnm_itemlist.getNamedItem("n_msgpk");
		Node node_answered = nnm_itemlist.getNamedItem("n_answered");
		Node node_text = nnm_itemlist.getNamedItem("sz_text");
		//sz_refno = node_refno.getNodeValue();
		String sz_objectpk = node_objectpk.getNodeValue();
		String sz_msgpk = node_msgpk.getNodeValue();
		String sz_answered = node_answered.getNodeValue();
		String sz_text = node_text.getNodeValue();
		
		m_returncode = new GpsSetupReturnCode(sz_objectpk, new Integer(sz_answered).intValue(), sz_msgpk, sz_text);
		get_loadingpanel().start_progress(1, "GPS Setup");
		
		onDownloadFinished();
		
		return;
	}
	public void onDownloadFinished() {
		get_callback().actionPerformed(new ActionEvent(get_returncode(), ActionEvent.ACTION_PERFORMED, "act_gps_answer"));
	}
}