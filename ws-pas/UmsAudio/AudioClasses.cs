using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;
using System.Data.Odbc;
using System.Data;

using com.ums.UmsCommon;
using com.ums.UmsParm;
using com.ums.UmsFile;

using TTSLib;
using WAV2RAWLib;


namespace com.ums.UmsCommon.Audio
{
    public class UAudio
    {
        /*
        public string[] generateVoice(VOCFILE[] message, Int64 l_refno, ref OdbcCommand cmd, string eat, string szTTSServerPath)
        {

            string[] filenames = new string[message.Length];
            BinaryWriter bw;
            string source, destination;
            
            string sz_audiofiles_path = Path.GetDirectoryName(System.Reflection.Assembly.GetExecutingAssembly().GetName().CodeBase);
            sz_audiofiles_path = sz_audiofiles_path.Replace("file:\\", "");
            DirectoryInfo di = Directory.GetParent(sz_audiofiles_path);
            di = di.Parent;
            sz_audiofiles_path = di.FullName + "\\audiofiles\\";
            try
            {
                for (int i = 0; i < message.Length; ++i)
                {
                    switch (message[i].type)
                    {
                        case VOCTYPE.TTS:
                            //tts to WAV
                            //WAV to RAW
                            filenames[i] = TTS(message[i].sz_tts_string, i, message[i].l_langpk, l_refno, ref cmd, szTTSServerPath, eat);
                            break;
                        case VOCTYPE.WAV:
                            //convert to raw
                            bw = new BinaryWriter(File.Open(sz_audiofiles_path + "v" + l_refno + "_" + i + ".wav", FileMode.Create));
                            bw.Write(message[i].audiodata);
                            bw.Flush();
                            bw.Close();
                            convertClass convert = new convertClass();
                            TTSClass conv = new TTSClass();
                            conv.Say_raw(sz_audiofiles_path + "v" + l_refno + "_" + i + ".wav", sz_audiofiles_path + "v" + l_refno + "_" + i + ".raw");
                            convert.WAV2RAWNormalize(sz_audiofiles_path + "v" + l_refno + "_" + i + ".wav", sz_audiofiles_path + "v" + l_refno + "_" + i + 1 + ".raw");
                            convert.DoConv(sz_audiofiles_path + "v" + l_refno + "_" + i + ".wav", sz_audiofiles_path + "v" + l_refno + "_" + i + 2 + ".raw");
                            filenames[i] = "v" + l_refno + "_" + i + ".raw";

                            source = sz_audiofiles_path + filenames[i];
                            destination = eat + filenames[i];
                            File.Copy(source, destination);
                            File.Delete(sz_audiofiles_path + "v" + l_refno + "_" + i + ".wav");
                            break;
                        case VOCTYPE.RAW:
                            //make copy to server
                            bw = new BinaryWriter(File.Open(eat + "v" + l_refno + "_" + i + ".raw", FileMode.Create));
                            bw.Write(message[i].audiodata);
                            bw.Flush();
                            bw.Close();
                            filenames[i] = "v" + l_refno + "_" + i + ".raw";
                            break;
                    }
                }
            }
            catch (Exception e)
            {
                com.ums.UmsCommon.ULog.write("Error in generate voice: " + e.Message + e.StackTrace);
                filenames[0] = e.Message + e.StackTrace;
            }
            return filenames;
        }

        private string TTS(string sz_message, int filenumber, int l_langpk, Int64 l_refno, ref OdbcCommand cmd, string szTTSServerPath, string eat)
        {
            string szTempFileName = "v" + l_refno + "_" + filenumber + ".wav";
            string szFileName = "v" + l_refno + "_" + filenumber + ".wav";
            string szRawFileName = "v" + l_refno + "_" + filenumber + ".raw";
            string szTxtFileName = "v" + l_refno + "_" + filenumber + ".txt";
            string szLangFileName = "v" + l_refno + "_" + filenumber + ".lang";
            string ret = "";
            string ting = "Før kall ";

            try
            {
                TTS objTXT2Wav = new TTSLib.TTS();
                cmd.Parameters.Clear();
                cmd.CommandType = CommandType.Text;
                //cmd.CommandText = String.Format("SELECT * FROM BBTTSLANG WHERE l_langpk={0}", l_langpk);
                cmd.CommandText = "SELECT * FROM BBTTSLANG WHERE l_langpk=?";
                cmd.Parameters.Add("@l_langpk", OdbcType.Int).Value = l_langpk; // This should be BigInt, but is not supported by ayanami
                
                OdbcDataReader dr = cmd.ExecuteReader();
                ting += "Etter kall";
                if (dr.HasRows)
                {
                    StreamWriter fLangFile = new StreamWriter(szLangFileName, false, Encoding.GetEncoding("iso-8859-1"));
                    fLangFile.WriteLine("$" + dr["sz_speaker"]);
                    fLangFile.WriteLine("£" + dr["sz_modename"]);
                    fLangFile.WriteLine("%" + dr["sz_manufacturer"]);
                    fLangFile.Close();
                }
                dr.Close();

                File.Move(szLangFileName, szTTSServerPath + szLangFileName);

                if (File.Exists(szTTSServerPath + szTempFileName))
                    File.Delete(szTTSServerPath + szTempFileName);

                try
                {
                    objTXT2Wav.Say_wavex(sz_message, szTTSServerPath + szFileName, 60);
                }
                catch (Exception ex)
                {
                    ums.UmsCommon.ULog.write("Error in TTS: " + ex.Message + "\n " + ex.StackTrace);
                }

                // This is waiting for TTS to finish converting wav to raw
                DateTime future = DateTime.Now.AddSeconds(10);
                bool b = true;

                while (b)
                {
                    if (File.Exists(szTTSServerPath + szRawFileName))
                        if (new FileInfo(szTTSServerPath + szRawFileName).Length > 0)
                            b = false;
                    if (b && 0 <= DateTime.Compare(DateTime.Now, future))
                        b = false;
                }

                if (File.Exists(szTTSServerPath + szRawFileName))
                    File.Move(szTTSServerPath + szRawFileName, eat + szRawFileName);

                ret = szRawFileName;

            }
            catch (Exception e)
            {
                if (File.Exists(szLangFileName))
                    File.Delete(szLangFileName);
                ums.UmsCommon.ULog.write("Error in TTS: " + e.Message + "\n " + e.StackTrace);
                ret += ting + e.Message + "\n" + e.StackTrace;
            }

            return ret;
        }*/

        public UCONVERT_TTS_RESPONSE ConvertTTS(ref ULOGONINFO logon, ref UCONVERT_TTS_REQUEST req)
        {
            UCONVERT_TTS_RESPONSE response = new UCONVERT_TTS_RESPONSE();
            response.n_responsecode = -1;
            try
            {
                TTS tts = new TTS();
                PASUmsDb db = new PASUmsDb();
                if (!db.CheckLogon(ref logon, true))
                {
                    response.sz_responsetext = "Error in logon";
                    return response;
                }
                UTTS_DB_PARAMS dbparams = new UTTS_DB_PARAMS();
                if (!db.GetTTSParams(req.n_langpk, ref dbparams))
                {
                    ULog.error(0, "Error, TTS langpk not found");
                    response.sz_responsetext = "Error, TTS langpk not found";
                    return response;
                }
                db.close();

                /*CREATE FILENAMES*/
                response.guid_sendingid = Guid.NewGuid().ToString();
                String sz_filebase = "PAS_" + response.guid_sendingid + "_" + req.n_dynfile;
                String sz_langfile = sz_filebase + ".lang";
                String sz_wavfile = sz_filebase + ".wav";
                String sz_rawfile = sz_filebase + ".raw";
                String sz_txtfile = sz_filebase + ".txt";
                String sz_tempfile = sz_filebase + ".wav";

                /*WRITE LANG FILE*/
                UFile file_lang = new UFile(UCommon.UPATHS.sz_path_audiofiles, sz_langfile);
                StreamWriter w = new StreamWriter(file_lang.full(), false, Encoding.GetEncoding("iso-8859-1"));
                w.WriteLine("$" + dbparams.sz_speaker);
                w.WriteLine("£" + dbparams.sz_modename);
                w.WriteLine("%" + dbparams.sz_manufacturer);
                w.Close();
                file_lang.MoveOperation(new UFile(UCommon.UPATHS.sz_path_ttsserver, sz_langfile));
                //File.Copy(UCommon.UPATHS.sz_path_audiofiles + sz_langfile, UCommon.UPATHS.sz_path_ttsserver + sz_langfile);

                /*CONVERT TTS*/
                UFile file_txt = new UFile(UCommon.UPATHS.sz_path_ttsserver, sz_txtfile);
                tts.Say_wavex(req.sz_text, file_txt.full(), 60);

                UFile file_temp = new UFile(UCommon.UPATHS.sz_path_ttsserver, sz_tempfile);
                UFile file_dest_wav = new UFile(UCommon.UPATHS.sz_path_audiofiles, sz_wavfile);
                UFile file_dest_raw = new UFile(UCommon.UPATHS.sz_path_audiofiles, sz_rawfile);

                file_temp.MoveOperation(file_dest_wav);
                WAV2RAWLib.convert w2r = new WAV2RAWLib.convert();
                w2r.WAV2RAWNormalize(file_dest_wav.full(), file_dest_raw.full());

                try
                {
                    response.wav = File.ReadAllBytes(file_dest_wav.full());
                    response.sz_server_filename = file_dest_wav.file();
                    response.n_responsecode = 0;
                    response.n_dynfile = req.n_dynfile;
                    response.n_langpk = req.n_langpk;
                    response.sz_responsetext = "OK";
                }
                catch (Exception e)
                {
                    response.n_responsecode = -1;
                    response.sz_responsetext = e.Message;
                }

            }
            catch (Exception e)
            {
                ULog.error(0, "Error converting TTS", e.Message);
                response.n_responsecode = -1;
                response.sz_responsetext = e.Message;
            }
            

            return response;

        }
        public AUDIO_RESPONSE UPostAudio(ref ULOGONINFO logon, ref AUDIO_REQUEST req)
        {
            string sz_physpath;
            string sz_physdestpath;
            string sz_tempfile;
            string sz_tempfileraw;
            string sz_destwav;
            string sz_destraw;
            BinaryWriter bw;
            //string sz_virtualpath;

            AUDIO_RESPONSE response = new AUDIO_RESPONSE();
            response.n_responsecode = -1;

            PASUmsDb db = new PASUmsDb();
            if (!db.CheckLogon(ref logon, true))
            {
                response.sz_responsetext = "Error in logon";
                return response;
            }
            db.close();
            String sz_destfile;
            try
            {
                convertClass conv = new convertClass();

                switch (req.n_filetype)
                {
                    case 1: //SOUNDFILE_TYPE_RECORD_
                    case 2: //TTS posted audio
                        sz_physpath = UCommon.UPATHS.sz_path_parmtemp;
                        sz_tempfile = "v" + req.n_refno + "_" + req.n_param;
                        sz_tempfileraw = sz_tempfile + ".raw";
                        sz_tempfile = sz_tempfile + ".wav";
                        sz_destwav = UCommon.UPATHS.sz_path_audiofiles + "\\" + sz_tempfile;
                        sz_destraw = UCommon.UPATHS.sz_path_backbone + "\\" + sz_tempfileraw;

                        bw = new BinaryWriter(File.Open(sz_physpath + sz_tempfile, FileMode.Create));
                        bw.Write(req.wav);
                        bw.Flush();
                        bw.Close();
                        conv.WAV2RAWNormalize(sz_physpath + sz_tempfile, sz_physpath + sz_tempfileraw);
                        
                        //File.Move(sz_physpath + sz_tempfile, sz_destwav);
                        //File.Move(sz_physpath + sz_tempfileraw, sz_destraw);
                        try
                        {
                            //File.Move(sz_physpath + sz_tempfile, sz_destwav);
                            //File.Move(sz_physpath + sz_tempfileraw, sz_destraw);
                            File.Copy(sz_physpath+sz_tempfile, sz_destwav);
                            //File.Delete(sz_tempfile);
                            //File.Delete(sz_tempfileraw);
                        }
                        catch (Exception)
                        {
                        }
                        File.Copy(sz_physpath + sz_tempfileraw, sz_destraw);

                        break;
                    /*case 2: //SOUNDFILE_TYPE_TTS_
                        string sz_filename = req.sz_filename;
                        string sz_destfile;
                        sz_physpath = UCommon.UPATHS.sz_path_audiofiles;    
                        // This means the user has previewed the file and it doesn't need to be converted again
                        if (req.sz_filename == null || req.sz_filename.Equals(""))
                        {
                            UCONVERT_TTS_REQUEST ttsreq = new UCONVERT_TTS_REQUEST();
                            ttsreq.n_dynfile = req.n_param;
                            ttsreq.sz_text = req.sz_tts_text;
                            ttsreq.n_langpk = req.n_langpk;
                            UCONVERT_TTS_RESPONSE ttsresp = ConvertTTS(ref logon, ref ttsreq);
                            sz_filename = ttsresp.sz_server_filename;
                            if (ttsresp.n_responsecode != 0)
                                throw new Exception(ttsresp.sz_responsetext);
                        }
                        
                        sz_tempfile = sz_physpath + sz_filename;
                        sz_tempfileraw = sz_physpath + sz_filename.Replace(".wav", ".raw");
                        sz_destfile = "v" + req.n_refno + "_" + req.n_param;
                        sz_destwav = UCommon.UPATHS.sz_path_audiofiles + sz_destfile + ".wav";
                        sz_destraw = UCommon.UPATHS.sz_path_backbone + sz_destfile + ".raw";

                        try
                        {
                            File.Move(sz_tempfile, sz_destwav);
                            File.Move(sz_tempfileraw, sz_destraw);
                            //File.Copy(sz_tempfile, sz_destwav);
                            //File.Copy(sz_tempfileraw, sz_destraw);
                            //File.Delete(sz_tempfile);
                            //File.Delete(sz_tempfileraw);
                        }
                        catch (Exception)
                        {
                            throw;
                        }
                        break;*/
                    case 4: //SOUNDFILE_TYPE_LIBRARY_
                        sz_physdestpath = UCommon.UPATHS.sz_path_audiofiles;
                        sz_physpath = UCommon.UPATHS.sz_path_bbmessages + req.n_deptpk + "/";
                        sz_tempfile = sz_physpath + req.n_messagepk + ".wav";
                        sz_tempfileraw = sz_physpath + req.n_messagepk + ".raw";
                        sz_destfile = "v" + req.n_refno + "_" + req.n_param;
                        sz_destwav = sz_physdestpath + sz_destfile + ".wav";
                        sz_destraw = UCommon.UPATHS.sz_path_backbone + sz_destfile + ".raw";

                        File.Copy(sz_tempfile, sz_destwav);
                        File.Copy(sz_tempfileraw, sz_destraw);
                        break;
                    case 8: //SOUNDFILE_TYPE_LOCAL_
                        sz_physpath = UCommon.UPATHS.sz_path_parmtemp;
                        //sz_virtpath = "java_pas/temp/";
                        sz_tempfile = "v" + req.n_refno + "_" + req.n_param;
                        sz_tempfileraw = sz_tempfile + ".raw";
                        sz_tempfile = sz_tempfile + ".wav";
                        sz_destwav = UCommon.UPATHS.sz_path_audiofiles + sz_tempfile;
                        sz_destraw = UCommon.UPATHS.sz_path_backbone + sz_tempfileraw;

                        bw = new BinaryWriter(File.Open(sz_physpath + sz_tempfile, FileMode.Create));
                        bw.Write(req.wav);
                        bw.Flush();
                        bw.Close();
                        conv.WAV2RAWNormalize(sz_physpath + sz_tempfile, sz_physpath + sz_tempfileraw);

                        File.Copy(sz_physpath + sz_tempfile, sz_destwav);
                        File.Copy(sz_physpath + sz_tempfileraw, sz_destraw);
                        break;

                }
                response.n_responsecode = 0;
                response.sz_responsetext = "OK";
            }
            catch (Exception e)
            {
                ULog.error(0, "Error WAV2RawNormalize", e.Message);
                response.n_responsecode = -1;
                response.sz_responsetext = e.Message;
            }

            return response;
        }

    }


    /*used for posting audio-files*/
    public class AUDIO_REQUEST
    {
        public int n_refno;
        public int n_param;
        public int n_filetype;
        public byte[] wav;
        public string sz_tts_text;
        public int n_langpk;
        public string sz_filename;
        public int n_messagepk;
        public int n_deptpk;
    }
    public class AUDIO_RESPONSE
    {
        public int n_responsecode;
        public string sz_responsetext;
    }

    public enum UTTS_LANG
    {

    }

    public class UCONVERT_TTS_REQUEST
    {
        public int n_langpk;
        public int n_dynfile;
        public String sz_text;
    }
    public class UCONVERT_TTS_RESPONSE
    {
        public int n_responsecode;
        public String sz_responsetext;
        public int n_langpk;
        public String guid_sendingid;
        public int n_dynfile;
        public byte[] wav;
        public String sz_server_filename;
    }
}
