
package no.ums.ws.pas.status;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UShape complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UShape">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="col_red" use="required" type="{http://www.w3.org/2001/XMLSchema}float" />
 *       &lt;attribute name="col_green" use="required" type="{http://www.w3.org/2001/XMLSchema}float" />
 *       &lt;attribute name="col_blue" use="required" type="{http://www.w3.org/2001/XMLSchema}float" />
 *       &lt;attribute name="col_alpha" use="required" type="{http://www.w3.org/2001/XMLSchema}float" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UShape")
public abstract class UShape {

    @XmlAttribute(name = "col_red", required = true)
    protected float colRed;
    @XmlAttribute(name = "col_green", required = true)
    protected float colGreen;
    @XmlAttribute(name = "col_blue", required = true)
    protected float colBlue;
    @XmlAttribute(name = "col_alpha", required = true)
    protected float colAlpha;

    /**
     * Gets the value of the colRed property.
     * 
     */
    public float getColRed() {
        return colRed;
    }

    /**
     * Sets the value of the colRed property.
     * 
     */
    public void setColRed(float value) {
        this.colRed = value;
    }

    /**
     * Gets the value of the colGreen property.
     * 
     */
    public float getColGreen() {
        return colGreen;
    }

    /**
     * Sets the value of the colGreen property.
     * 
     */
    public void setColGreen(float value) {
        this.colGreen = value;
    }

    /**
     * Gets the value of the colBlue property.
     * 
     */
    public float getColBlue() {
        return colBlue;
    }

    /**
     * Sets the value of the colBlue property.
     * 
     */
    public void setColBlue(float value) {
        this.colBlue = value;
    }

    /**
     * Gets the value of the colAlpha property.
     * 
     */
    public float getColAlpha() {
        return colAlpha;
    }

    /**
     * Sets the value of the colAlpha property.
     * 
     */
    public void setColAlpha(float value) {
        this.colAlpha = value;
    }

}
