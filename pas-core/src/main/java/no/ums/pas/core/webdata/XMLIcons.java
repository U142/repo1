package no.ums.pas.core.webdata;

import java.util.ArrayList;

import no.ums.pas.*;
import no.ums.pas.core.dataexchange.*;
import no.ums.pas.core.defines.*;
import no.ums.pas.maps.defines.*;
import no.ums.pas.ums.errorhandling.Error;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.awt.event.*;


public class XMLIcons extends XMLThread {
	DefaultPanel m_parent;
	public XMLIcons(PAS pas, String sz_url, DefaultPanel parent, HTTPReq http_req) {
		super(Thread.NORM_PRIORITY, pas, sz_url, null, null, "Loading icons...", false, http_req);
		m_parent = parent;
	}
	ArrayList<IconObject> m_items;
	ArrayList<IconObject> get_items() { return m_items; }
	
	public void parseDoc(Document doc) {
		if(doc==null)
			return;
		/*parse header*/
		//get_pas().add_event("Inflating and parsing GPS list");
		NodeList list_itemlist = doc.getElementsByTagName("ICONLIST");
		if (list_itemlist == null)
			return;
		Node current = list_itemlist.item(0);
		if(current==null)
			return;
		NamedNodeMap nnm_itemlist = current.getAttributes();
		if(nnm_itemlist==null)
			return;
		Node node_records = nnm_itemlist.getNamedItem("records");
		if(node_records==null)
			return;
		String sz_records;
		sz_records = node_records.getNodeValue();
//		if(get_loadingpanel()!=null)
//			get_loadingpanel().start_progress(new Integer(sz_records).intValue(), "Inflating and parsing inhabitant list");
		/*parse items*/
		NodeList list_items = current.getChildNodes(); //mapobjects
		if(list_items==null)
			return;
		Node node_item;
		NamedNodeMap nnm_items;
		
		String[] sz_itemattr = { "l_pk", "l_comppk", "l_deptpk", "sz_name", "l_op", "l_size", "sz_fileext" };
		String[] sz_values;
		IconObject iconobj;
		int n_gpscoors_added = 0;
		double f_percent = 0;
		int n_show_percent = 0;
		double f_percent_pr_item;
		int n_record_count = new Integer(sz_records).intValue();
		if(n_record_count > 0)
			f_percent_pr_item = ((double)30 / n_record_count);
		else
			f_percent_pr_item = 100;
		
		m_items = new ArrayList<IconObject>(0); //init array as l_totitem big
		for(int n_items=0; n_items < list_items.getLength(); n_items++)
		{
			node_item = list_items.item(n_items);
			nnm_items = node_item.getAttributes();
			if(nnm_items==null)
				continue;
			sz_values = new String[sz_itemattr.length];
			for(int n_attr=0; n_attr < sz_itemattr.length; n_attr++)
			{
				try {
					sz_values[n_attr] = new String(nnm_items.getNamedItem(sz_itemattr[n_attr]).getNodeValue());
				}
				catch(Exception e)
				{
					sz_values[n_attr] = new String("0");
					Error.getError().addError("XMLIcons","Exception in parseDoc",e,1);
				}
			}
			try {
				iconobj = new IconObject(sz_values);
				m_items.add(iconobj);
			} catch(Exception e) { 
				String sz_temp = new String();
				for(int z=0; z<sz_itemattr.length; z++)
					sz_temp += ", " + new String(nnm_items.getNamedItem(sz_itemattr[z]).getNodeValue());
				System.out.println(e.getMessage());
				e.printStackTrace();
				Error.getError().addError("XMLIcons","Exception in parseDoc",e,1);
				continue;
			}
		}
		onDownloadFinished();
	}
	public void onDownloadFinished() {
		ActionEvent e = new ActionEvent(m_items, ActionEvent.ACTION_PERFORMED, "act_iconload_finished");
		m_parent.actionPerformed(e);
		//get_pas().get_housecontroller().onDownloadFinished();
	}
	
}