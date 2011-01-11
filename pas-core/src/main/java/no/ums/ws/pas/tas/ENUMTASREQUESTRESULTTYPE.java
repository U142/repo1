
package no.ums.ws.pas.tas;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ENUM_TASREQUESTRESULTTYPE.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ENUM_TASREQUESTRESULTTYPE">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="COUNTREQUEST"/>
 *     &lt;enumeration value="SENDING"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ENUM_TASREQUESTRESULTTYPE")
@XmlEnum
public enum ENUMTASREQUESTRESULTTYPE {

    COUNTREQUEST,
    SENDING;

    public String value() {
        return name();
    }

    public static ENUMTASREQUESTRESULTTYPE fromValue(String v) {
        return valueOf(v);
    }

}
