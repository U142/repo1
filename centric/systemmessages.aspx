<%@ Page MasterPageFile="~/MasterPage.master" Language="C#" AutoEventWireup="true"  CodeFile="systemmessages.aspx.cs" Inherits="systemmessages" %>

<%@ Register Assembly="AjaxControlToolkit" Namespace="AjaxControlToolkit" TagPrefix="asp" %>

<asp:Content ID="Content1" ContentPlaceHolderID="body" runat="server">
    <asp:ToolkitScriptManager ID="ScriptManager1" runat="server"></asp:ToolkitScriptManager>
    
    <asp:Table ID="Table2" runat="server">
        <asp:TableHeaderRow>
            <asp:TableHeaderCell ColumnSpan="6" HorizontalAlign="Left"><asp:Label ID="lbl_active_sysm" runat="server" Text="Overview System messages"></asp:Label></asp:TableHeaderCell>
        </asp:TableHeaderRow>
        <asp:TableRow>
            <asp:TableCell ColumnSpan="6">
                <asp:ListBox ID="lst_messages" runat="server" Height="200px" Width="471px" Font-Names="Courier New" OnSelectedIndexChanged="lst_messages_selectedindex" AutoPostBack="True">
                </asp:ListBox>
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
                <asp:TextBox ID="txt_message" runat="server" Height="79px" TextMode="MultiLine" Width="379px"></asp:TextBox>
                <asp:RequiredFieldValidator ID="RequiredFieldValidator1" runat="server" ControlToValidate="txt_message" Text="*" ErrorMessage="Text is requred"></asp:RequiredFieldValidator>
            </asp:TableCell>
        </asp:TableRow>
        <asp:TableRow>
            <asp:TableCell>
                <asp:Label ID="lbl_operator" runat="server" Text="Operator"></asp:Label>
            </asp:TableCell>
            <asp:TableCell ColumnSpan="4">
                <asp:DropDownList ID="ddl_operator" runat="server" Width="217px">
                    <asp:ListItem Text="KPN" Value="1"></asp:ListItem>
                    <asp:ListItem Text="T-Mobile" Value="2"></asp:ListItem>
                    <asp:ListItem Text="Vodafone" Value="3"></asp:ListItem>
                    <asp:ListItem Text="UMS" Value="4"></asp:ListItem>
                    <asp:ListItem Text="Centric" Value="5"></asp:ListItem>
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
                
                <asp:CalendarExtender ID="CalendarExtender1" runat="server" PopupPosition="Right" PopupButtonID="Image1"
                        TargetControlID="txt_activate" Format="dd-MM-yyyy">
                    </asp:CalendarExtender>
                <asp:TextBox ID="txt_activate" runat="server" Width="70px" onFocus="javascript:this.blur();" ></asp:TextBox>&nbsp;<asp:Image ID="Image1" runat="server" ImageUrl="images/Calendar_scheduleHS.png" />
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
                    
                    <asp:CalendarExtender ID="CalendarExtender2" runat="server" PopupPosition="Right" PopupButtonID="Image2"
                        TargetControlID="txt_deactivate" Format="dd-MM-yyyy">
                </asp:CalendarExtender>
                <asp:TextBox ID="txt_deactivate" runat="server" Width="70px" ></asp:TextBox>&nbsp;<asp:Image ID="Image2" runat="server" ImageUrl="images/Calendar_scheduleHS.png" /> 
                <asp:RegularExpressionValidator ID="RegularExpressionValidator1" runat="server" ErrorMessage="Date has to be blank, 0 or in this format dd-MM-yyyy" Text="*" ControlToValidate="txt_deactivate" ValidationExpression="(\d{2}-\d{2}-\d{4})|([0])"></asp:RegularExpressionValidator>
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