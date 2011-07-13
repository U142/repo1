package no.ums.pas.pluginbase;


import no.ums.log.Log;
import no.ums.log.UmsLog;

public class PluginLoader
{
    private static final Log log = UmsLog.getLogger(PluginLoader.class);

	public enum FILETYPE
	{
		CLASS,
		JAR,
	}
	public static AbstractPasScriptingInterface loadPlugin(String host, String classname, FILETYPE f)
		throws Exception
	{
		try
		{
			AbstractPasScriptingInterface o = new NetworkClassLoader(host, classname, f).loadClass().newInstance();
			return o;
		}
		catch(Exception e)
		{
			log.warn(e.getMessage(), e);
			throw e;
		}
	}
	public static boolean LoadExternalJar(String host, String jarfile, String classname)
		throws Exception
	{
		try
		{
			new NetworkClassLoader(host, jarfile, classname).loadClass();
			return true;
		}
		catch(Exception e)
		{
			log.warn(e.getMessage(), e);
			throw e;
		}
	}
}