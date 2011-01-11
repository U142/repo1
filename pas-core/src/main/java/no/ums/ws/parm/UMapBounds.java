
package no.ums.ws.parm;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UMapBounds complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UMapBounds">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="l_bo" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="r_bo" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="u_bo" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="b_bo" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UMapBounds", propOrder = {
    "lBo",
    "rBo",
    "uBo",
    "bBo"
})
public class UMapBounds {

    @XmlElement(name = "l_bo")
    protected double lBo;
    @XmlElement(name = "r_bo")
    protected double rBo;
    @XmlElement(name = "u_bo")
    protected double uBo;
    @XmlElement(name = "b_bo")
    protected double bBo;

    /**
     * Gets the value of the lBo property.
     * 
     */
    public double getLBo() {
        return lBo;
    }

    /**
     * Sets the value of the lBo property.
     * 
     */
    public void setLBo(double value) {
        this.lBo = value;
    }

    /**
     * Gets the value of the rBo property.
     * 
     */
    public double getRBo() {
        return rBo;
    }

    /**
     * Sets the value of the rBo property.
     * 
     */
    public void setRBo(double value) {
        this.rBo = value;
    }

    /**
     * Gets the value of the uBo property.
     * 
     */
    public double getUBo() {
        return uBo;
    }

    /**
     * Sets the value of the uBo property.
     * 
     */
    public void setUBo(double value) {
        this.uBo = value;
    }

    /**
     * Gets the value of the bBo property.
     * 
     */
    public double getBBo() {
        return bBo;
    }

    /**
     * Sets the value of the bBo property.
     * 
     */
    public void setBBo(double value) {
        this.bBo = value;
    }

}
