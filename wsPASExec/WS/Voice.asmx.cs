﻿using System;
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

using com.ums.UmsParm;
using com.ums.UmsCommon;
using com.ums.ws.vb.VB;

using libums2_csharp;


namespace com.ums.ws.voice
{
    /// <summary>
    /// Summary description for Voice
    /// </summary>
    [WebService(Namespace = "http://ums.no/ws/vb/")]
    [WebServiceBinding(ConformsTo = WsiProfiles.BasicProfile1_1)]
    [ToolboxItem(false)]
    // To allow this Web Service to be called from script, using ASP.NET AJAX, uncomment the following line. 
    // [System.Web.Script.Services.ScriptService]
    public class Voice : System.Web.Services.WebService
    {
        
        [WebMethod]
        public Int64 sendVoice(ACCOUNT acc, string to, string from, VOCFILE[] message, Int64 actionprofilepk)
        {
            Int64 ret = -1;
            try
            {
                libums2_csharp.SendVoice voice = new libums2_csharp.SendVoice();
                ret = voice.send(acc, new string[] {to}, from, message, actionprofilepk);
            }
            catch (Exception e)
            {
                ULog.error("Error in voice sendVoice: " + e.Message + " _ " +e.StackTrace);
            }
            return ret;
        }

        [WebMethod]
        public Int64 sendMultipleVoice(ACCOUNT acc, string[] to, string from, VOCFILE[] message, Int64 actionprofilepk)
        {
            Int64 ret = -1;
            try
            {
                libums2_csharp.SendVoice voice = new libums2_csharp.SendVoice();
                ret = voice.send(acc, to, from, message, actionprofilepk);
            }
            catch (Exception e)
            {
                ULog.error("Error in voice sendVoice: " + e.Message);
            }
            return ret;
        }
        [WebMethod]
        public Int64 testSendVoice()
        {
            try
            {
                libums2_csharp.SendVoice voice = new libums2_csharp.SendVoice();

                //input:
                VOCFILE[] files = new VOCFILE[1];


                //string v_refno_1_src = "\\\\195.119.0.167\\Backbone\\voicestd\\no\\1262_pine.raw";
                //string v_refno_1_src = "c:\\i386\\U2L1FR.WAV";
                string v_refno_1_src = "C:\\Program Files\\NetMeeting\\TestSnd.wav";
                byte[] raw = File.ReadAllBytes(v_refno_1_src);

                VOCFILE vocfile = new VOCFILE();
                vocfile.type = VOCTYPE.TTS;
                vocfile.audiodata = raw;
                vocfile.sz_tts_string = "Hei, hei, jeg har en fin presang til deg. Rimte ikke det litt? Johoe, det gjorde det. Nå må du se å klappe igjen smella di du maser som et lokomotiv!";
                vocfile.l_langpk = 6;
                files[0] = vocfile;
                ACCOUNT acc = new ACCOUNT();
                acc.l_comppk = 2;
                acc.l_deptpk = 1;
                acc.l_userpk = 2;
                acc.sz_password = "mh123";
                acc.sz_compid = "UMS";
                acc.sz_userid = "MH";
                acc.sz_deptid = "TEST";

                return voice.send(acc, new string[] { "004792293390" }, "23000000", files, 564);
            }
            catch (Exception e)
            {
                ULog.error(-1, "wsPASExec.Voice.asmx", e.Message + " _ " + e.StackTrace);
                return -1;
            }
               
        }
        
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
                logon.sz_password = "mh123,1";

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
