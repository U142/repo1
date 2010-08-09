﻿using System;
using System.Xml;
using System.Xml.Serialization;
using System.Collections.Generic;
using com.ums.UmsCommon;
using com.ums.UmsParm;
using com.ums.PAS.Project;
using System.IO;

namespace com.ums.PAS.CB
{
    public class CBGenerateSending
    {
        protected CB_OPERATION_BASE operation;
        protected ULOGONINFO logon;

        public CBGenerateSending(ref ULOGONINFO l, ref CB_OPERATION_BASE sending)
        {
            this.operation = sending;
            this.logon = l;
            //fill logonstuff into sending
            sending.setComppk(l.l_comppk);
            sending.setDeptpk(l.l_deptpk);
            sending.setUserpk(l.l_userpk);

        }

        /**
         * Send the message. Get refno, Insert into DB, then Serialize
         */
        public CB_SENDING_RESPONSE Send()
        {
            CB_SEND_BASE alert = (CB_SEND_BASE)operation;

            //verify class
            if (alert.GetType().Equals(typeof(CB_ALERT_POLYGON)))
            {
            }
            else if(alert.GetType().Equals(typeof(CB_ALERT_KILL)))
            {
                throw new USendingTypeNotSupportedException(alert.GetType().ToString());
            }
            else if (alert.GetType().Equals(typeof(CB_ALERT_PLMN)))
            {
                throw new USendingTypeNotSupportedException(alert.GetType().ToString());
            }

            CB_SENDING_RESPONSE response = new CB_SENDING_RESPONSE();
            PASUmsDb db = new PASUmsDb();
            
            //fetch refno
            alert.setRefno(db.newRefno());
            
            BBPROJECT project = new BBPROJECT();

            //Create a new project if not specified
            try
            {
                if (alert.l_projectpk <= 0)
                {
                    UProject pr = new UProject();
                    UPROJECT_REQUEST project_request = new UPROJECT_REQUEST();
                    project_request.sz_name = alert.sz_projectname;

                    UPROJECT_RESPONSE resp = pr.uproject(ref logon, ref project_request);
                    project.sz_projectpk = resp.n_projectpk.ToString();
                    alert.l_projectpk = resp.n_projectpk;
                }
                else
                    project.sz_projectpk = alert.l_projectpk.ToString();
            }
            catch (Exception e)
            {
                throw e;
            }
            //attach refno to project
            db.linkRefnoToProject(ref project, alert.l_refno, 0, 0);

            //retrieve message object from send-object
            CB_MESSAGE message = alert.textmessages.list[0];
            
            //insert records into LBASEND
            List<Int32> operatorfilter = null;
            db.InsertLBARecord_2_0(-1, alert.l_refno, 
                                199, -1, -1, -1, 0, 1, "", "", 0,
                                ref operatorfilter, logon.l_deptpk, (int)LBA_SENDINGTYPES.CELLBROADCAST);

            //TODO: insert record into MDVSENDINGINFO
            MDVSENDINGINFO mdv = new MDVSENDINGINFO();
            mdv.sz_messagetext = message.sz_text;
            mdv.sz_oadc = alert.sz_sender;
            mdv.sz_sendingname = "Alert " + alert.l_refno;
            mdv.l_scheddate = "0";
            mdv.l_schedtime = "0";
            mdv.l_createdate = "0";
            mdv.l_createtime = "0";
            mdv.l_group = 2; //POLYGON
            mdv.l_deptpk = alert.l_deptpk;
            mdv.l_type = (int)LBA_SENDINGTYPES.CELLBROADCAST;
            PAS_SENDING ps = new PAS_SENDING();
            ps.m_sendinginfo = mdv;
            ps.l_refno = alert.l_refno;
            db.InsertMDVSENDINGINFO(ref ps);

            // Insert record into LBASEND_TEXT_CC
            db.insertLBATEXTCC(alert.l_refno, message.sz_text, message.l_cbchannel);

            //Insert language into database. Defaults to one language pr sending
            ULocationBasedAlert loc = new ULocationBasedAlert();
            loc.addLanguage("Channel " + message.l_cbchannel, alert.sz_sender, "0", message.sz_text);
            db.InjectLBALanguages(alert.l_refno, ref loc);

            //Save shape to PASHAPES for status lookup
            CB_ALERT_POLYGON poly = (CB_ALERT_POLYGON)alert;
            String polyxml = "", md5 = "";
            //poly.shape.CreateXml(ref polyxml, ref md5);
            poly.l_validity = db.getCBDuration(alert.l_deptpk, alert.l_refno);

            String xml = poly.shape.Serialize();
            bool changed = false;
            db.UpdatePAShape(poly.l_refno, xml, PASHAPETYPES.PASENDING, ref changed);


            //CREATE SEND XML FILE
            // set status 199 and move to eat
            db.updateStatus(alert.l_refno, 199);
            alert.Serialize(UCommon.UPATHS.sz_path_cb);
            //alert.Serialize("s:\\temp\\", "CB_test.xml");

            response.l_code = 0;
            response.l_projectpk = alert.l_projectpk;
            response.l_refno = alert.l_refno;
            response.l_timestamp = db.getDbClock();

            db.close();
            return response;
        }

        /**
         * Kill the message. Update DB, then Serialize
         */
        public CB_SENDING_RESPONSE KillSending()
        {
            //PASUmsDb db = new PASUmsDb();
            CB_ALERT_KILL kill = (CB_ALERT_KILL)operation;
            kill.Serialize(UCommon.UPATHS.sz_path_cb);
            return new CB_SENDING_RESPONSE();
        }
    }
}