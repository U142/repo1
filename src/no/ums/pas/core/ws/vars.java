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
	
	public static void init(String sz_base)
	{
		WSDL_CONFERENCE 		= sz_base + "Conference.asmx?WSDL";
		
		WSDL_EXTERNALEXEC 		= sz_base + "ExternalExec.asmx?WSDL";
		//WSDL_EXTERNALEXEC		= "http://localhost/WS/ExternalExec.asmx?WSDL";
		
		WSDL_INFOSENTRAL		= sz_base + "Infosentral.asmx?WSDL";
		
		//WSDL_PARMADMIN 			= sz_base + "ParmAdmin.asmx?WSDL";
		WSDL_PARMADMIN 			= "http://localhost/WS/ParmAdmin.asmx?WSDL";

		WSDL_PAS 				= sz_base + "PAS.asmx?WSDL";
		//WSDL_PAS 				= "http://localhost/WS/PAS.asmx?WSDL";
		
		//WSDL_PASSTATUS			= "http://localhost/WS/PasStatus.asmx?WSDL";
		WSDL_PASSTATUS			= sz_base + "PasStatus.asmx?WSDL";
		
		WSDL_TAS				= sz_base + "TAS.asmx?WSDL";
		//WSDL_TAS				= "http://localhost/WS/TAS.asmx?WSDL";
		WSDL_VB					= sz_base + "VB.asmx?WSDL";
		WSDL_VOICE				= sz_base + "Voice.asmx?WSDL";
	}
}