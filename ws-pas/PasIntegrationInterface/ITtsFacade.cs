using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace com.ums.pas.integration
{
    /// <summary>
    /// Facade interface for handling conversion of TTS.
    /// For simplicity, conversions here will return byte arrays.
    /// In that manner the application will be responsible to write as files to the system where needed.
    /// </summary>
    public interface ITtsFacade
    {
        /// <summary>
        /// Convert TTS and return raw audiofile
        /// </summary>
        /// <param name="Text"></param>
        /// <param name="Speaker"></param>
        /// <param name="DestinationFile"></param>
        /// <returns>File content</returns>
        byte [] ConvertTtsRaw(String Text, String Speaker, String DestinationFile);

        /// <summary>
        /// Convert TTS and return wav audiofile
        /// </summary>
        /// <param name="Text"></param>
        /// <param name="Speaker"></param>
        /// <param name="DestinationFile"></param>
        /// <returns>File content</returns>
        byte [] ConvertTtsWav(String Text, String Speaker, String DestinationFile);
    }
}
