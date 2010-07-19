
package no.ums.ws.parm;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CB_OPERATION.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="CB_OPERATION">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="NewAlertPolygon"/>
 *     &lt;enumeration value="NewAlertPLNM"/>
 *     &lt;enumeration value="UpdateAlert"/>
 *     &lt;enumeration value="KillAlert"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "CB_OPERATION")
@XmlEnum
public enum CBOPERATION {

    @XmlEnumValue("NewAlertPolygon")
    NEW_ALERT_POLYGON("NewAlertPolygon"),
    @XmlEnumValue("NewAlertPLNM")
    NEW_ALERT_PLNM("NewAlertPLNM"),
    @XmlEnumValue("UpdateAlert")
    UPDATE_ALERT("UpdateAlert"),
    @XmlEnumValue("KillAlert")
    KILL_ALERT("KillAlert");
    private final String value;

    CBOPERATION(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static CBOPERATION fromValue(String v) {
        for (CBOPERATION c: CBOPERATION.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
