﻿//------------------------------------------------------------------------------
// <auto-generated>
//     This code was generated by a tool.
//     Runtime Version:2.0.50727.4927
//
//     Changes to this file may cause incorrect behavior and will be lost if
//     the code is regenerated.
// </auto-generated>
//------------------------------------------------------------------------------

// 
// This source code was auto-generated by Microsoft.VSDesigner, Version 2.0.50727.4927.
// 
#pragma warning disable 1591

namespace umsalertix.alertix.statistics {
    using System.Diagnostics;
    using System.Web.Services;
    using System.ComponentModel;
    using System.Web.Services.Protocols;
    using System;
    using System.Xml.Serialization;
    
    
    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("System.Web.Services", "2.0.50727.4927")]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Web.Services.WebServiceBindingAttribute(Name="StatisticsApiHttpBinding", Namespace="http://api.alertix.cellvision")]
    public partial class StatisticsApi : System.Web.Services.Protocols.SoapHttpClientProtocol {
        
        private System.Threading.SendOrPostCallback getCountryDistributionByCcOperationCompleted;
        
        private System.Threading.SendOrPostCallback getSubscriberCountByCcOperationCompleted;
        
        private bool useDefaultCredentialsSetExplicitly;
        
        /// <remarks/>
        public StatisticsApi() {
            this.Url = global::umsalertix.Properties.Settings.Default.umsalertix_alertix_statistics_StatisticsApi;
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
        public event getCountryDistributionByCcCompletedEventHandler getCountryDistributionByCcCompleted;
        
        /// <remarks/>
        public event getSubscriberCountByCcCompletedEventHandler getSubscriberCountByCcCompleted;
        
        /// <remarks/>
        [System.Web.Services.Protocols.SoapDocumentMethodAttribute("", RequestNamespace="http://api.alertix.cellvision", ResponseNamespace="http://api.alertix.cellvision", Use=System.Web.Services.Description.SoapBindingUse.Literal, ParameterStyle=System.Web.Services.Protocols.SoapParameterStyle.Wrapped)]
        [return: System.Xml.Serialization.XmlElementAttribute("out", IsNullable=true)]
        public CountryDistributionByCcResponse getCountryDistributionByCc() {
            object[] results = this.Invoke("getCountryDistributionByCc", new object[0]);
            return ((CountryDistributionByCcResponse)(results[0]));
        }
        
        /// <remarks/>
        public void getCountryDistributionByCcAsync() {
            this.getCountryDistributionByCcAsync(null);
        }
        
        /// <remarks/>
        public void getCountryDistributionByCcAsync(object userState) {
            if ((this.getCountryDistributionByCcOperationCompleted == null)) {
                this.getCountryDistributionByCcOperationCompleted = new System.Threading.SendOrPostCallback(this.OngetCountryDistributionByCcOperationCompleted);
            }
            this.InvokeAsync("getCountryDistributionByCc", new object[0], this.getCountryDistributionByCcOperationCompleted, userState);
        }
        
        private void OngetCountryDistributionByCcOperationCompleted(object arg) {
            if ((this.getCountryDistributionByCcCompleted != null)) {
                System.Web.Services.Protocols.InvokeCompletedEventArgs invokeArgs = ((System.Web.Services.Protocols.InvokeCompletedEventArgs)(arg));
                this.getCountryDistributionByCcCompleted(this, new getCountryDistributionByCcCompletedEventArgs(invokeArgs.Results, invokeArgs.Error, invokeArgs.Cancelled, invokeArgs.UserState));
            }
        }
        
        /// <remarks/>
        [System.Web.Services.Protocols.SoapDocumentMethodAttribute("", RequestNamespace="http://api.alertix.cellvision", ResponseNamespace="http://api.alertix.cellvision", Use=System.Web.Services.Description.SoapBindingUse.Literal, ParameterStyle=System.Web.Services.Protocols.SoapParameterStyle.Wrapped)]
        [return: System.Xml.Serialization.XmlElementAttribute("out", IsNullable=true)]
        public SubscriberCountResponse getSubscriberCountByCc([System.Xml.Serialization.XmlElementAttribute(IsNullable=true)] CountryCodes in0) {
            object[] results = this.Invoke("getSubscriberCountByCc", new object[] {
                        in0});
            return ((SubscriberCountResponse)(results[0]));
        }
        
        /// <remarks/>
        public void getSubscriberCountByCcAsync(CountryCodes in0) {
            this.getSubscriberCountByCcAsync(in0, null);
        }
        
        /// <remarks/>
        public void getSubscriberCountByCcAsync(CountryCodes in0, object userState) {
            if ((this.getSubscriberCountByCcOperationCompleted == null)) {
                this.getSubscriberCountByCcOperationCompleted = new System.Threading.SendOrPostCallback(this.OngetSubscriberCountByCcOperationCompleted);
            }
            this.InvokeAsync("getSubscriberCountByCc", new object[] {
                        in0}, this.getSubscriberCountByCcOperationCompleted, userState);
        }
        
        private void OngetSubscriberCountByCcOperationCompleted(object arg) {
            if ((this.getSubscriberCountByCcCompleted != null)) {
                System.Web.Services.Protocols.InvokeCompletedEventArgs invokeArgs = ((System.Web.Services.Protocols.InvokeCompletedEventArgs)(arg));
                this.getSubscriberCountByCcCompleted(this, new getSubscriberCountByCcCompletedEventArgs(invokeArgs.Results, invokeArgs.Error, invokeArgs.Cancelled, invokeArgs.UserState));
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
    [System.CodeDom.Compiler.GeneratedCodeAttribute("System.Xml", "2.0.50727.4927")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace="http://vo.api.alertix.cellvision")]
    public partial class CountryDistributionByCcResponse {
        
        private int codeField;
        
        private bool codeFieldSpecified;
        
        private CountByCc[] countryDistributionByCcField;
        
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
        [System.Xml.Serialization.XmlArrayAttribute(IsNullable=true)]
        [System.Xml.Serialization.XmlArrayItemAttribute(Namespace="http://api.domain.common.alertix.cellvision")]
        public CountByCc[] countryDistributionByCc {
            get {
                return this.countryDistributionByCcField;
            }
            set {
                this.countryDistributionByCcField = value;
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
    [System.CodeDom.Compiler.GeneratedCodeAttribute("System.Xml", "2.0.50727.4927")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace="http://api.domain.common.alertix.cellvision")]
    public partial class CountByCc {
        
        private int countryCodeField;
        
        private bool countryCodeFieldSpecified;
        
        private int countField;
        
        private bool countFieldSpecified;
        
        /// <remarks/>
        public int countryCode {
            get {
                return this.countryCodeField;
            }
            set {
                this.countryCodeField = value;
            }
        }
        
        /// <remarks/>
        [System.Xml.Serialization.XmlIgnoreAttribute()]
        public bool countryCodeSpecified {
            get {
                return this.countryCodeFieldSpecified;
            }
            set {
                this.countryCodeFieldSpecified = value;
            }
        }
        
        /// <remarks/>
        public int count {
            get {
                return this.countField;
            }
            set {
                this.countField = value;
            }
        }
        
        /// <remarks/>
        [System.Xml.Serialization.XmlIgnoreAttribute()]
        public bool countSpecified {
            get {
                return this.countFieldSpecified;
            }
            set {
                this.countFieldSpecified = value;
            }
        }
    }
    
    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("System.Xml", "2.0.50727.4927")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace="http://vo.api.alertix.cellvision")]
    public partial class SubscriberCountResponse {
        
        private int codeField;
        
        private bool codeFieldSpecified;
        
        private int countField;
        
        private bool countFieldSpecified;
        
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
        public int count {
            get {
                return this.countField;
            }
            set {
                this.countField = value;
            }
        }
        
        /// <remarks/>
        [System.Xml.Serialization.XmlIgnoreAttribute()]
        public bool countSpecified {
            get {
                return this.countFieldSpecified;
            }
            set {
                this.countFieldSpecified = value;
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
    [System.CodeDom.Compiler.GeneratedCodeAttribute("System.Xml", "2.0.50727.4927")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace="http://api.domain.common.alertix.cellvision")]
    public partial class CountryCode {
        
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
    [System.CodeDom.Compiler.GeneratedCodeAttribute("System.Xml", "2.0.50727.4927")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace="http://api.domain.common.alertix.cellvision")]
    public partial class CountryCodes {
        
        private CountryCode[] countryCodesField;
        
        /// <remarks/>
        [System.Xml.Serialization.XmlArrayAttribute(IsNullable=true)]
        public CountryCode[] countryCodes {
            get {
                return this.countryCodesField;
            }
            set {
                this.countryCodesField = value;
            }
        }
    }
    
    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("System.Web.Services", "2.0.50727.4927")]
    public delegate void getCountryDistributionByCcCompletedEventHandler(object sender, getCountryDistributionByCcCompletedEventArgs e);
    
    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("System.Web.Services", "2.0.50727.4927")]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    public partial class getCountryDistributionByCcCompletedEventArgs : System.ComponentModel.AsyncCompletedEventArgs {
        
        private object[] results;
        
        internal getCountryDistributionByCcCompletedEventArgs(object[] results, System.Exception exception, bool cancelled, object userState) : 
                base(exception, cancelled, userState) {
            this.results = results;
        }
        
        /// <remarks/>
        public CountryDistributionByCcResponse Result {
            get {
                this.RaiseExceptionIfNecessary();
                return ((CountryDistributionByCcResponse)(this.results[0]));
            }
        }
    }
    
    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("System.Web.Services", "2.0.50727.4927")]
    public delegate void getSubscriberCountByCcCompletedEventHandler(object sender, getSubscriberCountByCcCompletedEventArgs e);
    
    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("System.Web.Services", "2.0.50727.4927")]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    public partial class getSubscriberCountByCcCompletedEventArgs : System.ComponentModel.AsyncCompletedEventArgs {
        
        private object[] results;
        
        internal getSubscriberCountByCcCompletedEventArgs(object[] results, System.Exception exception, bool cancelled, object userState) : 
                base(exception, cancelled, userState) {
            this.results = results;
        }
        
        /// <remarks/>
        public SubscriberCountResponse Result {
            get {
                this.RaiseExceptionIfNecessary();
                return ((SubscriberCountResponse)(this.results[0]));
            }
        }
    }
}

#pragma warning restore 1591