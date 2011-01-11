
package no.ums.ws.parm.admin;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PARMOPERATION.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="PARMOPERATION">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="insert"/>
 *     &lt;enumeration value="update"/>
 *     &lt;enumeration value="delete"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "PARMOPERATION")
@XmlEnum
public enum PARMOPERATION {

    @XmlEnumValue("insert")
    INSERT("insert"),
    @XmlEnumValue("update")
    UPDATE("update"),
    @XmlEnumValue("delete")
    DELETE("delete");
    private final String value;

    PARMOPERATION(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static PARMOPERATION fromValue(String v) {
        for (PARMOPERATION c: PARMOPERATION.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
