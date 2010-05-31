
package no.ums.ws.pas.tas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UTASREQUESTRESULTS complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UTASREQUESTRESULTS">
 *   &lt;complexContent>
 *     &lt;extension base="{http://ums.no/ws/pas/tas}UTASREQUEST">
 *       &lt;sequence>
 *         &lt;element name="type" type="{http://ums.no/ws/pas/tas}ENUM_TASREQUESTRESULTTYPE"/>
 *         &lt;element name="n_operator" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="sz_operatorname" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sz_jobid" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="n_response" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="n_status" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="n_userpk" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="n_deptpk" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="sz_userid" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sz_username" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="n_refno" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="n_retries" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="n_requesttype" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="n_simulation" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="sz_sendingname" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UTASREQUESTRESULTS", propOrder = {
    "type",
    "nOperator",
    "szOperatorname",
    "szJobid",
    "nResponse",
    "nStatus",
    "nUserpk",
    "nDeptpk",
    "szUserid",
    "szUsername",
    "nRefno",
    "nRetries",
    "nRequesttype",
    "nSimulation",
    "szSendingname"
})
public class UTASREQUESTRESULTS
    extends UTASREQUEST
{

    @XmlElement(required = true)
    protected ENUMTASREQUESTRESULTTYPE type;
    @XmlElement(name = "n_operator")
    protected int nOperator;
    @XmlElement(name = "sz_operatorname")
    protected String szOperatorname;
    @XmlElement(name = "sz_jobid")
    protected String szJobid;
    @XmlElement(name = "n_response")
    protected int nResponse;
    @XmlElement(name = "n_status")
    protected int nStatus;
    @XmlElement(name = "n_userpk")
    protected long nUserpk;
    @XmlElement(name = "n_deptpk")
    protected int nDeptpk;
    @XmlElement(name = "sz_userid")
    protected String szUserid;
    @XmlElement(name = "sz_username")
    protected String szUsername;
    @XmlElement(name = "n_refno")
    protected int nRefno;
    @XmlElement(name = "n_retries")
    protected int nRetries;
    @XmlElement(name = "n_requesttype")
    protected int nRequesttype;
    @XmlElement(name = "n_simulation")
    protected int nSimulation;
    @XmlElement(name = "sz_sendingname")
    protected String szSendingname;

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link ENUMTASREQUESTRESULTTYPE }
     *     
     */
    public ENUMTASREQUESTRESULTTYPE getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link ENUMTASREQUESTRESULTTYPE }
     *     
     */
    public void setType(ENUMTASREQUESTRESULTTYPE value) {
        this.type = value;
    }

    /**
     * Gets the value of the nOperator property.
     * 
     */
    public int getNOperator() {
        return nOperator;
    }

    /**
     * Sets the value of the nOperator property.
     * 
     */
    public void setNOperator(int value) {
        this.nOperator = value;
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
     * Gets the value of the nResponse property.
     * 
     */
    public int getNResponse() {
        return nResponse;
    }

    /**
     * Sets the value of the nResponse property.
     * 
     */
    public void setNResponse(int value) {
        this.nResponse = value;
    }

    /**
     * Gets the value of the nStatus property.
     * 
     */
    public int getNStatus() {
        return nStatus;
    }

    /**
     * Sets the value of the nStatus property.
     * 
     */
    public void setNStatus(int value) {
        this.nStatus = value;
    }

    /**
     * Gets the value of the nUserpk property.
     * 
     */
    public long getNUserpk() {
        return nUserpk;
    }

    /**
     * Sets the value of the nUserpk property.
     * 
     */
    public void setNUserpk(long value) {
        this.nUserpk = value;
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
     * Gets the value of the szUsername property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzUsername() {
        return szUsername;
    }

    /**
     * Sets the value of the szUsername property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzUsername(String value) {
        this.szUsername = value;
    }

    /**
     * Gets the value of the nRefno property.
     * 
     */
    public int getNRefno() {
        return nRefno;
    }

    /**
     * Sets the value of the nRefno property.
     * 
     */
    public void setNRefno(int value) {
        this.nRefno = value;
    }

    /**
     * Gets the value of the nRetries property.
     * 
     */
    public int getNRetries() {
        return nRetries;
    }

    /**
     * Sets the value of the nRetries property.
     * 
     */
    public void setNRetries(int value) {
        this.nRetries = value;
    }

    /**
     * Gets the value of the nRequesttype property.
     * 
     */
    public int getNRequesttype() {
        return nRequesttype;
    }

    /**
     * Sets the value of the nRequesttype property.
     * 
     */
    public void setNRequesttype(int value) {
        this.nRequesttype = value;
    }

    /**
     * Gets the value of the nSimulation property.
     * 
     */
    public int getNSimulation() {
        return nSimulation;
    }

    /**
     * Sets the value of the nSimulation property.
     * 
     */
    public void setNSimulation(int value) {
        this.nSimulation = value;
    }

    /**
     * Gets the value of the szSendingname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzSendingname() {
        return szSendingname;
    }

    /**
     * Sets the value of the szSendingname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzSendingname(String value) {
        this.szSendingname = value;
    }

}
