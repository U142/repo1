<%@ Page MasterPageFile="~/MasterPage.master" Language="C#" AutoEventWireup="true" Inherits="main" Codebehind="main.aspx.cs" %>
<%@ Register Assembly="AjaxControlToolkit" Namespace="AjaxControlToolkit" TagPrefix="ajaxToolkit" %>
   

<asp:Content ContentPlaceHolderID="body" runat="server">
    <script type="text/javascript">
       
        function validateActivate(oSrc, args) {
            if (document.getElementById("body_txt_activate").value.length > 1) {
                // Sjekk format
                //alert("test");
                
                if (!validateDate(document.getElementById("body_txt_activate").value)) {
                    document.getElementById("body_activate_validate").setAttribute("errorMessage", "Incorrect date");
                    args.IsValid = false;
                    return;
                }
                else {
                    //catch(err) {
                    //    document.getElementById("body_activate_validate").setAttribute("errorMessage", "Invalid date format");
                    //    args.IsValid = false;
                    //}
                    // Før nåtid?
                    //alert("test");
                    var date = new Date();
                    var now = parseInt("" + date.getFullYear() + padLeft(date.getMonth() + 1, 2) + padLeft(date.getDate(), 2) + padLeft(date.getHours(), 2) + padLeft(date.getMinutes(), 2) + "");
                    var activatetext = document.getElementById("body_txt_activate").value;
                    var activate = parseInt(activatetext.substr(6, 4) + activatetext.substr(3, 2) + activatetext.substr(0, 2) + document.getElementById("body_ddl_activate_h").value + document.getElementById("body_ddl_activate_m").value);
                    //alert("date: " + activate);
                    //alert(activate + " < " + now);
                    if (activate < now) {
                        document.getElementById("body_activate_validate").setAttribute("errorMessage", "Cannot set activation date earlier than current date and time");
                        args.IsValid = false;
                    }
                }
            }

        }
        function validateDeactivate(oSrc, args) {
            
            if (document.getElementById("body_txt_deactivate").value.length > 1) {
                if (!validateDate(document.getElementById("body_txt_deactivate").value)) {
                    document.getElementById("body_deactivate_validate").setAttribute("errorMessage", "Incorrect date");
                    args.IsValid = false;
                }
                else {
                    var activate;
                    // Se om activate er satt
                    if (document.getElementById("body_txt_activate").value.length > 1) {
                        // Sjekk format
                        var activatetext = document.getElementById("body_txt_activate").value;
                        if (!validateDate(document.getElementById("body_txt_activate").value)) {
                            document.getElementById("body_activate_validate").setAttribute("errorMessage", "Incorrect date");
                            args.IsValid = false;
                            return;
                        }
                        else
                            activate = parseInt(activatetext.substr(6, 4) + activatetext.substr(3, 2) + activatetext.substr(0, 2) + document.getElementById("body_ddl_activate_h").value + document.getElementById("body_ddl_activate_m").value);
                    }
                    else {
                        // Activate ikke satt
                        //alert("activate ikke satt");
                        var date = new Date();
                        activate = parseInt("" + date.getFullYear() + padLeft(date.getMonth() + 1, 2) + padLeft(date.getDate(), 2) + padLeft(date.getHours(), 2) + padLeft(date.getMinutes(),2) + "");
                    }
                    var deactivatetext = document.getElementById("body_txt_deactivate").value;
                    var deactivate = parseInt(deactivatetext.substr(6, 4) + deactivatetext.substr(3, 2) + deactivatetext.substr(0, 2) + document.getElementById("body_ddl_deactivate_h").value + document.getElementById("body_ddl_deactivate_m").value);
                    //alert(deactivate + " < " + activate);
                    if (deactivate <= activate) {
                        document.getElementById("body_deactivate_validate").setAttribute("errorMessage", "Deactivation date has to be after current date and time and activation time");
                        args.IsValid = false;
                        return;
                        //alert("Activate after deactivate");
                    }
                    else
                        args.IsValid = true;
                }
            }
        }

        function padLeft(value, len) {
            value = value.toString();
            while (value.length < len) {
                value = "0" + value.toString();
            }
            return value;
        }
        // 
    </script>
    <ajaxToolkit:ToolkitScriptManager ID="ScriptManager1" runat="server" CombineScripts="false"></ajaxToolkit:ToolkitScriptManager>    
    <asp:Table ID="Table2" runat="server">
        <asp:TableHeaderRow>
            <asp:TableHeaderCell HorizontalAlign="Left" ColumnSpan="2"><asp:Label ID="lbl_active_sysm" runat="server" Text="Active system messages"></asp:Label></asp:TableHeaderCell>
            <asp:TableHeaderCell HorizontalAlign="Left" ><asp:Label ID="Label1" runat="server" Text="Message text"></asp:Label></asp:TableHeaderCell>
        </asp:TableHeaderRow>
        <asp:TableRow>
            <asp:TableCell ColumnSpan="2">
                <asp:ListBox ID="lst_messages" runat="server" Height="336px" Width="471px" OnSelectedIndexChanged="lst_messages_selectedindex" AutoPostBack="True" Font-Names="Courier New">
                </asp:ListBox>
            </asp:TableCell>
            <asp:TableCell VerticalAlign="Top">
                <asp:TextBox ID="txt_view_message" runat="server" TextMode="MultiLine" Width="305px" Height="100" onFocus="javascript:this.blur();" AutoCompleteType="JobTitle"></asp:TextBox>
            </asp:TableCell>
        </asp:TableRow>
        <asp:TableRow>
            <asp:TableCell><asp:Button ID="btn_edit" runat="server" Text="Edit" OnClick="btn_edit_Click" CausesValidation="false" /></asp:TableCell>
            <asp:TableCell><asp:Button ID="btn_deactivate" runat="server" Text="Deactivate"  CausesValidation="false" OnClick="btn_deactivate_Click" /></asp:TableCell>
        </asp:TableRow>
    </asp:Table>
    <asp:Table ID="Table1" runat="server">
        <asp:TableHeaderRow>
            <asp:TableHeaderCell HorizontalAlign="Left" ColumnSpan="4"><asp:Label ID="lbl_new_sysm" runat="server" Text="New System message"></asp:Label></asp:TableHeaderCell>
        </asp:TableHeaderRow>
        <asp:TableRow>
            <asp:TableCell RowSpan="4">
                <asp:TextBox ID="txt_message" runat="server" Height="79px" TextMode="MultiLine" Width="465px"></asp:TextBox>
                <asp:RequiredFieldValidator ID="RequiredFieldValidator1" runat="server" ControlToValidate="txt_message" Text="*" ErrorMessage="Text is requred"></asp:RequiredFieldValidator>
            </asp:TableCell>
            <asp:TableCell>
                <asp:Label ID="lbl_operator" runat="server" Text="Operator"></asp:Label>
            </asp:TableCell>
            <asp:TableCell ColumnSpan="3">
                <asp:DropDownList ID="ddl_operator" runat="server">
                </asp:DropDownList>
            </asp:TableCell>
            <asp:TableCell RowSpan="4">
                <asp:Button ID="btn_activate_message" runat="server" Text="Activate message" OnClick="btn_activate_message_Click"/>
            </asp:TableCell>
        </asp:TableRow>
        <asp:TableRow>
            <asp:TableCell>
                <asp:Label ID="lbl_type" runat="server" Text="Type"></asp:Label>
            </asp:TableCell>
            <asp:TableCell ColumnSpan="3">
                <asp:DropDownList ID="ddl_type" runat="server">
                    <asp:ListItem Text="Planned outage" Value="0"></asp:ListItem>
                    <asp:ListItem Text="Unplanned outage" Value="1"></asp:ListItem>
                    <asp:ListItem Text="Other" Value="2"></asp:ListItem>
                </asp:DropDownList>
            </asp:TableCell>
        </asp:TableRow>
        <asp:TableRow>
            <asp:TableCell>
                <asp:Label ID="lbl_activate_on" runat="server" Text="Activate on"></asp:Label>
            </asp:TableCell>
            <asp:TableCell>
                <ajaxToolkit:CalendarExtender ID="CalendarExtender1" runat="server" PopupPosition="BottomRight" PopupButtonID="Image1" TargetControlID="txt_activate" Format="dd-MM-yyyy"></ajaxToolkit:CalendarExtender>
                <asp:TextBox ID="txt_activate" runat="server" Width="70px" ></asp:TextBox>&nbsp;<asp:Image ID="Image1" runat="server" ImageUrl="images/Calendar_scheduleHS.png" />
                <asp:RegularExpressionValidator ID="RegularExpressionValidator2" runat="server" ErrorMessage="Date has to be blank, 0 or in this format dd-MM-yyyy" Text="*" ControlToValidate="txt_activate" ValidationExpression="(\d{2}-\d{2}-\d{4})|([0])"></asp:RegularExpressionValidator>
                <asp:CustomValidator ClientValidationFunction="validateActivate" ControlToValidate="txt_activate" runat="server" id="activate_validate" Text="*" ErrorMessage="Please make sure the date is correct"></asp:CustomValidator>
            </asp:TableCell>
            <asp:TableCell>
                <asp:DropDownList ID="ddl_activate_h" runat="server">
                    <asp:ListItem>00</asp:ListItem>
                    <asp:ListItem>01</asp:ListItem>
                    <asp:ListItem>02</asp:ListItem>
                    <asp:ListItem>03</asp:ListItem>
                    <asp:ListItem>04</asp:ListItem>
                    <asp:ListItem>05</asp:ListItem>
                    <asp:ListItem>06</asp:ListItem>
                    <asp:ListItem>07</asp:ListItem>
                    <asp:ListItem>08</asp:ListItem>
                    <asp:ListItem>09</asp:ListItem>
                    <asp:ListItem>10</asp:ListItem>
                    <asp:ListItem>11</asp:ListItem>
                    <asp:ListItem>12</asp:ListItem>
                    <asp:ListItem>13</asp:ListItem>
                    <asp:ListItem>14</asp:ListItem>
                    <asp:ListItem>15</asp:ListItem>
                    <asp:ListItem>16</asp:ListItem>
                    <asp:ListItem>17</asp:ListItem>
                    <asp:ListItem>18</asp:ListItem>
                    <asp:ListItem>19</asp:ListItem>
                    <asp:ListItem>20</asp:ListItem>
                    <asp:ListItem>21</asp:ListItem>
                    <asp:ListItem>22</asp:ListItem>
                    <asp:ListItem>23</asp:ListItem>
                </asp:DropDownList>
            </asp:TableCell>
            <asp:TableCell>
                <asp:DropDownList ID="ddl_activate_m" runat="server">
                    <asp:ListItem>00</asp:ListItem>
                    <asp:ListItem>05</asp:ListItem>
                    <asp:ListItem>10</asp:ListItem>
                    <asp:ListItem>15</asp:ListItem>
                    <asp:ListItem>20</asp:ListItem>
                    <asp:ListItem>25</asp:ListItem>
                    <asp:ListItem>30</asp:ListItem>
                    <asp:ListItem>35</asp:ListItem>
                    <asp:ListItem>40</asp:ListItem>
                    <asp:ListItem>45</asp:ListItem>
                    <asp:ListItem>50</asp:ListItem>
                    <asp:ListItem>55</asp:ListItem>
                </asp:DropDownList>
            </asp:TableCell>
        </asp:TableRow>
        <asp:TableRow>
            <asp:TableCell>
                <asp:Label ID="lbl_deactivate_on" runat="server" Text="Deactivate on"></asp:Label>
            </asp:TableCell>
            <asp:TableCell>
                <ajaxToolkit:CalendarExtender ID="CalendarExtender2" runat="server" PopupPosition="BottomRight" PopupButtonID="Image2" TargetControlID="txt_deactivate" Format="dd-MM-yyyy"></ajaxToolkit:CalendarExtender>
                <asp:TextBox ID="txt_deactivate" runat="server" Width="70px" ></asp:TextBox>&nbsp;<asp:Image ID="Image2" runat="server" ImageUrl="images/Calendar_scheduleHS.png" />
                <asp:RegularExpressionValidator ID="RegularExpressionValidator1" runat="server" ErrorMessage="Date has to be blank, 0 or in this format dd-MM-yyyy" Text="*" ControlToValidate="txt_deactivate" ValidationExpression="(\d{2}-\d{2}-\d{4})|([0])"></asp:RegularExpressionValidator>
                <asp:CustomValidator ClientValidationFunction="validateDeactivate" ControlToValidate="txt_deactivate" runat="server" id="deactivate_validate" Text="*" ErrorMessage="Please make sure the date is correct"></asp:CustomValidator>
            </asp:TableCell>
            <asp:TableCell>
                <asp:DropDownList ID="ddl_deactivate_h" runat="server">
                    <asp:ListItem>00</asp:ListItem>
                    <asp:ListItem>01</asp:ListItem>
                    <asp:ListItem>02</asp:ListItem>
                    <asp:ListItem>03</asp:ListItem>
                    <asp:ListItem>04</asp:ListItem>
                    <asp:ListItem>05</asp:ListItem>
                    <asp:ListItem>06</asp:ListItem>
                    <asp:ListItem>07</asp:ListItem>
                    <asp:ListItem>08</asp:ListItem>
                    <asp:ListItem>09</asp:ListItem>
                    <asp:ListItem>10</asp:ListItem>
                    <asp:ListItem>11</asp:ListItem>
                    <asp:ListItem>12</asp:ListItem>
                    <asp:ListItem>13</asp:ListItem>
                    <asp:ListItem>14</asp:ListItem>
                    <asp:ListItem>15</asp:ListItem>
                    <asp:ListItem>16</asp:ListItem>
                    <asp:ListItem>17</asp:ListItem>
                    <asp:ListItem>18</asp:ListItem>
                    <asp:ListItem>19</asp:ListItem>
                    <asp:ListItem>20</asp:ListItem>
                    <asp:ListItem>21</asp:ListItem>
                    <asp:ListItem>22</asp:ListItem>
                    <asp:ListItem>23</asp:ListItem>
                </asp:DropDownList>
            </asp:TableCell>
            <asp:TableCell>
                <asp:DropDownList ID="ddl_deactivate_m" runat="server">
                    <asp:ListItem>00</asp:ListItem>
                    <asp:ListItem>05</asp:ListItem>
                    <asp:ListItem>10</asp:ListItem>
                    <asp:ListItem>15</asp:ListItem>
                    <asp:ListItem>20</asp:ListItem>
                    <asp:ListItem>25</asp:ListItem>
                    <asp:ListItem>30</asp:ListItem>
                    <asp:ListItem>35</asp:ListItem>
                    <asp:ListItem>40</asp:ListItem>
                    <asp:ListItem>45</asp:ListItem>
                    <asp:ListItem>50</asp:ListItem>
                    <asp:ListItem>55</asp:ListItem>
                </asp:DropDownList>
            </asp:TableCell>
        </asp:TableRow>
        <asp:TableRow>
            <asp:TableCell><asp:ValidationSummary ID="ValidationSummary1" runat="server"></asp:ValidationSummary></asp:TableCell>
        </asp:TableRow>
    </asp:Table>
</asp:Content>