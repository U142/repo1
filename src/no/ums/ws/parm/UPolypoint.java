
package no.ums.ws.parm;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UPolypoint complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UPolypoint">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="xcoord" use="required" type="{http://www.w3.org/2001/XMLSchema}double" />
 *       &lt;attribute name="ycoord" use="required" type="{http://www.w3.org/2001/XMLSchema}double" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UPolypoint")
public class UPolypoint {

    @XmlAttribute(required = true)
    protected double xcoord;
    @XmlAttribute(required = true)
    protected double ycoord;

    /**
     * Gets the value of the xcoord property.
     * 
     */
    public double getXcoord() {
        return xcoord;
    }

    /**
     * Sets the value of the xcoord property.
     * 
     */
    public void setXcoord(double value) {
        this.xcoord = value;
    }

    /**
     * Gets the value of the ycoord property.
     * 
     */
    public double getYcoord() {
        return ycoord;
    }

    /**
     * Sets the value of the ycoord property.
     * 
     */
    public void setYcoord(double value) {
        this.ycoord = value;
    }

}
