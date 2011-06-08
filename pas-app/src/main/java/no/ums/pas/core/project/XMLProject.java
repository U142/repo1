package no.ums.pas.core.project;

import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.pas.core.dataexchange.HTTPReq;
import no.ums.pas.core.dataexchange.HttpPostForm;
import no.ums.pas.core.mainui.LoadingPanel;
import no.ums.pas.core.webdata.XMLThread;
import no.ums.pas.ums.errorhandling.Error;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.swing.JFrame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class XMLProject extends XMLThread {

    private static final Log log = UmsLog.getLogger(XMLProject.class);

	private Project m_project = new Project();
	protected Project get_project() { return m_project; }
	
	public XMLProject(HttpPostForm is, JFrame parent, LoadingPanel loader,
					  ActionListener callback, String sz_action_command) {
		super(is, Thread.NORM_PRIORITY, null, null, loader, "Creating Project", false, new HTTPReq(""));
		this.set_callbackclass(callback);
		this.set_callbackaction(sz_action_command);
	}
	
	public void parseDoc(Document doc) {
		String sz_projectpk, sz_name, sz_createtimestamp, sz_updatetimestamp;
		if(doc==null) {
			log.debug("doc==null");
			return;
		}
		NodeList list_itemlist = doc.getElementsByTagName("PROJECT");
		if (list_itemlist == null) {
			log.debug("XMLProject.parseDoc() - list_itemlist == null");
			return;
		}
		Node current = list_itemlist.item(0);
		if(current == null) {
			log.debug("XMLProject.parseDoc() - current == null");
		}
		NamedNodeMap nnm_itemlist = (current == null) ? null : current.getAttributes();
		if(nnm_itemlist == null) {
			log.debug("XMLProject.parseDoc() - nnm_itemlist == null");
		}
        else {
            try {
                Node node_projectpk = nnm_itemlist.getNamedItem("l_projectpk");
                Node node_name  = nnm_itemlist.getNamedItem("sz_name");
                Node node_createtimestamp = nnm_itemlist.getNamedItem("l_createtimestamp");
                Node node_updatetimestamp = nnm_itemlist.getNamedItem("l_updatetimestamp");
                sz_projectpk	= node_projectpk.getNodeValue();
                sz_name			= node_name.getNodeValue();
                sz_createtimestamp = node_createtimestamp.getNodeValue();
                sz_updatetimestamp = node_updatetimestamp.getNodeValue();

                log.debug("Projectpk = " + sz_projectpk);
                log.debug("Name = " + sz_name);
                log.debug("ctime = " + sz_createtimestamp);
                log.debug("utime = " + sz_updatetimestamp);

                m_project.set_projectpk(sz_projectpk);
                m_project.set_projectname(sz_name);
                m_project.set_createtimestamp(sz_createtimestamp);
                m_project.set_updatetimestamp(sz_updatetimestamp);
                m_project.set_saved();

                log.debug("Project set");
            } catch(Exception e) {
                log.debug(e.getMessage());
                e.printStackTrace();
                Error.getError().addError("XMLProject","Exception in parseDoc",e,1);
            }
        }
		onDownloadFinished();

		return;
	}
	public void onDownloadFinished() {
		try {
			ActionEvent e = new ActionEvent(get_project(), ActionEvent.ACTION_PERFORMED, get_callback_action());
			get_callback().actionPerformed(e);
		} catch(Exception e) {
			log.debug(e.getMessage());
			e.printStackTrace();
			Error.getError().addError("XMLProject","Exception in onDownloadFinished",e,1);
		}
	}
}