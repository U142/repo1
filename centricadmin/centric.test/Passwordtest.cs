using System;
using System.Text;
using System.Collections.Generic;
using System.Linq;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using System.Security.Cryptography;



namespace centric.test
{
    [TestClass]
    public class Passwordtest
    {
        [TestMethod]
        public void TestMethod1()
        {
            SHA512 sha = SHA512.Create();
            byte[] inputBytes = System.Text.Encoding.ASCII.GetBytes("password");
            byte[] hashBytes = sha.ComputeHash(inputBytes);

            // Convert the byte array to hexadecimal string
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < hashBytes.Length; i++)
            {
                sb.Append(hashBytes[i].ToString("x2"));
                // To force the hex string to lower-case letters instead of
                // upper-case, use he following line instead:
                // sb.Append(hashBytes[i].ToString("x2")); 
            }

            string ting = sb.ToString();
            
        }
    }
}
