using System;
using System.Collections.Generic;
using System.Collections;
using System.Web;
using System.Data.Odbc;
using System.IO;
using System.Linq;

namespace ums.ws.tcs
{
    public static class Database
    {
        public static string conn_login;
        public static string conn_whitelist;
        public static int text_size;

        // 6 = TCS, 5 = TAS, 4 = PAS
        private static int MESSAGETYPE = umssettings._settings.GetValue("MessageType", 6);

        // Common
        public static ERROR ValidateUser(Account acc, UserValues uv)
        {
            ERROR ret = ERROR.success; // ok

            try
            {
                uv.clientstring = String.Format("client {0}/{1} from {2}", acc.company.ToUpper(), acc.department.ToUpper(), HttpContext.Current.Request.UserHostAddress);

                string sql = String.Format(@"SELECT
	                                            BD.l_comppk,
	                                            BD.l_deptpk,
                                                isnull(BD.l_pas, 0) l_pas,
                                                isnull(BD.l_pas_send, 0) l_pas_send,
                                                convert(int,isnull(sz_stdcc,'0047')) l_stdcc
                                            FROM
	                                            v_BBDEPARTMENT BD,
	                                            BBCOMPANY BC
                                            WHERE
	                                            upper(BC.sz_compid)=?
	                                            AND	upper(BD.sz_deptid)=?
	                                            AND BD.sz_password=?
	                                            AND BD.l_comppk=BC.l_comppk");

                OdbcConnection conn = new OdbcConnection(conn_login);
                OdbcCommand cmd = new OdbcCommand(sql, conn);

                cmd.Parameters.Add("sz_compid", OdbcType.VarChar).Value = acc.company.ToUpper();
                cmd.Parameters.Add("sz_deptid", OdbcType.VarChar).Value = acc.department.ToUpper();
                cmd.Parameters.Add("sz_password", OdbcType.VarChar).Value = acc.password;

                conn.Open();
                OdbcDataReader rs = cmd.ExecuteReader();
                if (rs.Read())
                {
                    uv.company = rs.GetInt32(0);
                    uv.department = rs.GetInt32(1);
                    int l_pas = rs.GetInt32(2);
                    int l_pas_send = rs.GetInt32(3);
                    int l_stdcc = rs.GetInt32(4);

                    uv.f_simulate = ((1 & l_pas_send) == 1);
                    uv.f_live = ((2 & l_pas_send) == 2);

                    if (l_stdcc != 0) // if 0, use default class value (47)
                        uv.stdcc = l_stdcc;

                    uv.operators = Database.GetOperators(uv);
                    if (uv.operators.Count == 0 || ((4 & l_pas) != 4))
                        ret = ERROR.sending_accessdenied; // no TCS access
                }
                else
                {
                    ret = ERROR.invaliduser; // incorrect user
                }

                rs.Close();
                conn.Close();

            }
            catch (SoapException)
            {
                throw;
            }
            catch (Exception e)
            {
                throw new SoapException(e.Message, SoapException.ServerFaultCode);
            }

            return ret;
        }
        public static bool ValidateReference(UserValues uv, int reference)
        {
            bool ret = true; // ok

            try
            {
                string sql = String.Format(@"SELECT l_deptpk FROM MDVSENDINGINFO WHERE l_deptpk=? AND l_refno=?");

                OdbcConnection conn = new OdbcConnection(conn_login);
                OdbcCommand cmd = new OdbcCommand(sql, conn);

                cmd.Parameters.Add("l_deptpk", OdbcType.Int).Value = uv.department;
                cmd.Parameters.Add("l_refno", OdbcType.Int).Value = reference;

                conn.Open();
                OdbcDataReader rs = cmd.ExecuteReader();
                if (rs.Read())
                {
                    if (rs.GetInt32(0) != uv.department)
                        ret = false;
                }
                else
                {
                    ret = false; // incorrect user
                }

                rs.Close();
                conn.Close();
            }
            catch (SoapException)
            {
                throw;
            }
            catch (Exception e)
            {
                throw new SoapException(e.Message, SoapException.ServerFaultCode);
            }

            return ret;
        }
        private static SendingStatus GetSendingStatus(int status)
        {
            switch (status)
            {
                case 100:
                case 199:
                    return SendingStatus.QUEUED;
                case 200:
                case 290:
                case 400:
                case 405:
                    return SendingStatus.PROCESSING;
                case 410:
                case 411:
                    return SendingStatus.PREPARED;
                case 420:
                    return SendingStatus.CONFIRMED_BY_USER;
                case 430:
                    return SendingStatus.CANCELLED_BY_USER;
                case 440:
                    return SendingStatus.SENDING;
                case 800:
                    return SendingStatus.CANCELLING;
                case 1000:
                    return SendingStatus.FINISHED;
                case 2000:
                case 2001:
                case 2002:
                    return SendingStatus.CANCELLED;
                case 42001:
                case 42002:
                case 42003:
                case 42004:
                case 42005:
                case 42006:
                case 42007:
                case 42008:
                case 42009:
                case 42010:
                case 42011:
                case 42012:
                case 42013:
                case 42014:
                case 42015:
                case 42016:
                case 42017:
                case 42018:
                case 42019:
                case 42020:
                case 42101:
                case 42102:
                case 42103:
                case 42104:
                case 42105:
                case 42106:
                case 42201:
                    return SendingStatus.ERROR;
                default:
                    return SendingStatus.UNKNOWN;
            }
        }
        private static string GetCCIso(int cc)
        {
            string ret = "";
            string sql = "SELECT sz_iso FROM LBACOUNTRIES WHERE l_cc=?";
            
            try
            {
                OdbcConnection conn = new OdbcConnection(conn_login);
                OdbcCommand cmd = new OdbcCommand(sql, conn);

                cmd.Parameters.Add("l_cc", OdbcType.Int).Value = cc;

                conn.Open();
                ret = (string)cmd.ExecuteScalar();
                conn.Close();
            }
            catch (SoapException)
            {
                throw;
            }
            catch (Exception e)
            {
                throw new SoapException(e.Message, SoapException.ServerFaultCode);
            }

            return ret;
        }

        // Whitelists
        public static void SaveListContents(UserValues uv, Decimal listid, List<string> numbers)
        {
            try
            {
                string sql = String.Format("UPDATE WHITELISTS SET l_size=?, sz_content=? WHERE l_whitelistid=?");
                string xml_numbers = Number.SerializeList(numbers);

                OdbcConnection conn = new OdbcConnection(conn_whitelist);
                OdbcCommand cmd = new OdbcCommand(sql, conn);

                cmd.Parameters.Add("l_size", OdbcType.Int).Value = numbers.Count;
                cmd.Parameters.Add("sz_content", OdbcType.Text).Value = xml_numbers;
                cmd.Parameters.Add("l_whitelistid", OdbcType.Decimal).Value = listid;

                conn.Open();
                cmd.ExecuteNonQuery();
                conn.Close();
            }
            catch (SoapException)
            {
                throw;
            }
            catch (Exception e)
            {
                throw new SoapException(e.Message, SoapException.ServerFaultCode);
            }
        }
        public static void SetListState(UserValues uv, Decimal listid, QueueState state)
        {
            string sql = "";
            try
            {
                OdbcConnection conn = new OdbcConnection(conn_whitelist);
                OdbcCommand cmd = new OdbcCommand(sql, conn);

                conn.Open();

                sql = String.Format("UPDATE WHITELISTS SET l_state={0} WHERE l_whitelistid={1}"
                    , (int)state
                    , listid);
                cmd.CommandText = sql;
                cmd.ExecuteNonQuery();

                conn.Close();
            }
            catch (SoapException)
            {
                throw;
            }
            catch (Exception e)
            {
                throw new SoapException(e.Message, SoapException.ServerFaultCode);
            }
        }
        public static List<string> GetListContents(Decimal listid)
        {
            List<string> ret = new List<string>();

            try
            {
                string sql_textsize = String.Format("set textsize {0}", text_size);
                string sql = String.Format("SELECT sz_content FROM WHITELISTS WHERE l_whitelistid=?");

                OdbcConnection conn = new OdbcConnection(conn_whitelist);
                OdbcCommand cmd = new OdbcCommand();

                cmd.Connection = conn;
                cmd.Parameters.Add("l_whitelistid", OdbcType.Decimal).Value = listid;

                conn.Open();

                // set textsize
                cmd.CommandText = sql_textsize;
                cmd.ExecuteNonQuery();

                // get xml
                cmd.CommandText = sql;
                OdbcDataReader rs = cmd.ExecuteReader();
                if (rs.Read())
                {
                    if (!rs.IsDBNull(0))
                    {
                        string s = rs.GetString(0);

                        ret = Number.DeserializeList(s);
                    }
                }
                conn.Close();
            }
            catch (SoapException)
            {
                throw;
            }
            catch (Exception e)
            {
                throw new SoapException(e.Message, SoapException.ServerFaultCode);
            }

            return ret;
        }

        public static WhitelistInfo GetListInfo(string listname, int department, int company)
        {
            WhitelistInfo ret = new WhitelistInfo();
            string sql = String.Format("sp_tcs_get_whitelistbyname ?,?,?");

            try
            {
                OdbcConnection conn = new OdbcConnection(conn_whitelist);
                OdbcCommand cmd = new OdbcCommand(sql, conn);

                cmd.Parameters.Add("sz_name", OdbcType.VarChar, 50).Value = listname;
                cmd.Parameters.Add("l_deptpk", OdbcType.Int).Value = department;
                cmd.Parameters.Add("l_comppk", OdbcType.Int).Value = company;

                conn.Open();
                OdbcDataReader rs = cmd.ExecuteReader();
                if (rs.Read())
                {
                    if (!rs.IsDBNull(0)) 
                        ret.id = rs.GetDecimal(0);
                    if(!rs.IsDBNull(3)) 
                        ret.size = rs.GetInt32(3);
                    if(!rs.IsDBNull(4)) 
                        ret.state = (QueueState)rs.GetInt32(4);
                    if(!rs.IsDBNull(6)) 
                        ret.name = rs.GetString(6);
                }
                rs.Close();
                conn.Close();
            }
            catch (SoapException)
            {
                throw;
            }
            catch (Exception e)
            {
                throw new SoapException(e.Message, SoapException.ServerFaultCode);
            }

            return ret;
        }

        public static ERROR CreateWhitelist(UserValues uv, string name, ref Decimal listid)
        {
            ERROR ret = ERROR.success;

            try
            {
                //@l_deptpk int, @l_comppk int, @l_size int, @l_state int, @l_timestamp numeric(18,0),@sz_name varchar(50)
                string sql = String.Format("sp_tcs_new_whitelist {0},{1},0,{2},{3},?"
                    , uv.department
                    , uv.company
                    , (int)QueueState.QUEUED
                    , DateTime.Now.ToString("yyyyMMddHHmmss"));

                OdbcConnection conn = new OdbcConnection(conn_whitelist);
                OdbcCommand cmd = new OdbcCommand(sql, conn);

                // add name as parameter to avoid abuse of strings in SQL
                cmd.Parameters.Add("sz_name", OdbcType.VarChar, 50).Value = name;

                conn.Open();
                object pk = cmd.ExecuteScalar();

                if (pk.GetType() != typeof(decimal))
                {
                    if (pk.GetType() == typeof(int)) // some error
                    {
                        if ((int)pk == -1)
                        {
                            ret = ERROR.whitelist_duplicate;
                        }
                        else
                        {
                            ret = ERROR.unknown;
                        }
                    }
                }
                else // ok
                {
                    listid = (decimal)pk;
                    foreach (Operator op in uv.operators)
                    {
                        sql = String.Format("INSERT INTO WHITELISTS_X_OPERATOR(l_whitelistid, l_operator, l_status) VALUES({0}, {1}, 0)"
                            , listid
                            , op.l_operator);

                        cmd.CommandText = sql;
                        cmd.ExecuteNonQuery();
                    }
                }
                conn.Close();

            }
            catch (SoapException)
            {
                throw;
            }
            catch (Exception e)
            {
                throw new SoapException(e.Message, SoapException.ServerFaultCode);
            }

            return ret;
        }

        public static List<Whitelist> GetWhiteLists(UserValues uv)
        {
            List<Whitelist> ret = new List<Whitelist>();

            string sql = String.Format("sp_tcs_get_whitelistbydept ?");

            try
            {
                OdbcConnection conn = new OdbcConnection(conn_whitelist);
                OdbcCommand cmd = new OdbcCommand(sql, conn);

                cmd.Parameters.Add("l_deptpk", OdbcType.Int).Value = uv.department;

                conn.Open();
                OdbcDataReader rs = cmd.ExecuteReader();
                while (rs.Read())
                {
                    Whitelist tmp = new Whitelist();
                    tmp.name = rs.GetString(6);
                    tmp.size = rs.GetInt32(3);
                    tmp.state = (QueueState)rs.GetInt32(4);

                    ret.Add(tmp);
                }
                conn.Close();
            }
            catch (SoapException)
            {
                throw;
            }
            catch (Exception e)
            {
                throw new SoapException(e.Message, SoapException.ServerFaultCode);
            }

            return ret;
        }
        public static Whitelist GetWhiteList(UserValues uv, string name)
        {
            Whitelist ret = null;

            string sql = String.Format("sp_tcs_get_whitelistbyname ?,?,?");

            try
            {
                OdbcConnection conn = new OdbcConnection(conn_whitelist);
                OdbcCommand cmd = new OdbcCommand(sql, conn);

                cmd.Parameters.Add("sz_name", OdbcType.VarChar, 50).Value = name;
                cmd.Parameters.Add("l_deptpk", OdbcType.Int).Value = uv.department;
                cmd.Parameters.Add("l_comppk", OdbcType.Int).Value = uv.company;

                conn.Open();
                OdbcDataReader rs = cmd.ExecuteReader();
                if (rs.Read())
                {
                    ret = new Whitelist();

                    ret.name = rs.GetString(6);
                    ret.size = rs.GetInt32(3);
                    ret.state = (QueueState)rs.GetInt32(4);
                }
                conn.Close();
            }
            catch (SoapException)
            {
                throw;
            }
            catch (Exception e)
            {
                throw new SoapException(e.Message, SoapException.ServerFaultCode);
            }

            return ret;
        }

        // Messages
        public static List<MessageStatus> GetMessages(UserValues uv, DateTime? from, DateTime? to, int? reference)
        {
            List<MessageStatus> ret = new List<MessageStatus>();
            Hashtable temp_ret = new Hashtable();

            string sql = @"SELECT 
	                        MS.l_refno,
	                        LS.l_status,
	                        LS.l_items,
	                        LS.l_proc,
	                        LS.f_simulate,
	                        MS.l_createdate,
	                        MS.l_createtime,
	                        MS.sz_sendingname
                        FROM
	                        MDVSENDINGINFO MS,
	                        LBASEND LS,
	                        LBASEND_TS TS
                        WHERE
	                        MS.l_type=?
	                        and MS.l_deptpk=?
	                        and MS.l_refno=LS.l_refno
	                        and LS.l_refno=TS.l_refno
	                        and LS.l_operator=TS.l_operator
	                        and LS.l_status=TS.l_status";

            try
            {
                if (from.HasValue)
                    sql += " and (convert(numeric, l_createdate)*1000000 + convert(numeric, l_createtime)*100) >= " + from.Value.ToString("yyyyMMddHHmmss");
                if(to.HasValue)
                    sql += " and (convert(numeric, l_createdate)*1000000 + convert(numeric, l_createtime)*100) <= " + to.Value.ToString("yyyyMMddHHmmss");
                if (reference.HasValue)
                    sql += " and MS.l_refno=" + reference.ToString();

                OdbcConnection conn = new OdbcConnection(conn_login);
                OdbcCommand cmd = new OdbcCommand(sql, conn);

                cmd.Parameters.Add("l_type", OdbcType.Int).Value = MESSAGETYPE;
                cmd.Parameters.Add("l_deptpk", OdbcType.Int).Value = uv.department;

                conn.Open();
                OdbcDataReader rs = cmd.ExecuteReader();
                while (rs.Read())
                {
                    int l_refno = rs.GetInt32(0);
                    int l_recipients = rs.GetInt32(2);
                    int l_processed = rs.GetInt32(3);

                    if (l_recipients < 0) l_recipients = 0;
                    if (l_processed < 0) l_processed = 0;

                    SendingStatus status = GetSendingStatus(rs.GetInt32(1));

                    if (temp_ret.ContainsKey(l_refno))
                    {
                        MessageStatus tmp = (MessageStatus)temp_ret[l_refno];
                        // use the most "dominant" status (defined by the enum)
                        if (status > tmp.status)
                        {
                            tmp.status = status;
                        }
                        tmp.recipients += l_recipients;
                        tmp.processed += l_processed;
                        temp_ret[l_refno] = tmp;
                    }
                    else
                    {
                        MessageStatus tmp = new MessageStatus();
                        tmp.reference = l_refno;
                        tmp.status = status;
                        tmp.recipients = l_recipients;
                        tmp.processed = l_processed;
                        tmp.mode = (ExecuteMode)rs.GetInt32(4);
                        tmp.created = DateTime.ParseExact(String.Format("{0}{1,4:0000}", rs.GetInt32(5), rs.GetInt32(6)), "yyyyMMddHHmm", System.Globalization.DateTimeFormatInfo.InvariantInfo);
                        tmp.description = rs.GetString(7);

                        temp_ret.Add(tmp.reference, tmp);
                    }
                }
                rs.Close();
                conn.Close();
                foreach (MessageStatus val in temp_ret.Values)
                {
                    ret.Add(val);
                }
                // sort list
                var sorted = from msg in ret orderby msg.reference descending select msg;

                ret = sorted.ToList();
            }
            catch (SoapException)
            {
                throw;
            }
            catch (Exception e)
            {
                throw new SoapException(e.Message, SoapException.ServerFaultCode);
            }

            return ret;
        }
        public static List<CountryCodeStatus> GetCCStatus(UserValues uv, int reference)
        {
            List<CountryCodeStatus> ret = new List<CountryCodeStatus>();
            string sql = @"SELECT
	                        l_cc,
	                        sum(l_delivered) l_delivered,
	                        sum(l_expired) l_expired,
	                        sum(l_failed) + sum(l_unknown) l_failed,
                            sum(l_queued) l_queued,
	                        sum(l_submitted) l_sending,
	                        sum(l_subscribers) l_subscribers
                        FROM
	                        LBAHISTCC
                        WHERE
	                        l_refno=?
                        GROUP BY
	                        l_cc
                        ORDER BY
                            l_cc";
            try
            {
                OdbcConnection conn = new OdbcConnection(conn_login);
                OdbcCommand cmd = new OdbcCommand(sql, conn);

                cmd.Parameters.Add("l_refno", OdbcType.Int).Value = reference;

                conn.Open();
                OdbcDataReader rs = cmd.ExecuteReader();
                while (rs.Read())
                {
                    CountryCodeStatus cc = new CountryCodeStatus();
                    cc.cc = rs.GetInt32(0);
                    cc.delivered = rs.GetInt32(1);
                    cc.expired = rs.GetInt32(2);
                    cc.failed = rs.GetInt32(3);
                    cc.queued = rs.GetInt32(4);
                    cc.sending = rs.GetInt32(5);
                    cc.total = rs.GetInt32(6);

                    ret.Add(cc);
                }
                rs.Close();
                conn.Close();
            }
            catch (SoapException)
            {
                throw;
            }
            catch (Exception e)
            {
                throw new SoapException(e.Message, SoapException.ServerFaultCode);
            }

            return ret;
        }
        public static MessageStatus GetMessage(UserValues uv, int reference)
        {
            // return first (only) item in getmessages with specified refno
            return GetMessages(uv, null, null, reference)[0];
        }
        public static MessageContent GetContent(UserValues uv, int reference)
        {
            MessageContent ret = new MessageContent();
            ret.tocc = new List<CountryCode>();
            string sql = "";

            try
            {
                OdbcConnection conn = new OdbcConnection(conn_login);
                OdbcCommand cmd = new OdbcCommand(sql, conn);

                conn.Open();

                sql = @"SELECT
                            LT.sz_oadc, 
                            LT.sz_text
                        FROM
                            LBASEND_TEXT LT
                        WHERE
                            LT.l_refno=?";
                cmd.CommandText = sql;
                cmd.Parameters.Add("l_refno", OdbcType.Int).Value = reference;
                
                OdbcDataReader rs = cmd.ExecuteReader();
                if (rs.Read())
                {
                    ret.from = rs.GetString(0);
                    ret.text = rs.GetString(1);
                }
                rs.Close();
                
                sql = @"SELECT
	                LC.l_cc,
	                LC.sz_iso,
	                LC.sz_name
                FROM
	                LBASEND_TEXT LT,
	                LBASEND_TEXT_CC LTCC,
	                LBACOUNTRIES LC
                WHERE
	                LT.l_refno=?
	                AND LT.l_textpk=LTCC.l_textpk
	                AND LTCC.l_cc=LC.l_cc
                ORDER BY
                    LTCC.l_cc";
                cmd.CommandText = sql;
                cmd.Parameters.Add("l_refno", OdbcType.Int).Value = reference;

                rs = cmd.ExecuteReader();
                while (rs.Read())
                {
                    CountryCode cc = new CountryCode();
                    cc.cc = rs.GetInt32(0);
                    cc.iso = rs.GetString(1);
                    cc.name = rs.GetString(2);

                    ret.tocc.Add(cc);
                }
                rs.Close();
                conn.Close();
            }
            catch (SoapException)
            {
                throw;
            }
            catch (Exception e)
            {
                throw new SoapException(e.Message, SoapException.ServerFaultCode);
            }
            
            return ret;
        }
        public static List<SendingInfo> GetSendinginfo(int reference)
        {
            List<SendingInfo> ret = new List<SendingInfo>();

            string sql = "SELECT l_refno, l_operator, f_simulate, sz_jobid FROM LBASEND WHERE l_refno=?";

            try
            {
                OdbcConnection conn = new OdbcConnection(conn_login);
                OdbcCommand cmd = new OdbcCommand(sql, conn);

                cmd.Parameters.Add("l_refno", OdbcType.Int).Value = reference;

                conn.Open();
                OdbcDataReader rs = cmd.ExecuteReader();
                while (rs.Read())
                {
                    SendingInfo tmp = new SendingInfo();
                    tmp.l_refno = rs.GetInt32(0);
                    tmp.l_operator = rs.GetInt32(1);
                    tmp.l_simulate = rs.GetInt32(2);
                    tmp.sz_jobid = rs.GetString(3);

                    ret.Add(tmp);
                }
            }
            catch (SoapException)
            {
                throw;
            }
            catch (Exception e)
            {
                throw new SoapException(e.Message, SoapException.ServerFaultCode);
            }

            return ret;
        }
        public static void SetSendingStatus(int reference, int op, int status)
        {
            string sql = "UPDATE LBASEND SET l_status=? WHERE l_refno=? AND l_operator=?";

            try
            {
                OdbcConnection conn = new OdbcConnection(conn_login);
                OdbcCommand cmd = new OdbcCommand(sql, conn);

                cmd.Parameters.Add("l_status", OdbcType.Int).Value = status;
                cmd.Parameters.Add("l_refno", OdbcType.Int).Value = reference;
                cmd.Parameters.Add("l_operator", OdbcType.Int).Value = op;

                conn.Open();
                cmd.ExecuteNonQuery();
                conn.Close();
            }
            catch (SoapException)
            {
                throw;
            }
            catch (Exception e)
            {
                throw new SoapException(e.Message, SoapException.ServerFaultCode);
            }
        }
        public static List<CountryCode> GetCCs(UserValues uv)
        {
            List<CountryCode> ret = new List<CountryCode>();

            string sql = "SELECT l_cc, sz_iso, sz_name FROM LBACOUNTRIES WHERE l_cc>=1 AND l_cc<>? ORDER BY l_cc";

            try
            {
                OdbcConnection conn = new OdbcConnection(conn_login);
                OdbcCommand cmd = new OdbcCommand(sql, conn);

                cmd.Parameters.Add("l_stdcc", OdbcType.Int).Value = uv.stdcc;

                conn.Open();
                OdbcDataReader rs = cmd.ExecuteReader();
                while (rs.Read())
                {
                    CountryCode tmp = new CountryCode();
                    tmp.cc = rs.GetInt32(0);
                    tmp.iso = rs.GetString(1);
                    tmp.name = rs.GetString(2);

                    ret.Add(tmp);
                }
                rs.Close();
                conn.Close();
            }
            catch (SoapException)
            {
                throw;
            }
            catch (Exception e)
            {
                throw new SoapException(e.Message, SoapException.ServerFaultCode);
            }

            return ret;
        }
        public static int InsertSending(UserValues uv, Message msg)
        {
            int l_refno = 0;
            decimal l_textpk = 0;
            string sql = "";

            try
            {
                OdbcConnection conn = new OdbcConnection(conn_login);
                OdbcCommand cmd = new OdbcCommand(sql, conn);

                conn.Open();

                sql = "sp_refno_out";
                cmd.CommandText = sql;
                l_refno = (int)cmd.ExecuteScalar();

                if (l_refno <= 0)
                    throw new SoapException("Failed to get reference number.", SoapException.ServerFaultCode);

                sql = String.Format("INSERT INTO MDVSENDINGINFO(l_addresspos, l_lastantsep, l_refno, l_createdate, l_createtime, " +
                                      "l_scheddate, l_schedtime, sz_sendingname, l_sendingstatus, l_companypk, l_deptpk, l_nofax, l_group, " +
                                      "l_removedup, l_type, f_dynacall, l_addresstypes, l_maxchannels) " +
                                      "VALUES({0}, {1}, {2}, {3}, {4}, {5}, {6}, '{7}', {8}, {9}, {10}, {11}, " +
                                      "{12}, {13}, {14}, {15}, {16}, {17})",
                                0,
                                0,
                                l_refno,
                                DateTime.Now.ToString("yyyyMMdd"),
                                DateTime.Now.ToString("HHmm"),
                                0,
                                0,
                                "TCS - " + msg.description.Replace("'","''"), // + land eller multiple
                                1,
                                uv.company,
                                uv.department,
                                0,
                                5,
                                1,
                                MESSAGETYPE,
                                2,
                                1048576,
                                0);
                cmd.CommandText = sql;
                cmd.ExecuteNonQuery();

                sql = String.Format("sp_pas_ins_lbatext {0}, '{1}', '{2}', '{3}'",
                                l_refno,
                                "Message",
                                msg.from,
                                msg.text.Replace("'","''"));
                cmd.CommandText = sql;
                l_textpk = (decimal)cmd.ExecuteScalar();

                foreach (int cc in msg.toCountryCodes)
                {
                    sql = String.Format("sp_pas_ins_lbatext_cc {0}, {1}",
                        l_textpk,
                        cc);
                    cmd.CommandText = sql;
                    cmd.ExecuteNonQuery();

                    sql = String.Format("sp_tas_insert_send_country {0}, {1}, '{2}'",
                        l_refno,
                        cc,
                        GetCCIso(cc));
                    cmd.CommandText = sql;
                    cmd.ExecuteNonQuery();
                }
                foreach (Operator op in uv.operators)
                {
                    sql = String.Format("INSERT INTO LBASEND(l_refno, l_status, l_response, l_items, l_proc, l_retries, " +
                                             "l_requesttype, sz_jobid, sz_areaid, f_simulate, l_operator, l_type) VALUES({0}, {1}, {2}, {3}, {4}, {5}, " +
                                             "{6}, '{7}', '{8}', {9}, {10}, {11})",
                                             l_refno, 
                                             100, 
                                             -1, 
                                             -1, 
                                             -1, 
                                             0, 
                                             (int)RequestType.PREPARE,
                                             "", 
                                             "", 
                                             (int)msg.mode, 
                                             op.l_operator, 
                                             MESSAGETYPE);
                    cmd.CommandText = sql;
                    cmd.ExecuteNonQuery();
                }
            }
            catch (SoapException)
            {
                throw;
            }
            catch (Exception e)
            {
                throw new SoapException(e.Message, SoapException.ServerFaultCode);
            }

            return l_refno;
        }
        public static List<String> GetFromStrings(UserValues uv)
        {
            List<String> ret = new List<string>();

            try
            {
                string sql = String.Format("SELECT sz_from FROM LBADEPTFROM WHERE l_deptpk={0} AND l_comppk={1} ORDER BY sz_from"
                    , uv.department
                    , uv.company);

                OdbcConnection conn = new OdbcConnection(conn_login);
                OdbcCommand cmd = new OdbcCommand(sql, conn);

                conn.Open();
                OdbcDataReader rs = cmd.ExecuteReader();
                while (rs.Read())
                {
                    if(!rs.IsDBNull(0))
                        ret.Add(rs.GetString(0));
                }
                rs.Close();
                conn.Close();
            }
            catch (SoapException)
            {
                throw;
            }
            catch(Exception e)
            {
                throw new SoapException(e.Message, SoapException.ServerFaultCode);
            }

            return ret;
        }
        public static bool ValidSender(UserValues uv, string from)
        {
            bool ret = false;

            try
            {
                string sql = String.Format("SELECT sz_from FROM LBADEPTFROM WHERE l_deptpk={0} AND l_comppk={1} AND sz_from=? ORDER BY sz_from"
                    , uv.department
                    , uv.company);

                OdbcConnection conn = new OdbcConnection(conn_login);
                OdbcCommand cmd = new OdbcCommand(sql, conn);

                cmd.Parameters.Add("sz_from", OdbcType.VarChar, 25).Value = from;

                conn.Open();
                OdbcDataReader rs = cmd.ExecuteReader();
                while (rs.Read())
                {
                    if (!rs.IsDBNull(0))
                        if (rs.GetString(0) == from)
                            ret = true;
                }
                rs.Close();
                conn.Close();
            }
            catch (SoapException)
            {
                throw;
            }
            catch (Exception e)
            {
                throw new SoapException(e.Message, SoapException.ServerFaultCode);
            }

            return ret;
        }

        // Operators
        public static List<Operator> GetOperators()
        {
            string qryOperators = "SELECT l_operator, sz_operatorname, sz_url, sz_user, sz_password, f_alertapi, f_statusapi, f_internationalapi, f_statisticsapi FROM LBAOPERATORS WHERE f_internationalapi=1 ORDER BY l_operator";

            OdbcConnection dbConn = new OdbcConnection(conn_login);
            OdbcCommand cmdOperators = new OdbcCommand(qryOperators, dbConn);
            OdbcDataReader rsOperators;

            List<Operator> Operators = new List<Operator>();

            try
            {
                dbConn.Open();

                rsOperators = cmdOperators.ExecuteReader();

                while (rsOperators.Read())
                {
                    Operator op = new Operator();

                    op.l_operator = rsOperators.GetInt32(0);
                    op.sz_operatorname = rsOperators.GetString(1);
                    op.sz_url = rsOperators.GetString(2);
                    op.sz_user = rsOperators.GetString(3);
                    op.sz_password = rsOperators.GetString(4);
                    op.b_alertapi = rsOperators.GetInt16(5) == 1 ? true : false;
                    op.b_statusapi = rsOperators.GetInt16(6) == 1 ? true : false;
                    op.b_internationalapi = rsOperators.GetInt16(7) == 1 ? true : false;
                    op.b_statisticsapi = rsOperators.GetInt16(8) == 1 ? true : false;
                    Operators.Add(op);
                }
                rsOperators.Close();
                rsOperators.Dispose();
                cmdOperators.Dispose();
                dbConn.Close();
                dbConn.Dispose();
            }
            catch (Exception e)
            {
                Log.WriteSysLog(String.Format("GetOperator() (exception={0})", e.Message),2,1);
            }

            return Operators;
        }
        public static List<Operator> GetOperators(UserValues oUser)
        {
            string qryOperators = "SELECT OP.l_operator, OP.sz_operatorname, OP.sz_url, OP.sz_user, OP.sz_password, OP.f_alertapi, OP.f_statusapi, OP.f_internationalapi, OP.f_statisticsapi FROM LBAOPERATORS OP, LBAOPERATORS_X_DEPT OD WHERE OD.l_operator=OP.l_operator AND OD.l_deptpk=" + oUser.department.ToString() + " ORDER BY l_operator";

            OdbcConnection dbConn = new OdbcConnection(conn_login);
            OdbcCommand cmdOperators = new OdbcCommand(qryOperators, dbConn);
            OdbcDataReader rsOperators;

            List<Operator> Operators = new List<Operator>();

            try
            {
                dbConn.Open();

                rsOperators = cmdOperators.ExecuteReader();

                while (rsOperators.Read())
                {
                    Operator op = new Operator();

                    op.l_operator = rsOperators.GetInt32(0);
                    op.sz_operatorname = rsOperators.GetString(1);
                    op.sz_url = rsOperators.GetString(2);
                    op.sz_user = rsOperators.GetString(3);
                    op.sz_password = rsOperators.GetString(4);
                    op.b_alertapi = rsOperators.GetInt16(5) == 1 ? true : false;
                    op.b_statusapi = rsOperators.GetInt16(6) == 1 ? true : false;
                    op.b_internationalapi = rsOperators.GetInt16(7) == 1 ? true : false;
                    op.b_statisticsapi = rsOperators.GetInt16(8) == 1 ? true : false;

                    Operators.Add(op);
                }
                rsOperators.Close();
                rsOperators.Dispose();
                cmdOperators.Dispose();
                dbConn.Close();
                dbConn.Dispose();
            }
            catch (Exception e)
            {
                Log.WriteSysLog(String.Format("GetOperators(Settings s) (exception={0})", e.Message),2,1);
            }

            return Operators;
        }
        public static Operator GetOperator(int l_operator)
        {
            Operator oRet = new Operator();

            string qryOperator = "SELECT l_operator, sz_operatorname, sz_url, sz_user, sz_password, f_alertapi, f_statusapi, f_internationalapi, f_statisticsapi FROM LBAOPERATORS WHERE l_operator=" + l_operator.ToString();

            OdbcConnection dbConn = new OdbcConnection(conn_login);
            OdbcCommand cmdOperator = new OdbcCommand(qryOperator, dbConn);
            OdbcDataReader rsOperator;

            try
            {
                dbConn.Open();

                rsOperator = cmdOperator.ExecuteReader();

                while (rsOperator.Read())
                {
                    oRet.l_operator = rsOperator.GetInt32(0);
                    oRet.sz_operatorname = rsOperator.GetString(1);
                    oRet.sz_url = rsOperator.GetString(2);
                    oRet.sz_user = rsOperator.GetString(3);
                    oRet.sz_password = rsOperator.GetString(4);
                    oRet.b_alertapi = rsOperator.GetInt16(5) == 1 ? true : false;
                    oRet.b_statusapi = rsOperator.GetInt16(6) == 1 ? true : false;
                    oRet.b_internationalapi = rsOperator.GetInt16(7) == 1 ? true : false;
                    oRet.b_statisticsapi = rsOperator.GetInt16(8) == 1 ? true : false;
                }
                rsOperator.Close();
                rsOperator.Dispose();
                cmdOperator.Dispose();
                dbConn.Close();
                dbConn.Dispose();
            }
            catch (Exception e)
            {
                Log.WriteSysLog(String.Format("GetOperators(int i) (exception={0})", e.Message),2,1);
            }

            return oRet;
        }
    }
}
