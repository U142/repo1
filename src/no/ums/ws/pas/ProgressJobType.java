
package no.ums.ws.pas;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ProgressJobType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ProgressJobType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="GEMINI_IMPORT_STREETID"/>
 *     &lt;enumeration value="GEMINI_IMPORT_GNRBNR"/>
 *     &lt;enumeration value="STATUS_LIST"/>
 *     &lt;enumeration value="STATUS_ITEMS"/>
 *     &lt;enumeration value="PARM_UPDATE"/>
 *     &lt;enumeration value="HOUSE_DOWNLOAD"/>
 *     &lt;enumeration value="TAS_UPDATE"/>
 *     &lt;enumeration value="PARM_SEND"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ProgressJobType")
@XmlEnum
public enum ProgressJobType {

    GEMINI_IMPORT_STREETID,
    GEMINI_IMPORT_GNRBNR,
    STATUS_LIST,
    STATUS_ITEMS,
    PARM_UPDATE,
    HOUSE_DOWNLOAD,
    TAS_UPDATE,
    PARM_SEND;

    public String value() {
        return name();
    }

    public static ProgressJobType fromValue(String v) {
        return valueOf(v);
    }

}
