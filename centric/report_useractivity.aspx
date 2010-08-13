<%@ Page MasterPageFile="~/MasterPage.master" Language="C#" AutoEventWireup="true" CodeFile="report_useractivity.aspx.cs" Inherits="report_useractivity" Theme="sampleTheme" %>

<asp:Content ID="Content1" ContentPlaceHolderID="body" runat="server">
    <div>
    <table>
        <tr>
            <td>User Activities</td>
        </tr>
    </table>
    <table id="tbl_output" runat="server">
    </table>
    </div>
</asp:Content>