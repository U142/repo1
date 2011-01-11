
package no.ums.ws.parm;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CB_SENDING_RESPONSE complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CB_SENDING_RESPONSE">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="operation" type="{http://ums.no/ws/parm/}CB_OPERATION"/>
 *         &lt;element name="l_projectpk" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="l_timestamp" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="l_refno" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="l_code" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CB_SENDING_RESPONSE", propOrder = {
    "operation",
    "lProjectpk",
    "lTimestamp",
    "lRefno",
    "lCode"
})
public class CBSENDINGRESPONSE {

    @XmlElement(required = true)
    protected CBOPERATION operation;
    @XmlElement(name = "l_projectpk")
    protected long lProjectpk;
    @XmlElement(name = "l_timestamp")
    protected long lTimestamp;
    @XmlElement(name = "l_refno")
    protected long lRefno;
    @XmlElement(name = "l_code")
    protected long lCode;

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
     * Gets the value of the lCode property.
     * 
     */
    public long getLCode() {
        return lCode;
    }

    /**
     * Sets the value of the lCode property.
     * 
     */
    public void setLCode(long value) {
        this.lCode = value;
    }

}
