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
}