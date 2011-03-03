using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Services;
using System.Xml.Serialization;
using com.ums.UmsCommon;
using com.ums.UmsParm;
using com.ums.address;
using com.ums.PAS.Address;
using com.ums.wsPASExec;


namespace com.ums.ws.addressdatabase
{
    /// <summary>
    /// Summary description for AddressDatabase
    /// </summary>
    [WebService(Namespace = "http://ums.no/ws/addressdatabase/")]
    [WebServiceBinding(ConformsTo = WsiProfiles.BasicProfile1_1)]
    [System.ComponentModel.ToolboxItem(false)]
    // To allow this Web Service to be called from script, using ASP.NET AJAX, uncomment the following line. 
    // [System.Web.Script.Services.ScriptService]
    public class AddressDatabase : System.Web.Services.WebService
    {
        [XmlInclude(typeof(UPolygon))]
        [XmlInclude(typeof(UEllipse))]
        [XmlInclude(typeof(UMunicipalDef))]
        [XmlInclude(typeof(PersonInfo))]


        [WebMethod]
        public List<AddressInfo> GetAddressesById(List<string> IDLIST)
        {
            var returnList = Global.AdrIndex.FindById(IDLIST);
            return returnList;
        }

        [WebMethod]
        public List<AddressInfo> GetAddressesByPolygon(UPolygon p)
        {
            var returnList = new List<AddressInfo>();
            UBoundingRect bounds = p.CalcBounds();
            var addressInfos = Global.AdrIndex.FindInArea(bounds._left, bounds._top, bounds._right, bounds._bottom, 500000);
            foreach (var address in addressInfos)
            {
                if (p.PointInside(address.lng, address.lat))
                {
                    returnList.Add(address);
                }
            }
            return returnList;
        }

        [WebMethod]
        public List<AddressInfo> GetAddressesByEllipse(UEllipseDef e)
        {
            var returnList = new List<AddressInfo>();
            UMapBounds bounds = e.CalcBounds();
            var addressInfos = com.ums.wsPASExec.Global.AdrIndex.FindInArea(bounds.l_bo, bounds.u_bo, bounds.r_bo, bounds.b_bo, 500000);
            foreach (var address in addressInfos)
            {
                if (Math.Pow(address.lat - e.center.lat, 2) / Math.Pow(e.radius.lat, 2) + Math.Pow(address.lng - e.center.lon, 2) / Math.Pow(e.radius.lon, 2) < 1)
                {
                    returnList.Add(address);
                }
            }
            return returnList;
        }

        [WebMethod]
        public List<AddressInfo> GetAddressesByMunicipal(List<UMunicipalDef> m)
        {
            var returnList = new List<AddressInfo>();
            foreach (UMunicipalDef municipal in m)
            {
                if (municipal.sz_municipalid.Length <= 0)
                    continue;
                var addressInfos = com.ums.wsPASExec.Global.AdrIndex.FindInMunicipal(municipal);
                foreach (var address in addressInfos)
                {
                    returnList.Add(address);
                }
            }

            return returnList;
        }

        [WebMethod]
        public List<AddressInfo> Test()
        {
            //List<string> test = new List<string>();
            //test.Add("4DFF4EA340F0A823F15D3F4F01AB62EAE0E5DA579CCB851F8DB9DFE84C58B2B37B89903A740E1EE172DA793A6E79D560E5F7F9BD058A12A280433ED6FA46510A");
            //return GetAddressesById(test);

            /*UEllipseDef e = new UEllipseDef();
            e.center.lon = 10.59423;
            e.center.lat = 59.90865;
            e.radius.lon = 0.00001;
            e.radius.lat = 0.00001;
            return GetAddressesByEllipse(e);*/

            /*UMunicipalDef m = new UMunicipalDef();
            m.sz_municipalid = "1145";
            List<UMunicipalDef> list = new List<UMunicipalDef>();
            list.Add(m);
            var tmp = GetAddressesByMunicipal(list);
            return tmp;*/

            UPolygon p = new UPolygon();
            p.addPoint(10.59408, 59.90868);
            p.addPoint(10.59426, 59.90849);
            p.addPoint(10.59445, 59.90865);
            return GetAddressesByPolygon(p);
        }
    }
}
