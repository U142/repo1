using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using NUnit.Framework;
using com.ums.UmsCommon;

namespace UnitTests.cs
{
    [TestFixture]
    public class UnitTests
    {
        static void Main(string[] args)
        {
            String key = AppKeyStore.getNextKey();
            Console.WriteLine(AppKeyStore.isKeyValid(key));
            Console.WriteLine(AppKeyStore.isKeyValid(key));
            Console.ReadKey();
        }
        [TestCase]
        public void TestOneTimeKey()
        {
            String key = AppKeyStore.getNextKey();
            Assert.IsTrue(AppKeyStore.isKeyValid(key));
            Assert.IsFalse(AppKeyStore.isKeyValid(key));
        }
    }
}
