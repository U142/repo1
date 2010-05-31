
package no.ums.ws.pas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UMapInfoLayer complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UMapInfoLayer">
 *   &lt;complexContent>
 *     &lt;extension base="{http://ums.no/ws/pas/}UMapInfo">
 *       &lt;sequence>
 *         &lt;element name="sz_userid" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sz_password" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UMapInfoLayer", propOrder = {
    "szUserid",
    "szPassword"
})
@XmlSeeAlso({
    UMapInfoLayerCellVision.class
})
public class UMapInfoLayer
    extends UMapInfo
{

    @XmlElement(name = "sz_userid")
    protected String szUserid;
    @XmlElement(name = "sz_password")
    protected String szPassword;

    /**
     * Gets the value of the szUserid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzUserid() {
        return szUserid;
    }

    /**
     * Sets the value of the szUserid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzUserid(String value) {
        this.szUserid = value;
    }

    /**
     * Gets the value of the szPassword property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzPassword() {
        return szPassword;
    }

    /**
     * Sets the value of the szPassword property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzPassword(String value) {
        this.szPassword = value;
    }

}
