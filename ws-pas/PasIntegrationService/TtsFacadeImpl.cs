using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using com.ums.UmsCommon.Audio;
using com.ums.UmsDbLib;
using System.IO;

namespace com.ums.pas.integration
{
    public class TtsFacadeImpl : ITtsFacade
    {
        #region ITtsFacade Members

        public byte[] ConvertTtsRaw(string Text, int LangPk)
        {
            UmsDb umsDb = new UmsDb();

            UCONVERT_TTS_REQUEST convertReq = new UCONVERT_TTS_REQUEST();

            convertReq.sz_text = Text;
            convertReq.n_langpk = LangPk;
            String tempPath = System.Configuration.ConfigurationManager.AppSettings["TempPath"];
            String ttsPath = System.Configuration.ConfigurationManager.AppSettings["TtsPath"];

            UCONVERT_TTS_RESPONSE response = new UAudio().ConvertTTS(tempPath, ttsPath, convertReq);
            if (response.n_responsecode == 0)
            {
                byte[] rawBytes = File.ReadAllBytes(System.Configuration.ConfigurationManager.AppSettings["TtsPath"] + @"\" + response.sz_server_filename);
                return rawBytes;
            }
            throw new Exception(response.sz_responsetext);
        }

        public byte[] ConvertTtsWav(string Text, int LangPk)
        {
            UmsDb umsDb = new UmsDb();

            UCONVERT_TTS_REQUEST convertReq = new UCONVERT_TTS_REQUEST();

            convertReq.sz_text = Text;
            convertReq.n_langpk = LangPk;

            String tempPath = System.Configuration.ConfigurationManager.AppSettings["TempPath"];
            String ttsPath = System.Configuration.ConfigurationManager.AppSettings["TtsPath"];
            UCONVERT_TTS_RESPONSE response = new UAudio().ConvertTTS(tempPath, ttsPath, convertReq);
            if (response.n_responsecode == 0)
            {
                return response.wav;
            }
            throw new Exception(response.sz_responsetext);
        }

        #endregion
    }
}
