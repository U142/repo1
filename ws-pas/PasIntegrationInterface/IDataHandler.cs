using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace com.ums.pas.integration
{
    /// <summary>
    /// Handle the entire alert. Get all recipients based on payload and start alerting.
    /// </summary>
    public interface IDataHandler
    {
        void HandleAlert(AlertMqPayload payload);

    }
}
