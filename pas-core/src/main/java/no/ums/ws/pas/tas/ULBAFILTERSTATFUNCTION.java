
package no.ums.ws.pas.tas;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ULBAFILTER_STAT_FUNCTION.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ULBAFILTER_STAT_FUNCTION">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="STAT_AVERAGE"/>
 *     &lt;enumeration value="STAT_MAX"/>
 *     &lt;enumeration value="STAT_MIN"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ULBAFILTER_STAT_FUNCTION")
@XmlEnum
public enum ULBAFILTERSTATFUNCTION {

    STAT_AVERAGE,
    STAT_MAX,
    STAT_MIN;

    public String value() {
        return name();
    }

    public static ULBAFILTERSTATFUNCTION fromValue(String v) {
        return valueOf(v);
    }

}
