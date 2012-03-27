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

        function splitAndSetShape(ddlValue) {
            if (ddlValue == "-1") {
                document.mapapplet.clear();
            }
            else {
                var tmp = ddlValue.replace('"', '');
                tmp = tmp.replace(/\'/g, "");
                tmp = tmp.replace(/\s+/g, "");
                var tmparr = tmp.split(',');

                setShape(tmparr[0], tmparr[1], tmparr[2], tmparr[3], tmparr[4]);
            }
            
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
            } catch (err) {
            }
            
            
            
            //return false;
        }
        
        var prm = Sys.WebForms.PageRequestManager.getInstance();
        prm.add_pageLoaded(PageLoadedEventHandler);
        function PageLoadedEventHandler() {
            // custom script
            if (document.getElementById("body_txt_draw_active").value == "True") {
                try {
                    document.mapapplet.clear();
                    document.mapapplet.draw();
                }
                catch (err) {
                }
            }
        }

    </script>
    <asp:ScriptManager ID="ScriptManager1" runat="server">
    </asp:ScriptManager>
    
    <asp:Table ID="table" runat="server">
        <asp:TableHeaderRow>
            <asp:TableHeaderCell HorizontalAlign="Left"><asp:Label ID="Label4" runat="server" Text="Details Authorization area"></asp:Label></asp:TableHeaderCell>
        </asp:TableHeaderRow>
        <asp:TableRow>
            <asp:TableCell>
                <ul>
                    <li>Press create to activate drawing</li>
                    <li>Zoom using scroll wheel</li>
                    <li>Pan by dragging map</li>
                    <li>Start drawing by mouse left click, first two points define border line</li>
                    <li>Delete single points by pressing delete</li>
                    <li>Complete polygon by mouse right click</li>
                    <li>Click save to store</li>
                </ul>
            </asp:TableCell>
            
        </asp:TableRow>
        <asp:TableRow>
            <asp:TableCell HorizontalAlign="Right" ColumnSpan="4">
                <asp:DropDownList runat="server" ID="ddlAreas" onchange="javascript:splitAndSetShape(this.options[this.selectedIndex].value)"></asp:DropDownList>
            </asp:TableCell>
        </asp:TableRow>
        <asp:TableRow>
            <asp:TableCell>
                <asp:Label ID="Label1" runat="server" Text="Authorization area"></asp:Label>
            </asp:TableCell>
            <asp:TableCell>
                <asp:UpdatePanel ID="update1" runat="server" UpdateMode="Conditional">
                <ContentTemplate>
                    <asp:TextBox ID="txt_name" runat="server" Enabled="false"></asp:TextBox><asp:RequiredFieldValidator ID="RequiredFieldValidator1" runat="server" Text="*" ControlToValidate="txt_name" ErrorMessage="Name is required"></asp:RequiredFieldValidator>
                </ContentTemplate>
                <Triggers>
                    <asp:AsyncPostBackTrigger ControlID="btn_create" EventName="click" />
                </Triggers>
                </asp:UpdatePanel>
            </asp:TableCell>
            <asp:TableCell>
                <asp:UpdatePanel ID="UpdatePanel1" runat="server" UpdateMode="Conditional">
                <ContentTemplate>
                    <asp:CheckBox ID="chk_obsolete" runat="server" Enabled="false" />
                    <asp:Label ID="Label2" runat="server" Text="Obsolete"></asp:Label>
                </ContentTemplate>
                <Triggers>
                    <asp:AsyncPostBackTrigger ControlID="btn_create" EventName="click" />
                </Triggers>
                </asp:UpdatePanel>
            </asp:TableCell>
            <asp:TableCell HorizontalAlign="Right">
                <asp:TextBox ID="txt_obsolete" runat="server" Text="" Enabled="false" ></asp:TextBox>
            </asp:TableCell>
            <asp:TableCell>
                <asp:Button ID="btn_create" runat="server" Text="Create" OnClick="btn_create_click" CausesValidation="false"/>
            </asp:TableCell>
        </asp:TableRow>
        
        <asp:TableRow>
            <asp:TableCell ColumnSpan="5">
                <applet name="mapapplet" id="mapapplet" runat="server" width="924" height="695" archive="javaapp/centric-admin-2.0-SNAPSHOT.jar"
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

                        mapapplet.requestFocus();

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
            <asp:TableCell HorizontalAlign="Right" ColumnSpan="5">
                <asp:Button ID="btn_save" runat="server" Text="Save" OnClientClick="javascript:getShape();" OnClick="btn_save_Click" />
            </asp:TableCell>
        </asp:TableRow>
        <asp:TableRow>
            <asp:TableCell>
            <asp:UpdatePanel ID="UpdatePanel3" runat="server" UpdateMode="Conditional">
                <ContentTemplate>
                    <asp:Label ID="lbl_error" runat="server" Text=""></asp:Label>
                    <asp:ValidationSummary ID="ValidationSummary1" runat="server" />
                </ContentTemplate>
                <Triggers>
                    <asp:AsyncPostBackTrigger ControlID="btn_create" EventName="click" />
                </Triggers>
            </asp:UpdatePanel>
            </asp:TableCell>
        </asp:TableRow>
        <asp:TableHeaderRow>
            <asp:TableHeaderCell HorizontalAlign="Left"><asp:Label ID="Label3" runat="server" Text="Overview Authorization Areas"></asp:Label></asp:TableHeaderCell>
        </asp:TableHeaderRow>
        <asp:TableRow>
            <asp:TableCell ColumnSpan="4">
                <asp:ListBox ID="lst_areas" runat="server" Height="150" Width="640"  Visible="false" ></asp:ListBox>
                <asp:Table runat="server" ID="tbl_areas" GridLines="Both"></asp:Table>
            </asp:TableCell>
        </asp:TableRow>
    </asp:Table>    
    <asp:UpdatePanel ID="UpdatePanel2" runat="server" UpdateMode="Conditional">
    <ContentTemplate>
        <asp:TextBox ID="txt_coor" runat="server" style="visibility:hidden"></asp:TextBox>
        <asp:TextBox ID="txt_id" runat="server"  style="visibility:hidden"></asp:TextBox>
        <asp:TextBox ID="txt_obsolete_holder" runat="server"  style="visibility:hidden"></asp:TextBox>
        <asp:TextBox ID="txt_timestamp" runat="server"  style="visibility:hidden"></asp:TextBox>
        <asp:TextBox ID="txt_draw_active" runat="server"  style="visibility:hidden"></asp:TextBox>
    </ContentTemplate>
        <Triggers>
            <asp:AsyncPostBackTrigger ControlID="btn_create" EventName="click" />
        </Triggers>
    </asp:UpdatePanel>
</asp:Content>