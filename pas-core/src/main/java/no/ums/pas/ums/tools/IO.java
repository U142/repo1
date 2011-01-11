package no.ums.pas.ums.tools;

import java.io.*;
import java.nio.ByteBuffer;

public class IO
{
	public static byte[] ConvertInputStreamtoByteArray(InputStream in) throws IOException 
	{
	//StringBuffer out = new StringBuffer();
	/*byte[] b = new byte[4096];
	for (int n; (n = in.read(b)) != -1;) 
	{
		out.append(new String(b, 0, n));
	}
	ByteBuffer buf = new ByteBuffer();
	return out.toString().getBytes();*/
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int max = 1024;
		byte [] b = new byte[max];
		//ByteBuffer buff = ByteBuffer.allocate(max);
		int offset = 0;
		int n;
		for (; (n = in.read(b, 0, max)) != -1;)
		{
			//buff.put(b, offset, n);
			//out.write(b, offset, n);
			out.write(b, 0, n);
			offset+=n;
		}
		
		//byte [] ret = new byte[offset];
		//buff.get(ret, 0, offset);
		return out.toByteArray();
	}
}