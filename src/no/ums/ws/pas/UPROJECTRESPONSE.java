
package no.ums.ws.pas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UPROJECT_RESPONSE complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UPROJECT_RESPONSE">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="n_projectpk" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="sz_name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="n_createdtimestamp" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="n_updatedtimestamp" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="n_responsecode" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="sz_responsetext" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UPROJECT_RESPONSE", propOrder = {
    "nProjectpk",
    "szName",
    "nCreatedtimestamp",
    "nUpdatedtimestamp",
    "nResponsecode",
    "szResponsetext"
})
public class UPROJECTRESPONSE {

    @XmlElement(name = "n_projectpk")
    protected long nProjectpk;
    @XmlElement(name = "sz_name")
    protected String szName;
    @XmlElement(name = "n_createdtimestamp")
    protected long nCreatedtimestamp;
    @XmlElement(name = "n_updatedtimestamp")
    protected long nUpdatedtimestamp;
    @XmlElement(name = "n_responsecode")
    protected int nResponsecode;
    @XmlElement(name = "sz_responsetext")
    protected String szResponsetext;

    /**
     * Gets the value of the nProjectpk property.
     * 
     */
    public long getNProjectpk() {
        return nProjectpk;
    }

    /**
     * Sets the value of the nProjectpk property.
     * 
     */
    public void setNProjectpk(long value) {
        this.nProjectpk = value;
    }

    /**
     * Gets the value of the szName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzName() {
        return szName;
    }

    /**
     * Sets the value of the szName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzName(String value) {
        this.szName = value;
    }

    /**
     * Gets the value of the nCreatedtimestamp property.
     * 
     */
    public long getNCreatedtimestamp() {
        return nCreatedtimestamp;
    }

    /**
     * Sets the value of the nCreatedtimestamp property.
     * 
     */
    public void setNCreatedtimestamp(long value) {
        this.nCreatedtimestamp = value;
    }

    /**
     * Gets the value of the nUpdatedtimestamp property.
     * 
     */
    public long getNUpdatedtimestamp() {
        return nUpdatedtimestamp;
    }

    /**
     * Sets the value of the nUpdatedtimestamp property.
     * 
     */
    public void setNUpdatedtimestamp(long value) {
        this.nUpdatedtimestamp = value;
    }

    /**
     * Gets the value of the nResponsecode property.
     * 
     */
    public int getNResponsecode() {
        return nResponsecode;
    }

    /**
     * Sets the value of the nResponsecode property.
     * 
     */
    public void setNResponsecode(int value) {
        this.nResponsecode = value;
    }

    /**
     * Gets the value of the szResponsetext property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzResponsetext() {
        return szResponsetext;
    }

    /**
     * Sets the value of the szResponsetext property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzResponsetext(String value) {
        this.szResponsetext = value;
    }

}
