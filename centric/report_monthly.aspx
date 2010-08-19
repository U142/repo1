<%@ Page MasterPageFile="~/MasterPage.master" Language="C#" AutoEventWireup="true" CodeFile="report_monthly.aspx.cs" Inherits="report_monthly" Theme="sampleTheme" %>

<asp:Content ID="Content1" ContentPlaceHolderID="body" runat="server">
<div>

<table>
    <tr>
        <td>Monthly Report</td>
    </tr>
    <tr>
        <td>Month:</td>
        <td>
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
        </td>
    </tr>
    <tr>
        <td>Year:</td>
        <td>
            <asp:DropDownList ID="ddl_year" runat="server">
            </asp:DropDownList>
        </td>
    </tr>
     <tr>
        <td>&nbsp;</td>
        <td>
            <asp:Button ID="btn_show" runat="server" Text="Show" OnClick="btn_showClick" />
        </td>
    </tr>
</table>
All messages sent this month <asp:Button ID="btn_messages_month" runat="server" Text="Export to CSV" OnClick="btn_messages_month_Click" />
<table id="tbl_output" runat="server" border="1"></table>
Operator performance <asp:Button ID="btn_performance_month" runat="server" Text="Export to CSV" OnClick="btn_performance_month_Click" />
<table id="tbl_operatorperformance" runat="server" border="1"></table>
System messages in this month <asp:Button ID="btn_sysmessages_month" runat="server" Text="Export to CSV" OnClick="btn_sysmessages_month_Click" />
<table id="tbl_sysmessages" runat="server" border="1"></table>

</div>
</asp:Content>
