using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

using Lucene.Net.Documents;
using Lucene.Net.Index;
using Lucene.Net.Search;
using System.Globalization;
using com.ums.UmsCommon;

using com.ums.PAS.Address;

namespace com.ums.address
{

    public abstract class AddressInfo
    {

        public string Id { get; set; }
        public string ImportId { get; set; }
        public double lng { get; set; }
        public double lat { get; set; }
        public string CoorCode { get; set; }
        public string Fullname { get; set; }
        public string Address { get; set; }
        public string MuncipalId { get; set; }
        public string HouseId { get; set; }
        public string StreetId { get; set; }
        public string HouseLetter { get; set; }
        public string HouseApartment { get; set; }
        public string Zip { get; set; }
        public string ZipName { get; set; }
        public string Mobile { get; set; }
        public string Phone { get; set; }
        public long Gno { get; set; }
        public long Bno { get; set; }


        public abstract class AddressImport<T> : DataImport<T> where T : AddressInfo, new()
        {

            private readonly string _idParm;
            private readonly string _xParm;
            private readonly string _yParm;

            public AddressImport (string idParm, string xParm, string yParm) {
                _idParm = idParm;
                _xParm = xParm;
                _yParm = yParm;
            }

            protected override void ImportInto(T value, string importId, IDictionary<string, string> source)
            {
                value.Id = Helpers.CreateSHA512Hash(source[_idParm]);
                value.ImportId = importId;

                string coordX = source[_xParm];
                string coordY = source[_yParm];
                if (coordX.Length == 0 || coordY.Length == 0)
                {
                    value.lng = -1;
                    value.lat = -1;
                }
                else
                {
                    double lat = 0, lng = 0;
                    double y = Double.Parse(coordY.Replace(",", "."), com.ums.UmsCommon.UCommon.UGlobalizationInfo);
                    double x = Double.Parse(coordX.Replace(",", "."), com.ums.UmsCommon.UCommon.UGlobalizationInfo);
                    com.ums.UmsCommon.CoorConvert.UTM.UTM2LL(23, x, y, "33", 'V', ref lat, ref lng);
                    value.lng = lng;
                    value.lat = lat;
                }
            }

        }
        public UAddress toUAddress()
        {
            var adr = new UAddress();
            adr.address = Address;
            adr.bday = (this as PersonInfo).Birthday.ToString();
            adr.bedrift = 0;
            adr.bno = (int) Bno;
            adr.gno = (int) Gno;
            adr.hasfixed = (Phone.Trim().Length > 0) ? 1 : 0;
            adr.hasmobile = (Mobile.Trim().Length > 0) ? 1 : 0;
            adr.houseno = (HouseId.Trim().Length > 0 ? int.Parse(HouseId) : 0);
            adr.importid = -1;
            adr.kondmid = Id;
            adr.lon = lat;
            adr.lat = lng;
            adr.letter = HouseLetter;
            adr.mobile = Mobile;
            adr.municipalid = MuncipalId;
            adr.name = Fullname;
            adr.number = Phone;
            adr.postarea = ZipName;
            adr.postno = Zip;
            adr.region = (MuncipalId.Trim().Length > 0 ? int.Parse(MuncipalId) : 0);
            adr.streetid = (StreetId.Trim().Length > 0 ? int.Parse(StreetId) : 0);
            adr.xycode = CoorCode;
            return adr;
        }



        public static Query FindInArea(double lon1, double lat1, double lon2, double lat2)
        {
            var query = new BooleanQuery();
            query.Add(NumericRangeQuery.NewDoubleRange("lng", Math.Min(lon1, lon2), Math.Max(lon1, lon2), true, true), BooleanClause.Occur.MUST);
            query.Add(NumericRangeQuery.NewDoubleRange("lat", Math.Min(lat1, lat2), Math.Max(lat1, lat2), true, true), BooleanClause.Occur.MUST);
            return query;
        }

        public static Query FindByAddress(string muncipalId, string streetId, string houseId, string houseLetter)
        {
            var query = new BooleanQuery();
            if (muncipalId != null && muncipalId.Trim().Length > 0)
            {
                query.Add(new TermQuery(new Term("muncipalId", muncipalId)), BooleanClause.Occur.MUST);
            }
            if (streetId != null && streetId.Trim().Length > 0)
            {
                query.Add(new TermQuery(new Term("streetId", streetId)), BooleanClause.Occur.MUST);
            }
            if (houseId != null && houseId.Trim().Length > 0)
            {
                query.Add(new TermQuery(new Term("houseId", houseId)), BooleanClause.Occur.MUST);
            }
            if (houseLetter != null && houseLetter.Trim().Length > 0)
            {
                query.Add(new TermQuery(new Term("houseLetter", houseLetter)), BooleanClause.Occur.MUST);
            }
            return query;
        }

        public static Query FindById(String Id)
            {
                var query = new BooleanQuery();
                if (Id.Length > 0)
                {
                    query.Add(new TermQuery(new Term("id", Id)), BooleanClause.Occur.MUST);
                }
                else
                {
                    //should never occur, but will be needed if someone searches for an empty id
                    query.Add(new TermQuery(new Term("id", "-")), BooleanClause.Occur.MUST);
                }
                return query;
            }

        internal static Query FindOnlyMobile()
        {
            var query = new BooleanQuery();
            query.Add(new WildcardQuery(new Term("mobile", "?*")), BooleanClause.Occur.MUST);
            return query;
        }

        public abstract class AddressReader<T> : DocReader<T> where T : AddressInfo, new()
        {
            public override T Read(Document doc)
            {
                return new T() {
                    Id = doc.Get("id"),
                    ImportId = doc.Get("importId"),
                    lng = Double.Parse(doc.Get("lng")),
                    lat = Double.Parse(doc.Get("lat")),
                    CoorCode = doc.Get("coordCode"),
                    Fullname = doc.Get("fullName"),
                    Address = doc.Get("address"),
                    MuncipalId = doc.Get("muncipalId"),
                    StreetId = doc.Get("streetId"),
                    HouseId = doc.Get("houseId"),
                    HouseLetter = doc.Get("houseLetter"),
                    HouseApartment = doc.Get("houseApartment"),
                    Zip = doc.Get("zip"),
                    ZipName = doc.Get("zipName"),
                    Mobile = doc.Get("mobile"),
                    Phone = doc.Get("phone"),
                    Gno = Int64.Parse(doc.Get("gno")),
                    Bno = Int64.Parse(doc.Get("bno")),
                };
            }

            public override Document Write(T value)
            {
                var doc = NewDocument(
                    StoredNotAnalyzed("id", value.Id),
                    StoredNotAnalyzed("importId", value.ImportId),
                    StoredIndexedNumber("lng").SetDoubleValue(value.lng),
                    StoredIndexedNumber("lat").SetDoubleValue(value.lat),
                    StoredNotIndexed("coordCode", value.CoorCode),
                    StoredNotIndexed("fullName", value.Fullname),
                    StoredNotIndexed("address", value.Address),
                    StoredNotAnalyzed("muncipalId", value.MuncipalId),
                    StoredNotAnalyzed("streetId", value.StreetId),
                    StoredNotAnalyzed("houseId", value.HouseId),
                    StoredNotAnalyzed("houseLetter", value.HouseLetter),
                    StoredNotAnalyzed("houseApartment", value.HouseApartment),
                    StoredNotAnalyzed("zip", value.Zip),
                    StoredNotIndexed("zipName", value.ZipName),
                    StoredNotAnalyzed("mobile", value.Mobile),
                    StoredNotAnalyzed("phone", value.Phone),
                    StoredIndexedNumber("gno").SetLongValue(value.Gno),
                    StoredIndexedNumber("bno").SetLongValue(value.Bno)
                );
                Update(value, doc);
                return doc;
            }

            protected abstract void Update(T value, Document document);


            public override Term Identity(T value)
            {
                return new Term("id", value.Id);
            }

            public override Term ClearImport(string importId)
            {
                return new Term("importId", importId);
            }

        }
    }

    public class PersonInfo : AddressInfo
    {
        public DateTime Birthday { get; set; }
        public string Surname { get; set; }
        public string Firstname { get; set; }
        public string Midname { get; set; }
        public string FamilyId { get; set; }

        public class Import : AddressImport<PersonInfo>
        {
            public Import() : base("l_personcode", "l_xcoord", "l_ycoord") { }

            protected override void ImportInto(PersonInfo value, string importId, IDictionary<string, string> values)
            {
                base.ImportInto(value, importId, values);

                value.CoorCode = values["c_xycode"];
                value.Fullname = values["sz_fullname"];
                value.Address = values["sz_address"];
                value.MuncipalId = values["l_municipalno"];
                value.HouseId = values["l_houseno"];
                value.StreetId = values["l_streetid"];
                value.HouseLetter = values["sz_letter"];
                value.HouseApartment = values["sz_apartmentid"];
                value.Zip = values["sz_postno"];
                value.ZipName = values["sz_place"];
                value.Mobile = values["l_mobile"];
                value.Phone = values["l_phone"];

                value.Birthday = DateTime.ParseExact(values["l_birthday"], "yyyyMMdd", CultureInfo.CurrentCulture);
                value.Surname = values["sz_surname"];
                value.Firstname = values["sz_firstname"];
                value.Midname = values["sz_midname"];
                value.Gno = (values["l_gno"].Length == 0) ? -1 : Int64.Parse(values["l_gno"]);
                value.Bno = (values["l_bno"].Length == 0) ? -1 : Int64.Parse(values["l_bno"]);
                value.FamilyId = values["l_familyno"];
            }
        }

        public class Reader : AddressReader<PersonInfo> 
        {
            public override PersonInfo Read(Document doc)
            {
                var person = base.Read(doc);
                person.Birthday = DateTools.StringToDate(doc.Get("birthday"));
                person.Surname = doc.Get("surname");
                person.Firstname = doc.Get("firstname");
                person.Midname = doc.Get("midname");
                person.FamilyId = doc.Get("familyId");
                return person;
            }

            protected override void Update(PersonInfo value, Document document)
            {
                document.Add(StoredNotIndexed("birthday", value.Birthday));
                document.Add(StoredNotIndexed("surname", value.Surname));
                document.Add(StoredNotIndexed("firstname", value.Firstname));
                document.Add(StoredNotIndexed("midname", value.Midname));
                document.Add(StoredNotIndexed("familyId", value.FamilyId));
            }

        }
    }

    // fir_id	fir_ftknr	fir_Firma	fir_gateadr	husnr	oppgang	fir_ps_gatepostnr	ps_poststed	
    // fir_telefon	x_koord	y_koord	xy_kode	fir_kom_kommunenr	fyl_fylkesnr	nat_nacekode	
    // nat_nacetekst	fir_ansattgruppe_bedrift	adr_adrnr	adr_gatenavnkode	eid_gårdsnr	eid_bruksnr
    public class CompanyInfo : AddressInfo
    {
        public string CompanyId { get; set; }
        public string EmployeeRange { get; set; }
        public string NaceText { get; set; }
        public string NaceCode { get; set; }

        public class Import : AddressImport<CompanyInfo>
        {
            public Import() : base("fir_ftknr", "x_koord", "y_koord") { }

            protected override void ImportInto(CompanyInfo value, string importId, IDictionary<string, string> values)
            {
                base.ImportInto(value, importId, values);

                value.CoorCode = values["xy_kode"];
                value.Fullname = values["fir_Firma"];
                value.Address = values["fir_gateadr"];
                value.MuncipalId = values["fir_kom_kommunenr"];
                value.HouseId = values["husnr"];
                value.StreetId = values["adr_gatenavnkode"];
                value.HouseLetter = values["oppgang"];
                value.HouseApartment = "";
                value.Zip = values["fir_ps_gatepostnr"];
                value.ZipName = values["ps_poststed"];
                value.Mobile = "";
                value.Phone = values["fir_telefon"];
                value.Gno = (values["eid_gårdsnr"].Length == 0) ? -1 : Int64.Parse(values["eid_gårdsnr"]);
                value.Bno = (values["eid_bruksnr"].Length == 0) ? -1 : Int64.Parse(values["eid_bruksnr"]);

                value.CompanyId = values["fir_ftknr"];
                value.EmployeeRange = values["fir_ansattgruppe_bedrift"];
                value.NaceText = values["nat_nacetekst"];
                value.NaceCode = values["nat_nacekode"];
            }
        }

        public class Reader : AddressReader<CompanyInfo>
        {
            public override CompanyInfo Read(Document doc)
            {
                var companyInfo = base.Read(doc);
                companyInfo.CompanyId = doc.Get("companyId");
                companyInfo.EmployeeRange = doc.Get("employeeRange");
                companyInfo.NaceText = doc.Get("naceText");
                companyInfo.NaceCode = doc.Get("naceCode");
                return companyInfo;
            }

            protected override void Update(CompanyInfo value, Document document)
            {
                document.Add(StoredNotAnalyzed("companyId", value.CompanyId));
                document.Add(StoredNotIndexed("employeeRange", value.EmployeeRange));
                document.Add(StoredNotIndexed("naceText", value.NaceText));
                document.Add(StoredNotIndexed("naceCode", value.NaceCode));
            }
        }
           
    }
}
