<%@ Page MasterPageFile="~/MasterPage.master" Language="C#" AutoEventWireup="true" CodeFile="report_useractivity.aspx.cs" Inherits="report_useractivity" Theme="sampleTheme" %>

<asp:Content ID="Content1" ContentPlaceHolderID="body" runat="server">
    <div>
    <asp:Table ID="static_table" runat="server">
        <asp:TableHeaderRow>
            <asp:TableHeaderCell HorizontalAlign="Left">User Activities</asp:TableHeaderCell>
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
    </asp:Table>
    <table id="tbl_output" runat="server">
    </table>
    </div>
</asp:Content>