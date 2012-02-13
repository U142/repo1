<%@ Page MasterPageFile="~/MasterPage.master" Language="C#" AutoEventWireup="true" Inherits="report_monthly" Theme="sampleTheme" Codebehind="report_monthly.aspx.cs" %>

<asp:Content ID="Content1" ContentPlaceHolderID="body" runat="server">
<div>

<asp:Table ID="table" runat="server">
    <asp:TableHeaderRow>
            <asp:TableHeaderCell HorizontalAlign="Left">Monthly Report</asp:TableHeaderCell>
    </asp:TableHeaderRow>
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
        <asp:TableCell>&nbsp;</asp:TableCell>
        <asp:TableCell>
            <asp:Button ID="btn_show" runat="server" Text="Show" OnClick="btn_showClick" />
        </asp:TableCell>
    </asp:TableRow>
</asp:Table>

<asp:Table ID="table4" runat="server">
    <asp:TableHeaderRow>
            <asp:TableHeaderCell HorizontalAlign="Left">Total messages </asp:TableHeaderCell>
            <asp:TableCell><asp:Button ID="btn_messages_total" runat="server" Text="Export to CSV" OnClick="btn_messages_total_month_Click" Visible="false"/></asp:TableCell>
    </asp:TableHeaderRow>
</asp:Table>
<table id="tbl_total_messages" runat="server" border="1" width="700px"></table>

<asp:Table ID="table1" runat="server">
    <asp:TableHeaderRow>
            <asp:TableHeaderCell HorizontalAlign="Left">All messages sent this month </asp:TableHeaderCell>
            <asp:TableCell><asp:Button ID="btn_messages_month" runat="server" Text="Export to CSV" OnClick="btn_messages_month_Click" /></asp:TableCell>
    </asp:TableHeaderRow>
</asp:Table>
<table id="tbl_output" runat="server" border="1" width="700px"></table>

<asp:Table ID="table2" runat="server">
    <asp:TableHeaderRow>
            <asp:TableHeaderCell HorizontalAlign="Left">Operator performance</asp:TableHeaderCell>
            <asp:TableCell><asp:Button ID="btn_performance_month" runat="server" Text="Export to CSV" OnClick="btn_performance_month_Click" /></asp:TableCell>
    </asp:TableHeaderRow>
</asp:Table>
<table id="tbl_operatorperformance" runat="server" border="1" width="700px"></table>

<asp:Table ID="table3" runat="server">
    <asp:TableHeaderRow>
            <asp:TableHeaderCell HorizontalAlign="Left">System messages in this month </asp:TableHeaderCell>
            <asp:TableCell><asp:Button ID="btn_sysmessages_month" runat="server" Text="Export to CSV" OnClick="btn_sysmessages_month_Click" /></asp:TableCell>
    </asp:TableHeaderRow>
</asp:Table>
<table id="tbl_sysmessages" runat="server" border="1" width="700px"></table>

</div>
</asp:Content>
