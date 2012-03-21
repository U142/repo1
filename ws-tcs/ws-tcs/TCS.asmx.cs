using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Services;
using System.ComponentModel;
using umssettings;
using ums_tcs_lib;
using System.IO;

namespace ums.ws.tcs
{
    /// <summary>
    /// Summary description for Service1
    /// </summary>
    [WebService(Namespace = "https://secure.ums.no/soap/tcs/1.0", Description="Webservice for maintaining whitelists and sending TCS messages.")]
    [WebServiceBinding(ConformsTo = WsiProfiles.BasicProfile1_1)]
    [System.ComponentModel.ToolboxItem(false)]
    // To allow this Web Service to be called from script, using ASP.NET AJAX, uncomment the following line. 
    // [System.Web.Script.Services.ScriptService]
    public class TCSService : System.Web.Services.WebService
    {
        [WebMethod]
        public void ping() { }
        
        // Webmethods for creating and maintining whitelists
        [WebMethod(Description="Get all available whitelists.")]
        public GetWhitelistsResponse doGetWhitelists(Account Account)
        {
            GetWhitelistsResponse ret = new GetWhitelistsResponse();
            UserValues uv = new UserValues();

            ERROR login_result = Database.ValidateUser(Account, uv);
            if (!(login_result == ERROR.success))
                return (GetWhitelistsResponse)ret.SetError(login_result);

            ret.whitelists = Database.GetWhiteLists(uv);

            return ret;
        }

        [WebMethod(Description="Get a specified whitelist.")]
        public GetWhitelistResponse doGetWhitelist(Account Account, String Whitelist)
        {
            GetWhitelistResponse ret = new GetWhitelistResponse();
            UserValues uv = new UserValues();

            ERROR login_result = Database.ValidateUser(Account, uv);
            if (!(login_result == ERROR.success))
                return (GetWhitelistResponse)ret.SetError(login_result);

            if (Whitelist == null || Whitelist.Trim() == "")
                return (GetWhitelistResponse)ret.SetError(ERROR.whitelist_emptyname);

            ret.whitelist = Database.GetWhiteList(uv, Whitelist.Trim());
            if (ret.whitelist == null)
                return (GetWhitelistResponse)ret.SetError(ERROR.whitelist_invaliduser);

            return ret;
        }

        [WebMethod(Description="Get all numbers in a specified whitelist.")]
        public GetWhitelistNumbersResponse doGetWhitelistNumbers(Account Account, String Whitelist)
        {
            GetWhitelistNumbersResponse ret = new GetWhitelistNumbersResponse();
            UserValues uv = new UserValues();

            ERROR login_result = Database.ValidateUser(Account, uv);
            if (!(login_result == ERROR.success))
                return (GetWhitelistNumbersResponse)ret.SetError(login_result);

            if (Whitelist == null || Whitelist.Trim() == "")
                return (GetWhitelistNumbersResponse)ret.SetError(ERROR.whitelist_emptyname);

            WhitelistInfo list = Database.GetListInfo(Whitelist.Trim(), uv.department, uv.company);
            if (list.id == 0)
                return (GetWhitelistNumbersResponse)ret.SetError(ERROR.whitelist_invaliduser);

            // Get existing whitelist numbers
            ret.numbers = Database.GetListContents(list.id);

            return ret;
        }

        [WebMethod(Description="Create a new whitelist.")]
        public CreateWhitelistResponse doCreateWhitelist(Account Account, String Whitelist)
        {
            CreateWhitelistResponse ret = new CreateWhitelistResponse();
            UserValues uv = new UserValues();
            decimal listid = 0;

            ERROR login_result = Database.ValidateUser(Account, uv);
            if (!(login_result == ERROR.success))
                return (CreateWhitelistResponse)ret.SetError(login_result);

            if (Whitelist == null || Whitelist.Trim() == "")
                return (CreateWhitelistResponse)ret.SetError(ERROR.whitelist_emptyname);

            ERROR create = Database.CreateWhitelist(uv, Whitelist.Trim(), ref listid);
            if (create == ERROR.success)
            {
                try
                {
                    // generate XML to create whitelists at operators
                    TCSXmlWriter.TCSListCreate(_settings.GetString("ParserPath"), Whitelist.Trim(), listid, uv.company, uv.department);
                    Log.WriteSysLog(uv.clientstring, "created whitelist (name=" + Whitelist.Trim() + ", id=" + listid.ToString() + ")", 0, 4);
                }
                catch (SoapException)
                {
                    Database.SetListState(uv, listid, QueueState.ERROR);
                    throw;
                }
                catch (Exception e)
                {
                    Database.SetListState(uv, listid, QueueState.ERROR);
                    throw new SoapException(e.Message, SoapException.ServerFaultCode);
                }
            }
            else
            {
                Log.WriteSysLog(uv.clientstring, "failed to create whitelist (name=" + Whitelist.Trim() + ", err=" + create.ToString() + ")", 2, 4);
                return (CreateWhitelistResponse)ret.SetError(create);
            }
            
            return ret;
        }

        [WebMethod(Description="Delete a whitelist and all contained numbers.")]
        public DeleteWhitelistResponse doDeleteWhitelist(Account Account, String Whitelist)
        {
            DeleteWhitelistResponse ret = new DeleteWhitelistResponse();
            UserValues uv = new UserValues();

            ERROR login_result = Database.ValidateUser(Account, uv);
            if (!(login_result == ERROR.success))
                return (DeleteWhitelistResponse)ret.SetError(login_result);

            if (Whitelist == null || Whitelist.Trim() == "")
                return (DeleteWhitelistResponse)ret.SetError(ERROR.whitelist_emptyname);

            WhitelistInfo list = Database.GetListInfo(Whitelist.Trim(), uv.department, uv.company);
            if (list.id == 0)
                return (DeleteWhitelistResponse)ret.SetError(ERROR.whitelist_invaliduser);
            if (list.state != QueueState.READY && list.state!=QueueState.ERROR)
                return (DeleteWhitelistResponse)ret.SetError(ERROR.whitelist_notready);

            try
            {
                // set state to QueueState.QUEUED
                Database.SetListState(uv, list.id, QueueState.QUEUED);

                // write add and remove files to TAS server
                TCSXmlWriter.TCSListDelete(_settings.GetString("ParserPath"), list.name, list.id, uv.company, uv.department);
                Log.WriteSysLog(uv.clientstring, "deleted whitelist (name=" + list.name + ", id=" + list.id.ToString() + ")", 0, 4);
                
                // let AlertiX server delete from the database
                //Database.DeleteWhitelist(uv, Whitelist.Trim(), list.id);
            }
            catch (SoapException)
            {
                // something went wrong, update queuestate to error
                Database.SetListState(uv, list.id, QueueState.ERROR);
                throw;
            }
            catch (Exception e)
            {
                // something went wrong, update queuestate to error
                Database.SetListState(uv, list.id, QueueState.ERROR);
                throw new SoapException(e.Message, SoapException.ServerFaultCode);
            }

            return ret;
        }

        [WebMethod(Description="Add a list of numbers to a whitelist.")]
        public AddNumbersResponse doAddNumbers(Account Account, List<String> Numbers, String Whitelist)
        {
            AddNumbersResponse ret = new AddNumbersResponse();
            UserValues uv = new UserValues();

            ERROR login_result = Database.ValidateUser(Account, uv);
            if (!(login_result == ERROR.success))
                return (AddNumbersResponse)ret.SetError(login_result);

            if (Whitelist == null || Whitelist.Trim() == "")
                return (AddNumbersResponse)ret.SetError(ERROR.whitelist_emptyname);

            WhitelistInfo list = Database.GetListInfo(Whitelist.Trim(), uv.department, uv.company);
            if (list.id == 0)
                return (AddNumbersResponse)ret.SetError(ERROR.whitelist_invaliduser);
            if (list.state != QueueState.READY)
                return (AddNumbersResponse)ret.SetError(ERROR.whitelist_notready);

            // Get existing whitelist numbers
            List<string> existing_numbers = Database.GetListContents(list.id);

            // Parse input numbers
            HashSet<string> upd_numbers = Number.parse(Numbers, ret.invalidNumbers);
            upd_numbers.ExceptWith(existing_numbers);

            ret.added = upd_numbers.Count;
            ret.size = existing_numbers.Count + ret.added;

            // new numbers to add, otherwise list is up-to-date
            if (ret.added > 0)
            {
                try
                {
                    // set state to QueueState.QUEUED
                    Database.SetListState(uv, list.id, QueueState.QUEUED);

                    // write add and remove files to TAS server
                    HashSet<string> del_numbers = new HashSet<string>();
                    TCSXmlWriter.TCSListUpdate(_settings.GetString("ParserPath"), upd_numbers, del_numbers, list.name, list.id, uv.company, uv.department);

                    // write complete list to DB
                    Database.SaveListContents(uv, list.id, upd_numbers.Concat(existing_numbers).ToList());
                    Log.WriteSysLog(uv.clientstring, "added " + ret.added.ToString() + " to whitelist (name=" + list.name + ", id=" + list.id.ToString() + ")", 0, 4);
                }
                catch (SoapException)
                {
                    // something went wrong, update queuestate to error
                    Database.SetListState(uv, list.id, QueueState.ERROR);
                    throw;
                }
                catch (Exception e)
                {
                    // something went wrong, update queuestate to error
                    Database.SetListState(uv, list.id, QueueState.ERROR);
                    throw new SoapException(e.Message, SoapException.ServerFaultCode);
                }
            }

            return ret;
        }

        [WebMethod(Description="Remove a list of numbers from a whitelist.")]
        public DeleteNumbersResponse doDeleteNumbers(Account Account, List<String> Numbers, String Whitelist)
        {
            DeleteNumbersResponse ret = new DeleteNumbersResponse();
            UserValues uv = new UserValues();

            ERROR login_result = Database.ValidateUser(Account, uv);
            if (!(login_result == ERROR.success))
                return (DeleteNumbersResponse)ret.SetError(login_result);

            if (Whitelist == null || Whitelist.Trim() == "")
                return (DeleteNumbersResponse)ret.SetError(ERROR.whitelist_emptyname);

            WhitelistInfo list = Database.GetListInfo(Whitelist.Trim(), uv.department, uv.company);
            if (list.id == 0)
                return (DeleteNumbersResponse)ret.SetError(ERROR.whitelist_invaliduser);
            if (list.state != QueueState.READY)
                return (DeleteNumbersResponse)ret.SetError(ERROR.whitelist_notready);

            // Get existing whitelist numbers
            List<string> existing_numbers = Database.GetListContents(list.id);

            // Parse input numbers
            HashSet<string> upd_numbers = Number.parse(Numbers, ret.invalidNumbers);
            upd_numbers.IntersectWith(existing_numbers);

            ret.deleted = upd_numbers.Count;
            ret.size = existing_numbers.Count - ret.deleted;

            // new numbers to add, otherwise list is up-to-date
            if (ret.deleted > 0)
            {
                try
                {
                    // set state to QueueState.QUEUED
                    Database.SetListState(uv, list.id, QueueState.QUEUED);

                    // write add and remove files to TAS server
                    HashSet<string> add_numbers = new HashSet<string>();
                    TCSXmlWriter.TCSListUpdate(_settings.GetString("ParserPath"), add_numbers, upd_numbers, list.name, list.id, uv.company, uv.department);

                    // write complete list to DB
                    Database.SaveListContents(uv, list.id, existing_numbers.Except(upd_numbers).ToList());
                    Log.WriteSysLog(uv.clientstring, "deleted " + ret.deleted.ToString() + " from whitelist (name=" + list.name + ", id=" + list.id.ToString() + ")", 0, 4);
                }
                catch (SoapException)
                {
                    // something went wrong, update queuestate to error
                    Database.SetListState(uv, list.id, QueueState.ERROR);
                    throw;
                }
                catch (Exception e)
                {
                    // something went wrong, update queuestate to error
                    Database.SetListState(uv, list.id, QueueState.ERROR);
                    throw new SoapException(e.Message, SoapException.ServerFaultCode);
                }
            }

            return ret;
        }

        [WebMethod(Description="Modify a whitelist to contain supplied numbers")]
        public UpdateNumbersResponse doUpdateNumbers(Account Account, List<String> Numbers, String Whitelist)
        {
            UpdateNumbersResponse ret = new UpdateNumbersResponse();
            UserValues uv = new UserValues();

            ERROR login_result = Database.ValidateUser(Account, uv);
            if (!(login_result == ERROR.success))
                return (UpdateNumbersResponse)ret.SetError(login_result);

            if (Whitelist == null || Whitelist.Trim() == "")
                return (UpdateNumbersResponse)ret.SetError(ERROR.whitelist_emptyname);

            WhitelistInfo list = Database.GetListInfo(Whitelist.Trim(), uv.department, uv.company);
            if (list.id == 0)
                return (UpdateNumbersResponse)ret.SetError(ERROR.whitelist_invaliduser);
            if (list.state != QueueState.READY)
                return (UpdateNumbersResponse)ret.SetError(ERROR.whitelist_notready);

            // Get existing whitelist numbers
            List<string> existing_numbers = Database.GetListContents(list.id);

            // Parse input numbers
            HashSet<string> upd_numbers = Number.parse(Numbers, ret.invalidNumbers);
            HashSet<string> add_numbers = new HashSet<string>(upd_numbers);
            HashSet<string> del_numbers = new HashSet<string>(existing_numbers);

            del_numbers.ExceptWith(upd_numbers);
            add_numbers.ExceptWith(existing_numbers);

            ret.deleted = del_numbers.Count;
            ret.added = add_numbers.Count;
            ret.size = upd_numbers.Count;

            // new numbers to add, otherwise list is up-to-date
            if (ret.deleted > 0 || ret.added > 0)
            {
                try
                {
                    // set state to QueueState.QUEUED
                    Database.SetListState(uv, list.id, QueueState.QUEUED);

                    // write add and remove files to TAS server
                    TCSXmlWriter.TCSListUpdate(_settings.GetString("ParserPath"), add_numbers, del_numbers, list.name, list.id, uv.company, uv.department);

                    // write complete list to DB
                    Database.SaveListContents(uv, list.id, upd_numbers.ToList());
                    Log.WriteSysLog(uv.clientstring, "added " + ret.added.ToString() + " and deleted " + ret.deleted.ToString() + " from whitelist (name=" + list.name + ", id=" + list.id.ToString() + ")", 0, 4);
                }
                catch (SoapException)
                {
                    // something went wrong, update queuestate to error
                    Database.SetListState(uv, list.id, QueueState.ERROR);
                    throw;
                }
                catch (Exception e)
                {
                    // something went wrong, update queuestate to error
                    Database.SetListState(uv, list.id, QueueState.ERROR);
                    throw new SoapException(e.Message, SoapException.ServerFaultCode);
                }
            }

            return ret;
        }

        [WebMethod(Description="Add a file of numbers to a whitelist.")]
        public AddNumbersResponse doAddNumbersFromFile(Account Account, Byte[] File, String Whitelist)
        {
            return doAddNumbers(Account, Number.StreamToList(File), Whitelist);
        }

        [WebMethod(Description="Remove a file of numbers from a whitelist.")]
        public DeleteNumbersResponse doDeleteNumbersFromFile(Account Account, Byte[] File, String Whitelist)
        {
            return doDeleteNumbers(Account, Number.StreamToList(File), Whitelist);
        }

        [WebMethod(Description="Modify a whitelist to contain supplied numbers")]
        public UpdateNumbersResponse doUpdateNumbersFromFile(Account Account, Byte[] File, String Whitelist)
        {
            return doUpdateNumbers(Account, Number.StreamToList(File), Whitelist);
        }


        // Webmethods for sending messages and viewing statuses
        [WebMethod(Description="Prepare a new message for sending.")]
        public PrepareMessageResponse doPrepareMessage(Account Account, List<String> Whitelists, Message Message)
        {
            PrepareMessageResponse ret = new PrepareMessageResponse();
            UserValues uv = new UserValues();

            try
            {
                ERROR login_result = Database.ValidateUser(Account, uv);
                if (!(login_result == ERROR.success))
                    return (PrepareMessageResponse)ret.SetError(login_result);

                // verify that whitelist(s) are set and correct
                if (Whitelists == null || Whitelists.Count == 0)
                    return (PrepareMessageResponse)ret.SetError(ERROR.sending_nowhitelists);

                HashSet<string> checked_lists = new HashSet<string>();
                foreach (String Whitelist in Whitelists)
                {
                    if (Whitelist.Trim() == "")
                        return (PrepareMessageResponse)ret.SetError(ERROR.whitelist_emptyname);

                    WhitelistInfo list = Database.GetListInfo(Whitelist.Trim(), uv.department, uv.company);
                    if (list.id == 0)
                        return (PrepareMessageResponse)ret.SetError(ERROR.whitelist_invaliduser);
                    if (list.state != QueueState.READY)
                        return (PrepareMessageResponse)ret.SetError(ERROR.whitelist_notready);

                    checked_lists.Add(Whitelist.Trim());
                }

                // verify message
                ERROR err_msg = Number.CheckMessage(Message, uv);
                if (err_msg != ERROR.success)
                {
                    Log.WriteSysLog(uv.clientstring, "failed to prepare message (err=" + err_msg.ToString() + ")", 2, 2);
                    return (PrepareMessageResponse)ret.SetError(err_msg);
                }

                // check to cc's
                HashSet<int> tocc = new HashSet<int>(Message.toCountryCodes);
                if (tocc.Count == 0)
                    return (PrepareMessageResponse)ret.SetError(ERROR.sending_noccs);

                //List<CountryCode> validcc = Database.GetCCs(uv);
                HashSet<int> validcc = new HashSet<int>(from cc in Database.GetCCs(uv) select cc.cc);
                HashSet<int> invalidcc = new HashSet<int>(tocc);
                invalidcc.ExceptWith(validcc);
                if (invalidcc.Count != 0)
                    return (PrepareMessageResponse)ret.SetError(ERROR.sending_invalidccs);

                // verify sender number
                int ton = Number.GetOtoa(Message.from);
                if (ton == -1 || !Database.ValidSender(uv, Message.from))
                    return (PrepareMessageResponse)ret.SetError(ERROR.sending_invalidoadc);

                // write XML
                int reference = Database.InsertSending(uv, Message);
                TCSXmlWriter.TCSSendLBA(_settings.GetString("ParserPath"), reference, uv.company, uv.department, (int)Message.mode, Message.validity, (int)RequestType.PREPARE, ton, Message.text, Message.from, tocc, checked_lists);

                ret.reference = reference;
                Log.WriteSysLog(uv.clientstring, "prepared message to " + tocc.Count.ToString() + " cc(s) with refno " + ret.reference.ToString() + " (mode=" + Message.mode.ToString() + ")", 0, 2);
            }
            catch (SoapException)
            {
                throw;
            }
            catch (Exception e)
            {
                throw new SoapException(e.Message, SoapException.ServerFaultCode);
            }

            return ret;
        }

        [WebMethod(Description="Execute a prepared message.")]
        public ExecutePreparedMessageResponse doExecutePreparedMessage(Account Account, Int32 Reference)
        {
            ExecutePreparedMessageResponse ret = new ExecutePreparedMessageResponse();
            UserValues uv = new UserValues();

            ERROR login_result = Database.ValidateUser(Account, uv);
            if (!(login_result == ERROR.success))
                return (ExecutePreparedMessageResponse)ret.SetError(login_result);

            if (!Database.ValidateReference(uv, Reference))
                return (ExecutePreparedMessageResponse)ret.SetError(ERROR.reference_invaliduser);

            // check if status is PREPARED
            if (!(Database.GetMessage(uv, Reference).status == SendingStatus.PREPARED))
                return (ExecutePreparedMessageResponse)ret.SetError(ERROR.sending_locked);

            try
            {
                // get job info
                List<SendingInfo> jobs = Database.GetSendinginfo(Reference);

                // update each job (operator specific) and write XML file
                foreach (SendingInfo job in jobs)
                {
                    Database.SetSendingStatus(job.l_refno, job.l_operator, 420);
                    TCSXmlWriter.TCSConfirmLBA(_settings.GetString("ParserPath"), job.l_refno, uv.company, uv.department, job.l_simulate, job.l_operator, job.sz_jobid, false);
                    Log.WriteSysLog(uv.clientstring, "executed prepared message with refno " + Reference.ToString() + " (jobid=" + job.sz_jobid + ", mode=" + ((ExecuteMode)job.l_simulate).ToString() + ")", 0, 2);
                }
            }
            catch (SoapException)
            {
                throw;
            }
            catch (Exception e)
            {
                throw new SoapException(e.Message, SoapException.ServerFaultCode);
            }

            return ret;
        }

        [WebMethod(Description="Cancel a prepared message.")]
        public CancelPreparedMessageResponse doCancelPreparedMessage(Account Account, Int32 Reference)
        {
            CancelPreparedMessageResponse ret = new CancelPreparedMessageResponse();
            UserValues uv = new UserValues();

            ERROR login_result = Database.ValidateUser(Account, uv);
            if (!(login_result == ERROR.success))
                return (CancelPreparedMessageResponse)ret.SetError(login_result);

            if (!Database.ValidateReference(uv, Reference))
                return (CancelPreparedMessageResponse)ret.SetError(ERROR.reference_invaliduser);

            // check if status is PREPARED
            if (!(Database.GetMessage(uv, Reference).status == SendingStatus.PREPARED))
                return (CancelPreparedMessageResponse)ret.SetError(ERROR.sending_locked);

            try
            {
                // get job info
                List<SendingInfo> jobs = Database.GetSendinginfo(Reference);

                // update each job (operator specific) and write XML file
                foreach (SendingInfo job in jobs)
                {
                    Database.SetSendingStatus(job.l_refno, job.l_operator, 430);
                    TCSXmlWriter.TCSConfirmLBA(_settings.GetString("ParserPath"), job.l_refno, uv.company, uv.department, job.l_simulate, job.l_operator, job.sz_jobid, true);
                    Log.WriteSysLog(uv.clientstring, "cancelled prepared message with refno " + Reference.ToString() + " (jobid=" + job.sz_jobid + ")", 0, 2);
                }
            }
            catch (SoapException)
            {
                throw;
            }
            catch (Exception e)
            {
                throw new SoapException(e.Message, SoapException.ServerFaultCode);
            }

            return ret;
        }

        [WebMethod(Description="Get detailed status for a message.")]
        public GetMessageStatusResponse doGetMessageStatus(Account Account, Int32 Reference)
        {
            GetMessageStatusResponse ret = new GetMessageStatusResponse();
            UserValues uv = new UserValues();

            ERROR login_result = Database.ValidateUser(Account, uv);
            if (!(login_result == ERROR.success))
                return (GetMessageStatusResponse)ret.SetError(login_result);

            if (!Database.ValidateReference(uv, Reference))
                return (GetMessageStatusResponse)ret.SetError(ERROR.reference_invaliduser);

            ret.message = Database.GetMessage(uv, Reference);
            ret.ccstatus = Database.GetCCStatus(uv, Reference);
            ret.content = Database.GetContent(uv, Reference);

            return ret;
        }

        [WebMethod(Description="Get a list of previous messages with basic status.")]
        public GetMessagelistResponse doGetMessagelist(Account Account, DateTime? FromDate, DateTime? ToDate)
        {
            GetMessagelistResponse ret = new GetMessagelistResponse();
            UserValues uv = new UserValues();

            ERROR login_result = Database.ValidateUser(Account, uv);
            if (!(login_result == ERROR.success))
                return (GetMessagelistResponse)ret.SetError(login_result);

            ret.messages = Database.GetMessages(uv, FromDate, ToDate, null);

            return ret;
        }

        [WebMethod(Description = "Get a list of valid from strings for a message.")]
        public GetValidFromResponse doGetValidFrom(Account Account)
        {
            GetValidFromResponse ret = new GetValidFromResponse();
            UserValues uv = new UserValues();

            ERROR login_result = Database.ValidateUser(Account, uv);
            if (!(login_result == ERROR.success))
                return (GetValidFromResponse)ret.SetError(login_result);

            ret.validFrom = Database.GetFromStrings(uv);

            return ret;
        }

        [WebMethod(Description="Get a list of available contry codes.")]
        public GetAvailableCountryCodesResponse doGetAvailableCountryCodes(Account Account)
        {
            GetAvailableCountryCodesResponse ret = new GetAvailableCountryCodesResponse();
            UserValues uv = new UserValues();

            ERROR login_result = Database.ValidateUser(Account, uv);
            if (!(login_result == ERROR.success))
                return (GetAvailableCountryCodesResponse)ret.SetError(login_result);

            ret.countryCodes = Database.GetCCs(uv);

            return ret;
        }
    }

    // responses
    public class GetWhitelistResponse : BaseResponse
    {
        public Whitelist whitelist;
    }
    public class GetWhitelistsResponse : BaseResponse
    {
        public List<Whitelist> whitelists;
    }
    public class GetWhitelistNumbersResponse : BaseResponse
    {
        public List<String> numbers = new List<String>();
    }
    public class CreateWhitelistResponse : BaseResponse { }
    public class DeleteWhitelistResponse : BaseResponse { }
    public class AddNumbersResponse : BaseResponse
    {
        public int? size;
        public int? added;
        public List<String> invalidNumbers = new List<String>();
    }
    public class DeleteNumbersResponse : BaseResponse
    {
        public int? size;
        public int? deleted;
        public List<String> invalidNumbers = new List<String>();
    }
    public class UpdateNumbersResponse : BaseResponse
    {
        public int? size;
        public int? added;
        public int? deleted;
        public List<String> invalidNumbers = new List<String>();
    }
    public class PrepareMessageResponse : BaseResponse
    {
        public int? reference;
    }
    public class ExecutePreparedMessageResponse : BaseResponse { }
    public class CancelPreparedMessageResponse : BaseResponse { }
    public class GetMessageStatusResponse : BaseResponse
    {
        public MessageStatus message;
        public MessageContent content;
        public List<CountryCodeStatus> ccstatus;
    }
    public class GetMessagelistResponse : BaseResponse
    {
        public List<MessageStatus> messages;
    }
    public class GetValidFromResponse : BaseResponse
    {
        public List<String> validFrom;
    }
    public class GetAvailableCountryCodesResponse : BaseResponse 
    {
        public List<CountryCode> countryCodes;
    }

    // output classes
    public enum QueueState
    {
        ERROR = -1,
        READY = 0,
        QUEUED = 1,
        PROCESSING = 2
    }
    public enum SendingStatus
    {
        FINISHED,
        CANCELLED,
        PREPARED,
        CONFIRMED_BY_USER,
        CANCELLED_BY_USER,
        SENDING,
        CANCELLING,
        PROCESSING,
        QUEUED,
        ERROR,
        UNKNOWN
    }
    public class Whitelist
    {
        public string name;
        public int size;
        public QueueState state;
    }
    public class CountryCodeStatus
    {
        public int cc;
        public int delivered;
        public int expired;
        public int failed;
        public int sending;
        public int queued;
        public int total;
    }
    public class MessageStatus
    {
        public int reference;
        public string description;
        public SendingStatus status;
        public ExecuteMode mode;
        public int recipients=0;
        public int processed=0;
        public DateTime created;
    }
    public class CountryCode
    {
        public int cc;
        public string iso;
        public string name;
    }
    public class MessageContent
    {
        public string text;
        public string from;
        public List<CountryCode> tocc;
    }

    // input classes
    public enum ExecuteMode
    {
        SIMULATE = 1,
        LIVE = 0
    }
    public class Account
    {
        public string company="";
        public string department="";
        public string password="";
    }
    public class Message
    {
        public ExecuteMode mode = ExecuteMode.SIMULATE;

        public string description="";
        public string from="";
        public string text="";

        public List<int> toCountryCodes;

        [DefaultValue(60)]
        public int validity;
    }

    // internal classes
    public enum ERROR
    {
        success = 0,
        invaliduser = 100,
        whitelist_duplicate = 200,
        whitelist_emptyname = 210,
        whitelist_invaliduser = 220,
        whitelist_notready = 230,
        reference_invaliduser = 300,
        sending_accessdenied = 400,
        sending_nosend = 401,
        sending_nosimulate = 402,
        sending_nowhitelists = 410,
        sending_invalidoadc = 420,
        sending_nomessage = 421,
        sending_messagetoolong = 422,
        sending_messageinvalidgsm = 423,
        sending_messagenomode = 424,
        sending_noccs = 425,
        sending_invalidccs = 426,
        sending_locked = 430,
        unknown = 999
    }
    public enum RequestType
    {
        EXECUTE = 0,
        PREPARE = 1,
        AUTOCANCEL = 2
    }
    public abstract class BaseResponse
    {
        public object SetError(ERROR error)
        {
            errorCode = (int)error;
            successful = false;

            switch (error)
            {
                case ERROR.success:
                    successful = true;
                    break;
                case ERROR.invaliduser:
                    reason = "Incorrect user or password.";
                    break;
                case ERROR.whitelist_duplicate:
                    reason = "Duplicate whitelist name.";
                    break;
                case ERROR.whitelist_emptyname:
                    reason = "Whitelist name can't be empty.";
                    break;
                case ERROR.whitelist_invaliduser:
                    reason = "Whitelist not found for specified user.";
                    break;
                case ERROR.whitelist_notready:
                    reason = "Changes to a whitelist can only be done when it is in ready state.";
                    break;
                case ERROR.reference_invaliduser:
                    reason = "Reference not found for specified user.";
                    break;
                case ERROR.sending_accessdenied:
                    reason = "Specified user does not have access to send messages.";
                    break;
                case ERROR.sending_nosend:
                    reason = "Specified user does not have access to LIVE mode.";
                    break;
                case ERROR.sending_nosimulate:
                    reason = "Specified user does not have access to SIMULATE mode.";
                    break;
                case ERROR.sending_nowhitelists:
                    reason = "No whitelist(s) specified.";
                    break;
                case ERROR.sending_invalidoadc:
                    reason = "Invalid from address.";
                    break;
                case ERROR.sending_nomessage:
                    reason = "Message is empty.";
                    break;
                case ERROR.sending_messagetoolong:
                    reason = String.Format("Message is too long. Maximum message length is {0}.", _settings.GetValue("MaxMessageLength", 160));
                    break;
                case ERROR.sending_messageinvalidgsm:
                    reason = "Message contains invalid characters.";
                    break;
                case ERROR.sending_messagenomode:
                    reason = "Missing mode.";
                    break;
                case ERROR.sending_noccs:
                    reason = "No country code(s) specified.";
                    break;
                case ERROR.sending_invalidccs:
                    reason = "Message contains invalid destination country code(s).";
                    break;
                case ERROR.sending_locked:
                    reason = "Only messages that have status PREPARED can be confirmed or cancelled.";
                    break;
                default:
                    reason = "Unhandled error, please contact UMS.";
                    break;
            }

            return this;
        }

        public bool successful = true;
        public int errorCode = 0;
        public string reason;
    }
    public class UserValues
    {
        public UserValues()
        {
            try
            {
                Database.conn_login = String.Format("DSN={0};UID={1};PWD={2}",
                    _settings.GetString("dsn_login"),
                    _settings.GetString("uid_login"),
                    _settings.GetString("pwd_login"));

                Database.conn_whitelist = String.Format("DSN={0};UID={1};PWD={2}",
                    _settings.GetString("dsn_whitelist"),
                    _settings.GetString("uid_whitelist"),
                    _settings.GetString("pwd_whitelist"));

                Database.text_size = _settings.GetValue("text_size", 2048) * 3 * 1024; // convert from KB to Bytes and triple size for xml overhead

                Log.InitLog(_settings.GetValue("SyslogApp", "soap/tcs/1.0"),
                    _settings.GetValue("SyslogServer", "localhost"),
                    _settings.GetValue("SyslogPort", 514),
                    _settings.GetValue("SyslogType", 0));
            }
            catch (SoapException)
            {
                throw;
            }
            catch (Exception e)
            {
                throw new SoapException(e.Message, SoapException.ServerFaultCode);
            }
        }

        public int company = 0;
        public int department = 0;
        public int stdcc = 47;

        public string clientstring = "";

        public bool f_simulate = false;
        public bool f_live = false;

        public List<Operator> operators;
    }
    public class WhitelistInfo : Whitelist
    {
        public decimal id = 0;
    }
    public class SendingInfo
    {
        public int l_refno;
        public int l_operator;
        public int l_simulate;
        public string sz_jobid;
    }
    public class Operator
    {
        public int l_cc_from = 47;
        public string sz_iso_from = "NO";

        public int l_operator = 0;
        public string sz_operatorname = "";
        public string sz_url = "";
        public string sz_user = "";
        public string sz_password = "";

        public bool b_alertapi = false;
        public bool b_statusapi = false;
        public bool b_internationalapi = false;
        public bool b_statisticsapi = false;
    }
}
