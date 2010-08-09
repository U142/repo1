
package no.ums.ws.pas.status;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ULBASENDING complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ULBASENDING">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="l_refno" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="l_cbtype" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="l_status" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="l_response" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="l_items" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="l_proc" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="l_retries" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="l_requesttype" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="f_simulation" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="l_operator" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="sz_operator" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sz_jobid" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sz_areaid" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="histcc" type="{http://ums.no/ws/pas/status}ArrayOfULBAHISTCC" minOccurs="0"/>
 *         &lt;element name="histcell" type="{http://ums.no/ws/pas/status}ArrayOfULBAHISTCELL" minOccurs="0"/>
 *         &lt;element name="send_ts" type="{http://ums.no/ws/pas/status}ArrayOfULBASEND_TS" minOccurs="0"/>
 *         &lt;element name="languages" type="{http://ums.no/ws/pas/status}ArrayOfLBALanguage" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ULBASENDING", propOrder = {
    "lRefno",
    "lCbtype",
    "lStatus",
    "lResponse",
    "lItems",
    "lProc",
    "lRetries",
    "lRequesttype",
    "fSimulation",
    "lOperator",
    "szOperator",
    "szJobid",
    "szAreaid",
    "histcc",
    "histcell",
    "sendTs",
    "languages"
})
@XmlSeeAlso({
    CBSTATUS.class
})
public class ULBASENDING {

    @XmlElement(name = "l_refno")
    protected long lRefno;
    @XmlElement(name = "l_cbtype")
    protected int lCbtype;
    @XmlElement(name = "l_status")
    protected int lStatus;
    @XmlElement(name = "l_response")
    protected int lResponse;
    @XmlElement(name = "l_items")
    protected int lItems;
    @XmlElement(name = "l_proc")
    protected int lProc;
    @XmlElement(name = "l_retries")
    protected int lRetries;
    @XmlElement(name = "l_requesttype")
    protected int lRequesttype;
    @XmlElement(name = "f_simulation")
    protected int fSimulation;
    @XmlElement(name = "l_operator")
    protected int lOperator;
    @XmlElement(name = "sz_operator")
    protected String szOperator;
    @XmlElement(name = "sz_jobid")
    protected String szJobid;
    @XmlElement(name = "sz_areaid")
    protected String szAreaid;
    protected ArrayOfULBAHISTCC histcc;
    protected ArrayOfULBAHISTCELL histcell;
    @XmlElement(name = "send_ts")
    protected ArrayOfULBASENDTS sendTs;
    protected ArrayOfLBALanguage languages;

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
     * Gets the value of the lCbtype property.
     * 
     */
    public int getLCbtype() {
        return lCbtype;
    }

    /**
     * Sets the value of the lCbtype property.
     * 
     */
    public void setLCbtype(int value) {
        this.lCbtype = value;
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
     * Gets the value of the lItems property.
     * 
     */
    public int getLItems() {
        return lItems;
    }

    /**
     * Sets the value of the lItems property.
     * 
     */
    public void setLItems(int value) {
        this.lItems = value;
    }

    /**
     * Gets the value of the lProc property.
     * 
     */
    public int getLProc() {
        return lProc;
    }

    /**
     * Sets the value of the lProc property.
     * 
     */
    public void setLProc(int value) {
        this.lProc = value;
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
     * Gets the value of the lRequesttype property.
     * 
     */
    public int getLRequesttype() {
        return lRequesttype;
    }

    /**
     * Sets the value of the lRequesttype property.
     * 
     */
    public void setLRequesttype(int value) {
        this.lRequesttype = value;
    }

    /**
     * Gets the value of the fSimulation property.
     * 
     */
    public int getFSimulation() {
        return fSimulation;
    }

    /**
     * Sets the value of the fSimulation property.
     * 
     */
    public void setFSimulation(int value) {
        this.fSimulation = value;
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
     * Gets the value of the szJobid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzJobid() {
        return szJobid;
    }

    /**
     * Sets the value of the szJobid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzJobid(String value) {
        this.szJobid = value;
    }

    /**
     * Gets the value of the szAreaid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzAreaid() {
        return szAreaid;
    }

    /**
     * Sets the value of the szAreaid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzAreaid(String value) {
        this.szAreaid = value;
    }

    /**
     * Gets the value of the histcc property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfULBAHISTCC }
     *     
     */
    public ArrayOfULBAHISTCC getHistcc() {
        return histcc;
    }

    /**
     * Sets the value of the histcc property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfULBAHISTCC }
     *     
     */
    public void setHistcc(ArrayOfULBAHISTCC value) {
        this.histcc = value;
    }

    /**
     * Gets the value of the histcell property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfULBAHISTCELL }
     *     
     */
    public ArrayOfULBAHISTCELL getHistcell() {
        return histcell;
    }

    /**
     * Sets the value of the histcell property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfULBAHISTCELL }
     *     
     */
    public void setHistcell(ArrayOfULBAHISTCELL value) {
        this.histcell = value;
    }

    /**
     * Gets the value of the sendTs property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfULBASENDTS }
     *     
     */
    public ArrayOfULBASENDTS getSendTs() {
        return sendTs;
    }

    /**
     * Sets the value of the sendTs property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfULBASENDTS }
     *     
     */
    public void setSendTs(ArrayOfULBASENDTS value) {
        this.sendTs = value;
    }

    /**
     * Gets the value of the languages property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfLBALanguage }
     *     
     */
    public ArrayOfLBALanguage getLanguages() {
        return languages;
    }

    /**
     * Sets the value of the languages property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfLBALanguage }
     *     
     */
    public void setLanguages(ArrayOfLBALanguage value) {
        this.languages = value;
    }

}
