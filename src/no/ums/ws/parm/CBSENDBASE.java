
package no.ums.ws.parm;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CB_SEND_BASE complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CB_SEND_BASE">
 *   &lt;complexContent>
 *     &lt;extension base="{http://ums.no/ws/parm/}CB_OPERATION_BASE">
 *       &lt;sequence>
 *         &lt;element name="textmessages" type="{http://ums.no/ws/parm/}CB_MESSAGELIST" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="l_sched_utc" use="required" type="{http://www.w3.org/2001/XMLSchema}long" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CB_SEND_BASE", propOrder = {
    "textmessages"
})
@XmlSeeAlso({
    CBALERTUPDATE.class,
    CBALERTPOLYGON.class,
    CBALERTPLMN.class
})
public abstract class CBSENDBASE
    extends CBOPERATIONBASE
{

    protected CBMESSAGELIST textmessages;
    @XmlAttribute(name = "l_sched_utc", required = true)
    protected long lSchedUtc;

    /**
     * Gets the value of the textmessages property.
     * 
     * @return
     *     possible object is
     *     {@link CBMESSAGELIST }
     *     
     */
    public CBMESSAGELIST getTextmessages() {
        return textmessages;
    }

    /**
     * Sets the value of the textmessages property.
     * 
     * @param value
     *     allowed object is
     *     {@link CBMESSAGELIST }
     *     
     */
    public void setTextmessages(CBMESSAGELIST value) {
        this.textmessages = value;
    }

    /**
     * Gets the value of the lSchedUtc property.
     * 
     */
    public long getLSchedUtc() {
        return lSchedUtc;
    }

    /**
     * Sets the value of the lSchedUtc property.
     * 
     */
    public void setLSchedUtc(long value) {
        this.lSchedUtc = value;
    }

}
