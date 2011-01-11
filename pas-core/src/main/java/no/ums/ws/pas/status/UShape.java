
package no.ums.ws.pas.status;

import javax.xml.bind.annotation.*;


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
 *       &lt;attribute name="f_disabled" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="l_disabled_timestamp" use="required" type="{http://www.w3.org/2001/XMLSchema}long" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UShape")
@XmlSeeAlso({
    UPLMN.class,
    UEllipse.class,
    UPolygon.class,
    UBoundingRect.class
})
public abstract class UShape {

    @XmlAttribute(name = "col_red", required = true)
    protected float colRed;
    @XmlAttribute(name = "col_green", required = true)
    protected float colGreen;
    @XmlAttribute(name = "col_blue", required = true)
    protected float colBlue;
    @XmlAttribute(name = "col_alpha", required = true)
    protected float colAlpha;
    @XmlAttribute(name = "f_disabled", required = true)
    protected int fDisabled;
    @XmlAttribute(name = "l_disabled_timestamp", required = true)
    protected long lDisabledTimestamp;

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

    /**
     * Gets the value of the fDisabled property.
     * 
     */
    public int getFDisabled() {
        return fDisabled;
    }

    /**
     * Sets the value of the fDisabled property.
     * 
     */
    public void setFDisabled(int value) {
        this.fDisabled = value;
    }

    /**
     * Gets the value of the lDisabledTimestamp property.
     * 
     */
    public long getLDisabledTimestamp() {
        return lDisabledTimestamp;
    }

    /**
     * Sets the value of the lDisabledTimestamp property.
     * 
     */
    public void setLDisabledTimestamp(long value) {
        this.lDisabledTimestamp = value;
    }

}
