
package no.ums.ws.parm;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UMUNICIPALSENDING complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UMUNICIPALSENDING">
 *   &lt;complexContent>
 *     &lt;extension base="{http://ums.no/ws/parm/}UMAPSENDING">
 *       &lt;sequence>
 *         &lt;element name="municipals" type="{http://ums.no/ws/parm/}ArrayOfUMunicipalDef" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UMUNICIPALSENDING", propOrder = {
    "municipals"
})
public class UMUNICIPALSENDING
    extends UMAPSENDING
{

    protected ArrayOfUMunicipalDef municipals;

    /**
     * Gets the value of the municipals property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfUMunicipalDef }
     *     
     */
    public ArrayOfUMunicipalDef getMunicipals() {
        return municipals;
    }

    /**
     * Sets the value of the municipals property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfUMunicipalDef }
     *     
     */
    public void setMunicipals(ArrayOfUMunicipalDef value) {
        this.municipals = value;
    }

}
