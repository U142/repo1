
package no.ums.ws.pas.tas;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for UTASREQUEST complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UTASREQUEST">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="n_requestpk" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="b_success" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="list" type="{http://ums.no/ws/pas/tas}ArrayOfULBACOUNTRY" minOccurs="0"/>
 *         &lt;element name="n_timestamp" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="operators" type="{http://ums.no/ws/pas/tas}ArrayOfInt" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UTASREQUEST", propOrder = {
    "nRequestpk",
    "bSuccess",
    "list",
    "nTimestamp",
    "operators"
})
@XmlSeeAlso({
    UTASREQUESTRESULTS.class
})
public class UTASREQUEST {

    @XmlElement(name = "n_requestpk")
    protected int nRequestpk;
    @XmlElement(name = "b_success")
    protected boolean bSuccess;
    protected ArrayOfULBACOUNTRY list;
    @XmlElement(name = "n_timestamp")
    protected long nTimestamp;
    protected ArrayOfInt operators;

    /**
     * Gets the value of the nRequestpk property.
     * 
     */
    public int getNRequestpk() {
        return nRequestpk;
    }

    /**
     * Sets the value of the nRequestpk property.
     * 
     */
    public void setNRequestpk(int value) {
        this.nRequestpk = value;
    }

    /**
     * Gets the value of the bSuccess property.
     * 
     */
    public boolean isBSuccess() {
        return bSuccess;
    }

    /**
     * Sets the value of the bSuccess property.
     * 
     */
    public void setBSuccess(boolean value) {
        this.bSuccess = value;
    }

    /**
     * Gets the value of the list property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfULBACOUNTRY }
     *     
     */
    public ArrayOfULBACOUNTRY getList() {
        return list;
    }

    /**
     * Sets the value of the list property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfULBACOUNTRY }
     *     
     */
    public void setList(ArrayOfULBACOUNTRY value) {
        this.list = value;
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
     * Gets the value of the operators property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfInt }
     *     
     */
    public ArrayOfInt getOperators() {
        return operators;
    }

    /**
     * Sets the value of the operators property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfInt }
     *     
     */
    public void setOperators(ArrayOfInt value) {
        this.operators = value;
    }

}
