<%@ Page MasterPageFile="~/MasterPage.master" Language="C#" AutoEventWireup="true" CodeFile="report_authorizationarea.aspx.cs" Inherits="report_authorizationarea" Theme="sampleTheme" %>

<asp:Content ID="Content1" ContentPlaceHolderID="body" runat="server">
<div>
<table>
    <tr>
        <td>Authorization report</td>
    </tr>
    <tr>
        <td><asp:ListBox ID="lst_areas" runat="server" Height="150" Width="640" SelectionMode="Multiple"></asp:ListBox></td>
    </tr>
    <tr>
        <td>
            <asp:Button ID="btn_show" runat="server" Text="Show" OnClick="btn_showClick"/>
        </td>
    </tr>
</table> 
<table id="tbl_output" runat="server">
</table>   
</div>
</asp:Content>