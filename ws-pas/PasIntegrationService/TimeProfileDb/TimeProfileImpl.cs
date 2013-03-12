using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using com.ums.UmsCommon;

namespace com.ums.pas.integration.TimeProfileDb
{
    class TimeProfileImpl : ITimeProfilerCollector
    {
        public List<TimeProfile> TimeProfileList { get; private set; }

        public TimeProfileImpl()
        {
            TimeProfileList = new List<TimeProfile>();
        }

        #region ITimeProfilerCollector Members

        /// <summary>
        /// This implementation adds to list, but also inserts into database
        /// </summary>
        /// <param name="Profile"></param>
        void ITimeProfilerCollector.AddProfile(TimeProfile Profile)
        {
            TimeProfileList.Add(Profile);
        }

        List<TimeProfile> ITimeProfilerCollector.GetProfileList()
        {
            return TimeProfileList;
        }



        public void OnTimerDispose(TimeProfile Profile)
        {
            String Sql = String.Format("INSERT INTO MDVTIMEPROFILER(l_projectpk, sz_taskname, l_millisec, l_started_utc) VALUES({0},'{1}',{2},{3})",
                                            Profile.Id,
                                            Profile.TimerName,
                                            Profile.ElapsedMsec,
                                            Profile.StartedUtc);
            UmsDbLib.UmsDb db = new UmsDbLib.UmsDb();
            db.ExecNonQuery(Sql);
            db.close();
        }

        #endregion
    }
}
