using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;
using com.ums.UmsFile;
using Ionic.Zlib;
using Ionic.Zip;


namespace com.ums.ZipLib
{
    public static class UGZipLib
    {
        private static Encoding encoding = Encoding.GetEncoding("UTF-8");
        public static byte[] getZipped(String data)
        {
            using (var ms = new MemoryStream())
            {
                using (var gzs = new GZipStream(ms, CompressionMode.Compress))
                {
                    new MemoryStream(encoding.GetBytes(data)).WriteTo(gzs);
                }
                return ms.ToArray();
            }
        }
    }

    public class UZipLib
    {
        private MemoryStream ms = new MemoryStream();
        private ZipOutputStream zs;

        public UZipLib()
        {
            zs = new ZipOutputStream(ms);
        }

        public void AddTextAsZipFileEntry(String zipEntryName, String text, Encoding enc)
        {
            zs.PutNextEntry(zipEntryName);
            new MemoryStream(enc.GetBytes(text)).WriteTo(zs);
        }

        public byte[] ReadZipFileBytes()
        {
            zs.Close();
            return ms.ToArray();
        }
    }
}
