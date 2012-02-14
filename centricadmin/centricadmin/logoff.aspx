<%@ Page Language="C#" AutoEventWireup="true" Inherits="logoff" Codebehind="logoff.aspx.cs" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
<head runat="server">
    <link rel="stylesheet" href="css/default.css" />
    <title>NL-Alert Administration <%=System.Configuration.ConfigurationManager.AppSettings["version"]%></title>
</head>
<body>
    <form id="form1" runat="server">
    <div>
        <asp:Label ID="lbl_err" runat="server" Text="" ForeColor="Red"></asp:Label>
        <br />
        <asp:Button ID="btn_gotologon" runat="server" Text="Go to logon" 
            onclick="btn_gotologon_Click" />
    </div>
    </form>
</body>
</html>
