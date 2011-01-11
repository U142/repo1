package no.ums.pas.parm.xml;

//Parm packages

//Packages to work with filestreams
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.io.*;
import java.util.*;
import java.util.zip.*;
//Packages to work with xml
import no.ums.pas.PAS;
import no.ums.pas.ParmController;
import no.ums.pas.core.dataexchange.MailAccount;
import no.ums.pas.core.logon.Settings;
import no.ums.pas.core.logon.Settings.MAPSERVER;
import no.ums.pas.core.mainui.EastContent;
import no.ums.pas.core.storage.StorageController;
import no.ums.pas.importer.gis.GISList;
import no.ums.pas.importer.gis.GISRecord;
import no.ums.pas.maps.defines.EllipseStruct;
import no.ums.pas.maps.defines.GISShape;
import no.ums.pas.maps.defines.MapPoint;
import no.ums.pas.maps.defines.MapPointLL;
import no.ums.pas.maps.defines.PolygonStruct;
import no.ums.pas.maps.defines.ShapeStruct;
import no.ums.pas.parm.constants.*;
import no.ums.pas.parm.exception.ParmException;
import no.ums.pas.parm.main.*;
import no.ums.pas.parm.voobjects.*;
import no.ums.pas.parm.voobjects.AlertVO.LBAOperator;
import no.ums.pas.status.StatusItemObject;
import no.ums.pas.ums.errorhandling.Error;

import org.w3c.dom.*;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.xml.parsers.*;

import org.xml.sax.*;

import com.sun.org.apache.xerces.internal.dom.DeferredElementImpl;


public class XmlReader {

	private ZipInputStream zip;
	private XmlPolyReader polyReader;
	private XmlEllipseReader ellipseReader;
	private XmlGISReader gisReader;
	private XmlWriter writer;
	private InputStream xmlStream;
	private DocumentBuilderFactory docBuilderFactory;
	private DocumentBuilder docBuilder;
	private Document doc;
	//private MapPanel map;
	private MainController main;
	private ArrayList <Object>arrayCategories;
	private ArrayList <Object>arrayEvents;
	private ArrayList <Object>arrayObjects;
	private ArrayList <Object>arrayAlerts;
	private Collection <Object>objectList;
	private NodeList listOfObjects;
	private String timestamp;
	private ArrayList <Object>pkListe, tempPkListe;

	public XmlReader() {
		
	}
	
	public XmlReader(MainController main) {
		super();
		this.main = main;
		zip = null;
		writer = null;
		pkListe = null;
		tempPkListe = null;
	}

	public Collection <Object>readXml() throws ParmException {
		/*
		 * Goes through xml file and collects objects,events, alerts to a
		 * collection. This collection is to be used in tree
		 */
		this.pkListe = new ArrayList<Object>();
		this.objectList = new ArrayList<Object>();
		this.getXmlFromZip(ParmConstants.parmXmlName,ParmConstants.xmlLocation);
		
		if (this.xmlStream != null) {
			this.parseXmlDocument();
			this.paCategory();
			this.listOfObjects = null;
			this.paEvent(true); // False indicates that it is a update xml read
			this.sortListAscending(arrayEvents);
			this.listOfObjects = null;
			this.paAlert(true); // True indeicates that it is a normal xml read
			this.sortListAscending(arrayAlerts);
			this.listOfObjects = null;
			this.paObjects(true);
			this.sortListAscending(arrayObjects);
			this.listOfObjects = null;
			//this.readPolyXml();
			
			this.sortArrays();

			objectList.addAll(arrayCategories);
			objectList.addAll(arrayObjects);
		}

		return objectList;
	}

	public Collection <Object>readResponseXml(InputStream in) throws ParmException {
		this.doc = null;
		this.xmlStream = in;
		this.pkListe = new ArrayList<Object>();
		this.tempPkListe = new ArrayList<Object>();
		this.writeXmlFromStream(in, ParmConstants.resZipLocation);
		this.getXmlFromZip(ParmConstants.parmXmlName, ParmConstants.resZipLocation);
		this.parseXmlDocument();
		writer = new XmlWriter();
		if (doc != null) {
			NodeList responseList = doc.getElementsByTagName("responsecodes");
			NodeList updateList = doc.getElementsByTagName("updates");
			NodeList deleteList = doc.getElementsByTagName("delete");
			this.readResponseXml(responseList, updateList);
			// Går gjennom og fjerner slettede elementer fra XML filen
			for(int i=0;i<deleteList.getLength();i++){
				Element element = (Element)deleteList.item(i);
				writer.deleteFromTree(element.getAttribute("l_objectpk"));
				//PAS.get_pas().get_parmcontroller().refreshTree(PAS.get_pas().get_parmcontroller().getTreeCtrl())
			}
		}
		try{
			File file = new File(ParmConstants.resZipLocation);
			file.delete();
		}catch(Exception io){
			System.out.println("Klarte ikke slette "+ParmConstants.resZipLocation+" filen!");
			Error.getError().addError("XmlReader","Exception in readResponseXml",io,1);
		}
		return this.objectList;
	}

	public Collection <Object>readTempXml(XmlWriter writer) throws ParmException {
		this.getXmlFromZip("parmxml.xml", ParmConstants.xmlLocation);
		if (this.xmlStream != null) {
			this.parseXmlDocument();
			timestamp = doc.getDocumentElement().getAttribute("l_timestamp");
			writer.setRootTimestamp(timestamp);
			this.paEvent(false);
			this.sortListAscending(arrayEvents);
// Her tror jeg teorien er at alle oppdateringer skal finnes, men i stedet så mister jeg cell broadcast uten if'en her
			if(arrayAlerts == null || arrayAlerts.size() < 1)
				this.paAlert(false);
			this.sortListAscending(arrayAlerts);
			this.paObjects(false);
			this.sortListAscending(arrayObjects);
			//this.readPolyXml();

			if (!arrayObjects.isEmpty()){
				sortListAscending(arrayObjects);
				objectList.addAll(arrayObjects);
			}
			if (!arrayEvents.isEmpty()){
				sortListAscending(arrayEvents);
				objectList.addAll(arrayEvents);
			}
			if (!arrayAlerts.isEmpty()){
				sortListAscending(arrayAlerts);
				objectList.addAll(arrayAlerts);
			}
		}
		if (objectList == null || objectList.isEmpty())
			return null;
		else
			return objectList;
	}

	public String getRootTimestamp() throws ParmException {
		this.getXmlFromZip(ParmConstants.parmXmlName, ParmConstants.xmlLocation);
		this.parseXmlDocument();
		Node node = doc.getFirstChild();
		Element el = (Element) node;
		this.timestamp = el.getAttribute("l_timestamp");
		return this.timestamp;
	}

	public Collection <Object>getResponseList() {
		return this.objectList;
	}

	private void readPolyXml() throws ParmException {
		this.docBuilderFactory = null;
		this.docBuilder = null;
		this.doc = null;
		this.getXmlFromZip(ParmConstants.polyXmlName,ParmConstants.xmlLocation);
		this.parseXmlDocument();

		if (doc != null) {
			//this.polyReader = new XmlPolyReader(doc, main);
			this.ellipseReader = new XmlEllipseReader(doc, main);
			this.gisReader = new XmlGISReader(doc, main);

			
			//this.polyReader.readPolyObject(this.arrayObjects);
			//this.polyReader.readPolyAlert(this.arrayAlerts);
			// Denne burde vært i denne filen, men det er en praktisk finn alert funksjon i polyreader
			//this.polyReader.readCellBroadcast(this.arrayAlerts);
			
			this.ellipseReader.readEllipseObject(this.arrayObjects);
			this.ellipseReader.readEllipseAlert(this.arrayAlerts);
			// Denne burde vært i denne filen, men det er en praktisk finn alert funksjon i polyreader
			//this.polyReader.readCellBroadcast(this.arrayAlerts);
			this.gisReader.readGISAlert(this.arrayAlerts);
		}
	}

	private void readResponseCodes() {

		for (int i = 0; i < listOfObjects.getLength(); i++) {
			String objectpk, tempPk, status, timestamp, emsg;

			Node object = listOfObjects.item(i);
			Element element = (Element) object;
			objectpk = element.getAttribute("l_objectpk");
			tempPk = element.getAttribute("l_temppk");
			status = element.getAttribute("sz_status");
			timestamp = element.getAttribute("l_timestamp");
			if (status.compareTo("S_OK") == 0) { // Checks if the operation
				// was ok at the Database
				int check = writer.updateXml(objectpk, tempPk, timestamp);
				if (check == 0) {
					System.out.println(objectpk
							+ " is now updated in the local xml file!");
					this.pkListe.add(objectpk);
					this.tempPkListe.add(tempPk);
				} else {
					System.out.println(objectpk
							+ " didnt get updated in the local xml file!");
				}
				check = writer.updatePolyXml(objectpk, tempPk);
				if (check == 0) {
					System.out.println(objectpk
							+ " is now updated in the local xml file!");
					this.pkListe.add(objectpk);
					this.tempPkListe.add(tempPk);
				} else {
					System.out.println(objectpk
							+ " didnt get updated in the local xml file!");
				}
			} else {
				emsg = element.getNodeName();
				Error.getError().addError("Failure in responsecodes", "Error in responsecodes",
						"Object PK: " + objectpk + " Temp PK: " + tempPk + " Status: " + status +
						" Error message: " + emsg + " Timestamp: " + timestamp, -1, Error.SEVERITY_WARNING);
				System.out.println(element.getNodeValue());
			}
			// Her må vi ha en annen sjekk, dersom status = "Failed to insert, så må vi bare la inserten stå"
		}
	}

	private void readResponseXml(NodeList responseList, NodeList updateList)
			throws ParmException {
		// Get elements updated by this user
		if(this.objectList!= null)
			this.objectList.clear();
		else
			objectList = new ArrayList<Object>();
		
		Node responseNode = responseList.item(0);
		Element responseElement = (Element) responseNode;
		if (responseElement.hasChildNodes()) {
			// Work responsecodes
			this.listOfObjects = responseElement.getElementsByTagName(ParmConstants.xmlElmObject);
			this.readResponseCodes();

			this.listOfObjects = responseElement.getElementsByTagName(ParmConstants.xmlElmEvent);
			this.readResponseCodes();

			this.listOfObjects = responseElement.getElementsByTagName(ParmConstants.xmlElmAlert);
			this.readResponseCodes();
		}
		// Get elements created from other users
		Node updatesNode = updateList.item(0); // funker så langt
		Element updateElement = (Element) updatesNode;

		if (updateElement.hasChildNodes()) {
			// Get new items
			this.listOfObjects = updateElement.getElementsByTagName(ParmConstants.xmlElmCat);
			this.paCategory();
			
			this.listOfObjects = updateElement.getElementsByTagName(ParmConstants.xmlElmObject);
			this.paObjects(true);
			this.sortListAscending(arrayObjects);
			this.listOfObjects = updateElement.getElementsByTagName(ParmConstants.xmlElmEvent);
			this.paEvent(true);
			this.sortListAscending(arrayEvents);
			this.listOfObjects = updateElement.getElementsByTagName(ParmConstants.xmlElmAlert);
			this.paAlert(true);
			this.sortListAscending(arrayAlerts);
			
			Collection <Object>col = null;
			
			//Hent ut polygoner som skal legges til objektene
			/*
			doc = null; 
			docBuilder = null; 
			docBuilderFactory = null;
			this.getXmlFromZip(ParmConstants.polyXmlName,ParmConstants.resZipLocation); 
			this.parseXmlDocument();
			
			if (doc != null) { 
				this.polyReader = new XmlPolyReader(doc, main);
				this.ellipseReader = new XmlEllipseReader(doc, main);
				this.gisReader = new XmlGISReader(doc, main);
				
				this.polyReader.readPolyObject(this.arrayObjects);
				this.polyReader.readPolyAlert(this.arrayAlerts);
				// Denne burde vært i denne filen, men det er en praktisk finn alert funksjon i polyreader
				this.polyReader.readCellBroadcast(this.arrayAlerts);
				
				this.ellipseReader.readEllipseObject(this.arrayObjects);
				this.ellipseReader.readEllipseAlert(this.arrayAlerts);
				
				this.gisReader.readGISAlert(this.arrayAlerts);
				//n00b! col = this.polyReader.getUpdatedList();
				//this.objectList.addAll(this.polyReader.getUpdatedList());
			}*/
			// Legg lister inn i object collection
			
			if(col==null){//Dersom det ikke finnes polygonfil
				this.objectList.addAll(this.arrayObjects);
				this.objectList.addAll(this.arrayAlerts);
			}
			else//Legg til alle objektene som har vært i sjekk mot polyon filen
				this.objectList.addAll(col);
			
			this.objectList.addAll(this.arrayEvents);
			this.objectList.addAll(this.arrayCategories);

		}

		if (this.objectList != null) {
			this.timestamp = updateElement.getAttribute("l_timestamp");
			writer.setRootTimestamp(this.timestamp);
			System.out.println("PARM timestamp = " + this.timestamp);
			//Prøver å tømme polyxml slik at det ikke blir lagret dobbelt.
			//writer.writeEmptyPoly(ParmConstants.polyXmlName);
			this.writer.writeXml(this.objectList); // Her skriver den sikkert inn igjen objektene
		}
		this.listOfObjects = null;
		// Legg til de oppdaterte elementene i xml filen til objectListen
		//updateMemObjects();
	}

	private void updateMemObjects() throws ParmException {
		resetXmlStream();
		getXmlFromZip(ParmConstants.parmXmlName, ParmConstants.xmlLocation);
		parseXmlDocument();

		// Henter xml filens elementer og legger i lister, arrayCategories,
		// arrayEvents, arrayObjects og arrayAlerts
		// this.paCategory(); Trenger bare om categorier skal bli oppdatert
		// this.listOfObjects = null;
		this.paEvent(true);
		this.sortListAscending(arrayEvents);// False indicates that it is a update xml read
		this.listOfObjects = null;
// Denne kan ødelegge ganske bra
//		this.paAlert(true); // True indeicates that it is a normal xml read
		this.sortListAscending(arrayAlerts);
		this.listOfObjects = null;
		this.paObjects(true);
		this.sortListAscending(arrayObjects);
		this.listOfObjects = null;
		// Går igjennom alle pk og sjekker mot objects, events, alerts
		// Her må noe gjøres
		if (!pkListe.isEmpty() && !this.objectList.isEmpty()) {
			for (int i = 0; i < this.pkListe.size(); i++) {
				String pk = (String) this.pkListe.get(i);
				String tempPk = (String) this.tempPkListe.get(i);
				// Går igjennom objects
				ArrayList <Object>tempListe = (ArrayList<Object>) this.objectList;
				for (int o = 0; o < tempListe.size(); o++) {
					// MÅ gå igjennom objecter,events og alerts i denne løkken!
					Object obj = tempListe.get(o);
					if (obj.getClass().equals(ObjectVO.class)) {
						ObjectVO oVO = (ObjectVO) obj;
						if (oVO.getObjectPK().compareTo(pk) == 0) {
							oVO.setTempPk(tempPk);
							break;
						}
					} else if (obj.getClass().equals(EventVO.class)) {
						EventVO eVO = (EventVO) obj;
						if (eVO.getEventPk().compareTo(pk) == 0) {
							eVO.setTempPk(tempPk);
							break;
						}
					} else if (obj.getClass().equals(AlertVO.class)) {
						AlertVO aVO = (AlertVO) obj;
						if (aVO.getAlertpk().compareTo(pk) == 0) {
							aVO.setTempPk(tempPk);
							break;
						}
					}
				}
			}
		}
		this.pkListe = null;
	}

	private void paObjects(boolean normalRead) {
		arrayObjects = new ArrayList<Object>();
		if (this.listOfObjects == null)
			this.listOfObjects = doc.getElementsByTagName(ParmConstants.xmlElmObject);

		for (int i = 0; i < listOfObjects.getLength(); i++) {
			boolean isFolder;
			String objectpk, parent, categorypk;
			String name, description, address, postno, place, phone, metadata, timestamp;
			String temp;

			Node objects = listOfObjects.item(i);
			Element eAttributes = (Element) objects;

			if (eAttributes.hasAttribute("sz_operation") || normalRead) {
				temp = eAttributes.getAttribute(ParmConstants.xmlElmObjectPk);
				if (temp.length() > 0)
					objectpk = temp;
				else
					objectpk = "-1";
				// Legg til pk i pklisten
				// this.pkListe.add(objectpk);

				temp = eAttributes.getAttribute("l_parent");
				if (temp.length() > 0)
					parent = temp;
				else
					parent = "-1";

				temp = eAttributes.getAttribute("f_isobjectfolder");
				if (temp.length() > 0) {
					if (Integer.parseInt(temp) == 1)
						isFolder = true;
					else
						isFolder = false;
				} else
					isFolder = true;

				name = eAttributes.getAttribute("sz_name");
				description = eAttributes.getAttribute("sz_description");
				address = eAttributes.getAttribute("sz_address");
				postno = eAttributes.getAttribute("sz_postno");
				place = eAttributes.getAttribute("sz_place");
				phone = eAttributes.getAttribute("sz_phone");
				metadata = eAttributes.getAttribute("sz_metadata");
				timestamp = eAttributes.getAttribute("l_timestamp");

				ObjectVO oo = new ObjectVO(objectpk, name, description, parent,
						address, postno, place, phone, metadata, timestamp,
						isFolder);
				if (eAttributes.hasAttribute("sz_operation"))
					oo.setOperation(eAttributes.getAttribute("sz_operation"));

				temp = eAttributes.getAttribute("l_deptpk");
				if (temp.length() > 0)
					oo.setDeptPK(temp);
				else
					oo.setDeptPK("1");

				temp = eAttributes.getAttribute("l_importpk");
				if (temp.length() > 0)
					oo.setImportPK(temp);

				temp = eAttributes.getAttribute(ParmConstants.xmlElmCatPk);
				if (temp.length() > 0) {
					categorypk = temp;
					oo.setCategoryPK(categorypk);
				} else
					categorypk = "-1";

				if (normalRead) {
					CategoryVO co = null;
					if(arrayCategories!=null)
					{
						Iterator it = arrayCategories.iterator();
						while (it.hasNext()) {
							co = (CategoryVO) it.next();
							if (co.getCategoryPK().compareTo(categorypk) == 0
									&& categorypk.compareTo("-1") != 0) {
								break;
							} else
								co = null;
						}
						if (co != null)
							oo.setCategoryVO(co);
					}
					else
						oo.setCategoryVO(null);
				}
				
				NodeList anl = eAttributes.getElementsByTagName(ParmConstants.xmlElmObjectPoly);

				if(anl.getLength()>0) {
					Element el;
					for(int j=0;j<anl.getLength();++j) {
						el = (Element)anl.item(j);
						if(el.hasAttributes())
							set_PolyCoor(oo, el);
					}
				}
				
				NodeList enl = eAttributes.getElementsByTagName(ParmConstants.xmlElmObjectEllipse);
				
				if(enl.getLength()>0) {
					Element el;
					for(int j=0;j<enl.getLength();++j) {
						el = (Element)enl.item(j);
						if(el.hasAttributes())
							setEllipseCoor(oo, el);
					}
				}
				
				temp = null;
				if (eAttributes.hasAttribute("sz_operation") && normalRead) {
					if (eAttributes.getAttribute("sz_operation").compareTo(
							"delete") == 0) {

					} else
						arrayObjects.add(oo);
				} else
					arrayObjects.add(oo);
			}
		}
		this.listOfObjects = null;
	}

	private void paEvent(boolean normalRead) {
		arrayEvents = new ArrayList<Object>();
		if (this.listOfObjects == null)
			this.listOfObjects = doc
					.getElementsByTagName(ParmConstants.xmlElmEvent);

		for (int i = 0; i < this.listOfObjects.getLength(); i++) {

			String name, categorypk, objectpk, description, timestamp, eventPk;
			String temp;

			Node event = this.listOfObjects.item(i);
			Element eAttributes = (Element) event;
			if (eAttributes.hasAttribute("sz_operation") || normalRead) {
				temp = eAttributes.getAttribute(ParmConstants.xmlElmEventPk);
				if (temp.length() > 0)
					eventPk = temp;
				else
					eventPk = "-1";
				temp = eAttributes.getAttribute("l_parent");
				if (temp.length() > 0) {
					objectpk = temp;
				} else
					objectpk = "-1";
				name = eAttributes.getAttribute("sz_name");
				description = eAttributes.getAttribute("sz_description");
				timestamp = eAttributes.getAttribute("l_timestamp");

				EventVO eo = new EventVO(eventPk, objectpk, name, description,
						timestamp);
				
				if(eAttributes.hasAttribute("f_epi_lon") && eAttributes.hasAttribute("f_epi_lat")) {
					if(eAttributes.getAttribute("f_epi_lon").length() > 0)
						eo.setEpicentreX(Double.parseDouble(eAttributes.getAttribute("f_epi_lon")));
					if(eAttributes.getAttribute("f_epi_lat").length() > 0)
						eo.setEpicentreY(Double.parseDouble(eAttributes.getAttribute("f_epi_lat")));
				}

				if (eAttributes.hasAttribute("sz_operation"))
					eo.setOperation(eAttributes.getAttribute("sz_operation"));

				temp = eAttributes.getAttribute(ParmConstants.xmlElmCatPk);
				if (temp.length() > 0) {
					categorypk = temp;
					eo.setCategorypk(categorypk);
				} else
					categorypk = "-1";
				temp = null;
				CategoryVO co = null;
				if (normalRead) {
					if(arrayCategories!=null)
					{
						Iterator it = arrayCategories.iterator();
						while (it.hasNext()) {
							co = (CategoryVO) it.next();
							if (co.getCategoryPK().compareTo(categorypk) == 0
									&& categorypk.compareTo("-1") != 0) {
								break;
							} else
								co = null;
						}
						if (co != null)
							eo.setCatVO(co);
					}
					else
						eo.setCatVO(null);
				}
				if (eAttributes.hasAttribute("sz_operation") && normalRead) {
					if (eAttributes.getAttribute("sz_operation").compareTo(
							"delete") == 0) {

					} else
						arrayEvents.add(eo);
				} else
					arrayEvents.add(eo);
			}
		}
		this.listOfObjects = null;
	}

	private void paAlert(boolean normalRead) {
		arrayAlerts = new ArrayList<Object>();

		if (this.listOfObjects == null)
			this.listOfObjects = doc
					.getElementsByTagName(ParmConstants.xmlElmAlert);

		for (int i = 0; i < this.listOfObjects.getLength(); i++) {

			int addresstypes;
			String alertPk, parent, name, description, oadc, timestamp;
			String temp;

			Node alert = this.listOfObjects.item(i);
			Element eAttributes = (Element) alert;
			if (eAttributes.hasAttribute("sz_operation") || normalRead) {
				// Gets the integers and checks for "" values
				temp = eAttributes.getAttribute(ParmConstants.xmlElmAlertPk);
				if (temp.length() > 0)
					alertPk = temp;
				else
					alertPk = "-1";

				temp = eAttributes.getAttribute("l_parent");
				if (temp.length() > 0)
					parent = temp;
				else
					parent = "-1";

				temp = eAttributes.getAttribute("l_addresstypes");
				if (temp.length() > 0)
					addresstypes = Integer.parseInt(temp);
				else
					addresstypes = -1;
				name = eAttributes.getAttribute("sz_name");
				description = eAttributes.getAttribute("sz_description");
				oadc = eAttributes.getAttribute("sz_oadc");
				timestamp = eAttributes.getAttribute("l_timestamp");

				AlertVO ao = new AlertVO(alertPk, parent, name, addresstypes/*
																			 * ,
																			 * this.map,
																			 * this.map.getM_navigation()
																			 */);

				if (eAttributes.hasAttribute("sz_operation"))
					ao.setOperation(eAttributes.getAttribute("sz_operation"));

				ao.setDescription(description);
				ao.setTimestamp(timestamp);
				ao.setOadc(oadc);
				temp = eAttributes.getAttribute("l_profilepk");
				if (temp.length() > 0)
					ao.setProfilepk(Integer.parseInt(temp));

				temp = eAttributes.getAttribute("l_schedpk");
				if (temp.length() > 0)
					ao.setSchedpk(temp);

				temp = eAttributes.getAttribute("l_validity");
				if (temp.length() > 0)
					ao.setValidity(Integer.parseInt(temp));

				temp = eAttributes.getAttribute("f_locked");
				if (temp.length() > 0)
					ao.setLocked(Integer.parseInt(temp));

				temp = eAttributes.getAttribute("sz_areaid");
				if (temp.length() > 0)
					ao.setLBAAreaID(temp);

				temp = eAttributes.getAttribute("l_maxchannels");
				if (temp.length() > 0)
					ao.setMaxChannels(Integer.parseInt(temp));
				
				temp = eAttributes.getAttribute("l_requesttype");
				if (temp.length() > 0)
					ao.setRequestType(Integer.parseInt(temp));
				
				temp = eAttributes.getAttribute("l_expiry");
				if(temp.length() > 0)
					ao.setLBAExpiry(Integer.parseInt(temp));
				
				temp = eAttributes.getAttribute("sz_sms_oadc");
				if(temp.length() > 0)
					ao.set_sms_oadc(temp);
				
				temp = eAttributes.getAttribute("sz_sms_message");
				if(temp.length() > 0)
					ao.set_sms_message(temp);
			
				if(polyReader == null)
					polyReader = new XmlPolyReader(main);
				
				NodeList anl = eAttributes.getElementsByTagName(ParmConstants.xmlElmAlertPoly);

				if(anl.getLength()>0) {
					Element el;
					for(int j=0;j<anl.getLength();++j) {
						el = (Element)anl.item(j);
						if(el.hasAttributes())
							set_PolyCoor(ao, el);
					}
				}
				
				NodeList enl = eAttributes.getElementsByTagName(ParmConstants.xmlElmAlertEllipse);

				if(enl.getLength()>0) {
					Element el;
					for(int j=0;j<enl.getLength();++j) {
						el = (Element)enl.item(j);
						if(el.hasAttributes())
							setEllipseCoor(ao, el);
					}
				}
				
				NodeList gnl = eAttributes.getElementsByTagName(ParmConstants.xmlElmAlertstreetid);
				
				if(gnl.getLength()>0) {
					Element el;
					for(int j=0;j<gnl.getLength();++j) {
						el = (Element)gnl.item(j);
						//if(el.hasAttributes())
							set_gislist(ao, el);
					}
				}
				
				// leser LBA
				polyReader.readCellBroadcast(eAttributes, ao);
				
				temp = null;
				
				try
				{
					NodeList nl_operators = eAttributes.getElementsByTagName("lbaoperators");
					if(nl_operators!=null)
					{
						for(int op=0; op < nl_operators.getLength(); op++)
						{
							Node operatorstag = (Node)nl_operators.item(op);
							for(int counter = 0; counter < operatorstag.getChildNodes().getLength(); counter++)
							{
								//Element operator = (Element)operatorstag.getChildNodes().item(counter);
								Node operator = operatorstag.getChildNodes().item(counter);
								if(operator!=null)
								{
									NamedNodeMap map = operator.getAttributes();
									if(map!=null)
									{
										LBAOperator o = ao.new LBAOperator(Long.parseLong(alertPk.substring(1)), Integer.parseInt(map.getNamedItem("l_operator").getNodeValue()), Integer.parseInt(map.getNamedItem("l_status").getNodeValue()), map.getNamedItem("sz_operatorname").getNodeValue(), Long.parseLong(map.getNamedItem("l_areaid").getNodeValue()), "");
										ao.UpdateLbaOperator(o);
									}
								}
							}
						}
					}
					/*NodeList nl_operators = eAttributes.getElementsByTagName("lbaoperators");
					if(nl_operators!=null)
					{
						for(int op=0; op < nl_operators.getLength(); op++)
						{
							Element operator = (Element)nl_operators.item(op);
							if(operator.getNodeName().equals("operator"))
							{
								
							}
						}
					}*/
					
				}
				catch(Exception e)
				{
					Error.getError().addError(PAS.l("common_error"), "Error parsing LBA operator details", e, Error.SEVERITY_ERROR);
				}
				
				
				if (eAttributes.hasAttribute("sz_operation") && normalRead) {
					if (eAttributes.getAttribute("sz_operation").compareTo(
							"delete") == 0) {

					} else
						arrayAlerts.add(ao);
				} else
					arrayAlerts.add(ao);
			}
		}
		this.listOfObjects = null;
	}
	// Moved from polyreader
	private void set_PolyCoor(ParmVO o, Element elPolyObject) {
		int col_r, col_g, col_b, col_a;
		String xCor = "";
		String yCor = "";

		PolygonStruct shape = new PolygonStruct(main.getMapSize());
		
		if (elPolyObject.hasAttribute("col_r")
		 && elPolyObject.hasAttribute("col_g")
		 && elPolyObject.hasAttribute("col_b")
		 && elPolyObject.hasAttribute("col_a")) {
		
			col_r = Integer.parseInt(elPolyObject.getAttribute("col_r"));
			col_g = Integer.parseInt(elPolyObject.getAttribute("col_g"));
			col_b = Integer.parseInt(elPolyObject.getAttribute("col_b"));
			col_a = Integer.parseInt(elPolyObject.getAttribute("col_a"));
			Color col = new Color(col_r, col_g, col_b, col_a);
			// Goes through every object in objectlist and adds
			// polycoordinates
			shape.set_fill_color(col);
		}
		//Element el = (Element)node;
		NodeList listOfPolyPoints = elPolyObject.getChildNodes();
		
		for (int b = 0; b < listOfPolyPoints.getLength(); b++) {
			Node nPoint = listOfPolyPoints.item(b);
			if (nPoint.getNodeType() == Node.ELEMENT_NODE) {
				Element elPolyPoint = (Element) nPoint;
				xCor = elPolyPoint.getAttribute("xcord");
				yCor = elPolyPoint.getAttribute("ycord");
				shape.add_coor(new Double(Double.parseDouble(xCor)), new Double(Double.parseDouble(yCor)));
			}
		}
		if(listOfPolyPoints.getLength()>0)
			o.setM_shape(shape);
	}
	
	// Moved from ellipsereader
	private void setEllipseCoor(ParmVO o, Element elPolyObject) {
		int col_r, col_g, col_b, col_a;
		double centerx;
		double centery;
		double cornerx;
		double cornery;
		
		EllipseStruct shape = new EllipseStruct();
		
		if (elPolyObject.hasAttribute("col_r")
		 && elPolyObject.hasAttribute("col_g")
		 && elPolyObject.hasAttribute("col_b")
		 && elPolyObject.hasAttribute("col_a")) {
		
			col_r = Integer.parseInt(elPolyObject.getAttribute("col_r"));
			col_g = Integer.parseInt(elPolyObject.getAttribute("col_g"));
			col_b = Integer.parseInt(elPolyObject.getAttribute("col_b"));
			col_a = Integer.parseInt(elPolyObject.getAttribute("col_a"));
			Color col = new Color(col_r, col_g, col_b, col_a);
			// Goes through every object in objectlist and adds
			// polycoordinates
			shape.set_fill_color(col);
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
			
			shape.set_ellipse_center(main.getMapNavigation(), new MapPoint(main.getMapNavigation(),new MapPointLL(centerx,centery)));
			shape.set_ellipse_corner(main.getMapNavigation(), new MapPoint(main.getMapNavigation(),new MapPointLL(cornerx,cornery)));
			
			o.setM_shape(shape);
		} catch(Exception e) {
			Error.getError().addError("XmlEllipseReader","Error getting coordinates for ellipse", e, Error.SEVERITY_ERROR);
		}
	}

	// Moved from GISReader
	private void set_gislist(AlertVO alert, Element gis) {
		GISList gislist = new GISList();
		NodeList nl = gis.getChildNodes();
		for (int b = 0; b < nl.getLength(); b++) {
			Node n = nl.item(b);
			if (n.getNodeType() == Node.ELEMENT_NODE) {
				Element elgis = (Element) n;
				GISRecord gisr = new GISRecord(elgis.getAttribute("municipal"),
						elgis.getAttribute("streetid"),
						elgis.getAttribute("houseno"),
						elgis.getAttribute("letter"),
						elgis.getAttribute("namefilter1"),
						elgis.getAttribute("namefilter2"));
				gislist.add(gisr);
			}
		}
		alert.setM_shape(new GISShape(gislist));
	}
	
	private void paCategory() {
		arrayCategories = new ArrayList<Object>();

		if (this.listOfObjects == null)
			this.listOfObjects = doc.getElementsByTagName(ParmConstants.xmlElmCat);

		for (int i = 0; i < this.listOfObjects.getLength(); i++) {
			String categoryPk;
			String name, description, fileext, timestamp;
			String temp;

			Node categories = this.listOfObjects.item(i);
			Element eAttributes = (Element) categories;
			temp = eAttributes.getAttribute(ParmConstants.xmlElmCatPk);
			if (temp.length() > 0)
				categoryPk = temp;
			else
				categoryPk = "-1";
			name = eAttributes.getAttribute("sz_name");
			description = eAttributes.getAttribute("sz_description");
			fileext = eAttributes.getAttribute("sz_fileext");
			timestamp = eAttributes.getAttribute("l_timestamp");

			temp = null;
			// Put category object in collection
			arrayCategories.add(new CategoryVO(categoryPk, name, description,
					fileext, timestamp));
		}

	}

	private void sortArrays() {
		// Denne metoden sorterer alle listene slik at alt blir lagt tilhørende
		// de rette objekter.
		
		Iterator it;
		// Put the alerts into the right events
		if (arrayEvents != null && arrayAlerts != null) {
			it = arrayEvents.iterator();
			while (it.hasNext()) {
				EventVO eo = (EventVO) it.next();
				Iterator italert = arrayAlerts.iterator();
				while (italert.hasNext()) {
					AlertVO ao = (AlertVO) italert.next();
					if (ao.getParent().compareTo(eo.getEventPk()) == 0)
						eo.addAlerts(ao);
				}
			}
		}
		// Put the categories into the right event
		if (arrayEvents != null) {
			it = arrayEvents.iterator();
			while (it.hasNext()) {
				EventVO eo = (EventVO) it.next();
				Iterator itCat = arrayCategories.iterator();
				while (itCat.hasNext()) {
					CategoryVO co = (CategoryVO) itCat.next();
					if (eo.getCategorypk().compareTo(co.getCategoryPK()) == 0)
						eo.setCatVO(co);
				}
			}
		}
		// Put events into the right object
		it = arrayObjects.iterator();
		while (it.hasNext()) {
			ObjectVO oo = (ObjectVO) it.next();
			Iterator itEvent = arrayEvents.iterator();
			while (itEvent.hasNext()) {
				EventVO eo = (EventVO) itEvent.next();
				if (eo.getParentpk().compareTo(oo.getObjectPK()) == 0)
					oo.addEvents(eo);
			}
		}
		// Put the categories into the right object
		it = arrayObjects.iterator();
		while (it.hasNext()) {
			ObjectVO oo = (ObjectVO) it.next();
			Iterator itCat = arrayCategories.iterator();
			while (itCat.hasNext()) {
				CategoryVO co = (CategoryVO) itCat.next();
				if (oo.getCategoryPK().compareTo(co.getCategoryPK()) == 0)
					oo.setCategoryVO(co);
			}
		}
		// Put the objects into the right objects
		it = arrayObjects.iterator();
		while (it.hasNext()) {
			ObjectVO oo = (ObjectVO) it.next();
			Iterator itObj = arrayObjects.iterator();
			while (itObj.hasNext()) {
				ObjectVO temp = (ObjectVO) itObj.next();
				if (oo.getObjectPK().compareTo(temp.getParent()) == 0)
					oo.addObjects(temp);
			}
		}
		int size = arrayObjects.size() - 1;
		for (int i = size; i >= 0; i--) {
			ObjectVO temp = (ObjectVO) arrayObjects.get(i);
			if (temp.getParent().compareTo("o-1") != 0)
				arrayObjects.remove(i);
		}
	}
	private void sortListAscending(ArrayList<Object> liste){
		ListComparator comp = new ListComparator();
		Collections.sort(liste,comp);
	}
	
	private void writeXmlFromStream(InputStream in, String outPutFileName) throws ParmException {
		try {
			int len;
			byte[] buffer = new byte[1024];
				OutputStream out = new BufferedOutputStream(new FileOutputStream(outPutFileName));
				while ((len = in.read(buffer)) >= 0)
					out.write(buffer, 0, len);

				//in.close();
				out.close();
		} catch (IOException io) {
			io.printStackTrace();
			Error.getError().addError("XmlReader","Exception in writeXmlFromStream",io,1);
			throw new ParmException("XmlReader:There has occured a stream error;");
		}
	}
	
	public Document getXmlDocument(String filepath) {
		try {
			if(filepath == null)
				return null;
			else {
				File xmlfile = new File(filepath);
				if(!xmlfile.exists())
					return null;
				else {
					xmlStream = new FileInputStream(xmlfile);
					parseXmlDocument();
				}
			}
		} catch(Exception e) {
			Error.getError().addError("XmlReader","Error in getXmlDocument",e,Error.SEVERITY_ERROR);
		}
		return doc;
	}

	private void getXmlFromZip(String filename, String zipname) throws ParmException {
		// Get the xml file specified by filename from parmxml zip file
		File xmlfile = new File(zipname);
		boolean b_use_gzip = false;
		if (xmlfile.exists()) {
			try {
				try
				{
					GZIPInputStream gzip = new GZIPInputStream(new FileInputStream(xmlfile));
					xmlStream = gzip;
					b_use_gzip = true;
				}
				catch(Exception e)
				{
					
				}
				if(!b_use_gzip)
				{
					ZipFile zipfile = new ZipFile(xmlfile);
					Enumeration en = zipfile.entries();
					while (en.hasMoreElements()) {
						ZipEntry entry = (ZipEntry) en.nextElement();
						String navn = entry.getName();
						navn = navn.substring(0,8);
						if(filename.compareTo(ParmConstants.parmXmlName)== 0 && navn.compareTo("parmroot")==0){
							xmlStream = zipfile.getInputStream(entry);
							break;
						}
						else if(filename.compareTo(ParmConstants.polyXmlName)== 0 && navn.compareTo("parmpoly")==0){
							xmlStream = zipfile.getInputStream(entry);
							break;
						}
						else if (entry.getName().compareTo(filename) == 0){
							xmlStream = zipfile.getInputStream(entry);
							break;
						}
					}
				}
				
			/*} catch (ZipException ze) {
				Error.getError().addError("XmlReader","ZipException in getXmlFromZip",ze,1);
				throw new ParmException("XmlReader:Error while reading from zip file;");*/
			} catch (IOException io) {
				Error.getError().addError("XmlReader","IOException in getXmlFromZip",io,1);
				resetXMLFile();
			} finally {
				try {
					if (zip != null) {
						zip.close();
					}
				} catch (IOException ioe) {
					Error.getError().addError("XmlReader","IOException in getXmlFromZip",ioe,1);
					xmlfile.delete(); // If error delete the file
					System.out.println("Error while closing filestream: " + ioe.getMessage());
				}
				//PAS.get_pas().close_parm(false);
			}
		} else {
			this.writer = new XmlWriter();
			this.writer.writeXml(null);
		}
	}
	
	public void resetXMLFile() {
		try {
			if(writer == null)
				writer = new XmlWriter();
			
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document xmlDoc = db.newDocument();
			
			Element rootnd = (Element) xmlDoc.createElement("parmroot");
			rootnd.setAttribute("l_timestamp","0");
			xmlDoc.appendChild(rootnd);
			writer.writeXMLFile(xmlDoc, ParmConstants.homePath + ParmConstants.parmXmlName);
			
			
			xmlDoc = db.newDocument();
			rootnd = (Element) xmlDoc.createElement("polyroot");
			xmlDoc.appendChild(rootnd);
			writer.writeXMLFile(xmlDoc, ParmConstants.homePath + ParmConstants.polyXmlName);
			
			writer.zipXmlFile(new String[] { ParmConstants.xmlLocation, ParmConstants.polyxmlLocation});
		} catch (Exception e) {
			Error.getError().addError("XmlReader","Exception writing clean parmxml",e,1);
		}
	}

	private void parseXmlDocument() throws ParmException {
		try {
			docBuilderFactory = DocumentBuilderFactory.newInstance();
			docBuilder = docBuilderFactory.newDocumentBuilder();
			doc = docBuilder.parse(xmlStream);
		} catch (FactoryConfigurationError fce) {
			Error.getError().addError("XmlReader","FactoryConfigurationError in parseXmlDocument: " + fce.getMessage(),2,1);
			throw new ParmException("XmlReader:Implementation of the factory is not available or could not be instantiated;");
		} catch (ParserConfigurationException pce) {
			Error.getError().addError("XmlReader","ParceConfigurationException in parseXmlDocument",pce,1);
			throw new ParmException("XmlReader:Documentbuilder could not be created;");
		} catch (SAXException saxe) {
			//Error.getError().addError("XmlReader","SAXException in parseXmlDocument",saxe,1);
			saxe.printStackTrace();
			// Dette retter opp dersom xmlfilen blir korrupt
			resetXMLFile();
			//throw new ParmException("XmlReader:The xml document is not in a valid format;");
		} catch (IOException ioe) {
			Error.getError().addError("XmlReader","IOException in parseXmlDocument",ioe,1);
			throw new ParmException("XmlReader:There has occured a stream error;");
		}

	}

	private void resetXmlStream() {
		if (xmlStream != null) {
			try {
				xmlStream.close();
			} catch (IOException io) {
				System.out.println("XmlReader-->updateMemObjects()"	+ io.getMessage());
				Error.getError().addError("XmlReader","IOException in resetXmlStream",io,1);
			}
			doc = null;
			docBuilder = null;
			docBuilderFactory = null;
		}
	}
	
	public void loadSettings(){
		MailAccount account = PAS.get_pas().get_userinfo().get_mailaccount();
		Settings settings = PAS.get_pas().get_settings();
		
		Document xmlDoc = getXML(StorageController.StorageElements.get_path(StorageController.PATH_HOME_) + "settings.ini");
		
		if(xmlDoc != null) {
			NodeList nl = xmlDoc.getElementsByTagName("account");
			
			if(nl.getLength() > 0) {
				Element element = (Element)nl.item(0);
				nl=element.getElementsByTagName("name");
				
				if(nl.getLength() > 0) {
					Element child = (Element)nl.item(0);
					account.set_displayname(child.getTextContent());
					System.out.println(child.getTextContent());
				}
				
				nl=element.getElementsByTagName("email");
				
				if(nl.getLength() > 0) {
					Element child = (Element)nl.item(0);
					account.set_mailaddress(child.getTextContent());
					System.out.println(child.getTextContent());
				}
				
				nl=element.getElementsByTagName("mailserver");
				
				if(nl.getLength() > 0) {
					Element child = (Element)nl.item(0);
					account.set_mailserver(child.getTextContent());
					System.out.println(child.getTextContent());
				}
				
				nl=element.getElementsByTagName("port");
				
				if(nl.getLength() > 0) {
					Element child = (Element)nl.item(0);
					account.set_port(Integer.parseInt(child.getTextContent()));
					System.out.println(child.getTextContent());
				}
				
				nl=element.getElementsByTagName("autodetected");
				
				if(nl.getLength() > 0) {
					Element child = (Element)nl.item(0);
					account.set_autodetected(Boolean.getBoolean(child.getTextContent()));
					System.out.println(child.getTextContent());
				}
					
			}
			
			nl = xmlDoc.getElementsByTagName("lbarefresh");
			
			if(nl.getLength() > 0) {
				Element child = (Element)nl.item(0);
				settings.setLbaRefresh(Integer.valueOf(child.getTextContent()));
				System.out.println(child.getTextContent());
			}
			
			
		}
		
	}
	
	public Settings loadMapSetup(Settings settings)
	{
		if(settings == null)
			settings = new Settings();
		Document xmlDoc = getXML(StorageController.StorageElements.get_path(StorageController.PATH_HOME_) + "settings.ini");
		
		if(xmlDoc != null)
		{
			NodeList nl = xmlDoc.getElementsByTagName("Mapsettings");
			
			if(nl.getLength() > 0) {
				Element element = (Element)nl.item(0);
				nl = element.getElementsByTagName("MapServer");
				Element child;
				if(nl.getLength() > 0) {
					child = (Element)nl.item(0);
					settings.setMapServer(MAPSERVER.valueOf(child.getTextContent()));
				}
				
				nl = element.getElementsByTagName("panbydrag");
				if(nl.getLength() > 0)
				{
					child = (Element)nl.item(0);
					settings.setPanByDrag(new Boolean(child.getTextContent()));
				}
				
				String wmsname = "", wmsurl = "", wmsformat = "", layers = "";
				nl = element.getElementsByTagName("WmsSite");
				if(nl.getLength() > 0) {
					child = (Element)nl.item(0);
					NodeList nlName = child.getElementsByTagName("wmsname");
					if(nlName.getLength() > 0) {
						child = (Element)nlName.item(0);
						wmsname = child.getTextContent();
					}
					nlName = element.getElementsByTagName("wmsurl");
					if(nlName.getLength() > 0) {
						child = (Element)nlName.item(0);
						wmsurl = child.getTextContent();
					}
					settings.setWmsSite(wmsurl);
					
					nlName = element.getElementsByTagName("wmsformat");
					if(nlName.getLength() > 0) {
						child = (Element)nlName.item(0);
						wmsformat = child.getTextContent();
					} else
						wmsformat = "image/png";
					settings.setSelectedWmsFormat(wmsformat);
					//showlayers
					nlName = element.getElementsByTagName("showlayers");
					if(nlName.getLength() > 0) {
						child = (Element)nlName.item(0);
						layers = child.getTextContent();
					}
					settings.setSelectedWmsLayers(layers);
				}
			}
		}
		return settings;
	}
	
	public Settings loadVisualsSettings(Settings settings)
	{
		if(settings == null)
			settings = new Settings();
		Document xmlDoc = getXML(StorageController.StorageElements.get_path(StorageController.PATH_HOME_) + "settings.ini");
		
		if(xmlDoc != null) {
			NodeList nl = xmlDoc.getElementsByTagName("Visuals");
			
			if(nl.getLength() > 0) {
				Element element = (Element)nl.item(0);
				nl=element.getElementsByTagName("ButtonShaper");
				Element child;
				
				if(nl.getLength() > 0) {
					child = (Element)nl.item(0);
					settings.setButtonShaperClassName(child.getTextContent());
				}
				
				nl=element.getElementsByTagName("GradientPainter");
				
				if(nl.getLength() > 0) {
					child = (Element)nl.item(0);
					settings.setGradientClassname(child.getTextContent());
				}

				nl=element.getElementsByTagName("Theme");
				if(nl.getLength() > 0) {
					child = (Element)nl.item(0);
					settings.setThemeClassName(child.getTextContent());
				}

				nl=element.getElementsByTagName("TitlePainter");
				if(nl.getLength() > 0) {
					child = (Element)nl.item(0);
					settings.setTitlePainterClassname(child.getTextContent());
				}

				nl=element.getElementsByTagName("WaterMark");
				if(nl.getLength() > 0) {
					child = (Element)nl.item(0);
					settings.setWatermarkClassName(child.getTextContent());
				}
			}
		}
		return settings;
	}
	
	public Settings loadLogonSettings(Settings settings){
		if(settings == null)
			settings = new Settings();
		
		try
		{
			Document xmlDoc = getXML(StorageController.StorageElements.get_path(StorageController.PATH_HOME_) + "settings.ini");
			
			if(xmlDoc != null) {
				NodeList nl = xmlDoc.getElementsByTagName("defaultuser");
				
				if(nl.getLength() > 0) {
					Element element = (Element)nl.item(0);
					nl=element.getElementsByTagName("username");
					Element child;
					
					if(nl.getLength() > 0) {
						child = (Element)nl.item(0);
						settings.setUsername(child.getTextContent());
					}
					
					nl=element.getElementsByTagName("company");
					
					if(nl.getLength() > 0) {
						child = (Element)nl.item(0);
						settings.setCompany(child.getTextContent());
					}
					
					nl=element.getElementsByTagName("language");
					if(nl.getLength() > 0) {
						child = (Element)nl.item(0);
						settings.setLanguage(child.getTextContent());
					}
				}
			}
		}
		catch(Exception e)
		{
			
		}
		return settings;
	}
	public Settings loadAutostartSettings(Settings settings){
		if(settings == null)
			settings = new Settings();
		
		Document xmlDoc = getXML(StorageController.StorageElements.get_path(StorageController.PATH_HOME_) + "settings.ini");
		
		if(xmlDoc != null) {
			NodeList nl = xmlDoc.getElementsByTagName("autostart");
			
			if(nl.getLength() > 0) {
				Element element = (Element)nl.item(0);
				nl=element.getElementsByTagName("parm");
				Element child;
				
				if(nl.getLength() > 0) {
					child = (Element)nl.item(0);
					settings.setParm(Boolean.parseBoolean(child.getTextContent()));
				}
				
				nl=element.getElementsByTagName("fleetcontrol");
				
				if(nl.getLength() > 0) {
					child = (Element)nl.item(0);
					settings.setFleetcontrol(Boolean.parseBoolean(child.getTextContent()));
				}
			}
		}
		return settings;
	}
	public Settings loadScreenSize(Settings settings){
		if(settings == null)
			settings = new Settings();
		
		Document xmlDoc = getXML(StorageController.StorageElements.get_path(StorageController.PATH_HOME_) + "settings.ini");
		
		if(xmlDoc != null) {
			NodeList nl = xmlDoc.getElementsByTagName("windowsize");
			
			if(nl.getLength() > 0) {
				Element element = (Element)nl.item(0);
				String tagname = "ss" + String.valueOf(Toolkit.getDefaultToolkit().getScreenSize().width) + "x"
				+ String.valueOf(Toolkit.getDefaultToolkit().getScreenSize().height);
				nl = element.getElementsByTagName(tagname);
				if(nl.getLength() > 0) {
					Element child = (Element)nl.item(0);
					settings.setWindowFullscreen(Boolean.parseBoolean(child.getAttribute("fullscreen")));
					settings.setWindowHeight(Integer.parseInt(child.getAttribute("height")));
					settings.setWindowWidth(Integer.parseInt(child.getAttribute("width")));
					settings.setXpos(Integer.parseInt(child.getAttribute("xpos")));
					settings.setYpos(Integer.parseInt(child.getAttribute("ypos")));
				}
			}
			
			nl = xmlDoc.getElementsByTagName("messagelibdlg");
			if(nl.getLength() > 0) {
				Rectangle rect = new Rectangle(0,0,0,0);
				try
				{
					Element element = (Element)nl.item(0);
					rect.x = new Integer(element.getAttribute("x"));
					rect.y = new Integer(element.getAttribute("y"));
					rect.width = new Integer(element.getAttribute("w"));
					rect.height = new Integer(element.getAttribute("h"));
					if(!rect.isEmpty())
					{
						settings.setRectMessageLibDlg(rect);
					}
				}
				catch(Exception e)
				{
					
				}
			}
			
			nl = xmlDoc.getElementsByTagName("messagelibdlg_exploded_nodes");
			List<Object> exploded = new ArrayList<Object>();
			if(nl.getLength() > 0) {
				Element element = (Element)nl.item(0);
				NodeList explode = element.getElementsByTagName("node");
				for(int i=0; i < explode.getLength(); i++)
				{
					Element n = (Element)explode.item(i);
					Object pk = n.getAttribute("pk");
					exploded.add(pk);
				}
			}
			PAS.get_pas().get_settings().setMessageLibExplodedNodes(exploded);
		}
		return settings;
	}
	
	private Document getXML(String filepath) {
		try {
			File file = new File(filepath);
			
			if(file.exists())
				xmlStream = new FileInputStream(file);
			else
				return null;
			
			parseXmlDocument();
		}
		catch(Exception e){
			Error.getError().addError("XmlReader","Exception in getXML", e, 1);
			e.printStackTrace();
		}
		finally {
		}
		return doc;
	}
	
	public void readNodesExpandFromFile(JTree tree, ParmController parmCtrl) {
		try {
			File file = new File(ParmConstants.homePath + "tree.cfg");
						
			if(file.exists()) {
				FileReader fr = new FileReader(file);
				BufferedReader br = new BufferedReader(fr);
		
		        String line = ""; 
		        String temp = br.readLine();
		        
		        while(temp!=null) {
		        	line = line + temp;
		        	temp = br.readLine();
		        }
		        fr.close();
		        
		        String[] pk = line.split(",",0);
		        
		        for(int i=0;i<pk.length;i++) {
		        	DefaultMutableTreeNode node = parmCtrl.findNodeByPk(pk[i]); 	
		        	tree.expandPath(getTreepath(node));
		        }
	        }
		} catch(Exception e) {
			System.out.println("feil ved lesing");
		}
	}
	
	private TreePath getTreepath(DefaultMutableTreeNode node) {
		return new TreePath(node.getPath());
	}
	
	/*public Collection <Object>readStatusItemsRes(Document xmlDoc) {
		String [] sz_sendattr = { "sz_sendingname", "l_refno", "l_group", "l_createdate", "l_createtime", "l_scheddate", "l_schedtime",
				  "l_sendingstatus", "l_comppk", "l_deptpk", "l_type", "l_addresstypes", "l_profilepk", 
				  "l_queuestatus", "l_totitem", "l_proc", "l_altjmp", "l_alloc", "l_maxalloc" };

		NodeList nl = xmlDoc.getElementsByTagName("updates");
		
		if(nl != null) {
			for(int i=0;i<nl.getLength();i++) {
				StatusItemObject statusItem = new StatusItemObject();
			}
		}
		return null;
	}*/
}
