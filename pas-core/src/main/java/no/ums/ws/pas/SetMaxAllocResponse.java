
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
 *         &lt;element name="SetMaxAllocResult" type="{http://ums.no/ws/pas/}UMAXALLOC" minOccurs="0"/>
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
    "setMaxAllocResult"
})
@XmlRootElement(name = "SetMaxAllocResponse")
public class SetMaxAllocResponse {

    @XmlElement(name = "SetMaxAllocResult")
    protected UMAXALLOC setMaxAllocResult;

    /**
     * Gets the value of the setMaxAllocResult property.
     * 
     * @return
     *     possible object is
     *     {@link UMAXALLOC }
     *     
     */
    public UMAXALLOC getSetMaxAllocResult() {
        return setMaxAllocResult;
    }

    /**
     * Sets the value of the setMaxAllocResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link UMAXALLOC }
     *     
     */
    public void setSetMaxAllocResult(UMAXALLOC value) {
        this.setMaxAllocResult = value;
    }

}
