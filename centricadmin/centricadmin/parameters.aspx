<%@ Page MasterPageFile="~/MasterPage.master" Language="C#" AutoEventWireup="true" Inherits="parameters" Codebehind="parameters.aspx.cs" %>

<asp:Content ID="Content1" ContentPlaceHolderID="body" runat="server">
    <asp:Table ID="Table2" runat="server">
        <asp:TableHeaderRow>
            <asp:TableHeaderCell HorizontalAlign="Left"><asp:Label ID="Label4" runat="server" Text="Overview Parameters"></asp:Label></asp:TableHeaderCell>
        </asp:TableHeaderRow>
        <asp:TableRow>
            <asp:TableCell>
                <asp:Label ID="Label1" runat="server" Text="Incorrect logons"></asp:Label>
            </asp:TableCell>
            <asp:TableCell>
                <asp:TextBox ID="txt_inclogons" runat="server"></asp:TextBox>                
            </asp:TableCell>
            <asp:TableCell>
                <asp:RequiredFieldValidator ID="RequiredFieldValidator4" runat="server" text="*" ErrorMessage="Number of incorrect logons missing" ControlToValidate="txt_inclogons"></asp:RequiredFieldValidator>
                <!-- Error message is specified in page_load -->
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
                <!-- Error message is specified in page_load -->
                <asp:RangeValidator id="rng_channel" Type="Integer" runat="server" MinimumValue="0" ControlToValidate="txt_channel" Text="*" ErrorMessage="Integer value required for Channel number for NL-Alert-messages"></asp:RangeValidator>
            </asp:TableCell>
        </asp:TableRow>
        <asp:TableRow>
            <asp:TableCell>
                <asp:Label ID="lbl_second_channel" runat="server" Text="Second channel number for NL-Alert-messages"></asp:Label>
            </asp:TableCell>
            <asp:TableCell>
                <asp:TextBox ID="txt_second_channel" runat="server"></asp:TextBox>
            </asp:TableCell>
            <asp:TableCell>
                <asp:Label ID="lbl_activate_second_channel" runat="server" Text="Active:"></asp:Label>
            </asp:TableCell>
            <asp:TableCell>
                <asp:CheckBox ID="chk_second_channel" runat="server" />
            </asp:TableCell>
            <asp:TableCell>
                <asp:RequiredFieldValidator ID="req_second_channel" runat="server" Text="*" ErrorMessage="Channel number for second channel missing" ControlToValidate="txt_second_channel"></asp:RequiredFieldValidator>
                <!-- Error message is specified in page_load -->
                <asp:RangeValidator id="rng_second_channel" Type="Integer" runat="server" MinimumValue="0" ControlToValidate="txt_second_channel" Text="*" ErrorMessage="Integer value required for Second channel number for NL-Alert-messages"></asp:RangeValidator>
                <asp:CustomValidator ID="val_second_channel" runat="server" Text="*" ErrorMessage="There is a problem with the second channel number for NL-Alert-messages" ControlToValidate="txt_second_channel" ClientValidationFunction="validateSecondChannel" ></asp:CustomValidator>
                <asp:CompareValidator ID="cmp_first_second_channel" runat="server" Text="*" ErrorMessage="Primary and Secondary channels cannot be the same" ControlToValidate="txt_channel" ControlToCompare="txt_second_channel" Type="Integer" Operator="NotEqual"></asp:CompareValidator>
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
                <!-- Error message is specified in page_load -->
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
                <!-- Error message is specified in page_load -->
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
                <!-- Error message is specified in page_load -->
                <asp:RangeValidator id="rng_interval" Type="Integer" runat="server" MinimumValue="0" ControlToValidate="txt_interval" Text="*" ErrorMessage="Integer value required for sending interval"></asp:RangeValidator>
            </asp:TableCell>
        </asp:TableRow>
        <asp:TableRow>
            <asp:TableCell>
                <asp:Label ID="lbl_duration" runat="server" Text="Duration of the broadcast message"></asp:Label>
            </asp:TableCell>
            <asp:TableCell>
                <asp:TextBox ID="txt_duration" runat="server"></asp:TextBox>
            </asp:TableCell>
            <asp:TableCell>
                <asp:RequiredFieldValidator ID="req_repetitions" runat="server" Text="*" ErrorMessage="Duration missing" ControlToValidate="txt_duration" />
                <asp:RangeValidator id="rng_repetitions" Type="Integer" runat="server" MinimumValue="1" MaximumValue="1440" ControlToValidate="txt_duration" Text="*" ErrorMessage="Integer value between 1 and 1440 required for duration" />
            </asp:TableCell>
        </asp:TableRow>
        <asp:TableRow>
            <asp:TableCell>
                <asp:Label ID="lbl_pagesize" runat="server" Text="Number of characters per page"></asp:Label>
            </asp:TableCell>
            <asp:TableCell>
                <asp:TextBox ID="txt_pagesize" runat="server"></asp:TextBox>
            </asp:TableCell>
            <asp:TableCell>
                <asp:RequiredFieldValidator ID="req_pagesize" runat="server" Text="*" ErrorMessage="Characters per page missing" ControlToValidate="txt_pagesize" />
                <asp:RangeValidator id="rng_pagesize" Type="Integer" runat="server" MinimumValue="1" MaximumValue="93" ControlToValidate="txt_pagesize" Text="*" ErrorMessage="Integer value between 1 and 93 required for page characters" />
            </asp:TableCell>
        </asp:TableRow>
        <asp:TableRow>
            <asp:TableCell>
                <asp:Label ID="lbl_maxpages" runat="server" Text="Number of pages"></asp:Label>
            </asp:TableCell>
            <asp:TableCell>
                <asp:TextBox ID="txt_maxpages" runat="server"></asp:TextBox>
            </asp:TableCell>
            <asp:TableCell>
                <asp:RequiredFieldValidator ID="req_maxpages" runat="server" Text="*" ErrorMessage="Max number of pages missing" ControlToValidate="txt_maxpages" />
                <asp:RangeValidator id="rng_maxpages" Type="Integer" runat="server" MinimumValue="1" MaximumValue="15" ControlToValidate="txt_maxpages" Text="*" ErrorMessage="Integer value between 1 and 15 required for max pages" />
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