<%@ Page MasterPageFile="~/MasterPage.master" Language="C#" AutoEventWireup="true" CodeFile="predefine_text.aspx.cs" Inherits="predefine_text" Theme="sampleTheme" %>

<asp:Content ContentPlaceHolderID="body" runat="server">

<asp:Table runat="server" BorderStyle="Dashed">
    <asp:TableRow>
        <asp:TableCell><asp:Label ID="Label3" runat="server" Text="Overview predefined texts"></asp:Label></asp:TableCell>
        <asp:TableCell><asp:Label ID="Label1" runat="server" Text="Details predefined text"></asp:Label></asp:TableCell>
    </asp:TableRow>
    <asp:TableRow>
        <asp:TableCell RowSpan="2" VerticalAlign="Top">
            <asp:TreeView ID="TreeView1" runat="server" OnSelectedNodeChanged="TreeView1_changed" style="overflow:auto" Width="200" oncontextmenu="return showmenuie5(event)" BorderColor="AliceBlue" HoverNodeStyle-BackColor="WhiteSmoke" Height="200">
            </asp:TreeView>
        </asp:TableCell>
        <asp:TableCell>
            <asp:TextBox ID="txt_name" runat="server" Width="300"></asp:TextBox>
        </asp:TableCell>
    </asp:TableRow>
    <asp:TableRow>
        <asp:TableCell>
            <asp:TextBox ID="txt_message" runat="server" TextMode="MultiLine" Height="170" Width="300"></asp:TextBox>
        </asp:TableCell>
    </asp:TableRow>
    <asp:TableRow>
        <asp:TableCell HorizontalAlign="Right" ColumnSpan="2">
            <asp:Button ID="btn_save" runat="server" Text="Save" OnClick="btn_save_Click"/>
        </asp:TableCell>
    </asp:TableRow>
</asp:Table>
<asp:TextBox ID="txt_parent" runat="server" style="visibility:hidden"></asp:TextBox>
<asp:TextBox ID="txt_id" runat="server" style="visibility:hidden"></asp:TextBox>
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