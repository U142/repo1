package no.ums.pas.parm.constants;

public class ParmConstants {
	public static void init(String sz_sitename, String sz_homepath) {
		aspUrl = sz_sitename + "PAS_objectreg.asp";
		xmlLocation = sz_homepath + "parmxml.zip";
		tempxmlLocation = sz_homepath;
		polyxmlLocation = sz_homepath + "polyxml.zip";
		resZipLocation = sz_homepath + "tempResZip.zip";
		homePath = sz_homepath;
		settingsLocation = sz_homepath + "settings.ini";
		cleanExit = sz_homepath + "clean.exit";
	}
	public static final String fileName = "sz_xml_filename"; 
	public static final String polyFileName = "sz_poly_filename";
	//A parameter name that the server needs to identify that the incoming file is the xml file.
	public static String aspUrl;
	public static String xmlLocation;
	public static String tempxmlLocation;
	public static String polyxmlLocation;
	public static String resZipLocation;
	public static String homePath;
	public static String settingsLocation;
	public static String cleanExit;
	
//	public static String aspUrl = "http://vb4utv/PAS_objectreg.asp"; //The adress for the asp on the UMS server
//	public static String xmlLocation = "D:\\Projects\\Java\\Eclipse Projects\\PAS\\parm 04\\parmxml.zip"; //The local location for the parmxml.zip file
//	public static String tempxmlLocation = "D:\\Projects\\Java\\Eclipse Projects\\PAS\\parm 04\\"; //The local location for the tempxml.zip file
//	public static String polyxmlLocation = "D:\\Projects\\Java\\Eclipse Projects\\PAS\\parm 04\\polyxml.zip";
//	public static String resZipLocation = "D:\\Projects\\Java\\Eclipse Projects\\PAS\\parm 04\\tempResZip.zip";
//	
	public static long updateSequence = 60000; //60000*5; //The number of milliseconds the thread should sleep before runninng update
	
	public static final String parmXmlName = "parmxml.xml";
	public static final String polyXmlName = "polyxml.xml";
	
	//Constants for xmlCategory
	public static final String xmlElmCat = "pacategory";
	public static final String xmlElmCatPk = "l_categorypk";
	
	//Constants for xmlObject
	public static final String xmlElmObject = "paobject";
	public static final String xmlElmObjectPk = "l_objectpk";
	
	//Constants for xmlEvent
	public static final String xmlElmEvent = "paevent";
	public static final String xmlElmEventPk = "l_eventpk";
	
	//Constants for xmlAlert
	public static final String xmlElmAlert = "paalert";
	public static final String xmlElmAlertPk = "l_alertpk";
	
	//Constants for xmlObjectPoly
	public static final String xmlElmObjectPoly = "objectpolygon";
	public static final String xmlElmObjectEllipse = "objectellipse";
	
	//Constants for xmlAlertPoly
	public static final String xmlElmAlertPoly = "alertpolygon";
	public static final String xmlElmAlertEllipse = "alertellipse";
	public static final String xmlElmAlertstreetid = "alertstreetid";
	
}
