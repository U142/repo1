using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.IO;

using Lucene.Net.Index;
using Lucene.Net.Documents;
using Lucene.Net.Search;
using Lucene.Net.Analysis;
using System.Text;
using com.ums.UmsCommon;

namespace com.ums.address
{
    public class PersonImport
    {

        private readonly AddressIndexer _adrIndex;
        private readonly string _importFolder;

        private FileInfo.Reader _fileInfoReader = new FileInfo.Reader();
        private PersonInfo.Reader _personInfoReader = new PersonInfo.Reader();
        private PersonInfo.Import _personImport = new PersonInfo.Import();

        public PersonImport(AddressIndexer adrIndex, string importFolder)
        {
            _adrIndex = adrIndex;
            _importFolder = importFolder;
        }

        public PersonImport(AddressIndexer adrIndex)
            : this(adrIndex, Path.Combine(Path.Combine(Environment.GetFolderPath(Environment.SpecialFolder.ApplicationData), "umsAddress"), "import"))
        {
        }

        public void RefreshDatabase()
        {
            var importFiles = System.IO.Directory.GetFiles(_importFolder, "*.txt", SearchOption.TopDirectoryOnly);
            var expired = FilenameFilter.filterByFilename(importFiles).Where(isExpired).ToList();

            if (expired.Count() != 0)
            {
                _adrIndex.Backup();
                var writer = _adrIndex.OpenWriter();
                if (writer != null)
                {
                    foreach (var importFile in expired)
                    {
                        ULog.write("importing " + importFile);
                        try
                        {
                            var lines = System.IO.File.ReadAllLines(importFile, Encoding.GetEncoding("ISO-8859-1"));
                            var fileInfo = new FileInfo(importFile);

                            writer.DeleteDocuments(_fileInfoReader.ToQuery(fileInfo));
                            writer.DeleteDocuments(_personInfoReader.clearImport(fileInfo.BaseName));

                            string[] headings = lines[0].Split('\t');
                            foreach (var line in lines.Skip(1))
                            {
                                var props = new Dictionary<string, string>();
                                var values = line.Split('\t');
                                for (var i = 0; i < headings.Length; i++)
                                {
                                    props[headings[i]] = values[i];
                                }
                                writer.AddDocument(_personInfoReader.Write(_personImport.Import(fileInfo.BaseName, props)));
                            }
                            writer.AddDocument(_fileInfoReader.Write(fileInfo));
                        }
                        catch (IOException e)
                        {
                            ULog.warning("- Failed to import " + e.Message);
                        }
                    }
                    writer.Commit();
                    writer.Optimize();
                    writer.Close();
                    _adrIndex.Refresh();
                }
            }
        }

        private bool isExpired(string importFile)
        {
            var fileInfo = new FileInfo(importFile);
            var result = _adrIndex.Searcher.Search(_fileInfoReader.ToQuery(fileInfo), null, 5);
            return result.totalHits == 0 || fileInfo.IsNewer(_fileInfoReader.Read(_adrIndex.Searcher.Doc(result.scoreDocs[0].doc)));
        }

    }
}