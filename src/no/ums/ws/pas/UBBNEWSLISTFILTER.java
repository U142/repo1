
package no.ums.ws.pas;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UBBNEWSLIST_FILTER.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="UBBNEWSLIST_FILTER">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="ACTIVE"/>
 *     &lt;enumeration value="IN_BETWEEN_START_END"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "UBBNEWSLIST_FILTER")
@XmlEnum
public enum UBBNEWSLISTFILTER {

    ACTIVE,
    IN_BETWEEN_START_END;

    public String value() {
        return name();
    }

    public static UBBNEWSLISTFILTER fromValue(String v) {
        return valueOf(v);
    }

}
