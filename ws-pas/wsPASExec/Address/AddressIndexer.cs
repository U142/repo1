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
using com.ums.UmsCommon;
using com.ums.UmsParm;

namespace com.ums.address
{
    public class AddressIndexer
    {
        private readonly string _backupPath;
        private readonly Lucene.Net.Store.Directory _dir;

        private readonly IDictionary<string, DocReader<AddressInfo>> readers = new Dictionary<string, DocReader<AddressInfo>>();

        public Searcher Searcher { get; private set; }

        public AddressIndexer(string indexFolder, string backupPath) 
        {
            _backupPath = backupPath;
            _dir = FSDirectory.Open(new DirectoryInfo(indexFolder));
            // Make sure that the index directory exists and is created.
            if (!IndexReader.IndexExists(_dir))
            {
                new IndexWriter(_dir, null, IndexWriter.MaxFieldLength.LIMITED).Close();
            }
            Searcher = new IndexSearcher(_dir, true);
        }

        public AddressIndexer(string baseDir)
            : this(Path.Combine(baseDir, "index"), Path.Combine(baseDir, "backup")) { }

        public AddressIndexer() 
            : this(Path.Combine(Environment.GetFolderPath(Environment.SpecialFolder.ApplicationData), "umsAddress")) { }

        internal void Backup()
        {
            var backupFolder = Lucene.Net.Store.FSDirectory.Open(new DirectoryInfo(Path.Combine(_backupPath, "Backup." + DateTime.Now.ToString("yyyyMMdd.HHmmss"))));
            ULog.write("Performing backup to " + backupFolder);
            var backupWriter = new IndexWriter(backupFolder, new SimpleAnalyzer(), IndexWriter.MaxFieldLength.LIMITED);
            var reader = IndexReader.Open(_dir, true);
            backupWriter.AddIndexes(new IndexReader[] { reader });
            reader.Close();
            backupWriter.Close();
            ULog.write("Backup done");
        }

        internal IndexWriter OpenWriter()
        {
            return IndexWriter.IsLocked(_dir) ? null : new IndexWriter(_dir, new SimpleAnalyzer(), IndexWriter.MaxFieldLength.LIMITED);
        }

        internal void Refresh()
        {
            var old = Searcher;
            Searcher = new IndexSearcher(_dir, true);
            old.Close();
        }

        public UMapBounds FindMunicipalBounds(ref List<UMunicipalDef> list)
        {
            UMapBounds bounds = new UMapBounds();
            bounds.l_bo = 180;
            bounds.r_bo = -180;
            bounds.u_bo = -90;
            bounds.b_bo = 90;

            foreach (UMunicipalDef municipal in list)
            {
                List<AddressInfo> addresslist = FindInMunicipal(municipal);
                foreach(AddressInfo adr in addresslist)
                {
                    if (adr.lat == 0 || adr.lng == 0 || adr.lat == -1.0 || adr.lng == -1.0)
                        continue;
                    if (adr.lng < bounds.l_bo)
                        bounds.l_bo = adr.lng;
                    if (adr.lng > bounds.r_bo)
                        bounds.r_bo = adr.lng;
                    if (adr.lat < bounds.b_bo)
                        bounds.b_bo = adr.lat;
                    if (adr.lat > bounds.u_bo)
                        bounds.u_bo = adr.lat;


                }
            }
            return bounds;
        }

        public List<AddressInfo> FindInArea(double lon1, double lat1, double lon2, double lat2, int maxhits)
        {
            return FindForQuery(AddressInfo.FindInArea(lon1, lat1, lon2, lat2), maxhits);
        }

        public AddressInfo FindClosestAddressFromPoint(UMapPoint p)
        {
            AddressInfo closest = null;
            // As we get closer to the poles, we need to multiply the longtitude to accomdate for the earth
            // being round.
            double lonMul = 1/Math.Cos((p.lat / 180.0) * Math.PI);
            // Start at a delta of 0.00005 (about 50m) up to 0.0040 (about 400m)
            for (double latDelta = 0.00005; latDelta < 0.0041; latDelta *= 2)
            {
                var lonDelta = latDelta * lonMul;
                List<AddressInfo> addresslist = FindInArea(p.lon - lonDelta, p.lat - latDelta, p.lon + lonDelta, p.lat + latDelta, 50);
                if (addresslist.Count <= 0)
                    continue;
                double smallest_dist = 9999;
                foreach (AddressInfo adr in addresslist)
                {
                    double tmp = p.distance(adr.lng, adr.lat);
                    if (tmp < smallest_dist)
                    {
                        smallest_dist = tmp;
                        closest = adr;
                    }
                }
                break;
            }
            return closest ;
        }


        public List<AddressInfo> FindInMunicipal(UMunicipalDef municipal)
        {
            return FindForQuery(AddressInfo.FindByAddress(municipal.sz_municipalid, "", "", ""), 500000);
        }

        public List<AddressInfo> FindById(List<String> idList)
        {
            List<AddressInfo> addressList = new List<AddressInfo>();
            foreach(string id in idList)
            {
                var result = FindForQuery(AddressInfo.FindById(id), 5);
                addressList.AddRange(result);
            }
            return addressList;
        }

        public List<AddressInfo> FindByAddress(string muncipalId, string streetId, string houseId, string houseLetter)
        {
            return FindForQuery(AddressInfo.FindByAddress(muncipalId, streetId, houseId, houseLetter), 100000);
        }

        public List<AddressInfo> FindOnlyMobile()
        {
            return FindForQuery(AddressInfo.FindOnlyMobile(), 100000);
        }

        private List<AddressInfo> FindForQuery(Query query, int maxhits)
        {
            var result = Searcher.Search(query, null, maxhits);
            var addressInfos = new List<AddressInfo>(result.totalHits);
            foreach (var scoreDoc in result.scoreDocs)
            {
                var doc = Searcher.Doc(scoreDoc.doc);
                if (doc.Get("baseType").Equals(typeof(PersonInfo).FullName))
                {
                    addressInfos.Add(new PersonInfo.Reader().Read(doc));
                }
                else if (doc.Get("baseType").Equals(typeof(CompanyInfo).FullName))
                {
                    addressInfos.Add(new CompanyInfo.Reader().Read(doc));
                }
            }
            return addressInfos;
        }

    }

}
