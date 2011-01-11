package no.ums.pas.parm.xml;

import com.sun.org.apache.xerces.internal.dom.DeferredElementImpl;
import no.ums.pas.importer.gis.GISList;
import no.ums.pas.importer.gis.GISRecord;
import no.ums.pas.maps.defines.GISShape;
import no.ums.pas.parm.constants.ParmConstants;
import no.ums.pas.parm.main.MainController;
import no.ums.pas.parm.voobjects.AlertVO;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;


public class XmlGISReader {
	private Document doc;

	//private MapPanel map;
	private MainController main;
	private ArrayList<Object> arrayAlerts;
	private ArrayList<Object> updatedAlertList;
	private String pk;
	private NodeList listOfPoly;
	private GISShape gisshape;
	private GISList gislist;
	private int col_r, col_g, col_b, col_a;
	private Color col;
	private Element elPolyAlert;

	public XmlGISReader(){}
	
	public XmlGISReader(Document doc, MainController main) {
		this.doc = doc;
		this.main = main;
		this.arrayAlerts = null;
		this.updatedAlertList = null;
	}
	public void readGISAlert(ArrayList<Object> arrayAlerts) {
		this.arrayAlerts = arrayAlerts;
		this.updatedAlertList = new ArrayList<Object>();
		this.listOfPoly = doc.getElementsByTagName(ParmConstants.xmlElmAlertstreetid);

		if (listOfPoly.getLength() > 0) {
			for (int i = 0; i < listOfPoly.getLength(); i++) {
				//poly = new PolygonStruct(new Navigation(map, 1024, 768),
				//		new Dimension(1024, 768));
				
				gislist = new GISList();
				
				Node nPolyObject = listOfPoly.item(i);

				if (nPolyObject.getNodeType() == Node.ELEMENT_NODE && this.arrayAlerts != null) {

					elPolyAlert = (Element) nPolyObject;
					pk = elPolyAlert.getAttribute(ParmConstants.xmlElmAlertPk);

					AlertVO o = getAlert(pk);
					NodeList childnodes = elPolyAlert.getChildNodes();
					for(int j=0;j<childnodes.getLength();j++) {
						Node node = childnodes.item(j);
//						System.out.println(node.getAttributes());
						if(node.getClass().equals(DeferredElementImpl.class)) {	// Denne er litt jalla				
							Element child = (Element)node;
							GISRecord gisr = new GISRecord(child.getAttribute("municipal"),
									child.getAttribute("streetid"),
									child.getAttribute("houseno"),
									child.getAttribute("letter"),
									child.getAttribute("namefilter1"),
									child.getAttribute("namefilter2"));
							gislist.add(gisr);
						}
					}
					if (o != null) {
						gisshape = new GISShape(gislist);
						o.setM_shape(gisshape);
						this.updatedAlertList.add(o);
					}
				}
			}
		}
	}

	private AlertVO getAlert(String pk) {
		AlertVO ao = null;
		for (int a = 0; a < this.arrayAlerts.size(); a++) {
			ao = (AlertVO) arrayAlerts.get(a);
			if (ao.getAlertpk().compareTo(pk) == 0)
				break;
			else
				ao = null;
		}
		return ao;
	}
	
	public Collection<Object> getUpdatedList(){
		Collection <Object>updatedList = new ArrayList<Object>();
		updatedList.addAll(this.updatedAlertList);
		return updatedList;
	}
}
