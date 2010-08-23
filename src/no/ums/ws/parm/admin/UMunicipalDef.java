
package no.ums.ws.parm.admin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UMunicipalDef complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UMunicipalDef">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="sz_municipalid" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sz_municipalname" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UMunicipalDef", propOrder = {
    "szMunicipalid",
    "szMunicipalname"
})
public class UMunicipalDef {

    @XmlElement(name = "sz_municipalid")
    protected String szMunicipalid;
    @XmlElement(name = "sz_municipalname")
    protected String szMunicipalname;

    /**
     * Gets the value of the szMunicipalid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzMunicipalid() {
        return szMunicipalid;
    }

    /**
     * Sets the value of the szMunicipalid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzMunicipalid(String value) {
        this.szMunicipalid = value;
    }

    /**
     * Gets the value of the szMunicipalname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzMunicipalname() {
        return szMunicipalname;
    }

    /**
     * Sets the value of the szMunicipalname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzMunicipalname(String value) {
        this.szMunicipalname = value;
    }

}
