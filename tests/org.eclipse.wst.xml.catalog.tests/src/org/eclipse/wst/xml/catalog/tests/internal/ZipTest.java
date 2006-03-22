package org.eclipse.wst.xml.catalog.tests.internal;

import java.io.InputStream;
import java.net.URL;

import junit.framework.TestCase;

public class ZipTest extends TestCase {

	 public void testZip() throws Exception{
		  
		  URL url = new URL("jar:file:/D:/tcs/catalogJarTest/schemas.jar!/data/catalog.xsd");
		  InputStream is = url.openStream();
    }
}
