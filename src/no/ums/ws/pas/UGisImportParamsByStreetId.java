
package no.ums.ws.pas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UGisImportParamsByStreetId complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UGisImportParamsByStreetId">
 *   &lt;complexContent>
 *     &lt;extension base="{http://ums.no/ws/pas/}UGisImportParams">
 *       &lt;sequence>
 *         &lt;element name="COL_STREETID" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="COL_HOUSENO" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="COL_LETTER" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="DETAIL_THRESHOLD_LINES" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UGisImportParamsByStreetId", propOrder = {
    "colstreetid",
    "colhouseno",
    "colletter",
    "detailthresholdlines"
})
public class UGisImportParamsByStreetId
    extends UGisImportParams
{

    @XmlElement(name = "COL_STREETID")
    protected int colstreetid;
    @XmlElement(name = "COL_HOUSENO")
    protected int colhouseno;
    @XmlElement(name = "COL_LETTER")
    protected int colletter;
    @XmlElement(name = "DETAIL_THRESHOLD_LINES")
    protected int detailthresholdlines;

    /**
     * Gets the value of the colstreetid property.
     * 
     */
    public int getCOLSTREETID() {
        return colstreetid;
    }

    /**
     * Sets the value of the colstreetid property.
     * 
     */
    public void setCOLSTREETID(int value) {
        this.colstreetid = value;
    }

    /**
     * Gets the value of the colhouseno property.
     * 
     */
    public int getCOLHOUSENO() {
        return colhouseno;
    }

    /**
     * Sets the value of the colhouseno property.
     * 
     */
    public void setCOLHOUSENO(int value) {
        this.colhouseno = value;
    }

    /**
     * Gets the value of the colletter property.
     * 
     */
    public int getCOLLETTER() {
        return colletter;
    }

    /**
     * Sets the value of the colletter property.
     * 
     */
    public void setCOLLETTER(int value) {
        this.colletter = value;
    }

    /**
     * Gets the value of the detailthresholdlines property.
     * 
     */
    public int getDETAILTHRESHOLDLINES() {
        return detailthresholdlines;
    }

    /**
     * Sets the value of the detailthresholdlines property.
     * 
     */
    public void setDETAILTHRESHOLDLINES(int value) {
        this.detailthresholdlines = value;
    }

}
