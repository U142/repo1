<%@ Page MasterPageFile="~/MasterPage.master" Language="C#" AutoEventWireup="true" Inherits="area_edit" Codebehind="area_edit.aspx.cs" %>
<%@ MasterType TypeName="MasterPage" %>
<asp:Content ContentPlaceHolderID="body" runat="server">
    
    <script type="text/javascript">
        function getShape() {
            //alert(document.mapapplet.get());
            try {
                document.getElementById("body_lbl_error").innerHTML = "";
                document.getElementById("body_txt_coor").value = document.mapapplet.get();
                return false;
            } catch (err) { }

        }

        function setShape(coor, id, obsolete, timestamp, deptname) {
            
            document.getElementById("body_txt_id").value = id;
            document.getElementById("body_txt_obsolete_holder").value = obsolete;
            
            document.getElementById("body_txt_name").disabled = true;
            document.getElementById("body_txt_name").value = deptname;
            
            if (obsolete == 1) {
                document.getElementById("body_chk_obsolete").checked = true;
                document.getElementById("body_chk_obsolete").disabled = true;
                document.getElementById("body_chk_obsolete").parentElement.setAttribute('disabled', 'true');
                document.getElementById("body_txt_timestamp").value = timestamp;
            }
            else {
                document.getElementById("body_chk_obsolete").checked = false;
                document.getElementById("body_chk_obsolete").disabled = false;
                document.getElementById("body_chk_obsolete").parentElement.removeAttribute('disabled');
                document.getElementById("body_txt_timestamp").value = "";
            }
            document.getElementById("body_txt_obsolete").value = timestamp;
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
                <asp:Button ID="btn_create" runat="server" Text="Create" OnClick="btn_create_click" CausesValidation="false"/></asp:TableCell>
        </asp:TableRow>
        <asp:TableHeaderRow>
            <asp:TableHeaderCell HorizontalAlign="Left"><asp:Label ID="Label4" runat="server" Text="Details Authorization area"></asp:Label></asp:TableHeaderCell>
        </asp:TableHeaderRow>
        <asp:TableRow>
            <asp:TableCell>
                <asp:Label ID="Label1" runat="server" Text="Authorization area"></asp:Label>
            </asp:TableCell>
            <asp:TableCell>
                <asp:TextBox ID="txt_name" runat="server" Enabled="false"></asp:TextBox><asp:RequiredFieldValidator ID="RequiredFieldValidator1" runat="server" Text="*" ControlToValidate="txt_name" ErrorMessage="Name is required"></asp:RequiredFieldValidator></asp:TableCell>
            <asp:TableCell>
                <asp:CheckBox ID="chk_obsolete" runat="server" Enabled="false" />
                <asp:Label ID="Label2" runat="server" Text="Obsolete"></asp:Label>
            </asp:TableCell>
            <asp:TableCell>
                <asp:TextBox ID="txt_obsolete" runat="server" Text="" Enabled="false" ></asp:TextBox>
            </asp:TableCell>
        </asp:TableRow>
        <asp:TableRow>
            <asp:TableCell ColumnSpan="4">
                <applet name="mapapplet" id="mapapplet" runat="server" width="924" height="695" archive="javaapp/annotations-1.0.0.jar,
                        javaapp/centric-admin-2.0-SNAPSHOT.jar, 
                        javaapp/commons-beanutils-1.7.0.jar, 
                        javaapp/commons-lang-2.3.jar, 
                        javaapp/commons-logging-1.0.3.jar, 
                        javaapp/commons-pool-1.5.3.jar, 
                        javaapp/geoapi-2.3-M1.jar, 
                        javaapp/geoapi-pending-2.3-M1.jar, 
                        javaapp/gt-api-2.6.5.jar, 
                        javaapp/gt-coverage-2.6.5.jar, 
                        javaapp/gt-cql-2.6.5.jar, 
                        javaapp/gt-main-2.6.5.jar, 
                        javaapp/gt-metadata-2.6.5.jar, 
                        javaapp/gt-referencing-2.6.5.jar, 
                        javaapp/gt-render-2.6.5.jar, 
                        javaapp/gt-shapefile-2.6.5.jar, 
                        javaapp/gt-wms-2.6.5.jar, 
                        javaapp/gt-xml-2.6.5.jar, 
                        javaapp/guava-r07.jar, 
                        javaapp/hamcrest-core-1.1.jar, 
                        javaapp/hamcrest-library-1.1.jar, 
                        javaapp/jaxb2-basics-0.6.0.jar, 
                        javaapp/jaxb2-basics-runtime-0.6.0.jar, 
                        javaapp/jaxb2-basics-tools-0.6.0.jar, 
                        javaapp/jcommon-1.0.16.jar, 
                        javaapp/jdom-1.0.jar, 
                        javaapp/jfreechart-1.0.13.jar, 
                        javaapp/jmock-2.5.1.jar, 
                        javaapp/jnlp-1.0.jar, 
                        javaapp/joda-time-1.6.2.jar, 
                        javaapp/jsr-275-1.0-beta-2.jar, 
                        javaapp/jsr305-1.3.9.jar, 
                        javaapp/jts-1.11.jar, 
                        javaapp/NLMapSearch-1.0.jar, 
                        javaapp/pas-admin-ui-2.0-SNAPSHOT.jar, 
                        javaapp/pas-app-2.0-SNAPSHOT-core.jar, 
                        javaapp/pas-app-2.0-SNAPSHOT-gps.jar, 
                        javaapp/pas-app-2.0-SNAPSHOT-importer.jar, 
                        javaapp/pas-app-2.0-SNAPSHOT-maps.jar, 
                        javaapp/pas-app-2.0-SNAPSHOT-parm.jar, 
                        javaapp/pas-app-2.0-SNAPSHOT-pas.jar, 
                        javaapp/pas-app-2.0-SNAPSHOT-send.jar, 
                        javaapp/pas-app-2.0-SNAPSHOT-sound.jar, 
                        javaapp/pas-app-2.0-SNAPSHOT-status.jar, 
                        javaapp/pas-app-2.0-SNAPSHOT-tas.jar, 
                        javaapp/pas-app-2.0-SNAPSHOT-ums.jar, 
                        javaapp/pas-app-2.0-SNAPSHOT.jar, 
                        javaapp/pas-cellbroadcast-2.0-SNAPSHOT.jar, 
                        javaapp/pas-core-2.0-SNAPSHOT.jar, 
                        javaapp/pas-icons-2.0-SNAPSHOT.jar, 
                        javaapp/pas-lang-2.0-SNAPSHOT.jar, 
                        javaapp/substance-3.3.0.1.jar, 
                        javaapp/ums-log-2.0-SNAPSHOT.jar, 
                        javaapp/ums-map-2.0-SNAPSHOT.jar, 
                        javaapp/vecmath-1.3.2.jar, 
                        javaapp/ws-java-clients-1.2-SNAPSHOT.jar"
                 code="no/ums/adminui/pas/MapApplet.class" main-class="no.ums.adminui.pas.MapApplet">
                    <param id="userid" name="userid" runat="server" value="dette er noe tekst" />
                    <param id="compid" name="compid" runat="server" value="dette er noe tekst" />
                    <param id="deptid" name="deptid" runat="server" value="dette er noe tekst" />
                    <param id="password" name="password" runat="server" value="dette er noe tekst" />
                    <param id="session" name="session" runat="server" value="dette er noe tekst" />
                    <param id="mapinfo" name="mapinfo" runat="server" />
                    <param id="m" name="m" runat="server" />
                    <param id="w" name="w" runat="server" />
                    <param id="c" name="c" runat="server" />
                    <param id="applet_height" name="applet_height" runat="server" />
                    <param id="applet_width" name="applet_width" runat="server" />
                </applet>
                
                <script type="text/javascript">
                    var mapapplet = document.getElementById("body_mapapplet")
     

                    function preventScrolling(e) {
                        var evt = window.event || e;  //equalize event object

                        mapapplet.focus();

                        if (evt.preventDefault) { //disable default wheel action of scrolling page
                            evt.preventDefault();
                        }
                        else {
                            return false;
                        }
                    }

                    var mousewheelevt = (/Firefox/i.test(navigator.userAgent)) ? "DOMMouseScroll" : "mousewheel";  //FF doesn't recognize mousewheel as of FF3.x

                    if (mapapplet.attachEvent) { //if IE (and Opera depending on user setting)
                        mapapplet.attachEvent("on" + mousewheelevt, preventScrolling);
                    }
                    else if (mapapplet.addEventListener) { //WC3 browsers
                        mapapplet.addEventListener(mousewheelevt, preventScrolling, false);
                    }
                </script>
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
                <asp:ValidationSummary ID="ValidationSummary1" runat="server" />
            </asp:TableCell>
        </asp:TableRow>
    </asp:Table>    
    <asp:TextBox ID="txt_coor" runat="server" style="visibility:hidden"></asp:TextBox>
    <asp:TextBox ID="txt_id" runat="server"  style="visibility:hidden"></asp:TextBox>
    <asp:TextBox ID="txt_obsolete_holder" runat="server"  style="visibility:hidden"></asp:TextBox>
    <asp:TextBox ID="txt_timestamp" runat="server"  style="visibility:hidden"></asp:TextBox>
</asp:Content>