package no.ums.pas.ums.errorhandling;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Iterator;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import no.ums.pas.PAS;
import no.ums.pas.core.dataexchange.MailAccount;
import no.ums.pas.core.dataexchange.MailCtrl;
import no.ums.pas.core.storage.StorageController;

import com.sun.org.apache.xalan.internal.xsltc.runtime.Hashtable;


public class Error implements ActionListener {
	
	public static final int SEVERITY_ERROR = 1;
	public static final int SEVERITY_WARNING = 2;
	public static final int SEVERITY_INFORMATION = 3;
	
	private String error = "";
	private ArrayList<ErrorVO> errorList;
	private static Error errorObject = null;
	private ErrorGUI gui;
	private int index;
	private boolean append = false;
	private boolean b_write_to_logfile = true;
	
	public ErrorVO getError(int n) 
	{
		try
		{
			return errorList.get(n);
		}
		catch(Exception e)
		{
			return null;
		}
	}
	
	private MailAccount account = null;
	
	private Hashtable htErrors;
	
	public static Error getError(){
		if(errorObject == null)
			errorObject = new Error();
		return errorObject;
	}
	
	public Error(boolean lang) {
		b_write_to_logfile = lang;
		errorList = new ArrayList<ErrorVO>();
		gui = new ErrorGUI();
		gui.getBtnNext().addActionListener(this);
		gui.getBtnPrevious().addActionListener(this);
		gui.getBtnSend().addActionListener(this);
		try
		{
			gui.setIconImage(PAS.get_pas().get_appicon().getImage());
		}
		catch(Exception e)
		{
			
		}
		
		htErrors = new Hashtable();
		htErrors.put("1",new String("Polygon har for få punkter, ikke klar til sending."));
		htErrors.put("2",new String("HTTP Error"));		
	}
	
	public Error() {
		errorObject = this;
		errorList = new ArrayList<ErrorVO>();
		gui = new ErrorGUI();
		gui.getBtnNext().addActionListener(this);
		gui.getBtnPrevious().addActionListener(this);
		gui.getBtnSend().addActionListener(this);
		try
		{
			gui.setIconImage(PAS.get_pas().get_appicon().getImage());
		}
		catch(Exception e)
		{
			
		}
		
		htErrors = new Hashtable();
		htErrors.put("1",new String("Polygon har for få punkter, ikke klar til sending."));
		htErrors.put("2",new String("HTTP Error"));
	}
	
	public void addError(String sz_dlgheading, String sz_description, Exception e, int severity){
		error = error.concat(new GregorianCalendar().getTime().toString());
		error = error.concat("\nHeading: " + sz_dlgheading);
		error = error.concat("\nDescription: " + sz_description);
		error = error.concat("\nStackTrace: " + stackTraceToString(e));
		error = error.concat("\n***********************\n");
		writeFile(error);
		errorList.add(new ErrorVO(sz_dlgheading,sz_description,e,severity));
		index = errorList.size()-1;

		try
		{
			gui.setVisible(true);
			
			fillGUI(gui,(ErrorVO)errorList.get(index));
		}
		catch(Exception err)
		{
			
		}
		
		/*if(e.getClass().equals(IOException.class) || e.getClass().equals(SAXException.class)
				|| e.getClass().equals(ZipException.class) || e.getClass().equals(SAXParseException.class)) {
			JOptionPane.showMessageDialog(PAS.get_pas(),"Critical system error, application will exit");
			//PAS.get_pas().get_parmcontroller().getUpdateXML().suspend();
			//System.exit(0);
		}*/
	}
	
	public void addError(String sz_dlgheading, String sz_description, int errorid, int severity){
		error = error.concat(new GregorianCalendar().getTime().toString());
		error = error.concat("\nHeading: " + sz_dlgheading);
		error = error.concat("\nDescription: " + sz_description);
		error = error.concat("\nError ID: " + errorid);
		error = error.concat("\nError message: " + htErrors.get(Integer.toString(errorid)));
		error = error.concat("\n***********************\n");
		writeFile(error);
		errorList.add(new ErrorVO(sz_dlgheading,sz_description,errorid,severity));
		index = errorList.size()-1;
		gui.setVisible(true);
		fillGUI(gui,(ErrorVO)errorList.get(index));
	}
	
	public void addError(String sz_dlgheading, String sz_description, String sz_body, int errorid, int severity){
		error = error.concat(new GregorianCalendar().getTime().toString());
		error = error.concat("\nHeading: " + sz_dlgheading);
		error = error.concat("\nDescription: " + sz_description);
		error = error.concat("\nError ID: " + errorid);
		error = error.concat("\nError message: " + htErrors.get(Integer.toString(errorid)));
		if(sz_body!="")
			error = error.concat("\nError body: " + sz_body);
		error = error.concat("\n***********************\n");
		writeFile(error);
		errorList.add(new ErrorVO(sz_dlgheading,sz_description,sz_body,errorid,severity));
		index = errorList.size()-1;
		gui.setVisible(true);
		fillGUI(gui,(ErrorVO)errorList.get(index));
	}
	
	public boolean addError(InputStream http_reply) {
	    BufferedReader reader = new BufferedReader(new InputStreamReader(http_reply));
	    try {
	    	String sOut = reader.readLine();
	    	String sTotal = "";
	    	while(sOut != null) {
	    		sTotal = sTotal.concat(sOut);
	    		sOut = reader.readLine();
	    	}
		    if(sTotal.contains("S_OK"))
		    	return true;
		    else {
		    	String header = "Server error";
		    	String description = sTotal.substring(sTotal.indexOf("E_FAILED")+8,sTotal.indexOf("Err.description:"));
		    	String body = sTotal.substring(sTotal.indexOf("Err.description:")+17);
		    	Error.getError().addError(header, description, body, -1,Error.SEVERITY_ERROR);
		    }
	    } catch(IOException ioe) {
	    	addError("IOException in Error", ioe.getMessage(), ioe, Error.SEVERITY_ERROR);
	    }
	    return false;
	}
	
	private void writeFile(String error){
		if(b_write_to_logfile)
		{
			try {
				FileWriter errorLog = new FileWriter(new File(StorageController.StorageElements.get_path(StorageController.PATH_HOME_) + "error.log"),append);
				BufferedWriter bf = new BufferedWriter(errorLog);
				PrintWriter writer = new PrintWriter(bf);
				writer.println(error);
				bf.flush();
				writer.close();
				bf.close();
				append = true;
				errorLog = null;
			} catch(IOException io){
				io.printStackTrace();
			}
		}
	}
	
	private String stackTraceToString(Exception e){
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		
		return sw.toString();
	}
	
	private void sendMail(MailAccount account){		
		//MailCtrl mc = new MailCtrl(account.get_helo(),account.get_mailserver(),account.get_port(),account.get_displayname(),account.get_mailaddress(),"mh@ums.no",this,"PAS error",concatErrorList(errorList));
		PAS.pasplugin.onSendErrorMessages(concatErrorList(errorList), account, this);
	}
	private void fillGUI(ErrorGUI gui, ErrorVO e) {
		fillGUI(gui, e, true);
	}
	private void fillGUI(ErrorGUI gui, ErrorVO e, boolean bodyappend){
		gui.setTitle(e.getHeading());
		gui.getLblDescription().setText(e.getDescription());
		
		if(e.getSeverity()==SEVERITY_ERROR){
			gui.getLblDescription().setForeground(Color.RED);
			gui.getLblIcon().setIcon(UIManager.getIcon("OptionPane.errorIcon"));
			gui.getBtnSend().setEnabled(true);
		}
		else if(e.getSeverity()==SEVERITY_WARNING){
			gui.getLblDescription().setForeground(Color.BLUE);
			gui.getLblIcon().setIcon(UIManager.getIcon("OptionPane.warningIcon"));
			gui.getBtnSend().setEnabled(true);
		}
		else if(e.getSeverity()==SEVERITY_INFORMATION){
			gui.getLblDescription().setForeground(Color.GREEN);
			gui.getLblIcon().setIcon(UIManager.getIcon("OptionPane.informationIcon"));
			gui.getBtnSend().setEnabled(false);
		}
		
		if(e.getException() != null)
		{
			try
			{
				gui.getTxaStackTrace().setText(new GregorianCalendar().getTime().toString() + "\n" + stackTraceToString(e.getException()));
			}
			catch(Exception err) { }
		}
		if(e.getErrorid() != -1)
		{
			try
			{
				gui.getTxaStackTrace().setText(new GregorianCalendar().getTime().toString() + "\n" + htErrors.get(Integer.toString(e.getErrorid())).toString());
			}
			catch(Exception err) { }
		}		
		if(!e.getBody().equals(""))
		{
			try
			{
				if(bodyappend)
					gui.getTxaStackTrace().append(e.getBody());
				else
					gui.getTxaStackTrace().setText(e.getBody());
					
			}
			catch(Exception err) { }
		}
		
		gui.getLblCounter().setText(index+1 + " of " + errorList.size());
	}
	
	private void clearGUI(){
		gui.setTitle("");
		gui.getLblIcon().setIcon(null);
		gui.getLblCounter().setText("");
		gui.getTxaStackTrace().setText("");
		gui.getLblCounter().setText("0 of 0");
	}
	
	private String concatErrorList(ArrayList<ErrorVO> errorList){
		String msg = "";
		
		Iterator<ErrorVO> it = errorList.iterator();
		while(it.hasNext()){
			try
			{
				ErrorVO e = (ErrorVO)it.next();
				msg = msg.concat(stackTraceToString(e.exception));
				msg = msg.concat("*******************************************\n");
			}
			catch(Exception e)
			{
				
			}
		}
		return msg;
	}
	
	private ErrorVO nextPrevError(ArrayList<ErrorVO> errorList, int index) {
		return (ErrorVO)errorList.get(index);		
	}
	
	public class ErrorVO {
		private String heading;
		private String description;
		private String body = "";
		private Exception exception;
		private int errorid = -1;
		private int severity;
		
		public ErrorVO(String heading, String description, Exception exception, int severity){
			this.heading = heading;
			this.description = description;
			this.exception = exception;
			this.severity = severity;
		}
		
		public ErrorVO(String heading, String description, int errorid, int severity){
			this.heading = heading;
			this.description = description;
			this.errorid = errorid;
			this.severity = severity;
		}
		
		public ErrorVO(String heading, String description, String body, int errorid, int severity){
			this.heading = heading;
			this.description = description;
			this.body = body;
			this.errorid = errorid;
			this.severity = severity;
		}
		
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		public Exception getException() {
			return exception;
		}
		public void setException(Exception exception) {
			this.exception = exception;
		}
		public String getHeading() {
			return heading;
		}
		public void setHeading(String heading) {
			this.heading = heading;
		}
		public int getSeverity() {
			return severity;
		}
		public void setSeverity(int severity) {
			this.severity = severity;
		}
		public int getErrorid() {
			return errorid;
		}
		public void setErrorid(int errorid) {
			this.errorid = errorid;
		}
		public String getBody() {
			return body;
		}
		public void setBody(String body) {
			this.body = body;
		}
		public void appendBody(String body) {
			this.body += body;
			exception = new Exception(this.body);
			//gui.getLblDescription().setText(this.body);
			fillGUI(gui,(ErrorVO)errorList.get(index));
		}
		public void appendBodyFiltered(String body) {
			if(body!=null)
			{
				if(!this.body.contains(body))
					this.body += body;
				exception = new Exception(this.body);
				//else
				//	this.body = "";
					//this.body += body;
				fillGUI(gui,(ErrorVO)errorList.get(index), false);
			}
		}
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==gui.getBtnNext()){
			if(index<errorList.size()-1){
				index++;
				fillGUI(gui,nextPrevError(errorList,index));
			}
		}
		if(e.getSource()==gui.getBtnPrevious()){
			if(index-1>=0){
				index--;
				fillGUI(gui,nextPrevError(errorList,index));
			}
		}
		if(e.getSource()==gui.getBtnSend()){
			account = PAS.get_pas().get_userinfo().get_mailaccount();
			if(account.get_autodetected())
				sendMail(account);
			else if(account.get_mailaddress() == null || account.get_mailaddress().length()==0){
				MailSetupCtrl msc = new MailSetupCtrl();
				if(msc.editAccount(account))
					sendMail(account);
			}
			else
				sendMail(account);
		}
		if(e.getActionCommand().equals("act_maildelivery_success")){
				JOptionPane.showMessageDialog(gui,"Mail sent successfully!");
				errorList.clear();
				clearGUI();
				gui.setVisible(false);
		}
		if(e.getActionCommand().equals("act_maildelivery_failed")) {
			JOptionPane.showMessageDialog(gui,"Error sending mail, please check your settings");
			MailSetupCtrl msc = new MailSetupCtrl();
			if(msc.editAccount(account))
				sendMail(account);
		}
		
	}
}
