using System;
using System.Collections.Generic;
//using System.Linq;
using System.Text;
using System.Xml;
using System.Globalization;
using System.Net;
using UMSAlertiX.AlertiX3;
using com.ums.UmsCommon;
using com.ums.UmsCommon.CoorConvert;
using System.Data.Odbc;
using com.ums.UmsCommon;

namespace UMSAlertiX
{
    public class UMSAlertiXArea
    {
        UMSAlertiXController oController;

        public void SetController(ref UMSAlertiXController objController)
        {
            oController = objController;
        }

        public int InsertAreaPolygon(ref XmlDocument oDoc, ref UserValues oUser)
        {
            Point[] arrPoint = null;
            String szAreaName = "";
            UTM uCoConv = new UTM();

            double UTMnorth, UTMeast;
            string szZone;

            int lReturn = 0;
            NumberFormatInfo provider = new NumberFormatInfo();
            provider.NumberDecimalSeparator = ".";

            szAreaName = oDoc.SelectSingleNode("LBA").SelectSingleNode("alertpolygon").Attributes.GetNamedItem("l_alertpk").Value;
            oController.log.WriteLog(szAreaName + " Insert Area (polygon)");
            
            arrPoint = new Point[oDoc.SelectSingleNode("LBA").SelectSingleNode("alertpolygon").ChildNodes.Count];
            int i = 0;

            foreach (XmlNode oNode in oDoc.SelectSingleNode("LBA").SelectSingleNode("alertpolygon").ChildNodes)
            {
                UTMeast = 0;
                UTMnorth = 0;
                szZone = "";

                uCoConv.LL2UTM(23, Double.Parse(oNode.Attributes.GetNamedItem("ycord").Value, provider), Double.Parse(oNode.Attributes.GetNamedItem("xcord").Value, provider), 33, ref UTMnorth, ref UTMeast, ref szZone);

                arrPoint[i] = new Point();
                arrPoint[i].easting = UTMeast;
                arrPoint[i].northing = UTMnorth;

                i++;
            }

            // call AlertiX api with polygon
            if (!CreateArea(ref arrPoint, ref szAreaName, ref oUser))
            {
                lReturn = -2;
            }

            return lReturn;
        }

        public int InsertAreaEllipse(ref XmlDocument oDoc, ref UserValues oUser)
        {
            Point[] arrPoint = new Point[36];
            String szAreaName = "";
            UTM uCoConv = new UTM();

            double UTMnorth, UTMeast;
            string szZone;

            int lReturn = 0;
            NumberFormatInfo provider = new NumberFormatInfo();
            provider.NumberDecimalSeparator = ".";

            int steps = 36;
            double centerx = Double.Parse(oDoc.SelectSingleNode("LBA").SelectSingleNode("alertellipse").Attributes.GetNamedItem("centerx").Value, provider);
            double centery = Double.Parse(oDoc.SelectSingleNode("LBA").SelectSingleNode("alertellipse").Attributes.GetNamedItem("centery").Value, provider);
            double cornerx = Double.Parse(oDoc.SelectSingleNode("LBA").SelectSingleNode("alertellipse").Attributes.GetNamedItem("cornerx").Value, provider);
            double cornery = Double.Parse(oDoc.SelectSingleNode("LBA").SelectSingleNode("alertellipse").Attributes.GetNamedItem("cornery").Value, provider);
            szAreaName = oDoc.SelectSingleNode("LBA").SelectSingleNode("alertellipse").Attributes.GetNamedItem("l_alertpk").Value;
            oController.log.WriteLog(szAreaName + " Insert Area (ellipse)");

            double[,] arrPoly = new double[steps, 2];

            if(UCommon.ConvertEllipseToPolygon(centerx, centery, cornerx, cornery, steps, 0, ref arrPoly))
            {
                // put array of doubles into array of points
                for (int i = 0; i < steps; i++)
                {
                    UTMeast = 0;
                    UTMnorth = 0;
                    szZone = "";

                    uCoConv.LL2UTM(23, arrPoly[i, 1], arrPoly[i, 0], 33, ref UTMnorth, ref UTMeast, ref szZone);

                    arrPoint[i] = new Point();
                    arrPoint[i].easting = UTMeast;
                    arrPoint[i].northing = UTMnorth;
                }

                // call AlertiX api with polygon
                if (!CreateArea(ref arrPoint, ref szAreaName, ref oUser))
                {
                    lReturn = -2;
                }
            }
            else
            {
                lReturn = -2;
            }

            return lReturn;
        }

        public int UpdateAreaPolygon(ref XmlDocument oDoc, ref UserValues oUser)
        {
            Point[] arrPoint = null;
            String szAreaName = "";
            UTM uCoConv = new UTM();

            double UTMnorth, UTMeast;
            string szZone;

            int lReturn = 0;
            NumberFormatInfo provider = new NumberFormatInfo();
            provider.NumberDecimalSeparator = ".";

            szAreaName = oDoc.SelectSingleNode("LBA").SelectSingleNode("alertpolygon").Attributes.GetNamedItem("l_alertpk").Value;
            oController.log.WriteLog(szAreaName + " Update Area (polygon)");

            arrPoint = new Point[oDoc.SelectSingleNode("LBA").SelectSingleNode("alertpolygon").ChildNodes.Count];
            int i = 0;

            foreach (XmlNode oNode in oDoc.SelectSingleNode("LBA").SelectSingleNode("alertpolygon").ChildNodes)
            {
                UTMeast = 0;
                UTMnorth = 0;
                szZone = "";

                uCoConv.LL2UTM(23, Double.Parse(oNode.Attributes.GetNamedItem("ycord").Value, provider), Double.Parse(oNode.Attributes.GetNamedItem("xcord").Value, provider), 33, ref UTMnorth, ref UTMeast, ref szZone);

                arrPoint[i] = new Point();
                arrPoint[i].easting = UTMeast;
                arrPoint[i].northing = UTMnorth;

                i++;
            }

            // call AlertiX api with polygon
            PolygonTarget polygonTarget = new PolygonTarget();
            polygonTarget.polygon = arrPoint;
            polygonTarget.name = szAreaName;

            if (!UpdateArea(polygonTarget, oUser))
            {
                lReturn = -2;
            }

            return lReturn;
        }

        public int UpdateAreaEllipse(ref XmlDocument oDoc, ref UserValues oUser)
        {
            Point[] arrPoint = new Point[36];
            String szAreaName = "";
            UTM uCoConv = new UTM();

            double UTMnorth, UTMeast;
            string szZone;

            int lReturn = 0;
            NumberFormatInfo provider = new NumberFormatInfo();
            provider.NumberDecimalSeparator = ".";

            int steps = 36;
            double centerx = Double.Parse(oDoc.SelectSingleNode("LBA").SelectSingleNode("alertellipse").Attributes.GetNamedItem("centerx").Value, provider);
            double centery = Double.Parse(oDoc.SelectSingleNode("LBA").SelectSingleNode("alertellipse").Attributes.GetNamedItem("centery").Value, provider);
            double cornerx = Double.Parse(oDoc.SelectSingleNode("LBA").SelectSingleNode("alertellipse").Attributes.GetNamedItem("cornerx").Value, provider);
            double cornery = Double.Parse(oDoc.SelectSingleNode("LBA").SelectSingleNode("alertellipse").Attributes.GetNamedItem("cornery").Value, provider);
            szAreaName = oDoc.SelectSingleNode("LBA").SelectSingleNode("alertellipse").Attributes.GetNamedItem("l_alertpk").Value;
            oController.log.WriteLog(szAreaName + " Update Area (ellipse)");

            double[,] arrPoly = new double[steps, 2];

            if(UCommon.ConvertEllipseToPolygon(centerx, centery, cornerx, cornery, steps, 0, ref arrPoly))
            {
                // put array of doubles into array of points
                for (int i = 0; i < steps; i++)
                {
                    UTMeast = 0;
                    UTMnorth = 0;
                    szZone = "";

                    uCoConv.LL2UTM(23, arrPoly[i, 1], arrPoly[i, 0], 33, ref UTMnorth, ref UTMeast, ref szZone);

                    arrPoint[i] = new Point();
                    arrPoint[i].easting = UTMeast;
                    arrPoint[i].northing = UTMnorth;
                }

                // call AlertiX api with polygon
                PolygonTarget polygonTarget = new PolygonTarget();
                polygonTarget.polygon = arrPoint;
                polygonTarget.name = szAreaName;

                if (!UpdateArea(polygonTarget, oUser))
                {
                    lReturn = -2;
                }
            }
            else
            {
                lReturn = -2;
            }

            return lReturn;
        }

        public int DeleteArea(ref XmlDocument oDoc)
        {
            String szAreaName = "";
            Operator[] operators;

            int lReturn = 0;
            long l_alertpk=0;

            szAreaName = oDoc.SelectSingleNode("LBA").Attributes.GetNamedItem("sz_areaid").Value;
            if (szAreaName == "")
            {
                oController.log.WriteLog("sz_areaid not specified, using l_alertpk instead");
                szAreaName = oDoc.SelectSingleNode("LBA").Attributes.GetNamedItem("l_alertpk").Value;
            }
            l_alertpk = long.Parse(oDoc.SelectSingleNode("LBA").Attributes.GetNamedItem("l_alertpk").Value);

            operators = oController.GetAlertOperators(l_alertpk);

            foreach (Operator op in operators)
            {
                if (!DeleteArea(szAreaName, op))
                {
                    lReturn = -2;
                }
            }

            return lReturn;
        }

        private Point[] GetPointsFromPAShape(string xml)
        {
            List<Point> ret = new List<Point>();
            XmlDocument doc = new XmlDocument();

            xml = string.Format(@"<?xml version=""1.0"" encoding=""utf-8""?>
<POLYGONS>{0}</POLYGONS>", xml);

            doc.LoadXml(xml);

            UTM uCoConv = new UTM();

            NumberFormatInfo provider = new NumberFormatInfo();
            provider.NumberDecimalSeparator = ".";

            double UTMnorth, UTMeast;
            string szZone;

            XmlNode root = doc.DocumentElement;
            XmlNodeList Children = root.ChildNodes;
            for (int i = 0; i < Children.Count; i++)
            {
                XmlNode node = Children.Item(i);
                String sz_nodetype = node.Name.ToLower();
                if (sz_nodetype.Equals("alertpolygon"))
                {
                    // get polygon points
                    XmlNodeList points = node.ChildNodes;
                    for (int j = 0; j < points.Count; j++)
                    {
                        XmlNode point = points.Item(j);
                        String l_coorx, l_coory;
                        l_coorx = point.Attributes["xcord"].Value;
                        l_coory = point.Attributes["ycord"].Value;

                        UTMeast = 0;
                        UTMnorth = 0;
                        szZone = "";

                        double xcord = double.Parse(l_coorx, provider);
                        double ycord = double.Parse(l_coory, provider);

                        uCoConv.LL2UTM(23, ycord, xcord, 33, ref UTMnorth, ref UTMeast, ref szZone);

                        ret.Add(new Point() { easting = UTMeast, northing = UTMnorth });
                    }
                }
                else if (sz_nodetype.Equals("alertellipse"))
                {
                    int steps = 36;

                    double centerx = double.Parse(node.Attributes["centerx"].Value, provider);
                    double centery = double.Parse(node.Attributes["centery"].Value, provider);
                    double cornerx = double.Parse(node.Attributes["cornerx"].Value, provider);
                    double cornery = double.Parse(node.Attributes["cornery"].Value, provider);

                    double[,] arrPoly = new double[steps, 2];

                    if (UCommon.ConvertEllipseToPolygon(centerx, centery, cornerx, cornery, steps, 0, ref arrPoly))
                    {
                        // put array of doubles into array of points
                        for (int j = 0; j < steps; j++)
                        {
                            UTMeast = 0;
                            UTMnorth = 0;
                            szZone = "";

                            uCoConv.LL2UTM(23, arrPoly[i, 1], arrPoly[i, 0], 33, ref UTMnorth, ref UTMeast, ref szZone);

                            ret.Add(new Point() { easting = UTMeast, northing = UTMnorth });
                        }
                    }
                }
            }

            return ret.ToArray();
        }

        public bool CheckStoredArea(StoredTarget target, UserValues oUser)
        {
            foreach(Operator op in oUser.operators)
            {
                if (AreaExists(target.name, op))
                {
                    // all good, do nothing
                }
                else
                {
                    oController.log.WriteLog(target.name + " (" + op.sz_operatorname + ") WARNING area does not exist, creating new");

                    // create area
                    string xml = oController.getPAShapeFromDb(target.name);

                    if (xml == null)
                        xml = oController.GetPAShapeFromFile(target.name);

                    if (xml != null)
                    {
                        try
                        {
                            Point[] arrPoint = GetPointsFromPAShape(xml);

                            // call AlertiX api with polygon
                            PolygonTarget polygonTarget = new PolygonTarget();
                            polygonTarget.polygon = arrPoint;
                            polygonTarget.name = target.name;

                            if (!UpdateArea(polygonTarget, oUser)) // failed to update an area, return failure
                                return false;
                        }
                        catch (Exception e)
                        {
                            oController.log.WriteLog(target.name + " (" + op.sz_operatorname + ") ERROR: Failed to create area (res=" + e.Message + ")");
                            return false;
                        }
                    }
                }
            }

            // all ok, return success
            return true;
        }

        // communicate with AlertiX
        private bool CreateArea(ref Point[] areaPoints, ref String areaName, ref UserValues userValues)
        {
            bool bReturn=true;
            AlertixWsApiService alertixService = new AlertixWsApiService();
            AlertiX3.Response createResponse = new AlertiX3.Response();
            PolygonTarget polygonTarget = new PolygonTarget();

            polygonTarget.polygon = new Point[areaPoints.Length];
            for (int iCount = 0; iCount < areaPoints.Length; iCount++)
            {
                polygonTarget.polygon[iCount] = new Point();
                polygonTarget.polygon[iCount] = areaPoints[iCount];
            }

            polygonTarget.name = areaName;

            // Insert operators to PAALERT_LBA
            InsertPAALERT_LBA(areaName, ref userValues);

            // Get operators
            foreach (Operator op in userValues.operators)
            {
                try
                {
                    alertixService.Url = op.sz_url + oController.statusapi;

                    NetworkCredential objNetCredentials = new NetworkCredential(op.sz_user, op.sz_password);
                    Uri uri = new Uri(alertixService.Url);

                    ICredentials objAuth = objNetCredentials.GetCredential(uri, "Basic");
                    alertixService.Credentials = objAuth;
                    alertixService.PreAuthenticate = true;

                    oController.log.WriteLog(areaName + " (" + op.sz_operatorname + ") Creating area");

                    createResponse = alertixService.createTarget(polygonTarget);
                    if (createResponse.result == Result.SUCCESS)
                    {
                        //InsertPAALERT_LBA(long.Parse(areaName), op.l_operator, 0, long.Parse(areaName), (int)createResponse.result);
                        InsertPAALERT_LBA(long.Parse(areaName), op.l_operator, 0, long.Parse(areaName), 200);
                        bReturn = true;
                    }
                    else // failed, try update
                    {
                        oController.log.WriteLog(areaName + " (" + op.sz_operatorname + ") Create Area Failed (code=" + createResponse.result.ToString() + ") (msg=" + createResponse.description.ToString() + "), trying update.");
                        bReturn = UpdateArea(polygonTarget, op, userValues);
                    }
                }
                catch (Exception e)
                {
                    oController.log.WriteLog(areaName + " (" + op.sz_operatorname + ") Create Area Failed (exception): " + e.Message);
                    InsertPAALERT_LBA(long.Parse(areaName), op.l_operator, -2, -2, 0);
                    bReturn = false;
                }
            }
            return bReturn;
        }

        private bool UpdateArea(PolygonTarget polygonTarget, UserValues userValues)
        {
            bool bRet = true;

            foreach (Operator op in userValues.operators)
            {
                if (!UpdateArea(polygonTarget, op, userValues))
                    bRet = false;
            }

            return bRet;
        }

        private bool InsertPAALERT_LBA(string sz_areaname, ref UserValues oUser)
        {
            bool bRet = true;
            foreach (Operator op in oUser.operators)
            {
                if(!InsertPAALERT_LBA(long.Parse(sz_areaname), op.l_operator, -1, 0, 0))
                    bRet = false;
            }
            return bRet;
        }

        private bool InsertPAALERT_LBA(long l_alertpk, int l_operator, int l_status, long l_areaname, int l_responsecode)
        {
            bool bRet = true;
            try
            {
                oController.ExecDB("sp_ins_paalert_lba " + l_alertpk.ToString() + ", " + l_operator.ToString() + ", " + l_status.ToString() + ", " + l_areaname.ToString() + ", " + l_responsecode.ToString(), oController.dsn);
            }
            catch (Exception e)
            {
                oController.log.WriteLog(e.ToString(), e.Message);
                bRet = false;
            }
            return bRet;
        }

        private bool UpdateArea(PolygonTarget polygonTarget, Operator op, UserValues userValues)
        {
            bool bReturn=true;
            AlertixWsApiService aArea = new AlertixWsApiService();
            AlertiX3.Response aResponse = new AlertiX3.Response();

            try
            {
                oController.log.WriteLog(polygonTarget.name + " (" + op.sz_operatorname + ") Updating Area");

                aArea.Url = op.sz_url + oController.areaapi;

                NetworkCredential objNetCredentials = new NetworkCredential(op.sz_user, op.sz_password);
                Uri uri = new Uri(aArea.Url);

                ICredentials objAuth = objNetCredentials.GetCredential(uri, "Basic");
                aArea.Credentials = objAuth;
                aArea.PreAuthenticate = true;

                if (AreaExists(polygonTarget.name, op))
                {
                    aResponse = aArea.deleteTarget(polygonTarget.name);
                }

                aResponse = aArea.createTarget(polygonTarget);
                if (aResponse.result == Result.SUCCESS)
                {
                    //InsertPAALERT_LBA(long.Parse(polygonTarget.name), op.l_operator, 0, long.Parse(polygonTarget.name), (int)aResponse.result);
                    InsertPAALERT_LBA(long.Parse(polygonTarget.name), op.l_operator, 0, long.Parse(polygonTarget.name), 200);
                    bReturn = true;
                }
                else
                {
                    oController.log.WriteLog(polygonTarget.name + " (" + op.sz_operatorname + ") Update Area Failed (code=" + aResponse.result.ToString() + ") (msg=" + aResponse.description + ") trying to insert instead.");

                    aResponse = aArea.createTarget(polygonTarget);
                    if (aResponse.result == Result.SUCCESS)
                    {
                        //InsertPAALERT_LBA(long.Parse(polygonTarget.name), op.l_operator, 0, long.Parse(polygonTarget.name), (int)aResponse.result);
                        InsertPAALERT_LBA(long.Parse(polygonTarget.name), op.l_operator, 0, long.Parse(polygonTarget.name), 200); // set response to 200 for compatibility with old PAS
                        bReturn = true;
                    }
                    else // failed, give up
                    {
                        oController.log.WriteLog(polygonTarget.name + " (" + op.sz_operatorname + ") Insert Area Failed (code=" + aResponse.result.ToString() + ") (msg=" + aResponse.description + ")");
                        InsertPAALERT_LBA(long.Parse(polygonTarget.name), op.l_operator, -2, -2, (int)aResponse.result); // may use aResponse.result on a later date
                        bReturn = false;
                    }
                }
            }
            catch (Exception e)
            {
                oController.log.WriteLog(polygonTarget.name + " (" + op.sz_operatorname + ") Update Area Failed (exception): " + e.Message);
                InsertPAALERT_LBA(long.Parse(polygonTarget.name), op.l_operator, -2, -2, 0);
                bReturn = false;
            }

            return bReturn;
        }

        private bool AreaExists(String szAreaName, Operator op)
        {
            bool bReturn = true;
            AlertixWsApiService aArea = new AlertixWsApiService();
            TargetResponse aResponse = new TargetResponse();

            try
            {
                aArea.Url = op.sz_url + oController.areaapi;

                NetworkCredential objNetCredentials = new NetworkCredential(op.sz_user, op.sz_password);
                Uri uri = new Uri(aArea.Url);

                ICredentials objAuth = objNetCredentials.GetCredential(uri, "Basic");
                aArea.Credentials = objAuth;
                aArea.PreAuthenticate = true;

                aResponse = aArea.getTarget(szAreaName);
                if (aResponse.resultSpecified)
                {
                    if (aResponse.result == Result.SUCCESS)
                    {
                        bReturn = true;
                    }
                    else
                    {
                        // area doesn't exist, don't bother logging
                        bReturn = false;
                    }
                }
                else
                {
                    oController.log.WriteLog(szAreaName + " (" + op.sz_operatorname + ") AreaExists Failed: " + aResponse.description);
                    bReturn = false;
                }
            }
            catch (Exception e)
            {
                oController.log.WriteLog(szAreaName + " (" + op.sz_operatorname + ") AreaExists Failed (exception): " + e.Message);
                bReturn = false;
            }

            return bReturn;
        }

        private bool DeleteArea(String szAreaName, Operator op)
        {
            bool bReturn=true;
            AlertixWsApiService aArea = new AlertixWsApiService();
            AlertiX3.Response aResponse = new AlertiX3.Response();

            try
            {
                aArea.Url = op.sz_url + oController.areaapi;

                NetworkCredential objNetCredentials = new NetworkCredential(op.sz_user, op.sz_password);
                Uri uri = new Uri(aArea.Url);

                ICredentials objAuth = objNetCredentials.GetCredential(uri, "Basic");
                aArea.Credentials = objAuth;
                aArea.PreAuthenticate = true;

                oController.log.WriteLog(szAreaName + " (" + op.sz_operatorname + ") Deleting Area");

                aResponse = aArea.deleteTarget(szAreaName);
                if (aResponse.resultSpecified)
                {
                    if (aResponse.result == Result.SUCCESS)
                    {
                        bReturn = true;
                    }
                    else
                    {
                        oController.log.WriteLog(szAreaName + " (" + op.sz_operatorname + ") Delete Area Failed: " + aResponse.description);
                        bReturn = false;
                    }
                }
                else
                {
                    oController.log.WriteLog(szAreaName + " (" + op.sz_operatorname + ") Delete Area Failed: " + aResponse.description);
                    bReturn = false;
                }
            }
            catch (Exception e)
            {
                oController.log.WriteLog(szAreaName + " (" + op.sz_operatorname + ") Delete Area Failed (exception): " + e.Message);
                bReturn = false;
            }

            return bReturn;
        }
    }
}
