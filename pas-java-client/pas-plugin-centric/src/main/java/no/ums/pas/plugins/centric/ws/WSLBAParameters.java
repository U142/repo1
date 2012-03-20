package no.ums.pas.plugins.centric.ws;

import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.pas.core.Variables;
import no.ums.pas.core.ws.WSThread;
import no.ums.pas.core.ws.vars;
import no.ums.ws.common.ULBAPARAMETER;
import no.ums.ws.common.ULOGONINFO;
import no.ums.ws.pas.Pasws;

import javax.xml.namespace.QName;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

/**
 * @author Svein Anfinnsen <sa@ums.no>
 */
public class WSLBAParameters extends WSThread {

    private static final Log log = UmsLog.getLogger(WSCentricStatus.class);

    private String action;
    private ULBAPARAMETER lbapres;
    private ULOGONINFO logon;

    public WSLBAParameters(ActionListener callback, String action) {
        super(callback);

        logon = new ULOGONINFO();
        logon.setLComppk(Variables.getUserInfo().get_comppk());
        logon.setSzCompid(Variables.getUserInfo().get_compid());
        logon.setLDeptpk(Variables.getUserInfo().get_default_deptpk());
        logon.setSzDeptid(Variables.getUserInfo().get_default_dept().get_deptid());
        logon.setLUserpk(new Long(Variables.getUserInfo().get_userpk()));
        logon.setSzUserid(Variables.getUserInfo().get_userid());
        logon.setSzPassword(Variables.getUserInfo().get_passwd());
        logon.setSessionid(Variables.getUserInfo().get_sessionid());

        this.action = action;
    }


    @Override
    public void onDownloadFinished() {
        if(lbapres==null)
        {
            lbapres = new ULBAPARAMETER();
        }

        if(m_callback!=null && lbapres!=null) {
            m_callback.actionPerformed(new ActionEvent(lbapres, ActionEvent.ACTION_PERFORMED, action));
        }
    }

    @Override
    public void call() throws Exception {
        try
        {
            URL wsdl = new URL(vars.WSDL_PAS);
            QName service = new QName("http://ums.no/ws/pas/", "pasws");

            lbapres = new Pasws(wsdl, service).getPaswsSoap12().getCBParameters(logon);
        }
        catch(Exception e)
        {
            log.warn(e.getMessage(), e);
        }
    }

    @Override
    protected String getErrorMessage() {
        return "Error getting LBA parameters";
    }

}