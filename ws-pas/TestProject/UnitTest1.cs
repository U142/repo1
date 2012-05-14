using System;
using System.Text;
using System.Collections.Generic;
using System.Linq;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using com.ums.PAS.CB;

namespace TestProject
{
    /// <summary>
    /// Summary description for UnitTest1
    /// </summary>
    [TestClass]
    public class UnitTest1
    {
        public UnitTest1()
        {
            //
            // TODO: Add constructor logic here
            //
        }

        private TestContext testContextInstance;

        /// <summary>
        ///Gets or sets the test context which provides
        ///information about and functionality for the current test run.
        ///</summary>
        public TestContext TestContext
        {
            get
            {
                return testContextInstance;
            }
            set
            {
                testContextInstance = value;
            }
        }

        #region Additional test attributes
        //
        // You can use the following additional attributes as you write your tests:
        //
        // Use ClassInitialize to run code before running the first test in the class
        // [ClassInitialize()]
        // public static void MyClassInitialize(TestContext testContext) { }
        //
        // Use ClassCleanup to run code after all tests in a class have run
        // [ClassCleanup()]
        // public static void MyClassCleanup() { }
        //
        // Use TestInitialize to run code before running each test 
        // [TestInitialize()]
        // public void MyTestInitialize() { }
        //
        // Use TestCleanup to run code after each test has run
        // [TestCleanup()]
        // public void MyTestCleanup() { }
        //
        #endregion

        [TestMethod]
        public void TestSerializeCB()
        {
            com.ums.UmsParm.UPolygon polygon = new com.ums.UmsParm.UPolygon();
            polygon.addPoint(5, 50);
            polygon.addPoint(10, 50);
            polygon.addPoint(7.5, 55);

            CB_ALERT_POLYGON poly = new CB_ALERT_POLYGON();
            poly.f_simulation = true;
            poly.l_comppk = 2;
            poly.l_deptpk = 1;
            poly.l_parent_refno = -1;
            poly.l_projectpk = 1234512345;
            poly.l_refno = 123;
            poly.l_userpk = 1;
            poly.messagepart = new CB_MESSAGEPART(1, "tester");
            poly.shape = polygon;
            String xml = polygon.Serialize();
            com.ums.UmsParm.UShape deser = com.ums.UmsParm.UShape.Deserialize("<?xml version=\"1.0\" encoding=\"utf-8\"?><UPolygon col_red=\"0\" col_green=\"0\" col_blue=\"0\" col_alpha=\"0\" f_disabled=\"0\" l_disabled_timestamp=\"0\"><polypoint lon=\"6.16135\" lat=\"52.95728\" xmlns=\"http://ums.no/ws/common/parm\" /><polypoint lon=\"6.12474\" lat=\"52.96027\" xmlns=\"http://ums.no/ws/common/parm\" /><polypoint lon=\"6.08899\" lat=\"52.9662\" xmlns=\"http://ums.no/ws/common/parm\" /><polypoint lon=\"6.05467\" lat=\"52.97498\" xmlns=\"http://ums.no/ws/common/parm\" /><polypoint lon=\"6.02233\" lat=\"52.98646\" xmlns=\"http://ums.no/ws/common/parm\" /><polypoint lon=\"5.99248\" lat=\"53.00048\" xmlns=\"http://ums.no/ws/common/parm\" /><polypoint lon=\"5.96557\" lat=\"53.0168\" xmlns=\"http://ums.no/ws/common/parm\" /><polypoint lon=\"5.94205\" lat=\"53.03517\" xmlns=\"http://ums.no/ws/common/parm\" /><polypoint lon=\"5.92227\" lat=\"53.0553\" xmlns=\"http://ums.no/ws/common/parm\" /><polypoint lon=\"5.90656\" lat=\"53.07687\" xmlns=\"http://ums.no/ws/common/parm\" /><polypoint lon=\"5.89515\" lat=\"53.09955\" xmlns=\"http://ums.no/ws/common/parm\" /><polypoint lon=\"5.88824\" lat=\"53.12297\" xmlns=\"http://ums.no/ws/common/parm\" /><polypoint lon=\"5.88592\" lat=\"53.14677\" xmlns=\"http://ums.no/ws/common/parm\" /><polypoint lon=\"5.88824\" lat=\"53.17056\" xmlns=\"http://ums.no/ws/common/parm\" /><polypoint lon=\"5.89515\" lat=\"53.19398\" xmlns=\"http://ums.no/ws/common/parm\" /><polypoint lon=\"5.90656\" lat=\"53.21666\" xmlns=\"http://ums.no/ws/common/parm\" /><polypoint lon=\"5.92227\" lat=\"53.23823\" xmlns=\"http://ums.no/ws/common/parm\" /><polypoint lon=\"5.94205\" lat=\"53.25836\" xmlns=\"http://ums.no/ws/common/parm\" /><polypoint lon=\"5.96557\" lat=\"53.27673\" xmlns=\"http://ums.no/ws/common/parm\" /><polypoint lon=\"5.99248\" lat=\"53.29305\" xmlns=\"http://ums.no/ws/common/parm\" /><polypoint lon=\"6.02233\" lat=\"53.30707\" xmlns=\"http://ums.no/ws/common/parm\" /><polypoint lon=\"6.05467\" lat=\"53.31855\" xmlns=\"http://ums.no/ws/common/parm\" /><polypoint lon=\"6.08899\" lat=\"53.32733\" xmlns=\"http://ums.no/ws/common/parm\" /><polypoint lon=\"6.12474\" lat=\"53.33326\" xmlns=\"http://ums.no/ws/common/parm\" /><polypoint lon=\"6.16135\" lat=\"53.33625\" xmlns=\"http://ums.no/ws/common/parm\" /><polypoint lon=\"6.19826\" lat=\"53.33625\" xmlns=\"http://ums.no/ws/common/parm\" /><polypoint lon=\"6.23487\" lat=\"53.33326\" xmlns=\"http://ums.no/ws/common/parm\" /><polypoint lon=\"6.27062\" lat=\"53.32733\" xmlns=\"http://ums.no/ws/common/parm\" /><polypoint lon=\"6.30493\" lat=\"53.31855\" xmlns=\"http://ums.no/ws/common/parm\" /><polypoint lon=\"6.33728\" lat=\"53.30707\" xmlns=\"http://ums.no/ws/common/parm\" /><polypoint lon=\"6.36713\" lat=\"53.29305\" xmlns=\"http://ums.no/ws/common/parm\" /><polypoint lon=\"6.39404\" lat=\"53.27673\" xmlns=\"http://ums.no/ws/common/parm\" /><polypoint lon=\"6.41756\" lat=\"53.25836\" xmlns=\"http://ums.no/ws/common/parm\" /><polypoint lon=\"6.43734\" lat=\"53.23823\" xmlns=\"http://ums.no/ws/common/parm\" /><polypoint lon=\"6.45305\" lat=\"53.21666\" xmlns=\"http://ums.no/ws/common/parm\" /><polypoint lon=\"6.46446\" lat=\"53.19398\" xmlns=\"http://ums.no/ws/common/parm\" /><polypoint lon=\"6.47137\" lat=\"53.17056\" xmlns=\"http://ums.no/ws/common/parm\" /><polypoint lon=\"6.47369\" lat=\"53.14677\" xmlns=\"http://ums.no/ws/common/parm\" /><polypoint lon=\"6.47137\" lat=\"53.12297\" xmlns=\"http://ums.no/ws/common/parm\" /><polypoint lon=\"6.46446\" lat=\"53.09955\" xmlns=\"http://ums.no/ws/common/parm\" /><polypoint lon=\"6.45305\" lat=\"53.07687\" xmlns=\"http://ums.no/ws/common/parm\" /><polypoint lon=\"6.43734\" lat=\"53.0553\" xmlns=\"http://ums.no/ws/common/parm\" /><polypoint lon=\"6.41756\" lat=\"53.03517\" xmlns=\"http://ums.no/ws/common/parm\" /><polypoint lon=\"6.39404\" lat=\"53.0168\" xmlns=\"http://ums.no/ws/common/parm\" /><polypoint lon=\"6.36713\" lat=\"53.00048\" xmlns=\"http://ums.no/ws/common/parm\" /><polypoint lon=\"6.33728\" lat=\"52.98646\" xmlns=\"http://ums.no/ws/common/parm\" /><polypoint lon=\"6.30493\" lat=\"52.97498\" xmlns=\"http://ums.no/ws/common/parm\" /><polypoint lon=\"6.27062\" lat=\"52.9662\" xmlns=\"http://ums.no/ws/common/parm\" /><polypoint lon=\"6.23487\" lat=\"52.96027\" xmlns=\"http://ums.no/ws/common/parm\" /><polypoint lon=\"6.19826\" lat=\"52.95728\" xmlns=\"http://ums.no/ws/common/parm\" /></UPolygon>");
            Assert.IsNotNull(xml);

        }
    }
}
