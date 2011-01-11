package no.ums.pas.versioning;

import no.ums.pas.pluginbase.PasScriptingInterface;

import java.lang.annotation.Annotation;


public class Versioning {


    public final String NAME;

    public final String PLUGIN_NAME;

    public final Annotation[] ANNOTATIONS;

    public final String IMPLEMENTATION_TITLE;
    public final String IMPLEMENTATION_VERSION;
    public final String IMPLEMENTATION_VENDOR;

    public final boolean SEALED;

    public final String SPECIFICATION_TITLE;
    public final String SPECIFICATION_VERSION;
    public final String SPECIFICATION_VENDOR;


    public final String BUILT_DATE;


    public final String PLUGIN_IMPLEMENTATION_TITLE;
    public final String PLUGIN_IMPLEMENTATION_VERSION;
    public final String PLUGIN_IMPLEMENTATION_VENDOR;

    public final boolean PLUGIN_SEALED;

    public final String PLUGIN_SPECIFICATION_TITLE;
    public final String PLUGIN_SPECIFICATION_VERSION;
    public final String PLUGIN_SPECIFICATION_VENDOR;

    public static Versioning getInstance() {
        return instance;
    }

    private static Versioning instance;

    public static void initVersioning(PasScriptingInterface plugin) {
        instance = new Versioning(plugin);
    }

    private Versioning(PasScriptingInterface plugin) {
        Package p = Versioning.class.getPackage();
        NAME = p.getName();
        IMPLEMENTATION_TITLE = p.getImplementationTitle();
        IMPLEMENTATION_VERSION = p.getImplementationVersion();
        IMPLEMENTATION_VENDOR = p.getImplementationVendor();
        SPECIFICATION_TITLE = p.getSpecificationTitle();
        SPECIFICATION_VERSION = p.getSpecificationVersion();
        SPECIFICATION_VENDOR = p.getSpecificationVendor();
        SEALED = p.isSealed();
        ANNOTATIONS = p.getAnnotations();
        if (plugin != null) {
            Package pPlugin = plugin.getClass().getPackage();
            PLUGIN_NAME = pPlugin.getName();
            PLUGIN_IMPLEMENTATION_TITLE = pPlugin.getImplementationTitle();
            PLUGIN_IMPLEMENTATION_VERSION = pPlugin.getImplementationVersion();
            PLUGIN_IMPLEMENTATION_VENDOR = pPlugin.getImplementationVendor();
            PLUGIN_SPECIFICATION_TITLE = pPlugin.getSpecificationTitle();
            PLUGIN_SPECIFICATION_VERSION = pPlugin.getSpecificationVersion();
            PLUGIN_SPECIFICATION_VENDOR = pPlugin.getSpecificationVendor();
            PLUGIN_SEALED = pPlugin.isSealed();
        } else {
            PLUGIN_NAME = null;
            PLUGIN_IMPLEMENTATION_TITLE = null;
            PLUGIN_IMPLEMENTATION_VERSION = null;
            PLUGIN_IMPLEMENTATION_VENDOR = null;
            PLUGIN_SPECIFICATION_TITLE = null;
            PLUGIN_SPECIFICATION_VERSION = null;
            PLUGIN_SPECIFICATION_VENDOR = null;
            PLUGIN_SEALED = false;
        }
        BUILT_DATE = getBuildDate();
    }

    private String getBuildDate() {
        if (ANNOTATIONS != null) {
            for (Annotation ANNOTATION : ANNOTATIONS) {
                System.out.println("Package annotation: " + ANNOTATION.toString());
                if (ANNOTATION.toString().indexOf("Built-Date") >= 0)
                    return ANNOTATION.toString();
            }
        }
        return "";
    }
}