<%@ Application Language="C#" %>

<script runat="server">

    void Application_Start(object sender, EventArgs e) 
    {
        // Code that runs on application startup
    }
    
    void Application_End(object sender, EventArgs e) 
    {
        //  Code that runs on application shutdown

    }
        
    void Application_Error(object sender, EventArgs e) 
    { 
        // Code that runs when an unhandled error occurs

    }

    void Session_Start(object sender, EventArgs e) 
    {
        Session.Timeout = 525600;
        // Code that runs when a new session is started
        System.Globalization.CultureInfo CI = new System.Globalization.CultureInfo("en-us",true);
        //System.Globalization.DateTimeFormatInfo date = CI.DateTimeFormat;
        //CI.DateTimeFormat = date;
        
        System.Threading.Thread.CurrentThread.CurrentCulture = CI;
        System.Threading.Thread.CurrentThread.CurrentUICulture = CI;
    }

    void Session_End(object sender, EventArgs e) 
    {
        // Code that runs when a session ends. 
        // Note: The Session_End event is raised only when the sessionstate mode
        // is set to InProc in the Web.config file. If session mode is set to StateServer 
        // or SQLServer, the event is not raised.
        Session.Abandon();
    }
       
</script>
