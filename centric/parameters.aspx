<%@ Page MasterPageFile="~/MasterPage.master" Language="C#" AutoEventWireup="true"  CodeFile="parameters.aspx.cs" Inherits="parameters" %>

<asp:Content ID="Content1" ContentPlaceHolderID="body" runat="server">
    <asp:Table ID="Table2" runat="server">
        <asp:TableRow>
            <asp:TableCell><asp:Label ID="Label4" runat="server" Text="Overview Parameters"></asp:Label></asp:TableCell>
        </asp:TableRow>
        <asp:TableRow>
            <asp:TableCell>
                <asp:Label ID="Label1" runat="server" Text="Incorrect logons"></asp:Label>
            </asp:TableCell>
            <asp:TableCell>
                <asp:TextBox ID="txt_inclogons" runat="server"></asp:TextBox>                
            </asp:TableCell>
            <asp:TableCell>
                <asp:RequiredFieldValidator ID="RequiredFieldValidator4" runat="server" text="*" ErrorMessage="Number of incorrect logons missing" ControlToValidate="txt_inclogons"></asp:RequiredFieldValidator>
                <asp:RangeValidator id="inclogons_range" Type="Integer" runat="server" MinimumValue="0" ControlToValidate="txt_inclogons" Text="*" ErrorMessage="Integer value required for incorrect logons"></asp:RangeValidator>
            </asp:TableCell>
        </asp:TableRow>
        <asp:TableRow>
            <asp:TableCell>
                <asp:Label ID="Label3" runat="server" Text="Administrator email"></asp:Label>
            </asp:TableCell>
            <asp:TableCell>
                <asp:TextBox ID="txt_adminemail" runat="server"></asp:TextBox>
            </asp:TableCell>
            <asp:TableCell>
                <asp:RequiredFieldValidator ID="RequiredFieldValidator2" runat="server" Text="*" ErrorMessage="Administrator email missing" ControlToValidate="txt_adminemail"></asp:RequiredFieldValidator>
                <asp:RegularExpressionValidator ID="RegularExpressionValidator1" runat="server" Text="*" ErrorMessage="E-mail is not valid" ControlToValidate="txt_adminemail" ValidationExpression="^\S+@\S+.\S+$"></asp:RegularExpressionValidator>
            </asp:TableCell>
        </asp:TableRow>
        <asp:TableRow>
            <asp:TableCell>
                <asp:Label ID="lbl_channel" runat="server" Text="Channel number for NL-Alert-messages"></asp:Label>
            </asp:TableCell>
            <asp:TableCell>
                <asp:TextBox ID="txt_channel" runat="server"></asp:TextBox>
            </asp:TableCell>
            <asp:TableCell>
                <asp:RequiredFieldValidator ID="req_channel" runat="server" Text="*" ErrorMessage="Channel number for NL-Alert-messages missing" ControlToValidate="txt_channel"></asp:RequiredFieldValidator>
                <asp:RangeValidator id="rng_channel" Type="Integer" runat="server" MinimumValue="0" ControlToValidate="txt_channel" Text="*" ErrorMessage="Integer value required for Channel number for NL-Alert-messages"></asp:RangeValidator>
            </asp:TableCell>
        </asp:TableRow>
        <asp:TableRow>
            <asp:TableCell>
                <asp:Label ID="lbl_test_channel" runat="server" Text="Channel number for Test messages"></asp:Label>
            </asp:TableCell>
            <asp:TableCell>
                <asp:TextBox ID="txt_test_channel" runat="server"></asp:TextBox>
            </asp:TableCell>
            <asp:TableCell>
                <asp:RequiredFieldValidator ID="req_test_channel" runat="server" Text="*" ErrorMessage="Channel number for Test messages missing" ControlToValidate="txt_test_channel"></asp:RequiredFieldValidator>
                <asp:RangeValidator id="rng_test_channel" Type="Integer" runat="server" MinimumValue="0" ControlToValidate="txt_test_channel" Text="*" ErrorMessage="Integer value required for Channel number for Test messages"></asp:RangeValidator>
            </asp:TableCell>
        </asp:TableRow>
        <asp:TableRow>
            <asp:TableCell>
                <asp:Label ID="lbl_heartbeat" runat="server" Text="Channel number for Heartbeat messages"></asp:Label>
            </asp:TableCell>
            <asp:TableCell>
                <asp:TextBox ID="txt_heartbeat" runat="server"></asp:TextBox>
            </asp:TableCell>
            <asp:TableCell>
                <asp:RequiredFieldValidator ID="req_heartbeat" runat="server" Text="*" ErrorMessage="Days delayed sending missing" ControlToValidate="txt_heartbeat"></asp:RequiredFieldValidator>
                <asp:RangeValidator id="rng_heartbeat" Type="Integer" runat="server" MinimumValue="0" ControlToValidate="txt_heartbeat" Text="*" ErrorMessage="Integer value required for Channel number for Heartbeat"></asp:RangeValidator>
            </asp:TableCell>
        </asp:TableRow>
        <asp:TableRow>
            <asp:TableCell>
                <asp:Label ID="lbl_interval" runat="server" Text="Interval between broadcasts on a channel"></asp:Label>
            </asp:TableCell>
            <asp:TableCell>
                <asp:TextBox ID="txt_interval" runat="server"></asp:TextBox>
            </asp:TableCell>
            <asp:TableCell>
                <asp:RequiredFieldValidator ID="req_interval" runat="server" Text="*" ErrorMessage="Sending interval missing" ControlToValidate="txt_interval"></asp:RequiredFieldValidator>
                <asp:RangeValidator id="rng_interval" Type="Integer" runat="server" MinimumValue="0" ControlToValidate="txt_interval" Text="*" ErrorMessage="Integer value required for sending interval"></asp:RangeValidator>
            </asp:TableCell>
        </asp:TableRow>
        <asp:TableRow>
            <asp:TableCell>
                <asp:Label ID="lbl_repetitions" runat="server" Text="Number of repetitions of the broadcast message"></asp:Label>
            </asp:TableCell>
            <asp:TableCell>
                <asp:TextBox ID="txt_repetitions" runat="server"></asp:TextBox>
            </asp:TableCell>
            <asp:TableCell>
                <asp:RequiredFieldValidator ID="req_repetitions" runat="server" Text="*" ErrorMessage="Number of repetitions missing" ControlToValidate="txt_repetitions"></asp:RequiredFieldValidator>
                <asp:RangeValidator id="rng_repetitions" Type="Integer" runat="server" MinimumValue="0" ControlToValidate="txt_repetitions" Text="*" ErrorMessage="Integer value required for repetition"></asp:RangeValidator>
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
    </asp:Table>
</asp:Content>