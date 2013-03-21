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
                    Command.CommandText = "tmpFollowUpAlert ?";
                    Command.Parameters["l_projectpk"].Value = followUp.AlertId.Id;

                    using (OdbcDataReader rs = Command.ExecuteReader())
                    {
                        String [] address = rs.GetString(rs.GetOrdinal("sz_adrinfo")).Split('|');
                        int postNo = 0;
                        bool validPostno = address.Length >= 3 && Int32.TryParse(address[2], out postNo);

                        recipients.Add(new RecipientData()
                        {
                            Name = address.Length >= 1 ? address[0] : "",
                            Address = address.Length >= 2 ? address[1] : "",
                            Postno = address.Length >= 3 && validPostno ? postNo : 0,
                            PostPlace = address.Length >= 4 ? address[3] : "",
                            Company = rs.GetInt16(rs.GetOrdinal("iscompany")) == 1,
                            Lat = rs.GetDouble(rs.GetOrdinal("l_lat")),
                            Lon = rs.GetDouble(rs.GetOrdinal("l_lon")),

                            AlertTarget = null,

                            Endpoints = new List<Endpoint>()
                            {
                                new Phone()
                                {
                                    Address = rs.GetString(rs.GetOrdinal("sz_number")),
                                    CanReceiveSms = rs.GetInt16(rs.GetOrdinal("l_type")) == 2,
                                },
                            },

                        });
                    }
                }
            }

            return recipients;

        }

        private AlertTarget reconstructAlertTarget(int alertTarget, 
                                                int company,
                                                String name,
            int municipalId,
            int streetId,
            int houseNo,
            String letter,
            String oppgang,
            int gnr,
            int bnr,
            int fnr,
            int snr,
            int unr,
            int postno,
            String data,
            int birthdate,
            String attributes,
            String extId)                  
        {
            switch (alertTarget)
            {
                case 5: //resolves to FollowUpAlertObject - should not be used nor reconstrucable
                case 1: //AlertObject
                    break;
                case 2: //StreetId
                    break;
                case 3: //PropertyAddress
                    break;
                case 4: //OwnerAddress
                    break;
            }
        }


        #endregion


    }
}
