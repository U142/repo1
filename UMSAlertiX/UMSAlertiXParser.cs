using System;
using System.Collections.Generic;
//using System.Linq;
using System.Text;
using System.Xml;
using System.IO;
using System.Threading;
using System.Globalization;

namespace UMSAlertiX
{
    // Parser class with sending methods
    public class UMSAlertiXParser
    {
        UMSAlertiXController oController;
        int lRetVal = 0;

        public void SetController(ref UMSAlertiXController objController)
        {
            oController = objController;
        }

        public void CheckFiles()
        {
            while (oController.running)
            {
                try
                {
                    string[] fileEntries = Directory.GetFiles(oController.parsepath + "eat\\", "LBA_*.xml");
                    Array.Sort(fileEntries);
                    foreach (string fileName in fileEntries)
                    {
                        if (!oController.running) break;
                        lRetVal = ParseXMLFile(fileName);
                        if (lRetVal == 0)
                        {
                            oController.log.WriteLog("Moving " + fileName + " to done.");
                            Thread.Sleep(500);
                            File.Move(fileName, fileName.Replace("\\eat\\", "\\done\\"));
                        }
                        else if (lRetVal == -1)
                        {
                            oController.log.WriteLog("Moving " + fileName + " to retry.");
                            Thread.Sleep(500);
                            File.Move(fileName, fileName.Replace("\\eat\\", "\\retry\\"));
                        }
                        else // lRetVal -2
                        {
                            oController.log.WriteLog("Moving " + fileName + " to failed.");
                            Thread.Sleep(500);
                            File.Move(fileName, fileName.Replace("\\eat\\", "\\failed\\"));
                        }
                    }
                    string[] fileEntriesRetry = Directory.GetFiles(oController.parsepath + "retry\\", "LBA_*.xml");
                    Array.Sort(fileEntriesRetry);
                    foreach (string fileName in fileEntriesRetry)
                    {
                        if (!oController.running) break;
                        FileInfo fFile = new FileInfo(fileName);
                        if (fFile.LastAccessTime.AddMinutes(1).CompareTo(DateTime.Now) <= 0)
                        {
                            lRetVal = ParseXMLFile(fileName);
                            if (lRetVal == 0)
                            {
                                oController.log.WriteLog("Moving " + fileName + " to done.");
                                Thread.Sleep(500);
                                File.Move(fileName, fileName.Replace("\\retry\\", "\\done\\"));
                            }
                            else if (lRetVal == -1)
                            {
                                oController.log.WriteLog("Moving " + fileName + " to retry.");
                                Thread.Sleep(500);
                                File.Move(fileName, fileName.Replace("\\retry\\", "\\retry\\"));
                            }
                            else // lRetVal -2
                            {
                                oController.log.WriteLog("Moving " + fileName + " to failed.");
                                Thread.Sleep(500);
                                File.Move(fileName, fileName.Replace("\\retry\\", "\\failed\\"));
                            }
                        }
                    }
                }
                catch (Exception e)
                {
                    oController.log.WriteLog("Exception during file poll: " + e.Message);
                }
                Thread.Sleep(1000);
            }
            oController.threads--;
            Console.WriteLine("# Stopped Parser thread", 1);
        }

        private bool SetUserValues(XmlAttributeCollection attr, ref UserValues uv)
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
                if(attr.GetNamedItem("l_comppk") != null)
                    if(attr.GetNamedItem("l_comppk").Value != "")
                        uv.l_comppk = int.Parse(attr.GetNamedItem("l_comppk").Value);
                if (attr.GetNamedItem("l_userpk") != null)
                    if (attr.GetNamedItem("l_userpk").Value != "")
                        uv.l_userpk = long.Parse(attr.GetNamedItem("l_userpk").Value);
                if (attr.GetNamedItem("sz_compid") != null)
                    uv.sz_compid = attr.GetNamedItem("sz_compid").Value;
                if (attr.GetNamedItem("sz_deptid") != null)
                    uv.sz_deptid = attr.GetNamedItem("sz_deptid").Value;
                if (attr.GetNamedItem("sz_password") != null)
                    uv.sz_password = attr.GetNamedItem("sz_password").Value;
                if (attr.GetNamedItem("sz_userid") != null)
                    uv.sz_userid = attr.GetNamedItem("sz_userid").Value;

                uv.operators = oController.GetOperators(ref uv);
            }
            catch (Exception e)
            {
                oController.log.WriteLog("Error reading user values. " + e.ToString(), "Error reading user values. " + e.Message);
                return false;
            }

            return bRetval;
        }

        // parse XML file and find operation type
        private int ParseXMLFile(string szFileName)
        {
            int lReturn = 0;

            oController.log.WriteLog("Reading " + szFileName);
            XmlTextReader oReader = new XmlTextReader(szFileName);
            XmlDocument oDoc = new XmlDocument();
            UMSAlertiXAlert oAlert = new UMSAlertiXAlert();
            UMSAlertiXArea oArea = new UMSAlertiXArea();
            UserValues oUser = new UserValues();

            oAlert.SetController(ref oController);
            oArea.SetController(ref oController);

            try
            {
                //oReader.Close();
                if (oReader.Read())
                {
                    oDoc.Load(oReader);
                    if (oDoc.SelectSingleNode("LBA") != null)
                    {
                        if (oDoc.SelectSingleNode("LBA").Attributes.GetNamedItem("operation") != null)
                            switch (oDoc.SelectSingleNode("LBA").Attributes.GetNamedItem("operation").Value)
                            {
                                case "InsertAreaPolygon":
                                    //oController.log.WriteLog("ins area polygon");
                                    if (!SetUserValues(oDoc.SelectSingleNode("LBA").Attributes, ref oUser))
                                        return -2;
                                    lReturn = oArea.InsertAreaPolygon(ref oDoc, ref oUser);
                                    break;
                                case "UpdateAreaPolygon":
                                    //oController.log.WriteLog("upd area polygon");
                                    if (!SetUserValues(oDoc.SelectSingleNode("LBA").Attributes, ref oUser))
                                        return -2;
                                    lReturn = oArea.UpdateAreaPolygon(ref oDoc, ref oUser);
                                    break;
                                case "InsertAreaEllipse":
                                    //oController.log.WriteLog("ins area ellipse");
                                    if (!SetUserValues(oDoc.SelectSingleNode("LBA").Attributes, ref oUser))
                                        return -2;
                                    lReturn = oArea.InsertAreaEllipse(ref oDoc, ref oUser);
                                    break;
                                case "UpdateAreaEllipse":
                                    //oController.log.WriteLog("upd area ellipse");
                                    if (!SetUserValues(oDoc.SelectSingleNode("LBA").Attributes, ref oUser))
                                        return -2;
                                    lReturn = oArea.UpdateAreaEllipse(ref oDoc, ref oUser);
                                    break;
                                case "DeleteArea":
                                    //oController.log.WriteLog("del area");
                                    lReturn = oArea.DeleteArea(ref oDoc);
                                    break;
                                case "SendArea":
                                    //oController.log.WriteLog("send area");
                                    if (!SetUserValues(oDoc.SelectSingleNode("LBA").Attributes, ref oUser))
                                        return -2;
                                    lReturn = oAlert.SendArea(ref oDoc, ref oUser);
                                    break;
                                case "SendPolygon":
                                    //oController.log.WriteLog("send polygon");
                                    if (!SetUserValues(oDoc.SelectSingleNode("LBA").Attributes, ref oUser))
                                        return -2;
                                    lReturn = oAlert.SendPolygon(ref oDoc, ref oUser);
                                    break;
                                case "SendEllipse":
                                    //oController.log.WriteLog("send ellipse");
                                    if (!SetUserValues(oDoc.SelectSingleNode("LBA").Attributes, ref oUser))
                                        return -2;
                                    lReturn = oAlert.SendEllipse(ref oDoc, ref oUser);
                                    break;
                                case "ConfirmJob":
                                    //oController.log.WriteLog("send prep alert");
                                    lReturn = oAlert.ExecutePreparedAlert(ref oDoc);
                                    break;
                                case "CancelJob":
                                    //oController.log.WriteLog("cancel prep alert");
                                    lReturn = oAlert.CancelPreparedAlert(ref oDoc);
                                    break;
                                default:
                                    oController.log.WriteLog("ERROR: Operation not recognized");
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
                oController.log.WriteLog("ERROR: Exception while reading XML " + e.ToString(), "ERROR: Exception while reading XML " + e.Message);
                lReturn = -2;
            }
            return lReturn;
        }
    }
}
