using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using com.ums.UmsCommon;

namespace com.ums.pas.integration.TimeProfileDb
{
    /// <summary>
    /// Default implementation for storing multiple timer profiles.
    /// </summary>
    public class TimeProfilerCollectorImpl : ITimeProfilerCollector
    {
        public TimeProfilerCollectorImpl()
        {
            timeProfileList = new List<TimeProfile>();
        }
        public List<TimeProfile> timeProfileList { get; private set; }

        #region ITimeProfilerCollector Members

        public void AddProfile(TimeProfile Profile)
        {
            timeProfileList.Add(Profile);
        }

        public List<TimeProfile> GetProfileList()
        {
            return timeProfileList;
        }

        public void OnTimerDispose(TimeProfile Profile)
        {

        }

        #endregion
    }
}
