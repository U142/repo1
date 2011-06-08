package no.ums.pas.core.dataexchange;

import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.pas.core.mainui.LoadingPanel;
import no.ums.pas.maps.defines.Navigation;
import no.ums.pas.ums.errorhandling.Error;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXParseException;

import javax.swing.ProgressMonitor;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.Dimension;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.util.zip.ZipInputStream;

public class HTTPReq {

    private static final Log log = UmsLog.getLogger(HTTPReq.class);
	
	public static final String PAS_OPERATION_ = "OP:";
	public static final String PAS_OPERATION_DBCALL_ = "DB";
	public static final String PAS_OPERATION_REC_COUNT_ = "RC";
	public static final String PAS_OPERATION_WRITE_ = "WR";
	public static final String PAS_OPERATION_ZIP_ = "ZI";
	public static final String PAS_PERCENT_ = "PE:";
	public static final String OPERATION_STREAM_START_ = "ST";
	public static final String PAS_FILESIZE_ = "FS:";
	public static final String PAS_FILESIZE_ZIP_ = "ZS:";
	public static final String PAS_TIMER_DBCALL_ = "TD";
	public static final String PAS_TIMER_WRITE_ = "TW";	
	
	
	public String get_operation_description(String sz_op)
	{
		String sz_ret;
		if(sz_op.equals(PAS_OPERATION_DBCALL_))
			sz_ret = "ServerStatus - Executing Database call";
		else if(sz_op.equals(PAS_OPERATION_REC_COUNT_))
			sz_ret = "ServerStatus - Counting records";
		else if(sz_op.equals(PAS_OPERATION_WRITE_))
			sz_ret = "ServerStatus - Writing and Zipping data";
		else if(sz_op.equals(PAS_OPERATION_ZIP_))
			sz_ret = "ServerStatus - Zipping data";
		else if(sz_op.equals(OPERATION_STREAM_START_))
			sz_ret = "ServerStatus - Streaming data to client";
		else
			sz_ret = "ServerStatus - Unknown operation";
		//m_pas.add_event(sz_ret);
		return sz_ret;
	}
	
	
	DocumentBuilderFactory m_factory;
	private InputStream m_current_inputstream = null;
	private ZipInputStream m_current_zipinputstream = null;
	HeaderStruct m_header;
	//PAS m_pas;
	String m_sz_lasterror;
	ProgressMonitor m_monitor;
	String m_sz_xmlstring;
	public boolean m_b_interrupted = false;
	boolean m_b_running = false;
	private String m_sz_sitename;
	public String get_sitename() { return m_sz_sitename; }
	private Navigation m_navigation;
	public Navigation get_navigation() { return m_navigation; }
	boolean m_b_session_reconnect = false;
	public boolean session_reconnected() { return m_b_session_reconnect; }
	
	public void set_interrupted() { 
		try {
			m_current_inputstream.close();
		} catch(IOException e) {
			Error.getError().addError("HTTPReq","IOException in set_interrupted",e,1);
		}
		m_b_interrupted = true;
	}
	public boolean get_interrupted() { return m_b_interrupted; }
	public boolean get_running() { return m_b_running; }
	private void set_running(boolean b_run) { m_b_running = b_run; }

	public class HeaderStruct {
		String m_sz_maptime;
		String m_sz_lbo, m_sz_rbo, m_sz_ubo, m_sz_bbo;
		String m_sz_status;
		public HeaderStruct() {
		}
		void setMaptime(String sz_maptime) { m_sz_maptime = new String(sz_maptime); }
		public void setBounds(String sz_lbo, String sz_rbo, String sz_ubo, String sz_bbo)
		{
			m_sz_lbo = new String(sz_lbo);
			m_sz_rbo = new String(sz_rbo);
			m_sz_ubo = new String(sz_ubo);
			m_sz_bbo = new String(sz_bbo);
			get_navigation().setHeaderBounds(getLBO(), getRBO(), getUBO(), getBBO());
			//setHeaderBounds(getLBO(), getRBO(), getUBO(), getBBO());
			//Variables.NAVIGATION.set
			
		}
		void setStatus(String sz_status) { m_sz_status = new String(sz_status); }
		String getMapTime() { return m_sz_maptime; }
		String getLBO() { return m_sz_lbo; }
		String getRBO() { return m_sz_rbo; }
		String getUBO() { return m_sz_ubo; }
		String getBBO() { return m_sz_bbo; }
	}	
	/*public void setHeaderBounds(String l, String r, String u, String b) {
		Variables.NAVIGATION.setHeaderBounds(l, r, u, b);
	}*/
	public HTTPReq(String sz_sitename)
	{
		m_navigation = null;
		m_sz_sitename = sz_sitename;
		m_factory = DocumentBuilderFactory.newInstance();
		m_header = new HeaderStruct();
		set_last_error("");
	}
	public HTTPReq(String sz_sitename, Navigation nav)
	{
		this(sz_sitename);
		m_navigation = nav;
	}	
	public HeaderStruct get_httpheader()
	{
		return m_header;	
	}
	public InputStream get_current_inputstream()
	{
		return m_current_inputstream;	
	}	
	public void close_inputstream()
	{
		try {
			get_current_inputstream().close();
		}
		catch(Exception e)
		{
			Error.getError().addError("HTTPReq","Exception in close_inputstream",e,1);
		}
	}
	
	public DocumentBuilder createNewDocumentBuilder()
	{
		try{
			DocumentBuilder builder = m_factory.newDocumentBuilder();
			return builder;
		}
		catch(Exception e) { 
			Error.getError().addError("HTTPReq","Exception in createNewDocument",e,1);
			set_last_error("ERROR: createNewDocumentBuilder()");
		}
		return null;
	}
	
	public synchronized Document getDocument( String urlString, boolean b_setheaders, boolean b_zip, LoadingPanel progress ) 
	{
	   //DocumentBuilder db = createNewDocumentBuilder();
	   /*if(m_current_inputstream!=null)
	   {
		   	try {
			   	m_current_inputstream.close();
			} catch(IOException e) {}
	   }*/
	   try {
	     URL url = new URL( urlString );
	     
	     try {
	     	if(progress!=null)
	     	{
	     		progress.start_progress(0, "Connecting to server...");
	     		progress.inc_currentoverallitem(2);
	     	}
	       URLConnection URLconnection;// = url.openConnection ( ) ;
	       HttpURLConnection httpConnection = null;
	       int responseCode;
	       if(b_setheaders)
	       {
	       		URLconnection = url.openConnection ( ) ;
	 	        httpConnection = (HttpURLConnection)URLconnection;
	       		get_response_headers(httpConnection);
	 	        responseCode = httpConnection.getResponseCode ( ) ;
	       }
	       else if(b_zip)
	       {
	       		responseCode = HttpURLConnection.HTTP_OK;
	       		URLconnection = url.openConnection ( ) ;
	       		httpConnection = (HttpURLConnection)URLconnection;
	       		try {
	       			m_current_inputstream = URLconnection.getInputStream();
	       		} catch(Exception e) {
	       			Error.getError().addError("Error in getDocument()", "Could not open document", e, 1);
	       			return null;
	       		}
	       }
	       else
	       {
	       		URLconnection = url.openConnection ( ) ;
	       		httpConnection = (HttpURLConnection)URLconnection;
	       		httpConnection.setReadTimeout(30000);
	       		httpConnection.setConnectTimeout(30000);
	       		responseCode = httpConnection.getResponseCode ( ) ;
	       		
	       }
	       get_common_headers(httpConnection);
       
	       if ( responseCode == HttpURLConnection.HTTP_OK) 
	       {
	       		if(b_zip)
	       		{
	       			create_zipstream(progress);
	       		
	       		} //not zipped
	       		else {
	       			try {
	       				m_current_inputstream = httpConnection.getInputStream ( ) ;
	       			} catch(Exception e) {
	       				
	       			}
		       		//set_last_error("Got input stream");
	       		}
	        } 
	        else {
	            set_last_error( "HTTP connection response != HTTP_OK" );
	        } 
	     } catch ( IOException e ) { 
	          e.printStackTrace ( ) ;
	          //PAS.get_pas().add_event("getDoc()", e);
	          set_last_error("IOException");
	          Error.getError().addError("HTTPReq","IOException in getDocument",e,1);
	     } // Catch
	     catch(Exception e) {
	    	 Error.getError().addError("HTTPReq", "Exception caught", e, 1);
	     }
	  } catch ( MalformedURLException e ) {  
	      e.printStackTrace ( ) ;
          //PAS.get_pas().add_event("getDoc()", e);
              set_last_error("MalformedURLException");
              Error.getError().addError("HTTPReq","MalformedURLException in getDocument",e,1);
	  } // Catch
     catch(Exception e) {
    	 Error.getError().addError("HTTPReq", "Exception caught", e, 1);
     }
	  return null;
	} // getDocument
	
	private synchronized void create_zipstream(LoadingPanel progress) {
			try {
   				int n_file_size = 0;
   				int n_zipped_size = 1; //init size if we don't get the actual filesize in return
   				if(progress!=null)
   				{
						progress.set_totalitems(0, "ServerStatus - working...");
   				}

   				int n_buffer_size = n_zipped_size;
   				ByteBuffer sz_zipfile = null; //ByteBuffer.allocate(n_buffer_size);
   				int n_read = 1;
   				int n_offset = 0;
   				String sz_temp = "";
   				String sz_buffer = "";
   				boolean b_stream_started = false;
   				int n_prev_percent_found = 0;
   				int n_prev_operation_found = 0;
   				int n_current_read = 0;
   				
   				while(n_read != -1)
   				{
   					if(get_interrupted()) {
   						break;
   					}
   					byte[] temp = new byte[128];
   					n_read = 0;
   					try {
   						n_read = m_current_inputstream.read(temp);
   						if(n_read > 0)
   						{
   							n_current_read += n_read;
   							if(b_stream_started)
   							{
       							/*
       							 * just in case. If the stream contains more data than expected (expected is
       							 * zipped filesize), then double the buffersize before continuing.
       							 */
       							if(n_read + n_offset > n_buffer_size)
       							{
       								n_buffer_size += n_read + 1;
       								sz_zipfile = ByteBuffer.allocate(n_buffer_size).put(sz_zipfile.array(), 0, sz_zipfile.position()); //put(sz_zipfile, 0, sz_zipfile.position());
       								//m_pas.add_event("BufferSize exceeded, new BufferSize set to "+n_buffer_size+"B");
       							}	       							
       							sz_zipfile.put(temp, 0, n_read);//, n_offset, n_read);
           						if(n_read > 0)
           						{
           							n_offset += n_read;
           							if(progress!=null) {
	           							progress.set_currentitem(n_offset);
	           							progress.set_currentoverallitem((((double)n_offset/n_zipped_size) * 0.3 * 100));
           							}
           						}		       							
   							}
   							if(!b_stream_started)
   							{
   								sz_temp = new String(new String(temp).substring(0, n_read));
   								sz_buffer += sz_temp;
   								
   								/*check percent*/
   								int n_temp = -1;
   								int n_found = n_prev_percent_found;
   								while(n_found >= 0)
   								{
   									n_found = sz_buffer.indexOf(PAS_PERCENT_, n_found + 1);
   									if(n_found >= 0)
   									{
   										n_temp = n_found;
   									} else {
   										n_found = n_temp;
   										break;
   									}
   								}
   								if(n_found != -1)
   								{
   									//find \n
   									int n_found_n = sz_buffer.indexOf("\n", n_found);
   									if(n_found_n >= 0)
   									{
   										int n_percentage = new Integer(sz_buffer.substring(n_found + PAS_PERCENT_.length(), n_found_n)).intValue();
   										if(progress!=null) {
       										progress.set_currentitem(n_percentage);
       										progress.set_currentoverallitem(((double)n_percentage * 0.3));
   										}
   										n_prev_percent_found = n_found_n;
   									}
   								}
   								/*check operation*/
   								n_found = sz_buffer.indexOf(PAS_OPERATION_, n_prev_operation_found);
   								if(n_found != -1) //operation found
   								{
   									int n_found_n = sz_buffer.indexOf("\n", n_found);
   									if(n_found_n >= 0)
   									{
   										n_prev_operation_found = n_found_n;
   										String sz_op = sz_buffer.substring(n_found + PAS_OPERATION_.length(), n_found_n);
   										String sz_operation_description = get_operation_description(sz_op);
   										if(sz_op.equals(PAS_OPERATION_WRITE_))
   										{
   											if(progress!=null)
   												progress.set_totalitems(100, "");
   				   							//progress.lock_overallstage();
   										}
   										else if(sz_op.equals(PAS_OPERATION_DBCALL_)) {
   								     		if(progress!=null)
   								     			progress.inc_currentoverallitem(4);
   										}
   										else if(sz_op.equals(PAS_OPERATION_REC_COUNT_)) {
   								     		if(progress!=null)
   								     			progress.inc_currentoverallitem(4);
   										}
   										if(progress!=null)
   											progress.set_text(sz_operation_description);
   									}
   								}
   								/*check for filesize*/
   								if(n_file_size == 0)
   								{
       								n_found = sz_buffer.indexOf(PAS_FILESIZE_, 0);
       								if(n_found != -1) //filesize found
       								{
       									int n_found_n = sz_buffer.indexOf("\n", n_found);
       									if(n_found_n >= 0)
       									{
       										Integer n_filesize = new Integer(sz_buffer.substring(n_found + PAS_FILESIZE_.length(), n_found_n));
       										n_file_size = n_filesize.intValue();
       										//m_pas.add_event("Original filesize:" + n_filesize);
       									}
       								}
   								}
   								/*check for zip-filesize*/
   								if(n_zipped_size == 1)
   								{
       								n_found = sz_buffer.indexOf(PAS_FILESIZE_ZIP_, 0);
       								if(n_found != -1) //zip filesize found
       								{
       									int n_found_n = sz_buffer.indexOf("\n", n_found);
       									if(n_found_n >= 0)
       									{
       										Integer n_zipfilesize = new Integer(sz_buffer.substring(n_found + PAS_FILESIZE_ZIP_.length(), n_found_n));
       										n_zipped_size = n_zipfilesize.intValue();
       										n_buffer_size = n_zipped_size + 1;
       										//m_pas.add_event("Zipped filesize:" + n_zipped_size);
       										if(progress!=null)
       											progress.set_totalitems(n_zipped_size, "Downloading stream...");
       									}
       								}
   								}

   								int n_streamstart_index = sz_buffer.indexOf(PAS_OPERATION_ + OPERATION_STREAM_START_ + "\n\n", 0);
   								if(n_streamstart_index >= 0)
   								{

   									if(progress!=null)
   										progress.lock_overallstage();
   									b_stream_started = true;
   									
   									/*int n_streamstart = n_streamstart_index + 7;
   									int n_length = sz_buffer.length() - n_streamstart;
   									ByteBuffer buff = ByteBuffer.allocate(n_length);
   									//buff.put(sz_buffer.getBytes(), n_streamstart, n_length);
   									buff.put(sz_buffer.getBytes(), n_streamstart, n_length);*/
   									int n_streamstart = sz_buffer.length() - n_streamstart_index;

   									//int n_temp1 = sz_buffer.length() - (sz_buffer.length() - sz_temp.length());
   									int n_temp1 = sz_temp.length();
   									int n_temp2 = sz_buffer.length() - n_streamstart_index;
   									n_streamstart = sz_temp.length() - n_temp2 + new String(PAS_OPERATION_ + OPERATION_STREAM_START_ + "\n\n").length();
   									//working
   									//n_streamstart = sz_temp.lastIndexOf(PAS_OPERATION_ + OPERATION_STREAM_START_ + "\n\n") + 7;// + new String(PAS_OPERATION_ + OPERATION_STREAM_START_ + "\n").length();
   									//n_streamstart = n_streamstart_index;
   									int n_length = n_read - n_streamstart;
   									//int n_length = sz_buffer.length() - n_streamstart_index - 7;
   									ByteBuffer buff = ByteBuffer.allocate(n_length);//temp.length - n_streamstart);
   									//buff.put(sz_buffer.getBytes(), n_streamstart_index + 7, n_length);
   									buff.put(temp, n_streamstart, n_length);
   									//byte[] sz_show = new byte[n_length];
   									//buff.get(sz_show);
   									//m_pas.add_event("bufferstart: ");
   									
   									if(n_zipped_size==1)
   										n_zipped_size = n_length;
       								if(sz_zipfile==null)
       								{
       									sz_zipfile = ByteBuffer.allocate(n_zipped_size);
       								}
   									sz_zipfile.put(buff.array(), 0, n_length);
   									n_buffer_size = n_zipped_size;
   									n_offset = n_length;
   								}
   							}
   							
   						}
   					}
   					catch(Exception e) { 
   						//PAS.get_pas().add_event("getDoc()", e);
   						log.debug(e.getMessage());
   						e.printStackTrace();
   						Error.getError().addError("HTTPReq","Exception in create_zipstream",e,1);
   						break;
   					}
   				}
   				ByteArrayInputStream zip_data = new ByteArrayInputStream(sz_zipfile.array(), 0, sz_zipfile.position()); //n_offset);
   				m_current_zipinputstream = new ZipInputStream(zip_data);
   				//PAS.get_pas().add_event(m_current_zipinputstream.toString(), null);

   				//InputStream in = new BufferedInputStream(new ProgressMonitorInputStream(m_pas, "Downloading...", "", 0, 1000));
   			} catch(Exception e) { 
   				log.debug(e.getMessage());
   				e.printStackTrace();
   				Error.getError().addError("HTTPReq","Exception in create_zipstream",e,1);
					//PAS.get_pas().add_event("getDoc()", e);
   			}		
	}
	
	public synchronized Document get_xml(InputStream is, boolean b_zipped, LoadingPanel progress) {
		set_running(true);
		Document doc = null;
		try {
			/*if(!b_zipped)
				m_current_inputstream = is;
			else*/
			m_current_inputstream = is;
			if(b_zipped) {
				//m_current_zipinputstream = (ZipInputStream)is;
				//log.debug("create_zipstream");
				create_zipstream(progress);
				//log.debug("zipstream created");
			}
		}
		catch(Exception e) {
			log.debug(e.getMessage());
			e.printStackTrace();
			Error.getError().addError("HTTPReq","Exception in get_xml",e,1);
		}
		DocumentBuilder db = createNewDocumentBuilder();
		try {
			//Document doc;
			if(b_zipped) {
				try {
					m_current_zipinputstream.getNextEntry();
				} catch(Exception e) {
					doc = null;
					log.debug(e.getMessage());
					e.printStackTrace();
					Error.getError().addError("HTTPReq","Exception in get_xml",e,1);
				}
				try {
					doc = db.parse(m_current_zipinputstream);
				} catch(Exception e) {
					doc = null;
				}
			}
			else {
				try {
					doc = db.parse(m_current_inputstream);
				} catch(Exception e) {
					doc = null;
				}
			}
			close_inputstream();
			return doc;
		}
		catch(Exception e) {
			log.debug(e.getMessage());
			e.printStackTrace();
			Error.getError().addError("HTTPReq","Exception in get_xml",e,1);
		}
		set_running(false);
		return doc;
	}
	
	public synchronized Document get_xml(String sz_url, boolean b_zipped, LoadingPanel progress)
	{
		set_running(true);
		Document doc = null;
		try {
			doc = getDocument(sz_url, false, b_zipped, progress);
			//if(doc == null)
			//	return null;
		}
		catch(Exception e)
		{
			set_last_error("ERROR: Could not get document");
			set_running(false);
			Error.getError().addError("HTTPReq","Exception in get_xml",e,1);
			return null;
		}
		try { //unzip
			if(b_zipped)
			{
				
			}
		} catch(Exception e) { 
			//m_pas.add_event("Zip error: " + e.getMessage()); 
			set_running(false);
			Error.getError().addError("HTTPReq","Exception in get_xml",e,1);
			return null;
		}
		DocumentBuilder db = createNewDocumentBuilder();
		try {
			//Document doc = null;
			if(b_zipped) {
				try {
					if(get_interrupted()) {
						set_running(false);
						return null;
					}
					//m_pas.add_event("Zip file extracted to - " + m_current_zipinputstream.getNextEntry().getName());
				} catch(Exception e) { 
					//m_pas.add_event("Zip file not found: " + e.getMessage());
					Error.getError().addError("HTTPReq","Exception in get_xml",e,1);
				}
				try {
					//log.debug();
					m_current_zipinputstream.getNextEntry().getName();
					doc = db.parse(m_current_zipinputstream);
				} catch(Exception e) {
					//PAS.get_pas().add_event("HTTPReq.Exception", e);
					Error.getError().addError("HTTPReq","Exception in get_xml",e,1);
				}
			}
			else
			{
				if(m_current_inputstream!=null) {
					try {
						doc = db.parse(m_current_inputstream);
					} catch(SAXParseException e) {
						doc = null;
					}
				}
				else
					doc = null;
			}
			if(m_current_inputstream!=null)
				close_inputstream();
			set_running(false);
			return doc;
		}
		/*catch(SAXParseException e)
		{
			//PAS.get_pas().add_event("HTTPReq.SAXParseException", e);
			//MsgBox message = new MsgBox(new Frame("") , e.getMessage() + " line:" + e.getLineNumber(), true);
			//m_pas.add_event(e.getMessage() + " line:" + e.getLineNumber());
			set_last_error(get_last_error());
			Error.getError().addError("HTTPReq","SAXParseException in get_xml",e,1);
		}*/
		catch(Exception e) {
			//PAS.get_pas().add_event("HTTPReq.Exception", e);
			//MsgBox message = new MsgBox(new Frame("") , e.getMessage(), true);
			//m_pas.add_event(e.getMessage());
			Error.getError().addError("HTTPReq","Exception in get_xml",e,1);
			set_last_error(get_last_error());
			
			//close_inputstream();
		}
		set_running(false);		
		return doc;
	}
	
	public void set_last_error(String sz_error) { m_sz_lasterror = sz_error; }
	public String get_last_error() { return m_sz_lasterror; }
	
	public synchronized byte[] load_image(double n_lbo, double n_rbo, double n_ubo, double n_bbo, Dimension dim, int n_mapsite, String sz_portrayal) {
		try {
			String sz_url = get_sitename() + "PAS_getmap.asp?sz_compid=UMS&sz_deptid=TEST&sz_userid=UMS&sz_passwd=MSG&lBo=" + n_lbo + "&rBo=" + n_rbo + "&uBo=" + n_ubo + "&bBo=" + n_bbo + "&lMapWidth=" + dim.width + "&lMapHeight=" + dim.height + "&lMapSite=" + n_mapsite + "&szPortrayal=" + sz_portrayal;
			try {
				Document doc = getDocument(sz_url, true, false, null);
			} catch(Exception e) {
				log.debug(e.getMessage());
				e.printStackTrace();
				Error.getError().addError("HTTPReq","Exception in load_image",e,1);
			}
			byte[] is = new byte[819200];
			int n_read = 1;
			int n_offset = 0;
			while(n_read != -1) //EOF
			{
				byte[] temp = new byte[4096];
				n_read = 0;
				try {
					n_read = get_current_inputstream().read(temp);
				}
				catch(Exception e) { 
					log.debug("Error reading from input-stream");
					break;
					//Error.getError().addError("HTTPReq","Exception in load_image, error reading from input-stream",e,1);
				}
				for(int x = n_offset; x < n_offset + n_read; x++)
				{
					is[x] = temp[x - n_offset];
				}
				if(n_read >=0 )
					n_offset += n_read;
			}
			close_inputstream();
			return is;
		} catch(Exception e) {
			return null;
		}
	}
	public String get_zipped_response_header(HttpURLConnection conn, String sz_header) {
		try {
			return conn.getHeaderField(sz_header);
		} catch(Exception e) {
			Error.getError().addError("HTTPReq","Exception in get_zipped_response_header: no header found",e,1);
			return "ERROR: no header found";
		}
	}
	
	public void get_response_headers(HttpURLConnection conn)
	{
		try {
			m_header.setMaptime(conn.getHeaderField("MAPTIME"));
		} catch(Exception e) { Error.getError().addError("HTTPReq","Exception in get_response_headers: MAPTIME",e,1); }
		try {
			m_header.setBounds(conn.getHeaderField("TIME_LEFTBOUNDARY"), conn.getHeaderField("TIME_RIGHTBOUNDARY"), conn.getHeaderField("TIME_UPPERBOUNDARY"), conn.getHeaderField("TIME_LOWERBOUNDARY"));
		} catch(Exception e) { Error.getError().addError("HTTPReq","Exception in get_response_headers: TIME_LEFTBOUNDARY",e,1); }
		try {
			m_header.setStatus(conn.getHeaderField("MAPSTATUS"));
		} catch(Exception e) { Error.getError().addError("HTTPReq","Exception in get_response_headers: MAPSTATUS",e,1); }
	}
	
	private String m_sz_headers[] = new String [] { "b_session_reconnect" };
	public void get_common_headers(HttpURLConnection conn) {
		try {
			String b_session_reconnect = conn.getHeaderField("b_session_reconnect");
			if(b_session_reconnect.equals("true")) {
				m_b_session_reconnect = true;
			}
			else
				m_b_session_reconnect = false;
		} catch(Exception e) {
			log.debug(e.getMessage());
			e.printStackTrace();
			m_b_session_reconnect = false;
			Error.getError().addError("HTTPReq","Exception in get_common_headers",e,1);
		}
	}
	
	public Node [] getAttributes( Document document,String elementName, String [] attrNames ) {
	    
	    // Get elements with the given tag name
	    // (matches on * too)
	    NodeList nodes = document.getElementsByTagName( elementName );
	    if (nodes == null)
	    {
	    	set_last_error("NodeList == null");
	    	return null;
	    }
	    if ( nodes.getLength() < 1) {
	    	set_last_error("No nodes");
	      return null;
	    }
	    
	    Node firstElement = nodes.item( 0 );
	    NamedNodeMap nnm =
	       firstElement.getAttributes ( ) ;
	   
	    if (nnm != null) {
	      // Test the value of each attribute
	      Node [] matchNodes = new Node[ attrNames.length ];
	        
	       for (int i = 0; i < attrNames.length; i++){
	          boolean all = attrNames[ i ].equalsIgnoreCase("all");
	           if (all) {
	             // named node map
	             int nnmLength = nnm.getLength();
	             matchNodes = new Node[ nnmLength ];
	             
	             for ( int j = 0; j < nnmLength; j++){
	                matchNodes[ j ] = nnm.item( j );
	             }
	             return matchNodes;
	           } 
	           else {
	               matchNodes[ i ] = nnm.getNamedItem
	                 ( attrNames[ i ] );
	               if ( matchNodes[ i ] == null ) {
	                 matchNodes[ i ] = document.createAttribute( attrNames[ i ] );
	                 ((Attr)matchNodes[ i ]).setValue( "UnknownAttribute" );//unknownAttribute 
	               }
	           } // if
	      } // for
	      
	      return matchNodes;
	   } // if
	   set_last_error("nnm == NULL");
	   
	   return null;
	} // printDocumentAttrs	

	public Node [] getAttr(Document document, NamedNodeMap nnm, String [] attrNames) {
	    if (nnm != null) {
	      // Test the value of each attribute
	      Node [] matchNodes = new Node[ attrNames.length ];
	        
	       for (int i = 0; i < attrNames.length; i++){
	          boolean all = attrNames[ i ].equalsIgnoreCase("all");
	           if (all) {
	             // named node map
	             int nnmLength = nnm.getLength();
	             matchNodes = new Node[ nnmLength ];
	             
	             for ( int j = 0; j < nnmLength; j++){
	                matchNodes[ j ] = nnm.item( j );
	             }
	             return matchNodes;
	           } 
	           else {
	               matchNodes[ i ] = nnm.getNamedItem
	                 ( attrNames[ i ] );
	               if ( matchNodes[ i ] == null ) {
	                 matchNodes[ i ] = document.createAttribute( attrNames[ i ] );
	                 ((Attr)matchNodes[ i ]).setValue( "UnknownAttribute" );//unknownAttribute 
	               }
	           } // if
	      } // for
	      
	      return matchNodes;
	   } // if
	   set_last_error("nnm == NULL");
	   
	   return null;
	}

}

