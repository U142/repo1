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
                    Array.Sort(eatfileEntries);
                    foreach (string fileName in eatfileEntries)
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

                    string[] retryfileEntries = Directory.GetFiles(Settings.sz_parsepath + "retry\\", "*.xml");
                    Array.Sort(retryfileEntries);
                    foreach (string fileName in retryfileEntries)
                    {
                        if (!CBServer.running) break;
                        FileInfo fFile = new FileInfo(fileName);
                        if (fFile.LastAccessTime.AddMinutes(1).CompareTo(DateTime.Now) <= 0)
                        {
                            lRetVal = ParseXMLFile(fileName);
                            if (lRetVal == 0)
                            {
                                Thread.Sleep(500);
                                File.Move(fileName, fileName.Replace("\\retry\\", "\\done\\"));
                            }
                            else if (lRetVal == -1)
                            {
                                Thread.Sleep(500);
                                File.Move(fileName, fileName.Replace("\\retry\\", "\\retry\\"));
                            }
                            else // lRetVal -2
                            {
                                Thread.Sleep(500);
                                File.Move(fileName, fileName.Replace("\\retry\\", "\\failed\\"));
                            }
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
                                    lReturn = CreateAlert(oDoc.SelectSingleNode("cb"));
                                    break;
                                case "UpdateAlert":
                                    lReturn = UpdateAlert(oDoc.SelectSingleNode("cb"));
                                    break;
                                case "KillAlert":
                                    lReturn = KillAlert(oDoc.SelectSingleNode("cb"));
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

        private static int CreateAlert(XmlNode xmlCB)
        {
            // create an alert for each operator
            return 0;
        }
        private static int UpdateAlert(XmlNode xmlCB)
        {
            // update a given alert at each operator
            return 0;
        }
        private static int KillAlert(XmlNode xmlCB)
        {
            // kill a given alert at each operator
            return 0;
        }
        private static int GetAlertStatus()
        {
            // get alert status
            // all alerts for all operators?
            return 0;
        }
    }
}
