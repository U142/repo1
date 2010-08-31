
package no.ums.ws.parm;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for MDVSENDINGINFO_GROUP.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="MDVSENDINGINFO_GROUP">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="ADDRESSLIST"/>
 *     &lt;enumeration value="GROUPSENDING"/>
 *     &lt;enumeration value="MAP_POLYGON"/>
 *     &lt;enumeration value="MAP_GEMINI_GNOBNO"/>
 *     &lt;enumeration value="MAP_GEMINI_STREETID"/>
 *     &lt;enumeration value="MAP_ELLIPSE"/>
 *     &lt;enumeration value="MAP_MUNICIPAL"/>
 *     &lt;enumeration value="MAP_LBA_VOICE"/>
 *     &lt;enumeration value="MAP_CB_NATIONAL"/>
 *     &lt;enumeration value="MAP_POLYGONAL_ELLIPSE"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "MDVSENDINGINFO_GROUP")
@XmlEnum
public enum MDVSENDINGINFOGROUP {

    ADDRESSLIST,
    GROUPSENDING,
    MAP_POLYGON,
    MAP_GEMINI_GNOBNO,
    MAP_GEMINI_STREETID,
    MAP_ELLIPSE,
    MAP_MUNICIPAL,
    MAP_LBA_VOICE,
    MAP_CB_NATIONAL,
    MAP_POLYGONAL_ELLIPSE;

    public String value() {
        return name();
    }

    public static MDVSENDINGINFOGROUP fromValue(String v) {
        return valueOf(v);
    }

}
