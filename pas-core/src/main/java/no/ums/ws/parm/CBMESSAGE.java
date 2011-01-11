
package no.ums.ws.parm;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CB_MESSAGE complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CB_MESSAGE">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="l_channel" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="sz_text" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CB_MESSAGE")
public class CBMESSAGE {

    @XmlAttribute(name = "l_channel", required = true)
    protected int lChannel;
    @XmlAttribute(name = "sz_text")
    protected String szText;

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

    /**
     * Gets the value of the szText property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzText() {
        return szText;
    }

    /**
     * Sets the value of the szText property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzText(String value) {
        this.szText = value;
    }

}
