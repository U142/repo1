
package no.ums.ws.parm.admin;

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
 *         &lt;element name="DefinesResult" type="{http://www.w3.org/2001/XMLSchema}int"/>
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
    "definesResult"
})
@XmlRootElement(name = "DefinesResponse")
public class DefinesResponse {

    @XmlElement(name = "DefinesResult")
    protected int definesResult;

    /**
     * Gets the value of the definesResult property.
     * 
     */
    public int getDefinesResult() {
        return definesResult;
    }

    /**
     * Sets the value of the definesResult property.
     * 
     */
    public void setDefinesResult(int value) {
        this.definesResult = value;
    }

}
