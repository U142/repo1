using System;
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


            //link the sending to the department selected by the user

            //verify class
            if (operation.GetType().Equals(typeof(CB_ALERT_POLYGON)))
            {
            }
            else if (operation.GetType().Equals(typeof(CB_ALERT_KILL)))
            {
                //throw new USendingTypeNotSupportedException(alert.GetType().ToString());
                return KillSending();
            }
            else if (operation.GetType().Equals(typeof(CB_ALERT_PLMN)))
            {
                //throw new USendingTypeNotSupportedException(alert.GetType().ToString());
            }
            CB_SEND_BASE alert = (CB_SEND_BASE)operation;

            bool SIMULATION = alert.f_simulation;

            CB_SENDING_RESPONSE response = new CB_SENDING_RESPONSE();
            PASUmsDb db = new PASUmsDb();
            
            //fetch refno
            alert.setRefno(db.newRefno());
            
            BBPROJECT project = new BBPROJECT();

            //check that there's assigned a CB channel for this company
            //in the first version, only one channel is used pr company. 
            //May be changed to be one channel per language
            try
            {
                int n_cb_channel = db.getLBAChannelByComppk(operation.l_refno, alert.l_deptpk, logon.l_comppk);
                for(int i=0; i < alert.textmessages.list.Count; i++)
                {
                    alert.textmessages.list[i].l_cbchannel = n_cb_channel;
                }
            }
            catch (Exception e)
            {
                ULog.error(alert.l_refno, e.Message);
                throw;
            }

            List<int> operators = db.GetCBOperatorsForSendByComp(alert.l_comppk);
            if (operators.Count <= 0)
            {
                UNoAccessOperatorsException ex = new UNoAccessOperatorsException();
                ULog.error(alert.l_refno, ex.Message);
                throw ex;
            }


            //Create a new project if not specified
            bool b_project_is_valid = true;
            try
            {
                if (alert.l_projectpk <= 0)
                {
                    UProject pr = new UProject();
                    UPROJECT_REQUEST project_request = new UPROJECT_REQUEST();
                    project_request.sz_name = alert.sz_projectname;
                    project_request.sz_name = project_request.sz_name.Replace("'", "''");

                    UPROJECT_RESPONSE resp = pr.uproject(ref logon, ref project_request);
                    project.sz_projectpk = resp.n_projectpk.ToString();
                    alert.l_projectpk = resp.n_projectpk;
                    b_project_is_valid = true;
                }
                else
                {
                    project.sz_projectpk = alert.l_projectpk.ToString();

                    //check if project is valid and user may access it
                    db.ProjectIsValid(alert.l_projectpk, ref logon);
                }
            }
            catch (Exception e)
            {
                ULog.error(alert.l_refno, e.Message);
                throw;
            }
            //attach refno to project
            try
            {
                db.linkRefnoToProject(ref project, alert.l_refno, 0, alert.l_parent_refno);
            }
            catch (Exception e)
            {
                ULog.error(alert.l_refno, e.Message);
                throw;
            }

            //retrieve message object from send-object
            CB_MESSAGE message = alert.textmessages.list[0];
            //message.sz_text = message.sz_text.Replace("'", "''");
            
            //insert records into LBASEND
            try
            {
                List<Int32> operatorfilter = null;
                db.InsertLBARecordCB(-1, alert.l_refno,
                    199, -1, -1, -1, 0, 1, "", "", (alert.f_simulation ? 1 : 0),
                                    ref operatorfilter, alert.l_deptpk, (int)LBA_SENDINGTYPES.CELLBROADCAST);
            }
            catch (Exception e)
            {
                ULog.error(alert.l_refno, e.Message);
                throw;
            }

            MDVSENDINGINFO mdv = new MDVSENDINGINFO();
            mdv.sz_messagetext = message.sz_text.Replace("'", "''");
            mdv.sz_oadc = alert.sender.sz_name.Replace("'", "''");
            mdv.sz_sendingname = "Alert " + alert.l_refno;
            mdv.l_scheddate = "0";
            mdv.l_schedtime = "0";
            mdv.l_createdate = "0";
            mdv.l_createtime = "0";
            mdv.l_group = (long)alert.mdvgroup; //POLYGON
            mdv.l_deptpk = alert.l_deptpk;
            mdv.l_companypk = logon.l_comppk;
            mdv.l_userpk = logon.l_userpk;
            mdv.l_type = (long)LBA_SENDINGTYPES.CELLBROADCAST;
            PAS_SENDING ps = new PAS_SENDING();
            ps.m_sendinginfo = mdv;
            ps.l_refno = alert.l_refno;
            try
            {
                db.InsertMDVSENDINGINFO(ref ps);
            }
            catch (Exception e)
            {
                ULog.error(alert.l_refno, e.Message);
            }

            //Insert record into LBASEND_TEXT_CC
            //removed, will be inserted in loc.addLanguage
            //db.insertLBATEXTCC(alert.l_refno, message.sz_text, message.l_cbchannel);

            //Insert language into database. Defaults to one language pr sending
            ULocationBasedAlert loc = new ULocationBasedAlert();
            ULocationBasedAlert.LBALanguage lang = loc.addLanguage("Channel " + message.l_cbchannel, alert.sender.sz_name, "0", message.sz_text);
            lang.AddCCode(message.l_cbchannel.ToString());

            try
            {
                db.InjectLBALanguages(alert.l_refno, ref loc);
            }
            catch (Exception e)
            {
                ULog.warning(alert.l_refno, e.Message);
            }

            try
            {
                alert.l_validity = db.getCBDuration(logon.l_comppk, alert.l_deptpk, alert.l_refno);
            }
            catch (Exception)
            {
                throw;
            }

            //Save shape to PASHAPES for status lookup
            if (alert.GetType().Equals(typeof(CB_ALERT_POLYGON)))
            {
                CB_ALERT_POLYGON poly = (CB_ALERT_POLYGON)alert;
                String xml = poly.shape.Serialize();
                bool changed = false;
                db.UpdatePAShape(alert.l_refno, xml, PASHAPETYPES.PASENDING, ref changed);
            }
            else if (alert.GetType().Equals(typeof(CB_ALERT_PLMN)))
            {
                CB_ALERT_PLMN plmn = (CB_ALERT_PLMN)alert;
                plmn.shape.Serialize();
                String xml = plmn.shape.Serialize();
                bool changed = false;
                db.UpdatePAShape(alert.l_refno, xml, PASHAPETYPES.PASENDING, ref changed);
            }

            //Insert messageparts for this sending (RRO and the messagepart)
            db.insertLBASENDMessageField(alert.l_refno, alert.messagepart);
            db.insertLBASENDMessageField(alert.l_refno, alert.risk);
            db.insertLBASENDMessageField(alert.l_refno, alert.reaction);
            db.insertLBASENDMessageField(alert.l_refno, alert.originator);
            db.insertLBASENDMessageField(alert.l_refno, alert.sender);
            db.insertLBASENDMessageField(alert.l_refno, alert.messageconfirmation);


            //CREATE SEND XML FILE
            // set status 199 and move to eat
            db.updateStatus(alert.l_refno, 199);

            String szLog = "";
            if(SIMULATION)
                szLog+="[SIMULATED] ";
            else
                szLog+="[LIVE] ";
            switch (alert.mdvgroup)
            {
                case MDVSENDINGINFO_GROUP.MAP_CB_NATIONAL:
                    szLog += "[NATIONAL]";
                    break;
                case MDVSENDINGINFO_GROUP.MAP_POLYGON:
                    szLog += "[POLYGON]";
                    break;
                case MDVSENDINGINFO_GROUP.MAP_POLYGONAL_ELLIPSE:
                    szLog += "[ELLIPSE]";
                    break;
                default:
                    szLog += "[UNKNOWN SHAPETYPE]";
                    break;
            }
            szLog += " CB-sending sent by userpk=" + alert.l_userpk + " on behalf of deptpk=" + alert.l_deptpk;

            if (!SIMULATION)
            {
                try
                {
                    alert.Serialize(UCommon.UPATHS.sz_path_cb);
                }
                catch (Exception)
                {
                    throw;
                }
            }
            else
            {
                try
                {
                    doSimulateCBSending(ref db, ref alert, ref operators);
                }
                catch (Exception)
                {
                    throw;
                }
            }
            ULog.write(alert.l_refno, szLog);
            //alert.Serialize("s:\\temp\\", "CB_test.xml");

            response.l_code = 0;
            response.l_projectpk = alert.l_projectpk;
            response.l_refno = alert.l_refno;
            response.l_timestamp = db.getDbClock();

            db.close();
            return response;
        }

        protected bool doSimulateCBSending(ref PASUmsDb db, ref CB_SEND_BASE s, ref List<int> operators)
        {
            try
            {
                /*
                 * 	@l_refno int, 
	                @l_operator int, 
	                @l_2gtotal int,
	                @l_2gok int, 
	                @l_3gtotal int,
	                @l_3gok int,
	                @l_4gtotal int,
	                @l_4gok int,
	                @l_successpercentage float
                 */
                int maxcells2g = 1000;
                int maxcells3g = 1000;
                int maxcells4g = 1000;


                for (int i = 0; i < operators.Count; i++)
                {
                    Random rand = new Random();
                    int l_2gtotal = rand.Next(maxcells2g);
                    int l_2gok = rand.Next(l_2gtotal);
                    int l_3gtotal = rand.Next(maxcells3g);
                    int l_3gok = rand.Next(l_3gtotal);
                    int l_4gtotal = rand.Next(maxcells4g);
                    int l_4gok = rand.Next(l_4gtotal);
                    float l_successpercentage = (float)(l_2gok + l_3gok + l_4gok) / (float)(l_2gtotal + l_3gtotal + l_4gtotal);

                    
                    int n_operator = operators[i];
                    db.updateCellHist(s.l_refno, n_operator,
                                        l_2gtotal, l_2gok,
                                        l_3gtotal, l_3gok,
                                        l_4gtotal, l_4gok,
                                        (int)l_successpercentage,
                                        0);
                    db.updateStatusForOperator(s.l_refno, 1000, n_operator);
                }

                return true;
            }
            catch (Exception e)
            {
                throw new UCBSimulationException(e);
            }
        }

        protected List<ULBASENDING> _getOperatorsThatCanBeKilled(ref PASUmsDb db)
        {
            List<ULBASENDING> ret = new List<ULBASENDING>();
            List<ULBASENDING> sendings = new List<ULBASENDING>();
            db.GetLbaSendsByRefno(operation.l_refno, ref sendings);
            for (int i = 0; i < sendings.Count; i++)
            {
                ULBASENDING s = sendings[i];
                ULBAOPERATORSTATE operatorstatus = ULBAOPERATORSTATE.INITIALIZING;
                switch (s.l_status)
                {
                    case (int)ULBASTATUSCODES.LBASTATUS_INITED:
		            case (int)ULBASTATUSCODES.LBASTATUS_SENT_TO_LBA:
		            case (int)ULBASTATUSCODES.LBASTATUS_PARSING_LBAS:
		            case (int)ULBASTATUSCODES.LBASTATUS_PARSING_LBAS_FAILED_TO_SEND:
		            case (int)ULBASTATUSCODES.CBSTATUS_PREPARING_CELLVISION:
		            case (int)ULBASTATUSCODES.CBSTATUS_PROCESSING_SUBSCRIBERS_CELLVISION:
		            case (int)ULBASTATUSCODES.CBSTATUS_PREPARED_CELLVISION:
		            case (int)ULBASTATUSCODES.CBSTATUS_PREPARED_CELLVISION_COUNT_COMPLETE:
		            case (int)ULBASTATUSCODES.CBSTATUS_CONFIRMED_BY_USER:
		            case (int)ULBASTATUSCODES.CBSTATUS_SENDING:
		            case (int)ULBASTATUSCODES.CBSTATUS_PAUSED:
			            operatorstatus = ULBAOPERATORSTATE.ACTIVE;
			            break;
		            case (int)ULBASTATUSCODES.CBSTATUS_CANCELLED_BY_USER:
		            case (int)ULBASTATUSCODES.LBASTATUS_CANCEL_IN_PROGRESS:
			            operatorstatus = ULBAOPERATORSTATE.KILLING;
			            break;
            			
		            case (int)ULBASTATUSCODES.LBASTATUS_FINISHED:
		            case (int)ULBASTATUSCODES.LBASTATUS_CANCELLED:
		            case (int)ULBASTATUSCODES.LBASTATUS_CANCELLED_BY_USER_OR_SYSTEM:
			            operatorstatus = ULBAOPERATORSTATE.FINISHED;
			            break;
		            case (int)ULBASTATUSCODES.LBASTATUS_GENERAL_ERROR:
			            operatorstatus = ULBAOPERATORSTATE.ERROR;
			            break;
                }
                if (s.l_status >= 40000)
                    operatorstatus = ULBAOPERATORSTATE.ERROR;

                //check if we have a kill
                switch (operatorstatus)
                {
                    case ULBAOPERATORSTATE.ACTIVE:
                    case ULBAOPERATORSTATE.INITIALIZING:
                    //case ULBAOPERATORSTATE.ERROR:
                        ret.Add(s);
                        break;
                    default:
                        break;
                }
            }
            return ret;
        }

        /**
         * Kill the message. Update DB, then Serialize
         */
        protected CB_SENDING_RESPONSE KillSending()
        {
            PASUmsDb db = new PASUmsDb();

            CB_SENDING_RESPONSE response = new CB_SENDING_RESPONSE();
            response.l_projectpk = operation.l_projectpk;
            response.l_refno = operation.l_refno;
            response.l_timestamp = db.getDbClock();
            response.l_code = -1;

            //Check if this sending has been killed before
            bool b_can_be_killed = false;
            /*List<ULBASENDING> sendings = new List<ULBASENDING>();
            db.GetLbaSendsByRefno(operation.l_refno, ref sendings);
            for (int i = 0; i < sendings.Count; i++)
            {
                if (sendings[i].l_status == 530 ||
                    sendings[i].l_status == 800)
                {
                    b_can_be_killed = false;
                }
            }*/
            List<ULBASENDING> sendings = _getOperatorsThatCanBeKilled(ref db);
            if (sendings.Count > 0)
                b_can_be_killed = true;
            if (b_can_be_killed)
            {
                try
                {
                    CB_ALERT_KILL kill = (CB_ALERT_KILL)operation;
                    kill.Serialize(UCommon.UPATHS.sz_path_cb);
                    //mark sending as cancelled by user
                    db.updateStatusForOperators(operation.l_refno, 530, sendings);
                    response.l_code = 0;
                }
                catch (Exception err)
                {
                    response.l_code = -1;
                }
            }
            else
            {
                response.l_code = -2;
            }

            return response;
        }
    }
}