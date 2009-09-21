using System;
using System.Collections.Generic;
//using System.Linq;
using System.Text;
using System.Xml;
using System.Globalization;
using System.Net;
using UMSAlertiX.AlertiXAreaApi;
using com.ums.UmsCommon;
using com.ums.UmsCommon.CoorConvert;
using System.Data.Odbc;

namespace UMSAlertiX
{
    public class UMSAlertiXArea
    {
        UMSAlertiXController oController;

        public void SetController(ref UMSAlertiXController objController)
        {
            oController = objController;
        }

        public int InsertAreaPolygon(ref XmlDocument oDoc)
        {
            Point[] arrPoint = null;
            AreaName szAreaName = new AreaName();
            UTM uCoConv = new UTM();

            double UTMnorth, UTMeast;
            string szZone;

            int lReturn = 0;
            NumberFormatInfo provider = new NumberFormatInfo();
            provider.NumberDecimalSeparator = ".";

            szAreaName.value = oDoc.SelectSingleNode("LBA").SelectSingleNode("alertpolygon").Attributes.GetNamedItem("l_alertpk").Value;
            oController.log.WriteLog(szAreaName.value + " Insert Area (polygon)");
            
            arrPoint = new Point[oDoc.SelectSingleNode("LBA").SelectSingleNode("alertpolygon").ChildNodes.Count];
            int i = 0;

            foreach (XmlNode oNode in oDoc.SelectSingleNode("LBA").SelectSingleNode("alertpolygon").ChildNodes)
            {
                UTMeast = 0;
                UTMnorth = 0;
                szZone = "";

                uCoConv.LL2UTM(23, Double.Parse(oNode.Attributes.GetNamedItem("ycord").Value, provider), Double.Parse(oNode.Attributes.GetNamedItem("xcord").Value, provider), 33, ref UTMnorth, ref UTMeast, ref szZone);

                arrPoint[i] = new Point();
                arrPoint[i].x = UTMeast;
                arrPoint[i].y = UTMnorth;
                arrPoint[i].xSpecified = true;
                arrPoint[i].ySpecified = true;

                i++;
            }

            // call AlertiX api with polygon
            if (!CreateArea(ref arrPoint, ref szAreaName))
            {
                lReturn = -2;
            }

            return lReturn;
        }

        public int InsertAreaEllipse(ref XmlDocument oDoc)
        {
            Point[] arrPoint = new Point[36];
            AreaName szAreaName = new AreaName();
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
            szAreaName.value = oDoc.SelectSingleNode("LBA").SelectSingleNode("alertellipse").Attributes.GetNamedItem("l_alertpk").Value;
            oController.log.WriteLog(szAreaName.value + " Insert Area (ellipse)");

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
                    arrPoint[i].x = UTMeast;
                    arrPoint[i].y = UTMnorth;
                    arrPoint[i].xSpecified = true;
                    arrPoint[i].ySpecified = true;
                }

                // call AlertiX api with polygon
                if (!CreateArea(ref arrPoint, ref szAreaName))
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

        public int UpdateAreaPolygon(ref XmlDocument oDoc)
        {
            Point[] arrPoint = null;
            AreaName szAreaName = new AreaName();
            UTM uCoConv = new UTM();

            double UTMnorth, UTMeast;
            string szZone;

            int lReturn = 0;
            NumberFormatInfo provider = new NumberFormatInfo();
            provider.NumberDecimalSeparator = ".";

            szAreaName.value = oDoc.SelectSingleNode("LBA").SelectSingleNode("alertpolygon").Attributes.GetNamedItem("l_alertpk").Value;
            oController.log.WriteLog(szAreaName.value + " Update Area (polygon)");

            arrPoint = new Point[oDoc.SelectSingleNode("LBA").SelectSingleNode("alertpolygon").ChildNodes.Count];
            int i = 0;

            foreach (XmlNode oNode in oDoc.SelectSingleNode("LBA").SelectSingleNode("alertpolygon").ChildNodes)
            {
                UTMeast = 0;
                UTMnorth = 0;
                szZone = "";

                uCoConv.LL2UTM(23, Double.Parse(oNode.Attributes.GetNamedItem("ycord").Value, provider), Double.Parse(oNode.Attributes.GetNamedItem("xcord").Value, provider), 33, ref UTMnorth, ref UTMeast, ref szZone);

                arrPoint[i] = new Point();
                arrPoint[i].x = UTMeast;
                arrPoint[i].y = UTMnorth;
                arrPoint[i].xSpecified = true;
                arrPoint[i].ySpecified = true;

                i++;
            }

            // call AlertiX api with polygon

            if (!UpdateArea(ref arrPoint, ref szAreaName))
            {
                lReturn = -2;
            }

            return lReturn;
        }

        public int UpdateAreaEllipse(ref XmlDocument oDoc)
        {
            Point[] arrPoint = new Point[36];
            AreaName szAreaName = new AreaName();
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
            szAreaName.value = oDoc.SelectSingleNode("LBA").SelectSingleNode("alertellipse").Attributes.GetNamedItem("l_alertpk").Value;
            oController.log.WriteLog(szAreaName.value + " Update Area (ellipse)");

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
                    arrPoint[i].x = UTMeast;
                    arrPoint[i].y = UTMnorth;
                    arrPoint[i].xSpecified = true;
                    arrPoint[i].ySpecified = true;
                }

                // call AlertiX api with polygon
                if (!UpdateArea(ref arrPoint, ref szAreaName))
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
            AreaName szAreaName = new AreaName();

            int lReturn = 0;

            szAreaName.value = oDoc.SelectSingleNode("LBA").Attributes.GetNamedItem("sz_areaid").Value;
            if (szAreaName.value == "")
            {
                oController.log.WriteLog("sz_areaid not specified, using l_alertpk instead");
                szAreaName.value = oDoc.SelectSingleNode("LBA").Attributes.GetNamedItem("l_alertpk").Value.ToString();
            }

            foreach (Operator op in oController.GetOperators())
            {
                if (!DeleteArea(ref szAreaName, op))
                {
                    lReturn = -2;
                }
            }

            return lReturn;
        }

        // communicate with AlertiX
        private bool CreateArea(ref Point[] pArea, ref AreaName szAreaName)
        {
            bool bReturn=true;
            AreaApi aArea = new AreaApi();
            Response aResponse = new Response();
            Polygon oPoly = new Polygon();

            oPoly.vertices = new Point[pArea.Length];
            for (int iCount = 0; iCount < pArea.Length; iCount++)
            {
                oPoly.vertices[iCount] = new Point();
                oPoly.vertices[iCount] = pArea[iCount];
            }
            // Insert operators to PAALERT_LBA
            InsertPAALERT_LBA(szAreaName.value);

            // Get operators
            foreach (Operator op in oController.GetOperators())
            {
                try
                {
                    aArea.Url = op.sz_url + oController.areaapi;

                    NetworkCredential objNetCredentials = new NetworkCredential(op.sz_user, op.sz_password);
                    Uri uri = new Uri(aArea.Url);
                    ICredentials objAuth = objNetCredentials.GetCredential(uri, "Basic");

                    //aArea.Timeout = 600000;
                    aArea.Timeout = System.Threading.Timeout.Infinite;

                    aArea.Credentials = objAuth;
                    aArea.PreAuthenticate = true;

                    oController.log.WriteLog(szAreaName.value + " (" + op.sz_operatorname + ") Creating area");

                    aResponse = aArea.createArea(oPoly, szAreaName);
                    if (aResponse.successful)
                    {
                        InsertPAALERT_LBA(long.Parse(szAreaName.value), op.l_operator, 0, long.Parse(szAreaName.value), aResponse.code);
                        bReturn = true;
                    }
                    else // failed, try update
                    {
                        oController.log.WriteLog(szAreaName.value + " (" + op.sz_operatorname + ") Create Area Failed (code=" + aResponse.code.ToString() + ") (msg=" + aResponse.message.ToString() + "), trying update.");
                        bReturn = UpdateArea(ref pArea, ref szAreaName, op.l_operator);
                    }
                }
                catch (Exception e)
                {
                    oController.log.WriteLog(szAreaName.value + " (" + op.sz_operatorname + ") Create Area Failed (exception): " + e.Message);
                    InsertPAALERT_LBA(long.Parse(szAreaName.value), op.l_operator, -2, -2, 0);
                    bReturn = false;
                }
            }
            return bReturn;
        }

        private bool UpdateArea(ref Point[] pArea, ref AreaName szAreaName)
        {
            bool bRet = true;

            foreach (Operator op in oController.GetOperators())
            {
                if (!UpdateArea(ref pArea, ref szAreaName, op.l_operator))
                    bRet = false;
            }

            return bRet;
        }

        private bool InsertPAALERT_LBA(string sz_areaname)
        {
            bool bRet = true;
            foreach (Operator op in oController.GetOperators())
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

        private bool UpdateArea(ref Point[] pArea, ref AreaName szAreaName, int lOperator)
        {
            bool bReturn=true;
            AreaApi aArea = new AreaApi();
            Response aResponse = new Response();
            Polygon oPoly = new Polygon();
            Operator op = oController.GetOperator(lOperator);

            aArea.Url = op.sz_url + oController.areaapi;
            
            NetworkCredential objNetCredentials = new NetworkCredential(op.sz_user, op.sz_password);
            Uri uri = new Uri(aArea.Url);
            ICredentials objAuth = objNetCredentials.GetCredential(uri, "Basic");

            aArea.Credentials = objAuth;
            aArea.PreAuthenticate = true;

            oPoly.vertices = new Point[pArea.Length];
            for (int iCount = 0; iCount < pArea.Length; iCount++)
            {
                oPoly.vertices[iCount] = new Point();
                oPoly.vertices[iCount] = pArea[iCount];
            }

            oController.log.WriteLog(szAreaName.value + " (" + op.sz_operatorname + ") Updating Area");
            try
            {
                aResponse = aArea.updateArea(szAreaName, oPoly);
                if (aResponse.successful)
                {
                    InsertPAALERT_LBA(long.Parse(szAreaName.value), lOperator, 0, long.Parse(szAreaName.value), aResponse.code);
                    bReturn = true;
                }
                else
                {
                    oController.log.WriteLog(szAreaName.value + " (" + op.sz_operatorname + ") Update Area Failed (code=" + aResponse.code.ToString() + ") (msg=" + aResponse.message.ToString() + ")");
                    InsertPAALERT_LBA(long.Parse(szAreaName.value), lOperator, -2, -2, aResponse.code); // may use aResponse.code on a later date
                    bReturn = false;
                }
            }
            catch (Exception e)
            {
                oController.log.WriteLog(szAreaName.value + " (" + op.sz_operatorname + ") Update Area Failed (exception): " + e.Message);
                InsertPAALERT_LBA(long.Parse(szAreaName.value), lOperator, -2, -2, 0);
                bReturn = false;
            }

            return bReturn;
        }

        private bool AreaExists(ref AreaName szAreaName, Operator op)
        {
            bool bReturn = true;
            AreaApi aArea = new AreaApi();
            GetAreaResponse aResponse = new GetAreaResponse();

            aArea.Url = op.sz_url + oController.areaapi;

            NetworkCredential objNetCredentials = new NetworkCredential(op.sz_user, op.sz_password);
            Uri uri = new Uri(aArea.Url);
            ICredentials objAuth = objNetCredentials.GetCredential(uri, "Basic");

            aArea.Credentials = objAuth;
            aArea.PreAuthenticate = true;

            try
            {
                aResponse = aArea.getArea(szAreaName);
                if (aResponse.successfulSpecified)
                {
                    if (aResponse.successful)
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
                    oController.log.WriteLog(szAreaName.value + " (" + op.sz_operatorname + ") AreaExists Failed: " + aResponse.message);
                    bReturn = false;
                }
            }
            catch (Exception e)
            {
                oController.log.WriteLog(szAreaName.value + " (" + op.sz_operatorname + ") AreaExists Failed (exception): " + e.Message);
                bReturn = false;
            }

            return bReturn;
        }

        private bool DeleteArea(ref AreaName szAreaName, Operator op)
        {
            bool bReturn=true;
            AreaApi aArea = new AreaApi();
            Response aResponse = new Response();

            aArea.Url = op.sz_url + oController.areaapi;

            NetworkCredential objNetCredentials = new NetworkCredential(op.sz_user, op.sz_password);
            Uri uri = new Uri(aArea.Url);
            ICredentials objAuth = objNetCredentials.GetCredential(uri, "Basic");

            aArea.Credentials = objAuth;
            aArea.PreAuthenticate = true;

            oController.log.WriteLog(szAreaName.value + " (" + op.sz_operatorname + ") Deleting Area");
            try
            {
                aResponse = aArea.deleteArea(szAreaName);
                if (aResponse.successfulSpecified)
                {
                    if (aResponse.successful)
                    {
                        bReturn = true;
                    }
                    else
                    {
                        oController.log.WriteLog(szAreaName.value + " (" + op.sz_operatorname + ") Delete Area Failed: " + aResponse.message);
                        bReturn = false;
                    }
                }
                else
                {
                    oController.log.WriteLog(szAreaName.value + " (" + op.sz_operatorname + ") Delete Area Failed: " + aResponse.message);
                    bReturn = false;
                }
            }
            catch (Exception e)
            {
                oController.log.WriteLog(szAreaName.value + " (" + op.sz_operatorname + ") Delete Area Failed (exception): " + e.Message);
                bReturn = false;
            }

            return bReturn;
        }

        // Delete an area based on string containing the name
        public bool DeleteArea(string sz_areaname, Operator op)
        {
            AreaName areaname = new AreaName();
            areaname.value = sz_areaname;
            
            return DeleteArea(ref areaname, op);
        }

        // Check if an area exists using a string containing the name
        public bool AreaExists(string sz_areaname, Operator op)
        {
            AreaName areaname = new AreaName();
            areaname.value = sz_areaname;

            return AreaExists(ref areaname, op);
        }
    }
}
