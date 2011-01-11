
package no.ums.ws.pas;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UBBMODULEDEF.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="UBBMODULEDEF">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="CALL"/>
 *     &lt;enumeration value="INTRO"/>
 *     &lt;enumeration value="LOGON"/>
 *     &lt;enumeration value="SUBSTANCE"/>
 *     &lt;enumeration value="DIALOGUE"/>
 *     &lt;enumeration value="REPORT"/>
 *     &lt;enumeration value="CONFIRM"/>
 *     &lt;enumeration value="ENDING"/>
 *     &lt;enumeration value="HANGUP"/>
 *     &lt;enumeration value="ERROR"/>
 *     &lt;enumeration value="EXECUTE"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "UBBMODULEDEF")
@XmlEnum
public enum UBBMODULEDEF {

    CALL,
    INTRO,
    LOGON,
    SUBSTANCE,
    DIALOGUE,
    REPORT,
    CONFIRM,
    ENDING,
    HANGUP,
    ERROR,
    EXECUTE;

    public String value() {
        return name();
    }

    public static UBBMODULEDEF fromValue(String v) {
        return valueOf(v);
    }

}
