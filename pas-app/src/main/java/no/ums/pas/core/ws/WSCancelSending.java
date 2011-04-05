package no.ums.pas.core.ws;

import java.net.URL;

import javax.swing.SwingWorker;
import javax.xml.namespace.QName;

import no.ums.pas.core.Variables;
import no.ums.ws.common.UCancelSending;
import no.ums.ws.common.UCancelSendingResponse;
import no.ums.ws.common.ULOGONINFO;
import no.ums.ws.parm.Parmws;

public class WSCancelSending extends SwingWorker {
	public interface ICallback
	{
		public void onFinished(UCancelSendingResponse response);
	}

	private long refno;
	private ICallback callback;
	private UCancelSendingResponse response = new UCancelSendingResponse();
	
	public long getRefno() {
		return refno;
	}
	public void setRefno(long refno) {
		this.refno = refno;
	}
	public ICallback getCallback() {
		return callback;
	}
	public void setCallback(ICallback callback) {
		this.callback = callback;
	}
	public WSCancelSending(long refno, ICallback callback)
	{
		setRefno(refno);
		setCallback(callback);
		response.setLRefno(-1);
		response.setResponse(UCancelSending.ERROR);
	}
	
	@Override
	protected Object doInBackground() throws Exception {
		try
		{
			ULOGONINFO logon = new ULOGONINFO();
			WSFillLogoninfo.fill(logon, Variables.getUserInfo());
			URL wsdl = new java.net.URL(vars.WSDL_EXTERNALEXEC);
			QName service = new QName("http://ums.no/ws/parm/", "parmws");
			response = new Parmws(wsdl, service).getParmwsSoap12().cancelSending(logon, getRefno());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return Boolean.TRUE;
	}

	@Override
	protected void done() {
		getCallback().onFinished(response);
		super.done();
	}

	
	
}
