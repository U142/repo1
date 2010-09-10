
package no.ums.ws.pas;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RESTRICTION_TYPE.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="RESTRICTION_TYPE">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="PAUSERRESTRICTION"/>
 *     &lt;enumeration value="PADEPARTMENTRESTRICTION"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "RESTRICTION_TYPE")
@XmlEnum
public enum RESTRICTIONTYPE {

    PAUSERRESTRICTION,
    PADEPARTMENTRESTRICTION;

    public String value() {
        return name();
    }

    public static RESTRICTIONTYPE fromValue(String v) {
        return valueOf(v);
    }

}
