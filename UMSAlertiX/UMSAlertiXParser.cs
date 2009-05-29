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
                //string[] fileEntries = Directory.GetFiles("\\\\192.168.3.40\\ums\\var\\lba\\eat\\", "LBA_*.xml");
                string[] fileEntries = Directory.GetFiles(oController.parsepath + "eat\\", "LBA_*.xml");
                Array.Sort(fileEntries);
                foreach (string fileName in fileEntries)
                {
                    if (!oController.running) break;
                    lRetVal = ParseXMLFile(fileName);
                    if (lRetVal == 0)
                    {
                        oController.log.WriteLog("Moving " + fileName + " to done.");
                        File.Move(fileName, fileName.Replace("\\eat\\", "\\done\\"));
                    }
                    else if (lRetVal == -1)
                    {
                        oController.log.WriteLog("Moving " + fileName + " to retry.");
                        File.Move(fileName, fileName.Replace("\\eat\\", "\\retry\\"));
                    }
                    else // lRetVal -2
                    {
                        oController.log.WriteLog("Moving " + fileName + " to failed.");
                        File.Move(fileName, fileName.Replace("\\eat\\", "\\failed\\"));
                    }
                }
                //string[] fileEntriesRetry = Directory.GetFiles("\\\\192.168.3.40\\ums\\var\\lba\\retry\\", "LBA_*.xml");
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
                            File.Move(fileName, fileName.Replace("\\retry\\", "\\done\\"));
                        }
                        else if (lRetVal == -1)
                        {
                            oController.log.WriteLog("Moving " + fileName + " to retry.");
                            File.Move(fileName, fileName.Replace("\\retry\\", "\\retry\\"));
                        }
                        else // lRetVal -2
                        {
                            oController.log.WriteLog("Moving " + fileName + " to failed.");
                            File.Move(fileName, fileName.Replace("\\retry\\", "\\failed\\"));
                        }
                    }
                }
                Thread.Sleep(1000);
            }
            oController.threads--;
            Console.WriteLine("# Stopped Parser thread", 1);
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

            oAlert.SetController(ref oController);
            oArea.SetController(ref oController);

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
                                lReturn = oArea.InsertAreaPolygon(ref oDoc);
                                break;
                            case "UpdateAreaPolygon":
                                //oController.log.WriteLog("upd area polygon");
                                lReturn = oArea.UpdateAreaPolygon(ref oDoc);
                                break;
                            case "InsertAreaEllipse":
                                //oController.log.WriteLog("ins area ellipse");
                                lReturn = oArea.InsertAreaEllipse(ref oDoc);
                                break;
                            case "UpdateAreaEllipse":
                                //oController.log.WriteLog("upd area ellipse");
                                lReturn = oArea.UpdateAreaEllipse(ref oDoc);
                                break;
                            case "DeleteArea":
                                //oController.log.WriteLog("del area");
                                lReturn = oArea.DeleteArea(ref oDoc);
                                break;
                            case "SendArea":
                                //oController.log.WriteLog("send area");
                                lReturn = oAlert.SendArea(ref oDoc);
                                break;
                            case "SendPolygon":
                                //oController.log.WriteLog("send polygon");
                                lReturn = oAlert.SendPolygon(ref oDoc);
                                break;
                            case "SendEllipse":
                                //oController.log.WriteLog("send ellipse");
                                lReturn = oAlert.SendEllipse(ref oDoc);
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
            return lReturn;
        }
    }
}
