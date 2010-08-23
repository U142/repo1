
package no.ums.ws.pas.status;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CB_MESSAGE_MONTHLY_REPORT_RESPONSE complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CB_MESSAGE_MONTHLY_REPORT_RESPONSE">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="sz_usertype" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sz_userid" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="l_refno" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="l_operator" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="sz_operatorname" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="l_simulate" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="l_type" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="l_response" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="l_retries" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="l_status" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="l_last_ts" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="sz_text" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="l_addressedcells" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="l_performance" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CB_MESSAGE_MONTHLY_REPORT_RESPONSE", propOrder = {
    "szUsertype",
    "szUserid",
    "lRefno",
    "lOperator",
    "szOperatorname",
    "lSimulate",
    "lType",
    "lResponse",
    "lRetries",
    "lStatus",
    "lLastTs",
    "szText",
    "lAddressedcells",
    "lPerformance"
})
public class CBMESSAGEMONTHLYREPORTRESPONSE {

    @XmlElement(name = "sz_usertype")
    protected String szUsertype;
    @XmlElement(name = "sz_userid")
    protected String szUserid;
    @XmlElement(name = "l_refno")
    protected long lRefno;
    @XmlElement(name = "l_operator")
    protected int lOperator;
    @XmlElement(name = "sz_operatorname")
    protected String szOperatorname;
    @XmlElement(name = "l_simulate")
    protected int lSimulate;
    @XmlElement(name = "l_type")
    protected int lType;
    @XmlElement(name = "l_response")
    protected int lResponse;
    @XmlElement(name = "l_retries")
    protected int lRetries;
    @XmlElement(name = "l_status")
    protected int lStatus;
    @XmlElement(name = "l_last_ts")
    protected long lLastTs;
    @XmlElement(name = "sz_text")
    protected String szText;
    @XmlElement(name = "l_addressedcells")
    protected int lAddressedcells;
    @XmlElement(name = "l_performance")
    protected float lPerformance;

    /**
     * Gets the value of the szUsertype property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzUsertype() {
        return szUsertype;
    }

    /**
     * Sets the value of the szUsertype property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzUsertype(String value) {
        this.szUsertype = value;
    }

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
     * Gets the value of the lRefno property.
     * 
     */
    public long getLRefno() {
        return lRefno;
    }

    /**
     * Sets the value of the lRefno property.
     * 
     */
    public void setLRefno(long value) {
        this.lRefno = value;
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
     * Gets the value of the szOperatorname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzOperatorname() {
        return szOperatorname;
    }

    /**
     * Sets the value of the szOperatorname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzOperatorname(String value) {
        this.szOperatorname = value;
    }

    /**
     * Gets the value of the lSimulate property.
     * 
     */
    public int getLSimulate() {
        return lSimulate;
    }

    /**
     * Sets the value of the lSimulate property.
     * 
     */
    public void setLSimulate(int value) {
        this.lSimulate = value;
    }

    /**
     * Gets the value of the lType property.
     * 
     */
    public int getLType() {
        return lType;
    }

    /**
     * Sets the value of the lType property.
     * 
     */
    public void setLType(int value) {
        this.lType = value;
    }

    /**
     * Gets the value of the lResponse property.
     * 
     */
    public int getLResponse() {
        return lResponse;
    }

    /**
     * Sets the value of the lResponse property.
     * 
     */
    public void setLResponse(int value) {
        this.lResponse = value;
    }

    /**
     * Gets the value of the lRetries property.
     * 
     */
    public int getLRetries() {
        return lRetries;
    }

    /**
     * Sets the value of the lRetries property.
     * 
     */
    public void setLRetries(int value) {
        this.lRetries = value;
    }

    /**
     * Gets the value of the lStatus property.
     * 
     */
    public int getLStatus() {
        return lStatus;
    }

    /**
     * Sets the value of the lStatus property.
     * 
     */
    public void setLStatus(int value) {
        this.lStatus = value;
    }

    /**
     * Gets the value of the lLastTs property.
     * 
     */
    public long getLLastTs() {
        return lLastTs;
    }

    /**
     * Sets the value of the lLastTs property.
     * 
     */
    public void setLLastTs(long value) {
        this.lLastTs = value;
    }

    /**
     * Gets the value of the szText property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzText() {
        return szText;
    }

    /**
     * Sets the value of the szText property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzText(String value) {
        this.szText = value;
    }

    /**
     * Gets the value of the lAddressedcells property.
     * 
     */
    public int getLAddressedcells() {
        return lAddressedcells;
    }

    /**
     * Sets the value of the lAddressedcells property.
     * 
     */
    public void setLAddressedcells(int value) {
        this.lAddressedcells = value;
    }

    /**
     * Gets the value of the lPerformance property.
     * 
     */
    public float getLPerformance() {
        return lPerformance;
    }

    /**
     * Sets the value of the lPerformance property.
     * 
     */
    public void setLPerformance(float value) {
        this.lPerformance = value;
    }

}
