<%@ Page MasterPageFile="~/MasterPage.master" Language="C#" AutoEventWireup="true" CodeFile="channel.aspx.cs" Inherits="channel" %>

<asp:Content ID="Content1" ContentPlaceHolderID="body" runat="server">
    <asp:Table ID="Table2" runat="server">
        <asp:TableRow>
            <asp:TableCell ColumnSpan="2">
                <asp:ListBox ID="lst_channels" runat="server" Width="350" Height="150"></asp:ListBox>
            </asp:TableCell>
        </asp:TableRow>
        <asp:TableRow>
            <asp:TableCell>
                <asp:Label ID="Label1" runat="server" Text="Channel name"></asp:Label>
            </asp:TableCell>
            <asp:TableCell HorizontalAlign="Right">
                <asp:TextBox ID="txt_name" runat="server"></asp:TextBox>                
            </asp:TableCell>
            <asp:TableCell>
                <asp:RequiredFieldValidator ID="RequiredFieldValidator" runat="server" text="*" ErrorMessage="Channel name is missing" ControlToValidate="txt_name"></asp:RequiredFieldValidator>
            </asp:TableCell>
        </asp:TableRow>
        <asp:TableRow>
            <asp:TableCell>
                <asp:Label ID="Label4" runat="server" Text="Channel ID"></asp:Label>
            </asp:TableCell>
            <asp:TableCell HorizontalAlign="Right">
                <asp:TextBox ID="txt_channel" runat="server"></asp:TextBox>
            </asp:TableCell>
            <asp:TableCell>
                <asp:RequiredFieldValidator ID="RequiredFieldValidator1" runat="server" Text="*" ErrorMessage="Channel id is missing" ControlToValidate="txt_channel"></asp:RequiredFieldValidator>
                <asp:RangeValidator id="channel_range" Type="Integer" runat="server" MinimumValue="0" ControlToValidate="txt_channel" Text="*" ErrorMessage="Integer value required for channel"></asp:RangeValidator>
            </asp:TableCell>
        </asp:TableRow>
        <asp:TableRow>
            <asp:TableCell HorizontalAlign="Right" ColumnSpan="2">
                <asp:Button ID="btn_save" runat="server" Text="Save" OnClick="btn_save_Click"/>
            </asp:TableCell>
        </asp:TableRow>
        <asp:TableRow>
            <asp:TableCell ColumnSpan="3">
                <asp:ValidationSummary ID="ValidationSummary1" runat="server" />
            </asp:TableCell>
        </asp:TableRow>
        <asp:TableRow>
            <asp:TableCell>
                <asp:Button id="btn_create" runat="server" Text="Create" OnClick="btn_create_Click" CausesValidation="False" />
                <asp:Button ID="btn_delete" runat="server" Text="Delete" OnClick="btn_delete_Click" CausesValidation="False" />
            </asp:TableCell>
        </asp:TableRow>
    </asp:Table>
</asp:Content>