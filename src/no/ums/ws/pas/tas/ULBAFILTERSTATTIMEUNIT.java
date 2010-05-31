
package no.ums.ws.pas.tas;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ULBAFILTER_STAT_TIMEUNIT.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ULBAFILTER_STAT_TIMEUNIT">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="PER_HOUR"/>
 *     &lt;enumeration value="PER_DAY"/>
 *     &lt;enumeration value="PER_WEEK"/>
 *     &lt;enumeration value="PER_MONTH"/>
 *     &lt;enumeration value="PER_YEAR"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ULBAFILTER_STAT_TIMEUNIT")
@XmlEnum
public enum ULBAFILTERSTATTIMEUNIT {

    PER_HOUR,
    PER_DAY,
    PER_WEEK,
    PER_MONTH,
    PER_YEAR;

    public String value() {
        return name();
    }

    public static ULBAFILTERSTATTIMEUNIT fromValue(String v) {
        return valueOf(v);
    }

}
