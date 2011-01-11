package no.ums.pas.core;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.util.jar.Manifest;

public class versioning
{
	public static String NAME;
	//public static String MANIFEST_VERSION;
	//public static String ANT_VERSION;
	//public static String CREATED_BY;
	//public static String BUILT_BY;
	//public static String BUILT_DATE;
	
	public static Annotation [] ANNOTATIONS;
	
	public static String IMPLEMENTATION_TITLE;
	public static String IMPLEMENTATION_VERSION;
	public static String IMPLEMENTATION_VENDOR;
	
	public static boolean SEALED;
	
	public static String SPECIFICATION_TITLE;
	public static String SPECIFICATION_VERSION;
	public static String SPECIFICATION_VENDOR;

	public static String BUILT_DATE;
	
	
	
	
	

	
	public static void setVersion()
	{
		Package p = versioning.class.getPackage();
		
		/*InputStream is = versioning.class
		.getResourceAsStream("MANIFEST.MF");
		try
		{
			Manifest mf = new Manifest(is);
			mf.toString();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}*/
		
		
	
		
	
		NAME = p.getName();
		 
		IMPLEMENTATION_TITLE = p.getImplementationTitle();
		IMPLEMENTATION_VERSION = p.getImplementationVersion();
		IMPLEMENTATION_VENDOR = p.getImplementationVendor();
		
		SPECIFICATION_TITLE = p.getSpecificationTitle();
		SPECIFICATION_VERSION = p.getSpecificationVersion();
		SPECIFICATION_VENDOR = p.getSpecificationVendor();
		
		SEALED = p.isSealed();
		
		ANNOTATIONS = p.getAnnotations();
		if(ANNOTATIONS!=null)
		{
			for(int i=0; i < ANNOTATIONS.length; i++)
			{
				System.out.println(ANNOTATIONS[i].toString());
				if(ANNOTATIONS[i].toString().indexOf("Built-Date")>=0)
					BUILT_DATE = ANNOTATIONS[i].toString();
			}
		}
	}
}