using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace com.ums.pas.integration
{
    public interface IDataHandler
    {
        void HandleAlert(AlertMqPayload payload);

    }
}
