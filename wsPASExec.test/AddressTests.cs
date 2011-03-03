using System;
using System.Text;
using System.Collections.Generic;
using System.Linq;
using Microsoft.VisualStudio.TestTools.UnitTesting;

using System.IO;

namespace com.ums.address
{

    sealed class TestDir : IDisposable
    {
        private readonly string _dirName;

        private TestDir(string dirName)
        {
            _dirName = dirName;
            System.IO.Directory.CreateDirectory(_dirName);
        }

        public TestDir() : this(System.IO.Path.Combine(System.IO.Path.GetTempPath(), System.IO.Path.GetRandomFileName())) {}

        public string Path
        {
            get
            {
                if (!System.IO.Directory.Exists(_dirName)) throw new ObjectDisposedException(GetType().Name);
                return _dirName;
            }
        }

        public TestDir SubFolder(string name)
        {
            return new TestDir(System.IO.Path.Combine(_dirName, name));
        }

        ~TestDir() { Dispose(false); }
        public void Dispose() { Dispose(true); }
        private void Dispose(bool disposing)
        {
            if (disposing)
            {
                GC.SuppressFinalize(this);
            }
            try { System.IO.Directory.Delete(_dirName); }
            catch { } // best effort
        }
    }

    /// <summary>
    /// Summary description for AddressTests
    /// </summary>
    [TestClass]
    [DeploymentItem("TestData", "TestData")]
    [DeploymentItem("BedriftData", "BedriftData")]
    public class AddressTests
    {

        /// <summary>
        ///Gets or sets the test context which provides
        ///information about and functionality for the current test run.
        ///</summary>
        public TestContext TestContext { get; set; }

        [TestMethod]
        public void TestPersonIndexing()
        {
            using (var workDir = new TestDir())
            {
                var adrIndex = new AddressIndexer(workDir.Path);
                
                var personImport = new PersonImport(adrIndex, Path.Combine(Path.GetDirectoryName(typeof(AddressTests).Assembly.Location), "TestData"));
                personImport.RefreshDatabase();
                var persons = adrIndex.FindByAddress("236", "1107", "37", "A");
                Assert.AreNotEqual(0, persons.Count);
                Console.WriteLine(persons[0].Mobile);
            }
        }

        [TestMethod]
        public void TestCompanyIndexing()
        {
            using (var workDir = new TestDir())
            {
                var adrIndex = new AddressIndexer(workDir.Path);

                var companyImport = new CompanyImport(adrIndex, Path.GetDirectoryName(typeof(AddressTests).Assembly.Location));
                companyImport.RefreshDatabase();
                var x = adrIndex.Searcher.MaxDoc();
                var company = adrIndex.FindByAddress("0238", "1255", "140", null);
                Assert.AreNotEqual(0, company.Count);
            }
        }


        [TestMethod]
        public void TestOnlyMobile()
        {
            using (var workDir = new TestDir())
            {
                var adrIndex = new AddressIndexer(workDir.Path);

                var personImport = new PersonImport(adrIndex, Path.Combine(Path.GetDirectoryName(typeof(AddressTests).Assembly.Location), "TestData"));
                personImport.RefreshDatabase();
                var persons = adrIndex.FindOnlyMobile();
                Assert.AreNotEqual(0, persons.Count);
                foreach (var person in persons)
                {
                    Assert.IsFalse(string.IsNullOrWhiteSpace(person.Mobile));
                }
            }
        }

        [TestMethod]
        public void TestFullCompanyIndex()
        {
            using (var workDir = new TestDir())
            {
                var adrIndex = new AddressIndexer(workDir.Path);

                var personImport = new CompanyImport(adrIndex);
                personImport.RefreshDatabase();
                Console.WriteLine(adrIndex.Searcher.MaxDoc());
            }
        }
    }
}
