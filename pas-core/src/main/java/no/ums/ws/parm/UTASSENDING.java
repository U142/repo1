
package no.ums.ws.parm;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UTASSENDING complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UTASSENDING">
 *   &lt;complexContent>
 *     &lt;extension base="{http://ums.no/ws/parm/}UMAPSENDING">
 *       &lt;sequence>
 *         &lt;element name="countrylist" type="{http://ums.no/ws/parm/}ArrayOfULBACOUNTRY" minOccurs="0"/>
 *         &lt;element name="n_requesttype" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="b_allow_response" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="sz_response_number" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UTASSENDING", propOrder = {
    "countrylist",
    "nRequesttype",
    "bAllowResponse",
    "szResponseNumber"
})
public class UTASSENDING
    extends UMAPSENDING
{

    protected ArrayOfULBACOUNTRY countrylist;
    @XmlElement(name = "n_requesttype")
    protected int nRequesttype;
    @XmlElement(name = "b_allow_response")
    protected boolean bAllowResponse;
    @XmlElement(name = "sz_response_number")
    protected String szResponseNumber;

    /**
     * Gets the value of the countrylist property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfULBACOUNTRY }
     *     
     */
    public ArrayOfULBACOUNTRY getCountrylist() {
        return countrylist;
    }

    /**
     * Sets the value of the countrylist property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfULBACOUNTRY }
     *     
     */
    public void setCountrylist(ArrayOfULBACOUNTRY value) {
        this.countrylist = value;
    }

    /**
     * Gets the value of the nRequesttype property.
     * 
     */
    public int getNRequesttype() {
        return nRequesttype;
    }

    /**
     * Sets the value of the nRequesttype property.
     * 
     */
    public void setNRequesttype(int value) {
        this.nRequesttype = value;
    }

    /**
     * Gets the value of the bAllowResponse property.
     * 
     */
    public boolean isBAllowResponse() {
        return bAllowResponse;
    }

    /**
     * Sets the value of the bAllowResponse property.
     * 
     */
    public void setBAllowResponse(boolean value) {
        this.bAllowResponse = value;
    }

    /**
     * Gets the value of the szResponseNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzResponseNumber() {
        return szResponseNumber;
    }

    /**
     * Sets the value of the szResponseNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzResponseNumber(String value) {
        this.szResponseNumber = value;
    }

}
