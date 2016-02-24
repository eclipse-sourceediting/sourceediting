/**
 *  Copyright (c) 2013-2014 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 */
package org.eclipse.wst.json.core.document;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.eclipse.wst.json.core.TestUtil;
import org.eclipse.wst.json.core.contenttype.ContentTypeIdForJSON;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.junit.Assert;
import org.junit.Test;

public class JSONArrayCreationTest {

	@Test
	public void createEmptyArrayNotClosed() throws Exception {

		IJSONModel model = (IJSONModel) TestUtil.createModel();
		IStructuredDocument structuredDocument = model.getStructuredDocument();
		IJSONDocument document = model.getDocument();
		assertNull(document.getFirstChild());

		// Load JSON Array
		structuredDocument.set("[");
		assertNotNull(document.getFirstChild());
		Assert.assertTrue(document.getFirstChild() instanceof IJSONArray);

		// The JSON array is not closed.
		IJSONArray array = (IJSONArray) document.getFirstChild();
		Assert.assertFalse(array.isClosed());
	}

	@Test
	public void createEmptyArray() throws Exception {

		IJSONModel model = (IJSONModel) TestUtil.createModel();
		IStructuredDocument structuredDocument = model.getStructuredDocument();
		IJSONDocument document = model.getDocument();
		assertNull(document.getFirstChild());

		// Load JSON Array
		structuredDocument.set("[]");
		assertNotNull(document.getFirstChild());
		Assert.assertTrue(document.getFirstChild() instanceof IJSONArray);

		// The JSON array is closed.
		IJSONArray array = (IJSONArray) document.getFirstChild();
		Assert.assertTrue(array.isClosed());
	}

}
