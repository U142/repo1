
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
 *         &lt;element name="LoadLanguageAndVisualsResult" type="{http://ums.no/ws/pas/}UPASUISETTINGS" minOccurs="0"/>
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
    "loadLanguageAndVisualsResult"
})
@XmlRootElement(name = "LoadLanguageAndVisualsResponse")
public class LoadLanguageAndVisualsResponse {

    @XmlElement(name = "LoadLanguageAndVisualsResult")
    protected UPASUISETTINGS loadLanguageAndVisualsResult;

    /**
     * Gets the value of the loadLanguageAndVisualsResult property.
     * 
     * @return
     *     possible object is
     *     {@link UPASUISETTINGS }
     *     
     */
    public UPASUISETTINGS getLoadLanguageAndVisualsResult() {
        return loadLanguageAndVisualsResult;
    }

    /**
     * Sets the value of the loadLanguageAndVisualsResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link UPASUISETTINGS }
     *     
     */
    public void setLoadLanguageAndVisualsResult(UPASUISETTINGS value) {
        this.loadLanguageAndVisualsResult = value;
    }

}
