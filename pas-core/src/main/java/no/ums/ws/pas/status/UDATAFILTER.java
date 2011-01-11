
package no.ums.ws.pas.status;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UDATAFILTER.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="UDATAFILTER">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="NONE"/>
 *     &lt;enumeration value="BY_COMPANY"/>
 *     &lt;enumeration value="BY_DEPARTMENT"/>
 *     &lt;enumeration value="BY_USER"/>
 *     &lt;enumeration value="BY_LIVE"/>
 *     &lt;enumeration value="BY_SIMULATION"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "UDATAFILTER")
@XmlEnum
public enum UDATAFILTER {

    NONE,
    BY_COMPANY,
    BY_DEPARTMENT,
    BY_USER,
    BY_LIVE,
    BY_SIMULATION;

    public String value() {
        return name();
    }

    public static UDATAFILTER fromValue(String v) {
        return valueOf(v);
    }

}
