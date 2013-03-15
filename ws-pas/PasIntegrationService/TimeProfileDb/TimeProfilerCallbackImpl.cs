using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using com.ums.UmsCommon;
using log4net;

namespace com.ums.pas.integration.TimeProfileDb
{
    class TimeProfilerCallbackImpl : ITimeProfilerCallback
    {
        private static ILog log = LogManager.GetLogger(typeof(TimeProfilerCallbackImpl));

        #region ITimeProfilerCallback Members

        public void StartCallback(string text)
        {
            log.Info(text);
        }

        public void StopCallback(string text)
        {
            log.Info(text);
        }

        #endregion
    }
}
