
package no.ums.ws.pas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ULBAMESSAGE complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ULBAMESSAGE">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="n_messagepk" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="n_deptpk" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="sz_name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sz_description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="n_langpk" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="n_parentpk" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="n_depth" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="n_timestamp" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="sz_message" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ULBAMESSAGE", propOrder = {
    "nMessagepk",
    "nDeptpk",
    "szName",
    "szDescription",
    "nLangpk",
    "nParentpk",
    "nDepth",
    "nTimestamp",
    "szMessage"
})
public class ULBAMESSAGE {

    @XmlElement(name = "n_messagepk")
    protected long nMessagepk;
    @XmlElement(name = "n_deptpk")
    protected int nDeptpk;
    @XmlElement(name = "sz_name")
    protected String szName;
    @XmlElement(name = "sz_description")
    protected String szDescription;
    @XmlElement(name = "n_langpk")
    protected long nLangpk;
    @XmlElement(name = "n_parentpk")
    protected long nParentpk;
    @XmlElement(name = "n_depth")
    protected int nDepth;
    @XmlElement(name = "n_timestamp")
    protected long nTimestamp;
    @XmlElement(name = "sz_message")
    protected String szMessage;

    /**
     * Gets the value of the nMessagepk property.
     * 
     */
    public long getNMessagepk() {
        return nMessagepk;
    }

    /**
     * Sets the value of the nMessagepk property.
     * 
     */
    public void setNMessagepk(long value) {
        this.nMessagepk = value;
    }

    /**
     * Gets the value of the nDeptpk property.
     * 
     */
    public int getNDeptpk() {
        return nDeptpk;
    }

    /**
     * Sets the value of the nDeptpk property.
     * 
     */
    public void setNDeptpk(int value) {
        this.nDeptpk = value;
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
     * Gets the value of the szDescription property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzDescription() {
        return szDescription;
    }

    /**
     * Sets the value of the szDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzDescription(String value) {
        this.szDescription = value;
    }

    /**
     * Gets the value of the nLangpk property.
     * 
     */
    public long getNLangpk() {
        return nLangpk;
    }

    /**
     * Sets the value of the nLangpk property.
     * 
     */
    public void setNLangpk(long value) {
        this.nLangpk = value;
    }

    /**
     * Gets the value of the nParentpk property.
     * 
     */
    public long getNParentpk() {
        return nParentpk;
    }

    /**
     * Sets the value of the nParentpk property.
     * 
     */
    public void setNParentpk(long value) {
        this.nParentpk = value;
    }

    /**
     * Gets the value of the nDepth property.
     * 
     */
    public int getNDepth() {
        return nDepth;
    }

    /**
     * Sets the value of the nDepth property.
     * 
     */
    public void setNDepth(int value) {
        this.nDepth = value;
    }

    /**
     * Gets the value of the nTimestamp property.
     * 
     */
    public long getNTimestamp() {
        return nTimestamp;
    }

    /**
     * Sets the value of the nTimestamp property.
     * 
     */
    public void setNTimestamp(long value) {
        this.nTimestamp = value;
    }

    /**
     * Gets the value of the szMessage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzMessage() {
        return szMessage;
    }

    /**
     * Sets the value of the szMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzMessage(String value) {
        this.szMessage = value;
    }

}
