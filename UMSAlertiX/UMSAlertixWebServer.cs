using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net;
using System.IO;
using System.Threading;

namespace UMSAlertiX
{
    public class UMSAlertiXWebServer
    {
        HttpListener _listener;

        public UMSAlertiXWebServer(string uriPrefix)
        {
            // trenger ikke sette threadpool verdier, holder å bruke defaults (basert på antall cpu'er)
/*            System.Threading.ThreadPool.SetMaxThreads(50, 1000);
            System.Threading.ThreadPool.SetMinThreads(50, 50);*/
            _listener = new HttpListener();
            _listener.Prefixes.Add(uriPrefix);
        }

        UMSAlertiXController oController;
        public void SetController(ref UMSAlertiXController obj)
        {
            oController = obj;
        }

        public void Start()
        {
            _listener.Start();
            while (true)
                try
                {
                    HttpListenerContext request = _listener.GetContext();
                    ThreadPool.QueueUserWorkItem(ProcessRequest, request);
                }
                catch (HttpListenerException) { break; }
                catch (InvalidOperationException) { break; }
        }

        public void Stop() { _listener.Stop(); }

        void ProcessRequest(object listenerContext)
        {
            try
            {
                DateTime dtmstart = DateTime.Now;
                var context = (HttpListenerContext)listenerContext;
                string filename = context.Request.RawUrl;

                // remove / from destination url (http://host:port/destinationhost:port/path -> /destinationhost:port/path)
                if (filename.StartsWith("/"))
                    filename = filename.Remove(0, 1);

                byte[] msg;

                msg = GetWebPage(filename);

                context.Response.ContentLength64 = msg.Length;
                using (Stream s = context.Response.OutputStream)
                    s.Write(msg, 0, msg.Length);

                DateTime dtmDone = DateTime.Now;
                TimeSpan tsDuration = dtmDone - dtmstart;
                oController.log.WriteLog("Got " + filename + " (" + (long)tsDuration.TotalMilliseconds + " ms)");
            }
            catch (Exception e)
            {
                oController.log.WriteLog("Request error: " + e.Message);
                //Console.WriteLine("Request error: " + ex);
            }
        }

        private byte[] GetWebPage(String uri)    
        {    
            const int bufSizeMax = 65536; // max read buffer size conserves memory    
            const int bufSizeMin = 8192;  // min size prevents numerous small reads    
            StringBuilder sb;  
            Encoding encType = Encoding.Default;
            
            // A WebException is thrown if HTTP request fails    
            try     
            {        
                // Create an HttpWebRequest using WebRequest.Create (see .NET docs)!        
                HttpWebRequest request = (HttpWebRequest)WebRequest.Create(uri);        
                
                // Execute the request and obtain the response stream        
                HttpWebResponse response = (HttpWebResponse)request.GetResponse();        
                Stream responseStream = response.GetResponseStream();
                if(response.CharacterSet!="")
                    encType = Encoding.GetEncoding(response.CharacterSet);
                
                // Content-Length header is not trustable, but makes a good hint.        
                // Responses longer than int size will throw an exception here!        
                int length = (int)response.ContentLength;        
                
                // Use Content-Length if between bufSizeMax and bufSizeMin        
                int bufSize = bufSizeMin;        
                if (length > bufSize)            
                    bufSize = length > bufSizeMax ? bufSizeMax : length;        
                
                // Allocate buffer and StringBuilder for reading response        
                byte[] buf = new byte[bufSize];        
                sb = new StringBuilder(bufSize);        
                
                // Read response stream until end        
                while ((length = responseStream.Read(buf, 0, buf.Length)) != 0)            
                    sb.Append(encType.GetString(buf, 0, length));    
            }    
            catch (Exception ex)    
            {        
                sb = new StringBuilder(ex.Message);    
            }    
            //return sb.ToString();
            return encType.GetBytes(sb.ToString());
        }    
    }
}
