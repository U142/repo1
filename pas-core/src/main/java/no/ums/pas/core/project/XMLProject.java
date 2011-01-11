package no.ums.pas.core.project;

import javax.swing.JFrame;
import java.awt.event.*;

import no.ums.pas.core.dataexchange.*;
import no.ums.pas.core.mainui.*;
import no.ums.pas.core.webdata.*;
import no.ums.pas.ums.errorhandling.Error;

import org.w3c.dom.*;


public class XMLProject extends XMLThread {
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
			System.out.println("doc==null");
			return;
		}
		NodeList list_itemlist = doc.getElementsByTagName("PROJECT");
		if (list_itemlist == null) {
			System.out.println("XMLProject.parseDoc() - list_itemlist == null");
			return;
		}
		Node current = list_itemlist.item(0);
		if(current == null) {
			System.out.println("XMLProject.parseDoc() - current == null");
		}
		NamedNodeMap nnm_itemlist = current.getAttributes();
		if(nnm_itemlist == null) {
			System.out.println("XMLProject.parseDoc() - nnm_itemlist == null");
		}
		try {
			Node node_projectpk = nnm_itemlist.getNamedItem("l_projectpk");
			Node node_name  = nnm_itemlist.getNamedItem("sz_name");
			Node node_createtimestamp = nnm_itemlist.getNamedItem("l_createtimestamp");
			Node node_updatetimestamp = nnm_itemlist.getNamedItem("l_updatetimestamp");
			sz_projectpk	= node_projectpk.getNodeValue();
			sz_name			= node_name.getNodeValue();
			sz_createtimestamp = node_createtimestamp.getNodeValue();
			sz_updatetimestamp = node_updatetimestamp.getNodeValue();
			
			System.out.println("Projectpk = " + sz_projectpk);
			System.out.println("Name = " + sz_name);
			System.out.println("ctime = " + sz_createtimestamp);
			System.out.println("utime = " + sz_updatetimestamp);
			
			m_project.set_projectpk(sz_projectpk);
			m_project.set_projectname(sz_name);
			m_project.set_createtimestamp(sz_createtimestamp);
			m_project.set_updatetimestamp(sz_updatetimestamp);
			m_project.set_saved();
			
			System.out.println("Project set");
		} catch(Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			Error.getError().addError("XMLProject","Exception in parseDoc",e,1);
		}		
		onDownloadFinished();

		return;
	}
	public void onDownloadFinished() {
		try {
			ActionEvent e = new ActionEvent(get_project(), ActionEvent.ACTION_PERFORMED, get_callback_action());
			get_callback().actionPerformed(e);
		} catch(Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			Error.getError().addError("XMLProject","Exception in onDownloadFinished",e,1);
		}
	}
}