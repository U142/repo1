<%@ Page MasterPageFile="~/MasterPage.master" Language="C#" AutoEventWireup="true" Inherits="systemmessages" Codebehind="systemmessages.aspx.cs" %>
<%@ Register Assembly="AjaxControlToolkit" Namespace="AjaxControlToolkit" TagPrefix="asp" %>



<asp:Content ID="Content1" ContentPlaceHolderID="body" runat="server">
    <script type="text/javascript">
        /*
        Må sjekke følgende:
        Har activate verid?
        -Ja-
            Er den før dagens dato
                -Nei-
                    Gudd
                -Ja-
                    Feilmelding
         -Nei-
            har deactivate verdi?
            -Ja-
                er den mindre enn dagens dato?
                    -Nei-
                        Gudd
                    -Ja-
                        Feilmelding
                er den mindre enn activate?
                    -Nei-
                        Gudd
                    -Ja-
                        Feilmelding
        
        
        */
        function validateActivate(oSrc, args) {
            if (document.getElementById("ctl00_body_txt_activate").value.length > 1) {
                // Sjekk format
                //alert("test");
                var re = new RegExp(/\d{2}-\d{2}-\d{4}/);
                if (re.exec(document.getElementById("ctl00_body_txt_activate").value) == null) {
                    document.getElementById("ctl00_body_activate_validate").setAttribute("errorMessage", "Incorrect date format");
                    args.IsValid = false;
                }
                /*
                else {
                    // Før nåtid?
                    //alert("test");
                    var date = new Date();
                    var now = parseInt("" + date.getFullYear() + padLeft(date.getMonth() + 1, 2) + padLeft(date.getDate(), 2) + padLeft(date.getHours(), 2) + padLeft(date.getMinutes(), 2) + "");
                    var activatetext = document.getElementById("ctl00_body_txt_activate").value;
                    var activate = parseInt(activatetext.substr(6, 4) + activatetext.substr(3, 2) + activatetext.substr(0, 2) + document.getElementById("ctl00_body_ddl_activate_h").value + document.getElementById("ctl00_body_ddl_activate_m").value);
                    //alert("date: " + activate);
                    //alert(activate + " < " + now);
                    if (activate < now) {
                        document.getElementById("ctl00_body_activate_validate").setAttribute("errorMessage", "Cannot set activation date earlier than current date and time");
                        args.IsValid = false;
                    }
                }*/
            }

        }
        function validateDeactivate(oSrc, args) {

            if (document.getElementById("ctl00_body_txt_deactivate").value.length > 1) {
                
                if (!validateDate(document.getElementById("ctl00_body_txt_deactivate").value)) {
                    document.getElementById("ctl00_body_deactivate_validate").setAttribute("errorMessage", "Incorrect date");
                    args.IsValid = false;
                    return;
                }
                else {
                    var activate;
                    var date = new Date();
                    var now = parseInt("" + date.getFullYear() + padLeft(date.getMonth() + 1, 2) + padLeft(date.getDate(), 2) + padLeft(date.getHours(), 2) + padLeft(date.getMinutes(), 2) + "");
                    
                    // Se om activate er satt
                    if (document.getElementById("ctl00_body_txt_activate").value.length > 1) {
                        // Sjekk format
                        var activatetext = document.getElementById("ctl00_body_txt_activate").value;
                        
                        if (!validateDate(document.getElementById("ctl00_body_txt_activate").value)) {
                            document.getElementById("ctl00_body_activate_validate").setAttribute("errorMessage", "Incorrect date");
                            args.IsValid = false;
                            return;
                        }
                        else
                            activate = parseInt(activatetext.substr(6, 4) + activatetext.substr(3, 2) + activatetext.substr(0, 2) + document.getElementById("ctl00_body_ddl_activate_h").value + document.getElementById("ctl00_body_ddl_activate_m").value);
                    }
                    else {
                        // Activate ikke satt
                        //alert("activate ikke satt");
                        activate = parseInt("" + date.getFullYear() + padLeft(date.getMonth() + 1, 2) + padLeft(date.getDate(), 2) + padLeft(date.getHours(), 2) + padLeft(date.getMinutes(), 2) + "");
                    }

                    if (now > activate)
                        activate = now;
                    
                    var deactivatetext = document.getElementById("ctl00_body_txt_deactivate").value;
                    var deactivate = parseInt(deactivatetext.substr(6, 4) + deactivatetext.substr(3, 2) + deactivatetext.substr(0, 2) + document.getElementById("ctl00_body_ddl_deactivate_h").value + document.getElementById("ctl00_body_ddl_deactivate_m").value);
                    //alert(deactivate + " < " + activate);
                    if (deactivate <= activate) {
                        document.getElementById("ctl00_body_deactivate_validate").setAttribute("errorMessage", "Deactivation date has to be after current date and time and activation time");
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

    <asp:ToolkitScriptManager ID="ScriptManager1" runat="server"></asp:ToolkitScriptManager>
    
    <asp:Table ID="Table2" runat="server">
        <asp:TableHeaderRow>
            <asp:TableHeaderCell ColumnSpan="6" HorizontalAlign="Left"><asp:Label ID="lbl_active_sysm" runat="server" Text="Overview System messages"></asp:Label></asp:TableHeaderCell>
        </asp:TableHeaderRow>
        <asp:TableRow>
            <asp:TableCell ColumnSpan="6">
                <asp:ListBox ID="lst_messages" runat="server" Height="200px" Width="471px" Font-Names="Courier New" OnSelectedIndexChanged="lst_messages_selectedindex" AutoPostBack="True">
                </asp:ListBox>
                <asp:RequiredFieldValidator ID="RequiredFieldValidator3" runat="server" Text="*" ControlToValidate="lst_messages" ErrorMessage="Please select message to edit"></asp:RequiredFieldValidator>
            </asp:TableCell>
        </asp:TableRow>
        <asp:TableHeaderRow>
            <asp:TableHeaderCell HorizontalAlign="Left" ColumnSpan="6"><asp:Label ID="lbl_new_sysm" runat="server" Text="Message details"></asp:Label></asp:TableHeaderCell>
        </asp:TableHeaderRow>
        <asp:TableRow>
            <asp:TableCell>
                <asp:Label ID="lbl_message" runat="server" Text="Message text"></asp:Label>
            </asp:TableCell>
            <asp:TableCell ColumnSpan="5">
                <asp:TextBox ID="txt_message" runat="server" Height="79px" TextMode="MultiLine" Width="379px" ></asp:TextBox>
                <asp:RequiredFieldValidator ID="RequiredFieldValidator1" runat="server" ControlToValidate="txt_message" Text="*" ErrorMessage="Text is requred"></asp:RequiredFieldValidator>
            </asp:TableCell>
        </asp:TableRow>
        <asp:TableRow>
            <asp:TableCell>
                <asp:Label ID="lbl_operator" runat="server" Text="Operator"></asp:Label>
            </asp:TableCell>
            <asp:TableCell ColumnSpan="4">
                <asp:DropDownList ID="ddl_operator" runat="server" Width="217px">
                </asp:DropDownList>
            </asp:TableCell>
            <asp:TableCell RowSpan="4" VerticalAlign="Bottom" HorizontalAlign="Right">
                <asp:Button ID="btn_save" runat="server" Text="Save" OnClick="btn_save_Click"/>
            </asp:TableCell>
        </asp:TableRow>
        <asp:TableRow>
            <asp:TableCell>
                <asp:Label ID="lbl_type" runat="server" Text="Type"></asp:Label>
            </asp:TableCell>
            <asp:TableCell ColumnSpan="4">
                <asp:DropDownList ID="ddl_type" runat="server" Width="217px">
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
            <asp:TableCell Width="70px">
                
                <asp:CalendarExtender ID="CalendarExtender1" runat="server" PopupPosition="BottomRight" PopupButtonID="Image1"
                        TargetControlID="txt_activate" Format="dd-MM-yyyy">
                    </asp:CalendarExtender>
                <asp:TextBox ID="txt_activate" runat="server" Width="70px" onFocus="javascript:this.blur();" ></asp:TextBox>&nbsp;<asp:Image ID="Image1" runat="server" ImageUrl="images/Calendar_scheduleHS.png" />
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
            <asp:TableCell>:&nbsp;&nbsp;</asp:TableCell>
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
                <asp:RequiredFieldValidator ID="RequiredFieldValidator2" runat="server" ControlToValidate="txt_activate" Text="*" ErrorMessage="Activation date is required"></asp:RequiredFieldValidator>
            </asp:TableCell>
        </asp:TableRow>
        <asp:TableRow>
            <asp:TableCell>
                <asp:Label ID="lbl_deactivate_on" runat="server" Text="Deactivate on"></asp:Label>
            </asp:TableCell>
            <asp:TableCell>
                    <asp:CalendarExtender ID="CalendarExtender2" runat="server" PopupPosition="BottomRight" PopupButtonID="Image2"
                        TargetControlID="txt_deactivate" Format="dd-MM-yyyy">
                </asp:CalendarExtender>
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
            <asp:TableCell>:&nbsp;&nbsp;</asp:TableCell>
            <asp:TableCell Width="70px">
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
    </asp:Table>
    <asp:ValidationSummary ID="ValidationSummary1" runat="server" />
</asp:Content>