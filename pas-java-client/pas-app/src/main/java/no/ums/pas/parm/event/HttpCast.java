package no.ums.pas.parm.event;

//import no.ums.pas.core.dataexchange.HttpPostForm;



public class HttpCast {
	public HttpCast(){ 
		
	}
	/*public InputStream send(String url, Hashtable table)throws ParmException{
		InputStream in;
		HttpPostForm webServer;
		Set liste = table.keySet();
		
		try {
			webServer = new HttpPostForm(url);
			
			Iterator it = liste.iterator();
			while(it.hasNext()){
				String temp = it.next().toString();
				webServer.setParameter(temp,table.get(temp));
			}
			in = webServer.post();
			
		} catch (FileNotFoundException fnfe) {
			Error.getError().addError("HttpCast","FileNotFoundException in send",fnfe,1);
			throw new ParmException("File not found!");
		} catch (IOException ioe) {
			Error.getError().addError("HttpCast","IOException in send",ioe,1);
			throw new ParmException(ioe.getMessage());
		} catch (Exception e) {
			Error.getError().addError("HttpCast","Exception in send",e,1);
			log.warn(e.getMessage(), e);
			throw new ParmException(e.getMessage());
		}
		return in;
	}*/
	public static void main(String[]args){
		new HttpCast();
	}
}
