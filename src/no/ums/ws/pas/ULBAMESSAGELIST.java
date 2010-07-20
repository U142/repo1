
package no.ums.ws.pas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ULBAMESSAGELIST complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ULBAMESSAGELIST">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="list" type="{http://ums.no/ws/pas/}ArrayOfULBAMESSAGE" minOccurs="0"/>
 *         &lt;element name="deleted" type="{http://ums.no/ws/pas/}ArrayOfULBAMESSAGE" minOccurs="0"/>
 *         &lt;element name="n_servertimestamp" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ULBAMESSAGELIST", propOrder = {
    "list",
    "deleted",
    "nServertimestamp"
})
public class ULBAMESSAGELIST {

    protected ArrayOfULBAMESSAGE list;
    protected ArrayOfULBAMESSAGE deleted;
    @XmlElement(name = "n_servertimestamp")
    protected long nServertimestamp;

    /**
     * Gets the value of the list property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfULBAMESSAGE }
     *     
     */
    public ArrayOfULBAMESSAGE getList() {
        return list;
    }

    /**
     * Sets the value of the list property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfULBAMESSAGE }
     *     
     */
    public void setList(ArrayOfULBAMESSAGE value) {
        this.list = value;
    }

    /**
     * Gets the value of the deleted property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfULBAMESSAGE }
     *     
     */
    public ArrayOfULBAMESSAGE getDeleted() {
        return deleted;
    }

    /**
     * Sets the value of the deleted property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfULBAMESSAGE }
     *     
     */
    public void setDeleted(ArrayOfULBAMESSAGE value) {
        this.deleted = value;
    }

    /**
     * Gets the value of the nServertimestamp property.
     * 
     */
    public long getNServertimestamp() {
        return nServertimestamp;
    }

    /**
     * Sets the value of the nServertimestamp property.
     * 
     */
    public void setNServertimestamp(long value) {
        this.nServertimestamp = value;
    }

}
