
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
 *         &lt;element name="SavePasUiSettingsResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
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
    "savePasUiSettingsResult"
})
@XmlRootElement(name = "SavePasUiSettingsResponse")
public class SavePasUiSettingsResponse {

    @XmlElement(name = "SavePasUiSettingsResult")
    protected boolean savePasUiSettingsResult;

    /**
     * Gets the value of the savePasUiSettingsResult property.
     * 
     */
    public boolean isSavePasUiSettingsResult() {
        return savePasUiSettingsResult;
    }

    /**
     * Sets the value of the savePasUiSettingsResult property.
     * 
     */
    public void setSavePasUiSettingsResult(boolean value) {
        this.savePasUiSettingsResult = value;
    }

}
