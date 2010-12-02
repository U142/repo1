using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Security.Cryptography;
using System.Security.Cryptography.Xml;
using System.Security.Cryptography.X509Certificates;
using System.Xml;
using System.IO;
using JavaScience;

namespace pas_cb_server.signxml
{
    class _signxml
    {
        public static string SignXml(ref XmlDocument doc, string CertSerial)
        {
            X509Store store = new X509Store(StoreName.TrustedPeople, StoreLocation.LocalMachine);
            store.Open(OpenFlags.ReadOnly);

            X509Certificate2Collection certs = store.Certificates.Find(X509FindType.FindBySerialNumber, CertSerial, false);
            if (certs.Count >= 1)
            {
                SignXml(ref doc, certs[0]);
            }
            else
            {
                throw new Exception("Certificate not found.");
            }

            return doc.OuterXml;
        }

        public static string SignXml(ref XmlDocument doc, string RSACert, string RSAKey, string Password)
        {
            try
            {
                X509Certificate2 cert = new X509Certificate2(Encoding.ASCII.GetBytes(RSACert));

                RSACryptoServiceProvider pkey = new RSACryptoServiceProvider();
                pkey.FromXmlString(opensslkey.DecodePEMKey(RSAKey, Password));

                cert.PrivateKey = pkey;

                SignXml(ref doc, cert);
            }
            catch
            {
                throw new Exception("Failed to get certificate.");
            }

            return doc.OuterXml;
        }

        private static void SignXml(ref XmlDocument doc, X509Certificate2 cert)
        {
            // Create a SignedXml object.
            XmlNamespaceManager nsmngr = new XmlNamespaceManager(doc.NameTable);
            nsmngr.AddNamespace("ns", doc.DocumentElement.NamespaceURI);

            XmlNode xmlsig = doc.SelectSingleNode("ns:IBAG_Alert_Attributes", nsmngr).SelectSingleNode("ns:IBAG_Digital_Signature", nsmngr);

            SignedXml signedXml = new SignedXml(doc);
            KeyInfo keyInfo = new KeyInfo();
            KeyInfoX509Data keyInfoData = new KeyInfoX509Data();

            keyInfoData.AddCertificate(cert);
            keyInfo.AddClause(keyInfoData);

            signedXml.SigningKey = cert.PrivateKey;
            signedXml.KeyInfo = keyInfo;

            // Create a reference to be signed.
            Reference reference = new Reference();
            reference.Uri = "";

            // Add an enveloped transformation to the reference.
            XmlDsigEnvelopedSignatureTransform env = new XmlDsigEnvelopedSignatureTransform();
            reference.AddTransform(env);

            XmlDsigC14NTransform c14ntransform = new XmlDsigC14NTransform();
            reference.AddTransform(c14ntransform);

            // Add the reference to the SignedXml object.
            signedXml.AddReference(reference);

            // Compute the signature.
            signedXml.ComputeSignature();

            // Get the XML representation of the signature and save
            // it to an XmlElement object.
            XmlElement xmlDigitalSignature = signedXml.GetXml();

            xmlsig.AppendChild(doc.ImportNode(xmlDigitalSignature, true));
        }

        public static bool VerifyXml(XmlDocument doc, X509Certificate2 cert)
        {
            // Create a new SignedXml object and pass it
            // the XML document class.
            SignedXml signedXml = new SignedXml(doc);

            // Find the "Signature" node and create a new
            XmlNamespaceManager nsmngr = new XmlNamespaceManager(doc.NameTable);
            nsmngr.AddNamespace("ns", doc.DocumentElement.NamespaceURI);
            XmlNode node = doc.SelectSingleNode("ns:IBAG_Alert_Attributes", nsmngr).SelectSingleNode("ns:IBAG_Digital_Signature", nsmngr);

            XmlNodeList nodeList = doc.GetElementsByTagName("ds:Signature");
            if (nodeList.Count == 0)
                nodeList = doc.GetElementsByTagName("Signature");

            // Load the signature node.
            signedXml.LoadXml((XmlElement)nodeList[0]);

            return signedXml.CheckSignature(cert, true);
        }
    }
}
