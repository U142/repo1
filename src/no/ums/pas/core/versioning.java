package no.ums.pas.core;

import java.lang.annotation.Annotation;

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

	

	public static void setVersion()
	{
		Package p = versioning.class.getPackage();
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
				System.out.println(ANNOTATIONS[i]);
		}
	}
}