package no.ums.pas.pluginbase;


public class PluginLoader
{
	public enum FILETYPE
	{
		CLASS,
		JAR,
	}
	public static PasScriptingInterface loadPlugin(String host, String classname, FILETYPE f)
		throws Exception
	{
		try
		{
			PasScriptingInterface o = new NetworkClassLoader(host, classname, f).loadClass().newInstance();
			return o;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw e;
		}
	}
	public static boolean LoadExternalJar(String host, String jarname)
		throws Exception
	{
		try
		{
			new NetworkClassLoader(host, jarname, FILETYPE.JAR);
			return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw e;
		}
	}
}