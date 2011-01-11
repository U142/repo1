
package no.ums.ws.pas;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for BBUSER_BLOCK_REASONS.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="BBUSER_BLOCK_REASONS">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="NONE"/>
 *     &lt;enumeration value="REACHED_RETRY_LIMIT"/>
 *     &lt;enumeration value="BLOCKED_BY_ADMIN"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "BBUSER_BLOCK_REASONS")
@XmlEnum
public enum BBUSERBLOCKREASONS {

    NONE,
    REACHED_RETRY_LIMIT,
    BLOCKED_BY_ADMIN;

    public String value() {
        return name();
    }

    public static BBUSERBLOCKREASONS fromValue(String v) {
        return valueOf(v);
    }

}
