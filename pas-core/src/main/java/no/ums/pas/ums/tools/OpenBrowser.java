package no.ums.pas.ums.tools;

import no.ums.pas.ums.errorhandling.Error;

import javax.jnlp.BasicService;
import javax.jnlp.ServiceManager;
import javax.jnlp.UnavailableServiceException;
import java.net.URL;

public class OpenBrowser{ 
	final String WIN_ID = "Win"; 
	
	
	public OpenBrowser() {
	}
	public boolean showDocument(URL url) {
		try { 
			// Lookup the javax.jnlp.BasicService object 
			BasicService bs = (BasicService)ServiceManager.lookup("javax.jnlp.BasicService"); 
			// Invoke the showDocument method 
			return bs.showDocument(url); 
		} catch(UnavailableServiceException ue) { 
			// Service is not supported
			Error.getError().addError("OpenBrowser","UnavailableServiceException in showDocument",ue,1);
			return false; 
		} 		
	}
	public OpenBrowser(java.io.File file) { 
		if(isWindowsPlatform()) { 
			try{ 
				Runtime.getRuntime().exec(tryCommand1()+file); 
			}
			catch(Exception exc1) { 
				//Error.getError().addError("OpenBrowser","Exception in OpenBrowser",exc1,1);
				try{ 
					Runtime.getRuntime().exec(tryCommand2()+file);
				}
				catch(Exception exc2) { 
					//Error.getError().addError("OpenBrowser","Exception in OpenBrowser",exc2,1);
					try{ 
						Runtime.getRuntime().exec(tryCommand3()+file);
					}
					catch(Exception exc3) {
						//Error.getError().addError("OpenBrowser","Exception in OpenBrowser",exc3,1);
					/*if(MainEditor.getOpenBrowserHelper()) 
						new OpenBrowserHelper(file);*/ 
					}
				}
			}
		}
		else {
			javax.swing.JOptionPane.showMessageDialog(null, "<html><center>"+ "Not a Windows Operating System.<br>Contact software author for details"+ "</html></center>", "Not Using Windows", javax.swing.JOptionPane.WARNING_MESSAGE); 
		}
	}
	public OpenBrowser(String url) {
		if(isWindowsPlatform()) { 
			try{ 
				Runtime.getRuntime().exec(tryCommand1()+url); 
			}
			catch(Exception exc1) {
				//Error.getError().addError("OpenBrowser","Exception in OpenBrowser",exc1,1);
				try{ 
					Runtime.getRuntime().exec(tryCommand4()+url);
				}
				catch(Exception exc4) { 
					//Error.getError().addError("OpenBrowser","Exception in OpenBrowser",exc2,1);
					try
					{
						Runtime.getRuntime().exec(tryCommand2()+url);						
					}
					catch(Exception exc2)
					{
						try{ 
							Runtime.getRuntime().exec(tryCommand3()+url);
						}
						catch(Exception exc3) {
							//Error.getError().addError("OpenBrowser","Exception in OpenBrowser",exc1,1);
						/*if(MainEditor.getOpenBrowserHelper()) 
							new OpenBrowserHelper(file);*/ 
							try {
								showDocument(new URL(url));
							} catch(Exception e) {
								Error.getError().addError("OpenBrowser","Exception in OpenBrowser",e,1);
							}
						}
					}
				}
			}
		}
		else {
			javax.swing.JOptionPane.showMessageDialog(null, "<html><center>"+ "Not a Windows Operating System.<br>Contact software author for details"+ "</html></center>", "Not Using Windows", javax.swing.JOptionPane.WARNING_MESSAGE); 
		}
	}
	private boolean isWindowsPlatform() { 
		String os = System.getProperty("os.name"); 
		if ( os != null && os.startsWith(WIN_ID)) 
			return true; 
		return false; 
	} 
	private String tryCommand1() { 
		return "C:\\Program Files\\Internet Explorer\\Iexplore.exe -k "; //file://
	}
	private String tryCommand4() {
		return "C:\\Programfiler\\Internet Explorer\\Iexplore.exe -k ";
	}
	private String tryCommand2() {
		return "start rundll32 url.dll,FileProtocolHandler -k"; //file://
	}
	private String tryCommand3() { 
		return ""; //file://
	}
}