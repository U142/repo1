
package no.ums.ws.parm;

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
 *         &lt;element name="GetEventListResult" type="{http://ums.no/ws/parm/}ArrayOfPAEVENT" minOccurs="0"/>
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
    "getEventListResult"
})
@XmlRootElement(name = "GetEventListResponse")
public class GetEventListResponse {

    @XmlElement(name = "GetEventListResult")
    protected ArrayOfPAEVENT getEventListResult;

    /**
     * Gets the value of the getEventListResult property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfPAEVENT }
     *     
     */
    public ArrayOfPAEVENT getGetEventListResult() {
        return getEventListResult;
    }

    /**
     * Sets the value of the getEventListResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfPAEVENT }
     *     
     */
    public void setGetEventListResult(ArrayOfPAEVENT value) {
        this.getEventListResult = value;
    }

}
