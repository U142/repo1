package no.ums.pas.parm.xml;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.w3c.dom.Document;

public class XmlWriterTest {
	
	@Test
	public void TestWriteFile() throws IOException,
			ParserConfigurationException {
		final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		final DocumentBuilder db = dbf.newDocumentBuilder();
		final Document xmlDoc = db.newDocument();

		new XmlWriter().writeXMLFile(xmlDoc, File
				.createTempFile("test", ".xml").getAbsolutePath());
	}
}