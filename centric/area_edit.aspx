<%@ Page MasterPageFile="~/MasterPage.master" Language="C#" AutoEventWireup="true" CodeFile="area_edit.aspx.cs" Inherits="area_edit" %>
<%@ MasterType TypeName="MasterPage" %>
<asp:Content ContentPlaceHolderID="body" runat="server">
    <script type="text/javascript" src="http://www.java.com/js/deployJava.js"></script>
    <script type="text/javascript">
        function getShape() {
            //alert(document.mapapplet.get());
            document.getElementById("ctl00_body_txt_coor").value = document.mapapplet.get();
            return false;
        }
        function setShape(coor, id, obsolete, timestamp, deptname) {
            
            document.getElementById("ctl00_body_txt_id").value = id;
            document.getElementById("ctl00_body_txt_obsolete_holder").value = obsolete;
            
            document.getElementById("ctl00_body_txt_name").disabled = true;
            document.getElementById("ctl00_body_txt_name").value = deptname;
            
            if (obsolete == 1) {
                document.getElementById("ctl00_body_chk_obsolete").checked = true;
                document.getElementById("ctl00_body_chk_obsolete").disabled = true;
                document.getElementById("ctl00_body_chk_obsolete").parentElement.setAttribute('disabled', 'true');
                document.getElementById("ctl00_body_txt_timestamp").value = timestamp;
            }
            else {
                document.getElementById("ctl00_body_chk_obsolete").checked = false;
                document.getElementById("ctl00_body_chk_obsolete").disabled = false;
                document.getElementById("ctl00_body_chk_obsolete").parentElement.removeAttribute('disabled');
                document.getElementById("ctl00_body_txt_timestamp").value = "";
            }
            document.getElementById("ctl00_body_txt_obsolete").value = timestamp;
            try {
                document.mapapplet.put(id);
            } catch (err) { }
            
            
            
            //return false;
        }
        
    </script>
    <asp:Table ID="table" runat="server">
        <asp:TableHeaderRow>
            <asp:TableHeaderCell HorizontalAlign="Left"><asp:Label ID="Label3" runat="server" Text="Overview Authorization Areas"></asp:Label></asp:TableHeaderCell>
        </asp:TableHeaderRow>
        <asp:TableRow>
            <asp:TableCell ColumnSpan="4">
                <asp:ListBox ID="lst_areas" runat="server" Height="150" Width="640"  Visible="false" ></asp:ListBox>
                <asp:Table runat="server" ID="tbl_areas" GridLines="Both"></asp:Table>
            </asp:TableCell>
        </asp:TableRow>
        <asp:TableRow>
            <asp:TableCell>
                <asp:Button ID="btn_create" runat="server" Text="Create" OnClick="btn_create_click"/></asp:TableCell>
        </asp:TableRow>
        <asp:TableHeaderRow>
            <asp:TableHeaderCell HorizontalAlign="Left"><asp:Label ID="Label4" runat="server" Text="Details Authorization area"></asp:Label></asp:TableHeaderCell>
        </asp:TableHeaderRow>
        <asp:TableRow>
            <asp:TableCell>
                <asp:Label ID="Label1" runat="server" Text="Authorization area"></asp:Label>
            </asp:TableCell>
            <asp:TableCell>
                <asp:TextBox ID="txt_name" runat="server" Enabled="false"></asp:TextBox></asp:TableCell>
            <asp:TableCell>
                <asp:CheckBox ID="chk_obsolete" runat="server" Enabled="false" />
                <asp:Label ID="Label2" runat="server" Text="Obsolete"></asp:Label>
            </asp:TableCell>
            <asp:TableCell>
                <asp:TextBox ID="txt_obsolete" runat="server" Text="" Enabled="false" ></asp:TextBox>
            </asp:TableCell>
        </asp:TableRow>
        <asp:TableRow>
            <asp:TableCell><input type="button" id="btn_zoom" value="zoom" OnClick="javascript:document.mapapplet.zoom();" /></asp:TableCell>
            <asp:TableCell><input type="button" id="btn_pan" runat="server" value="pan" OnClick="javascript:document.mapapplet.pan();" /></asp:TableCell>
            <asp:TableCell><input type="button" id="btn_draw" runat="server" value="draw" OnClick="javascript:document.mapapplet.draw();" disabled="true"/></asp:TableCell>
        </asp:TableRow>
        <asp:TableRow>
            <asp:TableCell ColumnSpan="4">
                <applet name="mapapplet" id="mapapplet" runat="server" width="640" height="480">
                    <param id="userid" name="userid" runat="server" value="dette er noe tekst" />
                    <param id="compid" name="compid" runat="server" value="dette er noe tekst" />
                    <param id="deptid" name="deptid" runat="server" value="dette er noe tekst" />
                    <param id="password" name="password" runat="server" value="dette er noe tekst" />
                    <param id="session" name="session" runat="server" value="dette er noe tekst" />
                    <param name="jnlp_href" value="javaapp/jnlptest.jnlp?text=heisann" />
                </applet>
                <!--
                <script type="text/javascript">
                    var attributes = { width:675, height:300} ;
                    var parameters = { jnlp_href: 'javaapp/jnlptest.jnlp'};
                    deployJava.runApplet(attributes, parameters, '1.6');
                </script>-->
            </asp:TableCell>
        </asp:TableRow>
        <asp:TableRow>
            <asp:TableCell HorizontalAlign="Right" ColumnSpan="4">
                <asp:Button ID="btn_save" runat="server" Text="Save" OnClientClick="javascript:getShape();" OnClick="btn_save_Click" />
            </asp:TableCell>
        </asp:TableRow>
        <asp:TableRow>
            <asp:TableCell>
                <asp:Label ID="lbl_error" runat="server" Text=""></asp:Label>
            </asp:TableCell>
        </asp:TableRow>
    </asp:Table>    
    <asp:TextBox ID="txt_coor" runat="server" style="visibility:hidden"></asp:TextBox>
    <asp:TextBox ID="txt_id" runat="server"  style="visibility:hidden"></asp:TextBox>
    <asp:TextBox ID="txt_obsolete_holder" runat="server"  style="visibility:hidden"></asp:TextBox>
    <asp:TextBox ID="txt_timestamp" runat="server"  style="visibility:hidden"></asp:TextBox>
</asp:Content>