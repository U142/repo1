﻿//------------------------------------------------------------------------------
// <auto-generated>
//     This code was generated by a tool.
//     Runtime Version:2.0.50727.3082
//
//     Changes to this file may cause incorrect behavior and will be lost if
//     the code is regenerated.
// </auto-generated>
//------------------------------------------------------------------------------

// 
// This source code was auto-generated by Microsoft.VSDesigner, Version 2.0.50727.3082.
// 
#pragma warning disable 1591

namespace UMSAlertiX.AlertiXAreaApi {
    using System.Diagnostics;
    using System.Web.Services;
    using System.ComponentModel;
    using System.Web.Services.Protocols;
    using System;
    using System.Xml.Serialization;
    
    
    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("System.Web.Services", "2.0.50727.3053")]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Web.Services.WebServiceBindingAttribute(Name="AreaApiHttpBinding", Namespace="http://api.alertix.cellvision")]
    public partial class AreaApi : System.Web.Services.Protocols.SoapHttpClientProtocol {
        
        private System.Threading.SendOrPostCallback getAreaOperationCompleted;
        
        private System.Threading.SendOrPostCallback deleteAreaOperationCompleted;
        
        private System.Threading.SendOrPostCallback deleteAllAreasOperationCompleted;
        
        private System.Threading.SendOrPostCallback updateAreaOperationCompleted;
        
        private System.Threading.SendOrPostCallback getAllAreasOperationCompleted;
        
        private System.Threading.SendOrPostCallback createAreaOperationCompleted;
        
        private bool useDefaultCredentialsSetExplicitly;
        
        /// <remarks/>
        public AreaApi() {
            this.Url = global::UMSAlertiX.Properties.Settings.Default.UMSAlertiX_AlertiXAreaApi_AreaApi;
            if ((this.IsLocalFileSystemWebService(this.Url) == true)) {
                this.UseDefaultCredentials = true;
                this.useDefaultCredentialsSetExplicitly = false;
            }
            else {
                this.useDefaultCredentialsSetExplicitly = true;
            }
        }
        
        public new string Url {
            get {
                return base.Url;
            }
            set {
                if ((((this.IsLocalFileSystemWebService(base.Url) == true) 
                            && (this.useDefaultCredentialsSetExplicitly == false)) 
                            && (this.IsLocalFileSystemWebService(value) == false))) {
                    base.UseDefaultCredentials = false;
                }
                base.Url = value;
            }
        }
        
        public new bool UseDefaultCredentials {
            get {
                return base.UseDefaultCredentials;
            }
            set {
                base.UseDefaultCredentials = value;
                this.useDefaultCredentialsSetExplicitly = true;
            }
        }
        
        /// <remarks/>
        public event getAreaCompletedEventHandler getAreaCompleted;
        
        /// <remarks/>
        public event deleteAreaCompletedEventHandler deleteAreaCompleted;
        
        /// <remarks/>
        public event deleteAllAreasCompletedEventHandler deleteAllAreasCompleted;
        
        /// <remarks/>
        public event updateAreaCompletedEventHandler updateAreaCompleted;
        
        /// <remarks/>
        public event getAllAreasCompletedEventHandler getAllAreasCompleted;
        
        /// <remarks/>
        public event createAreaCompletedEventHandler createAreaCompleted;
        
        /// <remarks/>
        [System.Web.Services.Protocols.SoapDocumentMethodAttribute("", RequestNamespace="http://api.alertix.cellvision", ResponseNamespace="http://api.alertix.cellvision", Use=System.Web.Services.Description.SoapBindingUse.Literal, ParameterStyle=System.Web.Services.Protocols.SoapParameterStyle.Wrapped)]
        [return: System.Xml.Serialization.XmlElementAttribute("out", IsNullable=true)]
        public GetAreaResponse getArea([System.Xml.Serialization.XmlElementAttribute(IsNullable=true)] AreaName in0) {
            object[] results = this.Invoke("getArea", new object[] {
                        in0});
            return ((GetAreaResponse)(results[0]));
        }
        
        /// <remarks/>
        public void getAreaAsync(AreaName in0) {
            this.getAreaAsync(in0, null);
        }
        
        /// <remarks/>
        public void getAreaAsync(AreaName in0, object userState) {
            if ((this.getAreaOperationCompleted == null)) {
                this.getAreaOperationCompleted = new System.Threading.SendOrPostCallback(this.OngetAreaOperationCompleted);
            }
            this.InvokeAsync("getArea", new object[] {
                        in0}, this.getAreaOperationCompleted, userState);
        }
        
        private void OngetAreaOperationCompleted(object arg) {
            if ((this.getAreaCompleted != null)) {
                System.Web.Services.Protocols.InvokeCompletedEventArgs invokeArgs = ((System.Web.Services.Protocols.InvokeCompletedEventArgs)(arg));
                this.getAreaCompleted(this, new getAreaCompletedEventArgs(invokeArgs.Results, invokeArgs.Error, invokeArgs.Cancelled, invokeArgs.UserState));
            }
        }
        
        /// <remarks/>
        [System.Web.Services.Protocols.SoapDocumentMethodAttribute("", RequestNamespace="http://api.alertix.cellvision", ResponseNamespace="http://api.alertix.cellvision", Use=System.Web.Services.Description.SoapBindingUse.Literal, ParameterStyle=System.Web.Services.Protocols.SoapParameterStyle.Wrapped)]
        [return: System.Xml.Serialization.XmlElementAttribute("out", IsNullable=true)]
        public Response deleteArea([System.Xml.Serialization.XmlElementAttribute(IsNullable=true)] AreaName in0) {
            object[] results = this.Invoke("deleteArea", new object[] {
                        in0});
            return ((Response)(results[0]));
        }
        
        /// <remarks/>
        public void deleteAreaAsync(AreaName in0) {
            this.deleteAreaAsync(in0, null);
        }
        
        /// <remarks/>
        public void deleteAreaAsync(AreaName in0, object userState) {
            if ((this.deleteAreaOperationCompleted == null)) {
                this.deleteAreaOperationCompleted = new System.Threading.SendOrPostCallback(this.OndeleteAreaOperationCompleted);
            }
            this.InvokeAsync("deleteArea", new object[] {
                        in0}, this.deleteAreaOperationCompleted, userState);
        }
        
        private void OndeleteAreaOperationCompleted(object arg) {
            if ((this.deleteAreaCompleted != null)) {
                System.Web.Services.Protocols.InvokeCompletedEventArgs invokeArgs = ((System.Web.Services.Protocols.InvokeCompletedEventArgs)(arg));
                this.deleteAreaCompleted(this, new deleteAreaCompletedEventArgs(invokeArgs.Results, invokeArgs.Error, invokeArgs.Cancelled, invokeArgs.UserState));
            }
        }
        
        /// <remarks/>
        [System.Web.Services.Protocols.SoapDocumentMethodAttribute("", RequestNamespace="http://api.alertix.cellvision", ResponseNamespace="http://api.alertix.cellvision", Use=System.Web.Services.Description.SoapBindingUse.Literal, ParameterStyle=System.Web.Services.Protocols.SoapParameterStyle.Wrapped)]
        [return: System.Xml.Serialization.XmlElementAttribute("out", IsNullable=true)]
        public Response deleteAllAreas() {
            object[] results = this.Invoke("deleteAllAreas", new object[0]);
            return ((Response)(results[0]));
        }
        
        /// <remarks/>
        public void deleteAllAreasAsync() {
            this.deleteAllAreasAsync(null);
        }
        
        /// <remarks/>
        public void deleteAllAreasAsync(object userState) {
            if ((this.deleteAllAreasOperationCompleted == null)) {
                this.deleteAllAreasOperationCompleted = new System.Threading.SendOrPostCallback(this.OndeleteAllAreasOperationCompleted);
            }
            this.InvokeAsync("deleteAllAreas", new object[0], this.deleteAllAreasOperationCompleted, userState);
        }
        
        private void OndeleteAllAreasOperationCompleted(object arg) {
            if ((this.deleteAllAreasCompleted != null)) {
                System.Web.Services.Protocols.InvokeCompletedEventArgs invokeArgs = ((System.Web.Services.Protocols.InvokeCompletedEventArgs)(arg));
                this.deleteAllAreasCompleted(this, new deleteAllAreasCompletedEventArgs(invokeArgs.Results, invokeArgs.Error, invokeArgs.Cancelled, invokeArgs.UserState));
            }
        }
        
        /// <remarks/>
        [System.Web.Services.Protocols.SoapDocumentMethodAttribute("", RequestNamespace="http://api.alertix.cellvision", ResponseNamespace="http://api.alertix.cellvision", Use=System.Web.Services.Description.SoapBindingUse.Literal, ParameterStyle=System.Web.Services.Protocols.SoapParameterStyle.Wrapped)]
        [return: System.Xml.Serialization.XmlElementAttribute("out", IsNullable=true)]
        public Response updateArea([System.Xml.Serialization.XmlElementAttribute(IsNullable=true)] AreaName in0, [System.Xml.Serialization.XmlElementAttribute(IsNullable=true)] Polygon in1) {
            object[] results = this.Invoke("updateArea", new object[] {
                        in0,
                        in1});
            return ((Response)(results[0]));
        }
        
        /// <remarks/>
        public void updateAreaAsync(AreaName in0, Polygon in1) {
            this.updateAreaAsync(in0, in1, null);
        }
        
        /// <remarks/>
        public void updateAreaAsync(AreaName in0, Polygon in1, object userState) {
            if ((this.updateAreaOperationCompleted == null)) {
                this.updateAreaOperationCompleted = new System.Threading.SendOrPostCallback(this.OnupdateAreaOperationCompleted);
            }
            this.InvokeAsync("updateArea", new object[] {
                        in0,
                        in1}, this.updateAreaOperationCompleted, userState);
        }
        
        private void OnupdateAreaOperationCompleted(object arg) {
            if ((this.updateAreaCompleted != null)) {
                System.Web.Services.Protocols.InvokeCompletedEventArgs invokeArgs = ((System.Web.Services.Protocols.InvokeCompletedEventArgs)(arg));
                this.updateAreaCompleted(this, new updateAreaCompletedEventArgs(invokeArgs.Results, invokeArgs.Error, invokeArgs.Cancelled, invokeArgs.UserState));
            }
        }
        
        /// <remarks/>
        [System.Web.Services.Protocols.SoapDocumentMethodAttribute("", RequestNamespace="http://api.alertix.cellvision", ResponseNamespace="http://api.alertix.cellvision", Use=System.Web.Services.Description.SoapBindingUse.Literal, ParameterStyle=System.Web.Services.Protocols.SoapParameterStyle.Wrapped)]
        [return: System.Xml.Serialization.XmlElementAttribute("out", IsNullable=true)]
        public GetAllResponse getAllAreas() {
            object[] results = this.Invoke("getAllAreas", new object[0]);
            return ((GetAllResponse)(results[0]));
        }
        
        /// <remarks/>
        public void getAllAreasAsync() {
            this.getAllAreasAsync(null);
        }
        
        /// <remarks/>
        public void getAllAreasAsync(object userState) {
            if ((this.getAllAreasOperationCompleted == null)) {
                this.getAllAreasOperationCompleted = new System.Threading.SendOrPostCallback(this.OngetAllAreasOperationCompleted);
            }
            this.InvokeAsync("getAllAreas", new object[0], this.getAllAreasOperationCompleted, userState);
        }
        
        private void OngetAllAreasOperationCompleted(object arg) {
            if ((this.getAllAreasCompleted != null)) {
                System.Web.Services.Protocols.InvokeCompletedEventArgs invokeArgs = ((System.Web.Services.Protocols.InvokeCompletedEventArgs)(arg));
                this.getAllAreasCompleted(this, new getAllAreasCompletedEventArgs(invokeArgs.Results, invokeArgs.Error, invokeArgs.Cancelled, invokeArgs.UserState));
            }
        }
        
        /// <remarks/>
        [System.Web.Services.Protocols.SoapDocumentMethodAttribute("", RequestNamespace="http://api.alertix.cellvision", ResponseNamespace="http://api.alertix.cellvision", Use=System.Web.Services.Description.SoapBindingUse.Literal, ParameterStyle=System.Web.Services.Protocols.SoapParameterStyle.Wrapped)]
        [return: System.Xml.Serialization.XmlElementAttribute("out", IsNullable=true)]
        public Response createArea([System.Xml.Serialization.XmlElementAttribute(IsNullable=true)] Polygon in0, [System.Xml.Serialization.XmlElementAttribute(IsNullable=true)] AreaName in1) {
            object[] results = this.Invoke("createArea", new object[] {
                        in0,
                        in1});
            return ((Response)(results[0]));
        }
        
        /// <remarks/>
        public void createAreaAsync(Polygon in0, AreaName in1) {
            this.createAreaAsync(in0, in1, null);
        }
        
        /// <remarks/>
        public void createAreaAsync(Polygon in0, AreaName in1, object userState) {
            if ((this.createAreaOperationCompleted == null)) {
                this.createAreaOperationCompleted = new System.Threading.SendOrPostCallback(this.OncreateAreaOperationCompleted);
            }
            this.InvokeAsync("createArea", new object[] {
                        in0,
                        in1}, this.createAreaOperationCompleted, userState);
        }
        
        private void OncreateAreaOperationCompleted(object arg) {
            if ((this.createAreaCompleted != null)) {
                System.Web.Services.Protocols.InvokeCompletedEventArgs invokeArgs = ((System.Web.Services.Protocols.InvokeCompletedEventArgs)(arg));
                this.createAreaCompleted(this, new createAreaCompletedEventArgs(invokeArgs.Results, invokeArgs.Error, invokeArgs.Cancelled, invokeArgs.UserState));
            }
        }
        
        /// <remarks/>
        public new void CancelAsync(object userState) {
            base.CancelAsync(userState);
        }
        
        private bool IsLocalFileSystemWebService(string url) {
            if (((url == null) 
                        || (url == string.Empty))) {
                return false;
            }
            System.Uri wsUri = new System.Uri(url);
            if (((wsUri.Port >= 1024) 
                        && (string.Compare(wsUri.Host, "localHost", System.StringComparison.OrdinalIgnoreCase) == 0))) {
                return true;
            }
            return false;
        }
    }
    
    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("System.Xml", "2.0.50727.3082")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace="http://api.domain.common.alertix.cellvision")]
    public partial class AreaName {
        
        private string valueField;
        
        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute(IsNullable=true)]
        public string value {
            get {
                return this.valueField;
            }
            set {
                this.valueField = value;
            }
        }
    }
    
    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("System.Xml", "2.0.50727.3082")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace="http://vo.api.alertix.cellvision")]
    public partial class GetAllResponse {
        
        private Area[] areasField;
        
        private int codeField;
        
        private bool codeFieldSpecified;
        
        private string messageField;
        
        private bool successfulField;
        
        private bool successfulFieldSpecified;
        
        /// <remarks/>
        [System.Xml.Serialization.XmlArrayAttribute(IsNullable=true)]
        [System.Xml.Serialization.XmlArrayItemAttribute(Namespace="http://api.domain.common.alertix.cellvision")]
        public Area[] areas {
            get {
                return this.areasField;
            }
            set {
                this.areasField = value;
            }
        }
        
        /// <remarks/>
        public int code {
            get {
                return this.codeField;
            }
            set {
                this.codeField = value;
            }
        }
        
        /// <remarks/>
        [System.Xml.Serialization.XmlIgnoreAttribute()]
        public bool codeSpecified {
            get {
                return this.codeFieldSpecified;
            }
            set {
                this.codeFieldSpecified = value;
            }
        }
        
        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute(IsNullable=true)]
        public string message {
            get {
                return this.messageField;
            }
            set {
                this.messageField = value;
            }
        }
        
        /// <remarks/>
        public bool successful {
            get {
                return this.successfulField;
            }
            set {
                this.successfulField = value;
            }
        }
        
        /// <remarks/>
        [System.Xml.Serialization.XmlIgnoreAttribute()]
        public bool successfulSpecified {
            get {
                return this.successfulFieldSpecified;
            }
            set {
                this.successfulFieldSpecified = value;
            }
        }
    }
    
    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("System.Xml", "2.0.50727.3082")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace="http://api.domain.common.alertix.cellvision")]
    public partial class Area {
        
        private AreaId areaIdField;
        
        private AreaName areaNameField;
        
        private Point[] polygonField;
        
        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute(IsNullable=true)]
        public AreaId areaId {
            get {
                return this.areaIdField;
            }
            set {
                this.areaIdField = value;
            }
        }
        
        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute(IsNullable=true)]
        public AreaName areaName {
            get {
                return this.areaNameField;
            }
            set {
                this.areaNameField = value;
            }
        }
        
        /// <remarks/>
        [System.Xml.Serialization.XmlArrayAttribute(IsNullable=true)]
        public Point[] polygon {
            get {
                return this.polygonField;
            }
            set {
                this.polygonField = value;
            }
        }
    }
    
    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("System.Xml", "2.0.50727.3082")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace="http://api.domain.common.alertix.cellvision")]
    public partial class AreaId {
        
        private System.Nullable<int> valueField;
        
        private bool valueFieldSpecified;
        
        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute(IsNullable=true)]
        public System.Nullable<int> value {
            get {
                return this.valueField;
            }
            set {
                this.valueField = value;
            }
        }
        
        /// <remarks/>
        [System.Xml.Serialization.XmlIgnoreAttribute()]
        public bool valueSpecified {
            get {
                return this.valueFieldSpecified;
            }
            set {
                this.valueFieldSpecified = value;
            }
        }
    }
    
    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("System.Xml", "2.0.50727.3082")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace="http://api.domain.common.alertix.cellvision")]
    public partial class Point {
        
        private double xField;
        
        private bool xFieldSpecified;
        
        private double yField;
        
        private bool yFieldSpecified;
        
        /// <remarks/>
        public double x {
            get {
                return this.xField;
            }
            set {
                this.xField = value;
            }
        }
        
        /// <remarks/>
        [System.Xml.Serialization.XmlIgnoreAttribute()]
        public bool xSpecified {
            get {
                return this.xFieldSpecified;
            }
            set {
                this.xFieldSpecified = value;
            }
        }
        
        /// <remarks/>
        public double y {
            get {
                return this.yField;
            }
            set {
                this.yField = value;
            }
        }
        
        /// <remarks/>
        [System.Xml.Serialization.XmlIgnoreAttribute()]
        public bool ySpecified {
            get {
                return this.yFieldSpecified;
            }
            set {
                this.yFieldSpecified = value;
            }
        }
    }
    
    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("System.Xml", "2.0.50727.3082")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace="http://api.domain.common.alertix.cellvision")]
    public partial class Polygon {
        
        private Point[] verticesField;
        
        /// <remarks/>
        [System.Xml.Serialization.XmlArrayAttribute(IsNullable=true)]
        public Point[] vertices {
            get {
                return this.verticesField;
            }
            set {
                this.verticesField = value;
            }
        }
    }
    
    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("System.Xml", "2.0.50727.3082")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace="http://vo.common.alertix.cellvision")]
    public partial class Response {
        
        private int codeField;
        
        private bool codeFieldSpecified;
        
        private string messageField;
        
        private bool successfulField;
        
        private bool successfulFieldSpecified;
        
        /// <remarks/>
        public int code {
            get {
                return this.codeField;
            }
            set {
                this.codeField = value;
            }
        }
        
        /// <remarks/>
        [System.Xml.Serialization.XmlIgnoreAttribute()]
        public bool codeSpecified {
            get {
                return this.codeFieldSpecified;
            }
            set {
                this.codeFieldSpecified = value;
            }
        }
        
        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute(IsNullable=true)]
        public string message {
            get {
                return this.messageField;
            }
            set {
                this.messageField = value;
            }
        }
        
        /// <remarks/>
        public bool successful {
            get {
                return this.successfulField;
            }
            set {
                this.successfulField = value;
            }
        }
        
        /// <remarks/>
        [System.Xml.Serialization.XmlIgnoreAttribute()]
        public bool successfulSpecified {
            get {
                return this.successfulFieldSpecified;
            }
            set {
                this.successfulFieldSpecified = value;
            }
        }
    }
    
    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("System.Xml", "2.0.50727.3082")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace="http://vo.api.alertix.cellvision")]
    public partial class GetAreaResponse {
        
        private Area areaField;
        
        private int codeField;
        
        private bool codeFieldSpecified;
        
        private string messageField;
        
        private bool successfulField;
        
        private bool successfulFieldSpecified;
        
        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute(IsNullable=true)]
        public Area area {
            get {
                return this.areaField;
            }
            set {
                this.areaField = value;
            }
        }
        
        /// <remarks/>
        public int code {
            get {
                return this.codeField;
            }
            set {
                this.codeField = value;
            }
        }
        
        /// <remarks/>
        [System.Xml.Serialization.XmlIgnoreAttribute()]
        public bool codeSpecified {
            get {
                return this.codeFieldSpecified;
            }
            set {
                this.codeFieldSpecified = value;
            }
        }
        
        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute(IsNullable=true)]
        public string message {
            get {
                return this.messageField;
            }
            set {
                this.messageField = value;
            }
        }
        
        /// <remarks/>
        public bool successful {
            get {
                return this.successfulField;
            }
            set {
                this.successfulField = value;
            }
        }
        
        /// <remarks/>
        [System.Xml.Serialization.XmlIgnoreAttribute()]
        public bool successfulSpecified {
            get {
                return this.successfulFieldSpecified;
            }
            set {
                this.successfulFieldSpecified = value;
            }
        }
    }
    
    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("System.Web.Services", "2.0.50727.3053")]
    public delegate void getAreaCompletedEventHandler(object sender, getAreaCompletedEventArgs e);
    
    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("System.Web.Services", "2.0.50727.3053")]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    public partial class getAreaCompletedEventArgs : System.ComponentModel.AsyncCompletedEventArgs {
        
        private object[] results;
        
        internal getAreaCompletedEventArgs(object[] results, System.Exception exception, bool cancelled, object userState) : 
                base(exception, cancelled, userState) {
            this.results = results;
        }
        
        /// <remarks/>
        public GetAreaResponse Result {
            get {
                this.RaiseExceptionIfNecessary();
                return ((GetAreaResponse)(this.results[0]));
            }
        }
    }
    
    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("System.Web.Services", "2.0.50727.3053")]
    public delegate void deleteAreaCompletedEventHandler(object sender, deleteAreaCompletedEventArgs e);
    
    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("System.Web.Services", "2.0.50727.3053")]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    public partial class deleteAreaCompletedEventArgs : System.ComponentModel.AsyncCompletedEventArgs {
        
        private object[] results;
        
        internal deleteAreaCompletedEventArgs(object[] results, System.Exception exception, bool cancelled, object userState) : 
                base(exception, cancelled, userState) {
            this.results = results;
        }
        
        /// <remarks/>
        public Response Result {
            get {
                this.RaiseExceptionIfNecessary();
                return ((Response)(this.results[0]));
            }
        }
    }
    
    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("System.Web.Services", "2.0.50727.3053")]
    public delegate void deleteAllAreasCompletedEventHandler(object sender, deleteAllAreasCompletedEventArgs e);
    
    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("System.Web.Services", "2.0.50727.3053")]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    public partial class deleteAllAreasCompletedEventArgs : System.ComponentModel.AsyncCompletedEventArgs {
        
        private object[] results;
        
        internal deleteAllAreasCompletedEventArgs(object[] results, System.Exception exception, bool cancelled, object userState) : 
                base(exception, cancelled, userState) {
            this.results = results;
        }
        
        /// <remarks/>
        public Response Result {
            get {
                this.RaiseExceptionIfNecessary();
                return ((Response)(this.results[0]));
            }
        }
    }
    
    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("System.Web.Services", "2.0.50727.3053")]
    public delegate void updateAreaCompletedEventHandler(object sender, updateAreaCompletedEventArgs e);
    
    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("System.Web.Services", "2.0.50727.3053")]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    public partial class updateAreaCompletedEventArgs : System.ComponentModel.AsyncCompletedEventArgs {
        
        private object[] results;
        
        internal updateAreaCompletedEventArgs(object[] results, System.Exception exception, bool cancelled, object userState) : 
                base(exception, cancelled, userState) {
            this.results = results;
        }
        
        /// <remarks/>
        public Response Result {
            get {
                this.RaiseExceptionIfNecessary();
                return ((Response)(this.results[0]));
            }
        }
    }
    
    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("System.Web.Services", "2.0.50727.3053")]
    public delegate void getAllAreasCompletedEventHandler(object sender, getAllAreasCompletedEventArgs e);
    
    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("System.Web.Services", "2.0.50727.3053")]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    public partial class getAllAreasCompletedEventArgs : System.ComponentModel.AsyncCompletedEventArgs {
        
        private object[] results;
        
        internal getAllAreasCompletedEventArgs(object[] results, System.Exception exception, bool cancelled, object userState) : 
                base(exception, cancelled, userState) {
            this.results = results;
        }
        
        /// <remarks/>
        public GetAllResponse Result {
            get {
                this.RaiseExceptionIfNecessary();
                return ((GetAllResponse)(this.results[0]));
            }
        }
    }
    
    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("System.Web.Services", "2.0.50727.3053")]
    public delegate void createAreaCompletedEventHandler(object sender, createAreaCompletedEventArgs e);
    
    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("System.Web.Services", "2.0.50727.3053")]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    public partial class createAreaCompletedEventArgs : System.ComponentModel.AsyncCompletedEventArgs {
        
        private object[] results;
        
        internal createAreaCompletedEventArgs(object[] results, System.Exception exception, bool cancelled, object userState) : 
                base(exception, cancelled, userState) {
            this.results = results;
        }
        
        /// <remarks/>
        public Response Result {
            get {
                this.RaiseExceptionIfNecessary();
                return ((Response)(this.results[0]));
            }
        }
    }
}

#pragma warning restore 1591