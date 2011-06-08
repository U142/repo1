package no.ums.pas.send;

import no.ums.log.Log;
import no.ums.log.UmsLog;

public class ExecAlert {

    private static final Log log = UmsLog.getLogger(ExecAlert.class);

	public ExecAlert() {
		
	}
	
	public void SendAlert(String s_alertpk) {
		//String sUrl = "http://localhost:80/ExternalExec.asmx/ExecAlert"; //"http://pasutv/ExternalExec.asmx"; ///ExecAlert";
		try {
			//new MiniSOAP().post(sUrl);
			//HttpPostForm form = new HttpPostForm(sUrl, "ExecAlert");
			//InputStream is = form.postSoap("");
			
			/*HttpPostForm form = new HttpPostForm(sUrl);
			form.setParameter("l_alertpk", s_alertpk);
			form.setParameter("l_comppk", new Integer(PAS.get_pas().get_userinfo().get_comppk()));
			form.setParameter("l_userpk", new Integer(PAS.get_pas().get_userinfo().get_userpk()));
			form.setParameter("sz_compid", PAS.get_pas().get_userinfo().get_compid());
			form.setParameter("sz_userid", PAS.get_pas().get_userinfo().get_userid());
			form.setParameter("sz_password", PAS.get_pas().get_userinfo().get_passwd());
			InputStream is = form.post();*/
			//URL url = new URL(sUrl);
			//InputStream is = url.openStream();
			
			/*BufferedReader bis= new BufferedReader(new InputStreamReader(is));
			String line = bis.readLine();
			String concated = "";
			while(line != null) {
				concated += line;
				line = bis.readLine();
			}
			log.debug(concated);*/
			
			
		} catch(Exception e) {
			log.debug(e.getMessage(), e);
		}
		
		
	}
		
	
	
}