<%@ Page MasterPageFile="~/MasterPage.master" Language="C#" AutoEventWireup="true" Inherits="predefine_text" Theme="sampleTheme" Codebehind="predefine_text.aspx.cs" %>
<%@ MasterType TypeName="MasterPage" %>

<asp:Content ContentPlaceHolderID="body" runat="server">
<script type="text/javascript">
    function ValidateStringLength(source, arguments)   
    {
         var slen = document.getElementById("body_txt_message").value.length;
         var text = document.getElementById("body_txt_message").value;
         var num_char = 1302;
         
         // alert(arguments.Value + '\n' + slen);
         if (getLength() <= num_char) {   
             arguments.IsValid = true;   
         } else {   
             arguments.IsValid = false;
         }
         
     }
     function getLength() {
        var slen = document.getElementById("body_txt_message").value.length;
        var text = document.getElementById("body_txt_message").value;
        var num_char = 1302;
        var i;
        var tot = 0;
        var extended = /[|^€{}\[\]~\\]/;

        var re = new RegExp(extended);
        var m = null;
        var subtext = "";
        
        for (i = 0; i < slen; i++) {
            m = re.exec(text.charAt(i))
            if (m != null)
                tot++;
            tot++;
        }
        //alert(tot + " of " + num_char);
        document.getElementById("body_lbl_count").innerHTML = tot + " of " + num_char + "&nbsp;&nbsp";
                
        return tot;
     }  
</script> 
<asp:Table runat="server">
    <asp:TableHeaderRow>
        <asp:TableHeaderCell HorizontalAlign="Left"><asp:Label ID="Label3" runat="server" Text="Overview predefined texts"></asp:Label></asp:TableHeaderCell>
        <asp:TableHeaderCell HorizontalAlign="Left"><asp:Label ID="Label1" runat="server" Text="Details predefined text"></asp:Label></asp:TableHeaderCell>
    </asp:TableHeaderRow>
    <asp:TableRow>
        <asp:TableCell RowSpan="2" VerticalAlign="Top">
            <asp:TreeView ID="TreeView1" runat="server" OnSelectedNodeChanged="TreeView1_changed" style="overflow:auto" Width="200" BorderColor="AliceBlue" HoverNodeStyle-BackColor="WhiteSmoke" Height="200">
            </asp:TreeView>
        </asp:TableCell>
        <asp:TableCell>
            <asp:TextBox ID="txt_name" runat="server" Width="300" Enabled="false"></asp:TextBox><asp:RequiredFieldValidator
                ID="RequiredFieldValidator1" runat="server" Text="*" ErrorMessage="Name is required" ControlToValidate="txt_name"></asp:RequiredFieldValidator>
        </asp:TableCell>
    </asp:TableRow>
    <asp:TableRow>
        <asp:TableCell>
                <asp:TextBox ID="txt_message" runat="server" TextMode="MultiLine" Height="170" Width="300" MaxLength="14" onkeyup="javascript:getLength()" ></asp:TextBox>
        </asp:TableCell>
    </asp:TableRow>
    <asp:TableRow>
        <asp:TableCell HorizontalAlign="Right" ColumnSpan="2">
            <asp:Label ID="lbl_count" runat="server" Text=""></asp:Label>
            <asp:Button ID="btn_acquire_lock" runat="server" Text="Acquire lock" OnClick="btn_acquire_lock_Click" CausesValidation="false" />
            <asp:Button ID="btn_release_lock" runat="server" Text="Release lock" OnClick="btn_release_lock_Click" CausesValidation="false" />
            <asp:Button ID="btn_save" runat="server" Text="Save" OnClick="btn_save_Click" Enabled="false"/>
        </asp:TableCell>
    </asp:TableRow>
    <asp:TableRow>
        <asp:TableCell ColumnSpan="2">
            <asp:Label ID="lbl_error" runat="server" Text="" ForeColor="Red"></asp:Label>
        </asp:TableCell>
    </asp:TableRow>
</asp:Table>
<asp:Table>
    <asp:TableRow>
        <asp:TableCell><asp:Label ID="Label2" runat="server" Text="To start press aquire lock and then right click on a node to create child node or in the overview rectangle to create a root node"></asp:Label></asp:TableCell>
    </asp:TableRow>
</asp:Table>
<asp:TextBox ID="txt_parent" runat="server" style="visibility:hidden"></asp:TextBox>
<asp:TextBox ID="txt_id" runat="server" style="visibility:hidden"></asp:TextBox>
<div ID="Panel2" runat="server" style="visibility:hidden" bordercolor="Black" onmouseover="highlightie5(event)" onmouseout="lowlightie5(event)" onclick="jumptoie5(event)"> 
      <div>
        <asp:LinkButton ID="LinkButton1" runat="server" OnClick="new_click" BackColor="White" CausesValidation="false"
          >New Predefined Text
        </asp:LinkButton>
        &nbsp;
      </div>
      <div >
        <asp:LinkButton ID="LinkButton2" runat="server"  OnClick="edit_click" BackColor="White" CausesValidation="false"
          >Edit Predefined Text
        </asp:LinkButton>
        &nbsp;
      </div> 
      <div>
        <asp:LinkButton ID="LinkButton3" runat="server" OnClick="delete_click" BackColor="White" CausesValidation="false"
          >Delete Predefined Text
        </asp:LinkButton>
      </div> 
</div>
<asp:CustomValidator ID="val_message" runat="server"    
    ClientValidationFunction="ValidateStringLength" ControlToValidate="txt_message"    
    ErrorMessage="" Text="Message cannot be longer than 1302 characters" />
<asp:ValidationSummary ID="ValidationSummary1" runat="server" />
</asp:Content>