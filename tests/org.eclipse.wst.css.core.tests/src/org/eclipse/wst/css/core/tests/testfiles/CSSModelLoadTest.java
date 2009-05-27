/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.css.core.tests.testfiles;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import junit.framework.TestCase;

import org.eclipse.wst.css.core.internal.provisional.document.ICSSModel;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;

public class CSSModelLoadTest extends TestCase {
	private static final String HTML4_CSS = "html4.css";

	public CSSModelLoadTest() {
		super("CSS Model Loading Suite");
	}

	public void testLoadCSSfromStream() throws UnsupportedEncodingException, IOException {
		InputStream input = null;

		input = CSSModelLoadTest.class.getResourceAsStream(HTML4_CSS);
		IStructuredModel model = StructuredModelManager.getModelManager().getModelForEdit(HTML4_CSS, input, null);
		assertNotNull(model);
		assertTrue("IStructuredModel was not a ICSSModel", model instanceof ICSSModel);
		assertTrue("contents are incomplete", model.getStructuredDocument().get().indexOf("Basic HTML style information") > 4);
		model.releaseFromEdit();
	}
}
