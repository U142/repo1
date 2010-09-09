﻿<%@ Page MasterPageFile="~/MasterPage.master" Language="C#" AutoEventWireup="true"  CodeFile="user_admin.aspx.cs" Inherits="user_admin" %>

<asp:Content ID="Content1" ContentPlaceHolderID="body" runat="server">
    <script type="text/javascript">
        function passwordValidator(oSrc, args) {
            if (document.getElementById("ctl00_body_selected").value.length == 0) {
                args.isValid = false;
            }
        }
 
    </script>
    <asp:Table ID="Table1" runat="server">
        <asp:TableHeaderRow>
            <asp:TableHeaderCell HorizontalAlign="Left"><asp:Label runat="server" ID="lbl_overview" Text="Overview Users"></asp:Label></asp:TableHeaderCell>
        </asp:TableHeaderRow>
        <asp:TableRow>
            <asp:TableCell ColumnSpan="4">
                <asp:ListBox ID="lst_users" runat="server" Height="100" Width="450" OnSelectedIndexChanged="fill_form" AutoPostBack="True" Visible="false"></asp:ListBox>
                <asp:Table runat="server" ID="tbl_users" GridLines="Both">
                    
                </asp:Table>
            </asp:TableCell>
        </asp:TableRow>
        <asp:TableRow>
            <asp:TableCell>
                <asp:Button ID="btn_create" runat="server" Text="Create" OnClick="btn_create_Click" CausesValidation="false" />
            </asp:TableCell>
            <asp:TableCell>
                &nbsp;
            </asp:TableCell>
            <asp:TableCell>
                &nbsp;
            </asp:TableCell>
        </asp:TableRow>
        <asp:TableHeaderRow>
            <asp:TableHeaderCell HorizontalAlign="Left"><asp:Label runat="server" ID="lbl_userdetails" Text="User Details"></asp:Label></asp:TableHeaderCell>
        </asp:TableHeaderRow>
        <asp:TableRow>
            <asp:TableCell>
                <asp:Label ID="Label1" runat="server" Text="Username"></asp:Label>
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
                <asp:Label ID="Label3" runat="server" Text="Full name"></asp:Label>
            </asp:TableCell>
            <asp:TableCell>
                <asp:TextBox ID="txt_firstname" runat="server"></asp:TextBox><asp:RequiredFieldValidator runat="server" id="RequiredFieldValidator1" ControlToValidate="txt_firstname" Text="*" ErrorMessage="Username is required"></asp:RequiredFieldValidator>
            </asp:TableCell>
            <asp:TableCell HorizontalAlign="Right" >
                <asp:TextBox ID="txt_blocked" runat="server" Width="70" Enabled="false"></asp:TextBox>
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
                <asp:RadioButton ID="rad_regional" runat="server" GroupName="users" value="2" OnCheckedChanged="admin_Checked" AutoPostBack="True" Checked="true" />
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
                <asp:ListBox ID="lst_regions" runat="server" SelectionMode="Multiple" OnSelectedIndexChanged="region_select" AutoPostBack="True"></asp:ListBox><asp:RequiredFieldValidator runat="server" id="req_regions" ControlToValidate="lst_regions" Text="*" ErrorMessage="Region must be selected"></asp:RequiredFieldValidator>
            </asp:TableCell>
            <asp:TableCell HorizontalAlign="Right">
                <asp:Button ID="btn_reload_regions" runat="server" Text="Reload regions" onclick="reload_regions_click" CausesValidation="false"/>
            </asp:TableCell>
        </asp:TableRow>
        <asp:TableRow>
            <asp:TableCell>
                <asp:Label ID="Label4" runat="server" Text="Password"></asp:Label>
            </asp:TableCell>
            <asp:TableCell>
                <asp:TextBox ID="txt_password" runat="server" TextMode="Password" style="width:150px"></asp:TextBox>
                <asp:RequiredFieldValidator ID="RequiredFieldValidator2" runat="server" ControlToValidate="txt_password" Text="*" ErrorMessage="Password is required"></asp:RequiredFieldValidator>
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
    <asp:Label runat="server" ID="lbl_feedback" Text=""></asp:Label>
    <asp:TextBox runat="server" ID="selected" Text="" style="visibility:hidden" onchange="alert('Value');" />
</asp:Content>