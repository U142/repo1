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
	
	
	
	
	

	
	public void setVersion()
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
		try
		{
			/*InputStream is = versioning.class.getResourceAsStream("/META-INF/MANIFEST.MF");
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String s;
			while((s = br.readLine())!=null)
			{
				System.out.println(s);
			}*/
			/*Class clazz = versioning.class;
			String className = clazz.getSimpleName();
			String classFileName = className + ".class";
			String pathToClass = clazz.getResource(classFileName).toString();
			int mark = pathToClass.indexOf("/");
			String pathToManifest = pathToClass.toString().substring(0,mark+1);*/
			
			
			InputStream manifestStream = this.getClass().getResourceAsStream("/META-INF/MANIFEST.MF");
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
			}
			
			/*Class clazz = this.getClass();
			String classContainer = clazz.getProtectionDomain().getCodeSource().getLocation().toString();
			URL manifestURL = new URL("jar:" + classContainer + "/META-INF/MANIFEST.MF");
			Manifest manifest = new Manifest(manifestURL.openStream());
			Attributes attr = manifest.getMainAttributes();
			Set<Entry<Object,Object>> set = attr.entrySet();
			Iterator<Entry<Object,Object>> it = set.iterator();
			while(it.hasNext())
			{
				Entry<Object,Object> a = it.next();
				System.out.println("Key="+a.getKey() + " value="+a.getValue());
			}*/

		}
		catch(Exception e)
		{
			//e.printStackTrace();
		}
		
	}
}