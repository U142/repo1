
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
 *         &lt;element name="param" type="{http://ums.no/ws/pas/}UMapDistanceParams" minOccurs="0"/>
 *         &lt;element name="logoninfo" type="{http://ums.no/ws/pas/}ULOGONINFO"/>
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
    "param",
    "logoninfo"
})
@XmlRootElement(name = "GetNearestInhabitantsFromPoint")
public class GetNearestInhabitantsFromPoint {

    protected UMapDistanceParams param;
    @XmlElement(required = true)
    protected ULOGONINFO logoninfo;

    /**
     * Gets the value of the param property.
     * 
     * @return
     *     possible object is
     *     {@link UMapDistanceParams }
     *     
     */
    public UMapDistanceParams getParam() {
        return param;
    }

    /**
     * Sets the value of the param property.
     * 
     * @param value
     *     allowed object is
     *     {@link UMapDistanceParams }
     *     
     */
    public void setParam(UMapDistanceParams value) {
        this.param = value;
    }

    /**
     * Gets the value of the logoninfo property.
     * 
     * @return
     *     possible object is
     *     {@link ULOGONINFO }
     *     
     */
    public ULOGONINFO getLogoninfo() {
        return logoninfo;
    }

    /**
     * Sets the value of the logoninfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link ULOGONINFO }
     *     
     */
    public void setLogoninfo(ULOGONINFO value) {
        this.logoninfo = value;
    }

}
