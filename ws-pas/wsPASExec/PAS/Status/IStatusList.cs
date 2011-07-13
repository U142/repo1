using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using com.ums.UmsCommon;

namespace com.ums.PAS.Status
{
    public interface IStatusListSearch
    {
        IStatusListResults Find();
    }
    public interface IStatusListResults
    {
        void finalize();
    }
}
