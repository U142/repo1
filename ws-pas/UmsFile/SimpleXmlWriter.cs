using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml;
using System.IO;

namespace com.ums.UmsFile
{


    public class USimpleXmlWriter
    {
        MemoryStream output = new MemoryStream();
        //StringWriter output = new StringWriter();
        //StringBuilder output = new StringBuilder();
        //XmlWriter w;
        XmlWriter w;
        Encoding encoding;

        public string getXml(Encoding enc)
        {
            return enc.GetString(output.GetBuffer(), 0, (int)output.Length);
        }
        public string getXml()
        {
            //char[] b = new char[output.Length];
            //output.Read(b, 0, (int)output.Length);
            //return new String(b);
            return encoding.GetString(output.GetBuffer(), 0, (int)output.Length);
        }
        public string getXml2()
        {
            return encoding.GetString(output.GetBuffer(), 3, (int)output.Length-3);
        }
        public USimpleXmlWriter()
        {

        }
        public USimpleXmlWriter(Encoding enc)
        {
            this.encoding = enc;
            XmlWriterSettings s = new XmlWriterSettings();
            s.Encoding = this.encoding;
            s.CheckCharacters = false;
            s.OmitXmlDeclaration = false;
            s.Indent = true;
            //s.ConformanceLevel = ConformanceLevel.Document;
            //XmlTextWriter wtemp = new XmlTextWriter(output, Encoding.UTF8); //XmlTextWriter.Create(output, Encoding.UTF8);
            //output.Position = 0;
            w = XmlTextWriter.Create(output, s); //new XmlTextWriter(output, this.encoding);
            //w = XmlTextWriter.Create(output, s);
        }
        public USimpleXmlWriter(String encoding)
        {
            this.encoding = Encoding.GetEncoding(encoding);
            XmlWriterSettings s = new XmlWriterSettings();
            s.Encoding = this.encoding;
            s.CheckCharacters = false;
            s.OmitXmlDeclaration = false;
            s.Indent = true;
            //s.ConformanceLevel = ConformanceLevel.Document;
            //XmlTextWriter wtemp = new XmlTextWriter(output, Encoding.UTF8); //XmlTextWriter.Create(output, Encoding.UTF8);
            //output.Position = 0;
            w = XmlTextWriter.Create(output, s); //new XmlTextWriter(output, this.encoding);
            //w = XmlTextWriter.Create(output, s);
        }
        public void insertComment(String s)
        {
            w.WriteComment(s);
        }

        public void endElement()
        {
            w.WriteEndElement();
        }
        public void insertStartElement(String s)
        {
            w.WriteStartElement(s);
        }
        public void insertElementString(String s)
        {
            w.WriteString(s);
        }
        public void insertEndElement()
        {
            w.WriteEndElement();
        }
        public void insertAttribute(String s, String v)
        {
            w.WriteStartAttribute(s);
            w.WriteString(v);
            w.WriteEndAttribute();
        }
        public void insertStartDocument()
        {
            w.WriteStartDocument();
            //Encoding enc = Encoding.UTF8;
            //w.WriteString(String.Format("<?xml version=\"1.0\" encoding=\"{0}\"?>", enc.CodePage));
        }
        public void insertEndDocument()
        {
            w.WriteEndDocument();
        }
        public void insertText(String s)
        {
            w.WriteRaw(s);
        }
        public void finalize()
        {
            w.Close();
        }
        public override string ToString()
        {
            return getXml();
        }
        public XmlDocument GetXmlDocument()
        {
            XmlDocument doc = new XmlDocument();
            try
            {
                doc.LoadXml(ToString());
            }
            catch (Exception e)
            {
                String err = e.Message;
            }
            return doc;
        }
    }
}
