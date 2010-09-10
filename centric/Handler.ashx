<%@ WebHandler Language="C#" Class="Handler" %>

using System;
using System.Web;
using System.Collections.Specialized;
using com.ums.ws.pas.admin;

public class Handler : IHttpHandler {
    
    private NameValueCollection nvc;
    
    public void ProcessRequest (HttpContext context) {
        nvc = context.Request.Params;
        String page = nvc.Get("page");
        context.Response.ContentType = "text/plain";
        context.Response.Write(page);
        PasAdmin pa = new PasAdmin();

        if (page == "area_admin") {
            CheckAccessResponse res = pa.doSetOccupied((ULOGONINFO)context.Session["logoninfo"], ACCESSPAGE.RESTRICTIONAREA, false);
        }
        else if (page == "predefine_text")
        {
            CheckAccessResponse res = pa.doSetOccupied((ULOGONINFO)context.Session["logoninfo"], ACCESSPAGE.PREDEFINEDTEXT, false);
        }
        
    }
 
    public bool IsReusable {
        get {
            return false;
        }
    }

}