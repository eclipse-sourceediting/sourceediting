/*******************************************************************************
 * Copyright (c) 2019 IBM Corporation
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.json.core.catalog;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.json.core.internal.schema.catalog.CommonXML;
import org.eclipse.wst.json.core.internal.schema.catalog.EntryParser;
import org.eclipse.wst.json.core.internal.schema.catalog.UserEntry;
import org.junit.Assert;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import junit.framework.TestCase;

public class CatalogTests extends TestCase {
	/* Serialized single UserEntry for reference */
	static final String ONE_ENTRY_USING_JAXB = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><entries><entry fileMatch=\"fileMatch0\" url=\"file:///Users/user_0\"/></entries>";

	public CatalogTests() {
	}

	/**
	 * Should succeed, regardless of the JRE being used.
	 * 
	 * @throws IOException
	 * @throws CoreException
	 */
	public void testVersusXmlBinding() throws Exception {
		Document jaxbDocument = CommonXML.getDocumentBuilder(false)
				.parse(new InputSource(new StringReader(ONE_ENTRY_USING_JAXB)));

		UserEntry[] entryArray = new UserEntry[1];
		for (int i = 0; i < entryArray.length; i++) {
			entryArray[i] = new UserEntry();
			entryArray[i].setFileMatch("fileMatch" + i);
			entryArray[i].setUrl(new URI("file:///Users/user_" + i));
		}
		Set<UserEntry> entries = new HashSet<UserEntry>(Arrays.asList(entryArray));
		String newXML = new EntryParser().serialize(entries);
		Document newDocument = CommonXML.getDocumentBuilder(false).parse(new InputSource(new StringReader(newXML)));

		boolean same = compareNodeLists("#document", getElements(jaxbDocument.getChildNodes()),
				getElements(newDocument.getChildNodes()));

		Assert.assertTrue("Incompatible storage, user preferences might be lost!", same);
	}

	private Element[] getElements(NodeList nodes) {
		List<Element> elements = new ArrayList<>(nodes.getLength());
		for (int i = 0; i < nodes.getLength(); i++) {
			if (nodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
				elements.add((Element) nodes.item(i));
			}
		}
		return elements.toArray(new Element[elements.size()]);
	}

	private boolean compareNodeLists(String elementName, Element[] oldList, Element[] newList) {
		Assert.assertEquals(elementName + " children differed by number", oldList.length, newList.length);
		for (int i = 0; i < oldList.length; i++) {
			Assert.assertEquals("child " + i + " of " + elementName + " had a different name", oldList[i].getNodeName(),
					newList[i].getNodeName());
			compareNodeLists(oldList[i].getNodeName(), getElements(oldList[i].getChildNodes()),
					getElements(newList[i].getChildNodes()));
		}
		return true;
	}
}
