
package no.ums.ws.pas.status;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for BBPROJECT complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BBPROJECT">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="l_projectpk" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="sz_projectname" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BBPROJECT")
public class BBPROJECT {

    @XmlAttribute(name = "l_projectpk")
    protected String lProjectpk;
    @XmlAttribute(name = "sz_projectname")
    protected String szProjectname;

    /**
     * Gets the value of the lProjectpk property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLProjectpk() {
        return lProjectpk;
    }

    /**
     * Sets the value of the lProjectpk property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLProjectpk(String value) {
        this.lProjectpk = value;
    }

    /**
     * Gets the value of the szProjectname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzProjectname() {
        return szProjectname;
    }

    /**
     * Sets the value of the szProjectname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzProjectname(String value) {
        this.szProjectname = value;
    }

}
