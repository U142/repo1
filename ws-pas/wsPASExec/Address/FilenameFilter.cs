using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace com.ums.address
{

    /// <summary>
    /// A helper to filter out the latest file with the same prefix, based either on the
    /// directory name of the file, or the filename before an underscore.
    /// 
    /// Example: 
    /// Filtering The files:
    /// Fn_20110101.txt
    /// Fn_20110201.txt
    /// Would result in just Fn_20110201.txt being returned.
    /// </summary>
    public static class FilenameFilter
    {
        /// <summary>
        /// Filter to only pick the latest name for each file with a similar prefix. 
        /// This method assumes it will get a list of filenames, which start with an 
        /// identifying string, then a seperator which is one of "_", " " or "-". The
        /// text before the seperator is used to identify the file, and the latest file
        /// (as determined by name sort order, not by file last modified order) for each 
        /// prefix will be returned.
        /// </summary>
        /// <param name="filenames">List of filenames to filter out the latest file for.</param>
        /// <returns></returns>
        public static List<string> FilterByFilename(IEnumerable<string> filenames)
        {
            var matches = new Dictionary<string, string>();
            foreach (var filename in filenames.Where((s) => s.Contains('_') || s.Contains('-') || s.Contains(' ')))
            {
                var id = System.IO.Path.GetFileName(filename).Split(' ', '-', '_')[0];
                if (!matches.ContainsKey(id) || matches[id].CompareTo(filename) < 0)
                {
                    matches[id] = filename;
                }
            }
            return matches.Values.ToList();
        }

        /// <summary>
        /// This method works similiarily to FilterByFilename, but instead uses the directory
        /// where the file is stored to extraxct the prefix. This is usefull if you have a file 
        /// named the same, stored in incrementing folder names.
        /// </summary>
        /// <param name="filenames">List of files to filter by their directory.</param>
        /// <returns></returns>
        public static List<string> FilterByDirname(IEnumerable<string> filenames)
        {
            var matches = new Dictionary<string, string>();
            foreach (var filename in filenames.Where((s) => s.Contains('_') || s.Contains('-') || s.Contains(' ')))
            {
                var id = System.IO.Path.GetFileName(System.IO.Path.GetDirectoryName(filename)).Split(' ','-','_')[0];
                if (!matches.ContainsKey(id) || matches[id].CompareTo(filename) < 0)
                {
                    matches[id] = filename;
                }
            }
            return matches.Values.ToList();
        }
    }
}
