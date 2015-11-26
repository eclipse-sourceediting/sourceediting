/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.core.tests.model;

import junit.framework.TestCase;

import org.eclipse.wst.css.core.internal.provisional.adapters.ICSSModelAdapter;
import org.eclipse.wst.html.core.internal.htmlcss.StyleAdapterFactory;
import org.eclipse.wst.html.core.internal.provisional.HTML40Namespace;
import org.eclipse.wst.html.core.tests.utils.FileUtil;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class TestCSS extends TestCase {
	private static final String MARKUP = "<html><head></head><body></body></html>";

	public void testStyleElementAdapter() {
		IDOMModel model = FileUtil.createHTMLModel();

		try {
			IStructuredDocument structuredDocument = model.getStructuredDocument();
			structuredDocument.set(MARKUP);

			IDOMDocument doc = model.getDocument();

			// get head tag
			NodeList list = doc.getElementsByTagName(HTML40Namespace.ElementName.HEAD);
			Element head = (Element) list.item(0);

			// create and append style element
			Element ele = doc.createElement(HTML40Namespace.ElementName.STYLE);
			ele.setAttribute(HTML40Namespace.ATTR_NAME_TYPE, "text/css");
			head.appendChild(ele);
			INodeAdapter adapter = StyleAdapterFactory.getInstance().adapt((IDOMNode) ele);
			assertNotNull("No adapter on " + ele, adapter);
			assertTrue("Adapter is not an ICSSModelAdapter", adapter instanceof ICSSModelAdapter);

			ICSSModelAdapter modelAdapter = (ICSSModelAdapter) adapter;
			assertNotNull("There should be a CSS model for the node", modelAdapter.getModel());
		}
		finally {
			if (model != null)
				model.releaseFromEdit();
		}
	}

	public void testTypeFooBarStyleElementAdapter() {
		IDOMModel model = FileUtil.createHTMLModel();

		try {
			IStructuredDocument structuredDocument = model.getStructuredDocument();
			structuredDocument.set(MARKUP);

			IDOMDocument doc = model.getDocument();

			// get head tag
			NodeList list = doc.getElementsByTagName(HTML40Namespace.ElementName.HEAD);
			Element head = (Element) list.item(0);

			// create and append style element
			Element ele = doc.createElement(HTML40Namespace.ElementName.STYLE);
			ele.setAttribute(HTML40Namespace.ATTR_NAME_TYPE, "foo/bar");
			head.appendChild(ele);
			INodeAdapter adapter = StyleAdapterFactory.getInstance().adapt((IDOMNode) ele);
			assertNotNull("No adapter on " + ele, adapter);
			assertTrue("Adapter is not an ICSSModelAdapter", adapter instanceof ICSSModelAdapter);

			ICSSModelAdapter modelAdapter = (ICSSModelAdapter) adapter;
			assertNull("There should be no CSS model for the node", modelAdapter.getModel());
		}
		finally {
			if (model != null)
				model.releaseFromEdit();
		}
	}
}
