using System;
using System.Data;
using System.Configuration;
using System.Linq;
using System.Web;
using System.Web.Security;
using System.Web.UI;
using System.Web.UI.HtmlControls;
using System.Web.UI.WebControls;
using System.Web.UI.WebControls.WebParts;
using System.Xml.Linq;
using System.Data.Odbc;
using com.ums.PAS.Address;
using com.ums.UmsDbLib;
using com.ums.UmsCommon;
using System.Collections.Generic;


namespace com.ums.PAS.Database
{
    public class UAdrDb : UmsDb
    {
        /*
         * Connect to "vb_adr_" + sz_stdcc depending on which database to access
         */
        public UAdrDb(UmsDbConnParams conn, String sz_stdcc) 
            : base(conn.sz_dsn + sz_stdcc, conn.sz_uid, conn.sz_pwd)
        {

        }

        public UAddressList GetAddresslistByQuality(UMapAddressParams param)
        {
            UAddressList list = new UAddressList();
            return list;
        }

        public int GetGisImport(ref List<UGisImportResultLine> p, int startat, int max, ref int next)
        {
            String szSQL = "";
            int i = 0;
            int n_validcount = 0;
            for (i = startat; i < p.Count; i++)
            {
                if (p[i].isValid())
                {
                    if (n_validcount>0) //there are more sql before this one, add union
                        szSQL += " UNION ";
                    szSQL += String.Format(UCommon.UGlobalizationInfo, "SELECT isnull(KON_DMID, 0) KON_DMID, isnull(NAVN, ' '), isnull(ADRESSE, ' '), isnull(HUSNR, 0) HUSNR, isnull(OPPGANG, ' ') OPPGANG, isnull(POSTNR, '0'), isnull(POSTSTED, ''), isnull(KOMMUNENR, 0) KOMMUNENR, isnull(FØDTÅR, '0'), isnull(TELEFON, ''), isnull(LON, 0) LON, isnull(LAT, 0) LAT, isnull(GNR, 0) GNR, isnull(BNR, 0) BNR, isnull(BEDRIFT, 0) BEDRIFT, isnull(l_importid, -1) l_importid, " +
                                             "isnull(MOBIL, ''), isnull(GATEKODE, 0) GATEKODE, isnull(XY_KODE, 'a') AS QUALITY, isnull(f_hasfixed, 0), isnull(f_hasmobile,0), arr_indexnumber={3} FROM " +
                                             "ADR_KONSUM WITH (INDEX (idx_kommunegatenr)) WHERE KOMMUNENR={0} AND GATEKODE={1} AND HUSNR={2} AND BEDRIFT IN (0,1)",
                                             p[i].municipalid, p[i].streetid, p[i].houseno, p[i].n_linenumber - 1);
                    if (p[i].letter.Trim().Length > 0)
                    {
                        szSQL += " AND OPPGANG='" + p[i].letter.Trim() + "'";
                    }
                    n_validcount++;
                    if (n_validcount >= max)
                        break;
                }
            }
            if (n_validcount > 0)
            {
                try
                {
                    OdbcDataReader rs;
                    m_cmd = new OdbcCommand(szSQL, conn);
                    rs = m_cmd.ExecuteReader();
                    while (rs.Read())
                    {
                        UAddress adr = new UAddress();
                        readAddressFromDb(ref adr, ref rs);
                        if (adr.arrayindex >= 0)
                        {
                            p[adr.arrayindex].list.addLine(ref adr);
                            //p[adr.arrayindex].list.finalize();
                        }
                    }
                    rs.Close();
                }
                catch (Exception err)
                {
                    next = startat;
                    throw err;
                }
            }
            /*if (i >= p.Count)
            {
                return -1;
            }*/
            //return (i+1); //return next
            next = (i + 1);
            return i-startat+1;
        }

        public int GetGisImport(ref UGisImportResultLine p)
        {
            String szSQL = String.Format(UCommon.UGlobalizationInfo, "SELECT isnull(KON_DMID, 0) KON_DMID, isnull(NAVN, ' '), isnull(ADRESSE, ' '), isnull(HUSNR, 0) HUSNR, isnull(OPPGANG, ' ') OPPGANG, isnull(POSTNR, '0'), isnull(POSTSTED, ''), isnull(KOMMUNENR, 0) KOMMUNENR, isnull(FØDTÅR, '0'), isnull(TELEFON, ''), isnull(LON, 0) LON, isnull(LAT, 0) LAT, isnull(GNR, 0) GNR, isnull(BNR, 0) BNR, isnull(BEDRIFT, 0) BEDRIFT, isnull(l_importid, -1) l_importid, " +
                                         "isnull(MOBIL, ''), isnull(GATEKODE, 0) GATEKODE, isnull(XY_KODE, 'a') AS QUALITY, isnull(f_hasfixed, 0), isnull(f_hasmobile,0), arr_indexnumber=0 FROM " +
                                         "ADR_KONSUM WITH (INDEX (idx_kommunegatenr)) WHERE KOMMUNENR={0} AND GATEKODE={1} AND HUSNR={2} AND BEDRIFT IN (0,1)",
                                         p.municipalid, p.streetid, p.houseno);
            if (p.letter.Trim().Length > 0)
            {
                szSQL += " AND OPPGANG='" + p.letter.Trim() + "'";
            }
            try
            {
                
                //rs = base.ExecReader(szSQL, UmsDb.UREADER_AUTOCLOSE);
                OdbcDataReader rs;
                m_cmd = new OdbcCommand(szSQL, conn);
                rs = m_cmd.ExecuteReader();
                while (rs.Read())
                {
                    UAddress adr = new UAddress();
                    readAddressFromDb(ref adr, ref rs);
                    p.list.addLine(ref adr);

                }
                rs.Close();
            }
            catch (Exception e)
            {
                ULog.error(0, "Error in retrieving GisImport addresses", e.Message);
                throw e;
            }


            return 0;
        }

        public UAddressList FindAddressesByDistance(UMapDistanceParams param)
        {
            /*
             * delta_y = abs(meters)/EARTH_CIRCUMFERENCE
             * delta_x = abs(meters)*cos(param.lat)/EARTH_CIRCUMFERENCE
             * candidates = LON>=param.lat - delta_y, LON<=param.lat + delta_y, LAT>=param.lon - delta_x, LAT<=param.lon + delta_x
             */
            double delta_y = Math.Abs(param.distance) / UCommon.UEARTH_CIRCUMFERENCE;
            double delta_x = Math.Abs(param.distance) / Math.Cos(UCommon.UDEG2RAD(param.lat)) / UCommon.UEARTH_CIRCUMFERENCE;
            UMapAddressParams adr = new UMapAddressParams();
            adr.l_bo = (float)(param.lon - delta_x);
            adr.r_bo = (float)(param.lon + delta_x);
            adr.u_bo = (float)(param.lat + delta_y);
            adr.b_bo = (float)(param.lat - delta_y);
            return GetAddresslist(adr);
        }
        public UAddressList GetAddresslist(UMapAddressParams param)
        {
            UAddressList list = new UAddressList();
/*
sprintf(szSQL,  "SELECT isnull(KON_DMID, 0) KON_DMID, NAVN, ADRESSE, isnull(HUSNR, 0) HUSNR, isnull(OPPGANG, ' ') OPPGANG, POSTNR, POSTSTED, isnull(KOMMUNENR, 0) KOMMUNENR, FØDTÅR, TELEFON, isnull(LON, 0) LON, isnull(LAT, 0) LAT, isnull(GNR, 0) GNR, isnull(BNR, 0) BNR, isnull(BEDRIFT, 0) BEDRIFT, isnull(l_importid, -1) l_importid, MOBIL, isnull(GATEKODE, 0) GATEKODE, isnull(XY_KODE, 'a') AS QUALITY, f_hasfixed, f_hasmobile FROM "
    "ADR_KONSUM WHERE %s AND BEDRIFT IN (0,1)", sz_filter1);
*/
            String szSQL = String.Format(UCommon.UGlobalizationInfo, "SELECT isnull(KON_DMID, 0) KON_DMID, isnull(NAVN, ' '), isnull(ADRESSE, ' '), isnull(HUSNR, 0) HUSNR, isnull(OPPGANG, ' ') OPPGANG, isnull(POSTNR, '0'), isnull(POSTSTED, ''), isnull(KOMMUNENR, 0) KOMMUNENR, isnull(FØDTÅR, '0'), isnull(TELEFON, ''), isnull(LON, 0) LON, isnull(LAT, 0) LAT, isnull(GNR, 0) GNR, isnull(BNR, 0) BNR, isnull(BEDRIFT, 0) BEDRIFT, isnull(l_importid, -1) l_importid, " +
                                         "isnull(MOBIL, ''), isnull(GATEKODE, 0) GATEKODE, isnull(XY_KODE, 'a') AS QUALITY, isnull(f_hasfixed, 0), isnull(f_hasmobile,0), arr_indexnumber=0 FROM " +
                                         "ADR_KONSUM WHERE LON>={0} AND LON<={1} AND LAT>={2} AND LAT<={3} AND BEDRIFT IN (0,1)",
                                         param.b_bo, param.u_bo, param.l_bo, param.r_bo);
            OdbcDataReader rs;
            try
            {
                rs = base.ExecReader(szSQL, UmsDb.UREADER_AUTOCLOSE);
            }
            catch(Exception e)
            {
                ULog.error(0, "Error in retrieving addresses", e.Message);
                throw e;
            }
                while (rs.Read())
                {
                    UAddress adr = new UAddress();
                    readAddressFromDb(ref adr, ref rs);
                    list.addLine(ref adr);

                    /*try
                    {
                        adr.kondmid = rs.GetString(0);
                    }
                    catch(Exception)
                    {
                        adr.kondmid = "0";
                    }
                    try
                    {
                        adr.name = rs.GetString(1);
                    }
                    catch(Exception)
                    {
                        adr.name = "";
                    }
                    try
                    {
                        adr.address = rs.GetString(2);
                    }
                    catch(Exception)
                    {
                        adr.address = "";
                    }
                    try
                    {
                        adr.houseno = rs.GetInt32(3);
                    }
                    catch(Exception)
                    {
                        adr.houseno = 0;
                    }
                    try
                    {
                        adr.letter = rs.GetString(4);
                    }
                    catch(Exception)
                    {
                        adr.letter = "";
                    }
                    try
                    {
                        adr.postno = rs.GetString(5);
                    }
                    catch(Exception)
                    {
                        adr.postno = "";
                    }
                    try
                    {
                        adr.postarea = rs.GetString(6);
                    }
                    catch(Exception)
                    {
                        adr.postarea = "";
                    }
                    try
                    {
                        adr.region = rs.GetInt32(7);
                    }
                    catch(Exception)
                    {
                        adr.region = 0;
                    }
                    try
                    {
                        adr.bday = rs.GetString(8);
                    }
                    catch(Exception)
                    {
                        adr.bday = "0";
                    }
                    //adr.bday = 0;
                    try
                    {
                        adr.number = rs.GetString(9);
                    }
                    catch(Exception)
                    {
                        adr.number = "";
                    }
                    try
                    {
                        adr.lon = rs.GetDouble(10);
                    }
                    catch(Exception)
                    {
                        adr.lon = 0;
                    }
                    try
                    {
                        adr.lat = rs.GetDouble(11);
                    }
                    catch(Exception)
                    {
                        adr.lat  = 0;
                    }
                    try
                    {
                        adr.gno = rs.GetInt32(12);
                    }
                    catch(Exception)
                    {
                        adr.gno = 0;
                    }
                    try
                    {
                        adr.bno = rs.GetInt32(13);
                    }
                    catch(Exception)
                    {
                        adr.bno = 0;
                    }
                    try
                    {
                        adr.bedrift = rs.GetInt32(14);
                    }
                    catch(Exception)
                    {
                        adr.bedrift = 0;
                    }
                    try
                    {
                        adr.importid = rs.GetInt32(15);
                    }
                    catch(Exception)
                    {
                        adr.importid = -1;
                    }
                    try
                    {
                        adr.mobile = rs.GetString(16);
                    }
                    catch(Exception)
                    {
                        adr.mobile = "";
                    }
                    try
                    {
                        adr.streetid = rs.GetInt32(17);
                    }
                    catch(Exception)
                    {
                        adr.streetid = 0;
                    }
                    try
                    {
                        adr.xycode = rs.GetString(18);
                    }
                    catch(Exception)
                    {
                        adr.xycode = "a";
                    }
                    try
                    {
                        adr.hasfixed = rs.GetInt16(19);
                    }
                    catch(Exception)
                    {
                        adr.hasfixed = 0;
                    }
                    try
                    {
                        adr.hasmobile = rs.GetInt16(20);
                    }
                    catch(Exception)
                    {
                        adr.hasmobile = 0;
                    }

                        list.addLine(ref adr);*/
                    /*}
                    catch (Exception e)
                    {
                        //ULog.warning(-1, "Error in address", e.Message);
                    }*/

                }
            /*}
            catch (Exception e)
            {

            }*/
            list.finalize();

            return list;
        }
        protected bool readAddressFromDb(ref UAddress adr, ref OdbcDataReader rs)
        {
            try
            {
                adr.kondmid = rs.GetString(0);
            }
            catch (Exception)
            {
                adr.kondmid = "0";
            }
            try
            {
                adr.name = rs.GetString(1);
            }
            catch (Exception)
            {
                adr.name = "";
            }
            try
            {
                adr.address = rs.GetString(2);
            }
            catch (Exception)
            {
                adr.address = "";
            }
            try
            {
                adr.houseno = rs.GetInt32(3);
            }
            catch (Exception)
            {
                adr.houseno = 0;
            }
            try
            {
                adr.letter = rs.GetString(4);
            }
            catch (Exception)
            {
                adr.letter = "";
            }
            try
            {
                adr.postno = rs.GetString(5);
            }
            catch (Exception)
            {
                adr.postno = "";
            }
            try
            {
                adr.postarea = rs.GetString(6);
            }
            catch (Exception)
            {
                adr.postarea = "";
            }
            try
            {
                adr.region = rs.GetInt32(7);
            }
            catch (Exception)
            {
                adr.region = 0;
            }
            try
            {
                adr.bday = rs.GetString(8);
            }
            catch (Exception)
            {
                adr.bday = "0";
            }
            //adr.bday = 0;
            try
            {
                adr.number = rs.GetString(9);
            }
            catch (Exception)
            {
                adr.number = "";
            }
            try
            {
                adr.lon = rs.GetDouble(10);
            }
            catch (Exception)
            {
                adr.lon = 0;
            }
            try
            {
                adr.lat = rs.GetDouble(11);
            }
            catch (Exception)
            {
                adr.lat = 0;
            }
            try
            {
                adr.gno = rs.GetInt32(12);
            }
            catch (Exception)
            {
                adr.gno = 0;
            }
            try
            {
                adr.bno = rs.GetInt32(13);
            }
            catch (Exception)
            {
                adr.bno = 0;
            }
            try
            {
                adr.bedrift = rs.GetInt32(14);
            }
            catch (Exception)
            {
                adr.bedrift = 0;
            }
            try
            {
                adr.importid = rs.GetInt32(15);
            }
            catch (Exception)
            {
                adr.importid = -1;
            }
            try
            {
                adr.mobile = rs.GetString(16);
            }
            catch (Exception)
            {
                adr.mobile = "";
            }
            try
            {
                adr.streetid = rs.GetInt32(17);
            }
            catch (Exception)
            {
                adr.streetid = 0;
            }
            try
            {
                adr.xycode = rs.GetString(18);
            }
            catch (Exception)
            {
                adr.xycode = "a";
            }
            try
            {
                adr.hasfixed = rs.GetInt16(19);
            }
            catch (Exception)
            {
                adr.hasfixed = 0;
            }
            try
            {
                adr.hasmobile = rs.GetInt16(20);
            }
            catch (Exception)
            {
                adr.hasmobile = 0;
            }
            try
            {
                adr.arrayindex = rs.GetInt32(21);
            }
            catch (Exception)
            {
                adr.arrayindex = -1;
            }
            return true;
        }
    }
}
