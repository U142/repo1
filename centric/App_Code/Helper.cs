using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

using System.Security.Cryptography;
using System.Text;

/// <summary>
/// Summary description for Helper
/// </summary>
public class Helper
{
	public Helper()
	{
		//
		// TODO: Add constructor logic here
		//
	}

    public static string CreateSHA512Hash(string input)
    {
        SHA512 sha = SHA512.Create();
        byte[] inputBytes = System.Text.Encoding.ASCII.GetBytes(input);
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
        return sb.ToString();
    }

    public static string FormatDate(long timestamp)
    {
        string tmp = timestamp.ToString();
        if (timestamp > 0)
            return tmp.Substring(6, 2) + "-" + tmp.Substring(4, 2) + "-" + tmp.Substring(0, 4) + " " + tmp.Substring(8, 2) + ":" + tmp.Substring(10, 2);
        else return "0";

    }
}
