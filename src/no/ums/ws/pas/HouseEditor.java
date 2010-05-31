
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
 *         &lt;element name="adr" type="{http://ums.no/ws/pas/}UAddress" minOccurs="0"/>
 *         &lt;element name="logoninfo" type="{http://ums.no/ws/pas/}ULOGONINFO"/>
 *         &lt;element name="operation" type="{http://ums.no/ws/pas/}HOUSEEDITOR_OPERATION"/>
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
    "adr",
    "logoninfo",
    "operation"
})
@XmlRootElement(name = "HouseEditor")
public class HouseEditor {

    protected UAddress adr;
    @XmlElement(required = true)
    protected ULOGONINFO logoninfo;
    @XmlElement(required = true)
    protected HOUSEEDITOROPERATION operation;

    /**
     * Gets the value of the adr property.
     * 
     * @return
     *     possible object is
     *     {@link UAddress }
     *     
     */
    public UAddress getAdr() {
        return adr;
    }

    /**
     * Sets the value of the adr property.
     * 
     * @param value
     *     allowed object is
     *     {@link UAddress }
     *     
     */
    public void setAdr(UAddress value) {
        this.adr = value;
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

    /**
     * Gets the value of the operation property.
     * 
     * @return
     *     possible object is
     *     {@link HOUSEEDITOROPERATION }
     *     
     */
    public HOUSEEDITOROPERATION getOperation() {
        return operation;
    }

    /**
     * Sets the value of the operation property.
     * 
     * @param value
     *     allowed object is
     *     {@link HOUSEEDITOROPERATION }
     *     
     */
    public void setOperation(HOUSEEDITOROPERATION value) {
        this.operation = value;
    }

}
