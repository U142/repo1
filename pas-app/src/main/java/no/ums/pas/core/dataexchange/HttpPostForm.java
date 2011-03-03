package no.ums.pas.core.dataexchange;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

public class HttpPostForm {
    public static InputStream newInputStream(final ByteBuffer buf) {
        return new InputStream() {
            public synchronized int read() throws IOException {
                if (!buf.hasRemaining()) {
                    return -1;
                }
                return buf.get();
            }
    
            public synchronized int read(byte[] bytes, int off, int len) throws IOException {
                // Read only what's left
                len = Math.min(len, buf.remaining());
                buf.get(bytes, off, len);
                return len;
            }
        };
    }
    public static OutputStream newOutputStream(final ByteBuffer buf) {
        return new OutputStream() {
            public synchronized void write(int b) throws IOException {
                buf.put((byte)b);
            }
    
            public synchronized void write(byte[] bytes, int off, int len) throws IOException {
                buf.put(bytes, off, len);
            }
        };
    }
	
  boolean m_b_session_reconnect = false;
  public boolean session_reconnected() { return m_b_session_reconnect; }
  URLConnection connection;
  OutputStream os = null;
  Map cookies = new HashMap();
  StringBuffer stringBuffer = new StringBuffer("");

  protected void connect() throws IOException {
    if (os == null) os = connection.getOutputStream();
  }

  protected void write(char c) throws IOException {
    stringBuffer.append(c);
    connect();
    os.write(c);
    os.flush();
  }

  protected void write(String s) throws IOException {
    stringBuffer.append(s);
    connect();
    os.write(s.getBytes());
    os.flush();
  }

  protected void newline() throws IOException {
    stringBuffer.append("\r\n");
    connect();
    write("\r\n");
  }

  protected void writeln(String s) throws IOException {
    connect();
    write(s);
    newline();
  }

  private static Random random = new Random();

  protected static String randomString() {
    return Long.toString(random.nextLong(), 36);
  }

  String boundary = "---------------------------" +
  randomString() +
  randomString() + randomString();

  private void boundary() throws IOException {
    write("--");
    write(boundary);
  }


  public HttpPostForm(URLConnection connection) throws IOException {
    this.connection = connection;
    connection.setDoOutput(true);
    connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
  }


  public HttpPostForm(URL url) throws IOException {
    this(url.openConnection());
  }


  public HttpPostForm(String urlString) throws IOException {
    this(new URL(urlString));
  }
  public HttpPostForm(String sSoapHost, String sFunction) throws IOException {
	  //this(new URL(sSoapHost));
	  this.connection = new URL("http://pasutv/ExternalExec.asmx/ExecAlert").openConnection();
	  HttpURLConnection u = (HttpURLConnection)connection;
	  connection.setDoOutput(true);
	  //c.setDoInput(true);
	  u.setRequestMethod("POST");
	  //u.setRequestProperty("SOAPAction", "http://pasutv/ExternalExec.asmx/ExecAlert");
	  u.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");
	  String sParameters = "l_alertpk=string&l_comppk=string&l_userpk=string&sz_compid=string&sz_userid=string&sz_password=string";
	  u.addRequestProperty("Content-Length", Integer.toString(sParameters.length() + 2));
  }

  ActionListener m_callback = null;
  private void postProgress(int n) {
	  if(m_callback!=null) {
		  m_callback.actionPerformed(new ActionEvent(n, ActionEvent.ACTION_PERFORMED, "act_progress"));
	  }
  }
  
  public HttpPostForm(URLConnection connection, ActionListener callback) throws IOException {
	    this.connection = connection;
	    m_callback = callback;
	    connection.setDoOutput(true);
	    connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
	  }


	  public HttpPostForm(URL url, ActionListener callback) throws IOException {
	    this(url.openConnection(), callback);
	  }


	  public HttpPostForm(String urlString, ActionListener callback) throws IOException {
	    this(new URL(urlString), callback);
	  }
	  

	  protected void postCookies() {
    StringBuffer cookieList = new StringBuffer();

    for (Iterator<Object> i = cookies.entrySet().iterator(); i.hasNext();) {
      Map.Entry entry = (Map.Entry)(i.next());
      cookieList.append(entry.getKey().toString() + "=" + entry.getValue());

      if (i.hasNext()) {
        cookieList.append("; ");
      }
    }
    if (cookieList.length() > 0) {
      connection.setRequestProperty("Cookie", cookieList.toString());
    }
  }


  public void setCookie(String name, String value) throws IOException {
    cookies.put(name, value);
  }


  public void setCookies(Map cookies) throws IOException {
    if (cookies == null) 
    	return;
    this.cookies.putAll(cookies);
  }

  public void setCookies(String[] cookies) throws IOException {
    if (cookies == null) 
    	return;
    for (int i = 0; i < cookies.length - 1; i+=2) {
      setCookie(cookies[i], cookies[i+1]);
    }
  }

  private void writeName(String name) throws IOException {
    newline();
    write("Content-Disposition: form-data; name=\"");
    write(name);
    write('"');
  }

  public void setParameter(String name, String value) throws IOException {
    boundary();
    writeName(name);
    newline(); newline();
    writeln(value);
  }
  


  private void pipe(InputStream in, OutputStream out) throws IOException {
    byte[] buf = new byte[4096]; //500000
    int nread;
    int navailable;
    int total = 0;
    int n_prevkb = 0;
    int n_kbsent = 0;
    synchronized (in) {
    	//while((nread = in.read(buf, 0, buf.length)) >= 0) {

      while((nread = in.read(buf, 0, buf.length)) > 0) {
        out.write(buf, 0, nread);
        total += nread;
        n_kbsent = total /  4096;
        if(n_kbsent > n_prevkb) {
        	postProgress(n_kbsent);
        	n_prevkb = n_kbsent;
        }
        out.flush();
      }
    }
    //out.flush();
    buf = null;
  }

  public void setParameter(String name, String filename, InputStream is) throws IOException {
    boundary();
    writeName(name);
    write("; filename=\"");
    write(filename);
    write('"');
    newline();
    write("Content-Type: ");
    String type =
    	URLConnection.guessContentTypeFromName(filename);
    if (type == null) 
    	type = "application/octet-stream";
    writeln(type);
    newline();
    pipe(is, os);
    newline();
  }
  
 

  public void setParameter(String name, File file) throws IOException {
    setParameter(name, file.getPath(), new FileInputStream(file));
  }


  public void setParameter(String name, Object object) throws IOException {
    if (object instanceof File) {
      setParameter(name, (File) object);
    } else {
      setParameter(name, object.toString());
    }
  }


  public void setParameters(Map parameters) throws IOException {
    if (parameters == null) return;
    for (Iterator i = parameters.entrySet().iterator(); i.hasNext();) {
      Map.Entry entry = (Map.Entry)i.next();
      setParameter(entry.getKey().toString(), entry.getValue());
    }
  }


  public void setParameters(Object[] parameters) throws IOException {
    if (parameters == null) 
    	return;
    for (int i = 0; i < parameters.length - 1; i+=2) {
      setParameter(parameters[i].toString(), parameters[i+1]);
    }
  }


  public InputStream post() throws IOException {
    boundary();
    writeln("--");
    os.close();
    String sz_recon="false";
    try {
    	connection.getHeaderField("b_session_reconnect");
    } catch(Exception e) {
    	
    }
    if(sz_recon=="true")
    	m_b_session_reconnect = true;
    else
    	m_b_session_reconnect = false;
//    InputStream is = connection.getInputStream();
//    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
//    System.out.println(reader.read());
    return connection.getInputStream();
  }
  
  public InputStream postSoap(String sParameters) throws IOException {
	  
	  //newline();
	write("l_alertpk=string&l_comppk=string&l_userpk=string&sz_compid=string&sz_userid=string&sz_password=string");
	os.close();
	return connection.getInputStream();
  }
 


  public InputStream post(Map parameters) throws IOException {
    setParameters(parameters);
    return post();
  }

  
  public InputStream post(Object[] parameters) throws IOException {
    setParameters(parameters);
    return post();
  }


  public InputStream post(Map cookies, Map parameters) throws IOException {
    setCookies(cookies);
    setParameters(parameters);
    return post();
  }


  public InputStream post(String[] cookies, Object[] parameters) throws IOException {
    setCookies(cookies);
    setParameters(parameters);
    return post();
  }


  public InputStream post(String name, Object value) throws IOException {
    setParameter(name, value);
    return post();
  }


  public InputStream post(String name1, Object value1, String name2, Object value2) throws IOException {
    setParameter(name1, value1);
    return post(name2, value2);
  }


  public InputStream post(String name1, Object value1, String name2, Object value2, 
  						String name3, Object value3) throws IOException {
    setParameter(name1, value1);
    return post(name2, value2, name3, value3);
  }


  public InputStream post(String name1, Object value1,
							String name2, Object
							value2, String name3, Object value3, String name4,
							Object value4) throws
							IOException {
    setParameter(name1, value1);
    return post(name2, value2, name3, value3, name4, value4);
  }


  public static InputStream post(URL url, Map parameters) throws IOException {
    return new HttpPostForm(url).post(parameters);
  }


  public static InputStream post(URL url, Object[] parameters) throws IOException {
    return new HttpPostForm(url).post(parameters);
  }


  public static InputStream post(URL url, Map cookies, Map parameters) throws IOException {
    return new HttpPostForm(url).post(cookies, parameters);
  }


  public static InputStream post(URL url, String[] cookies, Object[] parameters) throws IOException {
    return new HttpPostForm(url).post(cookies, parameters);
  }


  public static InputStream post(URL url, String name1, Object value1) throws IOException {
    return new HttpPostForm(url).post(name1, value1);
  }


  public static InputStream post(URL url, String name1, Object value1, String name2, Object value2) throws IOException {
    return new HttpPostForm(url).post(name1, value1, name2, value2);
  }


  public static InputStream post(URL url, String name1, Object value1, String name2, Object value2, 
  								String name3, Object value3) throws IOException {
    return new HttpPostForm(url).post(name1, value1, name2, value2, name3, value3);
  }


  public static InputStream post(URL url, String name1, Object value1, String name2, Object value2, String name3, 
  								Object value3, String name4, Object value4) throws IOException {
    return new HttpPostForm(url).post(name1, value1, name2, value2, name3, value3, name4, value4);
  }

  public String toString() {
    return stringBuffer.toString();
  }
}
