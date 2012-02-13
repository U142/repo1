<%@ Page MasterPageFile="~/MasterPage.master" Language="C#" AutoEventWireup="true" Inherits="report_authorizationarea" Theme="sampleTheme" Codebehind="report_authorizationarea.aspx.cs" %>

<asp:Content ID="Content1" ContentPlaceHolderID="body" runat="server">
<script language="javascript" type="text/javascript">
    function launchApplet(applet) {
        document.getElementById("ctl00_body_applet").innerHTML = applet;    
    }
</script>
<div>
<asp:Table ID="table" runat="server">
    <asp:TableHeaderRow>
        <asp:TableHeaderCell HorizontalAlign="Left" ColumnSpan="2">Authorization report</asp:TableHeaderCell>
    </asp:TableHeaderRow>
    <asp:TableRow>
        <asp:TableCell ColumnSpan="2"><asp:ListBox ID="lst_areas" runat="server" Height="150" Width="640" SelectionMode="Multiple"></asp:ListBox></asp:TableCell>
    </asp:TableRow>
    <asp:TableRow>
        <asp:TableCell>Month:</asp:TableCell>
        <asp:TableCell>
            <asp:DropDownList ID="ddl_month" runat="server">
                <asp:ListItem Text="January" Value="01"></asp:ListItem>
                <asp:ListItem Text="Feburary" Value="02"></asp:ListItem>
                <asp:ListItem Text="March" Value="03"></asp:ListItem>
                <asp:ListItem Text="April" Value="04"></asp:ListItem>
                <asp:ListItem Text="May" Value="05"></asp:ListItem>
                <asp:ListItem Text="June" Value="06"></asp:ListItem>
                <asp:ListItem Text="July" Value="07"></asp:ListItem>
                <asp:ListItem Text="August" Value="08"></asp:ListItem>
                <asp:ListItem Text="September" Value="09"></asp:ListItem>
                <asp:ListItem Text="October" Value="10"></asp:ListItem>
                <asp:ListItem Text="November" Value="11"></asp:ListItem>
                <asp:ListItem Text="December" Value="12"></asp:ListItem>
            </asp:DropDownList>
        </asp:TableCell>
    </asp:TableRow>
    <asp:TableRow>
        <asp:TableCell>Year:</asp:TableCell>
        <asp:TableCell>
            <asp:DropDownList ID="ddl_year" runat="server">
            </asp:DropDownList>
        </asp:TableCell>
    </asp:TableRow>
    <asp:TableRow>
        <asp:TableCell>
            <asp:Button ID="btn_show" runat="server" Text="Show" OnClick="btn_showClick"/>
        </asp:TableCell>
    </asp:TableRow>
</asp:Table> 
<table id="tbl_output" runat="server">
</table>

</div>
<div id="applet" runat="server"></div>   
</asp:Content>