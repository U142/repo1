
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
 *         &lt;element name="n_refno" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="n_code" type="{http://www.w3.org/2001/XMLSchema}long"/>
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
    "nRefno",
    "nCode"
})
public class CBSENDINGRESPONSE {

    @XmlElement(required = true)
    protected CBOPERATION operation;
    @XmlElement(name = "n_refno")
    protected long nRefno;
    @XmlElement(name = "n_code")
    protected long nCode;

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
     * Gets the value of the nRefno property.
     * 
     */
    public long getNRefno() {
        return nRefno;
    }

    /**
     * Sets the value of the nRefno property.
     * 
     */
    public void setNRefno(long value) {
        this.nRefno = value;
    }

    /**
     * Gets the value of the nCode property.
     * 
     */
    public long getNCode() {
        return nCode;
    }

    /**
     * Sets the value of the nCode property.
     * 
     */
    public void setNCode(long value) {
        this.nCode = value;
    }

}
