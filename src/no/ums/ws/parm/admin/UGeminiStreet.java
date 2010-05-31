
package no.ums.ws.parm.admin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UGeminiStreet complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UGeminiStreet">
 *   &lt;complexContent>
 *     &lt;extension base="{http://ums.no/ws/parm/admin/}UShape">
 *       &lt;sequence>
 *         &lt;element name="linelist" type="{http://ums.no/ws/parm/admin/}ArrayOfUGisImportResultLine" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UGeminiStreet", propOrder = {
    "linelist"
})
public class UGeminiStreet
    extends UShape
{

    protected ArrayOfUGisImportResultLine linelist;

    /**
     * Gets the value of the linelist property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfUGisImportResultLine }
     *     
     */
    public ArrayOfUGisImportResultLine getLinelist() {
        return linelist;
    }

    /**
     * Sets the value of the linelist property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfUGisImportResultLine }
     *     
     */
    public void setLinelist(ArrayOfUGisImportResultLine value) {
        this.linelist = value;
    }

}
