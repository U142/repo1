<%@ Page Language="C#" AutoEventWireup="true" Inherits="logon" Codebehind="logon.aspx.cs" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
<head runat="server">
    <link rel="stylesheet" href="css/default.css" />
    <title>NL-Alert Administration <%=System.Reflection.Assembly.GetAssembly(typeof(centric.SiteMaster)).GetName().Version%></title>
</head>
<body>
    <form id="form1" runat="server">
    <div>
        <asp:Table ID="Table1" runat="server" Width="100%">
        <asp:TableRow>
        <asp:TableCell>
            <asp:Table ID="table2" runat="server" HorizontalAlign="Center">
                <asp:TableHeaderRow>
                    <asp:TableCell>
                        <asp:Label ID="lbl_logon" runat="server" Text="Logon"></asp:Label>
                    </asp:TableCell>
                </asp:TableHeaderRow>
                <asp:TableRow>
                    <asp:TableCell>
                        <asp:Label ID="lbl_user" runat="server" Text="Username:"></asp:Label>
                    </asp:TableCell>
                    <asp:TableCell>
                        <asp:TextBox ID="txt_user" runat="server"></asp:TextBox>
                    </asp:TableCell>
                </asp:TableRow>
                <asp:TableRow>
                    <asp:TableCell>
                        <asp:Label ID="lbl_password" runat="server" Text="Password:"></asp:Label>
                    </asp:TableCell>
                    <asp:TableCell>
                        <asp:TextBox ID="txt_password" runat="server" TextMode="Password" style="width:143px"></asp:TextBox>
                    </asp:TableCell>
                </asp:TableRow>
                <asp:TableRow>
                    <asp:TableCell>
                        <asp:Button ID="btn_ok" runat="server" Text="Ok" Width="60" OnClick="btn_ok_Click" />
                    </asp:TableCell>
                    <asp:TableCell HorizontalAlign="Right">
                        <asp:Button ID="btn_cancel" runat="server" Text="Cancel" Width="60" />
                    </asp:TableCell>
                </asp:TableRow>
                <asp:TableRow>
                    <asp:TableCell HorizontalAlign="Center" ColumnSpan="2">
                        <asp:Label ID="lbl_error" runat="server" Text="" ></asp:Label>
                    </asp:TableCell>
                </asp:TableRow>
                <asp:TableRow>
                    <asp:TableCell>
                        <asp:Label ID="lbl_company" runat="server" Text="Company:" style="visibility:hidden"></asp:Label>
                    </asp:TableCell>
                    <asp:TableCell>
                        <asp:TextBox ID="txt_company" runat="server" style="visibility:hidden"></asp:TextBox>
                    </asp:TableCell>
                </asp:TableRow>
            </asp:Table>
        </asp:TableCell>
        </asp:TableRow>
        </asp:Table>
    </div>
    </form>
</body>
</html>
