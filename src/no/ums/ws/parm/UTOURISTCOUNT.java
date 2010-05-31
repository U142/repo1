
package no.ums.ws.parm;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UTOURISTCOUNT complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UTOURISTCOUNT">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="l_lastupdate" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="l_operator" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="sz_operator" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="l_touristcount" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UTOURISTCOUNT", propOrder = {
    "lLastupdate",
    "lOperator",
    "szOperator",
    "lTouristcount"
})
public class UTOURISTCOUNT {

    @XmlElement(name = "l_lastupdate")
    protected long lLastupdate;
    @XmlElement(name = "l_operator")
    protected int lOperator;
    @XmlElement(name = "sz_operator")
    protected String szOperator;
    @XmlElement(name = "l_touristcount")
    protected int lTouristcount;

    /**
     * Gets the value of the lLastupdate property.
     * 
     */
    public long getLLastupdate() {
        return lLastupdate;
    }

    /**
     * Sets the value of the lLastupdate property.
     * 
     */
    public void setLLastupdate(long value) {
        this.lLastupdate = value;
    }

    /**
     * Gets the value of the lOperator property.
     * 
     */
    public int getLOperator() {
        return lOperator;
    }

    /**
     * Sets the value of the lOperator property.
     * 
     */
    public void setLOperator(int value) {
        this.lOperator = value;
    }

    /**
     * Gets the value of the szOperator property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzOperator() {
        return szOperator;
    }

    /**
     * Sets the value of the szOperator property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzOperator(String value) {
        this.szOperator = value;
    }

    /**
     * Gets the value of the lTouristcount property.
     * 
     */
    public int getLTouristcount() {
        return lTouristcount;
    }

    /**
     * Sets the value of the lTouristcount property.
     * 
     */
    public void setLTouristcount(int value) {
        this.lTouristcount = value;
    }

}
