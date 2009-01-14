using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using com.ums.UmsCommon;
using com.ums.UmsDbLib;
using com.ums.UmsFile;
using System.Collections;
using com.ums.UmsParm;


namespace com.ums.UmsParm
{
    public class ParmGenerateSending
    {
        public enum SYSLOG{
            ALERTINFO_SYSLOG_NONE,
            ALERTINFO_SYSLOG_WARNING,
            ALERTINFO_SYSLOG_ERROR,
            ALERTINFO_SYSLOG_INFO
        };

        PASUmsDb db;
        ULOGONINFO logoninfo;
        protected USimpleXmlWriter xmlwriter;

        protected String m_sz_lasterror;
        protected void setLastError(String s, bool b_errortag) { 
            m_sz_lasterror = s;
            if (b_errortag)
            {
                xmlwriter.insertStartElement("error");
                xmlwriter.insertText(s);
                xmlwriter.insertEndElement();
            }
        }

        public String getLastError() { return m_sz_lasterror; }

        public void setAlertInfo(bool b_okresult, String l_projectpk, long l_refno, Int64 l_alertpk, String sz_alertname, String s_msg, String s_extended, SYSLOG log_as)
        {
            xmlwriter.insertStartElement("alert");
            xmlwriter.insertAttribute("result", b_okresult.ToString());
            //xmlwriter.insertAttribute("l_projectpk", l_projectpk);
            xmlwriter.insertAttribute("l_refno", l_refno.ToString());
            xmlwriter.insertAttribute("l_alertpk", l_alertpk.ToString());
            xmlwriter.insertAttribute("sz_name", sz_alertname);
            xmlwriter.insertAttribute("text", s_msg);
            xmlwriter.insertText(s_extended);
            xmlwriter.insertEndElement();
            try
            {
                //if (b_okresult)
                switch(log_as)
                {
                    case SYSLOG.ALERTINFO_SYSLOG_INFO:
                        ULog.write(l_refno, s_msg + "\r\n" + s_extended);
                        break;
                    case SYSLOG.ALERTINFO_SYSLOG_ERROR:
                        ULog.error(l_refno, s_msg + "\r\n" + s_extended);
                        break;
                    case SYSLOG.ALERTINFO_SYSLOG_WARNING:
                        ULog.warning(l_refno, s_msg + "\r\n" + s_extended);
                        break;
                }
            }
            catch (Exception) { }
        }

        public bool Initialize(ref ULOGONINFO logoninfo, ref USimpleXmlWriter xmlwriter)
        {
            this.xmlwriter = xmlwriter;
            this.logoninfo = logoninfo;
            try
            {
                db = new PASUmsDb(UCommon.UBBDATABASE.sz_dsn, UCommon.UBBDATABASE.sz_uid, UCommon.UBBDATABASE.sz_pwd);
            }
            catch (Exception e)
            {
                setAlertInfo(false, "0", 0, 0, " ", "Could not initialize Database Connection", e.Message, SYSLOG.ALERTINFO_SYSLOG_ERROR);
                return false;
            }

            try
            {
                bool b = db.CheckLogon(ref this.logoninfo); //l_userpk, l_comppk, l_deptpk, sz_userid, sz_compid, sz_deptid, sz_password);
                if (b)
                {
                    return true;
                }
                else
                    return false;
                //else
                //{
                    //setLastError(db.getLastError(), true);
                //}
            }
            catch (Exception e)
            {
                //this will be logged directly in the db-class
                setAlertInfo(false, "0", 0, 0, " ", db.getLastError(), e.Message, SYSLOG.ALERTINFO_SYSLOG_NONE);
                return false;
            }
        }
        public void DeInitialize()
        {
            try
            {
                db.close();
            }
            catch (Exception e)
            {
                throw e;
            }
        }


        protected bool createProject(String s, Int64 l_alertpk, ref BBPROJECT project, int n_function)
        {
            if (n_function != UCommon.USENDING_TEST)
            {
                bool b_ret = db.newProject(String.Format("UTC {0} {1} WebService ({2})", UCommon.UGetDateNowLiteralUTC(), UCommon.UGetTimeNowLiteralUTC(), s), ref logoninfo, ref project);
                if (!b_ret)
                {
                    setAlertInfo(false, "0", 0, l_alertpk, " ", "Error creating a new project. Sending will continue", db.getLastError(), SYSLOG.ALERTINFO_SYSLOG_ERROR);
                }
                return b_ret;
            } else 
            {
                project.sz_projectpk = Guid.NewGuid().ToString();
                project.sz_name = "Temporary";
                project.n_deptpk = logoninfo.l_deptpk;
                return true;
            }
        }

        public bool SendAlert(Int64 l_alertpk, int n_function, string sz_scheddate, string sz_schedtime)
        {
            PAALERT pa = new PAALERT();
            db.FillAlert(l_alertpk, n_function, ref pa);
            BBPROJECT project = new BBPROJECT();
            bool b = createProject("SendAlert", l_alertpk, ref project, n_function);
            xmlwriter.insertStartElement("project");
            xmlwriter.insertAttribute("l_projectpk", project.sz_projectpk);
            b = _sendhandler(ref project, 0, ref pa, n_function, sz_scheddate, sz_schedtime);
            xmlwriter.insertEndElement(); //project
            return b;
        }

        public bool SendEvent(Int64 l_eventpk, int n_function, string sz_scheddate, string sz_schedtime)
        {
            //get all alerts. send all

            ArrayList alerts = new ArrayList();
            db.GetAlertsFromEvent(l_eventpk, n_function, ref alerts);

            BBPROJECT project = new BBPROJECT();
            createProject("SendEvent", 0, ref project, n_function);

            xmlwriter.insertStartElement("project");
            xmlwriter.insertAttribute("l_projectpk", project.sz_projectpk);

            IEnumerator enu = alerts.GetEnumerator();
            while (enu.MoveNext())
            {
                PAALERT pa = (PAALERT)enu.Current;
                _sendhandler(ref project, l_eventpk, ref pa, n_function, sz_scheddate, sz_schedtime);
            }
            xmlwriter.insertEndElement(); //project

            return true;
        }

        protected bool _sendhandler(ref BBPROJECT project, Int64 l_fromeventpk, ref PAALERT pa, int n_function,
                            string sz_scheddate, string sz_schedtime)
        {
            try
            {
                send_v3(ref project, l_fromeventpk, ref pa, n_function, sz_scheddate, sz_schedtime);
            }
            catch (UVerifyAlertException e)
            {
                setAlertInfo(false, project.sz_projectpk, 0, pa.l_alertpk, pa.sz_name, getLastError(), e.Message, SYSLOG.ALERTINFO_SYSLOG_ERROR);
                return false;
            }
            catch (UProfileNotSupportedException e)
            {
                setAlertInfo(false, project.sz_projectpk, 0, pa.l_alertpk, pa.sz_name, e.Message, "", SYSLOG.ALERTINFO_SYSLOG_ERROR);
                return false;
            }
            catch (URefnoException e)
            {
                setAlertInfo(false, project.sz_projectpk, 0, pa.l_alertpk, pa.sz_name, e.Message, "", SYSLOG.ALERTINFO_SYSLOG_ERROR);
                return false;
            }
            catch (UDbNoDataException e)
            {
                setAlertInfo(false, project.sz_projectpk, 0, pa.l_alertpk, pa.sz_name, e.Message, "", SYSLOG.ALERTINFO_SYSLOG_ERROR);
                return false;
            }
            /*catch (Exception e)
            {
                setAlertInfo(false, project.sz_projectpk, 0, pa.l_alertpk, pa.sz_name, e.Message, "", SYSLOG.ALERTINFO_SYSLOG_ERROR);
                return false;
            }*/
            return true;
        }

        protected bool send_v3(ref BBPROJECT project, Int64 l_fromeventpk, ref PAALERT pa, int n_function,
                            string sz_scheddate, string sz_schedtime)
        {
            try
            {
                db.VerifyAlert(pa.l_alertpk, ref logoninfo);
                db.VerifyProfile(pa.l_profilepk);
            }
            catch (Exception e) //may catch UVerifyAlertException or UProfileNotSupportedException. rethrow
            {
                throw e;
            }
            BBRESCHEDPROFILE resched_profile = new BBRESCHEDPROFILE();
            BBVALID valid = new BBVALID();
            BBSENDNUM sendnum = new BBSENDNUM();
            MDVSENDINGINFO sendinginfo = new MDVSENDINGINFO();
            BBACTIONPROFILESEND profile = new BBACTIONPROFILESEND();

            bool b_ret = false;

            long l_refno = 0;


            //If this is only a test, don't waste a refno
            if (n_function != UCommon.USENDING_TEST)
            {
                try
                {
                    l_refno = db.newRefno();
                }
                catch (Exception)
                {
                    throw new URefnoException();
                }
            }
            string sz_sendingname = String.Format("{0}", pa.sz_name);

            

            UXmlAlert file = new UXmlAlert(UCommon.UPATHS.sz_path_predefined_areas, String.Format("a{0}.xml", pa.l_alertpk));
            try
            {
                file.load(String.Format("{0}.{1}", project.sz_projectpk, l_refno));
            }
            catch (UFileCopyException e)
            {
                //we don't have a local copy
                setAlertInfo(false, project.sz_projectpk, l_refno, pa.l_alertpk, pa.sz_name, "Could not copy remote file to a local copy", e.Message, SYSLOG.ALERTINFO_SYSLOG_ERROR);
                return false;
            }
            catch (UXmlParseException e)
            {
                //the file has been copied to a local drive, delete it
                setAlertInfo(false, project.sz_projectpk, l_refno, pa.l_alertpk, pa.sz_name, "Could not parse XML file", e.Message, SYSLOG.ALERTINFO_SYSLOG_ERROR);
                file.DeleteOperation();
                return false;
            }

            pa.setSendingType(file.getSendingType());
            pa.setShape(file.getShape());
            pa.setLBAShape(file.getLBAShape());


            PAS_SENDING sending = new PAS_SENDING();
            sending.setRefno(l_refno, ref project);

            //retrieve sending info from DB
            db.FillReschedProfile(pa.l_schedpk, ref resched_profile);
            db.FillValid(ref pa, ref valid);
            db.FillSendNum(ref pa, ref sendnum);
            db.FillActionProfile(ref pa, ref profile);
            db.FillSendingInfo(ref logoninfo, ref pa, ref sendinginfo, new UDATETIME(sz_scheddate, sz_schedtime), sz_sendingname);

            //fill a sending struct
            sending.setSendingInfo(ref sendinginfo);
            sending.setReschedProfile(ref resched_profile);
            sending.setValid(ref valid);
            sending.setSendNum(ref sendnum);
            sending.setActionProfile(ref profile);


            bool b_publish_voice = false;
            bool b_publish_lba = false;
            try
            {
                sending.setShape(ref pa.m_shape); //will also create a temp address file
                b_publish_voice = true;
            }
            catch (Exception e)
            {
                setAlertInfo(false, project.sz_projectpk, sending.l_refno, pa.l_alertpk, pa.sz_name, "Error creating shape file for sending. Aborting...", e.Message, SYSLOG.ALERTINFO_SYSLOG_ERROR);
                file.DeleteOperation();
                return false;
            }
            try
            {
                if (pa.m_lba_shape != null)
                {
                    if (pa.m_lba_shape.lba().getValid() && pa.hasValidAreaID())
                    {
                        sending.setLBAShape(ref logoninfo, ref pa, ref pa.m_lba_shape, n_function);
                        b_publish_lba = true;
                        
                    }
                    else
                    {
                        if (!pa.m_lba_shape.lba().getValid())
                            setAlertInfo(false, project.sz_projectpk, sending.l_refno, pa.l_alertpk, pa.sz_name, "An error was found in the Location Based Alert-part", "", SYSLOG.ALERTINFO_SYSLOG_WARNING);
                        else if (!pa.hasValidAreaID())
                            setAlertInfo(false, project.sz_projectpk, sending.l_refno, pa.l_alertpk, pa.sz_name, "This ALERT has not registered a valid AREA-ID from provider", "", SYSLOG.ALERTINFO_SYSLOG_WARNING);
                    }
                }
            }
            catch (Exception e)
            {
                setAlertInfo(false, project.sz_projectpk, sending.l_refno, pa.l_alertpk, pa.sz_name, "Error creating shape file for Location Based Alert", e.Message, SYSLOG.ALERTINFO_SYSLOG_ERROR);
                sending.lbacleanup();
            }


            //send it
            try
            {
                if (n_function == UCommon.USENDING_TEST) //test DB and rollback
                {
                    //db.BeginTransaction();
                    //db.RollbackTransaction();
                }
                else
                {
                    b_ret = db.Send(ref sending);
                }
            }
            catch (Exception e)
            {
                setAlertInfo(false, project.sz_projectpk, sending.l_refno, pa.l_alertpk, pa.sz_name, "Could not send due to database error. Aborting...", e.Message, SYSLOG.ALERTINFO_SYSLOG_ERROR);
                return false;
            }
            if (n_function == UCommon.USENDING_TEST) //test DB and rollback
            {
                //db.BeginTransaction();
                //db.RollbackTransaction();
                b_ret = true; //fake link to project
            }
            else
            {
                b_ret = db.linkRefnoToProject(ref project, l_refno, 0, 0);
            }
            if (!b_ret)
            {
                setAlertInfo(false, project.sz_projectpk, sending.l_refno, pa.l_alertpk, pa.sz_name, "Could not link sending to project. Sending will continue", db.getLastError(), SYSLOG.ALERTINFO_SYSLOG_WARNING);
            }
            

            //if all ok, publish addressfile
            try
            {
                if (n_function != UCommon.USENDING_TEST)
                    sending.publishGUIAdrFile();
            }
            catch (Exception e)
            {
                //this is not important for the sending, so continue
                //ULog.warning(sending.l_refno, "Could not publish GUI address file", e.Message);
                setAlertInfo(false, project.sz_projectpk, sending.l_refno, pa.l_alertpk, pa.sz_name, "Could not publish GUI address file. Only required for status view.", e.Message, SYSLOG.ALERTINFO_SYSLOG_WARNING);
            }
            try
            {
                if (b_publish_voice) //requires that no exceptions were caught while writing temp file
                {
                    if (n_function != UCommon.USENDING_TEST)
                    {
                        sending.publishAdrFile();
                    }
                    setAlertInfo(true, project.sz_projectpk, sending.l_refno, pa.l_alertpk, pa.sz_name, "Voice Message " + UCommon.USENDINGTYPE_SENT(n_function) + " [" + pa.getSendingTypeText() + "]", "", SYSLOG.ALERTINFO_SYSLOG_NONE);
                }
            }
            catch (Exception e)
            {
                //ULog.error(sending.l_refno, "Could not publish address file", e.Message);
                setAlertInfo(false, project.sz_projectpk, sending.l_refno, pa.l_alertpk, pa.sz_name, "Could not publish address file. Aborting... [" + pa.getSendingTypeText() + "]", e.Message, SYSLOG.ALERTINFO_SYSLOG_ERROR);
            }
            try
            {
                if (b_publish_lba) //requires that no exceptions were caught while writing temp file
                {
                    if (n_function != UCommon.USENDING_TEST)
                    {
                        try
                        {
                            db.InsertLBARecord(sending.l_refno, 199, -1, -1, -1, 0, pa.n_requesttype, "", pa.sz_areaid, n_function);
                            if (sending.publishLBAFile())
                            {
                                setAlertInfo(true, project.sz_projectpk, sending.l_refno, pa.l_alertpk, pa.sz_name, "Location Based Alert " + UCommon.USENDINGTYPE_SENT(n_function) + " [" + pa.sz_areaid + "]", "", SYSLOG.ALERTINFO_SYSLOG_NONE);
                            }
                            else
                            {
                                setAlertInfo(true, project.sz_projectpk, sending.l_refno, pa.l_alertpk, pa.sz_name, "No Location Based Alert found", "", SYSLOG.ALERTINFO_SYSLOG_ERROR);
                                db.SetLBAStatus(sending.l_refno, 41100);
                            }
                        }
                        catch(Exception e)
                        {
                            setAlertInfo(true, project.sz_projectpk, sending.l_refno, pa.l_alertpk, pa.sz_name, "Could not insert into LBASEND", e.Message, SYSLOG.ALERTINFO_SYSLOG_ERROR);
                        }
                    }
                    else
                    {
                        setAlertInfo(true, project.sz_projectpk, sending.l_refno, pa.l_alertpk, pa.sz_name, "Location Based Alert " + UCommon.USENDINGTYPE_SENT(n_function) + " [" + pa.sz_areaid + "]", "", SYSLOG.ALERTINFO_SYSLOG_NONE);
                    }
                }
            }
            catch (Exception e)
            {
                if(sending.hasLBA())
                {
                    //ULog.error(sending.l_refno, "Could not publish LBA address file", e.Message);
                    setAlertInfo(false, project.sz_projectpk, sending.l_refno, pa.l_alertpk, pa.sz_name, "Could not publish LBA address file.", e.Message, SYSLOG.ALERTINFO_SYSLOG_ERROR);
                }
                else
                {
                    ULog.warning(sending.l_refno, "Could not remove the temporary LBA addressfile", e.Message);
                }
            }

            try
            {
                file.DeleteOperation();
            }
            catch (Exception e)
            {
                ULog.warning(sending.l_refno, String.Format("Could not remove the local temporary alert file\n{0}", file.full()), e.Message);
            }

            ULog.write(String.Format("New <{0}> Sending\nUserID={1}\nDeptID={2}\nCompID={3}\nProject={4}\nRefno={5}\nAlertpk={6}\nEventpk={7})",
                //(f_simulation ? "[Simulated]" : "[Live]"),
                UCommon.USENDINGTYPE(n_function).ToUpper(),
                logoninfo.sz_userid, logoninfo.sz_deptid, logoninfo.sz_compid, project.sz_projectpk, l_refno, pa.l_alertpk, l_fromeventpk));

            return true;
        }


        protected bool send(ref BBPROJECT project, Int64 l_fromeventpk, ref PAALERT pa, int n_function,
                            string sz_scheddate, string sz_schedtime)
        {
            //if n_function = UCommon.USENDING_TEST then nothing should be inserted into DB and address files should not be published

            try
            {
                db.VerifyAlert(pa.l_alertpk, ref logoninfo);
                db.VerifyProfile(pa.l_profilepk);
            }
            catch (Exception e) //may catch UVerifyAlertException or UProfileNotSupportedException. rethrow
            {
                throw e;
            }

            BBRESCHEDPROFILE resched_profile = new BBRESCHEDPROFILE();
            BBVALID valid = new BBVALID();
            BBSENDNUM sendnum = new BBSENDNUM();
            MDVSENDINGINFO sendinginfo = new MDVSENDINGINFO();
            BBACTIONPROFILESEND profile = new BBACTIONPROFILESEND();

            bool b_ret = false;

            long l_refno = 0;


            //If this is only a test, don't waste a refno
            if (n_function != UCommon.USENDING_TEST)
            {
                try
                {
                    l_refno = db.newRefno();
                }
                catch (Exception)
                {
                    throw new URefnoException();
                }
            }
            string sz_sendingname = String.Format("WebService [{0}])", pa.sz_name);

            

            UXmlAlert file = new UXmlAlert(UCommon.UPATHS.sz_path_predefined_areas, String.Format("a{0}.xml", pa.l_alertpk));
            try
            {
                file.load(String.Format("{0}.{1}", project.sz_projectpk, l_refno));
            }
            catch (UFileCopyException e)
            {
                //we don't have a local copy
                setAlertInfo(false, project.sz_projectpk, l_refno, pa.l_alertpk, pa.sz_name, "Could not copy remote file to a local copy", e.Message, SYSLOG.ALERTINFO_SYSLOG_ERROR);
                return false;
            }
            catch (UXmlParseException e)
            {
                //the file has been copied to a local drive, delete it
                setAlertInfo(false, project.sz_projectpk, l_refno, pa.l_alertpk, pa.sz_name, "Could not parse XML file", e.Message, SYSLOG.ALERTINFO_SYSLOG_ERROR);
                file.DeleteOperation();
                return false;
            }

            pa.setSendingType(file.getSendingType());
            pa.setShape(file.getShape());
            pa.setLBAShape(file.getLBAShape());


            PAS_SENDING sending = new PAS_SENDING();
            sending.setRefno(l_refno, ref project);
            //sending.setLBAShape(

            bool b_publish_voice = false;
            bool b_publish_lba = false;
            try
            {
                sending.setShape(ref pa.m_shape); //will also create a temp address file
                b_publish_voice = true;
            }
            catch (Exception e)
            {
                setAlertInfo(false, project.sz_projectpk, sending.l_refno, pa.l_alertpk, pa.sz_name, "Error creating shape file for sending. Aborting...", e.Message, SYSLOG.ALERTINFO_SYSLOG_ERROR);
                file.DeleteOperation();
                return false;
            }
            try
            {
                if (pa.m_lba_shape != null)
                {
                    if (pa.m_lba_shape.lba().getValid())
                    {
                        sending.setLBAShape(ref logoninfo, ref pa, ref pa.m_lba_shape, n_function);
                        b_publish_lba = true;
                    }
                    else
                    {
                        setAlertInfo(false, project.sz_projectpk, sending.l_refno, pa.l_alertpk, pa.sz_name, "An error was found in the Location Based Alert-part", "", SYSLOG.ALERTINFO_SYSLOG_WARNING);
                    }
                }
            }
            catch (Exception e)
            {
                setAlertInfo(false, project.sz_projectpk, sending.l_refno, pa.l_alertpk, pa.sz_name, "Error creating shape file for Location Based Alert", e.Message, SYSLOG.ALERTINFO_SYSLOG_ERROR);
                sending.lbacleanup();
            }

            //retrieve sending info from DB
            db.FillReschedProfile(pa.l_schedpk, ref resched_profile);
            db.FillValid(ref pa, ref valid);
            db.FillSendNum(ref pa, ref sendnum);
            db.FillActionProfile(ref pa, ref profile);
            db.FillSendingInfo(ref logoninfo, ref pa, ref sendinginfo, new UDATETIME(sz_scheddate, sz_schedtime), sz_sendingname);

            //fill a sending struct
            sending.setSendingInfo(ref sendinginfo);
            sending.setReschedProfile(ref resched_profile);
            sending.setValid(ref valid);
            sending.setSendNum(ref sendnum);
            sending.setActionProfile(ref profile);

            //send it
            try
            {
                if (n_function == UCommon.USENDING_TEST) //test DB and rollback
                {
                    //db.BeginTransaction();
                    //db.RollbackTransaction();
                }
                else
                {
                    b_ret = db.Send(ref sending);
                }
            }
            catch (Exception e)
            {
                setAlertInfo(false, project.sz_projectpk, sending.l_refno, pa.l_alertpk, pa.sz_name, "Could not send due to database error. Aborting...", e.Message, SYSLOG.ALERTINFO_SYSLOG_ERROR);
                return false;
            }
            if (n_function == UCommon.USENDING_TEST) //test DB and rollback
            {
                //db.BeginTransaction();
                //db.RollbackTransaction();
                b_ret = true; //fake link to project
            }
            else
            {
                b_ret = db.linkRefnoToProject(ref project, l_refno, 0, 0);
            }
            if (!b_ret)
            {
                setAlertInfo(false, project.sz_projectpk, sending.l_refno, pa.l_alertpk, pa.sz_name, "Could not link sending to project. Sending will continue", db.getLastError(), SYSLOG.ALERTINFO_SYSLOG_WARNING);
            }
            

            //if all ok, publish addressfile
            try
            {
                if (n_function != UCommon.USENDING_TEST)
                    sending.publishGUIAdrFile();
            }
            catch (Exception e)
            {
                //this is not important for the sending, so continue
                //ULog.warning(sending.l_refno, "Could not publish GUI address file", e.Message);
                setAlertInfo(false, project.sz_projectpk, sending.l_refno, pa.l_alertpk, pa.sz_name, "Could not publish GUI address file. Only required for status view.", e.Message, SYSLOG.ALERTINFO_SYSLOG_WARNING);
            }
            try
            {
                if (b_publish_voice) //requires that no exceptions were caught while writing temp file
                {
                    if (n_function != UCommon.USENDING_TEST)
                    {
                        sending.publishAdrFile();
                    }
                    setAlertInfo(true, project.sz_projectpk, sending.l_refno, pa.l_alertpk, pa.sz_name, "Voice Message " + UCommon.USENDINGTYPE_SENT(n_function) + " [" + pa.getSendingTypeText() + "]", "", SYSLOG.ALERTINFO_SYSLOG_NONE);
                }
            }
            catch (Exception e)
            {
                //ULog.error(sending.l_refno, "Could not publish address file", e.Message);
                setAlertInfo(false, project.sz_projectpk, sending.l_refno, pa.l_alertpk, pa.sz_name, "Could not publish address file. Aborting... [" + pa.getSendingTypeText() + "]", e.Message, SYSLOG.ALERTINFO_SYSLOG_ERROR);
            }
            try
            {
                if (b_publish_lba) //requires that no exceptions were caught while writing temp file
                {
                    if (n_function != UCommon.USENDING_TEST)
                    {
                        if (sending.publishLBAFile())
                            setAlertInfo(true, project.sz_projectpk, sending.l_refno, pa.l_alertpk, pa.sz_name, "Location Based Alert " + UCommon.USENDINGTYPE_SENT(n_function) + " [" + pa.sz_areaid + "]", "", SYSLOG.ALERTINFO_SYSLOG_NONE);
                        else
                            setAlertInfo(true, project.sz_projectpk, sending.l_refno, pa.l_alertpk, pa.sz_name, "No Location Based Alert found", "", SYSLOG.ALERTINFO_SYSLOG_NONE);
                    }
                    else
                    {
                        setAlertInfo(true, project.sz_projectpk, sending.l_refno, pa.l_alertpk, pa.sz_name, "Location Based Alert " + UCommon.USENDINGTYPE_SENT(n_function) + " [" + pa.sz_areaid + "]", "", SYSLOG.ALERTINFO_SYSLOG_NONE);
                    }
                }
            }
            catch (Exception e)
            {
                if(sending.hasLBA())
                {
                    //ULog.error(sending.l_refno, "Could not publish LBA address file", e.Message);
                    setAlertInfo(false, project.sz_projectpk, sending.l_refno, pa.l_alertpk, pa.sz_name, "Could not publish LBA address file.", e.Message, SYSLOG.ALERTINFO_SYSLOG_ERROR);
                }
                else
                {
                    ULog.warning(sending.l_refno, "Could not remove the temporary LBA addressfile", e.Message);
                }
            }

            try
            {
                file.DeleteOperation();
            }
            catch (Exception e)
            {
                ULog.warning(sending.l_refno, String.Format("Could not remove the local temporary alert file\n{0}", file.full()), e.Message);
            }

            ULog.write(String.Format("New <{0}> Sending\nUserID={1}\nDeptID={2}\nCompID={3}\nProject={4}\nRefno={5}\nAlertpk={6}\nEventpk={7})",
                //(f_simulation ? "[Simulated]" : "[Live]"),
                UCommon.USENDINGTYPE(n_function).ToUpper(),
                logoninfo.sz_userid, logoninfo.sz_deptid, logoninfo.sz_compid, project.sz_projectpk, l_refno, pa.l_alertpk, l_fromeventpk));

            return true;
        }

    }
}
