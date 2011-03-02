package no.ums.pas.ui;

import no.ums.pas.app.PasApplication;
import no.ums.pas.ums.tools.Utils;
import no.ums.ws.common.ULOGONINFO;
import no.ums.ws.common.parm.UPASLOGON;
import org.jdesktop.application.Task;

/**
* @author Ståle Undheim <su@ums.no>
*/
public class LoginTask extends Task<UPASLOGON, Void> {
    interface LoginCallback {

        void success();

        void denied();

        void serverError();
    }

    private final PasApplication application;
    private final LoginCallback loginCallback;
    private final LogonBean logonBean;

    LoginTask(LogonBean logonBean, LoginCallback loginCallback) {
        this(PasApplication.getInstance(), logonBean, loginCallback);
    }

    LoginTask(PasApplication application, LogonBean logonBean, LoginCallback loginCallback) {
        super(application);
        this.application = application;
        this.logonBean = logonBean;
        this.loginCallback = loginCallback;
    }

    @Override
    protected UPASLOGON doInBackground() {
        final ULOGONINFO ulogoninfo = new ULOGONINFO();
        ulogoninfo.setJobid("1");
        ulogoninfo.setLAltservers(0);
        ulogoninfo.setLComppk(0);
        ulogoninfo.setLDeptpk(0);
        ulogoninfo.setLPriserver(0);
        ulogoninfo.setLUserpk(0);
        ulogoninfo.setSzStdcc("");
        ulogoninfo.setLDeptpri(3);
        ulogoninfo.setSzDeptid("");
        ulogoninfo.setSzUserid(logonBean.getUsername());
        ulogoninfo.setSzCompid(logonBean.getCustomer());
        ulogoninfo.setSzPassword(Utils.encrypt(logonBean.getPassword()));

        return application.getPasws().pasLogon(ulogoninfo);
    }

    @Override
    protected void failed(Throwable cause) {
        cause.printStackTrace();
        loginCallback.serverError();
    }

    @Override
    protected void succeeded(UPASLOGON result) {
        if (result.isFGranted()) {
            loginCallback.success();
        } else {
            loginCallback.denied();
        }
    }
}
