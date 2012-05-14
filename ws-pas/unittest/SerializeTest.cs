using System;
using System.Text;
using System.Collections.Generic;
using System.Linq;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using wsPASExec;
using com.ums.UmsParm;

namespace unittest
{
    [TestClass]
    public class SerializeTest
    {
        [TestMethod]
        public void TestPolygonSerializeAndDeserialize()
        {
            UPolygon poly = new UPolygon();
            poly.addPoint(21.2323, 6.3433);
            poly.addPoint(21.2323, 6.3433);
            poly.addPoint(21.2323, 6.3433);
            var xml = poly.Serialize();
            Console.WriteLine(xml);
            var shape = UPolygon.Deserialize(xml);

            Assert.IsInstanceOfType(shape, typeof(UPolygon));
            Assert.AreEqual(21.2323, ((UPolygon)shape).getPoint(0).lon);
            Assert.AreEqual(6.3433, ((UPolygon)shape).getPoint(0).lat);
        }

        [TestMethod]
        public void TestUPLMNSerializeAndDeserialize()
        {
            UPLMN national = new UPLMN();
            var xml = national.Serialize();
            Console.WriteLine(xml);
            var shape = UPLMN.Deserialize(xml);

            Assert.IsInstanceOfType(shape, typeof(UPLMN));
        }

        [TestMethod]
        public void TestUEllipseSerializeAndDeserialize()
        {
            UEllipse ellipse = new UEllipse();
            ellipse.setCenter(24.34, 8.23);
            ellipse.setExtents(25.12, 9.11);
            var xml = ellipse.Serialize();
            Console.WriteLine(xml);
            var shape = UEllipse.Deserialize(xml);

            Assert.IsInstanceOfType(shape, typeof(UEllipse));
            Assert.AreEqual(((UEllipse)shape).lon, 24.34);
            Assert.AreEqual(((UEllipse)shape).lat, 8.23);
            Assert.AreEqual(((UEllipse)shape).x, 25.12);
            Assert.AreEqual(((UEllipse)shape).y, 9.11);
        }
    }
}
