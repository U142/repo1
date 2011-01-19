package no.ums.pas.versioning;

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
        URL resource = Thread.currentThread().getContextClassLoader().getResource("build.properties");
        Properties props = new Properties();
        if (resource != null) {
            try {
                InputStream propsStream = resource.openStream();
                props.load(propsStream);
                propsStream.close();
            } catch (IOException e) {
                throw new IllegalStateException("Failed to open stream: "+resource);
            }
        }

        revisionNumber = props.getProperty("revision.number", "");
        buildNumber = props.getProperty("build.number", "");
        buildUser = props.getProperty("build.user", "");
        buildVersion = props.getProperty("build.version", "");
        buildTimestamp = props.getProperty("build.timestamp", "0");
    }

}