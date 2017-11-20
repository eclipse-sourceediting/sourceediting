/**
 *  Copyright (c) 2015, 2016 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 */
package org.eclipse.wst.json.core.document;

import org.eclipse.wst.json.core.TestUtil;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.junit.Assert;
import org.junit.Test;

public class JSONArrayDocumentChangesTest {

	@Test
	public void testInsertCloseArray() {
		IJSONModel model = TestUtil.loadModel("[");
		IJSONDocument document = model.getDocument();

		// Document contains an Array which is not closed
		Assert.assertNotNull(document.getFirstChild());
		Assert.assertTrue(document.getFirstChild() instanceof IJSONArray);
		IJSONArray array = (IJSONArray) document.getFirstChild();
		Assert.assertFalse(array.isClosed());

		IStructuredDocument structuredDocument = model.getStructuredDocument();
		// insert at end ']'
		structuredDocument.replaceText(structuredDocument, 1, 0, "]");
		array = (IJSONArray) document.getFirstChild();
		Assert.assertTrue(array.isClosed());
	}
}
