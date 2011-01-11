
package no.ums.ws.pas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="logon" type="{http://ums.no/ws/pas/}ULOGONINFO"/>
 *         &lt;element name="param" type="{http://ums.no/ws/pas/}ULBAPARAMETER" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "logon",
    "param"
})
@XmlRootElement(name = "updateCBParameters")
public class UpdateCBParameters {

    @XmlElement(required = true)
    protected ULOGONINFO logon;
    protected ULBAPARAMETER param;

    /**
     * Gets the value of the logon property.
     * 
     * @return
     *     possible object is
     *     {@link ULOGONINFO }
     *     
     */
    public ULOGONINFO getLogon() {
        return logon;
    }

    /**
     * Sets the value of the logon property.
     * 
     * @param value
     *     allowed object is
     *     {@link ULOGONINFO }
     *     
     */
    public void setLogon(ULOGONINFO value) {
        this.logon = value;
    }

    /**
     * Gets the value of the param property.
     * 
     * @return
     *     possible object is
     *     {@link ULBAPARAMETER }
     *     
     */
    public ULBAPARAMETER getParam() {
        return param;
    }

    /**
     * Sets the value of the param property.
     * 
     * @param value
     *     allowed object is
     *     {@link ULBAPARAMETER }
     *     
     */
    public void setParam(ULBAPARAMETER value) {
        this.param = value;
    }

}
