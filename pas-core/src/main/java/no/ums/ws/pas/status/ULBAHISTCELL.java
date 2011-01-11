
package no.ums.ws.pas.status;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ULBAHISTCELL complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ULBAHISTCELL">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="l_operator" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="sz_operator" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="l_2gtotal" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="l_2gok" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="l_3gtotal" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="l_3gok" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="l_4gtotal" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="l_4gok" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="l_timestamp" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="l_successpercentage" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ULBAHISTCELL", propOrder = {
    "lOperator",
    "szOperator",
    "l2Gtotal",
    "l2Gok",
    "l3Gtotal",
    "l3Gok",
    "l4Gtotal",
    "l4Gok",
    "lTimestamp",
    "lSuccesspercentage"
})
public class ULBAHISTCELL {

    @XmlElement(name = "l_operator")
    protected int lOperator;
    @XmlElement(name = "sz_operator")
    protected String szOperator;
    @XmlElement(name = "l_2gtotal")
    protected int l2Gtotal;
    @XmlElement(name = "l_2gok")
    protected int l2Gok;
    @XmlElement(name = "l_3gtotal")
    protected int l3Gtotal;
    @XmlElement(name = "l_3gok")
    protected int l3Gok;
    @XmlElement(name = "l_4gtotal")
    protected int l4Gtotal;
    @XmlElement(name = "l_4gok")
    protected int l4Gok;
    @XmlElement(name = "l_timestamp")
    protected long lTimestamp;
    @XmlElement(name = "l_successpercentage")
    protected float lSuccesspercentage;

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
     * Gets the value of the l2Gtotal property.
     * 
     */
    public int getL2Gtotal() {
        return l2Gtotal;
    }

    /**
     * Sets the value of the l2Gtotal property.
     * 
     */
    public void setL2Gtotal(int value) {
        this.l2Gtotal = value;
    }

    /**
     * Gets the value of the l2Gok property.
     * 
     */
    public int getL2Gok() {
        return l2Gok;
    }

    /**
     * Sets the value of the l2Gok property.
     * 
     */
    public void setL2Gok(int value) {
        this.l2Gok = value;
    }

    /**
     * Gets the value of the l3Gtotal property.
     * 
     */
    public int getL3Gtotal() {
        return l3Gtotal;
    }

    /**
     * Sets the value of the l3Gtotal property.
     * 
     */
    public void setL3Gtotal(int value) {
        this.l3Gtotal = value;
    }

    /**
     * Gets the value of the l3Gok property.
     * 
     */
    public int getL3Gok() {
        return l3Gok;
    }

    /**
     * Sets the value of the l3Gok property.
     * 
     */
    public void setL3Gok(int value) {
        this.l3Gok = value;
    }

    /**
     * Gets the value of the l4Gtotal property.
     * 
     */
    public int getL4Gtotal() {
        return l4Gtotal;
    }

    /**
     * Sets the value of the l4Gtotal property.
     * 
     */
    public void setL4Gtotal(int value) {
        this.l4Gtotal = value;
    }

    /**
     * Gets the value of the l4Gok property.
     * 
     */
    public int getL4Gok() {
        return l4Gok;
    }

    /**
     * Sets the value of the l4Gok property.
     * 
     */
    public void setL4Gok(int value) {
        this.l4Gok = value;
    }

    /**
     * Gets the value of the lTimestamp property.
     * 
     */
    public long getLTimestamp() {
        return lTimestamp;
    }

    /**
     * Sets the value of the lTimestamp property.
     * 
     */
    public void setLTimestamp(long value) {
        this.lTimestamp = value;
    }

    /**
     * Gets the value of the lSuccesspercentage property.
     * 
     */
    public float getLSuccesspercentage() {
        return lSuccesspercentage;
    }

    /**
     * Sets the value of the lSuccesspercentage property.
     * 
     */
    public void setLSuccesspercentage(float value) {
        this.lSuccesspercentage = value;
    }

}
