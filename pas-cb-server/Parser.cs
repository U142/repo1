using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml;
using System.IO;
using System.Collections;
using System.Threading;
using System.Globalization;

namespace pas_cb_server
{
    class Parser
    {
        static int lRetVal = 0;

        public static void CheckFiles()
        {
            while (CBServer.running)
            {
                try
                {
                    string[] eatfileEntries = Directory.GetFiles(Settings.sz_parsepath + "eat\\", "*.xml");
                    string[] retryfileEntries = Directory.GetFiles(Settings.sz_parsepath + "retry\\", "*.xml");
                    Array.Sort(eatfileEntries);
                    Array.Sort(retryfileEntries);

                    HashSet<string> fileEntries = new HashSet<string>();
                    fileEntries.Concat(eatfileEntries);
                    fileEntries.Concat(retryfileEntries);

                    foreach (string fileName in fileEntries)
                    {
                        if (!CBServer.running) break;
                        lRetVal = ParseXMLFile(fileName);
                        if (lRetVal == 0)
                        {
                            Thread.Sleep(500);
                            File.Move(fileName, fileName.Replace("\\eat\\", "\\done\\"));
                        }
                        else if (lRetVal == -1)
                        {
                            Thread.Sleep(500);
                            File.Move(fileName, fileName.Replace("\\eat\\", "\\retry\\"));
                        }
                        else // lRetVal -2
                        {
                            Thread.Sleep(500);
                            File.Move(fileName, fileName.Replace("\\eat\\", "\\failed\\"));
                        }
                    }
                }
                catch (Exception e)
                {
                    Log.WriteLog("Exception during file poll: " + e.Message, 2);
                }
                Thread.Sleep(1000);
            }
            Log.WriteLog("Stopped parser thread", 9);
        }

        private static int ParseXMLFile(string sz_filename)
        {
            int lReturn = 0;

            XmlTextReader oReader = new XmlTextReader(sz_filename);
            XmlDocument oDoc = new XmlDocument();
            Settings oUser = new Settings();

            try
            {
                if (oReader.Read())
                {
                    oDoc.Load(oReader);
                    if (oDoc.SelectSingleNode("cb") != null)
                    {
                        if (oDoc.SelectSingleNode("cb").Attributes.GetNamedItem("operation") != null)
                            switch (oDoc.SelectSingleNode("cb").Attributes.GetNamedItem("operation").Value)
                            {
                                case "NewAlertPolygon":
                                    break;
                                case "UpdateAlert":
                                    break;
                                case "KillAlert":
                                    break;
                                default:
                                    Log.WriteLog("ERROR: Operation not recognized", 2);
                                    lReturn = -2;
                                    break;
                            }
                    }
                    oReader.Close();
                }
            }
            catch (Exception e)
            {
                oReader.Close();
                Log.WriteLog("ERROR: Exception while reading XML " + e.Message, "ERROR: Exception while reading XML " + e.ToString(), 0);
                lReturn = -2;
            }
            return lReturn;
        }
    }
}
