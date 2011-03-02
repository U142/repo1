package no.ums.pas.ui;

import no.ums.pas.app.PasApplication;
import no.ums.pas.ums.tools.Utils;
import no.ums.ws.common.ULOGONINFO;
import no.ums.ws.common.parm.UPASLOGON;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public class LoginTaskTest {

    @Test
    public void noop() {}

//    @Test
//    public void testMortenLogin() throws Exception {
//        final ULOGONINFO ulogoninfo = new ULOGONINFO();
//        ulogoninfo.setJobid("1");
//        ulogoninfo.setLAltservers(0);
//        ulogoninfo.setLComppk(0);
//        ulogoninfo.setLDeptpk(0);
//        ulogoninfo.setLPriserver(0);
//        ulogoninfo.setLUserpk(0);
//        ulogoninfo.setSzStdcc("");
//        ulogoninfo.setLDeptpri(3);
//        ulogoninfo.setSzDeptid("");
//        ulogoninfo.setSzCompid("ums");
//        ulogoninfo.setSzUserid("mh");
//        ulogoninfo.setSzPassword(Utils.encrypt("mh123mh123"));
//
//        UPASLOGON upaslogon = new PasApplication().getPasws().pasLogon(ulogoninfo);
//        assertThat(upaslogon, notNullValue());
//        assertThat(upaslogon.isFGranted(), equalTo(true));
//    }

//    @Test
//    public void testLoginTask() throws Exception {
//        LoginTask task = new LoginTask(new PasApplication(), new LogonBean("mh", "ums", "mh123mh123"), new LoginTask.LoginCallback() {
//            @Override
//            public void success() {
//                System.out.println("Success");
//            }
//
//            @Override
//            public void denied() {
//                System.out.println("Denied");
//            }
//
//            @Override
//            public void serverError() {
//                System.out.println("Error");
//            }
//        });
//        task.run();
//        UPASLOGON upaslogon = task.get();
//        assertThat(upaslogon.isFGranted(), equalTo(true));
//    }
}
