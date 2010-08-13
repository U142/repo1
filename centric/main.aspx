<%@ Page MasterPageFile="~/MasterPage.master" Language="C#" AutoEventWireup="true"  CodeFile="main.aspx.cs" Inherits="_Default" %>

<asp:Content ContentPlaceHolderID="body" runat="server">
    <asp:Table ID="Table2" runat="server">
        <asp:TableRow>
            <asp:TableCell ColumnSpan="2"><asp:Label ID="lbl_active_sysm" runat="server" Text="Active system messages"></asp:Label></asp:TableCell>
            <asp:TableCell><asp:Label ID="Label1" runat="server" Text="Message text"></asp:Label></asp:TableCell>
        </asp:TableRow>
        <asp:TableRow>
            <asp:TableCell ColumnSpan="2">
                <asp:ListBox ID="lst_messages" runat="server" Height="336px" Width="471px" OnSelectedIndexChanged="lst_messages_selectedindex" AutoPostBack="True">
                </asp:ListBox>
            </asp:TableCell>
            <asp:TableCell VerticalAlign="Top">
                <asp:TextBox ID="txt_view_message" runat="server" TextMode="MultiLine" Width="305px" Height="100" Enabled="false"></asp:TextBox>
            </asp:TableCell>
        </asp:TableRow>
        <asp:TableRow>
            <asp:TableCell><asp:Button ID="btn_edit" runat="server" Text="Edit" OnClick="btn_edit_Click"/></asp:TableCell>
            <asp:TableCell><asp:Button ID="btn_deactivate" runat="server" Text="Deactivate" /></asp:TableCell>
        </asp:TableRow>
    </asp:Table>
    <asp:Table ID="Table1" runat="server">
        <asp:TableRow>
            <asp:TableCell ColumnSpan="4"><asp:Label ID="lbl_new_sysm" runat="server" Text="New System message"></asp:Label></asp:TableCell>
        </asp:TableRow>
        <asp:TableRow>
            <asp:TableCell RowSpan="4">
                <asp:TextBox ID="txt_message" runat="server" Height="79px" TextMode="MultiLine" Width="465px"></asp:TextBox>
            </asp:TableCell>
            <asp:TableCell>
                <asp:Label ID="lbl_operator" runat="server" Text="Operator"></asp:Label>
            </asp:TableCell>
            <asp:TableCell ColumnSpan="3">
                <asp:DropDownList ID="ddl_operator" runat="server">
                    <asp:ListItem Text="KPN" Value="1"></asp:ListItem>
                    <asp:ListItem Text="T-Mobile" Value="2"></asp:ListItem>
                    <asp:ListItem Text="Vodafone" Value="3"></asp:ListItem>
                    <asp:ListItem Text="UMS" Value="4"></asp:ListItem>
                    <asp:ListItem Text="Centric" Value="5"></asp:ListItem>
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
                <asp:TextBox ID="txt_activate" runat="server"></asp:TextBox> 
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
                <asp:TextBox ID="txt_deactivate" runat="server"></asp:TextBox> 
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
    </asp:Table>
</asp:Content>