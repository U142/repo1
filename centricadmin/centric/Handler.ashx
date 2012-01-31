<%@ WebHandler Language="C#" Class="Handler" %>

using System;
using System.Web;
using System.Web.SessionState;

using System.Configuration;

using System.Collections.Specialized;
using com.ums.ws.pas.admin;


public class Handler : IHttpHandler, IRequiresSessionState {
    
    private NameValueCollection nvc;
    
    
    public void ProcessRequest (HttpContext context) {
        
        nvc = context.Request.Params;
        String page = nvc.Get("page");
        context.Response.ContentType = "text/plain";
        context.Response.Write(page);
        PasAdminSoapClient pa = new PasAdminSoapClient();
        pa.Endpoint.Address = new System.ServiceModel.EndpointAddress(ConfigurationManager.AppSettings["PasAdmin"]);
        ULOGONINFO info = (ULOGONINFO)context.Session["logoninfo"];
        
        if (page == "area_edit") {
            CheckAccessResponse res = pa.doSetOccupied(info, ACCESSPAGE.RESTRICTIONAREA, false);
        }
        else if (page == "predefine_text")
        {
            
            CheckAccessResponse res = pa.doSetOccupied(info, ACCESSPAGE.PREDEFINEDTEXT, false);
        }
        
    }
 
    public bool IsReusable {
        get {
            return false;
        }
    }

}