
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
 *         &lt;element name="logon" type="{http://ums.no/ws/parm/admin/}ULOGONINFO"/>
 *         &lt;element name="obj" type="{http://ums.no/ws/parm/admin/}PAOBJECT" minOccurs="0"/>
 *         &lt;element name="type" type="{http://ums.no/ws/parm/admin/}PASHAPETYPES"/>
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
    "logon",
    "obj",
    "type"
})
@XmlRootElement(name = "ExecPAShapeUpdate")
public class ExecPAShapeUpdate {

    @XmlElement(required = true)
    protected ULOGONINFO logon;
    protected PAOBJECT obj;
    @XmlElement(required = true)
    protected PASHAPETYPES type;

    /**
     * Gets the value of the logon property.
     * 
     * @return
     *     possible object is
     *     {@link ULOGONINFO }
     *     
     */
    public ULOGONINFO getLogon() {
        return logon;
    }

    /**
     * Sets the value of the logon property.
     * 
     * @param value
     *     allowed object is
     *     {@link ULOGONINFO }
     *     
     */
    public void setLogon(ULOGONINFO value) {
        this.logon = value;
    }

    /**
     * Gets the value of the obj property.
     * 
     * @return
     *     possible object is
     *     {@link PAOBJECT }
     *     
     */
    public PAOBJECT getObj() {
        return obj;
    }

    /**
     * Sets the value of the obj property.
     * 
     * @param value
     *     allowed object is
     *     {@link PAOBJECT }
     *     
     */
    public void setObj(PAOBJECT value) {
        this.obj = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link PASHAPETYPES }
     *     
     */
    public PASHAPETYPES getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link PASHAPETYPES }
     *     
     */
    public void setType(PASHAPETYPES value) {
        this.type = value;
    }

}
