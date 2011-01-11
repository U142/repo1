
package no.ums.ws.pas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UGabSearchResultList complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UGabSearchResultList">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="list" type="{http://ums.no/ws/pas/}ArrayOfUGabResult" minOccurs="0"/>
 *         &lt;element name="sz_errortext" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sz_exceptiontext" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="b_haserror" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UGabSearchResultList", propOrder = {
    "list",
    "szErrortext",
    "szExceptiontext",
    "bHaserror"
})
public class UGabSearchResultList {

    protected ArrayOfUGabResult list;
    @XmlElement(name = "sz_errortext")
    protected String szErrortext;
    @XmlElement(name = "sz_exceptiontext")
    protected String szExceptiontext;
    @XmlElement(name = "b_haserror")
    protected boolean bHaserror;

    /**
     * Gets the value of the list property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfUGabResult }
     *     
     */
    public ArrayOfUGabResult getList() {
        return list;
    }

    /**
     * Sets the value of the list property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfUGabResult }
     *     
     */
    public void setList(ArrayOfUGabResult value) {
        this.list = value;
    }

    /**
     * Gets the value of the szErrortext property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzErrortext() {
        return szErrortext;
    }

    /**
     * Sets the value of the szErrortext property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzErrortext(String value) {
        this.szErrortext = value;
    }

    /**
     * Gets the value of the szExceptiontext property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzExceptiontext() {
        return szExceptiontext;
    }

    /**
     * Sets the value of the szExceptiontext property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzExceptiontext(String value) {
        this.szExceptiontext = value;
    }

    /**
     * Gets the value of the bHaserror property.
     * 
     */
    public boolean isBHaserror() {
        return bHaserror;
    }

    /**
     * Sets the value of the bHaserror property.
     * 
     */
    public void setBHaserror(boolean value) {
        this.bHaserror = value;
    }

}
