package no.ums.pas.parm.xml;

import no.ums.pas.cellbroadcast.Area;
import no.ums.pas.cellbroadcast.CBMessage;
import no.ums.pas.cellbroadcast.CCode;
import no.ums.pas.cellbroadcast.CountryCodes;
import no.ums.pas.maps.defines.PolygonStruct;
import no.ums.pas.parm.constants.ParmConstants;
import no.ums.pas.parm.main.MainController;
import no.ums.pas.parm.voobjects.AlertVO;
import no.ums.pas.parm.voobjects.ObjectVO;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;



public class XmlPolyReader {
	private Document doc;

	//private MapPanel map;
	private MainController main;

	private ArrayList<Object> arrayObjects;
	
	private ArrayList<Object> updatedObjectList;

	private ArrayList<Object> arrayAlerts;
	
	private ArrayList<Object> updatedAlertList;

	private String pk, xCor, yCor;

	private NodeList listOfPoly;

	private PolygonStruct poly;

	private int col_r, col_g, col_b, col_a;

	private Color col;

	private Element elPolyObject;
	
	public XmlPolyReader(){}
	
	public XmlPolyReader(MainController main) {
		this.main = main;
		this.arrayAlerts = null;
		this.arrayObjects = null;
		this.updatedAlertList = null;
		this.updatedObjectList = null;
	}
	public void readPolyObject(Element parentnode, ObjectVO object) {
		this.updatedObjectList = new ArrayList<Object>();
		
		this.listOfPoly = parentnode.getElementsByTagName(ParmConstants.xmlElmObjectPoly);

		if (listOfPoly.getLength() > 0) {
			for (int i = 0; i < listOfPoly.getLength(); i++) {
				//poly = new PolygonStruct(new Navigation(map, 1024, 768),
				//		new Dimension(1024, 768));
				poly = new PolygonStruct(main.getMapSize());
				
				Node nPolyObject = listOfPoly.item(i);

				if (nPolyObject.getNodeType() == Node.ELEMENT_NODE && this.arrayObjects != null) {

					elPolyObject = (Element) nPolyObject;
					pk = elPolyObject.getAttribute(ParmConstants.xmlElmObjectPk);

					ObjectVO o = getObject(pk);
					if (o != null) {
						this.setPolyCoor(nPolyObject);
						o.setM_shape(poly);
						this.updatedObjectList.add(o);
					}
				}
			}
		}
	}
	public void readPolyAlert(Element parentnode, AlertVO alert) {
		Element nCellBroadcast;
		this.updatedAlertList = new ArrayList<Object>();
		this.listOfPoly = parentnode.getElementsByTagName(ParmConstants.xmlElmAlertPoly);

		if (listOfPoly.getLength() > 0) {

			for (int a = 0; a < listOfPoly.getLength(); a++) {
				//poly = new PolygonStruct(new Navigation(map, 1024, 768),
				//		new Dimension(1024, 768));
				poly = new PolygonStruct(main.getMapSize());
				Node nPolyObject = listOfPoly.item(a);

				if (nPolyObject.getNodeType() == Node.ELEMENT_NODE
						&& this.arrayAlerts != null) {

					elPolyObject = (Element) nPolyObject;
					pk = elPolyObject.getAttribute(ParmConstants.xmlElmAlertPk);
					
					this.setPolyCoor(nPolyObject);
					alert.setM_shape(poly);
					this.updatedAlertList.add(alert);
				}
			}
		}
	}
	// Denne er ny for cell broadcast
	public void readCellBroadcast(Element parentnode, AlertVO alert) {
		NodeList listOfCellBroadcasts = parentnode.getElementsByTagName("cellbroadcast");
		AlertVO a;
		for(int i=0;i< listOfCellBroadcasts.getLength();i++) {
			Element cellNode = (Element)listOfCellBroadcasts.item(i); 
			String alertpk = cellNode.getAttribute("l_alertpk");
			String id = cellNode.getAttribute("sz_id");
			String area = cellNode.getAttribute("sz_area");
			//a = getAlert(alertpk);
			
			if(alert != null) {
				alert.setArea(new Area(id,area));
				// Her hentes meldingene ut
				NodeList messages = cellNode.getElementsByTagName("message");
				ArrayList<Object> cbmessages = new ArrayList<Object>();
				for(int j=0;j<messages.getLength();j++) {
					Element message = (Element)messages.item(j);
					String lang = message.getAttribute("sz_lang");
					String text = message.getAttribute("sz_text");
					String cboadc = message.getAttribute("sz_cb_oadc");
					
					NodeList ccodes = message.getElementsByTagName("ccode");
					ArrayList<CCode> arrCcodes = new ArrayList<CCode>();
					
					for(int k=0;k<ccodes.getLength();k++) {
						
						if(ccodes.item(k).getNodeType() == Node.ELEMENT_NODE ) {
							Element ccode = (Element)ccodes.item(k);
							arrCcodes.add(CountryCodes.getCountryByCCode(ccode.getTextContent()));
						}
					}
					cbmessages.add(new CBMessage(lang, arrCcodes, text, cboadc));					 
				}
				alert.setCBMessages(cbmessages);
				//Element elm_local = (Element)message.item(0);
				//Element elm_international = (Element)message.item(1);
				
//				String[] local  = { elm_local.getAttribute("sz_ccode"), elm_local.getAttribute("sz_text") };
//				a.setLocalLanguage(local);
//				String[] international = { elm_international.getAttribute("sz_ccode"), elm_international.getAttribute("sz_text") };
//				a.setInternationalLanguage(international);
				
				/*
				 *
				 <cellbroadcast l_alertpk="a500000000304" sz_area="Test1 (ID 1)" sz_cb_oadc="HilsenSvein" sz_id="1">
					<message sz_lang="English" sz_text="english for iron baners">
						<ccode>-1</ccode>
					</message>
					<message sz_lang="Scandivaian" sz_text="noregs dilldall">
						<ccode>0047</ccode>
						<ccode>0045</ccode>
						<ccode>0046</ccode>
					</message>
					<message sz_lang="Ze Germanz" sz_text="Do hast meine lederhosen gestohlen">
						<ccode>0041</ccode>
					</messages>
				  </cellbroadcast>

				 * 
				 */
				
			}
		}
	}
	
	private ObjectVO getObject(String pk) {
		ObjectVO o = null;
		for (int b = 0; b < this.arrayObjects.size(); b++) {
			o = (ObjectVO) arrayObjects.get(b);
			if (o.getObjectPK().compareTo(pk) == 0)
				break;
			else{
				o = null;
			}
		}
		return o;
	}
	private AlertVO getAlert(String pk) {
		AlertVO ao = null;
		for (int a = 0; a < this.arrayAlerts.size(); a++) {
			ao = (AlertVO) arrayAlerts.get(a);
			if (ao.getAlertpk().equals(pk))
				break;
			else
				ao = null;
		}
		return ao;
	}
	private void setPolyCoor(Node node) {
		if (elPolyObject.hasAttribute("col_r")
		 && elPolyObject.hasAttribute("col_g")
		 && elPolyObject.hasAttribute("col_b")
		 && elPolyObject.hasAttribute("col_a")) {
		
			col_r = Integer.parseInt(elPolyObject.getAttribute("col_r"));
			col_g = Integer.parseInt(elPolyObject.getAttribute("col_g"));
			col_b = Integer.parseInt(elPolyObject.getAttribute("col_b"));
			col_a = Integer.parseInt(elPolyObject.getAttribute("col_a"));
			col = new Color(col_r, col_g, col_b, col_a);
			// Goes through every object in objectlist and adds
			// polycoordinates
			poly.set_fill_color(col);
		}
		Element el = (Element)node;
		NodeList listOfPolyPoints = el
				.getElementsByTagName("polypoint");
		for (int b = 0; b < listOfPolyPoints.getLength(); b++) {
			Node nPoint = listOfPolyPoints.item(b);
			if (nPoint.getNodeType() == Node.ELEMENT_NODE) {
				Element elPolyPoint = (Element) nPoint;
				xCor = elPolyPoint.getAttribute("xcord");
				yCor = elPolyPoint.getAttribute("ycord");
			}
			poly.add_coor(new Double(Double.parseDouble(xCor)), new Double(Double.parseDouble(yCor)));
		}
	}
	public Collection<Object> getUpdatedList(){
		Collection<Object> updatedList = new ArrayList<Object>();
		updatedList.addAll(this.updatedObjectList);
		updatedList.addAll(this.updatedAlertList);
		return updatedList;
	}
}
