<%@ Page MasterPageFile="~/MasterPage.master" Language="C#" AutoEventWireup="true"  CodeFile="user_admin.aspx.cs" Inherits="user_admin" %>

<asp:Content ID="Content1" ContentPlaceHolderID="body" runat="server">
    <asp:Table ID="Table1" runat="server">
        <asp:TableRow>
            <asp:TableCell><asp:Label runat="server" ID="lbl_overview" Text="Overview Users"></asp:Label></asp:TableCell>
        </asp:TableRow>
        <asp:TableRow>
            <asp:TableCell ColumnSpan="4">
                <asp:ListBox ID="lst_users" runat="server" Height="100" Width="450" OnSelectedIndexChanged="fill_form" AutoPostBack="True"></asp:ListBox>
            </asp:TableCell>
        </asp:TableRow>
        <asp:TableRow>
            <asp:TableCell>
                <asp:Button ID="btn_create" runat="server" Text="Create" OnClick="btn_create_Click"/>
            </asp:TableCell>
            <asp:TableCell>
                &nbsp;
            </asp:TableCell>
            <asp:TableCell>
                &nbsp;
            </asp:TableCell>
        </asp:TableRow>
        <asp:TableRow>
            <asp:TableCell><asp:Label runat="server" ID="lbl_userdetails" Text="User Details"></asp:Label></asp:TableCell>
        </asp:TableRow>
        <asp:TableRow>
            <asp:TableCell>
                <asp:Label ID="Label1" runat="server" Text="User"></asp:Label>
            </asp:TableCell>
            <asp:TableCell>
                <asp:TextBox ID="txt_username" runat="server"></asp:TextBox><asp:RequiredFieldValidator runat="server" id="validate_username" ControlToValidate="txt_username" Text="*" ErrorMessage="User is required"></asp:RequiredFieldValidator>
            </asp:TableCell>
            <asp:TableCell HorizontalAlign="Right" >
                <asp:CheckBox ID="chk_blocked" runat="server" />
                <asp:Label ID="Label2" runat="server" Text="Blocked"></asp:Label>
            </asp:TableCell>
        </asp:TableRow>
        <asp:TableRow>
            <asp:TableCell>
                <asp:Label ID="Label3" runat="server" Text="User name"></asp:Label>
            </asp:TableCell>
            <asp:TableCell>
                <asp:TextBox ID="txt_firstname" runat="server"></asp:TextBox><asp:RequiredFieldValidator runat="server" id="RequiredFieldValidator1" ControlToValidate="txt_firstname" Text="*" ErrorMessage="Username is required"></asp:RequiredFieldValidator>
            </asp:TableCell>
            <asp:TableCell HorizontalAlign="Right" >
                <asp:TextBox ID="txt_blocked" runat="server" Width="70"></asp:TextBox>
            </asp:TableCell>
        </asp:TableRow>
        <asp:TableRow>
            <asp:TableCell>
                <asp:Label ID="Label4" runat="server" Text="Password"></asp:Label>
            </asp:TableCell>
            <asp:TableCell>
                <asp:TextBox ID="txt_password" runat="server" TextMode="Password"></asp:TextBox>
            </asp:TableCell>
        </asp:TableRow>
         <asp:TableRow>
            <asp:TableCell>
                <asp:Label ID="Label9" runat="server" Text="Organization"></asp:Label>
            </asp:TableCell>
            <asp:TableCell>
                <asp:TextBox ID="txt_organization" runat="server" ></asp:TextBox>
            </asp:TableCell>
        </asp:TableRow>
        <asp:TableRow>
            <asp:TableCell></asp:TableCell>
            <asp:TableCell>
                <asp:RadioButton ID="rad_regional" runat="server" GroupName="users" value="2" OnCheckedChanged="admin_Checked" AutoPostBack="True" />
                <asp:Label ID="Label5" runat="server" Text="Regional"></asp:Label>
            </asp:TableCell>
        </asp:TableRow>
        <asp:TableRow>
            <asp:TableCell></asp:TableCell>
            <asp:TableCell>
                <asp:RadioButton ID="rad_sregional" runat="server" GroupName="users" value="3" OnCheckedChanged="admin_Checked" AutoPostBack="True" />
                <asp:Label ID="Label6" runat="server" Text="Super Regional"></asp:Label>
            </asp:TableCell>
        </asp:TableRow>
        <asp:TableRow>
            <asp:TableCell></asp:TableCell>
            <asp:TableCell>
                <asp:RadioButton ID="rad_national" runat="server" GroupName="users" value="5" OnCheckedChanged="admin_Checked" AutoPostBack="True" />
                <asp:Label ID="Label7" runat="server" Text="National"></asp:Label>
            </asp:TableCell>
        </asp:TableRow>
        <asp:TableRow>
            <asp:TableCell></asp:TableCell>
            <asp:TableCell>
                <asp:RadioButton ID="rad_administrator" runat="server" GroupName="users" value="7" OnCheckedChanged="admin_Checked" AutoPostBack="True" />
                <asp:Label ID="Label8" runat="server" Text="Administrator"></asp:Label>
            </asp:TableCell>
        </asp:TableRow>
        <asp:TableRow>
        <asp:TableCell></asp:TableCell>
            <asp:TableCell>
                <asp:ListBox ID="lst_regions" runat="server" SelectionMode="Multiple"></asp:ListBox>
            </asp:TableCell>
        </asp:TableRow>
        <asp:TableRow>
            <asp:TableCell ColumnSpan="4" HorizontalAlign="Right">
                <asp:Button ID="btn_save" runat="server" Text="Save" OnClick="btn_save_Click"/>
            </asp:TableCell>
        </asp:TableRow>
        <asp:TableRow>
            <asp:TableCell ColumnSpan="3"><asp:ValidationSummary ID="ValidationSummary1" runat="server" /></asp:TableCell>
        </asp:TableRow>
    </asp:Table>
</asp:Content>