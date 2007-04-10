/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.sse.unittests.minortools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.eclipse.wst.xml.core.tests.util.CommonXML;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;



/**
 * Modifies plugin.xml and fragment.xml files to not require specific versions
 * of their plugin dependencies.
 * 
 * @author nitin
 */
public class VersionRemover {

	char[] charbuff = new char[2048];
	StringBuffer s = null;

	public VersionRemover() {
		super();
	}



	public static void main(String[] args) {
		if (args.length < 1)
			new VersionRemover().visit(new File("d:/target"));
		else
			new VersionRemover().visit(new File(args[0]));
	}



	protected void visit(File file) {
		// Skip directories like org.eclipse.*, org.apache.*, and org.junit.*
		if (file.isDirectory() && !file.getName().startsWith("org.eclipse.") && !file.getName().startsWith("org.apache") && !file.getName().startsWith("org.junit")) {
			String[] contents = file.list();
			for (int i = 0; i < contents.length; i++)
				visit(new File(file.getAbsolutePath() + '/' + contents[i]));
		}
		else {
			fixupFile(file);
		}
	}

	protected void fixupFile(File file) {
		// only load and fixup files named plugin.xml or fragment.xml under eclipse\plugins\XXXXXXX.*
		if (!(file.getName().equalsIgnoreCase("plugin.xml") || file.getName().equalsIgnoreCase("fragment.xml")) || file.getAbsolutePath().indexOf("eclipse\\plugins\\XXXXXXX.") == -1)
			return;
		//		System.out.println(file.getAbsolutePath());
		try {
			Document doc = CommonXML.getDocumentBuilder().parse(file);
			NodeList imports = null;
			if (file.getName().equalsIgnoreCase("plugin.xml"))
				imports = doc.getElementsByTagName("import");
			else if (file.getName().equalsIgnoreCase("fragment.xml"))
				imports = doc.getElementsByTagName("fragment");
			boolean changed = false;
			for (int i = 0; i < imports.getLength(); i++) {
				Node importNode = imports.item(i);
				if (importNode.getNodeName().equalsIgnoreCase("import") && importNode.getAttributes().getNamedItem("version") != null) {
					changed = true;
					importNode.getAttributes().removeNamedItem("version");
				}
				if (importNode.getAttributes().getNamedItem("plugin-version") != null) {
					changed = true;
					importNode.getAttributes().removeNamedItem("plugin-version");
				}
				if (importNode.getAttributes().getNamedItem("match") != null) {
					importNode.getAttributes().removeNamedItem("match");
					changed = true;
				}
			}
			if (changed) {
				FileOutputStream ostream = new FileOutputStream(file.getAbsolutePath());
				CommonXML.serialize(doc, ostream);
				ostream.close();
				System.out.println("Modified " + file.getAbsolutePath());
			}
		}
		catch (SAXException e) {
			System.err.println(file.getPath() + ": " + e);
		}
		catch (IOException e) {
			System.err.println(file.getPath() + ": " + e);
		}
	}
}
