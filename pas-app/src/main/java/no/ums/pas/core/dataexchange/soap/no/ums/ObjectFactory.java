
package no.ums.pas.core.dataexchange.soap.no.ums;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the no.ums package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: no.ums
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ExecEvent }
     * 
     */
    public ExecEvent createExecEvent() {
        return new ExecEvent();
    }

    /**
     * Create an instance of {@link ExecResponse }
     * 
     */
    public ExecResponse createExecResponse() {
        return new ExecResponse();
    }

    /**
     * Create an instance of {@link ExecEventResponse }
     * 
     */
    public ExecEventResponse createExecEventResponse() {
        return new ExecEventResponse();
    }

    /**
     * Create an instance of {@link ArrayOfAlertResultLine }
     * 
     */
    public ArrayOfAlertResultLine createArrayOfAlertResultLine() {
        return new ArrayOfAlertResultLine();
    }

    /**
     * Create an instance of {@link ExecAlertResponse }
     * 
     */
    public ExecAlertResponse createExecAlertResponse() {
        return new ExecAlertResponse();
    }

    /**
     * Create an instance of {@link ExecAlert }
     * 
     */
    public ExecAlert createExecAlert() {
        return new ExecAlert();
    }

    /**
     * Create an instance of {@link ExecEventResponse.ExecEventResult }
     * 
     */
    public ExecEventResponse.ExecEventResult createExecEventResponseExecEventResult() {
        return new ExecEventResponse.ExecEventResult();
    }

    /**
     * Create an instance of {@link AlertResultLine }
     * 
     */
    public AlertResultLine createAlertResultLine() {
        return new AlertResultLine();
    }

}
