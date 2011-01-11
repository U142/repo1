
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
 *         &lt;element name="PasLogoffResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
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
    "pasLogoffResult"
})
@XmlRootElement(name = "PasLogoffResponse")
public class PasLogoffResponse {

    @XmlElement(name = "PasLogoffResult")
    protected boolean pasLogoffResult;

    /**
     * Gets the value of the pasLogoffResult property.
     * 
     */
    public boolean isPasLogoffResult() {
        return pasLogoffResult;
    }

    /**
     * Sets the value of the pasLogoffResult property.
     * 
     */
    public void setPasLogoffResult(boolean value) {
        this.pasLogoffResult = value;
    }

}
