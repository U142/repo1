
package no.ums.ws.parm.admin;

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
 *         &lt;element name="logoninfo" type="{http://ums.no/ws/parm/admin/}ULOGONINFO"/>
 *         &lt;element name="user" type="{http://ums.no/ws/parm/admin/}UBBUSER" minOccurs="0"/>
 *         &lt;element name="deptk" type="{http://ums.no/ws/parm/admin/}ArrayOfInt" minOccurs="0"/>
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
    "logoninfo",
    "user",
    "deptk"
})
@XmlRootElement(name = "StoreUser")
public class StoreUser {

    @XmlElement(required = true)
    protected ULOGONINFO logoninfo;
    protected UBBUSER user;
    protected ArrayOfInt deptk;

    /**
     * Gets the value of the logoninfo property.
     * 
     * @return
     *     possible object is
     *     {@link ULOGONINFO }
     *     
     */
    public ULOGONINFO getLogoninfo() {
        return logoninfo;
    }

    /**
     * Sets the value of the logoninfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link ULOGONINFO }
     *     
     */
    public void setLogoninfo(ULOGONINFO value) {
        this.logoninfo = value;
    }

    /**
     * Gets the value of the user property.
     * 
     * @return
     *     possible object is
     *     {@link UBBUSER }
     *     
     */
    public UBBUSER getUser() {
        return user;
    }

    /**
     * Sets the value of the user property.
     * 
     * @param value
     *     allowed object is
     *     {@link UBBUSER }
     *     
     */
    public void setUser(UBBUSER value) {
        this.user = value;
    }

    /**
     * Gets the value of the deptk property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfInt }
     *     
     */
    public ArrayOfInt getDeptk() {
        return deptk;
    }

    /**
     * Sets the value of the deptk property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfInt }
     *     
     */
    public void setDeptk(ArrayOfInt value) {
        this.deptk = value;
    }

}
