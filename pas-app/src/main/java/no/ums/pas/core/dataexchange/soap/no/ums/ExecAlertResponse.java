
package no.ums.pas.core.dataexchange.soap.no.ums;

import javax.xml.bind.annotation.*;


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
 *         &lt;element name="ExecAlertResult" type="{http://ums.no/}ExecResponse" minOccurs="0"/>
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
    "execAlertResult"
})
@XmlRootElement(name = "ExecAlertResponse")
public class ExecAlertResponse {

    @XmlElement(name = "ExecAlertResult")
    protected ExecResponse execAlertResult;

    /**
     * Gets the value of the execAlertResult property.
     * 
     * @return
     *     possible object is
     *     {@link ExecResponse }
     *     
     */
    public ExecResponse getExecAlertResult() {
        return execAlertResult;
    }

    /**
     * Sets the value of the execAlertResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ExecResponse }
     *     
     */
    public void setExecAlertResult(ExecResponse value) {
        this.execAlertResult = value;
    }

}
