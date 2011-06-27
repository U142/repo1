using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using java.util.zip;
using java.io;
using System.IO;
using com.ums.UmsFile;
using System.IO.Compression;


namespace com.ums.ZipLib
{
    public class UGZipLib
    {
        protected UFile ufile;
        String encoding = "UTF-8";
        public UGZipLib(string path, string file)
        {
            ufile = new UFile(path, file);
        }

        public byte [] getZipped(String data)
        {
            using (var ms = new MemoryStream())
            {
                using (var gzStream = new GZipStream(ms, CompressionMode.Compress))
                {
                    var content = Encoding.GetEncoding(encoding).GetBytes(data);
                    gzStream.Write(content, 0, content.Length);
                }
                return ms.ToArray();
            }
        }
    }

    public class UZipLib
    {
        protected ZipOutputStream zos;
        protected UFile ufile;

        public UZipLib(string path, string zipfilename)
        {
            //sz_filename = zipfilename;
            ufile = new UFile(path, zipfilename);
            java.io.File file = new java.io.File(ufile.full());
            FileOutputStream fos = new FileOutputStream(file);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            zos = new ZipOutputStream(bos);
            //zos = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
        }
        /*public void ZipTextToFile(String zipfilname, String text, Encoding enc)
        {
            Byte[] parm_to_utf8 = Encoding.GetEncoding(encoding).GetBytes(text);

        }*/

        public void AddTextAsZipFileEntry(String zipEntryName, String text, Encoding enc)
        {
            Byte[] str_encoded = enc.GetBytes(text);
            int l1 = (int)str_encoded.Length;
            sbyte[] sb1 = new sbyte[l1];
            Buffer.BlockCopy(str_encoded, 0, sb1, 0, l1);
            writeZipFileEntry(zos, zipEntryName, sb1);
        }

        public void finalize()
        {
            try
            {
                zos.close();
            }
            catch (Exception e)
            {
                throw;
            }
        }

        public void writeZipFileEntry(ZipOutputStream zos, String zipEntryName, sbyte[] byteArray)
        {
            try
            {
                int byteArraySize = byteArray.Length;

                CRC32 crc = new CRC32();
                crc.update(byteArray, 0, byteArraySize);

                ZipEntry entry = new ZipEntry(zipEntryName);
                entry.setMethod(ZipEntry.STORED);
                entry.setSize(byteArraySize);
                entry.setCrc(crc.getValue());

                zos.putNextEntry(entry);
                zos.write(byteArray, 0, byteArraySize);
                zos.closeEntry();
            }
            catch (Exception e)
            {
                throw;
            }
        }

        public byte[] ReadZipFileBytes()
        {
            try
            {
                FileInfo zipped = new FileInfo(ufile.full());
                FileStream fszipped = zipped.OpenRead();
                byte[] outbytes = new byte[zipped.Length];
                fszipped.Read(outbytes, 0, (int)zipped.Length);
                fszipped.Close();
                try
                {
                    ufile.DeleteOperation();
                }
                catch (Exception)
                {
                    
                }
                return outbytes;
            }
            catch (Exception e)
            {
                throw;
            }
        }
    }
}
