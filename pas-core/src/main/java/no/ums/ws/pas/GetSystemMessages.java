
package no.ums.ws.pas;

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
 *         &lt;element name="l" type="{http://ums.no/ws/pas/}ULOGONINFO"/>
 *         &lt;element name="timestamp" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="selectmode" type="{http://ums.no/ws/pas/}UBBNEWSLIST_FILTER"/>
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
    "l",
    "timestamp",
    "selectmode"
})
@XmlRootElement(name = "GetSystemMessages")
public class GetSystemMessages {

    @XmlElement(required = true)
    protected ULOGONINFO l;
    protected long timestamp;
    @XmlElement(required = true)
    protected UBBNEWSLISTFILTER selectmode;

    /**
     * Gets the value of the l property.
     * 
     * @return
     *     possible object is
     *     {@link ULOGONINFO }
     *     
     */
    public ULOGONINFO getL() {
        return l;
    }

    /**
     * Sets the value of the l property.
     * 
     * @param value
     *     allowed object is
     *     {@link ULOGONINFO }
     *     
     */
    public void setL(ULOGONINFO value) {
        this.l = value;
    }

    /**
     * Gets the value of the timestamp property.
     * 
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the value of the timestamp property.
     * 
     */
    public void setTimestamp(long value) {
        this.timestamp = value;
    }

    /**
     * Gets the value of the selectmode property.
     * 
     * @return
     *     possible object is
     *     {@link UBBNEWSLISTFILTER }
     *     
     */
    public UBBNEWSLISTFILTER getSelectmode() {
        return selectmode;
    }

    /**
     * Sets the value of the selectmode property.
     * 
     * @param value
     *     allowed object is
     *     {@link UBBNEWSLISTFILTER }
     *     
     */
    public void setSelectmode(UBBNEWSLISTFILTER value) {
        this.selectmode = value;
    }

}
