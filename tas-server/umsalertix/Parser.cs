using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml;
using System.IO;
using System.Collections;
using System.Threading;
using System.Globalization;

namespace umsalertix
{
    class Parser
    {
        static int lRetVal = 0;

        public static void CheckFiles()
        {
            while (Program.running)
            {
                try
                {
                    string[] fileEntries = null;
                    // get new files sorted by creation time
                    fileEntries = Directory.GetFiles(Settings.sz_parsepath + "eat\\", "*.xml").OrderBy(file => File.GetCreationTime(file)).ToArray();
                    foreach (string fileName in fileEntries)
                    {
                        if (!Program.running) break;
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

                    // get retry files sorted by creation time
                    fileEntries = Directory.GetFiles(Settings.sz_parsepath + "retry\\", "*.xml").OrderBy(file => File.GetCreationTime(file)).ToArray();
                    foreach (string fileName in fileEntries)
                    {
                        if (!Program.running) break;
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
                    Log.WriteLog("Exception during file poll: " + e.Message,2);
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
                    if (oDoc.SelectSingleNode("LBA") != null)
                    {
                        if (oDoc.SelectSingleNode("LBA").Attributes.GetNamedItem("operation") != null)
                            switch (oDoc.SelectSingleNode("LBA").Attributes.GetNamedItem("operation").Value)
                            {
                                case "InsertAreaPolygon":
                                    Log.WriteLog("ins area polygon",9);
                                    break;
                                case "UpdateAreaPolygon":
                                    Log.WriteLog("upd area polygon",9);
                                    break;
                                case "InsertAreaEllipse":
                                    Log.WriteLog("ins area ellipse",9);
                                    break;
                                case "UpdateAreaEllipse":
                                    Log.WriteLog("upd area ellipse",9);
                                    break;
                                case "DeleteArea":
                                    Log.WriteLog("del area",9);
                                    break;
                                case "SendArea":
                                    Log.WriteLog("send area",9);
                                    break;
                                case "SendPolygon":
                                    Log.WriteLog("send polygon",9);
                                    break;
                                case "SendEllipse":
                                    Log.WriteLog("send ellipse",9);
                                    break;
                                case "SendInternational":
                                    //Log.WriteLog("send international", 9);
                                    if (!SetUserValues(oDoc.SelectSingleNode("LBA").Attributes, oUser))
                                        return -2;
                                    lReturn = AXInternational.SendInternational(oDoc, oUser);
                                    break;
                                case "CountInternational":
                                    //Log.WriteLog("count international", 9);
                                    if (!SetUserValues(oDoc.SelectSingleNode("LBA").Attributes, oUser))
                                        return -2;
                                    lReturn = AXInternational.CountInternational(oDoc, oUser);
                                    break;
                                case "ConfirmInternational":
                                    //Log.WriteLog("send prep int alert",9);
                                    if (!SetUserValues(oDoc.SelectSingleNode("LBA").Attributes, oUser))
                                        return -2;
                                    lReturn = AXInternational.ConfirmPreparedAlert(oDoc, oUser);
                                    break;
                                case "CancelInternational":
                                    //Log.WriteLog("cancel prep int alert",9);
                                    if (!SetUserValues(oDoc.SelectSingleNode("LBA").Attributes, oUser))
                                        return -2;
                                    lReturn = AXInternational.CancelPreparedAlert(oDoc, oUser);
                                    break;
                                default:
                                    Log.WriteLog("ERROR: Operation not recognized",2);
                                    lReturn = -2;
                                    break;
                            }
                    }
                    else if (oDoc.SelectSingleNode("TAS") != null)
                    {
                        if (oDoc.SelectSingleNode("TAS").Attributes.GetNamedItem("operation") != null)
                            switch (oDoc.SelectSingleNode("TAS").Attributes.GetNamedItem("operation").Value)
                            {
                                case "CreateList":
                                    //Log.WriteLog("CreateList", 9);
                                    if (!SetUserValues(oDoc.SelectSingleNode("TAS").Attributes, oUser))
                                        return -2;
                                    lReturn = AXAdmin.CreateWhitelist(oDoc, oUser);
                                    break;
                                case "UpdateWhitelist":
                                    //Log.WriteLog("UpdateWhitelist", 9);
                                    if (!SetUserValues(oDoc.SelectSingleNode("TAS").Attributes, oUser))
                                        return -2;
                                    lReturn = AXAdmin.UpdateWhitelist(oDoc, oUser);
                                    break;
                                case "DeleteList":
                                    //Log.WriteLog("DeleteList", 9);
                                    if (!SetUserValues(oDoc.SelectSingleNode("TAS").Attributes, oUser))
                                        return -2;
                                    lReturn = AXAdmin.DeleteWhitelist(oDoc, oUser);
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

        private static bool SetUserValues(XmlAttributeCollection attr, Settings uv)
        {
            bool bRetval = true;
            try
            {
                // required fields
                if (attr.GetNamedItem("l_deptpk") != null)
                {
                    if (attr.GetNamedItem("l_deptpk").Value != "")
                    {
                        uv.l_deptpk = int.Parse(attr.GetNamedItem("l_deptpk").Value);
                    }
                    else
                    {
                        bRetval = false;
                    }
                }
                else
                {
                    bRetval = false;
                }

                // optional fields
                if (attr.GetNamedItem("l_comppk") != null)
                    if (attr.GetNamedItem("l_comppk").Value != "")
                        uv.l_comppk = int.Parse(attr.GetNamedItem("l_comppk").Value);
                
                if (attr.GetNamedItem("l_userpk") != null)
                    if (attr.GetNamedItem("l_userpk").Value != "")
                        uv.l_userpk = long.Parse(attr.GetNamedItem("l_userpk").Value);
                
                if (attr.GetNamedItem("sz_compid") != null)
                    uv.sz_compid = attr.GetNamedItem("sz_compid").Value;
                else if (uv.l_comppk != 0)
                    uv.sz_compid = Database.GetCompID(uv.l_comppk);
                
                if (attr.GetNamedItem("sz_deptid") != null)
                    uv.sz_deptid = attr.GetNamedItem("sz_deptid").Value;
                else if (uv.l_deptpk !=0 )
                    uv.sz_deptid = Database.GetDeptID(uv.l_deptpk);
                
                if (attr.GetNamedItem("sz_password") != null)
                    uv.sz_password = attr.GetNamedItem("sz_password").Value;

                if (attr.GetNamedItem("sz_userid") != null)
                    uv.sz_userid = attr.GetNamedItem("sz_userid").Value;
                else if (uv.l_userpk != 0)
                    uv.sz_userid = Database.GetUserID(uv.l_userpk);
                else
                    uv.sz_userid = "system";

                uv.operators = Operator.GetOperators(uv);
            }
            catch (Exception e)
            {
                Log.WriteLog("Error reading user values. " + e.ToString(), "Error reading user values. " + e.Message,2);
                return false;
            }

            return bRetval;
        }
    }
}
