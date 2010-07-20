
package no.ums.ws.parm.admin;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PASHAPETYPES.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="PASHAPETYPES">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="PAALERT"/>
 *     &lt;enumeration value="PAEVENT"/>
 *     &lt;enumeration value="PAOBJECT"/>
 *     &lt;enumeration value="PAUSERRESTRICTION"/>
 *     &lt;enumeration value="PADEPARTMENTRESTRICTION"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "PASHAPETYPES")
@XmlEnum
public enum PASHAPETYPES {

    PAALERT,
    PAEVENT,
    PAOBJECT,
    PAUSERRESTRICTION,
    PADEPARTMENTRESTRICTION;

    public String value() {
        return name();
    }

    public static PASHAPETYPES fromValue(String v) {
        return valueOf(v);
    }

}
