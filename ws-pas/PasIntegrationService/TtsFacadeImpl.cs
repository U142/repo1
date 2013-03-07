using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using com.ums.UmsCommon.Audio;

namespace com.ums.pas.integration
{
    public class TtsFacadeImpl : ITtsFacade
    {
        #region ITtsFacade Members

        public byte[] ConvertTtsRaw(string Text, string Speaker, string DestinationFile)
        {

            return null;
        }

        public byte[] ConvertTtsWav(string Text, string Speaker, string DestinationFile)
        {
            UAudio audio = new UAudio();
            UCONVERT_TTS_REQUEST ttsReq = new UCONVERT_TTS_REQUEST();
            UCONVERT_TTS_RESPONSE ttsResponse = audio.ConvertTTS(ttsReq);
            if(ttsResponse.n_responsecode==0)
                return ttsResponse.wav;
            throw new Exception(ttsResponse.sz_responsetext);
        }

        #endregion
    }
}
