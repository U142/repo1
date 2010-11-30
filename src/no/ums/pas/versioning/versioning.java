package no.ums.pas.versioning;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import no.ums.pas.pluginbase.PasScriptingInterface;



public class versioning
{

	
	public static String NAME;
	
	public static String PLUGIN_NAME;
	
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
	
	
	public static String PLUGIN_IMPLEMENTATION_TITLE;
	public static String PLUGIN_IMPLEMENTATION_VERSION;
	public static String PLUGIN_IMPLEMENTATION_VENDOR;
	
	public static boolean PLUGIN_SEALED;
	
	public static String PLUGIN_SPECIFICATION_TITLE;
	public static String PLUGIN_SPECIFICATION_VERSION;
	public static String PLUGIN_SPECIFICATION_VENDOR;
	
	
	

	
	public void setVersion(PasScriptingInterface plugin)
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
		
		if(plugin!=null)
		{
			Package pPlugin = plugin.getClass().getPackage();
			PLUGIN_NAME = pPlugin.getName();
			PLUGIN_IMPLEMENTATION_TITLE = pPlugin.getImplementationTitle();
			PLUGIN_IMPLEMENTATION_VERSION = pPlugin.getImplementationVersion();
			PLUGIN_IMPLEMENTATION_VENDOR = pPlugin.getImplementationVendor();
			PLUGIN_SPECIFICATION_TITLE = pPlugin.getSpecificationTitle();
			PLUGIN_SPECIFICATION_VERSION = pPlugin.getSpecificationVersion();
			PLUGIN_SPECIFICATION_VENDOR = pPlugin.getSpecificationVendor();
			PLUGIN_SEALED = pPlugin.isSealed();
		}
		/*if(ANNOTATIONS!=null)
		{
			for(int i=0; i < ANNOTATIONS.length; i++)
			{
				System.out.println(ANNOTATIONS[i].toString());
				if(ANNOTATIONS[i].toString().indexOf("Built-Date")>=0)
					BUILT_DATE = ANNOTATIONS[i].toString();
			}
		}*/
		try
		{
			/*InputStream manifestStream = versioning.class.getResourceAsStream("/META-INF/MANIFEST.MF");
			if(manifestStream!=null)
			{
				Manifest manifest = new Manifest(manifestStream);
				Attributes attr = manifest.getMainAttributes();
				Set<Entry<Object,Object>> set = attr.entrySet();
				Iterator<Entry<Object,Object>> it = set.iterator();
				while(it.hasNext())
				{
					Entry<Object,Object> a = it.next();
					System.out.println("Key="+a.getKey() + " value="+a.getValue());
				}
			}*/
		}
		catch(Exception e)
		{
			//e.printStackTrace();
		}
		
	}
}