package no.ums.pas.parm.xml;

import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.pas.PAS;
import no.ums.pas.cellbroadcast.Area;
import no.ums.pas.cellbroadcast.CBMessage;
import no.ums.pas.cellbroadcast.CCode;
import no.ums.pas.core.logon.Settings;
import no.ums.pas.core.storage.StorageController;
import no.ums.pas.importer.gis.GISList;
import no.ums.pas.importer.gis.GISRecord;
import no.ums.pas.maps.defines.EllipseStruct;
import no.ums.pas.maps.defines.GISShape;
import no.ums.pas.maps.defines.PolygonStruct;
import no.ums.pas.maps.defines.ShapeStruct;
import no.ums.pas.parm.constants.ParmConstants;
import no.ums.pas.parm.voobjects.AlertVO;
import no.ums.pas.parm.voobjects.AlertVO.LBAOperator;
import no.ums.pas.parm.voobjects.CategoryVO;
import no.ums.pas.parm.voobjects.EventVO;
import no.ums.pas.parm.voobjects.ObjectVO;
import no.ums.pas.parm.voobjects.ParmVO;
import no.ums.pas.send.sendpanels.Sending_Cell_Broadcast_text;
import no.ums.pas.ums.errorhandling.Error;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Toolkit;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;




public class XmlWriter {
    private static final Log log = UmsLog.getLogger(XmlWriter.class);
	private String XMLFilePath = "parmxml.zip";
	
	private final String strCategory = "pacategory";
	private final String strCategoryPK = "l_categorypk";
	
	private final String strObject = "paobject";
	private final String strObjectPK = "l_objectpk";
	
	private final String strEvent = "paevent";
	private final String strEventPK = "l_eventpk";
	
	private final String strAlert = "paalert";
	private final String strAlertPK = "l_alertpk";
	
	private int returnValue = 0;
	private String rootTimestamp = "0";
	
	public XmlWriter(){}
	
	public String writeTempXml(Collection <Object>objects, String filepath) {
		String[] files = new String[2];
		files[0] = filepath;
		String polypath = filepath.substring(0,filepath.indexOf("tempxml")) + "polyxml" + filepath.substring(filepath.indexOf("tempxml")+7);
		// Blir slik sessionid-tempxml-highesttemp
		//           sessionid+polyxml+highesttemp
		files[1] = polypath;
		/*
		try{
			// Leser ut rootTimestamp
			XmlReader reader = new XmlReader();
			rootTimestamp = reader.getRootTimestamp();
			reader = null;
			if(rootTimestamp == null)
				rootTimestamp = "0";
		}
		catch(Exception e){
			log.debug("Feilet med å lese timestamp fra parmxml: " + e.getMessage());
			returnValue = -1;
		}*/
		
		if(objects != null)
			writer(objects, filepath);
		else
			writeEmptyParm(filepath);
		
		if(objects != null) {
			Iterator <Object>it = objects.iterator();
			boolean writtenPoly = false;
			while(it.hasNext()){
				Object o = it.next();
				if(o.getClass().equals(ObjectVO.class) || o.getClass().equals(AlertVO.class)) {
					returnValue = writePolyXml(o, files[1]);
					writtenPoly = true;
				}
			}
			// Hvis det er et event som er blitt lagt til så må det skrives en tom poly
			if(!writtenPoly)
				writeEmptyPoly(files[1]);
		}
		else
			returnValue = writeEmptyPoly(files[1]);
		
		returnValue = zipXmlFile(files);
		return filepath;
	}
	
	public int writeEmptyParm(String filename){
		Document xmlDoc = null;
	

		if(filename.equals(""))
			xmlDoc = getXMLDocument(ParmConstants.xmlLocation,ParmConstants.xmlLocation);
		else
			xmlDoc = getXMLDocument(filename,filename);
		
		Element rootnd;
		
		if(!xmlDoc.hasChildNodes()){
			rootnd = (Element) xmlDoc.createElement("parmroot");
			rootnd.setAttribute("l_timestamp",rootTimestamp);
			
			xmlDoc.appendChild(rootnd);
		}
		else{
			rootnd = xmlDoc.getDocumentElement();
			rootnd.setAttribute("l_timestamp",rootTimestamp);
		}
		/*
		try{
			XmlReader reader = new XmlReader();
			rootTimestamp = reader.getRootTimestamp();
			reader = null;
			if(rootTimestamp == null)
				rootTimestamp = "0";
		}
		catch(Exception e){
			log.debug("Fant ikke rootTimestamp: " + e.getMessage());
		}*/
		
		if(filename.equals(""))
			writeXMLFile(xmlDoc,ParmConstants.xmlLocation);
		else
			writeXMLFile(xmlDoc,filename);
		return returnValue;
	}
	public int writeEmptyPoly(String filename){
		Document xmlDoc = null;
		
		if(filename.equals(""))
			xmlDoc = getXMLDocument(ParmConstants.polyxmlLocation,ParmConstants.xmlLocation);
		else
			xmlDoc = getXMLDocument(filename,filename);
		
		Element rootnd = (Element) xmlDoc.createElement("polyroot");
		xmlDoc.appendChild(rootnd);
		if(filename.equals(""))
			writeXMLFile(xmlDoc,ParmConstants.polyxmlLocation);
		else
			writeXMLFile(xmlDoc,filename);
		return returnValue;
	}
	
	// Denne skal i følge MainController slette objekter
	public int writeXml(Collection <Object>objects) {//, String timestamp) {

		String[] files = new String[2];
		files[0] = ParmConstants.xmlLocation;
		files[1] = ParmConstants.polyxmlLocation;
		
		if(objects != null){
			// Den går egentlig bare inn og forandrer sz_operation
			returnValue = writer(objects, ParmConstants.xmlLocation);
			
//			Iterator it = objects.iterator();
//			while(it.hasNext()){
//				Object o = it.next();
//				if(o.getClass().equals(ObjectVO.class) || o.getClass().equals(AlertVO.class)) {
					// Her må jeg oppdatere temppk'ene for at den ikke skal legge til i stedet for å oppdatere polygonene
					/*if(o.getClass().equals(ObjectVO.class) && ((ObjectVO)o).getM_polygon() != null)
						updatePolyXml(((ObjectVO)o).getObjectPK(),((ObjectVO)o).getTempPk());
					else if(o.getClass().equals(AlertVO.class) && ((AlertVO)o).getM_polygon() != null)  // det kan jo kun være ObjectVO eller AlertVO
						updatePolyXml(((AlertVO)o).getAlertpk(),((AlertVO)o).getTempPk());*/
					
//					returnValue = writePolyXml(o, ParmConstants.polyxmlLocation);
//				}
//			}
			
			returnValue = zipXmlFile(files);
		}
		else{
			// Sjekker om zipfilen eksisterer, hvis den gjør det så må jeg ikke overskrive med tomme filer
			File file = new File(ParmConstants.xmlLocation);
			if(!file.exists()){
				returnValue = writeEmptyParm("");
				returnValue = writeEmptyPoly("");
				
				returnValue = zipXmlFile(files);
			}
		}
		return returnValue;
	}
	
	private int writer(Collection <Object>objects, String filepath){
		if(objects!=null)
			objects = extractObjects(objects);
		
		Document xmlDoc = getXMLDocument(filepath,filepath);
		Element element = null;
		
		try {
			// Må sjekke om det har root element
			Element rootnd = xmlDoc.getDocumentElement();
			if (rootnd == null) {
				rootnd = (Element) xmlDoc.createElement("parmroot");
				rootnd.setAttribute("l_timestamp",rootTimestamp);
				xmlDoc.appendChild(rootnd);
			}
			else
				rootnd.setAttribute("l_timestamp",rootTimestamp);

            for (Object o : objects) {
                if (o.getClass().equals(CategoryVO.class)) {
                    CategoryVO category = (CategoryVO) o;

                    // Må sjekke om et element med samme pk eksisterer
                    element = checkXMLElement(xmlDoc, strCategory, strCategoryPK, category);
                    if (element == null) // Hvis element fortsatt er null nå finnes ikke dette elementet fra før
                        element = (Element) xmlDoc.createElement(strCategory);

                    rootnd.appendChild(element);
                    element.setAttribute("l_categorypk", category.getCategoryPK());
                    element.setAttribute("sz_name", category.getName());
                    element.setAttribute("sz_description", category.getDescription());
                    element.setAttribute("sz_fileext", category.getFileext());
                    element.setAttribute("l_timestamp", category.getTimestamp());
                    element = null;
                } else if (o.getClass().equals(ObjectVO.class)) {
                    ObjectVO object = (ObjectVO) o;
//					 Må sjekke om et element med samme pk eksisterer
                    element = checkXMLElement(xmlDoc, strObject, strObjectPK, object);
                    if (element == null) // Hvis element fortsatt er null nå finnes ikke dette elementet fra før
                        element = (Element) xmlDoc.createElement(strObject);

                    rootnd.appendChild(element);

                    element.setAttribute("l_objectpk", object.getObjectPK());
                    element.setAttribute("l_deptpk", object.getDeptPK());
                    element.setAttribute("l_importpk", object.getImportPK());
                    element.setAttribute("sz_name", object.getName());
                    element.setAttribute("sz_description", object.getDescription());
                    element.setAttribute("l_categorypk", object.getCategoryPK());
                    element.setAttribute("l_parent", object.getParent());
                    element.setAttribute("sz_address", object.getAddress());
                    element.setAttribute("sz_postno", object.getPostno());
                    element.setAttribute("sz_place", object.getPlace());
                    element.setAttribute("sz_phone", object.getPhone());
                    element.setAttribute("sz_metadata", object.getMetadata());
                    element.setAttribute("l_timestamp", object.getTimestamp());
                    String isObjectFolder = "0";
                    if (object.isObjectFolder())
                        isObjectFolder = "1";
                    element.setAttribute("f_isobjectfolder", isObjectFolder);
                    if (object.getM_shape() != null) {

                        if (object.getM_shape().getClass().equals(PolygonStruct.class)) {
                            Node shape = element.appendChild(xmlDoc.createElement(ParmConstants.xmlElmObjectPoly));

                            Element el_shape = (Element) shape;

                            el_shape.setAttribute("col_r", String.valueOf(object.getM_shape().get_fill_color().getRed()));
                            el_shape.setAttribute("col_g", String.valueOf(object.getM_shape().get_fill_color().getGreen()));
                            el_shape.setAttribute("col_b", String.valueOf(object.getM_shape().get_fill_color().getBlue()));
                            el_shape.setAttribute("col_a", String.valueOf(object.getM_shape().get_fill_color().getAlpha()));

                            PolygonStruct alertShape = object.getM_shape().typecast_polygon();
                            Element polypoint;
                            for (int i = 0; i < alertShape.get_size(); ++i) {
                                polypoint = (Element) el_shape.appendChild(xmlDoc.createElement("polypoint"));
                                polypoint.setAttribute("xcord", String.valueOf(alertShape.get_coor_lon(i)));
                                polypoint.setAttribute("ycord", String.valueOf(alertShape.get_coor_lat(i)));
                            }
                        } else if (object.getM_shape().getClass().equals(EllipseStruct.class)) {
                            EllipseStruct alertShape = object.getM_shape().typecast_ellipse();
                            Element ellipse = (Element) element.appendChild(xmlDoc.createElement(ParmConstants.xmlElmObjectEllipse));
                            ellipse.setAttribute("centerx", String.valueOf(alertShape.get_center().get_lon()));
                            ellipse.setAttribute("centery", String.valueOf(alertShape.get_center().get_lat()));
                            ellipse.setAttribute("cornerx", String.valueOf(alertShape.get_corner().get_lon()));
                            ellipse.setAttribute("cornery", String.valueOf(alertShape.get_corner().get_lat()));
                            ellipse.setAttribute("col_r", String.valueOf(object.getM_shape().get_fill_color().getRed()));
                            ellipse.setAttribute("col_g", String.valueOf(object.getM_shape().get_fill_color().getGreen()));
                            ellipse.setAttribute("col_b", String.valueOf(object.getM_shape().get_fill_color().getBlue()));
                            ellipse.setAttribute("col_a", String.valueOf(object.getM_shape().get_fill_color().getAlpha()));
                        }
                    }
                    if (object.getOperation() != null)
                        element.setAttribute("sz_operation", object.getOperation());
                    element = null;
                } else if (o.getClass().equals(EventVO.class)) {
                    EventVO event = (EventVO) o;
//					 Må sjekke om et element med samme pk eksisterer
                    element = checkXMLElement(xmlDoc, strEvent, strEventPK, event);
                    if (element == null) // Hvis element fortsatt er null nå finnes ikke dette elementet fra før
                        element = (Element) xmlDoc.createElement(strEvent);

                    rootnd.appendChild(element);
                    element.setAttribute("l_eventpk", event.getEventPk());
                    element.setAttribute("l_parent", event.getParentpk());
                    element.setAttribute("sz_name", event.getName());
                    element.setAttribute("sz_description", event.getDescription());
                    element.setAttribute("l_categorypk", event.getCategorypk());
                    element.setAttribute("l_timestamp", event.getTimestamp());
                    element.setAttribute("f_epi_lon", String.valueOf(event.getEpicentreX()));
                    element.setAttribute("f_epi_lat", String.valueOf(event.getEpicentreY()));
                    if (event.getOperation() != null)
                        element.setAttribute("sz_operation", event.getOperation());
                    element = null;
                } else if (o.getClass().equals(AlertVO.class)) {
                    AlertVO alert = (AlertVO) o;
                    // Må sjekke om et element med samme pk eksisterer
                    element = checkXMLElement(xmlDoc, strAlert, strAlertPK, alert);
                    if (element == null) // Hvis element fortsatt er null nå finnes ikke dette elementet fra før
                        element = (Element) xmlDoc.createElement(strAlert);
                    rootnd.appendChild(element);
                    // Legger til elementer
//					 Må sjekke om det er temppk som blir lagt inn eller vanlig (temppk har ikke bokstav foran)
                    element.setAttribute("l_alertpk", alert.getAlertpk());
                    element.setAttribute("l_parent", alert.getParent());
                    element.setAttribute("sz_name", alert.getName());
                    element.setAttribute("sz_description", alert.getDescription());
                    element.setAttribute("l_profilepk", String.valueOf(alert.getProfilepk()));
                    element.setAttribute("l_schedpk", String.valueOf(alert.getSchedpk()));
                    element.setAttribute("sz_oadc", alert.getOadc());
                    element.setAttribute("l_addresstypes", String.valueOf(alert.getAddresstypes()));
                    element.setAttribute("l_timestamp", alert.getTimestamp());
                    element.setAttribute("l_validity", String.valueOf(alert.getValidity()));
                    element.setAttribute("sz_areaid", alert.getLBAAreaID());
                    element.setAttribute("l_maxchannels", String.valueOf(alert.getMaxChannels()));
                    element.setAttribute("l_requesttype", String.valueOf(alert.getRequestType()));
                    if (alert.getOperation() != null)
                        element.setAttribute("sz_operation", alert.getOperation());
                    element.setAttribute("f_locked", String.valueOf(alert.getLocked()));
                    element.setAttribute("l_expiry", String.valueOf(alert.get_LBAExpiry()));
                    element.setAttribute("sz_sms_oadc", alert.get_sms_oadc());
                    element.setAttribute("sz_sms_message", alert.get_sms_message());
                    if (alert.getShape() != null) {
                    	// Fjerner evt shapes som er lagret fra før
                    	removeCurrentShape(element);
                        if (alert.getShape().getClass().equals(PolygonStruct.class)) {
                            
                        	Node shape = element.appendChild(xmlDoc.createElement(ParmConstants.xmlElmAlertPoly));

                            Element el_shape = (Element) shape;

                            el_shape.setAttribute("col_r", String.valueOf(alert.getM_shape().get_fill_color().getRed()));
                            el_shape.setAttribute("col_g", String.valueOf(alert.getM_shape().get_fill_color().getGreen()));
                            el_shape.setAttribute("col_b", String.valueOf(alert.getM_shape().get_fill_color().getBlue()));
                            el_shape.setAttribute("col_a", String.valueOf(alert.getM_shape().get_fill_color().getAlpha()));

                            PolygonStruct alertShape = alert.getShape().typecast_polygon();
                            Element polypoint;
                            for (int i = 0; i < alertShape.get_size(); ++i) {
                                polypoint = (Element) el_shape.appendChild(xmlDoc.createElement("polypoint"));
                                polypoint.setAttribute("xcord", String.valueOf(alertShape.get_coor_lon(i)));
                                polypoint.setAttribute("ycord", String.valueOf(alertShape.get_coor_lat(i)));
                            }
                        } else if (alert.getShape().getClass().equals(GISShape.class)) {
                            GISShape alertShape = alert.getShape().typecast_gis();
                            Element streetid = (Element) element.appendChild(xmlDoc.createElement(ParmConstants.xmlElmAlertstreetid));

                            for (int i = 0; i < alertShape.get_gislist().size(); ++i) {
                                GISRecord gisrecord = alertShape.get_gislist().get(i);

                                Element el_shape = (Element) streetid.appendChild(xmlDoc.createElement("line"));
                                el_shape.setAttribute("col_r", String.valueOf(alert.getM_shape().get_fill_color().getRed()));
                                el_shape.setAttribute("col_g", String.valueOf(alert.getM_shape().get_fill_color().getGreen()));
                                el_shape.setAttribute("col_b", String.valueOf(alert.getM_shape().get_fill_color().getBlue()));
                                el_shape.setAttribute("col_a", String.valueOf(alert.getM_shape().get_fill_color().getAlpha()));
                                el_shape.setAttribute("houseno", gisrecord.get_houseno());
                                el_shape.setAttribute("letter", gisrecord.get_letter());
                                el_shape.setAttribute("municipal", gisrecord.get_municipal());
                                el_shape.setAttribute("namefilter1", gisrecord.get_name1());
                                el_shape.setAttribute("namefilter2", gisrecord.get_name2());
                                el_shape.setAttribute("streetid", gisrecord.get_streetid());
                            }
                        } else if (alert.getShape().getClass().equals(EllipseStruct.class)) {
                            EllipseStruct alertShape = alert.getShape().typecast_ellipse();
                            Element ellipse = (Element) element.appendChild(xmlDoc.createElement(ParmConstants.xmlElmAlertEllipse));
                            ellipse.setAttribute("centerx", String.valueOf(alertShape.get_center().get_lon()));
                            ellipse.setAttribute("centery", String.valueOf(alertShape.get_center().get_lat()));
                            ellipse.setAttribute("cornerx", String.valueOf(alertShape.get_corner().get_lon()));
                            ellipse.setAttribute("cornery", String.valueOf(alertShape.get_corner().get_lat()));
                            ellipse.setAttribute("col_r", String.valueOf(alert.getM_shape().get_fill_color().getRed()));
                            ellipse.setAttribute("col_g", String.valueOf(alert.getM_shape().get_fill_color().getGreen()));
                            ellipse.setAttribute("col_b", String.valueOf(alert.getM_shape().get_fill_color().getBlue()));
                            ellipse.setAttribute("col_a", String.valueOf(alert.getM_shape().get_fill_color().getAlpha()));
                        }
                    }

                    if (alert.getArea() != null)
                        writeCellBroadcast(alert, element, xmlDoc, "cellbroadcast");

                    //add lba operator tags
                    if (alert.getOperators().size() > 0) {
                        Node lbaoperators = element.appendChild(xmlDoc.createElement("lbaoperators"));
                        for (int op = 0; op < alert.getOperators().size(); op++) {
                            LBAOperator lba = alert.getOperators().get(op);
                            Element operator = (Element) lbaoperators.appendChild(xmlDoc.createElement("operator"));
                            operator.setAttribute("l_operator", Integer.toString(lba.l_operator));
                            operator.setAttribute("l_status", Integer.toString(lba.l_status));
                            operator.setAttribute("l_areaid", Long.toString(lba.l_areaid));
                            operator.setAttribute("sz_operatorname", lba.sz_operatorname);
                            operator.setAttribute("sz_status", lba.sz_status);
                        }

                    }
                    //element = null;
                }
            }
			unzipXmlFile(filepath);
			writeXMLFile(xmlDoc,filepath);
			
		} catch (Exception e) {
			returnValue = -1;
			log.debug("Feilmelding: " + e.getMessage());
			log.warn(e.getMessage(), e);
			Error.getError().addError("XmlWriter","Exception in writer",e,1);
		}
		return returnValue;
	}
	
	public int updateXml(String pk, String temppk, String timestamp){
		
		returnValue = 0;
		unzipXmlFile(ParmConstants.xmlLocation);
		Document xmlDoc = getXMLDocument(ParmConstants.xmlLocation,ParmConstants.xmlLocation);
		// Dette brukes til zipingen
		String[] files = new String[2];
		files[0] = ParmConstants.xmlLocation;
		files[1] = ParmConstants.polyxmlLocation;
		
		//Jeg finner ut hvilket objekt det er også kan jeg gå gjennom hele xmlfilen og bytte ut
		Element element = null;
		Element rootnd = xmlDoc.getDocumentElement();
		if (rootnd == null) {
			rootnd = (Element) xmlDoc.createElement("parmroot");
			rootnd.setAttribute("l_timestamp",rootTimestamp);
			xmlDoc.appendChild(rootnd);
		}
		else
			rootnd.setAttribute("l_timestamp",rootTimestamp);
		
		NodeList nl = rootnd.getElementsByTagName("paobject");
		
		// Jeg må inn i hver type og oppdatere pk'ene, det må jo skiftes ut i parent også vet du...
		for(int i=0;i<nl.getLength() ;i++){
			
			element = (Element)nl.item(i);	
			// Dette kan egentlig ikke skje...
			if(element == null) // Hvis element fortsatt er null nå finnes ikke dette elementet fra før
				element = (Element) xmlDoc.createElement(strObject);
			
			//rootnd.appendChild(element);
			// Denne kan ha temppk
			if((element.getAttribute("l_objectpk")).compareTo(temppk) == 0) {
				element.setAttribute("l_objectpk", pk);
				element.setAttribute("l_timestamp", timestamp);						
			}
			
			// Denne kan ha temppk
			if((element.getAttribute("l_parent")).compareTo(temppk) == 0)
				element.setAttribute("l_parent", pk);
			
			// Hvis denne har vært oppdatert må operation attributtet fjernes slik at det ikke blir tatt med i neste oppdatering
			if(element.hasAttribute("sz_operation")){
				String temp = element.getAttribute("sz_operation");
				// Hvis den skal slettes
				if(temp.compareTo("delete") == 0)
					rootnd.removeChild(element);
				element.removeAttribute("sz_operation");
			}
		}
		
		nl = rootnd.getElementsByTagName("paevent");
		
		// Jeg må inn i hver type og oppdatere pk'ene, det må jo skiftes ut i parent også vet du...
		for(int i=0;i<nl.getLength() ;i++){
			element = (Element)nl.item(i);
			// Dette kan egentlig ikke skje...
			if(element == null) // Hvis element fortsatt er null nå finnes ikke dette elementet fra før
				element = (Element) xmlDoc.createElement(strEvent);
			
			//rootnd.appendChild(element);
			
			// Denne kan ha temppk
			if((element.getAttribute("l_eventpk")).compareTo(temppk) == 0){
				element.setAttribute("l_eventpk", pk);
				element.setAttribute("l_timestamp", timestamp);
			}
			
			// Denne kan ha temppk
			if((element.getAttribute("l_parent")).compareTo(temppk) == 0)
				element.setAttribute("l_parent", pk);
			
			// Hvis denne har vært oppdatert må operation attributtet fjernes slik at det ikke blir tatt med i neste oppdatering
			if(element.hasAttribute("sz_operation")){
				String temp = element.getAttribute("sz_operation");
				// Hvis den skal slettes
				if(temp.compareTo("delete") == 0)
					rootnd.removeChild(element);
				element.removeAttribute("sz_operation");
			}
		}
		
		nl = rootnd.getElementsByTagName("paalert");
		
		// Jeg må inn i hver type og oppdatere pk'ene, det må jo skiftes ut i parent også vet du...
		for(int i=0;i<nl.getLength() ;i++){

			element = (Element)nl.item(i);
			// Dette kan egentlig ikke skje...
			if(element == null) // Hvis element fortsatt er null nå finnes ikke dette elementet fra før
				element = (Element) xmlDoc.createElement(strAlert);

			// Denne kan ha temppk
			if((element.getAttribute("l_alertpk")).compareTo(temppk) == 0){
				element.setAttribute("l_alertpk", pk);
				element.setAttribute("l_timestamp", timestamp);
			}
			// Denne kan ha temppk
			if((element.getAttribute("l_parent")).compareTo(temppk) == 0)
				element.setAttribute("l_parent", pk);
				
			// Hvis denne har vært oppdatert må operation attributtet fjernes slik at det ikke blir tatt med i neste oppdatering
			if(element.hasAttribute("sz_operation")){
				String temp = element.getAttribute("sz_operation");
				// Hvis den skal slettes
				if(temp.compareTo("delete") == 0)
					rootnd.removeChild(element);
				element.removeAttribute("sz_operation");
			}
		}
		writeXMLFile(xmlDoc,ParmConstants.xmlLocation);
		zipXmlFile(files);
		return returnValue;
	}
	
	public int updatePolyXml(String pk, String temppk) {
		returnValue = 0;
		
		// Må pakke ut filene før jeg begynner å oppdatere dem ;)
		unzipXmlFile(ParmConstants.xmlLocation);
		
		Document xmlDoc = getXMLDocument(ParmConstants.polyxmlLocation,ParmConstants.xmlLocation);
		// Dette brukes til zipingen
		String[] files = new String[2];
		files[0] = ParmConstants.xmlLocation;
		files[1] = ParmConstants.polyxmlLocation;
		
		//Jeg finner ut hvilket objekt det er også kan jeg gå gjennom hele xmlfilen og bytte ut
		Element element = null;
		Element rootnd = xmlDoc.getDocumentElement();
		if (rootnd == null) {
			rootnd = (Element) xmlDoc.createElement("polyroot");
		}
		
		NodeList nl = rootnd.getElementsByTagName("objectpolygon");
		
		// Jeg må inn i hver type og oppdatere pk'ene, det må jo skiftes ut i parent også vet du...
		for(int i=0;i<nl.getLength() ;i++){
			
			element = (Element)nl.item(i);	
	
			//rootnd.appendChild(element);
			// Denne kan ha temppk
			if((element.getAttribute("l_objectpk")).equals(temppk)) {
				element.setAttribute("l_objectpk", pk);						
			}
			
			// Hvis denne har vært oppdatert må operation attributtet fjernes slik at det ikke blir tatt med i neste oppdatering
			if(element.hasAttribute("sz_operation")){
				String temp = element.getAttribute("sz_operation");
				// Hvis den skal slettes
				if(temp.compareTo("delete") == 0)
					rootnd.removeChild(element);
				element.removeAttribute("sz_operation");
			}
		}
		
		String[] tagName = { "alertpolygon", "cellbroadcast", "alertellipse" };
		
		for(int j=0;j<tagName.length;j++) {
		
			nl = rootnd.getElementsByTagName(tagName[j]);
			
			// Jeg må inn i hver type og oppdatere pk'ene, det må jo skiftes ut i parent også vet du...
			for(int i=0;i<nl.getLength() ;i++){
	
				element = (Element)nl.item(i);
	
				// Denne kan ha temppk
				if((element.getAttribute("l_alertpk")).equals(temppk))
					element.setAttribute("l_alertpk", pk);
					
				// Hvis denne har vært oppdatert må operation attributtet fjernes slik at det ikke blir tatt med i neste oppdatering
				if(element.hasAttribute("sz_operation")){
					String temp = element.getAttribute("sz_operation");
					// Hvis den skal slettes
					if(temp.compareTo("delete") == 0)
						rootnd.removeChild(element);
					element.removeAttribute("sz_operation");
				}
			}
		}

//		// Denne er ny:
//		nl = rootnd.getElementsByTagName("cellbroadcast");
//		
//		// Jeg må inn i hver type og oppdatere pk'ene, det må jo skiftes ut i parent også vet du...
//		for(int i=0;i<nl.getLength() ;i++){
//
//			element = (Element)nl.item(i);
//
//			// Denne kan ha temppk
//			if((element.getAttribute("l_alertpk")).equals(temppk))
//				element.setAttribute("l_alertpk", pk);
//				
//			// Hvis denne har vært oppdatert må operation attributtet fjernes slik at det ikke blir tatt med i neste oppdatering
//			if(element.hasAttribute("sz_operation")){
//				String temp = element.getAttribute("sz_operation");
//				// Hvis den skal slettes
//				if(temp.compareTo("delete") == 0)
//					rootnd.removeChild(element);
//				element.removeAttribute("sz_operation");
//			}
//		}
		
		writeXMLFile(xmlDoc,ParmConstants.polyxmlLocation);
		zipXmlFile(files);
		return returnValue;
	}

	private int writePolyXml(Object obj, String filepath){
		String objectPK = null;
		String objectPolygon = null;
		ObjectVO object = null;
		AlertVO alert = null;
		
		// Document xmlDoc = getXMLDocument(parm.constants.Constants.polyxmlLocation);
		Document xmlDoc = null;
		
		//xmlDoc = getXMLDocument(ParmConstants.xmlLocation,filepath); // MÅtte fjerne denne fordi den fant en zipfil(katastrofe)
		xmlDoc = getXMLDocument(filepath,filepath);
		
		// Henter rootelementet
		Element rootnd = xmlDoc.getDocumentElement();
		
		if (obj.getClass().equals(ObjectVO.class)) {
			objectPK = ParmConstants.xmlElmObjectPk;
			object = (ObjectVO) obj;
			if(object.getM_shape() != null) {
				if(object.getM_shape().getType() == ShapeStruct.SHAPE_ELLIPSE) {
					objectPolygon = ParmConstants.xmlElmObjectEllipse;
					xmlDoc = writeEllipse(object,filepath,xmlDoc,objectPolygon,objectPK);
				} else if(object.getM_shape().getType() == ShapeStruct.SHAPE_POLYGON) {
					objectPolygon = ParmConstants.xmlElmObjectPoly;
					xmlDoc = writePolygon(object,filepath,xmlDoc,objectPolygon,objectPK);
				}
			}
			else if(rootnd == null) {
				rootnd = (Element) xmlDoc.createElement("polyroot");
				xmlDoc.appendChild(rootnd);
			}
		}
		else if (obj.getClass().equals(AlertVO.class)) {
			objectPK = ParmConstants.xmlElmAlertPk;
			alert = (AlertVO)obj;
			if(alert.getM_shape() != null) {
				if(alert.getM_shape().getType() == ShapeStruct.SHAPE_ELLIPSE) {
					objectPolygon = ParmConstants.xmlElmAlertEllipse;
					xmlDoc = writeEllipse(alert,filepath,xmlDoc,objectPolygon,objectPK);
					// Her må jeg ha writeCellBroadcast
					if(alert.getArea() != null);
						//xmlDoc = writeCellBroadcast(alert,filepath,xmlDoc,"cellbroadcast",objectPK);
				} else if(alert.getM_shape().getType() == ShapeStruct.SHAPE_POLYGON) {
					objectPolygon = ParmConstants.xmlElmAlertPoly;
					xmlDoc = writePolygon(alert,filepath,xmlDoc,objectPolygon,objectPK);
					// Her må jeg ha writeCellBroadcast
					if(alert.getArea() != null);
						//xmlDoc = writeCellBroadcast(alert,filepath,xmlDoc,"cellbroadcast",objectPK);
						
				} else if(alert.getM_shape().getType() == ShapeStruct.SHAPE_GISIMPORT) {
					objectPolygon = "alertstreetid";
					xmlDoc = writeGIS(alert,filepath,xmlDoc,objectPolygon,objectPK);
				}
			}
			else if(rootnd == null) {
				rootnd = (Element) xmlDoc.createElement("polyroot");
				xmlDoc.appendChild(rootnd);
			}
		}	

		writeXMLFile(xmlDoc,filepath);
		
		return returnValue;
	}
	
	private Document writeCellBroadcast(AlertVO alert, Element parentnode, Document xmlDoc, String cellBroadcast) {
		boolean remove = false;
		Element element = null;
		
		if(alert.getArea() != null) {
			NodeList nl = parentnode.getElementsByTagName(cellBroadcast);
			if(nl.getLength()>0) {
				element = (Element)nl.item(0);
			
				// Må sjekke om objektet skal slettes, da må jeg fjerne det fra polyxml
				// Her må jeg sjekke om det er alert e
				// ller object
                if(alert.getOperation()!=null && alert.getOperation().equals("delete")){
                    if(alert.getArea()!=null){
                        parentnode.removeChild(element);
                        remove = true;
                    }
                }
                else if(element != null)
                    element.setAttribute("l_alertpk", alert.getPk());
			}
				
//			 Hvis noden er fjernet er det ikke mye vits å gjøre dette
			if(!remove && alert.getArea() != null){ // Den siste biten la jeg til for at den ikke skulle skrive bare farger i polyxml
				
				if(element == null){ // Hvis element fortsatt er null nå finnes ikke dette elementet fra før
					element = (Element) xmlDoc.createElement(cellBroadcast);
					parentnode.appendChild(element);
				}
				
				element.setAttribute("l_alertpk", alert.getAlertpk());
				element.setAttribute("sz_id", alert.getArea().get_id());
				element.setAttribute("sz_area", alert.getArea().get_name());
				if(alert.getOperation() != null)
					element.setAttribute("sz_operation", alert.getOperation());
				// Her må jeg fjerne meldingene fordi jeg gidder ikke oppdatere
				NodeList messages = element.getChildNodes();
				Element tmpNode;
				for(int i=messages.getLength()-1;i>=0;i--) {
					tmpNode = (Element)messages.item(i);
					if(tmpNode!= null)
						element.removeChild(tmpNode); // Mulig jeg må ha i-1 her
				} 
				
				for(int i=0;i<alert.getCBMessages().size();i++) {
					CBMessage cbm = (CBMessage)alert.getCBMessages().get(i);
					Element messagenode = (Element) xmlDoc.createElement("message");
					element.appendChild(messagenode);
					messagenode.setAttribute("sz_lang", cbm.getMessageName());
					messagenode.setAttribute("sz_text",cbm.getMessage());
					messagenode.setAttribute("sz_cb_oadc", cbm.getCboadc());
					
					for(int j=0;j<cbm.getCcodes().size();j++) {
						Element ccnode = (Element) xmlDoc.createElement("ccode");
						messagenode.appendChild(ccnode);
						ccnode.setTextContent(((CCode)cbm.getCcodes().get(j)).getCCode());						
					}
				}
				
//				messagenode = (Element) xmlDoc.createElement("message");
//				cellnode.appendChild(messagenode);
//				messagenode.setAttribute("sz_ccode", alert.getInternationalLanguage()[0]);
//				messagenode.setAttribute("sz_text", alert.getInternationalLanguage()[1]);
				
				/*
				 *
				 <cellbroadcast l_alertpk="a500000000304" sz_area="Test1 (ID 1)" sz_id="1">
					<cb-languages>
						<message sz_lang="English" sz_text="english for iron baners" sz_cb_oadc="HilsenSvein">
							<ccode>-1</ccode>
						</message>
						<message sz_lang="Scandivaian" sz_text="noregs dilldall" sz_cb_oadc="HilsenSvein">
							<ccode>0047</ccode>
							<ccode>0045</ccode>
							<ccode>0046</ccode>
						</message>
						<message sz_lang="Ze Germanz" sz_text="Do hast meine lederhosen gestohlen" sz_cb_oadc="HilsenSvein">
							<ccode>0041</ccode>
						</messages>
					</cb-languages>
				  </cellbroadcast>

				 * 
				 */
			}
			
		}
		return xmlDoc;
	}
	
	public String generateCBXML(Sending_Cell_Broadcast_text cbpanel) { // Returnerer filepath til xmlfilen
		Document xmlDoc = null;
		DocumentBuilder db = null;
		DocumentBuilderFactory dbf = null;
		
		try {
			dbf = DocumentBuilderFactory.newInstance();
			db = dbf.newDocumentBuilder();
			xmlDoc = db.newDocument();
			
			Element rootnd = (Element) xmlDoc.createElement("cellbroadcast");
			xmlDoc.appendChild(rootnd);
			
			rootnd.setAttribute("sz_area", ((Area)(cbpanel.get_combo_area().getSelectedItem())).get_name());
			rootnd.setAttribute("sz_id", ((Area)(cbpanel.get_combo_area().getSelectedItem())).get_id());
			
			for(int i=0;i<cbpanel.get_cbx_messages().getItemCount();i++) {
				CBMessage cbm = (CBMessage)cbpanel.get_cbx_messages().getItemAt(i);
				Element messagend = (Element) xmlDoc.createElement("message");
				rootnd.appendChild(messagend);
				messagend.setAttribute("sz_lang", cbm.getMessageName());
				messagend.setAttribute("sz_text", cbm.getMessage());
				messagend.setAttribute("sz_cb_oadc", cbm.getCboadc());
				for(int j=0;j<cbm.getCcodes().size();j++) {
					Element ccodend = (Element) xmlDoc.createElement("ccode");
					messagend.appendChild(ccodend);
					ccodend.setTextContent(((CCode)cbm.getCcodes().get(j)).getCCode());
				}
			}
			
			/*
			 *
			 <cellbroadcast l_alertpk="a500000000304" sz_area="Test1 (ID 1)" sz_id="1">
				<cb-languages>
					<message sz_lang="English" sz_text="english for iron baners" sz_cb_oadc="HilsenSvein">
						<ccode>-1</ccode>
					</message>
					<message sz_lang="Scandivaian" sz_text="noregs dilldall" sz_cb_oadc="HilsenSvein">
						<ccode>0047</ccode>
						<ccode>0045</ccode>
						<ccode>0046</ccode>
					</message>
					<message sz_lang="Ze Germanz" sz_text="Do hast meine lederhosen gestohlen" sz_cb_oadc="HilsenSvein">
						<ccode>0041</ccode>
					</messages>
				</cb-languages>
			  </cellbroadcast>

			 * 
			 */
			
		} catch (ParserConfigurationException pce) {
			returnValue = -1;
			log.debug("Feil i checkXMLFile: ParserConfigurationException-->" + pce.getMessage());
			Error.getError().addError("XmlWriter","ParserConfigurationException in getXmlDocument",pce,1);
		}
		writeXMLFile(xmlDoc, StorageController.StorageElements.get_path(StorageController.PATH_PARM_) + "cbtemp.xml");
		return StorageController.StorageElements.get_path(StorageController.PATH_PARM_) + "cbtemp.xml";
	}
	
	private Document writePolygon(ParmVO obj, String filepath, Document xmlDoc, String objectPolygon, String objectPK) {
		AlertVO alert = null;
		ObjectVO object = null;
		Element element = null;
		boolean remove = false;
		Color color = null;
		
		PolygonStruct poly = obj.getM_shape().typecast_polygon(); // Jeg har ikke satt polygon til noe, det må jeg huske å gjøre i morgen
		
		// Må sjekke om det har root element
		Element rootnd = xmlDoc.getDocumentElement();
		if (rootnd == null) {
			rootnd = (Element) xmlDoc.createElement("polyroot");
			xmlDoc.appendChild(rootnd);
		}

		// Må sjekke om objektet skal slettes, da må jeg fjerne det fra polyxml
		// Her må jeg sjekke om det er alert eller object
        if(obj.getClass() == ObjectVO.class) {
            element = checkXMLElement(xmlDoc,objectPolygon,ParmConstants.xmlElmObjectPk,obj);
        }
        else {
            element = checkXMLElement(xmlDoc,objectPolygon,ParmConstants.xmlElmAlertPk,obj);
        }
        if(obj.getOperation()!=null && obj.getOperation().equals("delete") && filepath.equals(ParmConstants.polyxmlLocation)){
            if(poly!=null){
                rootnd.removeChild(element);
                remove = true;
            }
        }
        else if(element != null) {
            element.setAttribute(objectPK,obj.getPk());
        }

		// Hvis noden er fjernet er det ikke mye vits å gjøre dette
		if(!remove && poly != null && poly.get_coors_lat().size()>0){ // Den siste biten la jeg til for at den ikke skulle skrive bare farger i polyxml

			if(element == null && poly!=null){ // Hvis element fortsatt er null nå finnes ikke dette elementet fra før
				element = (Element) xmlDoc.createElement(objectPolygon);
				rootnd.appendChild(element);
			}
			if(element != null){
				// Hvis den har childnodes må disse slettes for å legge inn koordinatene på nytt
				if(element.hasChildNodes())
					while(element.getLastChild()!= null)
						element.removeChild(element.getLastChild());
				
				if(object == null){
					element.setAttribute(objectPK, obj.getPk());
					if(obj.getOperation()!=null)
						element.setAttribute("sz_operation",obj.getOperation());
				}
				
				if(poly != null)
					color = poly.get_fill_color();
				
				if(color!=null){
					element.setAttribute("col_r",Integer.toString(color.getRed()));
					element.setAttribute("col_g",Integer.toString(color.getGreen()));
					element.setAttribute("col_b",Integer.toString(color.getBlue()));
					element.setAttribute("col_a",Integer.toString(color.getAlpha()));
				}
				else {
					element.setAttribute("col_r","0");
					element.setAttribute("col_g","0");
					element.setAttribute("col_b","0");
					element.setAttribute("col_a","51");
				}
				if(poly != null){
					// Må hente ut liste med koordinater
					for(int i=0;i<poly.get_coors_lat().size();i++){
						Element ChildElement = (Element) xmlDoc.createElement("polypoint");
						element.appendChild(ChildElement);
						ChildElement.setAttribute("xcord", Double.toString(poly.get_coor_lon(i)));
						ChildElement.setAttribute("ycord", Double.toString(poly.get_coor_lat(i)));
					}
				}
			}
		}
		return xmlDoc;
	}
	
	private Document writeEllipse(ParmVO obj, String filepath, Document xmlDoc, String objectPolygon, String objectPK) {
		ObjectVO object = null;
		AlertVO alert = null;
		EllipseStruct ellipse = obj.getM_shape().typecast_ellipse();
		boolean remove = false;
		Color color = null;
		
		if (obj.getClass().equals(ObjectVO.class)) {
			objectPK = ParmConstants.xmlElmObjectPk;
			objectPolygon = "objectellipse";
			object = (ObjectVO) obj;
		}
		else if (obj.getClass().equals(AlertVO.class)) {
			objectPK = ParmConstants.xmlElmAlertPk;
			objectPolygon = "alertellipse";
			alert = (AlertVO)obj;
		}
		
		Element element = null;
		
		//xmlDoc = getXMLDocument(ParmConstants.xmlLocation,filepath); // MÅtte fjerne denne fordi den fant en zipfil(katastrofe)
		xmlDoc = getXMLDocument(filepath,filepath);
						
		// Må sjekke om det har root element
		Element rootnd = xmlDoc.getDocumentElement();
		if (rootnd == null) {
			rootnd = (Element) xmlDoc.createElement("polyroot");
			xmlDoc.appendChild(rootnd);
		}
		
		// Må sjekke om objektet skal slettes, da må jeg fjerne det fra polyxml
		// Her må jeg sjekke om det er alert eller object
		if(obj != null){
			element = checkXMLElement(xmlDoc,objectPolygon,objectPK,alert);
			try {
				ellipse = obj.getM_shape().typecast_ellipse();
			} catch(Exception e) {
				log.debug("Feil med alert.getM_shape().typecast_ellipse();");
				log.warn(e.getMessage(), e);
			}
			if(obj.getOperation()!=null && obj.getOperation().equals("delete") && filepath.equals(ParmConstants.polyxmlLocation)){
				if(ellipse!=null){
					rootnd.removeChild(element);
					remove = true;
				}
			}
			else if(element != null)
				element.setAttribute(objectPK,alert.getAlertpk());
		}
		
		// Hvis noden er fjernet er det ikke mye vits å gjøre dette
		if(!remove && ellipse != null && ellipse.get_center() != null){ // Den siste biten la jeg til for at den ikke skulle skrive bare farger i polyxml
			if(element == null && ellipse!=null){ // Hvis element fortsatt er null nå finnes ikke dette elementet fra før
				element = (Element) xmlDoc.createElement(objectPolygon);
				rootnd.appendChild(element);
			}
			if(element != null){
				if(object == null){
					element.setAttribute(objectPK, alert.getAlertpk());
					if(alert.getOperation()!=null)
						element.setAttribute("sz_operation",alert.getOperation());
					else
						element.removeAttribute("sz_operation");
				}
				else if(alert == null){
					element.setAttribute(objectPK, object.getObjectPK());
					if(object.getOperation()!=null)
						element.setAttribute("sz_operation",object.getOperation());
					else
						element.removeAttribute("sz_operation");
				}
				
				if(ellipse != null) {
					color = ellipse.get_fill_color();
					element.setAttribute("centerx", Double.toString(ellipse.get_center().get_lon()));
					element.setAttribute("centery", Double.toString(ellipse.get_center().get_lat()));
					element.setAttribute("cornerx", Double.toString(ellipse.get_corner().get_lon()));
					element.setAttribute("cornery", Double.toString(ellipse.get_corner().get_lat()));
				}
				
				if(color!=null){
					element.setAttribute("col_r",Integer.toString(color.getRed()));
					element.setAttribute("col_g",Integer.toString(color.getGreen()));
					element.setAttribute("col_b",Integer.toString(color.getBlue()));
					element.setAttribute("col_a",Integer.toString(color.getAlpha()));
				}
				else {
					element.setAttribute("col_r","0");
					element.setAttribute("col_g","0");
					element.setAttribute("col_b","0");
					element.setAttribute("col_a","51");
				}
			}
		}
		//writeXMLFile(xmlDoc,filepath);
		return xmlDoc;
	}
	
	// Denne gjør jobbem som checkXMLFile gjorde
	private Document getXMLDocument(String filepath, String xmlpath) {
		Document xmlDoc = null;
		DocumentBuilder db = null;
		DocumentBuilderFactory dbf = null;
		InputStream is = null;
		File xmlfile = null;
		File unzipedFile = null;
		String strTemp = null;
		// Et skikkelig tjuvtrix for å få det til å funke, for den må ikke finne zip filen når den skal skrive temppolyxml
		if(filepath.contains("polyxml"))
			xmlfile = new File(filepath.substring(0,filepath.indexOf(".zip")) + ".xml"); //Denne må til pga det elendige trixet mitt over
		else if(filepath.equals(ParmConstants.polyxmlLocation))
			xmlfile = new File(ParmConstants.xmlLocation);
		else
			xmlfile = new File(filepath);
				
		// Bruker dette for å gjøre ha noe å hente den riktige xml filen ut fra zip filen
		if(!filepath.contains(".ini")) {
			int index = xmlpath.indexOf(".zip");
			strTemp = xmlpath.substring(xmlpath.lastIndexOf(File.separator)+1,index) + ".xml";
			//strTemp = xmlpath.substring(0,index) + ".xml";
			
			// Den må ikke pakke ut zipfilen for hver gang, sjekker om xmlfilen eksisterer
			index = filepath.indexOf(".zip");
			//String strTemp2 = filepath.substring(filepath.lastIndexOf("\\")+1,index) + ".xml";
			String strTemp2 = filepath.substring(0,index) + ".xml";
			unzipedFile = new File(strTemp2);
		}
		else
			unzipedFile = new File(filepath);
		
		
		if(xmlfile.exists()){
			try{
				// Her sjekker jeg om xmlfilen er pakket ut, hvis ikke pakkes den ut
				if(unzipedFile.exists())
					is = new FileInputStream(unzipedFile);
				else{
					ZipFile zipfile = new ZipFile(xmlfile);
					Enumeration en = zipfile.entries();
					while(en.hasMoreElements()){
						ZipEntry entry = (ZipEntry)en.nextElement();
						if(entry.getName().compareTo(strTemp) == 0){
							is = zipfile.getInputStream(entry);
						}
					}
					if(is == null)
						is = new GZIPInputStream(new FileInputStream(xmlfile));
				}
				dbf = DocumentBuilderFactory.newInstance();
				dbf.setNamespaceAware(true);
				db = dbf.newDocumentBuilder();
				xmlDoc = db.parse(is);
			}
			catch(ZipException ze){
				Error.getError().addError("XmlWriter","ZipException in getXmlDocument",ze,1);
				log.debug("XMLWriter - getXMLDocument: ZipException - " + ze.getMessage());
			}
			catch(IOException ioe){
				Error.getError().addError("XmlWriter","IOException in getXmlDocument",ioe,1);
				log.debug("XMLWriter - getXMLDocument: IOException - " + ioe.getMessage());
			}
			catch(ParserConfigurationException pce){
				Error.getError().addError("XmlWriter","ParserConfigurationException in getXmlDocument",pce,1);
				log.debug("XMLWriter - getXMLDocument: ParserConfigurationException - " + pce.getMessage());
			}
			catch(SAXException saxe){
				Error.getError().addError("XmlWriter","SAXException in getXmlDocument",saxe,1);
				log.debug("XMLWriter - getXMLDocument: SAXException - " + saxe.getMessage());
			}
			catch(IllegalArgumentException iae) {
				log.warn(iae.getMessage(), iae);
			}
		}
		else {
			if (xmlDoc == null) {
				try {
					dbf = DocumentBuilderFactory.newInstance();
					db = dbf.newDocumentBuilder();
					xmlDoc = db.newDocument();
				} catch (ParserConfigurationException pce) {
					returnValue = -1;
					log.debug("Feil i checkXMLFile: ParserConfigurationException-->" + pce.getMessage());
					Error.getError().addError("XmlWriter","ParserConfigurationException in getXmlDocument",pce,1);
				}
			}
		}
		
		return xmlDoc;
	}
	
	
	// Trekker ut objektene og gjør klar til lagring
	public Collection<Object> extractObjects(Collection<Object> objectList){
		Iterator <Object>it = objectList.iterator();
		ArrayList <Object>list = new ArrayList<Object>();
		
		while(it.hasNext()){
			Object o = it.next();
			test(o,list);
		}
		
		return list;
	}
	
	// ObjectVO har jo en arraylist med objekter som også kan være av typen ObjectVO
//	 ObjectVO har jo en arraylist med objekter som også kan være av typen ObjectVO
	private void test(Object o, ArrayList<Object> list){	

		if(o.getClass().equals(ObjectVO.class)){
			ObjectVO oo = (ObjectVO)o;
			list.add(oo);

            for (Object obj : oo.getList()) {
                if (obj.getClass().equals(ObjectVO.class)) {
                    test((ObjectVO) obj, list); // Må sende til seg selv for å komme til neste objekt
                } else if (obj.getClass().equals(EventVO.class)) { // Nå har jeg kommet til kanten
                    EventVO event = (EventVO) obj;
                    if (event.getAlertListe() != null) {
                        Iterator<Object> eventIt = event.getAlertListe().iterator();
                        while (eventIt.hasNext()) {
                            AlertVO alert = (AlertVO) eventIt.next();
                            list.add(alert);
                        }
                    }
                    list.add(event);
                }
            }
		}
		else if(o.getClass().equals(EventVO.class)){
			EventVO event = (EventVO)o;
			
			if (event.getAlertListe() != null){
				Iterator<Object> eventIt = event.getAlertListe().iterator();
				while(eventIt.hasNext()){
					AlertVO alert = (AlertVO)eventIt.next();
					list.add(alert);
				}
			}
			list.add(event);
		}	
		else
			list.add(o);
	}

	// Må ha en metode som sletter objekt fra xmlfilen public int deleteObject(Object o, int pk);
	public Object deleteObject(Object o){
		
		// Må få lagt inn
		ArrayList <Object>list = new ArrayList<Object>();
		list.add(o);
		list = (ArrayList<Object>)extractObjects(list);
		Iterator<Object> it = list.iterator();
		
		Document xmlDoc = getXMLDocument(XMLFilePath,XMLFilePath);
		Element element = null;

		while(it.hasNext()) {
			o = it.next();
			try {
				// Må sjekke om det har root element
				Element rootnd = xmlDoc.getDocumentElement();
				if (rootnd == null) {
					rootnd = (Element) xmlDoc.createElement("parmroot");
					xmlDoc.appendChild(rootnd);
				}
				
				if (o.getClass().equals(ObjectVO.class)) {
					// Slett object!
					//ObjectVO object = (ObjectVO)o;
					element = checkXMLElement(xmlDoc,strObject,strObjectPK,o);
					if(element != null)
						rootnd.removeChild(element);
					
				}else if (o.getClass().equals(EventVO.class)) {
					// Slett event
					//EventVO event = (EventVO)o;
					element = checkXMLElement(xmlDoc,strEvent,strEventPK,o);
					if(element != null)
						rootnd.removeChild(element);
					
				}else if (o.getClass().equals(AlertVO.class)) {
					// Slett alert
					//AlertVO alert = (AlertVO)o;
					element = checkXMLElement(xmlDoc,strAlert,strAlertPK,o);
					if(element != null)
						rootnd.removeChild(element);
					
				}else if(o.getClass().equals(CategoryVO.class)) {
					// Denne skal egentlig ikke kunne slettes
				}
			}	
			catch(Exception e){
				log.debug("XMLWriter: " + e.getMessage());
				Error.getError().addError("XmlWriter","Exception in deleteObject",e,1);
			}	
		}
		
		writeXMLFile(xmlDoc,XMLFilePath);
		
		return null;
	}
	
	public void writeXMLFile(Document xmlDoc, String filepath){
		
		try {
			
			// Her skriver jeg xmlfilen, må gjøre det for at jeg skal kunne legge den i zipfilen
			// Trenger denne for å få skrevet ut filen
			TransformerFactory tf = TransformerFactory.newInstance();
			// set all necessary features for your transformer -> see OutputKeys
			Transformer t = tf.newTransformer();
			
			File file = null;
			
			if(!filepath.contains(".ini")) {
				int index = filepath.indexOf(".zip");
				if(index != -1)
					file = new File(filepath.substring(0,index) + ".xml");
				else
					file = new File(filepath);
			}
			else
				file = new File(filepath);
			// Må gjøre det slik for at den skal klare å lukke filen etterpå (som igjen gjør at filen kan slettes)
			OutputStream os = new FileOutputStream(file);
			
			//FileOutputStream result = new FileOutputStream(file);
			StreamResult result = new StreamResult(os);
			try
			{
				t.transform(new DOMSource(xmlDoc), result);
			}
			catch(TransformerException e)
			{
				log.warn(e.getMessage(), e);
			}

			t = null;
			tf = null;
			result = null;
			os.close();
			file = null;
		}
		catch(TransformerConfigurationException tce){
			log.debug("XMLWriter: TransformerConfigurationException --> " + tce.getMessage());
			Error.getError().addError("XmlWriter","TransformerConfigurationException in writeXMLFile",tce,1);
			returnValue = -1;
		}
		catch(TransformerException te){
			Error.getError().addError("XmlWriter","TransformerException in writeXMLFile",te,1);
			log.debug("XMLWriter: TransformerException --> " + te.getMessage());
			returnValue = -1;
		}
		catch(FileNotFoundException fnfe){
			Error.getError().addError("XmlWriter","FileNotFoundException in writeXMLFile",fnfe,1);
			log.debug("XMLWriter: FileNotFoundException --> " + fnfe.getMessage());
			//fnflog.warn(e.getMessage(), e);
			returnValue = -1;
		}
		catch(IOException ioe){
			Error.getError().addError("XmlWriter","IOException in writeXMLFile",ioe,1);
			System.out.print("XmlWriter: IOException --> " + ioe.getMessage());
			returnValue = -1;
		}
		catch(Exception e){
			Error.getError().addError("XmlWriter","Exception in writeXMLFile",e,1);
			log.debug("XmlWriter: Exception --> " + e.getMessage());
			returnValue = -1;
		}
	}
	public void setRootTimestamp(String timestamp){
		rootTimestamp = timestamp;
	}
	
//	private void unzipXmlFile(String filepath){
//		// Denne pakker ut filene fra zipfilen også sletter den etterpå.
//		try {
//			ZipFile zipfile = new ZipFile(filepath);
//			Enumeration en = zipfile.entries();
//			while(en.hasMoreElements()){
//				ZipEntry entry = (ZipEntry)en.nextElement();
//				entry.
//			}
//		}
//		catch(Exception e){
//			log.debug("Exception: XmlWriter -- unzipXmlFile" + e.getMessage());
//		}
//	}
	
	public int zipXmlFile(String[] files){
		final int BUFFER = 2048;
		ZipEntry entry = null;
		File file = null;
		
		try {
			// Her begynner zipingen
			BufferedInputStream origin = null;
			FileOutputStream dest = null;
			
			/*
			for(int i=0;i<files.length;i++){
				if(files[i].indexOf("tempxml") == -1)
					destination = files[1]; // zip filen
				else
					destination = files[0]; // zip filen
			}*/
			
			dest = new FileOutputStream(files[0]);
			
			ZipOutputStream outzip = new ZipOutputStream(new BufferedOutputStream(dest));
			outzip.setMethod(ZipOutputStream.DEFLATED); //Betyr at den komprimerer
			
			byte data[] = new byte[BUFFER];
			
			String entryName;
			// Her henter jeg opp igjen og legger inn filene som jeg unzipet tidligere
			for(int i=0;i<files.length;i++){
				entryName = files[i].substring(files[i].lastIndexOf(File.separator)+1); // Hva entry'en skal hete
				entryName = entryName.substring(0,entryName.lastIndexOf(".zip")) + ".xml";
				FileInputStream in = new FileInputStream(files[i].substring(0,files[i].lastIndexOf(".zip")) + ".xml");
				origin = new BufferedInputStream(in,BUFFER);
				entry = new ZipEntry(entryName);
				outzip.putNextEntry(entry);
				// Overfører data til zipfilen
	            int count;
	            while ((count = origin.read(data, 0, BUFFER)) != -1) {
	                outzip.write(data, 0, count);
	            }
	            origin.close();
			}
			
	        outzip.close();
	        dest.close();
	        
	        if(returnValue == 0)
	        	for(int i=0;i<files.length;i++){
	        		file = new File(files[i].substring(0,files[i].lastIndexOf(".zip"))+ ".xml");
	        		if(!file.delete())
	            		returnValue = -1;
				}
		}
		catch(ZipException ze){
			Error.getError().addError("XmlWriter","ZipException in zipXMLFile",ze,1);
			log.debug("XMLWriter: ZipException --> " + ze.getMessage());
			returnValue = -1;
		}
		catch(FileNotFoundException fe){
			Error.getError().addError("XmlWriter","FileNotFoundException in zipXMLFile",fe,1);
			log.debug("XMLWriter: zipXmlFile -->" + fe.getMessage());
		}
		catch(IOException ioe){
			Error.getError().addError("XmlWriter","IOException in zipXMLFile",ioe,1);
			System.out.print("XmlWriter: IOException --> " + ioe.getMessage());
			returnValue = -1;
		}
		return returnValue;
	}
	
	private void unzipXmlFile(String filepath){
		final int BUFFER = 2048;
		ZipEntry entry = null;
		int entryCount = 0; // Tar vare på hvor mange filer som ligger i zipfilen
		
		try {
			File zipfile = new File(filepath);
			if(zipfile.exists()){
				FileInputStream fis = new FileInputStream(filepath);
				ZipInputStream zin = new ZipInputStream(new BufferedInputStream(fis));
				
				while((entry = zin.getNextEntry()) != null){
					//Pakker ut filene
					FileOutputStream fos = new FileOutputStream(ParmConstants.homePath + entry.getName());
					BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER);
					int count;
					byte data[] = new byte[BUFFER];
					while ((count = zin.read(data, 0, BUFFER)) != -1) {
					   //System.out.write(x);
					   dest.write(data, 0, count);
					}
					dest.flush();
					dest.close();
					entryCount++;
				}
				zin.close();
			}
		} catch(ZipException ze){
			Error.getError().addError("XmlWriter","ZipException in unzipXMLFile",ze,1);
			log.warn(ze.getMessage(), ze);
		} catch(FileNotFoundException fnf){
			Error.getError().addError("XmlWriter","FileNotFoundException in unzipXMLFile",fnf,1);
			log.warn(fnf.getMessage(), fnf);
		} catch(IOException ioe){
			Error.getError().addError("XmlWriter","IOException in unzipXMLFile",ioe,1);
			log.warn(ioe.getMessage(), ioe);
		}
	}
	
	public boolean deleteZip(String filepath){
		File zipfile = new File(filepath);
		return zipfile.delete();
	}
	
	// Sjekker om elementet eksisterer
	private Element checkXMLElement(Document xmlDoc, String tag, String pk_tag, Object o){
		String objPk = "-1";
		Element element = null;
		
		if (o.getClass().equals(CategoryVO.class)) {
			CategoryVO cat = (CategoryVO)o;
			objPk = cat.getCategoryPK();
			cat = null;
		}else if (o.getClass().equals(ObjectVO.class)) {
			// Denne if setningen må til for at polygoner med temppk skal bli oppdatert
			ObjectVO obj = (ObjectVO)o;
			if(obj.getTempPk() != null)
				objPk = obj.getTempPk();
			else
				objPk = obj.getObjectPK();
			obj = null;
		}else if (o.getClass().equals(EventVO.class)) {
			EventVO event = (EventVO)o;
			objPk = event.getEventPk();
			event = null;
		}else if (o.getClass().equals(AlertVO.class)) {
			// Denne if setningen må til for at polygoner med temppk skal bli oppdatert
			AlertVO alert = (AlertVO)o;
			//if(!alert.hasValidPk())
			if(alert.getTempPk() != null && alert.getOperation() != "delete") // Nå er det ikke lenger mulig å slette en med temppk pga update etter hver lagring
				objPk = alert.getTempPk();
			else
				objPk = alert.getAlertpk();
			alert = null;
		}
		// Henter ut elementene med denne tag'en
		NodeList nl = xmlDoc.getElementsByTagName(tag);
		if(nl.getLength() > 0) {
			int i=0;
			while(i < nl.getLength()){
				element = (Element)nl.item(i);
				//log.debug("if(" + Integer.parseInt(element.getAttribute(pk)) + " != " + objPk + ")");
				if(element.getAttribute(pk_tag).compareTo(objPk) == 0)
					break; // Hvis den kommer inn her så eksisterer elementet fra før med samme pk
				element = null;
				i++;
			}
		}
		return element;
	}

	public void deleteFromTree(String pk) {
		unzipXmlFile(ParmConstants.xmlLocation);
		Document xmlDoc = getXMLDocument(ParmConstants.xmlLocation,ParmConstants.xmlLocation);
		
//		Jeg finner ut hvilket objekt det er også kan jeg gå gjennom hele xmlfilen og bytte ut
		Element element = null;
		Element rootnd = xmlDoc.getDocumentElement();
		if (rootnd != null) {
			NodeList nl = rootnd.getElementsByTagName("paobject");
			// Brukes slik at jeg kan gå gjennom objektene og sette parent id
			String temppk = pk;
			
			// Jeg må inn i hver type og oppdatere pk'ene, det må jo skiftes ut i parent også vet du...
			for(int i=0;i<nl.getLength();++i){	
				element = (Element)nl.item(i);	
				// Her må jeg finne på noe for å kunne slette alle underliggende, nei, alle pk'ene som skal slettes kommer fra databasen
				if(element != null){
					if((element.getAttribute("l_objectpk")).compareTo(temppk) == 0){
						Element parent = (Element)element.getParentNode();
						parent.removeChild(element);
						deletePolygon(pk);
						try{
							DefaultMutableTreeNode node = PAS.get_pas().get_parmcontroller().findNodeByPk(pk);
							if(node != null)
								PAS.get_pas().get_parmcontroller().getTreeCtrl().getGui().getTreeModel().removeNodeFromParent(node);
						} catch(Exception e){
							Error.getError().addError("XmlWriter","Exception in deleteFromTree", e, 2);
						}
					}
				}
			}
			// Nå slettes første child av objekt med denne pk'en, må finne på et bra trix for å 
			nl = rootnd.getElementsByTagName("paevent");
			
			// Jeg må inn i hver type og oppdatere pk'ene, det må jo skiftes ut i parent også vet du...
			for(int i=0;i<nl.getLength();++i){	
				element = (Element)nl.item(i);	
				if(element != null){
					if((element.getAttribute("l_eventpk")).compareTo(temppk) == 0){
						Element parent = (Element)element.getParentNode();
						parent.removeChild(element);
						try{
							DefaultMutableTreeNode node = PAS.get_pas().get_parmcontroller().findNodeByPk(pk);
							if(node != null)
								PAS.get_pas().get_parmcontroller().getTreeCtrl().getGui().getTreeModel().removeNodeFromParent(node);
						} catch(Exception e){
							Error.getError().addError("XmlWriter","Exception in deleteFromTree", e, 2);
						}
					}
				}
			}
			
			nl = rootnd.getElementsByTagName("paalert");
			
			//Jeg må inn i hver type og oppdatere pk'ene, det må jo skiftes ut i parent også vet du...
			for(int i=0;i<nl.getLength();++i){	
				element = (Element)nl.item(i);	
				if(element != null){
					if((element.getAttribute("l_alertpk")).compareTo(temppk) == 0){
						Element parent = (Element)element.getParentNode();
						parent.removeChild(element);
						deletePolygon(pk);
						try{
							DefaultMutableTreeNode node = PAS.get_pas().get_parmcontroller().findNodeByPk(pk);
							if(node != null)
								PAS.get_pas().get_parmcontroller().getTreeCtrl().getGui().getTreeModel().removeNodeFromParent(node);
						} catch(Exception e){
							Error.getError().addError("XmlWriter","Exception in deleteFromTree", e, 2);
						}
					}
				}
			}
		}
		writeXMLFile(xmlDoc,ParmConstants.xmlLocation);
		zipXmlFile(new String[]{ParmConstants.xmlLocation,ParmConstants.polyxmlLocation});
	}
	
	private void deletePolygon(String primarykey){
		Document polyxmlDoc = getXMLDocument(ParmConstants.polyxmlLocation,ParmConstants.xmlLocation);
		
		Element element = null;
		Element rootnd = polyxmlDoc.getDocumentElement();
		
		if (rootnd != null) {
			NodeList nl = rootnd.getElementsByTagName("objectpolygon");
			
			for(int i=0;i<nl.getLength();++i){	
				element = (Element)nl.item(i);	
				if(element != null){
					if((element.getAttribute("l_objectpk")).equals(primarykey)){
						Element parent = (Element)element.getParentNode();
						parent.removeChild(element);
					}
				}
			}
			
			String[] tagName = { "alertpolygon", "cellbroadcast" };
			for(int j=0;j<tagName.length;j++) {
			
				nl = rootnd.getElementsByTagName(tagName[j]);
			
				for(int i=0;i<nl.getLength();++i){	
					element = (Element)nl.item(i);	
					if(element != null){
						if((element.getAttribute("l_alertpk")).equals(primarykey)){
							Element parent = (Element)element.getParentNode();
							parent.removeChild(element);
						}
					}
				}
			}
		}
		writeXMLFile(polyxmlDoc,ParmConstants.polyxmlLocation);
	}
	
	public void saveSettings(boolean b_save_default_user) {
		Settings settings = PAS.get_pas().get_settings();
		Document doc = getXMLDocument(StorageController.StorageElements.get_path(StorageController.PATH_HOME_) + "settings.ini",StorageController.StorageElements.get_path(StorageController.PATH_HOME_) + "settings.ini");
		
		Element element = null;
		Element rootnd = doc.getDocumentElement();
		
		if(!doc.hasChildNodes()) {
			rootnd = (Element) doc.createElement("settings");
			doc.appendChild(rootnd);
		}
		NodeList nl;
		if(b_save_default_user)
		{
			nl = rootnd.getElementsByTagName("defaultuser");
			
			if(nl.getLength() < 1) {
				element = (Element) doc.createElement("defaultuser");
				rootnd.appendChild(element);
			}
			else
				element = (Element)nl.item(0);
			
			Element userchild = null;
			
			nl = element.getElementsByTagName("username");
			
			if(nl.getLength() < 1) {
				userchild = (Element) doc.createElement("username");
				element.appendChild(userchild);
			} else
				userchild = (Element)nl.item(0);
			
			userchild.setTextContent(settings.getUsername());
			
			nl = element.getElementsByTagName("company");
			
			if(nl.getLength() < 1) {
				userchild = (Element) doc.createElement("company");
				element.appendChild(userchild);
			} else 
				userchild = (Element)nl.item(0);
			
			userchild.setTextContent(settings.getCompany());
		
			nl = element.getElementsByTagName("language");
			if(nl.getLength() < 1) {
				userchild = (Element) doc.createElement("language");
				element.appendChild(userchild);
			} else
				userchild = (Element)nl.item(0);
			userchild.setTextContent(settings.getLanguage());
		}
		try
		{
			writeXMLFile(doc,StorageController.StorageElements.get_path(StorageController.PATH_HOME_) + "settings.ini");
		}
		catch(Exception e)
		{
			
		}
	}
	public void saveScreenSize(Frame frame) {
		Document doc = getXMLDocument(StorageController.StorageElements.get_path(StorageController.PATH_HOME_) + "settings.ini",StorageController.StorageElements.get_path(StorageController.PATH_HOME_) + "settings.ini");
		Element element = null;
		Element rootnd = doc.getDocumentElement();
		
		if(!doc.hasChildNodes()) {
			rootnd = (Element) doc.createElement("settings");
			doc.appendChild(rootnd);
		}
		
		NodeList nl = rootnd.getElementsByTagName("windowsize");
		
		if(nl.getLength() < 1) {
			element = (Element) doc.createElement("windowsize");
			rootnd.appendChild(element);
		}
		else
			element = (Element)nl.item(0);
		
		Element child = null;
		
		String tagname = "ss" + String.valueOf(Toolkit.getDefaultToolkit().getScreenSize().width) + "x"
			+ String.valueOf(Toolkit.getDefaultToolkit().getScreenSize().height);
		
		nl = element.getElementsByTagName(tagname);
		if(nl.getLength() < 1) {
			child = (Element) doc.createElement(tagname);
			element.appendChild(child);
		}
		else
			child = (Element)nl.item(0);
		
		if((frame.getExtendedState() & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH)
			child.setAttribute("fullscreen", "true");
		else
			child.setAttribute("fullscreen", "false");
		child.setAttribute("height", String.valueOf(frame.getHeight()));
		child.setAttribute("width", String.valueOf(frame.getWidth()));
		child.setAttribute("xpos", String.valueOf(frame.getLocation().x));
		child.setAttribute("ypos", String.valueOf(frame.getLocation().y));
		
		nl = rootnd.getElementsByTagName("messagelibdlg");
		if(nl.getLength() < 1) {
			element = (Element) doc.createElement("messagelibdlg");
			rootnd.appendChild(element);
			
		}
		else
			element = (Element)nl.item(0);
		element.setAttribute("x", Integer.toString(PAS.get_pas().get_settings().getRectMessageLibDlg().x));
		element.setAttribute("y", Integer.toString(PAS.get_pas().get_settings().getRectMessageLibDlg().y));
		element.setAttribute("w", Integer.toString(PAS.get_pas().get_settings().getRectMessageLibDlg().width));
		element.setAttribute("h", Integer.toString(PAS.get_pas().get_settings().getRectMessageLibDlg().height));
		
		
		nl = rootnd.getElementsByTagName("messagelibdlg_exploded_nodes");
		if(nl.getLength() < 1) {
			element = (Element) doc.createElement("messagelibdlg_exploded_nodes");
			rootnd.appendChild(element);
			
		}
		else
		{
			
			element = (Element)nl.item(0);
			rootnd.removeChild(element);
			element = (Element) doc.createElement("messagelibdlg_exploded_nodes");
			rootnd.appendChild(element);
			/*try
			{
				for(int x=0; x < element.getChildNodes().getLength(); x++)
				{
					Node node_del = element.getChildNodes().item(x);
					element.removeChild(node_del);
				}
			}
			catch(Exception err)
			{
				
			}*/
		}
		List<Object> list_nodes = PAS.get_pas().get_settings().getMessageLibExplodedNodes();
		if(list_nodes!=null)
		{
			Iterator<Object> it = list_nodes.iterator();
			while(it.hasNext())
			{
				//Long nodepk = (Long)it.next();
				Object nodepk = it.next();
				if(nodepk!=null)
				{
					Element newChild = (Element)doc.createElement("node");
					newChild.setAttribute("pk", nodepk.toString());
					element.appendChild(newChild);
				}
			}
		}
		try {
			writeXMLFile(doc,StorageController.StorageElements.get_path(StorageController.PATH_HOME_) + "settings.ini");
		}
		catch (Exception e) {
			log.warn(e.getMessage(), e);
		}
	}
	
	// Denne er langt fra ferdig, begynnte litt på den, men måtte avbryte
	public void updateStatusItems(Document updatesDoc, Document localDoc, String xmlTag) {
		// Lurer på om jeg skal lage den generell eller om jeg må ha en for hver XMLTing?
		// Jeg kan sikkert bare sende med inn xml-tag'en som skal sjekkes?
		// Jeg må ha en egen responsekodes ting
		NodeList nl = updatesDoc.getElementsByTagName(xmlTag);
		// Denne må jeg sette i PARMConstants
//		if(xmlTag.equals("SENDING")){
//			Send send = new Send(
//		}
		if(nl != null){
			for(int i=0; i < nl.getLength(); i++){
				//Node current = nl.item(i);
				//StatusSending status;
				
			}
		}
		
	}
	
	public void writeTreeToFile(JTree tree, DefaultTreeModel treeModel) {
		//DefaultMutableTreeNode node = (DefaultMutableTreeNode)tre.getRoot();
		
		try {
			File file = new File(ParmConstants.homePath + "tree.cfg");
			FileWriter fw = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(fw);
			PrintWriter writer = new PrintWriter(bw);
			
			DefaultMutableTreeNode root = (DefaultMutableTreeNode)treeModel.getRoot();
			
			for(int i=0;i<root.getChildCount();i++) {
				DefaultMutableTreeNode child = (DefaultMutableTreeNode)root.getChildAt(i);
				if(tree.isExpanded(getTreepath(child)))
					writeNodePK(child,writer,tree);
			}
			writer.flush();
			bw.flush();
			fw.flush();
			
		} catch(IOException io) {
			System.out.print("Feil i writeTreeToFile");
		}
	}
	
	private void writeNodePK(DefaultMutableTreeNode node, PrintWriter writer, JTree tree) {
		writer.print(((ParmVO)node.getUserObject()).getPk() + ",");
		
		for(int i=0;i<node.getChildCount();i++) {
			DefaultMutableTreeNode child = (DefaultMutableTreeNode)node.getChildAt(i);
			if(tree.isExpanded(getTreepath(child)))
				writeNodePK(child,writer,tree);
		}
	}
	
	private TreePath getTreepath(DefaultMutableTreeNode node) {
		return new TreePath(node.getPath());
	}
	
	private Document writeGIS(ParmVO obj, String filepath, Document xmlDoc, String objectPolygon, String objectPK) {
		/*
		<alertstreetid / alertgbno>
			alertgbno
			<line municipalid="XXXX" gno="yyy" bno="zzz" />

			streetid
			<line postno="xxxx" streetid="yyyy" houseno="zz" letter="a" />
		</alertstreetid>*/
		
		AlertVO alert = null;
		// Henter gislist fra konstruktøren
		boolean remove = false;
		Color color = null;
		GISShape gisshape = null;
		GISList gislist = null;
		
		objectPK = ParmConstants.xmlElmAlertPk;
		objectPolygon = "alertstreetid";
		alert = (AlertVO)obj;
		gisshape = (GISShape)alert.getM_shape();
		gislist = gisshape.get_gislist();
		
		Element alertnode = null;
		Element element = null;
		
		//xmlDoc = getXMLDocument(ParmConstants.xmlLocation,filepath); // MÅtte fjerne denne fordi den fant en zipfil(katastrofe)
		xmlDoc = getXMLDocument(filepath,filepath);
						
		// Må sjekke om det har root element
		Element rootnd = xmlDoc.getDocumentElement();
		if (rootnd == null) {
			rootnd = (Element) xmlDoc.createElement("polyroot");
			xmlDoc.appendChild(rootnd);
		}
		
		// Må sjekke om objektet skal slettes, da må jeg fjerne det fra polyxml
		// Her må jeg sjekke om det er alert eller object
		
		alertnode = checkXMLElement(xmlDoc,objectPolygon,objectPK,alert);
		
		if(alertnode == null) {
			alertnode = (Element) xmlDoc.createElement(objectPolygon);
			rootnd.appendChild(alertnode);
		}
		
		if(obj.getOperation()!=null && obj.getOperation().equals("delete") && filepath.equals(ParmConstants.polyxmlLocation)){
			if(gislist!=null){
				rootnd.removeChild(alertnode);
				remove = true;
			}
		}
		else if(alertnode != null) {
			alertnode.setAttribute(objectPK,alert.getAlertpk());
			if(alert.getOperation()!=null)
				alertnode.setAttribute("sz_operation",alert.getOperation());
			else
				alertnode.removeAttribute("sz_operation");
		}
		
		// Hvis noden er fjernet er det ikke mye vits å gjøre dette
		if(!remove && gislist != null){
			log.debug(obj.toString() + " Gislist size: " + gislist.size());
			//int children = 0;
			if(alertnode.hasChildNodes())
				while(alertnode.getLastChild()!= null) {
					alertnode.removeChild(alertnode.getLastChild());
					//children++;
				}

			//log.debug("Removed " + children + " children");
			
			for(int i=0;i<gislist.size();i++) {
				GISRecord gis = (GISRecord)gislist.get(i);
				
				if(gis!=null){ // Hvis element fortsatt er null nå finnes ikke dette elementet fra før
					element = (Element) xmlDoc.createElement("line");
					alertnode.appendChild(element);
				}
				if(element != null){
					//color = gis.get_fill_color();
					element.setAttribute("municipal", gis.get_municipal());
					element.setAttribute("streetid", gis.get_streetid());
					element.setAttribute("houseno", gis.get_houseno());
					element.setAttribute("letter", gis.get_letter());
					element.setAttribute("namefilter1", gis.get_name1());
					element.setAttribute("namefilter2", gis.get_name2());
					
					if(color!=null){
						element.setAttribute("col_r",Integer.toString(color.getRed()));
						element.setAttribute("col_g",Integer.toString(color.getGreen()));
						element.setAttribute("col_b",Integer.toString(color.getBlue()));
						element.setAttribute("col_a",Integer.toString(color.getAlpha()));
					}
					else {
						element.setAttribute("col_r","0");
						element.setAttribute("col_g","0");
						element.setAttribute("col_b","0");
						element.setAttribute("col_a","51");
					}
				}
			}
		}
		//writeXMLFile(xmlDoc,filepath);
		return xmlDoc;

	}
	private void removeCurrentShape(Element element)
	{
		NodeList nl = element.getElementsByTagName(ParmConstants.xmlElmAlertPoly);
		for(int i=0;i<nl.getLength();i++)
			element.removeChild(nl.item(i));
		
		nl = element.getElementsByTagName(ParmConstants.xmlElmAlertEllipse);
		for(int i=0;i<nl.getLength();i++)
			element.removeChild(nl.item(i));
		
		nl = element.getElementsByTagName(ParmConstants.xmlElmAlertstreetid);
		for(int i=0;i<nl.getLength();i++)
			element.removeChild(nl.item(i));
	}
}