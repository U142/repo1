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
            oController.log.WriteLog(szAreaName.value + " Delete Area");
            if (!DeleteArea(ref szAreaName))
            {
                lReturn = -2;
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

            NetworkCredential objNetCredentials = new NetworkCredential(oController.wsuser, oController.wspass); //("jone", "jone");
            Uri uri = new Uri(aArea.Url);
            ICredentials objAuth = objNetCredentials.GetCredential(uri, "Basic");

            aArea.Url = oController.areaapi; // "http://lbv.netcom.no:8080/alertix/AreaApi";
            aArea.Credentials = objAuth;
            aArea.PreAuthenticate = true;

            oPoly.vertices = new Point[pArea.Length];
            for (int iCount = 0; iCount < pArea.Length; iCount++)
            {
                oPoly.vertices[iCount] = new Point();
                oPoly.vertices[iCount] = pArea[iCount];
            }

            try
            {
                aResponse = aArea.createArea(oPoly, szAreaName);
                if (aResponse.successful)
                {
                    oController.ExecDB("UPDATE PAALERT SET sz_areaid='" + szAreaName.value + "', l_timestamp=" + DateTime.Now.ToString("yyyyMMddHHmmss") + " WHERE l_alertpk=" + szAreaName.value, oController.dsn);
                    bReturn = true;
                }
                else if (aResponse.code == 1020)
                {
                    oController.log.WriteLog(szAreaName.value + " Area already exist, trying update.");
                    bReturn = UpdateArea(ref pArea, ref szAreaName);
                }
                else
                {
                    oController.log.WriteLog(szAreaName.value + " Create Area Failed (code=" + aResponse.code.ToString() + ") (msg=" + aResponse.message.ToString() + ")");
                    oController.ExecDB("UPDATE PAALERT SET sz_areaid='-2', l_timestamp=" + DateTime.Now.ToString("yyyyMMddHHmmss") + " WHERE l_alertpk=" + szAreaName.value, oController.dsn);
                    bReturn = false;
                }
            }
            catch (Exception e)
            {
                oController.log.WriteLog(szAreaName.value + " Create Area Failed (exception): " + e.Message);
                oController.ExecDB("UPDATE PAALERT SET sz_areaid='-2', l_timestamp=" + DateTime.Now.ToString("yyyyMMddHHmmss") + " WHERE l_alertpk=" + szAreaName.value, oController.dsn);
                bReturn = false;
            }

            return bReturn;
        }

        private bool UpdateArea(ref Point[] pArea, ref AreaName szAreaName)
        {
            bool bReturn=true;
            AreaApi aArea = new AreaApi();
            Response aResponse = new Response();
            Polygon oPoly = new Polygon();

            NetworkCredential objNetCredentials = new NetworkCredential(oController.wsuser, oController.wspass); //("jone", "jone");
            Uri uri = new Uri(aArea.Url);
            ICredentials objAuth = objNetCredentials.GetCredential(uri, "Basic");

            aArea.Url = oController.areaapi; // "http://lbv.netcom.no:8080/alertix/AreaApi";
            aArea.Credentials = objAuth;
            aArea.PreAuthenticate = true;

            oPoly.vertices = new Point[pArea.Length];
            for (int iCount = 0; iCount < pArea.Length; iCount++)
            {
                oPoly.vertices[iCount] = new Point();
                oPoly.vertices[iCount] = pArea[iCount];
            }

            try
            {
                aResponse = aArea.updateArea(szAreaName, oPoly);
                if (aResponse.successful)
                {
                    oController.ExecDB("UPDATE PAALERT SET sz_areaid='" + szAreaName.value + "', l_timestamp=" + DateTime.Now.ToString("yyyyMMddHHmmss") + " WHERE l_alertpk=" + szAreaName.value, oController.dsn);
                    bReturn = true;
                }
                else
                {
                    oController.log.WriteLog(szAreaName.value + " Update Area Failed (code=" + aResponse.code.ToString() + ") (msg=" + aResponse.message.ToString() + ")");
                    oController.ExecDB("UPDATE PAALERT SET sz_areaid='-2', l_timestamp=" + DateTime.Now.ToString("yyyyMMddHHmmss") + " WHERE l_alertpk=" + szAreaName.value, oController.dsn);
                    bReturn = false;
                }
            }
            catch (Exception e)
            {
                oController.log.WriteLog(szAreaName.value + " Update Area Failed (exception): " + e.Message);
                oController.ExecDB("UPDATE PAALERT SET sz_areaid='-2', l_timestamp=" + DateTime.Now.ToString("yyyyMMddHHmmss") + " WHERE l_alertpk=" + szAreaName.value, oController.dsn);
                bReturn = false;
            }

            return bReturn;
        }

        private bool DeleteArea(ref AreaName szAreaName)
        {
            bool bReturn=true;
            AreaApi aArea = new AreaApi();
            Response aResponse = new Response();

            NetworkCredential objNetCredentials = new NetworkCredential(oController.wsuser, oController.wspass); //("jone", "jone");
            Uri uri = new Uri(aArea.Url);
            ICredentials objAuth = objNetCredentials.GetCredential(uri, "Basic");

            aArea.Url = oController.areaapi; // "http://lbv.netcom.no:8080/alertix/AreaApi";
            aArea.Credentials = objAuth;
            aArea.PreAuthenticate = true;

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
                        oController.log.WriteLog(szAreaName.value + " Delete Area Failed: " + aResponse.message);
                        bReturn = false;
                    }
                }
                else
                {
                    oController.log.WriteLog(szAreaName.value + " Delete Area Failed: " + aResponse.message);
                    bReturn = false;
                }
            }
            catch (Exception e)
            {
                oController.log.WriteLog(szAreaName.value + " Delete Area Failed (exception): " + e.Message);
                bReturn = false;
            }

            return bReturn;
        }
    }
}
