<%@ Page MasterPageFile="~/MasterPage.master" Language="C#" AutoEventWireup="true" Inherits="predefine_area" Theme="sampleTheme" Codebehind="predefine_area.aspx.cs" %>
<asp:Content ContentPlaceHolderID="body" runat="server">
    <asp:Table ID="Table1" runat="server">
        <asp:TableRow>
            <asp:TableCell>&nbsp;</asp:TableCell>
            <asp:TableCell>
                <asp:TextBox runat="server" ID="txt_name"></asp:TextBox>
            </asp:TableCell>
        </asp:TableRow>
        <asp:TableRow>
            <asp:TableCell RowSpan="2">
                <asp:TreeView ID="TreeView1" runat="server" OnSelectedNodeChanged="TreeView1_changed" style="overflow:auto" Width="200" oncontextmenu="return showmenuie5(event)" BorderColor="AliceBlue" HoverNodeStyle-BackColor="WhiteSmoke" Height="480">
            </asp:TreeView>
            </asp:TableCell>
        </asp:TableRow>
        <asp:TableRow>
            <asp:TableCell>
                <applet name="mapapplet" id="mapapplet" width="640" height="480">
                    <param name="jnlp_href" value="javaapp/jnlptest.jnlp" />
                </applet>
            </asp:TableCell>
        </asp:TableRow>
        <asp:TableRow>
            <asp:TableCell HorizontalAlign="Right" ColumnSpan="2">
                <asp:Button ID="btn_save" runat="server" Text="Save" OnClick="btn_save_Click" />
            </asp:TableCell>
        </asp:TableRow>
    </asp:Table>
    <asp:TextBox ID="txt_parent" runat="server" style="visibility:visible"></asp:TextBox>
    <asp:TextBox ID="txt_id" runat="server" style="visibility:visible"></asp:TextBox>
    <div ID="Panel2" runat="server" style="visibility:hidden" bordercolor="Black" onmouseover="highlightie5(event)" onmouseout="lowlightie5(event)" onclick="jumptoie5(event)"> 
          <div>
            <asp:LinkButton ID="LinkButton1" runat="server" OnClick="new_click" 
              >New Node
            </asp:LinkButton>
          </div>
          <div >
            <asp:LinkButton ID="LinkButton2" runat="server"  OnClick="edit_click"
              >Edit Node
            </asp:LinkButton>
          </div> 
          <hr />

          <div>
            <asp:LinkButton ID="LinkButton3" runat="server" OnClick="delete_click"
              >Delete Node
            </asp:LinkButton>
          </div> 
          <hr /> 
    </div>
</asp:Content>