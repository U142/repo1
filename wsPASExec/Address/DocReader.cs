using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Lucene.Net.Documents;
using Lucene.Net.Search;
using Lucene.Net.Index;

namespace com.ums.address
{
    public abstract class DocReader<T>
    {
        public string TypeName { get { return typeof(T).FullName; } }

        protected Document NewDocument(params Fieldable[] fields)
        {
            var doc = new Document();
            var type = typeof(T);
            doc.Add(new Field("baseType", type.FullName, Field.Store.YES, Field.Index.NOT_ANALYZED));
            while (type != null)
            {
                doc.Add(new Field("type", type.FullName, Field.Store.YES, Field.Index.NOT_ANALYZED));
                type = type.BaseType;
            }
            foreach (var field in fields)
            {
                doc.Add(field);
            }
            return doc;
        }

        public Query ToQuery(T value)
        {
            var query = new BooleanQuery();
            query.Add(new TermQuery(new Term("type", typeof(T).FullName)), BooleanClause.Occur.MUST);
            query.Add(new TermQuery(Identity(value)), BooleanClause.Occur.MUST);
            return query;
        }

        public abstract Term ClearImport(string importId);
        public abstract T Read(Document doc);
        public abstract Document Write(T value);
        public abstract Term Identity(T value);

        protected Fieldable StoredNotAnalyzed(string name, string value)
        {
            return new Field(name, value, Field.Store.YES, Field.Index.NOT_ANALYZED);
        }

        protected Fieldable StoredNotIndexed(string name, string value)
        {
            return new Field(name, value, Field.Store.YES, Field.Index.NO);
        }

        protected Fieldable StoredNotIndexed(string name, DateTime value)
        {
            return new Field(name, DateTools.DateToString(value, DateTools.Resolution.SECOND), Field.Store.YES, Field.Index.NO);
        }

        protected NumericField StoredIndexedNumber(string name)
        {
            return new NumericField(name, Field.Store.YES, true);
        }

        protected NumericField StoredNumber(string name)
        {
            return new NumericField(name, Field.Store.YES, false);
        }

    }

    public abstract class DataImport<T> where T : new()
    {
        public T Import(string importId, IDictionary<string, string> source)
        {
            T value = new T();
            ImportInto(value, importId, source);
            return value;
        }

        protected abstract void ImportInto(T value, string importId, IDictionary<string, string> source);
    }
}
