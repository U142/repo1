
package no.ums.ws.parm.admin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UBoundingRect complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UBoundingRect">
 *   &lt;complexContent>
 *     &lt;extension base="{http://ums.no/ws/parm/admin/}UShape">
 *       &lt;sequence>
 *         &lt;element name="_left" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="_right" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="_top" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="_bottom" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UBoundingRect", propOrder = {
    "left",
    "right",
    "top",
    "bottom"
})
public class UBoundingRect
    extends UShape
{

    @XmlElement(name = "_left")
    protected double left;
    @XmlElement(name = "_right")
    protected double right;
    @XmlElement(name = "_top")
    protected double top;
    @XmlElement(name = "_bottom")
    protected double bottom;

    /**
     * Gets the value of the left property.
     * 
     */
    public double getLeft() {
        return left;
    }

    /**
     * Sets the value of the left property.
     * 
     */
    public void setLeft(double value) {
        this.left = value;
    }

    /**
     * Gets the value of the right property.
     * 
     */
    public double getRight() {
        return right;
    }

    /**
     * Sets the value of the right property.
     * 
     */
    public void setRight(double value) {
        this.right = value;
    }

    /**
     * Gets the value of the top property.
     * 
     */
    public double getTop() {
        return top;
    }

    /**
     * Sets the value of the top property.
     * 
     */
    public void setTop(double value) {
        this.top = value;
    }

    /**
     * Gets the value of the bottom property.
     * 
     */
    public double getBottom() {
        return bottom;
    }

    /**
     * Sets the value of the bottom property.
     * 
     */
    public void setBottom(double value) {
        this.bottom = value;
    }

}
