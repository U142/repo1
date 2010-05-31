
package no.ums.ws.pas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
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
 *       &lt;sequence>
 *         &lt;element name="col_red" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *         &lt;element name="col_green" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *         &lt;element name="col_blue" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *         &lt;element name="col_alpha" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UShape", propOrder = {
    "colRed",
    "colGreen",
    "colBlue",
    "colAlpha"
})
@XmlSeeAlso({
    UPolygon.class,
    UBoundingRect.class,
    UEllipse.class
})
public abstract class UShape {

    @XmlElement(name = "col_red")
    protected float colRed;
    @XmlElement(name = "col_green")
    protected float colGreen;
    @XmlElement(name = "col_blue")
    protected float colBlue;
    @XmlElement(name = "col_alpha")
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
