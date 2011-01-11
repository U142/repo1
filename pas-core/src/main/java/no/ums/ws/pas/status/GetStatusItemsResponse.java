
package no.ums.ws.pas.status;

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
 *         &lt;element name="GetStatusItemsResult" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/>
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
    "getStatusItemsResult"
})
@XmlRootElement(name = "GetStatusItemsResponse")
public class GetStatusItemsResponse {

    @XmlElement(name = "GetStatusItemsResult")
    protected byte[] getStatusItemsResult;

    /**
     * Gets the value of the getStatusItemsResult property.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getGetStatusItemsResult() {
        return getStatusItemsResult;
    }

    /**
     * Sets the value of the getStatusItemsResult property.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setGetStatusItemsResult(byte[] value) {
        this.getStatusItemsResult = ((byte[]) value);
    }

}
