package no.ums.pas.parm.xml;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.w3c.dom.Document;

public class XmlWriterTest
{
	@Test
	public void TestWriteFile() throws IOException, ParserConfigurationException
	{
		Document xmlDoc = null;
		DocumentBuilder db = null;
		DocumentBuilderFactory dbf = null;

		dbf = DocumentBuilderFactory.newInstance();
		db = dbf.newDocumentBuilder();
		xmlDoc = db.newDocument();
		File f = File.createTempFile("test", ".xml");
		String filepath = f.getAbsolutePath();
		new XmlWriter().writeXMLFile(xmlDoc, filepath);
	}
}