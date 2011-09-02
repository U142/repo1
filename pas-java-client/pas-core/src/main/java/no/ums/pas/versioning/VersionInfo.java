package no.ums.pas.versioning;

import com.google.common.io.Resources;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;


public enum VersionInfo {

    INSTANCE;

    public final String revisionNumber;
    public final String buildNumber;
    public final String buildUser;
    public final String buildVersion;
    public final String buildTimestamp;

    private VersionInfo() {
        Properties props = loadProperties();

        revisionNumber = props.getProperty("revision.number", "").substring(0, 8);
        buildNumber = props.getProperty("build.number", "");
        buildUser = props.getProperty("build.user", "");
        buildVersion = props.getProperty("build.version", "");
        buildTimestamp = props.getProperty("build.timestamp", "0");
    }

    Properties loadProperties() {
        Properties props = new Properties();
    	try
    	{
	        URL resource = Resources.getResource("build.properties");
	        if (resource != null) {
	            try {
	                InputStream propsStream = resource.openStream();
	                props.load(propsStream);
	                propsStream.close();
	            } catch (IOException e) {
	                throw new IllegalStateException("Failed to open stream: "+resource);
	            }
	        }
    	}
    	catch(Exception e)
    	{
    		props.put("build.properties", "Development");
    		props.put("revision.number", "00000000");
    		props.put("build.number", "0");
    		props.put("build.user", "Development");
    		props.put("build.version", "0");
    		props.put("build.timestamp", "0");
    	}
    	finally
    	{
    		return props;
    	}
    }

}