package no.ums.pas.pluginbase;


import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.pas.pluginbase.PluginLoader.FILETYPE;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;


public class NetworkClassLoader extends ClassLoader {

    private static final Log log = UmsLog.getLogger(NetworkClassLoader.class);

	String filename;
	String classname;
	String host;
	FILETYPE filetype;
	
	
	public NetworkClassLoader(String host, String classname, FILETYPE f)
	{
		super();
		this.filename = classname + "." + f.name().toLowerCase(); //".class";
		this.classname = classname; //classname.substring(classname.lastIndexOf(".")+1, classname.length());
		this.host = host;
		this.filetype = f;
	}
	
	public NetworkClassLoader(String host, String jarfile, String classname)
	{
		super();
		this.filename = jarfile + "." + FILETYPE.JAR.name().toLowerCase();
		this.classname = classname;
		this.host = host;
		this.filetype = FILETYPE.JAR;
	}
	
	
	public Class<AbstractPasScriptingInterface> loadClass() throws ClassNotFoundException {
		if(filetype==FILETYPE.CLASS)
		{
			try
			{
				return loadClass(classname);
			}
			catch(Exception e)
			{
				throw new ClassNotFoundException(classname);
			}
		}
		else if(filetype==FILETYPE.JAR)
		{
			try
			{
				URL url = new URL(host + "/" + filename);
				log.debug("Loading plugin " + url.toString());
				URLClassLoader loader = new URLClassLoader(new URL [] { url }, this.getClass().getClassLoader());
				Class<AbstractPasScriptingInterface> classToLoad = (Class<AbstractPasScriptingInterface>)Class.forName(classname, true, loader);
				return classToLoad;
			}
			catch(Exception e)
			{
				throw new ClassNotFoundException(classname + " in " + filename);
			}
		}
		throw new ClassNotFoundException(filename);
	}
	@Override
	public Class<AbstractPasScriptingInterface> loadClass(String name) throws ClassNotFoundException {
		return (Class<AbstractPasScriptingInterface>)super.loadClass(name);
	}
	public Class<AbstractPasScriptingInterface> findClass(String name) {
		try
		{
			byte [] b = loadClassData(name);
			return (Class<AbstractPasScriptingInterface>)defineClass(name, b, 0, b.length);
		}
		catch(Exception e)
		{
			log.warn(e.getMessage(), e);
			return null;
		}
	}
	public byte [] loadClassData(String name) {
		try
		{
			ByteArrayOutputStream tmpOut = new ByteArrayOutputStream();
			URL url = new URL(host + "/" + filename);
			InputStream is = url.openStream();
			byte [] b = new byte[1];
			while(true){
				int l = is.read(b);
				if(l==-1)
					break;
				tmpOut.write(b);
			}
			tmpOut.close();
			return tmpOut.toByteArray();
		}
		catch(Exception e)
		{
			return null;
		}
	}
}