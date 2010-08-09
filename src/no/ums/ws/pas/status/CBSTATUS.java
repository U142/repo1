
package no.ums.ws.pas.status;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CB_STATUS complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CB_STATUS">
 *   &lt;complexContent>
 *     &lt;extension base="{http://ums.no/ws/pas/status}ULBASENDING">
 *       &lt;sequence>
 *         &lt;element name="sz_sendingname" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="mdv" type="{http://ums.no/ws/pas/status}MDVSENDINGINFO" minOccurs="0"/>
 *         &lt;element name="shape" type="{http://ums.no/ws/pas/status}UShape" minOccurs="0"/>
 *         &lt;element name="l_created_ts" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="l_started_ts" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="l_channel" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CB_STATUS", propOrder = {
    "szSendingname",
    "mdv",
    "shape",
    "lCreatedTs",
    "lStartedTs",
    "lChannel"
})
public class CBSTATUS
    extends ULBASENDING
{

    @XmlElement(name = "sz_sendingname")
    protected String szSendingname;
    protected MDVSENDINGINFO mdv;
    protected UShape shape;
    @XmlElement(name = "l_created_ts")
    protected long lCreatedTs;
    @XmlElement(name = "l_started_ts")
    protected long lStartedTs;
    @XmlElement(name = "l_channel")
    protected int lChannel;

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

    /**
     * Gets the value of the mdv property.
     * 
     * @return
     *     possible object is
     *     {@link MDVSENDINGINFO }
     *     
     */
    public MDVSENDINGINFO getMdv() {
        return mdv;
    }

    /**
     * Sets the value of the mdv property.
     * 
     * @param value
     *     allowed object is
     *     {@link MDVSENDINGINFO }
     *     
     */
    public void setMdv(MDVSENDINGINFO value) {
        this.mdv = value;
    }

    /**
     * Gets the value of the shape property.
     * 
     * @return
     *     possible object is
     *     {@link UShape }
     *     
     */
    public UShape getShape() {
        return shape;
    }

    /**
     * Sets the value of the shape property.
     * 
     * @param value
     *     allowed object is
     *     {@link UShape }
     *     
     */
    public void setShape(UShape value) {
        this.shape = value;
    }

    /**
     * Gets the value of the lCreatedTs property.
     * 
     */
    public long getLCreatedTs() {
        return lCreatedTs;
    }

    /**
     * Sets the value of the lCreatedTs property.
     * 
     */
    public void setLCreatedTs(long value) {
        this.lCreatedTs = value;
    }

    /**
     * Gets the value of the lStartedTs property.
     * 
     */
    public long getLStartedTs() {
        return lStartedTs;
    }

    /**
     * Sets the value of the lStartedTs property.
     * 
     */
    public void setLStartedTs(long value) {
        this.lStartedTs = value;
    }

    /**
     * Gets the value of the lChannel property.
     * 
     */
    public int getLChannel() {
        return lChannel;
    }

    /**
     * Sets the value of the lChannel property.
     * 
     */
    public void setLChannel(int value) {
        this.lChannel = value;
    }

}
