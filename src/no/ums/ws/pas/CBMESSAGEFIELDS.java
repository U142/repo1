
package no.ums.ws.pas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CB_MESSAGE_FIELDS complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CB_MESSAGE_FIELDS">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="l_db_timestamp" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="risk_list" type="{http://ums.no/ws/pas/}ArrayOfCB_RISK" minOccurs="0"/>
 *         &lt;element name="reaction_list" type="{http://ums.no/ws/pas/}ArrayOfCB_REACTION" minOccurs="0"/>
 *         &lt;element name="originator_list" type="{http://ums.no/ws/pas/}ArrayOfCB_ORIGINATOR" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CB_MESSAGE_FIELDS", propOrder = {
    "lDbTimestamp",
    "riskList",
    "reactionList",
    "originatorList"
})
public class CBMESSAGEFIELDS {

    @XmlElement(name = "l_db_timestamp")
    protected long lDbTimestamp;
    @XmlElement(name = "risk_list")
    protected ArrayOfCBRISK riskList;
    @XmlElement(name = "reaction_list")
    protected ArrayOfCBREACTION reactionList;
    @XmlElement(name = "originator_list")
    protected ArrayOfCBORIGINATOR originatorList;

    /**
     * Gets the value of the lDbTimestamp property.
     * 
     */
    public long getLDbTimestamp() {
        return lDbTimestamp;
    }

    /**
     * Sets the value of the lDbTimestamp property.
     * 
     */
    public void setLDbTimestamp(long value) {
        this.lDbTimestamp = value;
    }

    /**
     * Gets the value of the riskList property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfCBRISK }
     *     
     */
    public ArrayOfCBRISK getRiskList() {
        return riskList;
    }

    /**
     * Sets the value of the riskList property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfCBRISK }
     *     
     */
    public void setRiskList(ArrayOfCBRISK value) {
        this.riskList = value;
    }

    /**
     * Gets the value of the reactionList property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfCBREACTION }
     *     
     */
    public ArrayOfCBREACTION getReactionList() {
        return reactionList;
    }

    /**
     * Sets the value of the reactionList property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfCBREACTION }
     *     
     */
    public void setReactionList(ArrayOfCBREACTION value) {
        this.reactionList = value;
    }

    /**
     * Gets the value of the originatorList property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfCBORIGINATOR }
     *     
     */
    public ArrayOfCBORIGINATOR getOriginatorList() {
        return originatorList;
    }

    /**
     * Sets the value of the originatorList property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfCBORIGINATOR }
     *     
     */
    public void setOriginatorList(ArrayOfCBORIGINATOR value) {
        this.originatorList = value;
    }

}
