package no.ums.pas.parm.xml;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;

import no.ums.pas.maps.defines.EllipseStruct;
import no.ums.pas.maps.defines.MapPoint;
import no.ums.pas.maps.defines.MapPointLL;
import no.ums.pas.parm.constants.ParmConstants;
import no.ums.pas.parm.main.*;
import no.ums.pas.parm.voobjects.AlertVO;
import no.ums.pas.parm.voobjects.ObjectVO;
import no.ums.pas.ums.errorhandling.Error;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class XmlEllipseReader {
	private Document doc;

	//private MapPanel map;
	private MainController main;
	private ArrayList<Object> arrayObjects;
	private ArrayList<Object> updatedObjectList;
	private ArrayList<Object> arrayAlerts;
	private ArrayList<Object> updatedAlertList;
	private String pk, xCor, yCor;
	private NodeList listOfPoly;
	private EllipseStruct ellipse;
	private int col_r, col_g, col_b, col_a;
	private Color col;
	private Element elPolyObject;

	public XmlEllipseReader(){}
	
	public XmlEllipseReader(Document doc, MainController main) {
		this.doc = doc;
		this.main = main;
		this.arrayAlerts = null;
		this.arrayObjects = null;
		this.updatedAlertList = null;
		this.updatedObjectList = null;
	}
	public void readEllipseObject(ArrayList<Object> arrayObjects) {
		this.arrayObjects = arrayObjects;
		this.updatedObjectList = new ArrayList<Object>();
		
		this.listOfPoly = doc.getElementsByTagName(ParmConstants.xmlElmObjectEllipse);

		if (listOfPoly.getLength() > 0) {
			for (int i = 0; i < listOfPoly.getLength(); i++) {
				//poly = new PolygonStruct(new Navigation(map, 1024, 768),
				//		new Dimension(1024, 768));
				ellipse = new EllipseStruct();
				
				Node nPolyObject = listOfPoly.item(i);

				if (nPolyObject.getNodeType() == Node.ELEMENT_NODE && this.arrayObjects != null) {

					elPolyObject = (Element) nPolyObject;
					pk = elPolyObject.getAttribute(ParmConstants.xmlElmObjectPk);

					ObjectVO o = getObject(pk);
					if (o != null) {
						this.setPolyCoor(nPolyObject);
						o.setM_shape(ellipse);
						this.updatedObjectList.add(o);
					}
				}
			}
		}
	}
	public void readEllipseAlert(ArrayList<Object> arrayAlerts) {
		this.arrayAlerts = arrayAlerts;
		this.updatedAlertList = new ArrayList<Object>();
		this.listOfPoly = doc.getElementsByTagName(ParmConstants.xmlElmAlertEllipse);

		if (listOfPoly.getLength() > 0) {

			for (int a = 0; a < listOfPoly.getLength(); a++) {
				//poly = new PolygonStruct(new Navigation(map, 1024, 768),
				//		new Dimension(1024, 768));
				ellipse = new EllipseStruct();
				Node nPolyObject = listOfPoly.item(a);

				if (nPolyObject.getNodeType() == Node.ELEMENT_NODE && this.arrayAlerts != null) {

					elPolyObject = (Element) nPolyObject;
					pk = elPolyObject.getAttribute(ParmConstants.xmlElmAlertPk);
					
					AlertVO o = getAlert(pk);
					if (o != null) {
						this.setPolyCoor(nPolyObject);
						o.setM_shape(ellipse);
						this.updatedAlertList.add(o);
					}
				}
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
			if (ao.getAlertpk().compareTo(pk) == 0)
				break;
			else
				ao = null;
		}
		return ao;
	}
	private void setPolyCoor(Node node) {
		double centerx;
		double centery;
		double cornerx;
		double cornery;
		
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
			ellipse.set_fill_color(col);
		}
		
		try {
			centerx = Double.parseDouble(elPolyObject.getAttribute("centerx"));
			centery = Double.parseDouble(elPolyObject.getAttribute("centery"));
			cornerx = Double.parseDouble(elPolyObject.getAttribute("cornerx"));
			cornery = Double.parseDouble(elPolyObject.getAttribute("cornery"));
			
			//Sjekk om dette egentlig var vits, det kom plutselig feil i recalc_shape pga m_pix ikke kunne bli satt http://bugz.ums.no/cgi-bin/bugzilla/show_bug.cgi?id=431
			double lbo = cornerx+ ((cornerx - centerx)*2);
			double rbo = cornerx;
			double ubo = cornery;
			double bbo = cornery + ((centery-cornery)*2);
			
			ellipse.set_ellipse_center(main.getMapNavigation(), new MapPoint(main.getMapNavigation(),new MapPointLL(centerx,centery)));
			ellipse.set_ellipse_corner(main.getMapNavigation(), new MapPoint(main.getMapNavigation(),new MapPointLL(cornerx,cornery)));
		} catch(Exception e) {
			Error.getError().addError("XmlEllipseReader","Error getting coordinates for ellipse", e, Error.SEVERITY_ERROR);
		}
	}
	public Collection<Object> getUpdatedList(){
		Collection<Object> updatedList = new ArrayList<Object>();
		updatedList.addAll(this.updatedObjectList);
		updatedList.addAll(this.updatedAlertList);
		return updatedList;
	}
}
