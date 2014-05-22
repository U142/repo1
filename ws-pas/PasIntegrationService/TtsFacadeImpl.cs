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

        public AudioContent ConvertTtsRaw(string Text, int LangPk)
        {
            UmsDb umsDb = new UmsDb();

            UCONVERT_TTS_REQUEST convertReq = new UCONVERT_TTS_REQUEST();

            convertReq.sz_text = Text;
            convertReq.n_langpk = LangPk;
            String tempPath = System.Configuration.ConfigurationManager.AppSettings["TempPath"];
            String ttsPath = System.Configuration.ConfigurationManager.AppSettings["TtsPath"];
            int TtsRetries; int.TryParse(System.Configuration.ConfigurationManager.AppSettings["TtsRetries"], out TtsRetries); // get number of tts retries (or 0 for none)

            UCONVERT_TTS_RESPONSE response = new UAudio().ConvertTTS(tempPath, ttsPath, convertReq);
            if (response.n_responsecode == 0) // TODO: implement retries if this fails?
            {
                string ttsFile = System.Configuration.ConfigurationManager.AppSettings["TtsPath"] + @"\" + response.sz_server_filename.Replace(".wav", ".raw");
                int tries = 0;

                // check if file exists and is available for reading, try 10 * retries+1 with 100ms delay
                while (IsFileLocked(new FileInfo(ttsFile)) && tries++ > ((TtsRetries + 1) * 10))
                    System.Threading.Thread.Sleep(100);

                byte[] rawBytes = File.ReadAllBytes(ttsFile);
                return new AudioContent()
                {
                    RawVersion = rawBytes,
                    WavVersion = response.wav,
                };
                //return rawBytes;
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
            if (response.n_responsecode == 0) // TODO: implement retries if this fails?
            {
                return response.wav;
            }
            throw new Exception(response.sz_responsetext);
        }

        #endregion

        protected virtual bool IsFileLocked(FileInfo file)
        {
            FileStream stream = null;

            try
            {
                stream = file.Open(FileMode.Open, FileAccess.ReadWrite, FileShare.None);
            }
            catch (IOException)
            {
                //the file is unavailable because it is:
                //still being written to
                //or being processed by another thread
                //or does not exist (has already been processed)
                return true;
            }
            finally
            {
                if (stream != null)
                    stream.Close();
            }

            //file is not locked
            return false;
        }
    }
}
