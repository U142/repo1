
package no.ums.ws.pas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
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
 *         &lt;element name="getRestrictionShapesResult" type="{http://ums.no/ws/pas/}ArrayOfUDEPARTMENT" minOccurs="0"/>
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
    "getRestrictionShapesResult"
})
@XmlRootElement(name = "getRestrictionShapesResponse")
public class GetRestrictionShapesResponse {

    protected ArrayOfUDEPARTMENT getRestrictionShapesResult;

    /**
     * Gets the value of the getRestrictionShapesResult property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfUDEPARTMENT }
     *     
     */
    public ArrayOfUDEPARTMENT getGetRestrictionShapesResult() {
        return getRestrictionShapesResult;
    }

    /**
     * Sets the value of the getRestrictionShapesResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfUDEPARTMENT }
     *     
     */
    public void setGetRestrictionShapesResult(ArrayOfUDEPARTMENT value) {
        this.getRestrictionShapesResult = value;
    }

}
