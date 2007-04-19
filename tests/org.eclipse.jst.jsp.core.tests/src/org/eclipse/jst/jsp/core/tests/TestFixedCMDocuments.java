/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.core.tests;

import junit.framework.Test;
import junit.framework.TestCase;

import org.eclipse.jst.jsp.core.internal.contentmodel.JSPCMDocumentFactory;
import org.eclipse.wst.xml.core.internal.provisional.contentmodel.CMDocType;

public class TestFixedCMDocuments extends TestCase {
	public TestFixedCMDocuments(String name) {
		super(name);
	}

	public TestFixedCMDocuments() {
		super();
	}

	public static Test suite() {
		return new TestFixedCMDocuments();
	}

	public void testCHTMLdocumentAvailable() {
		assertNotNull("missing doc", JSPCMDocumentFactory.getCMDocument(CMDocType.CHTML_DOC_TYPE));
	}

	public void testHTML4documentAvailable() {
		assertNotNull("missing doc", JSPCMDocumentFactory.getCMDocument(CMDocType.HTML_DOC_TYPE));
	}

	public void testJSP11documentAvailable() {
		assertNotNull("missing doc", JSPCMDocumentFactory.getCMDocument(CMDocType.JSP11_DOC_TYPE));
	}

	public void testJSP12documentAvailable() {
		assertNotNull("missing doc", JSPCMDocumentFactory.getCMDocument(CMDocType.JSP12_DOC_TYPE));
	}

	public void testJSP20documentAvailable() {
		assertNotNull("missing doc", JSPCMDocumentFactory.getCMDocument(CMDocType.JSP20_DOC_TYPE));
	}

	public void testTag20documentAvailable() {
		assertNotNull("missing doc", JSPCMDocumentFactory.getCMDocument(CMDocType.TAG20_DOC_TYPE));
	}
}
