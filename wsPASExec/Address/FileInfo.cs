using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;

using Lucene.Net.Index;
using Lucene.Net.Store;
using Lucene.Net.Analysis;
using Lucene.Net.Documents;
using Lucene.Net.Search;
using System.Globalization;
using Lucene.Net.Util;

namespace com.ums.address
{
    public class FileInfo
    {

        public string BaseName { get; set; }
        public DateTime LastModified { get; set; }

        public FileInfo(string fileName)
        {
            BaseName = Path.GetFileName(fileName).Split('.', '_')[0];
            LastModified = System.IO.Directory.GetLastWriteTime(fileName);
        }

        public FileInfo()
        {
        }

        public bool IsNewer(FileInfo other)
        {
            return other.BaseName.Equals(BaseName) && LastModified - other.LastModified > new TimeSpan(0, 0, 2);
        }

        public class Reader : DocReader<FileInfo>
        {
            public override Term Identity(FileInfo value)
            {
                return new Term("baseName", value.BaseName);
            }

            public override Term ClearImport(string importId)
            {
                return new Term("baseName", importId);
            }

            public override Document Write(FileInfo value)
            {
                return NewDocument(
                    new Field("baseName", value.BaseName, Field.Store.YES, Field.Index.NOT_ANALYZED),
                    new Field("lastModified", DateTools.DateToString(value.LastModified, DateTools.Resolution.SECOND), Field.Store.YES, Field.Index.NO)
                );
            }

            public override FileInfo Read(Document doc)
            {
                return new FileInfo
                {
                    BaseName = doc.Get("baseName"),
                    LastModified = DateTools.StringToDate(doc.Get("lastModified"))
                };
            }
        }

    }

}
