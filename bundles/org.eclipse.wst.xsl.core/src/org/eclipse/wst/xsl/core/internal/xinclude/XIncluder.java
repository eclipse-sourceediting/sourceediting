/******************************************************************************
* Copyright (c) 2008 Lars Vogel 
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/eplv10.html
*
* Contributors:
* Lars Vogel - Lars.Vogel@gmail.com - initial API and implementation
* David Carver - STAR - bug 217919 - renamed to XIncluder from MyXIncluder
*******************************************************************************/

package org.eclipse.wst.xsl.core.internal.xinclude;

import java.io.File;
import java.io.FileOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

public class XIncluder {

	public void extractXMLFile(String in, String out) throws Exception {
		Document document = null;
		File file;

		file = new File(in);
		file.getAbsolutePath();
		file.lastModified();

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setXIncludeAware(true);
		dbf.setNamespaceAware(true);
		DocumentBuilder dom = dbf.newDocumentBuilder();
		document = dom.parse(file);
		// ---- Use a XSLT transformer for writing the new XML file ----
		Transformer transformer = TransformerFactory.newInstance()
				.newTransformer();
		// TODO: This should be read from the input file and not hardcoded
		// here
		transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC,
				"-//OASIS//DTD DocBook XML V4.5//EN");
		transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM,
				"../../docbook-xml-4.5/docbookx.dtd");

		DOMSource source = new DOMSource(document);
		FileOutputStream os = new FileOutputStream(new File(out));
		StreamResult result = new StreamResult(os);
		transformer.transform(source, result);
	}
}
