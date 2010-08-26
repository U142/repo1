<%@ Page MasterPageFile="~/MasterPage.master" Language="C#" AutoEventWireup="true" CodeFile="report_useraccess.aspx.cs" Inherits="report_useraccess" Theme="sampleTheme" %>

<asp:Content ID="Content1" ContentPlaceHolderID="body" runat="server">
    <div>
    <asp:Table ID="table" runat="server">
        <asp:TableHeaderRow>
            <asp:TableHeaderCell HorizontalAlign="Left">Access permissions per user</asp:TableHeaderCell>
        </asp:TableHeaderRow>
        <asp:TableRow>
            <asp:TableCell><asp:ListBox ID="lst_areas" runat="server" Height="150" Width="640" SelectionMode="Multiple"></asp:ListBox></asp:TableCell>
        </asp:TableRow>
        <asp:TableRow>
            <asp:TableCell>
                <asp:Button ID="btn_show" runat="server" Text="Show" OnClick="btn_showClick"/>
            </asp:TableCell>
        </asp:TableRow>
        <asp:TableRow>
            <asp:TableCell>
                <asp:Button ID="btn_export_user_access" runat="server" Text="Export to CSV" OnClick="btn_export_user_access_Click" Visible="false" />
            </asp:TableCell>
        </asp:TableRow>
    </asp:Table>
    <table id="tbl_output" runat="server">
    </table>
    </div>
</asp:Content>
