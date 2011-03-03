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
    public class PersonImport : GenericImport<PersonInfo>
    {

        public PersonImport(AddressIndexer adrIndex, string importFolder) :
            base(adrIndex, importFolder, new PersonInfo.Reader(), new PersonInfo.Import(), delegate(string folder)
            {
                var importFiles = System.IO.Directory.GetFiles(importFolder, "*.txt", SearchOption.TopDirectoryOnly);
                return FilenameFilter.FilterByFilename(importFiles);
            })
        {
        }

        public PersonImport(AddressIndexer adrIndex)
            : this(adrIndex, Path.Combine(DefaultBase, "import"))
        {
        }

    }

    public class CompanyImport : GenericImport<CompanyInfo>
    {

        public CompanyImport(AddressIndexer adrIndex, string importFolder) :
            base(adrIndex, importFolder, new CompanyInfo.Reader(), new CompanyInfo.Import(), delegate(string folder)
            {
                var importFiles = System.IO.Directory.GetFiles(importFolder, "ums_næring.txt", SearchOption.AllDirectories);
                return FilenameFilter.FilterByDirname(importFiles);
            })
        {
        }

        public CompanyImport(AddressIndexer adrIndex)
            : this(adrIndex, Path.Combine(DefaultBase, "bedriftImport"))
        {
        }

    }

    public class GenericImport<T> where T:new()
    {
        public static string DefaultBase { 
            get {
                return Path.Combine(Environment.GetFolderPath(Environment.SpecialFolder.ApplicationData), "umsAddress"); 
            } 
        }

        private readonly AddressIndexer _adrIndex;
        private readonly string _importFolder;

        public delegate IEnumerable<string> GetFiles(string importFolder);

        private FileInfo.Reader _fileInfoReader = new FileInfo.Reader();
        private readonly DocReader<T> _reader;
        private readonly DataImport<T> _importer;
        private readonly GetFiles _getFiles;

        public GenericImport(AddressIndexer adrIndex, string importFolder, DocReader<T> reader, DataImport<T> importer, GetFiles getFiles)
        {
            _adrIndex = adrIndex;
            _importFolder = importFolder;
            _reader = reader;
            _importer = importer;
            _getFiles = getFiles;
        }


        public void RefreshDatabase()
        {
            var expired = _getFiles(_importFolder).Where(isExpired);

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
                            writer.DeleteDocuments(_reader.ClearImport(fileInfo.BaseName));

                            string[] headings = lines[0].Split('\t');
                            foreach (var line in lines.Skip(1))
                            {
                                var props = new Dictionary<string, string>();
                                var values = line.Split('\t');
                                if (values.Length == headings.Length)
                                {
                                    for (var i = 0; i < headings.Length; i++)
                                    {
                                        props[headings[i]] = values[i];
                                    }
                                    writer.AddDocument(_reader.Write(_importer.Import(fileInfo.BaseName, props)));
                                }
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