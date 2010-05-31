
package no.ums.ws.parm;

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
 *         &lt;element name="municipal" type="{http://ums.no/ws/parm/}UMUNICIPALSENDING" minOccurs="0"/>
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
    "municipal"
})
@XmlRootElement(name = "ExecMunicipalSending")
public class ExecMunicipalSending {

    protected UMUNICIPALSENDING municipal;

    /**
     * Gets the value of the municipal property.
     * 
     * @return
     *     possible object is
     *     {@link UMUNICIPALSENDING }
     *     
     */
    public UMUNICIPALSENDING getMunicipal() {
        return municipal;
    }

    /**
     * Sets the value of the municipal property.
     * 
     * @param value
     *     allowed object is
     *     {@link UMUNICIPALSENDING }
     *     
     */
    public void setMunicipal(UMUNICIPALSENDING value) {
        this.municipal = value;
    }

}
