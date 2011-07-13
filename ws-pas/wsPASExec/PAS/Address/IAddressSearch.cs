using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace com.ums.PAS.Address
{
    public interface IAddressSearch
    {
        IAddressResults Find();
    }
    public interface IAddressResults
    {
        void finalize();
    }
}
