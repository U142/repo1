using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using log4net;
using System.Data.Odbc;

namespace com.ums.pas.integration.AddressLookup
{
    class FollowupLookupImpl : IFollowupLookupFacade
    {
        private static ILog log = LogManager.GetLogger(typeof(FollowupLookupImpl));

        #region IFollowupLookupFacade Members

        /// <summary>
        /// Connect to backbone and retrieve recipientdata from previous alerts.
        /// </summary>
        /// <param name="ConnectionString"></param>
        /// <param name="FollowupAlerts"></param>
        /// <returns></returns>
        public IEnumerable<RecipientData> GetRecipientsFromEarlierAlerts(String ConnectionString, List<FollowupAlertObject> FollowupAlerts)
        {
            if (FollowupAlerts.Count == 0)
            {
                log.Info("No followup alerts listed");
                return new List<RecipientData>();
            }

            List<RecipientData> recipients = new List<RecipientData>();
            using (OdbcConnection Connection = new OdbcConnection(ConnectionString))
            using (OdbcCommand Command = Connection.CreateCommand())
            {
                Connection.Open();
                Command.Parameters.Add("l_projectpk", OdbcType.Numeric);
                foreach(FollowupAlertObject followUp in FollowupAlerts)
                {
                    int mobilePhones = 0;
                    int fixedPhones = 0;

                    log.InfoFormat("Retrieving recipients from Alert {0}", followUp.AlertId.Id);
                    Command.CommandText = "sp_getProjectStatus ?";
                    Command.Parameters["l_projectpk"].Value = followUp.AlertId.Id;

                    using (OdbcDataReader rs = Command.ExecuteReader())
                    {
                        while (rs.Read())
                        {
                            String[] address = rs["sz_adrinfo"] == DBNull.Value ? "".Split('|') : rs["sz_adrinfo"].ToString().Split('|');//rs.GetString(rs.GetOrdinal("sz_adrinfo")).Split('|');
                            int postNo = 0;
                            bool validPostno = address.Length >= 3 && Int32.TryParse(address[2], out postNo);

                            recipients.Add(new RecipientData()
                            {
                                Name = address.Length >= 1 ? address[0] : "",
                                Address = address.Length >= 2 ? address[1] : "",
                                Postno = address.Length >= 3 && validPostno ? postNo : 0,
                                PostPlace = address.Length >= 4 ? address[3] : "",
                                Company = rs.GetByte(rs.GetOrdinal("iscompany")) == 1,
                                Lat = rs.GetDouble(rs.GetOrdinal("l_lat")),
                                Lon = rs.GetDouble(rs.GetOrdinal("l_lon")),

                                AlertTarget = AlertTargetHelpers.ReconstructAlertTarget(
                                                        rs.GetByte(rs.GetOrdinal("alerttarget")),
                                                        rs.GetByte(rs.GetOrdinal("iscompany")),
                                                        rs.GetString(rs.GetOrdinal("name")),
                                                        rs.GetInt32(rs.GetOrdinal("municipalid")),
                                                        rs.GetInt32(rs.GetOrdinal("streetid")),
                                                        rs.GetInt32(rs.GetOrdinal("houseno")),
                                                        rs.GetString(rs.GetOrdinal("letter")),
                                                        rs.GetString(rs.GetOrdinal("oppgang")),
                                                        rs.GetInt32(rs.GetOrdinal("gnr")),
                                                        rs.GetInt32(rs.GetOrdinal("bnr")),
                                                        rs.GetInt32(rs.GetOrdinal("fnr")),
                                                        rs.GetInt32(rs.GetOrdinal("snr")),
                                                        rs.GetInt32(rs.GetOrdinal("unr")),
                                                        rs.GetInt32(rs.GetOrdinal("postno")),
                                                        rs.GetString(rs.GetOrdinal("data")),
                                                        rs.GetInt32(rs.GetOrdinal("birthdate")),
                                                        rs.GetString(rs.GetOrdinal("attributes")),
                                                        rs.GetString(rs.GetOrdinal("externalid")),
                                                        new Phone()
                                                        {
                                                            Address = rs.GetString(rs.GetOrdinal("sz_number")),
                                                            CanReceiveSms = rs.GetInt32(rs.GetOrdinal("l_type")) == 2,
                                                        }
                                                        ),

                                Endpoints = new List<Endpoint>()
                                {
                                    new Phone()
                                    {
                                        Address = rs.GetString(rs.GetOrdinal("sz_number")),
                                        CanReceiveSms = rs.GetInt32(rs.GetOrdinal("l_type")) == 2,
                                    },
                                },
                                
                            });
                            mobilePhones += rs.GetInt32(rs.GetOrdinal("l_type")) == 2 ? 1 : 0;
                            fixedPhones += rs.GetInt32(rs.GetOrdinal("l_type")) == 1 ? 1 : 0;
                        }
                    }
                    log.InfoFormat("Found {0} recipients based on Alert {1}, owning {2} mobile and {3} fixed phones", recipients.Count, followUp.AlertId.Id, mobilePhones, fixedPhones);
                }
            }

            return recipients;

        }




        #endregion


    }
}
