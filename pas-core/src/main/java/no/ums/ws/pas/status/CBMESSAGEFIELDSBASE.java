
package no.ums.ws.pas.status;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for CB_MESSAGE_FIELDS_BASE complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CB_MESSAGE_FIELDS_BASE">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="l_pk" use="required" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="sz_name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CB_MESSAGE_FIELDS_BASE")
@XmlSeeAlso({
    CBSENDER.class,
    CBMESSAGEPART.class,
    CBORIGINATOR.class,
    CBRISK.class,
    CBMESSAGECONFIRMATION.class,
    CBREACTION.class
})
public class CBMESSAGEFIELDSBASE {

    @XmlAttribute(name = "l_pk", required = true)
    protected long lPk;
    @XmlAttribute(name = "sz_name")
    protected String szName;

    /**
     * Gets the value of the lPk property.
     * 
     */
    public long getLPk() {
        return lPk;
    }

    /**
     * Sets the value of the lPk property.
     * 
     */
    public void setLPk(long value) {
        this.lPk = value;
    }

    /**
     * Gets the value of the szName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzName() {
        return szName;
    }

    /**
     * Sets the value of the szName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzName(String value) {
        this.szName = value;
    }

}
