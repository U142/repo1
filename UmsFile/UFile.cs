using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml;
using com.ums.UmsCommon;
using System.IO;


namespace com.ums.UmsFile
{
    public class UFile
    {
        protected String sz_filename;
        protected String sz_path;
        protected String sz_ext;
        public String file() { return sz_filename; }
        public String path() { return sz_path; }
        public String full() { return sz_path + "\\" + sz_filename; }
        public String ext() { return sz_ext; }
        public UFile(string path, string file) 
        {
            sz_filename = file;
            sz_path = path;
            sz_ext = sz_filename.Substring(sz_filename.LastIndexOf("."));
        }

        //
        // Summary:
        // Move the file. Copy then Delete will be performed
        // Parameters: 
        //  dest:
        //      the file to move
        // Exceptions:
        //  Exception:
        //      If either copy or delete fails
        public void MoveOperation(UFile dest)
        {
            MoveOperation(dest, false);
        }

        public void MoveOperation(UFile dest, bool b_overwrite)
        {
            try
            {
                File.Copy(this.full(), dest.full(), b_overwrite);
            }
            catch (Exception e)
            {
                ULog.error(e.Message);
                throw e;
            }
            try
            {
                File.Delete(this.full());
            }
            catch (Exception e)
            {
                ULog.error(e.Message);
                throw e;
            }
        }

        public void RenameOperation(UFile dest)
        {
            try
            {
                File.Move(this.full(), dest.full());
            }
            catch (Exception e)
            {
                ULog.error(e.Message);
                throw e;
            }
        }

        public void CopyOperation(UFile dest)
        {
            try
            {
                File.Copy(this.full(), dest.full());
            }
            catch (Exception e)
            {
                ULog.error(e.Message);
                throw e;
            }
        }

        public virtual bool DeleteOperation()
        {
            try
            {
                File.Delete(this.full());
                return true;
            }
            catch (Exception e)
            {
                throw e;
            }
        }

        /*public static UFile GetTempFile()
        {
            String path = UCommon.UPATHS.sz_temppath;

        }*/
    }



    /*
     * Write and publish Shape-files for use in GUI
     */
    public class AdrfileGUIWriter : AdrfileWriter
    {
        public AdrfileGUIWriter(long l_refno)
        {
            n_refno = l_refno;
            n_resend_refno = -1;
            this.file = new UFile(UCommon.UPATHS.sz_path_temp, String.Format("{0}.adr", l_refno));
            openUTF8();
        }
        public AdrfileGUIWriter(long l_refno, long l_from_resend_refno)
        {
            n_refno = l_refno;
            n_resend_refno = l_from_resend_refno;
            this.file = new UFile(UCommon.UPATHS.sz_path_mapsendings, String.Format("{0}.adr", l_refno));
            //don't open the file for writing
        }
        public override bool publish()
        {
            if (n_resend_refno > 0)
            {
                UFile from = new UFile(UCommon.UPATHS.sz_path_mapsendings, String.Format("{0}.adr", n_resend_refno));
                try
                {
                    //copy the old GUI adrfile to the new resend
                    from.CopyOperation(file);
                }
                catch (Exception e)
                {
                    throw e;
                }
                return true;
            }
            else
            {
                try
                {
                    sw.Close();
                }
                catch (Exception) //just in case it's still open
                {

                }
                UFile dest = new UFile(UCommon.UPATHS.sz_path_mapsendings, this.file.file());
                try
                {
                    file.MoveOperation(dest);
                    return true;
                }
                catch (Exception e)
                {
                    ULog.error(n_refno, "Error publishing GUI addressfile", e.Message);
                    throw e;
                }
            }
        }
    }

    /*
     * Write and publish LBA-files
     */
    public class AdrfileLBAWriter : AdrfileWriter
    {
        SENDCHANNEL m_channel;
        public AdrfileLBAWriter(String sz_projectpk, long l_refno, bool b_utf8, SENDCHANNEL channel)
        {
            m_channel = channel;
            n_refno = l_refno;
            this.file = new UFile(UCommon.UPATHS.sz_path_temp, String.Format("{0}_SEND_{1}.{2}.xml", channel.ToString(), sz_projectpk, l_refno));
            if (b_utf8)
                openUTF8();
            else
                open();

        }
        public override bool publish()
        {
            try
            {
                sw.Close(); //just in case it's still open
            }
            catch (Exception)
            {
            }
            //UFile temp = new UFile(UCommon.UPATHS.sz_path_lba, this.file.file().Replace(this.file.ext(), ".tmp"));
            //UFile desttemp = new UFile(UCommon.UPATHS.sz_path_lba, this.file.file());
            String destpath = "";
            switch (m_channel)
            {
                case SENDCHANNEL.TAS:
                    destpath = UCommon.UPATHS.sz_path_tas;
                    break;
                default:
                    destpath = UCommon.UPATHS.sz_path_lba;
                    break;
            }
            String tempfile = this.file.file().Replace(this.file.ext(), ".tmp");
            UFile desttemp = new UFile(destpath, tempfile);
            UFile dest = new UFile(destpath, this.file.file());

            try
            {
                file.MoveOperation(desttemp);
                desttemp.RenameOperation(dest);
                return true;
            }
            catch (Exception e)
            {
                ULog.error(n_refno, "Error publishing LBA addressfile", e.Message);
                throw e;

            }
        }
        public bool publishResend()
        {
            try
            {
                sw.Close(); //just in case it's still open
            }
            catch (Exception)
            {
            }
            //UFile temp = new UFile(UCommon.UPATHS.sz_path_lba, this.file.file().Replace(this.file.ext(), ".tmp"));
            //UFile desttemp = new UFile(UCommon.UPATHS.sz_path_lba, this.file.file());
            String destpath = "";
            switch (m_channel)
            {
                case SENDCHANNEL.TAS:
                    destpath = UCommon.UPATHS.sz_path_tas;
                    break;
                default:
                    destpath = UCommon.UPATHS.sz_path_lba;
                    break;
            }
            DateTime now = DateTime.Now;
            
            String tempfile = this.file.file().Replace(this.file.ext(), ".tmp");
            UFile desttemp = new UFile(destpath, tempfile);
            UFile dest = new UFile(destpath, this.file.file().Replace(this.file.ext(), "." + now.ToString("yyyyMMddHHmmss") + ".xml"));

            try
            {
                file.MoveOperation(desttemp);
                desttemp.RenameOperation(dest);
                return true;
            }
            catch (Exception e)
            {
                ULog.error(n_refno, "Error publishing LBA addressfile", e.Message);
                throw e;

            }
        }
    }

    /*
     * Write and publish LBA confirm/cancel file
     */
    public class ConfirmLBAWriter : AdrfileWriter
    {
        String sz_jobid = "";
        public ConfirmLBAWriter(int refno, String jobid)
        {
            n_refno = refno;
            sz_jobid = jobid;
            this.file = new UFile(UCommon.UPATHS.sz_path_temp, String.Format("LBA_CONFIRM_{0}.{1}.xml", sz_jobid, n_refno));
            open();
        }
        public override bool publish()
        {
            try
            {
                sw.Close();
            }
            catch (Exception e)
            {
                throw e;
            }
            //UFile dest = new UFile(UCommon.UPATHS.sz_path_lba, this.file.file());
            String tempfile = this.file.file().Replace(this.file.ext(), ".tmp");
            UFile desttemp = new UFile(UCommon.UPATHS.sz_path_lba, tempfile);
            UFile dest = new UFile(UCommon.UPATHS.sz_path_lba, this.file.file());

            try
            {
                file.MoveOperation(desttemp);
                desttemp.RenameOperation(dest);
                return true;
            }
            catch (Exception e)
            {
                ULog.error(n_refno, "Error publishing Confirm LBA file", e.Message);
                throw e;
            }
        }
    }

    /*
    * Write and publish Shape-files for parsing (BCP) TAS resend
    */
    public class TASResendWriter : AdrfileWriter
    {
        public TASResendWriter (long l_refno, char sendingtype) {
            n_refno = l_refno;
            this.file = new UFile(UCommon.UPATHS.sz_path_bcp, String.Format("pri1-{0}{1}.{2}", sendingtype, l_refno, "res"));
            open();
        }
        public virtual bool publish()
        {
            try
            {
                sw.Close(); //just in case it's still open
            }
            catch (Exception)
            {

            }
            String tempfile = this.file.file().Replace(this.file.ext(), ".tmp");
            UFile desttemp = new UFile(UCommon.UPATHS.sz_path_bcp, tempfile);
            UFile dest = new UFile(UCommon.UPATHS.sz_path_bcp, this.file.file());
            try
            {
                file.MoveOperation(desttemp);
                desttemp.RenameOperation(dest);

                return true;
            }
            catch (Exception e)
            {
                ULog.error(n_refno, "Error publishing addressfile", e.Message);
                throw e;
            }
        }
    }

    /*
     * Write and publish Shape-files for parsing (BCP)
     */
    public class AdrfileWriter
    {
        protected UFile file;
        protected StreamWriter sw;
        protected long n_refno;
        protected long n_resend_refno;
        public long getRefno() { return n_refno; }


        protected AdrfileWriter()
        {

        }
        public AdrfileWriter(long l_refno, char sendingtype, bool b_resend)
        {
            n_refno = l_refno;
            this.file = new UFile(UCommon.UPATHS.sz_path_temp, String.Format("pri1-{0}{1}.{2}", sendingtype, l_refno, (b_resend ? "res" : "adr")));
            open();
        }
        public virtual bool delete()
        {
            try
            {
                File.Delete(this.file.full());
                return true;
            }
            catch (Exception e)
            {
                throw e;
            }
        }
        public virtual bool publish()
        {
            try
            {
                sw.Close(); //just in case it's still open
            }
            catch (Exception)
            {

            }
            String tempfile = this.file.file().Replace(this.file.ext(), ".tmp");
            UFile desttemp = new UFile(UCommon.UPATHS.sz_path_bcp, tempfile);
            UFile dest = new UFile(UCommon.UPATHS.sz_path_bcp, this.file.file());
            try
            {
                file.MoveOperation(desttemp);
                desttemp.RenameOperation(dest);
                
                return true;
            }
            catch (Exception e)
            {
                ULog.error(n_refno, "Error publishing addressfile", e.Message);
                throw e;
            }
        }
        protected bool open()
        {
            try
            {
                sw = new StreamWriter(file.full(), false, Encoding.GetEncoding("iso-8859-1"));
                return true;
            }
            catch (Exception e)
            {
                ULog.error(e.Message);
                return false;
            }
        }
        protected bool openUTF8()
        {
            try
            {
                sw = new StreamWriter(file.full(), false, Encoding.GetEncoding("UTF-8"));
                return true;
            }
            catch (Exception e)
            {
                ULog.error(e.Message);
                return false;
            }
        }
        public bool write(String s)
        {
            try
            {
                sw.Write(s);
                sw.Flush();
                return true;
            }
            catch (Exception e)
            {
                ULog.error(e.Message);
                return false;
            }
        }
        virtual public bool writeline(String s)
        {
            try
            {
                sw.WriteLine(s);
                sw.Flush();
                return true;
            }
            catch (Exception e)
            {
                //ULog.error(e.Message);
                //return false;
                throw e;
            }
        }
        virtual public void close()
        {
            try
            {
                sw.Flush();
                sw.Close();
            }
            catch (Exception e)
            {
                ULog.error(e.Message);
            }
        }

        
    }


    public class UXml : UFile
    {
        protected XmlDocument doc = new XmlDocument();
        protected String m_sz_lasterror;
        public String getLastError() { return m_sz_lasterror; }
        protected void setLastError(String s) { 
            m_sz_lasterror = s;
            ULog.error(s);
        }

        public UXml(string path, string file) : base(path, file)
        {
        }
        /*
         * start XmlTextReader and start first reading
         */
        protected bool read()
        {
            try
            {
                
                /*xmlReader = new XmlTextReader(sz_path + "/" + sz_filename);
                xmlReader.Read();*/
                doc = new XmlDocument();
                doc.Load(sz_path + "/" + sz_filename);
                return true;
            }
            catch (Exception e)
            {
                setLastError(e.Message);
                throw e;
            }
        }
    }
}
