
package no.ums.ws.pas;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for HOUSEEDITOR_OPERATION.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="HOUSEEDITOR_OPERATION">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="INSERT"/>
 *     &lt;enumeration value="UPDATE"/>
 *     &lt;enumeration value="DELETE"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "HOUSEEDITOR_OPERATION")
@XmlEnum
public enum HOUSEEDITOROPERATION {

    INSERT,
    UPDATE,
    DELETE;

    public String value() {
        return name();
    }

    public static HOUSEEDITOROPERATION fromValue(String v) {
        return valueOf(v);
    }

}
