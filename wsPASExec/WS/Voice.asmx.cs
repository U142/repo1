using System;
using System.Collections;
using System.ComponentModel;
using System.Data;
using System.Linq;
using System.Web;
using System.Web.Services;
using System.Web.Services.Protocols;
using System.Xml.Linq;
using System.IO;
using System.Text;

using System.Collections.Generic;
using System.Xml.Serialization;
using System.Xml;

using com.ums.UmsParm;
using com.ums.UmsCommon;
using com.ums.ws.vb.VB;

using libums2_csharp;


namespace com.ums.ws.voice
{
    ///<summary>
    /// This is a service for sending voice messages on the telephone network. A message is built using either text-to-speech or a collection of soundclips. To get an account and a password, contact the UMS Sales office on telephone +47 23501600, or email us on info@ums.no
    ///</summary>
    [WebService(Namespace = "http://ums.no/ws/vb/", Description = "This is a service for sending voice messages on the telephone network. A message is built using either text-to-speech or a collection of soundclips. To get an account and a password, contact the UMS Sales office on telephone +47 23501600, or email us on info@ums.no")]
    [WebServiceBinding(ConformsTo = WsiProfiles.BasicProfile1_1)]
    [ToolboxItem(false)]
    // To allow this Web Service to be called from script, using ASP.NET AJAX, uncomment the following line. 
    // [System.Web.Script.Services.ScriptService]
    public class Voice : System.Web.Services.WebService
    {
        [WebMethod(Description = "For multiple numbers pr recipient add as comma separated list in order of priority ( 004799999999,004723232323,004732323232 )")]
        public Int64 sendVoice(libums2_csharp.ACCOUNT acc, SendingSettings settings, RECIPIENT to, string from, VOCFILE[] message)
        {
            Int64 ret = -1;
        
            libums2_csharp.SendVoice voice = new libums2_csharp.SendVoice();
            voice.ConnectionString = String.Format("DSN={0};UID={1};PWD={2};",UCommon.UBBDATABASE.sz_dsn_aoba,UCommon.UBBDATABASE.sz_uid, UCommon.UBBDATABASE.sz_pwd);
            voice.EatPath = UCommon.UPATHS.sz_path_voice;
            voice.TTSServer = UCommon.UPATHS.sz_path_ttsserver;
            voice.Wav2RawRMS = UCommon.UVOICE.f_rms;
            ret = voice.send(acc, settings, new RECIPIENT[] { to }, from, message);
            return ret;
        }

        [WebMethod(Description = "For multiple numbers pr recipient add as comma separated list in order of priority ( 004799999999,004723232323,004732323232 )")]
        public Int64 sendMultipleVoice(libums2_csharp.ACCOUNT acc, SendingSettings settings, RECIPIENT[] to, string from, VOCFILE[] message)
        {
            Int64 ret = -1;
           
            libums2_csharp.SendVoice voice = new libums2_csharp.SendVoice();
            voice.ConnectionString = String.Format("DSN={0};UID={1};PWD={2};",UCommon.UBBDATABASE.sz_dsn_aoba,UCommon.UBBDATABASE.sz_uid, UCommon.UBBDATABASE.sz_pwd);
            voice.EatPath = UCommon.UPATHS.sz_path_voice;
            voice.TTSServer = UCommon.UPATHS.sz_path_ttsserver;
            voice.Wav2RawRMS = UCommon.UVOICE.f_rms;
            ret = voice.send(acc, settings, to, from, message);

            return ret;
        }
        [WebMethod]
        public List<STATUS> getStatus(libums2_csharp.ACCOUNT acc, Int64 referenceNumber)
        {
            libums2_csharp.SendVoice voice = new libums2_csharp.SendVoice();
            voice.ConnectionString = String.Format("DSN={0};UID={1};PWD={2};",UCommon.UBBDATABASE.sz_dsn_aoba,UCommon.UBBDATABASE.sz_uid, UCommon.UBBDATABASE.sz_pwd);
            voice.EatPath = UCommon.UPATHS.sz_path_voice;
            voice.TTSServer = UCommon.UPATHS.sz_path_ttsserver;
            voice.Wav2RawRMS = UCommon.UVOICE.f_rms;
            return voice.getStatus(acc, referenceNumber);
        }
        [WebMethod(Description = "Returns all available statuses which includes status code, short text and text")]
        public List<ItemStatus> getAvailableStatuses(ACCOUNT acc)
        {
            libums2_csharp.SendVoice voice = new libums2_csharp.SendVoice();
            voice.ConnectionString = String.Format("DSN={0};UID={1};PWD={2};",UCommon.UBBDATABASE.sz_dsn_aoba,UCommon.UBBDATABASE.sz_uid, UCommon.UBBDATABASE.sz_pwd);
            voice.EatPath = UCommon.UPATHS.sz_path_voice;
            voice.TTSServer = UCommon.UPATHS.sz_path_ttsserver;
            voice.Wav2RawRMS = UCommon.UVOICE.f_rms;
            return voice.getAvailableStatuses(acc);
        }
        [WebMethod]
        public List<string> getAvailableSoundLibraryFiles (ACCOUNT acc)
        {
            // Global path
            libums2_csharp.SendVoice voice = new libums2_csharp.SendVoice();
            voice.ConnectionString = String.Format("DSN={0};UID={1};PWD={2};",UCommon.UBBDATABASE.sz_dsn_aoba,UCommon.UBBDATABASE.sz_uid, UCommon.UBBDATABASE.sz_pwd);
            voice.EatPath = UCommon.UPATHS.sz_path_voice;
            voice.TTSServer = UCommon.UPATHS.sz_path_ttsserver;
            voice.Wav2RawRMS = UCommon.UVOICE.f_rms;
            return voice.getAvailableSoundLibraryFiles(acc,UCommon.UPATHS.sz_path_global_wav_dir);
        }
        [WebMethod(Description = "This method cancels all messages on a reference number. ItemNumber is for future use, just add -1 for now")]
        public string cancelVoice(ACCOUNT acc, Int64 referenceNumber, int itemNumber)
        {
            libums2_csharp.SendVoice voice = new SendVoice();
            voice.ConnectionString = String.Format("DSN={0};UID={1};PWD={2};", UCommon.UBBDATABASE.sz_dsn_aoba, UCommon.UBBDATABASE.sz_uid, UCommon.UBBDATABASE.sz_pwd);
            voice.EatPath = UCommon.UPATHS.sz_path_voice;
            voice.TTSServer = UCommon.UPATHS.sz_path_ttsserver;
            voice.Wav2RawRMS = UCommon.UVOICE.f_rms;
            return voice.cancelVoice(acc, referenceNumber);
        }
        [WebMethod(Description = "This method returns an array of VOCFILEs containing a WAV file. This VOCFILE object should be used as input when sending a Voice message")]
        public List<VOCFILE> previewTTS(ACCOUNT acc, VOCFILE[] message)
        {
            libums2_csharp.SendVoice voice = new SendVoice();
            voice.ConnectionString = String.Format("DSN={0};UID={1};PWD={2};", UCommon.UBBDATABASE.sz_dsn_aoba, UCommon.UBBDATABASE.sz_uid, UCommon.UBBDATABASE.sz_pwd);
            voice.EatPath = UCommon.UPATHS.sz_path_voice;
            voice.TTSServer = UCommon.UPATHS.sz_path_ttsserver;
            voice.Wav2RawRMS = UCommon.UVOICE.f_rms;
            return voice.previewTTS(acc, message);
        }
        /*
        [WebMethod]
        public string testCancelVoice(Int64 referenceNumber, int itemNumber)
        {
            libums2_csharp.SendVoice voice = new SendVoice();
            voice.ConnectionString = String.Format("DSN={0};UID={1};PWD={2};", UCommon.UBBDATABASE.sz_dsn_aoba, UCommon.UBBDATABASE.sz_uid, UCommon.UBBDATABASE.sz_pwd);
            voice.EatPath = UCommon.UPATHS.sz_path_voice;
            voice.TTSServer = UCommon.UPATHS.sz_path_ttsserver;
            voice.Wav2RawRMS = UCommon.UVOICE.f_rms;
            libums2_csharp.ACCOUNT acc = new libums2_csharp.ACCOUNT();
            acc.Password = "ums123";
            acc.Company = "UMS";
            acc.Department = "TEST";
            return voice.cancelVoice(acc, referenceNumber);
        }
        [WebMethod]
        public Int64 testSendVoice()
        {
            try
            {
                libums2_csharp.SendVoice voice = new libums2_csharp.SendVoice();
                voice.ConnectionString = String.Format("DSN={0};UID={1};PWD={2};", UCommon.UBBDATABASE.sz_dsn_aoba, UCommon.UBBDATABASE.sz_uid, UCommon.UBBDATABASE.sz_pwd);
                voice.EatPath = UCommon.UPATHS.sz_path_voice;
                voice.TTSServer = UCommon.UPATHS.sz_path_ttsserver;
                voice.Wav2RawRMS = UCommon.UVOICE.f_rms;

                //input:
                VOCFILE[] files = new VOCFILE[1];
                //PARAMETERS param = new PARAMETERS();
                SendingSettings settings = new SendingSettings();
                settings.SendingName = "testSendVoice";
                settings.Profile = new SendingProfile();
                settings.Profile.Number = 564;
                settings.RescheduleProfile = new SendingProfile();
                settings.RescheduleProfile.Number = -1;
                settings.RescheduleProfile.Name = "1 forsøk";
                settings.Schedule = Convert.ToInt64(DateTime.Now.ToString("yyyyMMddHHmm"));
                settings.MessageCaching = 0;//Convert.ToInt64(DateTime.Now.AddDays(1).ToString("yyyyMMddHHmm"));
                settings.IntroClip = 0;
                settings.HiddenNumber = false;

                string v_refno_1_src = "\\\\195.119.0.167\\Backbone\\voicestd\\no\\1262_pine.raw";
                //string v_refno_1_src = "c:\\i386\\U2L1FR.WAV";
                //string v_refno_1_src = "C:\\WINDOWS\\Media\\chimes.wav";
                byte[] raw = File.ReadAllBytes(v_refno_1_src);

                VOCFILE vocfile = new VOCFILE();
                vocfile.type = VOCTYPE.RAW;
                vocfile.audiodata = raw;
                vocfile.sz_tts_string = "Hei, hei, jeg har en fin presang til deg. Rimte ikke det litt? Johoe, det gjorde det. Nå må du se å klappe igjen smella di du maser som et lokomotiv!";
                vocfile.l_langpk = LANGUAGE.NORWEGIAN;
                files[0] = vocfile;
                libums2_csharp.ACCOUNT acc = new libums2_csharp.ACCOUNT();
                acc.Password = "ums123";
                acc.Company = "UMS";
                //acc.sz_userid = "MH";
                acc.Department = "TEST";

                return voice.send(acc, settings, new RECIPIENT[] { new RECIPIENT("004792293390") }, "23000000", files);
            }
            catch (Exception e)
            {
                //ULog.error(-1, "wsPASExec.Voice.asmx", e.Message + " _ " + e.StackTrace);
                return -1;
            }

        }*/
        /*[WebMethod]
        public string ting([XmlChoiceIdentifier] int lol)
        {
            
        }
        */
        /*
        [WebMethod]
        public List<string> testgetAvailableSoundLibraryFiles()
        {
            // Global path
            libums2_csharp.SendVoice voice = new libums2_csharp.SendVoice();
            voice.ConnectionString = String.Format("DSN={0};UID={1};PWD={2};",UCommon.UBBDATABASE.sz_dsn_aoba,UCommon.UBBDATABASE.sz_uid, UCommon.UBBDATABASE.sz_pwd);
            voice.EatPath = UCommon.UPATHS.sz_path_voice;
            voice.TTSServer = UCommon.UPATHS.sz_path_ttsserver;
            voice.Wav2RawRMS = UCommon.UVOICE.f_rms;
            ACCOUNT acc = new ACCOUNT();
            acc.sz_password = "ums123";
            acc.sz_compid = "UMS";
            //acc.sz_userid = "MH";
            acc.sz_deptid = "TEST";
            return voice.getAvailableSoundLibraryFiles(acc, "C:\\i386");
        }
        [WebMethod]
        public List<ItemStatus> testgetAvailableStatuses()
        {
            libums2_csharp.SendVoice voice = new libums2_csharp.SendVoice();
            voice.ConnectionString = String.Format("DSN={0};UID={1};PWD={2};",UCommon.UBBDATABASE.sz_dsn_aoba,UCommon.UBBDATABASE.sz_uid, UCommon.UBBDATABASE.sz_pwd);
            voice.EatPath = UCommon.UPATHS.sz_path_voice;
            voice.TTSServer = UCommon.UPATHS.sz_path_ttsserver;
            voice.Wav2RawRMS = UCommon.UVOICE.f_rms;
            ACCOUNT acc = new ACCOUNT();
            acc.sz_password = "ums123";
            acc.sz_compid = "UMS";
            //acc.sz_userid = "MH";
            acc.sz_deptid = "TEST";
            return voice.getAvailableStatuses(acc);
        }
        [WebMethod]
        public List<STATUS> testGetStatus(int l_refno)
        {
            List<STATUS> statusList;

            libums2_csharp.SendVoice voice = new libums2_csharp.SendVoice();
            voice.ConnectionString = String.Format("DSN={0};UID={1};PWD={2};",UCommon.UBBDATABASE.sz_dsn_aoba,UCommon.UBBDATABASE.sz_uid, UCommon.UBBDATABASE.sz_pwd);
            voice.EatPath = UCommon.UPATHS.sz_path_voice;
            voice.TTSServer = UCommon.UPATHS.sz_path_ttsserver;
            voice.Wav2RawRMS = UCommon.UVOICE.f_rms;
            libums2_csharp.ACCOUNT acc = new libums2_csharp.ACCOUNT();
            acc.sz_password = "ums123";
            acc.sz_compid = "UMS";
            //acc.sz_userid = "MH";
            acc.sz_deptid = "TEST";

            statusList = voice.getStatus(acc, l_refno);
            return statusList;
        }
        
        
        [WebMethod]
        public PARAMETERS getDefaultParameters()
        {
            return new PARAMETERS();
        }*/
        /*
        public struct Account
        {
            public string Company;
            public string Department;
            public string Password;
        }
        public struct Recipient
        {
            public string PhoneNumber;
            public string PinCode;
        }
        public struct SoundClip {
            public Speech Speech;
            public string Filename;
            public SoundGeneration Template;
            public object NextGroup;
            public FileAttachment FileAttatchment;
        }
        public struct SoundGeneration {
        }
        public struct FileAttachment {
            public string mimeType;
            public byte[] base64Binary;
        }
        public enum TextToSpeechEngine {
            DEFAULT,
            INFOVOX,
            NST,
        }
        public enum Language
        {
            NORWEGIAN,
            ENGLISH,
            GERMAN,
            SWEDISH,
            DANISH,
            DUTCH,
            BRITISH,
            SPANISH,
        }
        public struct Speech {
            public string Text;
            public Language Language;
            public TextToSpeechEngine Engine;
        }
        public struct VoiceSending {
            public SendingSettings Settings;
            public SoundClip[] SoundClips;
            public Recipient[] Recipients;
        }
        public struct SendingSettings {
            public string Caching; // Må endres
            public SendingProfile Profile;
            public SendingProfile ConfigProfile;
            public DateTime Schedule;
            public int IntroClip;
            public bool HiddenNumber;
        }
        public struct SendingProfile {
            public string Name;
            public Int32 Number;
        }*/
        /*[WebMethod]
        public int doSendVoice(Account Account, VoiceSending VoiceSending)
        {
            libums2_csharp.ACCOUNT acc2 = new libums2_csharp.ACCOUNT();
            acc2.sz_compid = Account.Company;
            acc2.sz_deptid = Account.Department;
            acc2.sz_password = Account.Password;

            //rec.Length
            RECIPIENT[] recipients = new RECIPIENT[VoiceSending.Recipients.Length];

            for(int i=0;i<VoiceSending.Recipients.Length;++i)
                recipients[i] = new RECIPIENT(VoiceSending.Recipients[i].PhoneNumber, VoiceSending.Recipients[i].PinCode);
            
            PARAMETERS param = new PARAMETERS();
            VOCFILE[] vocfiles = new VOCFILE[VoiceSending.SoundClips.Length]; // Have to check VoiceSending
            VOCFILE voc;
            for(int i=0;i<VoiceSending.SoundClips.Length;++i) {
                voc = new VOCFILE();
                if(!VoiceSending.SoundClips[i].Speech.Text.Equals("")) {
                    voc.type = VOCTYPE.TTS;
                    voc.sz_tts_string = VoiceSending.SoundClips[i].Speech.Text;
                    switch(VoiceSending.SoundClips[i].Speech.Language) {
                        case Language.NORWEGIAN:
                            voc.l_langpk = LANGUAGE.NORWEGIAN;
                            break;
                        case Language.ENGLISH:
                            voc.l_langpk = LANGUAGE.USENGLISH;
                            break;
                        case Language.GERMAN:
                            voc.l_langpk = LANGUAGE.GERMAN;
                            break;
                        case Language.SWEDISH:
                            voc.l_langpk = LANGUAGE.SWEDISH;
                            break;
                        case Language.DANISH:
                            voc.l_langpk = LANGUAGE.DANISH_METTE;
                            break;
                        case Language.DUTCH:
                            voc.l_langpk = LANGUAGE.DUTCH;
                            break;
                        case Language.BRITISH:
                            voc.l_langpk = LANGUAGE.BRITISH;
                            break;
                        case Language.SPANISH:
                            voc.l_langpk = LANGUAGE.SPANISH;
                            break;
                    }
                }
                else if(!VoiceSending.SoundClips[i].FileAttatchment.mimeType.Equals("")) {
                    string mime = VoiceSending.SoundClips[i].FileAttatchment.mimeType;
                    string mimeext = mime.Substring(mime.IndexOf("/")).ToLower();
                    if(mimeext.Equals("wav"))
                        voc.type = VOCTYPE.WAV;
                    else if(mimeext.Equals("raw"))
                        voc.type = VOCTYPE.RAW;
                    BinaryWriter bw = new BinaryWriter(new MemoryStream());
                    // Må lage data array buffer osv
                    bw.Write(VoiceSending.SoundClips[i].FileAttatchment.base64Binary);
                    BinaryReader br = new BinaryReader(bw.BaseStream);
                    br.BaseStream.Position = 0;
                    int initialLenght = 32768;
                    byte[] buffer = new byte[initialLenght];
                    int read = 0;
                    byte[] data;

                    int chunk;
                    bool done = false;
                    while ((chunk = br.BaseStream.Read(buffer, read, buffer.Length - read)) > 0 || done)
                    {
                        read += chunk;

                        if (read == buffer.Length)
                        {
                            int nextByte = br.ReadByte();
                            if (nextByte == - 1)
                                done = true;
                            byte[] newBuffer = new byte[buffer.Length * 2];
                            Array.Copy(buffer, newBuffer, buffer.Length);
                            newBuffer[read] = (byte)nextByte;
                            buffer = newBuffer;
                            read++;
                        }
                    }
                    data = new byte[read];
                    Array.Copy(buffer, data, read);
                    voc.audiodata = data;
                    br.Close();
                    bw.Close();
                }
                vocfiles[i] = voc;
            }
            // If the vs.Settings.Profile.Number is nothing then I have to check the database and find the profile pk belonging to Name
            // The old ws does not have sender number "23000000"
            return (int)sendMultipleVoice(acc2, param, recipients, "23000000", vocfiles, VoiceSending.Settings.Profile.Number);
        }*/
        /*
        public long simpleVoice(string recipient, string pin)
        {   //Array med lydfiler
            // Sende inn meldingen
            long refno = -1;
            BinaryWriter bw = null;
            string v_refno_1_src = "\\\\195.119.0.167\\Backbone\\voicestd\\no\\1262_pine.raw"; // Intro
            string v_refno_3_src = "\\\\195.119.0.167\\Backbone\\SndLib\\Templates\\No\\Tast1.raw"; // 
            //string destination = "\\\\crunch\\ums\\var\\bcp\\bbtemp\\";
            //string destination = "c:\\";

            try
            {
                Pincode pincode = new Pincode();
                ArrayList filenames = pincode.generate_filenames(pin);

                ULOGONINFO logon = new ULOGONINFO();
                logon.l_comppk = 2;
                logon.l_deptpk = 1;
                logon.l_userpk = 2;
                logon.sz_userid = "MH";
                logon.sz_compid = "UMS";
                logon.sz_password = "ums123,1";

                PASUmsDb umsdb = new PASUmsDb(UCommon.UBBDATABASE.sz_dsn_aoba,UCommon.UBBDATABASE.sz_uid, UCommon.UBBDATABASE.sz_pwd);

                refno = umsdb.newRefno();
                if (refno == -1)
                    throw new Exception("Error getting refno");

                BBDYNARESCHED dynre = new BBDYNARESCHED();
                
                dynre.l_canceldate = (long)Convert.ToInt32((DateTime.Now.AddDays(1)).ToString("yyyyMMdd"));
                dynre.l_canceltime = (long)Convert.ToInt16(DateTime.Now.ToString("HHmm"));
                dynre.l_interval = 2;
                dynre.l_pauseinterval = 2;
                dynre.l_pausetime = 5;
                dynre.l_retries = 3;

                if (!umsdb.InsertBBDYNARESCHED(ref refno, ref dynre))
                    throw new Exception("Error inserting dynamic reschedule");
                
                BBVALID valid = new BBVALID();
                valid.l_valid = (long)Convert.ToInt32((DateTime.Now.AddDays(1)).ToString("yyyyMMdd"));
                if (!umsdb.InsertBBVALID(ref refno, ref valid))
                    throw new Exception("Error inserting valid");

                BBSENDNUM sendnum = new BBSENDNUM();
                sendnum.sz_number = UCommon.UVOICE.sz_send_number;
                if (!umsdb.InsertBBSENDNUM(ref refno, ref sendnum))
                    throw new Exception("Error inserting sendnum");

                BBACTIONPROFILESEND apsend = new BBACTIONPROFILESEND();
                apsend.l_actionprofilepk = 5162;
                if (!umsdb.InsertBBACTIONPROFILE(ref refno, ref apsend))
                    throw new Exception("Error inserting action profile");

                // Dette bør flyttes over i en egen del?
                File.Copy(v_refno_1_src, UCommon.UPATHS.sz_path_voice + "v" + refno + "_1.raw");

                bw = new BinaryWriter(File.OpenWrite(UCommon.UPATHS.sz_path_voice + "v" + refno + "_2.raw"));
                BinaryReader br;

                // Silence
                uint nill = 0xFFFFFFFF;
                
                for (int i = 0; i < filenames.Count; i++)
                {
                    br = new BinaryReader(File.OpenRead(filenames[i].ToString()));
                    //fs = File.OpenRead(filenames[i].ToString());
                    Byte[] content = new Byte[br.BaseStream.Length];
                    br.Read(content, 0, content.Length);
                    bw.Write(content);
                    
                    for (int j = 0; j < 750; j++)
                        bw.Write(nill); // Inserting silence
                }
                bw.Close();

                File.Copy(v_refno_3_src, UCommon.UPATHS.sz_path_voice + "v" + refno + "_3.raw");

                StreamWriter sw = File.CreateText(UCommon.UPATHS.sz_path_voice + "d" + refno + ".tmp");
                sw.WriteLine("/MDV");
                sw.WriteLine("/Company=" + logon.sz_compid);
                sw.WriteLine("/Department=TEST"); // Her må jeg ha noe annet?
                sw.WriteLine("/Pri=8");
                sw.WriteLine("/SchedDate=" + DateTime.Now.ToString("yyyyMMdd"));
                sw.WriteLine("/SchedTime=" + DateTime.Now.ToString("HHmm"));
                sw.WriteLine("/Item=1"); // Hvordan bestemmes denne?
                sw.WriteLine("/Name=test");
                sw.WriteLine("/File=v" + refno + "_1.raw");
                sw.WriteLine("/File=v" + refno + "_2.raw");
                sw.WriteLine("/File=v" + refno + "_3.raw");
                sw.WriteLine("/DCALL NA " + recipient); //DCALL || SIMU
                //sw.WriteLine("/SIMU NA " + recipient); //DCALL || SIMU
                sw.Close();

                File.Move(UCommon.UPATHS.sz_path_voice + "d" + refno + ".tmp", UCommon.UPATHS.sz_path_voice + "d" + refno + ".adr");

                return refno;

            }
            catch (Exception e)
            {
                return -1;
            }
            finally
            {
                if (bw != null)
                    bw.Close();
            }
        }*/
    }
}
