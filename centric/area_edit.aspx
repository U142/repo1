<%@ Page MasterPageFile="~/MasterPage.master" Language="C#" AutoEventWireup="true" CodeFile="area_edit.aspx.cs" Inherits="area_edit" %>

<asp:Content ContentPlaceHolderID="body" runat="server">
    <script type="text/javascript" src="http://www.java.com/js/deployJava.js"></script>
    <script type="text/javascript">
        function getzShitz() {
            //alert(document.mapapplet.get());
            document.getElementById("ctl00_body_txt_coor").value = document.mapapplet.get();
            return false;
        }
        function setzShitz() {
            var lon = [58.9132, 58.9449, 58.7498, 58.6927];
            var lat = [6.0121, 6.0121, 6.4075, 5.9722];
            var index = document.getElementById("ctl00_body_lst_areas").selectedIndex;
            //alert(document.getElementById("ctl00_body_lst_areas").options[index].value);
            document.mapapplet.put(document.getElementById("ctl00_body_lst_areas").options[index].value);
            return false;
        }
    </script>
    <asp:Table ID="table" runat="server">
        <asp:TableHeaderRow>
            <asp:TableHeaderCell HorizontalAlign="Left"><asp:Label ID="Label3" runat="server" Text="Overview Authorization Areas"></asp:Label></asp:TableHeaderCell>
        </asp:TableHeaderRow>
        <asp:TableRow>
            <asp:TableCell ColumnSpan="4">
                <asp:ListBox ID="lst_areas" runat="server" Height="150" Width="640"  ></asp:ListBox>
            </asp:TableCell>
        </asp:TableRow>
        <asp:TableRow>
            <asp:TableCell>
                <asp:Button ID="Button2" runat="server" Text="Create" /></asp:TableCell>
        </asp:TableRow>
        <asp:TableHeaderRow>
            <asp:TableHeaderCell HorizontalAlign="Left"><asp:Label ID="Label4" runat="server" Text="Details Authorization area"></asp:Label></asp:TableHeaderCell>
        </asp:TableHeaderRow>
        <asp:TableRow>
            <asp:TableCell>
                <asp:Label ID="Label1" runat="server" Text="Authorization area"></asp:Label>
            </asp:TableCell>
            <asp:TableCell>
                <asp:TextBox ID="txt_name" runat="server"></asp:TextBox></asp:TableCell>
            <asp:TableCell>
                <asp:CheckBox ID="CheckBox1" runat="server" />
                <asp:Label ID="Label2" runat="server" Text="Obsolete"></asp:Label>
            </asp:TableCell>
            <asp:TableCell>
                <asp:TextBox ID="TextBox3" runat="server" Text="12.12.2010"></asp:TextBox>
            </asp:TableCell>
        </asp:TableRow>
        <asp:TableRow>
            <asp:TableCell><input type="button" id="btn_zoom" value="zoom" OnClick="javascript:document.mapapplet.zoom();" /></asp:TableCell>
            <asp:TableCell><input type="button" id="btn_pan" value="pan" OnClick="javascript:document.mapapplet.pan();" /></asp:TableCell>
            <asp:TableCell><input type="button" id="btn_draw" value="draw" OnClick="javascript:document.mapapplet.draw();" /></asp:TableCell>
        </asp:TableRow>
        <asp:TableRow>
            <asp:TableCell ColumnSpan="4">
                <applet name="mapapplet" id="mapapplet" width="640" height="480">
                    <param name="jnlp_href" value="javaapp/jnlptest.jnlp" />
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
                <asp:Button ID="btn_save" runat="server" Text="Save" OnClientClick="javascript:getzShitz();" OnClick="btn_save_Click" />
            </asp:TableCell>
        </asp:TableRow>
    </asp:Table>
    <asp:TextBox ID="txt_coor" runat="server" style="visibility:hidden"></asp:TextBox>
</asp:Content>