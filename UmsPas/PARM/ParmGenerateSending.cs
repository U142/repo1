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
        protected PercentProgress.SetPercentDelegate percentDelegate;

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
                db = new PASUmsDb(UCommon.UBBDATABASE.sz_dsn, UCommon.UBBDATABASE.sz_uid, UCommon.UBBDATABASE.sz_pwd, 60);
            }
            catch (Exception e)
            {
                setAlertInfo(false, "0", 0, 0, " ", "Could not initialize Database Connection", e.Message, SYSLOG.ALERTINFO_SYSLOG_ERROR);
                return false;
            }

            try
            {
                bool b = false;
                if (logoninfo.l_comppk > 0 && logoninfo.l_deptpk > 0 && logoninfo.l_userpk > 0)
                    b = db.CheckLogon(ref this.logoninfo); //l_userpk, l_comppk, l_deptpk, sz_userid, sz_compid, sz_deptid, sz_password);
                else
                    b = db.CheckLogonLiteral(ref this.logoninfo);
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

        public bool SendMapsending(ref UMAPSENDING sending, int n_function)
        {
            BBPROJECT project = new BBPROJECT();
            project.sz_projectpk = sending.n_projectpk.ToString();
            sending.setFunction(n_function);
            if (sending.n_projectpk <= 0)
            {
                createProject("SendMapsending", 0, ref project, n_function);
            }
            xmlwriter.insertStartElement("project");
            xmlwriter.insertAttribute("l_projectpk", project.sz_projectpk);
            try
            {
                send_adhoc(ref project, ref sending);
            }
            catch (Exception e)
            {

            }

            xmlwriter.insertEndElement();
            return true;
        }

        public bool SendAlert(Int64 l_alertpk, int n_function, string sz_scheddate, string sz_schedtime, PercentProgress.SetPercentDelegate percentDelegate)
        {
            this.percentDelegate = percentDelegate;
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

        public bool SendEvent(Int64 l_eventpk, int n_function, string sz_scheddate, string sz_schedtime, PercentProgress.SetPercentDelegate percentDelegate)
        {
            this.percentDelegate = percentDelegate;
            //get all alerts. send all

            //ArrayList alerts = new ArrayList();
            List<PAALERT> alerts = new List<PAALERT>();
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

        protected bool _sendVoice(ref PAS_SENDING sending)
        {
            return true;
        }
        protected bool _sendSMS(ref SMS_SENDING sending)
        {
            return true;
        }
        protected bool _sendLBA(ref PAS_SENDING sending)
        {
            return true;
        }

        protected bool send_adhoc(ref BBPROJECT project, ref UMAPSENDING sending)
        {
            bool b_test_to_single_recipient = false;
            if (typeof(UTESTSENDING) == sending.GetType())
                b_test_to_single_recipient = true;


            bool b_voice_active = false;
            bool b_sms_active = false;
            bool b_lba_active = false;
            bool b_tas_active = false;
            if (sending.doSendSMS() && ((sending.n_addresstypes & (long)ADRTYPES.FIXED_COMPANY_ALT_SMS) > 0 ||
                (sending.n_addresstypes & (long)ADRTYPES.FIXED_PRIVATE_ALT_SMS) > 0 ||
                (sending.n_addresstypes & (long)ADRTYPES.SMS_COMPANY) > 0 ||
                (sending.n_addresstypes & (long)ADRTYPES.SMS_COMPANY_ALT_FIXED) > 0 ||
                (sending.n_addresstypes & (long)ADRTYPES.SMS_PRIVATE) > 0 ||
                (sending.n_addresstypes & (long)ADRTYPES.SMS_PRIVATE_ALT_FIXED) > 0))
            {
                b_sms_active = true;
            }
            if(sending.doSendVoice() && ((sending.n_addresstypes & (long)ADRTYPES.FIXED_COMPANY_ALT_SMS) > 0 ||
                (sending.n_addresstypes & (long)ADRTYPES.FIXED_PRIVATE_ALT_SMS) > 0 ||

                (sending.n_addresstypes & (long)ADRTYPES.FIXED_COMPANY) > 0 ||
                (sending.n_addresstypes & (long)ADRTYPES.FIXED_PRIVATE) > 0 ||

                (sending.n_addresstypes & (long)ADRTYPES.SMS_COMPANY_ALT_FIXED) > 0 ||
                (sending.n_addresstypes & (long)ADRTYPES.SMS_PRIVATE_ALT_FIXED) > 0 ||

                (sending.n_addresstypes & (long)ADRTYPES.MOBILE_COMPANY) > 0 ||
                (sending.n_addresstypes & (long)ADRTYPES.MOBILE_PRIVATE) > 0 ||

                (sending.n_addresstypes & (long)ADRTYPES.FIXED_COMPANY_AND_MOBILE) > 0 ||
                (sending.n_addresstypes & (long)ADRTYPES.FIXED_PRIVATE_AND_MOBILE) > 0 ||

                (sending.n_addresstypes & (long)ADRTYPES.MOBILE_PRIVATE_AND_FIXED) > 0 ||
                (sending.n_addresstypes & (long)ADRTYPES.MOBILE_COMPANY_AND_FIXED) > 0))
            {
                b_voice_active = true;
            }
            if ((sending.n_addresstypes & (long)ADRTYPES.LBA_TEXT) > 0)
            {
                b_lba_active = true;
            }
            if ((sending.n_addresstypes & (long)ADRTYPES.SENDTO_TAS_SMS) > 0)
            {
                if (b_test_to_single_recipient)
                {
                    b_tas_active = false;
                    b_sms_active = true;
                }
                else
                    b_tas_active = true;
            }


            PAS_SENDING passending = new PAS_SENDING();
            SMS_SENDING smssending = new SMS_SENDING();
            PAS_SENDING lbasending = new PAS_SENDING();
            TAS_SENDING tassending = new TAS_SENDING();
            passending.setSimulation((sending.getFunction() == UCommon.USENDING_SIMULATION ? true : false));
            lbasending.setSimulation((sending.getFunction() == UCommon.USENDING_SIMULATION ? true : false));
            smssending.setSimulation((sending.getFunction() == UCommon.USENDING_SIMULATION ? true : false));
            tassending.setSimulation((sending.getFunction() == UCommon.USENDING_SIMULATION ? true : false));

            smssending.setSmsMessage(sending.sz_sms_message);
            smssending.setSmsOadc(sending.sz_sms_oadc);
            smssending.setExpiryTimeMinutes(sending.n_sms_expirytime_minutes);
            
            smssending.l_resend_refno = passending.l_resend_refno = lbasending.l_resend_refno = sending.n_resend_refno;
            smssending.b_resend = passending.b_resend = lbasending.b_resend = sending.b_resend;

            tassending.setSmsMessage(sending.sz_sms_message);
            tassending.setSmsOadc(sending.sz_sms_oadc);
            tassending.setExpiryTimeMinutes(sending.n_sms_expirytime_minutes);

            tassending.l_resend_refno = passending.l_resend_refno = lbasending.l_resend_refno = sending.n_resend_refno;
            tassending.b_resend = passending.b_resend = lbasending.b_resend = sending.b_resend;

            bool b_publish_voice = false;
            bool b_publish_lba = false;
            bool b_publish_sms = false;
            bool b_publish_tas = false;
            bool b_ret = false;

            if(typeof(UMUNICIPALSENDING).Equals(sending.GetType()))
            {
                
            }

            //create TAS sending if
            if (b_tas_active)
            {
                if (tassending.sz_smsmessage.Length <= 0)
                    throw new UEmptySMSMessageException();
                if (tassending.sz_smsoadc.Length <= 0)
                    throw new UEmptySMSOadcException();

                try
                {
                    MDVSENDINGINFO tassendinginfo = new MDVSENDINGINFO();
                    //tassendinginfo.l_refno = db.newRefno();
                    if (sending.n_refno > 0)
                        tassendinginfo.l_refno = sending.n_refno;
                    else
                        tassendinginfo.l_refno = db.newRefno();
                    tassending.setRefno(tassendinginfo.l_refno, ref project);
                    /*
                     * if this is a resend, then reorganize resend statuscodes
                     * 
                     */
                    tassending.createShape(ref sending);
                    db.FillSendingInfo(ref logoninfo, ref sending, ref tassendinginfo, new UDATETIME(sending.n_scheddate.ToString(), sending.n_schedtime.ToString()));
                    tassending.setSendingInfo(ref tassendinginfo);
                    tassending.m_sendinginfo.l_type = 5;
                    db.Send(ref tassending);
                    b_publish_tas = true;
                }
                catch (Exception e)
                {
                    b_publish_tas = false;
                    setAlertInfo(false, project.sz_projectpk, tassending.m_sendinginfo.l_refno, 0, tassending.m_sendinginfo.sz_sendingname, "Error creating shape file for TAS sending. Aborting...", e.Message, SYSLOG.ALERTINFO_SYSLOG_ERROR);
                }
                /*if (sending.getFunction() == UCommon.USENDING_TEST) //test DB and rollback
                {
                    b_ret = true; //fake link to project
                }
                else
                {
                    int n_linktype = 0;
                    if (tassending.m_sendinginfo.l_group == UShape.SENDINGTYPE_TESTSENDING)
                        n_linktype = 9;
                    b_ret = db.linkRefnoToProject(ref project, tassending.l_refno, n_linktype, (tassending.b_resend ? tassending.l_resend_refno : 0));
                }
                if (!b_ret)
                {
                    setAlertInfo(false, project.sz_projectpk, tassending.l_refno, 0, tassending.m_sendinginfo.sz_sendingname, "Could not link TAS sending to project. Sending will continue", db.getLastError(), SYSLOG.ALERTINFO_SYSLOG_WARNING);
                }*/


            }
            //create SMS sending if 
            if(b_sms_active)
            {
                //This is a sending with possible sms recipients.
                if (smssending.sz_smsmessage.Length <= 0)
                    throw new UEmptySMSMessageException();
                if (smssending.sz_smsoadc.Length <= 0)
                    throw new UEmptySMSOadcException();

                try
                {
                    //fetch a refno for the sms sending
                    //if (smssending.b_resend)
                    //    throw new USendingTypeNotSupportedException("Resend of SMS not yet implemented");
                    MDVSENDINGINFO smssendinginfo = new MDVSENDINGINFO();
                    smssendinginfo.l_refno = db.newRefno();
                    smssending.setRefno(smssendinginfo.l_refno, ref project);
                    /*
                     * if this is a resend, then reorganize resend statuscodes
                     * 
                     */
                    smssending.createShape(ref sending);
                    db.FillSendingInfo(ref logoninfo, ref sending, ref smssendinginfo, new UDATETIME(sending.n_scheddate.ToString(), sending.n_schedtime.ToString()));
                    smssending.setSendingInfo(ref smssendinginfo);
                    db.Send(ref smssending, ref logoninfo);
                    b_publish_sms = true;
                }
                catch (Exception e)
                {
                    b_publish_sms = false;
                    setAlertInfo(false, project.sz_projectpk, smssending.m_sendinginfo.l_refno, 0, smssending.m_sendinginfo.sz_sendingname, "Error creating shape file for SMS sending. Aborting...", e.Message, SYSLOG.ALERTINFO_SYSLOG_ERROR);
                }
                if (sending.getFunction() == UCommon.USENDING_TEST) //test DB and rollback
                {
                    b_ret = true; //fake link to project
                }
                else
                {
                    int n_linktype = 0;
                    if (smssending.m_sendinginfo.l_group == UShape.SENDINGTYPE_TESTSENDING)
                        n_linktype = 9;
                    b_ret = db.linkRefnoToProject(ref project, smssending.l_refno, n_linktype, (smssending.b_resend ? smssending.l_resend_refno : 0));
                }
                if (!b_ret)
                {
                    setAlertInfo(false, project.sz_projectpk, smssending.l_refno, 0, smssending.m_sendinginfo.sz_sendingname, "Could not link SMS sending to project. Sending will continue", db.getLastError(), SYSLOG.ALERTINFO_SYSLOG_WARNING);
                }

            }


            //create voice sending if
            if (b_voice_active || b_lba_active)
            {
                try
                {
                    if (b_voice_active)
                    {
                        //if (passending.l_refno <= 0)
                        //    passending.l_refno = db.newRefno();
                        if (sending.n_refno > 0)
                            passending.l_refno = sending.n_refno;
                        else
                            passending.l_refno = db.newRefno();

                    }
                    if (b_lba_active)
                    {
                        if (lbasending.l_refno <= 0)
                            lbasending.l_refno = db.newRefno();
                        lbasending.setRefno(lbasending.l_refno, ref project);
                    }
                }
                catch(Exception e)
                {
                    setAlertInfo(false, project.sz_projectpk, passending.m_sendinginfo.l_refno, 0, passending.m_sendinginfo.sz_sendingname, "Unable to get a refno", e.Message, SYSLOG.ALERTINFO_SYSLOG_ERROR);
                }
                try
                {
                    if(b_voice_active)
                        db.VerifyProfile(sending.n_profilepk, false);
                }
                catch (Exception e)
                {
                    setAlertInfo(false, project.sz_projectpk, passending.l_refno, 0, sending.sz_sendingname, "Could not verify message profile", e.Message, SYSLOG.ALERTINFO_SYSLOG_ERROR);
                    return false;
                }
                try
                {
                    passending.createShape(ref sending); //will also create a temp address file
                    lbasending.createShape(ref sending);
                    if(b_voice_active)
                        b_publish_voice = true;
                    if (b_lba_active)
                        b_publish_lba = true;
                }
                catch (Exception e)
                {
                    setAlertInfo(false, project.sz_projectpk, passending.l_refno, 0, sending.sz_sendingname, "Error creating shape file for sending. Aborting...", e.Message, SYSLOG.ALERTINFO_SYSLOG_ERROR);
                    //file.DeleteOperation();
                    return false;
                }

                BBRESCHEDPROFILE resched_profile = new BBRESCHEDPROFILE();
                MDVSENDINGINFO sendinginfo = new MDVSENDINGINFO();
                BBVALID valid = new BBVALID();
                BBSENDNUM sendnum = new BBSENDNUM();
                BBACTIONPROFILESEND profile = new BBACTIONPROFILESEND();
                db.FillReschedProfile(sending.n_reschedpk.ToString(), ref resched_profile);
                db.FillValid(sending.n_validity, ref valid);
                db.FillSendNum(sending.oadc.sz_number, ref sendnum);
                db.FillActionProfile(sending.n_profilepk, ref profile);
                try
                {
                    db.FillSendingInfo(ref logoninfo, ref sending, ref sendinginfo, new UDATETIME(sending.n_scheddate.ToString(), sending.n_schedtime.ToString()));
                }
                catch (Exception e)
                {
                    setAlertInfo(false, project.sz_projectpk, passending.l_refno, 0, passending.m_sendinginfo.sz_sendingname, "Could not send due to database error. Aborting...", e.Message, SYSLOG.ALERTINFO_SYSLOG_ERROR);
                    throw e;
                }

                //fill a sending struct
                passending.setSendingInfo(ref sendinginfo);
                passending.setReschedProfile(ref resched_profile);
                passending.setValid(ref valid);
                passending.setSendNum(ref sendnum);
                passending.setActionProfile(ref profile);
                
                lbasending.setSendingInfo(ref sendinginfo);
                lbasending.setReschedProfile(ref resched_profile);
                lbasending.setValid(ref valid);
                lbasending.setSendNum(ref sendnum);
                lbasending.setActionProfile(ref profile);

                
                try
                {
                    if (sending.getFunction() == UCommon.USENDING_TEST) //test DB and rollback
                    {
                    }
                    else
                    {
                        if(b_publish_voice)
                            b_ret = db.Send(ref passending);
                        if (b_publish_lba)
                        {
                            //mark this sending as LBA. 1=voice, 2=sms, 3=mail, 4=lba, 5=tas
                            lbasending.m_sendinginfo.l_type = 4;
                            b_ret = db.Send(ref lbasending);
                        }
                    }
                }
                catch (Exception e)
                {
                    setAlertInfo(false, project.sz_projectpk, passending.l_refno, 0, passending.m_sendinginfo.sz_sendingname, "Could not send due to database error. Aborting...", e.Message, SYSLOG.ALERTINFO_SYSLOG_ERROR);
                    return false;
                }

            }

            try
            {
                if (b_lba_active)
                    b_publish_lba = true;
                if (sending.b_resend && b_publish_lba)
                {
                    setAlertInfo(false, project.sz_projectpk, lbasending.l_refno, 0, lbasending.m_sendinginfo.sz_sendingname, "Resend LBA is not yet supported", "", SYSLOG.ALERTINFO_SYSLOG_WARNING);
                    b_publish_lba = false;
                }
                if (b_publish_lba && sending.m_lba != null)
                {
                    sending.m_lba.Validate();
                    if (sending.m_lba.getValid())
                    {
                        sending.m_lba.setSourceShape(ref lbasending.m_shape);
                        lbasending.setLBAShape(ref logoninfo, ref sending.m_lba, sending.getFunction());
                        b_publish_lba = true;

                    }
                    else
                    {
                        if (!sending.m_lba.getValid())
                            setAlertInfo(false, project.sz_projectpk, lbasending.l_refno, 0, lbasending.m_sendinginfo.sz_sendingname, "An error was found in the Location Based Alert-part", "", SYSLOG.ALERTINFO_SYSLOG_WARNING);
                        b_publish_lba = false;
                        //else if (!sending.m_lba.lba().hasValidAreaID())
                         //   setAlertInfo(false, project.sz_projectpk, sending.l_refno, pa.l_alertpk, pa.sz_name, "This ALERT has not registered a valid AREA-ID from provider", "", SYSLOG.ALERTINFO_SYSLOG_WARNING);
                    }
                }
            }
            catch (Exception e)
            {
                setAlertInfo(false, project.sz_projectpk, lbasending.l_refno, 0, lbasending.m_sendinginfo.sz_sendingname, "Error creating shape file for Location Based Alert", e.Message, SYSLOG.ALERTINFO_SYSLOG_ERROR);
                lbasending.lbacleanup();
            }

            //send it
            if (b_publish_voice)
            {
                if (sending.getFunction() == UCommon.USENDING_TEST) //test DB and rollback
                {
                    //db.BeginTransaction();
                    //db.RollbackTransaction();
                    b_ret = true; //fake link to project
                }
                else
                {
                    int n_linktype = 0;
                    if (passending.m_sendinginfo.l_group == 0)
                        n_linktype = 9; //test sending before real sending
                    b_ret = db.linkRefnoToProject(ref project, passending.l_refno, n_linktype, (passending.b_resend ? passending.l_resend_refno : 0));
                }
                if (!b_ret)
                {
                    setAlertInfo(false, project.sz_projectpk, passending.l_refno, 0, passending.m_sendinginfo.sz_sendingname, "Could not link sending to project. Sending will continue", db.getLastError(), SYSLOG.ALERTINFO_SYSLOG_WARNING);
                }


            }
            if (b_publish_sms)
            {
                try
                {
                    if (sending.getFunction() != UCommon.USENDING_TEST)
                    {
                        smssending.publishAdrFile();
                    }
                    setAlertInfo(true, project.sz_projectpk, smssending.l_refno, 0, smssending.m_sendinginfo.sz_sendingname, "SMS Message " + UCommon.USENDINGTYPE_SENT(sending.getFunction()) + " [" + PAALERT.getSendingTypeText(sending.getGroup()) + "]", "", SYSLOG.ALERTINFO_SYSLOG_NONE);
                }
                catch (Exception e)
                {
                    setAlertInfo(false, project.sz_projectpk, smssending.l_refno, 0, smssending.m_sendinginfo.sz_sendingname, "Could not publish SMS address file. Aborting... [" + PAALERT.getSendingTypeText(sending.getGroup()) + "]", e.Message, SYSLOG.ALERTINFO_SYSLOG_ERROR);
                }
                try
                {
                    if (sending.getFunction() != UCommon.USENDING_TEST)
                        smssending.publishGUIAdrFile();
                }
                catch (Exception e)
                {
                    setAlertInfo(false, project.sz_projectpk, smssending.l_refno, 0, smssending.m_sendinginfo.sz_sendingname, "Could not publish SMS GUI address file. Only required for status view.", e.Message, SYSLOG.ALERTINFO_SYSLOG_WARNING);
                }

            }
            if (b_publish_voice) //requires that no exceptions were caught while writing temp file
            {
                //if all ok, publish addressfile
                try
                {
                    if (sending.getFunction() != UCommon.USENDING_TEST)
                        passending.publishGUIAdrFile();
                }
                catch (Exception e)
                {
                    //this is not important for the sending, so continue
                    //ULog.warning(sending.l_refno, "Could not publish GUI address file", e.Message);
                    setAlertInfo(false, project.sz_projectpk, passending.l_refno, 0, passending.m_sendinginfo.sz_sendingname, "Could not publish GUI address file. Only required for status view.", e.Message, SYSLOG.ALERTINFO_SYSLOG_WARNING);
                }

                try
                {
                    if (sending.getFunction() != UCommon.USENDING_TEST)
                    {
                        passending.publishAdrFile();
                    }
                    setAlertInfo(true, project.sz_projectpk, passending.l_refno, 0, passending.m_sendinginfo.sz_sendingname, "Voice Message " + UCommon.USENDINGTYPE_SENT(sending.getFunction()) + " [" + PAALERT.getSendingTypeText(sending.getGroup()) + "]", "", SYSLOG.ALERTINFO_SYSLOG_NONE);
                }
                catch (Exception e)
                {
                    //ULog.error(sending.l_refno, "Could not publish address file", e.Message);
                    setAlertInfo(false, project.sz_projectpk, passending.l_refno, 0, passending.m_sendinginfo.sz_sendingname, "Could not publish address file. Aborting... [" + PAALERT.getSendingTypeText(sending.getGroup()) + "]", e.Message, SYSLOG.ALERTINFO_SYSLOG_ERROR);
                }
            }
            if (b_publish_tas)
            {
                try
                {
                    if (sending.getFunction() != UCommon.USENDING_TEST)
                        tassending.publishGUIAdrFile();
                }
                catch (Exception e)
                {
                    setAlertInfo(false, project.sz_projectpk, tassending.l_refno, 0, tassending.m_sendinginfo.sz_sendingname, "Could not publish TAS GUI address file. Only required for status view.", e.Message, SYSLOG.ALERTINFO_SYSLOG_WARNING);
                }
                try
                {
                    int n_linktype = 0;
                    if (tassending.m_sendinginfo.l_group == UShape.SENDINGTYPE_TESTSENDING)
                        n_linktype = 9;
                    b_ret = db.linkRefnoToProject(ref project, tassending.l_refno, n_linktype, (tassending.b_resend ? tassending.l_resend_refno : 0));

                    List<Int32> operatorfilter = null;
                    int n_requesttype = 0;
                    db.InsertLBARecord_2_0(-1, tassending.l_refno, 199, -1, -1, -1, 0, n_requesttype, "", "", sending.getFunction(), ref operatorfilter, logoninfo.l_deptpk, 5 /*tas*/);
                    ULocationBasedAlert lbanull = null;
                    tassending.setLBAShape(ref logoninfo, ref lbanull, sending.getFunction());
                    if (tassending.publishLBAFile())
                    {
                        setAlertInfo(true, project.sz_projectpk, tassending.l_refno, 0, tassending.m_sendinginfo.sz_sendingname, "Location Based Alert " + UCommon.USENDINGTYPE_SENT(sending.getFunction()) + " [" + "AdHoc" + "]", "", SYSLOG.ALERTINFO_SYSLOG_NONE);
                    }
                    else
                    {
                        setAlertInfo(true, project.sz_projectpk, tassending.l_refno, 0, tassending.m_sendinginfo.sz_sendingname, "No Location Based Alert found", "", SYSLOG.ALERTINFO_SYSLOG_ERROR);
                        db.SetLBAStatus(lbasending.l_refno, 41100);
                    }
                }
                catch (Exception e)
                {
                    setAlertInfo(true, project.sz_projectpk, tassending.l_refno, 0, tassending.m_sendinginfo.sz_sendingname, "Location Based Alert " + UCommon.USENDINGTYPE_SENT(sending.getFunction()) + " [" + "AdHoc" + "]", "", SYSLOG.ALERTINFO_SYSLOG_NONE);
                }
            }

            try
            {
                if (b_publish_lba) //requires that no exceptions were caught while writing temp file
                {
                    if (sending.getFunction() != UCommon.USENDING_TEST)
                    {
                        try
                        {
                            //if all ok, publish addressfile
                            try
                            {
                                if (sending.getFunction() != UCommon.USENDING_TEST)
                                    lbasending.publishGUIAdrFile();
                            }
                            catch (Exception e)
                            {
                                //this is not important for the sending, so continue
                                //ULog.warning(sending.l_refno, "Could not publish GUI address file", e.Message);
                                setAlertInfo(false, project.sz_projectpk, lbasending.l_refno, 0, lbasending.m_sendinginfo.sz_sendingname, "Could not publish GUI address file. Only required for status view.", e.Message, SYSLOG.ALERTINFO_SYSLOG_WARNING);
                            }

                            int n_linktype = 0;
                            if (lbasending.m_sendinginfo.l_group == UShape.SENDINGTYPE_TESTSENDING)
                                n_linktype = 9;
                            b_ret = db.linkRefnoToProject(ref project, lbasending.l_refno, n_linktype, (lbasending.b_resend ? lbasending.l_resend_refno : 0));

                            List<Int32> operatorfilter = null;
                            db.InsertLBARecord_2_0(-1, lbasending.l_refno, 199, -1, -1, -1, 0, sending.m_lba.getRequestType(), "", "", sending.getFunction(), ref operatorfilter, logoninfo.l_deptpk, 4 /*lbas*/);
                            if (lbasending.publishLBAFile())
                            {
                                setAlertInfo(true, project.sz_projectpk, lbasending.l_refno, 0, lbasending.m_sendinginfo.sz_sendingname, "Location Based Alert " + UCommon.USENDINGTYPE_SENT(sending.getFunction()) + " [" + "AdHoc" + "]", "", SYSLOG.ALERTINFO_SYSLOG_NONE);
                            }
                            else
                            {
                                setAlertInfo(true, project.sz_projectpk, lbasending.l_refno, 0, lbasending.m_sendinginfo.sz_sendingname, "No Location Based Alert found", "", SYSLOG.ALERTINFO_SYSLOG_ERROR);
                                db.SetLBAStatus(lbasending.l_refno, 41100);
                            }
                        }
                        catch (Exception e)
                        {
                            setAlertInfo(true, project.sz_projectpk, lbasending.l_refno, 0, lbasending.m_sendinginfo.sz_sendingname, "Could not insert into LBASEND", e.Message, SYSLOG.ALERTINFO_SYSLOG_ERROR);
                        }
                    }
                    else
                    {
                        setAlertInfo(true, project.sz_projectpk, lbasending.l_refno, 0, lbasending.m_sendinginfo.sz_sendingname, "Location Based Alert " + UCommon.USENDINGTYPE_SENT(sending.getFunction()) + " [" + "AdHoc" + "]", "", SYSLOG.ALERTINFO_SYSLOG_NONE);
                    }
                }
            }
            catch (Exception e)
            {
                if (lbasending.hasLBA())
                {
                    //ULog.error(sending.l_refno, "Could not publish LBA address file", e.Message);
                    setAlertInfo(false, project.sz_projectpk, lbasending.l_refno, 0, lbasending.m_sendinginfo.sz_sendingname, "Could not publish LBA address file.", e.Message, SYSLOG.ALERTINFO_SYSLOG_ERROR);
                }
                else
                {
                    ULog.warning(lbasending.l_refno, "Could not remove the temporary LBA addressfile", e.Message);
                }
            }

            ULog.write(String.Format("New <{0}> Sending\nUserID={1}\nDeptID={2}\nCompID={3}\nProject={4}\nRefno={5}\nAlertpk={6}\nEventpk={7})",
                //(f_simulation ? "[Simulated]" : "[Live]"),
                UCommon.USENDINGTYPE(sending.getFunction()).ToUpper(),
                logoninfo.sz_userid, logoninfo.sz_deptid, logoninfo.sz_compid, project.sz_projectpk, passending.l_refno, 0, 0));



            return true;
        }

        protected bool send_v3(ref BBPROJECT project, Int64 l_fromeventpk, ref PAALERT pa, int n_function,
                            string sz_scheddate, string sz_schedtime)
        {
            bool b_voice_active = false;
            bool b_sms_active = false;
            bool b_lba_active = false;
            if ((pa.l_addresstypes & (long)ADRTYPES.FIXED_COMPANY_ALT_SMS) > 0 ||
                (pa.l_addresstypes & (long)ADRTYPES.FIXED_PRIVATE_ALT_SMS) > 0 ||

                (pa.l_addresstypes & (long)ADRTYPES.FIXED_COMPANY) > 0 ||
                (pa.l_addresstypes & (long)ADRTYPES.FIXED_PRIVATE) > 0 ||

                (pa.l_addresstypes & (long)ADRTYPES.SMS_COMPANY_ALT_FIXED) > 0 ||
                (pa.l_addresstypes & (long)ADRTYPES.SMS_PRIVATE_ALT_FIXED) > 0 ||

                (pa.l_addresstypes & (long)ADRTYPES.MOBILE_COMPANY) > 0 ||
                (pa.l_addresstypes & (long)ADRTYPES.MOBILE_PRIVATE) > 0 ||

                (pa.l_addresstypes & (long)ADRTYPES.FIXED_COMPANY_AND_MOBILE) > 0 ||
                (pa.l_addresstypes & (long)ADRTYPES.FIXED_PRIVATE_AND_MOBILE) > 0 ||

                (pa.l_addresstypes & (long)ADRTYPES.MOBILE_PRIVATE_AND_FIXED) > 0 ||
                (pa.l_addresstypes & (long)ADRTYPES.MOBILE_COMPANY_AND_FIXED) > 0)
            {
                b_voice_active = true;
            }
            if((pa.l_addresstypes & (long)ADRTYPES.LBA_TEXT) > 0)
            {
                b_lba_active = true;
            }
            if ((pa.l_addresstypes & (long)ADRTYPES.FIXED_COMPANY_ALT_SMS) > 0 ||
                (pa.l_addresstypes & (long)ADRTYPES.FIXED_PRIVATE_ALT_SMS) > 0 ||
                (pa.l_addresstypes & (long)ADRTYPES.SMS_COMPANY) > 0 ||
                (pa.l_addresstypes & (long)ADRTYPES.SMS_COMPANY_ALT_FIXED) > 0 ||
                (pa.l_addresstypes & (long)ADRTYPES.SMS_PRIVATE) > 0 ||
                (pa.l_addresstypes & (long)ADRTYPES.SMS_PRIVATE_ALT_FIXED) > 0)
            {
                b_sms_active = true;
            }

            try
            {
                db.VerifyAlert(pa.l_alertpk, ref logoninfo);
                if(b_voice_active)
                    db.VerifyProfile(pa.l_profilepk, true);
            }
            catch (Exception e) //may catch UVerifyAlertException or UProfileNotSupportedException. rethrow
            {
                throw e;
            }
            BBRESCHEDPROFILE resched_profile = new BBRESCHEDPROFILE();
            BBVALID valid = new BBVALID();
            BBSENDNUM sendnum = new BBSENDNUM();
            MDVSENDINGINFO sendinginfo = new MDVSENDINGINFO();
            MDVSENDINGINFO smssendinginfo = new MDVSENDINGINFO();
            MDVSENDINGINFO lbasendinginfo = new MDVSENDINGINFO();

            BBACTIONPROFILESEND profile = new BBACTIONPROFILESEND();

            bool b_ret = false;


            string sz_sendingname = String.Format("{0}", pa.sz_name);

            

            UXmlAlert file = new UXmlAlert(UCommon.UPATHS.sz_path_predefined_areas, String.Format("a{0}.xml", pa.l_alertpk));
            file.SetLogonInfo(ref logoninfo);
            file.setPercentDelegate(percentDelegate);
            file.setJobType(ProgressJobType.PARM_SEND);
            try
            {
                String guid = Guid.NewGuid().ToString();//new Guid().ToString();
                file.load(String.Format("{0}.{1}", project.sz_projectpk, guid));
            }
            catch (UFileCopyException e)
            {
                //we don't have a local copy
                setAlertInfo(false, project.sz_projectpk, 0, pa.l_alertpk, pa.sz_name, "Could not copy remote file to a local copy", e.Message, SYSLOG.ALERTINFO_SYSLOG_ERROR);
                return false;
            }
            catch (UXmlParseException e)
            {
                //the file has been copied to a local drive, delete it
                setAlertInfo(false, project.sz_projectpk, 0, pa.l_alertpk, pa.sz_name, "Could not parse XML file", e.Message, SYSLOG.ALERTINFO_SYSLOG_ERROR);
                file.DeleteOperation();
                return false;
            }

            pa.setSendingType(file.getSendingType());
            pa.setShape(file.getShape());
            pa.setLBAShape(file.getLBAShape());
            pa.n_function = n_function;


            PAS_SENDING sending = new PAS_SENDING();
            SMS_SENDING smssending = new SMS_SENDING();
            PAS_SENDING lbasending = new PAS_SENDING();
            sending.setSimulation((n_function == UCommon.USENDING_SIMULATION ? true : false));
            smssending.setSimulation((n_function == UCommon.USENDING_SIMULATION ? true : false));
            lbasending.setSimulation((n_function == UCommon.USENDING_SIMULATION ? true : false));


            

            bool b_publish_voice = false;
            bool b_publish_lba = false;
            bool b_publish_sms = false;
            //if VOICE
            //create voice sending if
            if (b_voice_active || b_lba_active)
            {
                try
                {
                    long l_refno = 0;
                    long l_lba_refno = 0;
                    //If this is only a test, don't waste a refno
                    if (n_function != UCommon.USENDING_TEST)
                    {
                        try
                        {
                            if (b_voice_active)
                                l_refno = db.newRefno();
                            if (b_lba_active)
                                l_lba_refno = db.newRefno();
                        }
                        catch (Exception)
                        {
                            throw new URefnoException();
                        }
                    }
                    sending.setRefno(l_refno, ref project);
                    lbasending.setRefno(l_lba_refno, ref project);
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

                    lbasending.setSendingInfo(ref sendinginfo);
                    lbasending.setReschedProfile(ref resched_profile);
                    lbasending.setValid(ref valid);
                    lbasending.setSendNum(ref sendnum);
                    lbasending.setActionProfile(ref profile);

                    if(b_voice_active)
                        sending.setShape(ref pa.m_shape); //will also create a temp address file
                    if(b_lba_active)
                        lbasending.setShape(ref pa.m_shape);

                    if(b_voice_active)
                        b_publish_voice = true;
                    if (b_lba_active)
                        b_publish_lba = true;
                    try
                    {
                        if (n_function == UCommon.USENDING_TEST) //test DB and rollback
                        {
                        }
                        else
                        {
                            if(b_publish_voice)
                                b_ret = db.Send(ref sending);
                            if (b_publish_lba)
                            {
                                //mark sendinginfo as type LBA
                                lbasending.m_sendinginfo.l_type = 4;
                                b_ret = db.Send(ref lbasending);
                            }

                        }
                    }
                    catch (Exception e)
                    {
                        setAlertInfo(false, project.sz_projectpk, sending.l_refno, pa.l_alertpk, pa.sz_name, "Could not send due to database error. Aborting...", e.Message, SYSLOG.ALERTINFO_SYSLOG_ERROR);
                        return false;
                    }

                }
                catch (Exception e)
                {
                    setAlertInfo(false, project.sz_projectpk, sending.l_refno, pa.l_alertpk, pa.sz_name, "Error creating shape file for sending. Aborting...", e.Message, SYSLOG.ALERTINFO_SYSLOG_ERROR);
                    file.DeleteOperation();
                    return false;
                }
            }
            //if SMS
            if (b_sms_active)
            {
                try
                {
                    smssending.sz_smsmessage = pa.sz_sms_message;
                    smssending.sz_smsoadc = pa.sz_sms_oadc;
                    //This is a sending with possible sms recipients.
                    if (smssending.sz_smsmessage.Length <= 0)
                        throw new UEmptySMSMessageException();
                    if (smssending.sz_smsoadc.Length <= 0)
                        throw new UEmptySMSOadcException();

                    if (n_function != UCommon.USENDING_TEST)
                    {
                        try
                        {
                            smssendinginfo.l_refno = db.newRefno();
                        }
                        catch (Exception)
                        {
                            throw new URefnoException();
                        }
                    }
                    smssending.setRefno(smssendinginfo.l_refno, ref project);
                    smssending.setShape(ref pa.m_shape);
                    //smssending.setRefno(db.newRefno(), ref project);
                    db.FillSendingInfo(ref logoninfo, ref pa, ref smssendinginfo, new UDATETIME(sz_scheddate, sz_schedtime), sz_sendingname);
                    smssending.setSendingInfo(ref smssendinginfo);
                    //db.Send(ref smssending, ref logoninfo);
                    if (n_function == UCommon.USENDING_TEST) //test DB and rollback
                    {
                    }
                    else
                    {
                        b_ret = db.Send(ref smssending, ref logoninfo);
                    }
                    b_publish_sms = true;
                }
                catch (Exception e)
                {
                    setAlertInfo(false, project.sz_projectpk, sending.l_refno, pa.l_alertpk, pa.sz_name, "Error creating shape file for SMS sending. Aborting...", e.Message, SYSLOG.ALERTINFO_SYSLOG_ERROR);
                    file.DeleteOperation();
                    return false;
                }
            }
            //if LBA
            if (b_lba_active)
            {
                try
                {
                    if (pa.m_lba_shape != null)
                    {
                        if (pa.m_lba_shape.lba().getValid() && pa.hasValidAreaID())
                        {
                            lbasending.setLBAShape(ref logoninfo, ref pa, ref pa.m_lba_shape, n_function);
                            b_publish_lba = true;

                        }
                        else
                        {
                            if (!pa.m_lba_shape.lba().getValid())
                                setAlertInfo(false, project.sz_projectpk, lbasending.l_refno, pa.l_alertpk, pa.sz_name, "An error was found in the Location Based Alert-part", "", SYSLOG.ALERTINFO_SYSLOG_WARNING);
                            else if (!pa.hasValidAreaID())
                                setAlertInfo(false, project.sz_projectpk, lbasending.l_refno, pa.l_alertpk, pa.sz_name, "This ALERT has not registered a valid AREA-ID from provider", "", SYSLOG.ALERTINFO_SYSLOG_WARNING);
                        }
                    }
                }
                catch (Exception e)
                {
                    setAlertInfo(false, project.sz_projectpk, lbasending.l_refno, pa.l_alertpk, pa.sz_name, "Error creating shape file for Location Based Alert", e.Message, SYSLOG.ALERTINFO_SYSLOG_ERROR);
                    lbasending.lbacleanup();
                }
            }
            if(typeof(UGIS).Equals(sending.m_shape.GetType()))
            {
                setAlertInfo(true, "", 0, pa.l_alertpk, pa.sz_name, "Imported file: " + file.getShape().gis().GetLineCount() + " lines / " + sending.m_shape.gis().GetInabitantCount() + " inhabitants", "", SYSLOG.ALERTINFO_SYSLOG_NONE);
            }
            
            //send it
            if (b_publish_voice)
            {
                if (n_function == UCommon.USENDING_TEST) //test DB and rollback
                {
                    b_ret = true; //fake link to project
                }
                else
                {
                    b_ret = db.linkRefnoToProject(ref project, sending.l_refno, 0, 0);
                }
                if (!b_ret)
                {
                    setAlertInfo(false, project.sz_projectpk, sending.l_refno, pa.l_alertpk, pa.sz_name, "Could not link [VOICE] sending to project. Sending will continue", db.getLastError(), SYSLOG.ALERTINFO_SYSLOG_WARNING);
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
                    setAlertInfo(false, project.sz_projectpk, sending.l_refno, pa.l_alertpk, pa.sz_name, "Could not publish [VOICE] GUI address file (VOICE). Only required for status view.", e.Message, SYSLOG.ALERTINFO_SYSLOG_WARNING);
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
                    setAlertInfo(false, project.sz_projectpk, sending.l_refno, pa.l_alertpk, pa.sz_name, "Could not publish [VOICE] address file. Aborting... [" + pa.getSendingTypeText() + "]", e.Message, SYSLOG.ALERTINFO_SYSLOG_ERROR);
                }
            }
            if (b_publish_sms)
            {
                if (n_function == UCommon.USENDING_TEST) //test DB and rollback
                {
                    b_ret = true; //fake link to project
                }
                else
                {
                    b_ret = db.linkRefnoToProject(ref project, smssending.l_refno, 0, 0);
                }
                if (!b_ret)
                {
                    setAlertInfo(false, project.sz_projectpk, smssending.l_refno, pa.l_alertpk, pa.sz_name, "Could not link [SMS] sending to project. Sending will continue", db.getLastError(), SYSLOG.ALERTINFO_SYSLOG_WARNING);
                }

                try
                {
                    if (n_function != UCommon.USENDING_TEST)
                        smssending.publishGUIAdrFile();
                }
                catch (Exception e)
                {
                    //this is not important for the sending, so continue
                    setAlertInfo(false, project.sz_projectpk, smssending.l_refno, pa.l_alertpk, pa.sz_name, "Could not publish [SMS] GUI address file. Only required for status view.", e.Message, SYSLOG.ALERTINFO_SYSLOG_WARNING);
                }
                try
                {
                    if (b_publish_sms) //requires that no exceptions were caught while writing temp file
                    {
                        if (n_function != UCommon.USENDING_TEST)
                        {
                            smssending.publishAdrFile();
                        }
                        setAlertInfo(true, project.sz_projectpk, smssending.l_refno, pa.l_alertpk, pa.sz_name, "SMS Message " + UCommon.USENDINGTYPE_SENT(n_function) + " [" + pa.getSendingTypeText() + "]", "", SYSLOG.ALERTINFO_SYSLOG_NONE);
                    }
                }
                catch (Exception e)
                {
                    setAlertInfo(false, project.sz_projectpk, sending.l_refno, pa.l_alertpk, pa.sz_name, "Could not publish [SMS] address file (VOICE). Aborting... [" + pa.getSendingTypeText() + "]", e.Message, SYSLOG.ALERTINFO_SYSLOG_ERROR);
                }

            }
            

            try
            {
                if (b_publish_lba) //requires that no exceptions were caught while writing temp file
                {
                    if (n_function != UCommon.USENDING_TEST)
                    {
                        try
                        {
                            try
                            {
                                if (n_function != UCommon.USENDING_TEST)
                                    lbasending.publishGUIAdrFile();
                            }
                            catch (Exception e)
                            {
                                //this is not important for the sending, so continue
                                setAlertInfo(false, project.sz_projectpk, lbasending.l_refno, pa.l_alertpk, pa.sz_name, "Could not publish [SMS] GUI address file. Only required for status view.", e.Message, SYSLOG.ALERTINFO_SYSLOG_WARNING);
                            }

                            b_ret = db.linkRefnoToProject(ref project, lbasending.l_refno, 0, 0);
                            List<Int32> operatorfilter = null;
                            db.InsertLBARecord_2_0(pa.l_alertpk, lbasending.l_refno, 199, -1, -1, -1, 0, pa.n_requesttype, "", pa.sz_areaid, n_function, ref operatorfilter, logoninfo.l_deptpk, 4 /*lbas*/);
                            if (lbasending.publishLBAFile())
                            {
                                setAlertInfo(true, project.sz_projectpk, lbasending.l_refno, pa.l_alertpk, pa.sz_name, "Location Based Alert " + UCommon.USENDINGTYPE_SENT(n_function) + " [" + pa.sz_areaid + "]", "", SYSLOG.ALERTINFO_SYSLOG_NONE);
                            }
                            else
                            {
                                setAlertInfo(true, project.sz_projectpk, lbasending.l_refno, pa.l_alertpk, pa.sz_name, "No Location Based Alert found", "", SYSLOG.ALERTINFO_SYSLOG_ERROR);
                                db.SetLBAStatus(sending.l_refno, 41100);
                            }
                        }
                        catch(Exception e)
                        {
                            setAlertInfo(true, project.sz_projectpk, lbasending.l_refno, pa.l_alertpk, pa.sz_name, "Could not insert into LBASEND", e.Message, SYSLOG.ALERTINFO_SYSLOG_ERROR);
                        }
                    }
                    else
                    {
                        setAlertInfo(true, project.sz_projectpk, lbasending.l_refno, pa.l_alertpk, pa.sz_name, "Location Based Alert " + UCommon.USENDINGTYPE_SENT(n_function) + " [" + pa.sz_areaid + "]", "", SYSLOG.ALERTINFO_SYSLOG_NONE);
                    }
                }
            }
            catch (Exception e)
            {
                if(sending.hasLBA())
                {
                    //ULog.error(sending.l_refno, "Could not publish LBA address file", e.Message);
                    setAlertInfo(false, project.sz_projectpk, lbasending.l_refno, pa.l_alertpk, pa.sz_name, "Could not publish LBA address file.", e.Message, SYSLOG.ALERTINFO_SYSLOG_ERROR);
                }
                else
                {
                    ULog.warning(lbasending.l_refno, "Could not remove the temporary LBA addressfile", e.Message);
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

            if (b_publish_voice)
            {
                ULog.write(String.Format("New <{0}> Voice-Sending\nUserID={1}\nDeptID={2}\nCompID={3}\nProject={4}\nRefno={5}\nAlertpk={6}\nEventpk={7})",
                    //(f_simulation ? "[Simulated]" : "[Live]"),
                    UCommon.USENDINGTYPE(n_function).ToUpper(),
                    logoninfo.sz_userid, logoninfo.sz_deptid, logoninfo.sz_compid, project.sz_projectpk, sending.l_refno, pa.l_alertpk, l_fromeventpk));
            }
            if (b_publish_lba)
            {
                ULog.write(String.Format("New <{0}> LBA-Sending\nUserID={1}\nDeptID={2}\nCompID={3}\nProject={4}\nRefno={5}\nAlertpk={6}\nEventpk={7})",
                    //(f_simulation ? "[Simulated]" : "[Live]"),
                    UCommon.USENDINGTYPE(n_function).ToUpper(),
                    logoninfo.sz_userid, logoninfo.sz_deptid, logoninfo.sz_compid, project.sz_projectpk, lbasending.l_refno, pa.l_alertpk, l_fromeventpk));
            }
            if (b_publish_sms)
            {
                ULog.write(String.Format("New <{0}> SMS-Sending\nUserID={1}\nDeptID={2}\nCompID={3}\nProject={4}\nRefno={5}\nAlertpk={6}\nEventpk={7})",
                    //(f_simulation ? "[Simulated]" : "[Live]"),
                    UCommon.USENDINGTYPE(n_function).ToUpper(),
                    logoninfo.sz_userid, logoninfo.sz_deptid, logoninfo.sz_compid, project.sz_projectpk, smssending.l_refno, pa.l_alertpk, l_fromeventpk));
            }

            return true;
        }


        protected bool send(ref BBPROJECT project, Int64 l_fromeventpk, ref PAALERT pa, int n_function,
                            string sz_scheddate, string sz_schedtime)
        {
            //if n_function = UCommon.USENDING_TEST then nothing should be inserted into DB and address files should not be published

            try
            {
                db.VerifyAlert(pa.l_alertpk, ref logoninfo);
                db.VerifyProfile(pa.l_profilepk, true);
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
