
package no.ums.ws.parm;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="logon" type="{http://ums.no/ws/parm/}ULOGONINFO"/>
 *         &lt;element name="l_refno" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="sz_jobid" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="b_confirm" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "logon",
    "lRefno",
    "szJobid",
    "bConfirm"
})
@XmlRootElement(name = "ConfirmJob")
public class ConfirmJob {

    @XmlElement(required = true)
    protected ULOGONINFO logon;
    @XmlElement(name = "l_refno")
    protected int lRefno;
    @XmlElement(name = "sz_jobid")
    protected String szJobid;
    @XmlElement(name = "b_confirm")
    protected boolean bConfirm;

    /**
     * Gets the value of the logon property.
     * 
     * @return
     *     possible object is
     *     {@link ULOGONINFO }
     *     
     */
    public ULOGONINFO getLogon() {
        return logon;
    }

    /**
     * Sets the value of the logon property.
     * 
     * @param value
     *     allowed object is
     *     {@link ULOGONINFO }
     *     
     */
    public void setLogon(ULOGONINFO value) {
        this.logon = value;
    }

    /**
     * Gets the value of the lRefno property.
     * 
     */
    public int getLRefno() {
        return lRefno;
    }

    /**
     * Sets the value of the lRefno property.
     * 
     */
    public void setLRefno(int value) {
        this.lRefno = value;
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
     * Gets the value of the bConfirm property.
     * 
     */
    public boolean isBConfirm() {
        return bConfirm;
    }

    /**
     * Sets the value of the bConfirm property.
     * 
     */
    public void setBConfirm(boolean value) {
        this.bConfirm = value;
    }

}
