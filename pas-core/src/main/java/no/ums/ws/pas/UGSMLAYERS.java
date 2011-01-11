
package no.ums.ws.pas;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UGSMLAYERS.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="UGSMLAYERS">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="GSM900"/>
 *     &lt;enumeration value="GSM1800"/>
 *     &lt;enumeration value="ALL"/>
 *     &lt;enumeration value="UMTS"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "UGSMLAYERS")
@XmlEnum
public enum UGSMLAYERS {

    @XmlEnumValue("GSM900")
    GSM_900("GSM900"),
    @XmlEnumValue("GSM1800")
    GSM_1800("GSM1800"),
    ALL("ALL"),
    UMTS("UMTS");
    private final String value;

    UGSMLAYERS(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static UGSMLAYERS fromValue(String v) {
        for (UGSMLAYERS c: UGSMLAYERS.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
