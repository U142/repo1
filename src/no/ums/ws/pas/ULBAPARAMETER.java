
package no.ums.ws.pas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ULBAPARAMETER complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ULBAPARAMETER">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="l_parameterspk" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="l_incorrect" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="l_autologoff" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="sz_adminemail" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="l_channelno" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="l_test_channelno" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="l_heartbeat" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="l_duration" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="l_interval" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="l_repetition" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="l_deptpk" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="l_comppk" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ULBAPARAMETER", propOrder = {
    "lParameterspk",
    "lIncorrect",
    "lAutologoff",
    "szAdminemail",
    "lChannelno",
    "lTestChannelno",
    "lHeartbeat",
    "lDuration",
    "lInterval",
    "lRepetition",
    "lDeptpk",
    "lComppk"
})
public class ULBAPARAMETER {

    @XmlElement(name = "l_parameterspk")
    protected long lParameterspk;
    @XmlElement(name = "l_incorrect")
    protected int lIncorrect;
    @XmlElement(name = "l_autologoff")
    protected int lAutologoff;
    @XmlElement(name = "sz_adminemail")
    protected String szAdminemail;
    @XmlElement(name = "l_channelno")
    protected int lChannelno;
    @XmlElement(name = "l_test_channelno")
    protected int lTestChannelno;
    @XmlElement(name = "l_heartbeat")
    protected int lHeartbeat;
    @XmlElement(name = "l_duration")
    protected int lDuration;
    @XmlElement(name = "l_interval")
    protected int lInterval;
    @XmlElement(name = "l_repetition")
    protected int lRepetition;
    @XmlElement(name = "l_deptpk")
    protected int lDeptpk;
    @XmlElement(name = "l_comppk")
    protected int lComppk;

    /**
     * Gets the value of the lParameterspk property.
     * 
     */
    public long getLParameterspk() {
        return lParameterspk;
    }

    /**
     * Sets the value of the lParameterspk property.
     * 
     */
    public void setLParameterspk(long value) {
        this.lParameterspk = value;
    }

    /**
     * Gets the value of the lIncorrect property.
     * 
     */
    public int getLIncorrect() {
        return lIncorrect;
    }

    /**
     * Sets the value of the lIncorrect property.
     * 
     */
    public void setLIncorrect(int value) {
        this.lIncorrect = value;
    }

    /**
     * Gets the value of the lAutologoff property.
     * 
     */
    public int getLAutologoff() {
        return lAutologoff;
    }

    /**
     * Sets the value of the lAutologoff property.
     * 
     */
    public void setLAutologoff(int value) {
        this.lAutologoff = value;
    }

    /**
     * Gets the value of the szAdminemail property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzAdminemail() {
        return szAdminemail;
    }

    /**
     * Sets the value of the szAdminemail property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzAdminemail(String value) {
        this.szAdminemail = value;
    }

    /**
     * Gets the value of the lChannelno property.
     * 
     */
    public int getLChannelno() {
        return lChannelno;
    }

    /**
     * Sets the value of the lChannelno property.
     * 
     */
    public void setLChannelno(int value) {
        this.lChannelno = value;
    }

    /**
     * Gets the value of the lTestChannelno property.
     * 
     */
    public int getLTestChannelno() {
        return lTestChannelno;
    }

    /**
     * Sets the value of the lTestChannelno property.
     * 
     */
    public void setLTestChannelno(int value) {
        this.lTestChannelno = value;
    }

    /**
     * Gets the value of the lHeartbeat property.
     * 
     */
    public int getLHeartbeat() {
        return lHeartbeat;
    }

    /**
     * Sets the value of the lHeartbeat property.
     * 
     */
    public void setLHeartbeat(int value) {
        this.lHeartbeat = value;
    }

    /**
     * Gets the value of the lDuration property.
     * 
     */
    public int getLDuration() {
        return lDuration;
    }

    /**
     * Sets the value of the lDuration property.
     * 
     */
    public void setLDuration(int value) {
        this.lDuration = value;
    }

    /**
     * Gets the value of the lInterval property.
     * 
     */
    public int getLInterval() {
        return lInterval;
    }

    /**
     * Sets the value of the lInterval property.
     * 
     */
    public void setLInterval(int value) {
        this.lInterval = value;
    }

    /**
     * Gets the value of the lRepetition property.
     * 
     */
    public int getLRepetition() {
        return lRepetition;
    }

    /**
     * Sets the value of the lRepetition property.
     * 
     */
    public void setLRepetition(int value) {
        this.lRepetition = value;
    }

    /**
     * Gets the value of the lDeptpk property.
     * 
     */
    public int getLDeptpk() {
        return lDeptpk;
    }

    /**
     * Sets the value of the lDeptpk property.
     * 
     */
    public void setLDeptpk(int value) {
        this.lDeptpk = value;
    }

    /**
     * Gets the value of the lComppk property.
     * 
     */
    public int getLComppk() {
        return lComppk;
    }

    /**
     * Sets the value of the lComppk property.
     * 
     */
    public void setLComppk(int value) {
        this.lComppk = value;
    }

}
