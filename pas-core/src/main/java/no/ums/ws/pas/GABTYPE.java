
package no.ums.ws.pas;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GABTYPE.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="GABTYPE">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="House"/>
 *     &lt;enumeration value="Post"/>
 *     &lt;enumeration value="Street"/>
 *     &lt;enumeration value="Region"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "GABTYPE")
@XmlEnum
public enum GABTYPE {

    @XmlEnumValue("House")
    HOUSE("House"),
    @XmlEnumValue("Post")
    POST("Post"),
    @XmlEnumValue("Street")
    STREET("Street"),
    @XmlEnumValue("Region")
    REGION("Region");
    private final String value;

    GABTYPE(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static GABTYPE fromValue(String v) {
        for (GABTYPE c: GABTYPE.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
