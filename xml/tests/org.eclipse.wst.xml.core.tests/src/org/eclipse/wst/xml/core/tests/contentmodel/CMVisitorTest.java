/*******************************************************************************
 * Copyright (c) 2004, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xml.core.tests.contentmodel;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.core.runtime.IPlatformRunnable;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.ModelQuery;
import org.eclipse.wst.xml.core.internal.contentmodel.modelqueryimpl.ModelQueryImpl;
import org.eclipse.wst.xml.core.internal.contentmodel.modelqueryimpl.XMLAssociationProvider;
import org.eclipse.wst.xml.core.internal.contentmodel.util.CMDocumentCache;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class CMVisitorTest implements IPlatformRunnable {
	public Object run(Object a) {
		String args[] = (String[]) a;
		if (args.length > 0) {
			test(args[0]);
		}
		else {
			System.out.println("xml file name argument required"); //$NON-NLS-1$
		}
		return null;
	}

	protected void test(String fileName) {

		ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
		try {
			XMLAssociationProvider provider = new XMLAssociationProvider(new CMDocumentCache()) {
			};

			ModelQuery mq = new ModelQueryImpl(provider);

			Thread.currentThread().setContextClassLoader(CMVisitorTest.class.getClassLoader());
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document document = builder.parse(fileName);

			/*
			 * ClassLoader prevClassLoader =
			 * Thread.currentThread().getContextClassLoader();
			 * Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
			 * Class theClass =
			 * Class.forName("org.apache.xerces.parsers.DOMParser"); DOMParser
			 * parser = (DOMParser)theClass.newInstance();
			 * Thread.currentThread().setContextClassLoader(prevClassLoader);
			 * parser.parse(new InputSource(fileName)); Document document =
			 * parser.getDocument();
			 */
			visitNode(document, mq, 0);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			Thread.currentThread().setContextClassLoader(originalClassLoader);
		}
	}

	protected void visitNode(Node node, ModelQuery mq, int indent) {
		CMNode cmnode = mq.getCMNode(node);
		printlnIndented(indent, "node :" + node.getNodeName() + " cmnode : " + (cmnode != null ? cmnode.getNodeName() : "null")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		NamedNodeMap map = node.getAttributes();
		if (map != null) {
			indent += 2;
			int mapLength = map.getLength();
			for (int i = 0; i < mapLength; i++) {
				visitNode(map.item(i), mq, indent);
			}
			indent -= 2;
		}
		indent += 4;
		NodeList list = node.getChildNodes();
		int listLength = list.getLength();
		for (int i = 0; i < listLength; i++) {
			visitNode(list.item(i), mq, indent);
		}
		indent -= 4;
	}

	public static void printlnIndented(int indent, String string) {
		for (int i = 0; i < indent; i++) {
			System.out.print(" "); //$NON-NLS-1$
		}
		System.out.println(string);
	}
}
