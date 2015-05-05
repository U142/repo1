package no.ums.pas.core.ws;

public class vars
{
	public static String WSDL_CONFERENCE = "";
	public static String WSDL_EXTERNALEXEC = "";
	public static String WSDL_INFOSENTRAL = "";
	public static String WSDL_PARMADMIN = "";
	public static String WSDL_PAS = "";
	public static String WSDL_PASSTATUS = "";
	public static String WSDL_TAS = "";
	public static String WSDL_VB = "";
	public static String WSDL_VOICE = "";
	public static String WSDL_ADDRESSFILTERS = "";
	
	public static void init(String sz_base)
	{
		/**
		 * ULOVLIG Å ENDRE TIL LOCALHOST
		 */
		WSDL_CONFERENCE 		= "http://localhost:8080/WS/" + "Conference.asmx?WSDL";
		WSDL_EXTERNALEXEC 		= "http://localhost:8080/WS/" + "ExternalExec.asmx?WSDL";
		WSDL_INFOSENTRAL		= "http://localhost:8080/WS/" + "Infosentral.asmx?WSDL";
		WSDL_PARMADMIN 			= "http://localhost:8080/WS/" + "ParmAdmin.asmx?WSDL";
		WSDL_PAS 				= "http://localhost:8080/WS/" + "PAS.asmx?WSDL";
		WSDL_PASSTATUS			= "http://localhost:8080/WS/" + "PasStatus.asmx?WSDL";
		WSDL_TAS				= "http://localhost:8080/WS/" + "TAS.asmx?WSDL";
		WSDL_VB					= "http://localhost:8080/WS/" + "VB.asmx?WSDL";
		WSDL_VOICE				= "http://localhost:8080/WS/" + "Voice.asmx?WSDL";
		WSDL_ADDRESSFILTERS     = "http://localhost:8080/WS/" + "AddressFilters.asmx?WSDL";
		WSDL_TAS				= "http://localhost:8080/WS/" + "TAS.asmx?WSDL";
		WSDL_VB					= "http://localhost:8080/WS/" + "VB.asmx?WSDL";
		WSDL_VOICE				= "http://localhost:8080/WS/" + "Voice.asmx?WSDL";
	}
}
