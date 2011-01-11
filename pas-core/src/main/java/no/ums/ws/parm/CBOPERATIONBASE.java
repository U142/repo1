
package no.ums.ws.parm;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CB_OPERATION_BASE complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CB_OPERATION_BASE">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="risk" type="{http://ums.no/ws/parm/}CB_RISK" minOccurs="0"/>
 *         &lt;element name="reaction" type="{http://ums.no/ws/parm/}CB_REACTION" minOccurs="0"/>
 *         &lt;element name="originator" type="{http://ums.no/ws/parm/}CB_ORIGINATOR" minOccurs="0"/>
 *         &lt;element name="messageconfirmation" type="{http://ums.no/ws/parm/}CB_MESSAGE_CONFIRMATION" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="operation" use="required" type="{http://ums.no/ws/parm/}CB_OPERATION" />
 *       &lt;attribute name="l_refno" use="required" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="l_projectpk" use="required" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="sz_projectname" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="l_comppk" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="l_deptpk" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="l_userpk" use="required" type="{http://www.w3.org/2001/XMLSchema}long" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CB_OPERATION_BASE", propOrder = {
    "risk",
    "reaction",
    "originator",
    "messageconfirmation"
})
@XmlSeeAlso({
    CBALERTKILL.class,
    CBSENDBASE.class
})
public abstract class CBOPERATIONBASE {

    protected CBRISK risk;
    protected CBREACTION reaction;
    protected CBORIGINATOR originator;
    protected CBMESSAGECONFIRMATION messageconfirmation;
    @XmlAttribute(required = true)
    protected CBOPERATION operation;
    @XmlAttribute(name = "l_refno", required = true)
    protected long lRefno;
    @XmlAttribute(name = "l_projectpk", required = true)
    protected long lProjectpk;
    @XmlAttribute(name = "sz_projectname")
    protected String szProjectname;
    @XmlAttribute(name = "l_comppk", required = true)
    protected int lComppk;
    @XmlAttribute(name = "l_deptpk", required = true)
    protected int lDeptpk;
    @XmlAttribute(name = "l_userpk", required = true)
    protected long lUserpk;

    /**
     * Gets the value of the risk property.
     * 
     * @return
     *     possible object is
     *     {@link CBRISK }
     *     
     */
    public CBRISK getRisk() {
        return risk;
    }

    /**
     * Sets the value of the risk property.
     * 
     * @param value
     *     allowed object is
     *     {@link CBRISK }
     *     
     */
    public void setRisk(CBRISK value) {
        this.risk = value;
    }

    /**
     * Gets the value of the reaction property.
     * 
     * @return
     *     possible object is
     *     {@link CBREACTION }
     *     
     */
    public CBREACTION getReaction() {
        return reaction;
    }

    /**
     * Sets the value of the reaction property.
     * 
     * @param value
     *     allowed object is
     *     {@link CBREACTION }
     *     
     */
    public void setReaction(CBREACTION value) {
        this.reaction = value;
    }

    /**
     * Gets the value of the originator property.
     * 
     * @return
     *     possible object is
     *     {@link CBORIGINATOR }
     *     
     */
    public CBORIGINATOR getOriginator() {
        return originator;
    }

    /**
     * Sets the value of the originator property.
     * 
     * @param value
     *     allowed object is
     *     {@link CBORIGINATOR }
     *     
     */
    public void setOriginator(CBORIGINATOR value) {
        this.originator = value;
    }

    /**
     * Gets the value of the messageconfirmation property.
     * 
     * @return
     *     possible object is
     *     {@link CBMESSAGECONFIRMATION }
     *     
     */
    public CBMESSAGECONFIRMATION getMessageconfirmation() {
        return messageconfirmation;
    }

    /**
     * Sets the value of the messageconfirmation property.
     * 
     * @param value
     *     allowed object is
     *     {@link CBMESSAGECONFIRMATION }
     *     
     */
    public void setMessageconfirmation(CBMESSAGECONFIRMATION value) {
        this.messageconfirmation = value;
    }

    /**
     * Gets the value of the operation property.
     * 
     * @return
     *     possible object is
     *     {@link CBOPERATION }
     *     
     */
    public CBOPERATION getOperation() {
        return operation;
    }

    /**
     * Sets the value of the operation property.
     * 
     * @param value
     *     allowed object is
     *     {@link CBOPERATION }
     *     
     */
    public void setOperation(CBOPERATION value) {
        this.operation = value;
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
     * Gets the value of the lProjectpk property.
     * 
     */
    public long getLProjectpk() {
        return lProjectpk;
    }

    /**
     * Sets the value of the lProjectpk property.
     * 
     */
    public void setLProjectpk(long value) {
        this.lProjectpk = value;
    }

    /**
     * Gets the value of the szProjectname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzProjectname() {
        return szProjectname;
    }

    /**
     * Sets the value of the szProjectname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzProjectname(String value) {
        this.szProjectname = value;
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
     * Gets the value of the lUserpk property.
     * 
     */
    public long getLUserpk() {
        return lUserpk;
    }

    /**
     * Sets the value of the lUserpk property.
     * 
     */
    public void setLUserpk(long value) {
        this.lUserpk = value;
    }

}
