<%@ Page MasterPageFile="~/MasterPage.master" Language="C#" AutoEventWireup="true" CodeFile="duration.aspx.cs" Inherits="duration" %>

<asp:Content ID="Content1" ContentPlaceHolderID="body" runat="server">
<asp:Table ID="Table1" runat="server" BorderStyle="Dashed">
    <asp:TableRow>
        <asp:TableCell ColumnSpan="2">
            <!--<asp:DataGrid runat="server" ID="datagrid" AutoGenerateColumns="true">
                
            </asp:DataGrid>-->
            <asp:ListBox runat="server" ID="lst_duration" AutoPostBack="true" Width="350" Height="150" OnSelectedIndexChanged="edit_duration"></asp:ListBox>
        </asp:TableCell>
        <asp:TableCell></asp:TableCell>
    </asp:TableRow>
    <asp:TableRow>
        <asp:TableCell>
            <asp:Button ID="btn_create" runat="server" Text="Create" OnClick="btn_create_Click" CausesValidation="False" />
            <asp:Button ID="btn_delete" runat="server" Text="Delete" OnClick="btn_delete_Click" CausesValidation="False" />
        </asp:TableCell>
    </asp:TableRow>
    <asp:TableRow>
        <asp:TableCell>
            <asp:Label ID="lbl_duration" runat="server" Text="Duration"></asp:Label></asp:TableCell>
        <asp:TableCell HorizontalAlign="Left">
            <asp:TextBox ID="txt_duration" runat="server" ValidationGroup="RequiredFieldValidator1" ></asp:TextBox>
            <asp:RequiredFieldValidator ID="RequiredFieldValidator1" runat="server" Text="*" ErrorMessage="Duration required" ControlToValidate="txt_duration" ></asp:RequiredFieldValidator>
            <asp:RangeValidator id="duration_range" Type="Integer" runat="server" MinimumValue="0" ControlToValidate="txt_duration" Text="*" ErrorMessage="Integer value required"></asp:RangeValidator>
        </asp:TableCell>
    </asp:TableRow>
    <asp:TableRow>
        <asp:TableCell>
            <asp:Label ID="lbl_interval" runat="server" Text="Interval"></asp:Label>
        </asp:TableCell>
        <asp:TableCell>
            <asp:TextBox ID="txt_interval" runat="server" ValidationGroup="RequiredFieldValidator1" ></asp:TextBox>
            <asp:RequiredFieldValidator ID="RequiredFieldValidator2" runat="server" ErrorMessage="Interval required" Text="*" ControlToValidate="txt_interval" ></asp:RequiredFieldValidator>
            <asp:RangeValidator id="interval_range" Type="Integer" runat="server" MinimumValue="0" ControlToValidate="txt_interval" Text="*" ErrorMessage="Integer value required"></asp:RangeValidator>
        </asp:TableCell>
    </asp:TableRow>
    <asp:TableRow>
        <asp:TableCell>
            <asp:Label ID="lbl_repetition" runat="server" Text="Repetition"></asp:Label>
        </asp:TableCell>
        <asp:TableCell>
            <asp:TextBox ID="txt_repetition" runat="server" ValidationGroup="RequiredFieldValidator1" ></asp:TextBox>
            <asp:RequiredFieldValidator ID="RequiredFieldValidator3" runat="server" Text="*" ErrorMessage="Repetition required" ControlToValidate="txt_repetition" ></asp:RequiredFieldValidator>
            <asp:RangeValidator id="repetition_range" Type="Integer" runat="server" MinimumValue="0" ControlToValidate="txt_repetition" Text="*" ErrorMessage="Integer value required"></asp:RangeValidator>
        </asp:TableCell>
    </asp:TableRow>
    <asp:TableRow>
        <asp:TableCell>
            <asp:Button ID="btn_save" runat="server" Text="Save" OnClick="btn_save_Click" />            
        </asp:TableCell>
    </asp:TableRow>
    <asp:TableRow>
        <asp:TableCell ColumnSpan="2">
            <asp:ValidationSummary ID="ValidationSummary1" runat="server" />
        </asp:TableCell>
    </asp:TableRow>
</asp:Table>
</asp:Content>