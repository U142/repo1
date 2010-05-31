
package no.ums.ws.parm;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ExecResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ExecResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="l_execpk" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sz_function" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="l_projectpk" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="arr_alertresults" type="{http://ums.no/ws/parm/}ArrayOfAlertResultLine" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ExecResponse", propOrder = {
    "lExecpk",
    "szFunction",
    "lProjectpk",
    "arrAlertresults"
})
public class ExecResponse {

    @XmlElement(name = "l_execpk")
    protected String lExecpk;
    @XmlElement(name = "sz_function")
    protected String szFunction;
    @XmlElement(name = "l_projectpk")
    protected String lProjectpk;
    @XmlElement(name = "arr_alertresults")
    protected ArrayOfAlertResultLine arrAlertresults;

    /**
     * Gets the value of the lExecpk property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLExecpk() {
        return lExecpk;
    }

    /**
     * Sets the value of the lExecpk property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLExecpk(String value) {
        this.lExecpk = value;
    }

    /**
     * Gets the value of the szFunction property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzFunction() {
        return szFunction;
    }

    /**
     * Sets the value of the szFunction property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzFunction(String value) {
        this.szFunction = value;
    }

    /**
     * Gets the value of the lProjectpk property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLProjectpk() {
        return lProjectpk;
    }

    /**
     * Sets the value of the lProjectpk property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLProjectpk(String value) {
        this.lProjectpk = value;
    }

    /**
     * Gets the value of the arrAlertresults property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfAlertResultLine }
     *     
     */
    public ArrayOfAlertResultLine getArrAlertresults() {
        return arrAlertresults;
    }

    /**
     * Sets the value of the arrAlertresults property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfAlertResultLine }
     *     
     */
    public void setArrAlertresults(ArrayOfAlertResultLine value) {
        this.arrAlertresults = value;
    }

}
