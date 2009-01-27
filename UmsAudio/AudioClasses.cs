using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;

using TTSLib;
using WAV2RAWLib;
using com.ums.UmsCommon;
using com.ums.UmsParm;
using com.ums.UmsFile;


namespace com.ums.UmsCommon.Audio
{

    public class UAudio
    {
        public UCONVERT_TTS_RESPONSE ConvertTTS(ref ULOGONINFO logon, ref UCONVERT_TTS_REQUEST req)
        {
            UCONVERT_TTS_RESPONSE response = new UCONVERT_TTS_RESPONSE();
            response.n_responsecode = -1;
            try
            {
                TTS tts = new TTS();
                PASUmsDb db = new PASUmsDb();
                if (!db.CheckLogon(ref logon))
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
                response.guid_sendingid = Guid.NewGuid();
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
                    response.wav = null; // File.ReadAllBytes(file_dest_wav.full());
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
        public UPOST_AUDIO_RESPONSE UPostAudio(ref ULOGONINFO logon, ref UPOST_AUDIO_REQUEST req)
        {
            string sz_physpath;
            string sz_physdestpath;
            string sz_tempfile;
            string sz_tempfileraw;
            string sz_destwav;
            string sz_destraw;
            BinaryWriter bw;
            //string sz_virtualpath;

            UPOST_AUDIO_RESPONSE response = new UPOST_AUDIO_RESPONSE();
            response.n_responsecode = -1;

            PASUmsDb db = new PASUmsDb();
            if (!db.CheckLogon(ref logon))
            {
                response.sz_responsetext = "Error in logon";
                return response;
            }
            db.close();

            try
            {
                convertClass conv = new convertClass();

                switch (req.n_filetype)
                {
                    case 1:
                        sz_physpath = UCommon.UPATHS.sz_path_parmtemp;
                        sz_tempfile = "v" + req.n_refno + "_" + req.n_param;
                        sz_tempfileraw = sz_tempfile + ".raw";
                        sz_tempfile = sz_tempfile + ".wav";
                        sz_destwav = UCommon.UPATHS.sz_path_audiofiles + sz_tempfile;
                        sz_destraw = UCommon.UPATHS.sz_path_audiofiles + sz_tempfileraw;

                        bw = new BinaryWriter(File.Open(sz_physpath + sz_tempfile, FileMode.Create));
                        bw.Write(req.wav);
                        bw.Flush();
                        bw.Close();
                        conv.WAV2RAWNormalize(sz_physpath + sz_tempfile, sz_physpath + sz_tempfileraw);
                        
                        File.Move(sz_physpath + sz_tempfile, sz_destwav);
                        File.Move(sz_physpath + sz_tempfileraw, sz_destraw);
                        break;
                    case 2:
                        string sz_filename;
                        string sz_destfile;

                        sz_filename = req.sz_filename;
                        sz_physpath = UCommon.UPATHS.sz_path_parmtemp;
                        sz_tempfile = sz_physpath + sz_filename;
                        sz_tempfileraw = sz_physpath + sz_filename.Replace(".wav", ".raw");
                        sz_destfile = "v" + req.n_refno + "_" + req.n_param;
                        sz_destwav = UCommon.UPATHS.sz_path_audiofiles + sz_destfile + ".wav";
                        sz_destraw = UCommon.UPATHS.sz_path_audiofiles + sz_destfile + ".raw";

                        File.Copy(sz_tempfile, sz_destwav);
                        File.Copy(sz_tempfileraw, sz_destraw);
                        break;
                    case 4:
                        sz_physdestpath = UCommon.UPATHS.sz_path_audiofiles;
                        sz_physpath = UCommon.UPATHS.sz_path_bbmessages + req.n_deptpk + "/";
                        sz_tempfile = sz_physpath + req.n_messagepk + ".wav";
                        sz_tempfileraw = sz_physpath + req.n_messagepk + ".raw";
                        sz_destfile = "v" + req.n_refno + "_" + req.n_param;
                        sz_destwav = sz_physdestpath + sz_destfile + ".wav";
                        sz_destraw = sz_physdestpath + sz_destfile + ".raw";

                        File.Copy(sz_tempfile, sz_destwav);
                        File.Copy(sz_tempfileraw, sz_destraw);
                        break;
                    case 8:
                        sz_physpath = UCommon.UPATHS.sz_path_parmtemp;
                        //sz_virtpath = "java_pas/temp/";
                        sz_tempfile = "v" + req.n_refno + "_" + req.n_param;
                        sz_tempfileraw = sz_tempfile + ".raw";
                        sz_tempfile = sz_tempfile + ".wav";
                        sz_destwav = UCommon.UPATHS.sz_path_audiofiles + sz_tempfile;
                        sz_destraw = UCommon.UPATHS.sz_path_audiofiles + sz_tempfileraw;

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
    public class UPOST_AUDIO_REQUEST
    {
        public int n_refno;
        public int n_param;
        public int n_filetype;
        public byte[] wav;
        public string sz_filename;
        public int n_messagepk;
        public int n_deptpk;
    }
    public class UPOST_AUDIO_RESPONSE
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
        public Guid guid_sendingid;
        public int n_dynfile;
        public byte[] wav;
        public String sz_server_filename;
    }

}
