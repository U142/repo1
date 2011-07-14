using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace pas_cb_weblistener
{
    public class Tools
    {
        // convert t-mobile message number to correct endian
        public static byte[] GetBytes(int i)
        {
            if (BitConverter.IsLittleEndian)
                return BitConverter.GetBytes(i).Reverse().ToArray();
            else
                return BitConverter.GetBytes(i);
        }
        public static int ToInt32(byte[] b, int i)
        {
            if (BitConverter.IsLittleEndian)
                return BitConverter.ToInt32(b.Reverse().ToArray(), i);
            else
                return BitConverter.ToInt32(b, i);
        }
    }
}
