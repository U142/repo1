
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
 *         &lt;element name="GabSearchResult" type="{http://ums.no/ws/pas/}UGabSearchResultList" minOccurs="0"/>
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
    "gabSearchResult"
})
@XmlRootElement(name = "GabSearchResponse")
public class GabSearchResponse {

    @XmlElement(name = "GabSearchResult")
    protected UGabSearchResultList gabSearchResult;

    /**
     * Gets the value of the gabSearchResult property.
     * 
     * @return
     *     possible object is
     *     {@link UGabSearchResultList }
     *     
     */
    public UGabSearchResultList getGabSearchResult() {
        return gabSearchResult;
    }

    /**
     * Sets the value of the gabSearchResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link UGabSearchResultList }
     *     
     */
    public void setGabSearchResult(UGabSearchResultList value) {
        this.gabSearchResult = value;
    }

}
