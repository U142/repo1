
package no.ums.ws.pas;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for LANGUAGE.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="LANGUAGE">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="NORWEGIAN"/>
 *     &lt;enumeration value="BRITISH"/>
 *     &lt;enumeration value="USENGLISH"/>
 *     &lt;enumeration value="DUTCH"/>
 *     &lt;enumeration value="SWEDISH"/>
 *     &lt;enumeration value="DANISH_POUL"/>
 *     &lt;enumeration value="DANISH_METTE"/>
 *     &lt;enumeration value="GERMAN"/>
 *     &lt;enumeration value="SPANISH"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "LANGUAGE")
@XmlEnum
public enum LANGUAGE {

    NORWEGIAN,
    BRITISH,
    USENGLISH,
    DUTCH,
    SWEDISH,
    DANISH_POUL,
    DANISH_METTE,
    GERMAN,
    SPANISH;

    public String value() {
        return name();
    }

    public static LANGUAGE fromValue(String v) {
        return valueOf(v);
    }

}
