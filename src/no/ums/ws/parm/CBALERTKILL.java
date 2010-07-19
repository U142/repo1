
package no.ums.ws.parm;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CB_ALERT_KILL complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CB_ALERT_KILL">
 *   &lt;complexContent>
 *     &lt;extension base="{http://ums.no/ws/parm/}CB_OPERATION_BASE">
 *       &lt;attribute name="l_sched_utc" use="required" type="{http://www.w3.org/2001/XMLSchema}long" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CB_ALERT_KILL")
public class CBALERTKILL
    extends CBOPERATIONBASE
{

    @XmlAttribute(name = "l_sched_utc", required = true)
    protected long lSchedUtc;

    /**
     * Gets the value of the lSchedUtc property.
     * 
     */
    public long getLSchedUtc() {
        return lSchedUtc;
    }

    /**
     * Sets the value of the lSchedUtc property.
     * 
     */
    public void setLSchedUtc(long value) {
        this.lSchedUtc = value;
    }

}
