
package no.ums.ws.pas;

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
 *         &lt;element name="_no_useResult" type="{http://ums.no/ws/pas/}imports" minOccurs="0"/>
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
    "noUseResult"
})
@XmlRootElement(name = "_no_useResponse")
public class NoUseResponse {

    @XmlElement(name = "_no_useResult")
    protected Imports noUseResult;

    /**
     * Gets the value of the noUseResult property.
     * 
     * @return
     *     possible object is
     *     {@link Imports }
     *     
     */
    public Imports getNoUseResult() {
        return noUseResult;
    }

    /**
     * Sets the value of the noUseResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link Imports }
     *     
     */
    public void setNoUseResult(Imports value) {
        this.noUseResult = value;
    }

}
