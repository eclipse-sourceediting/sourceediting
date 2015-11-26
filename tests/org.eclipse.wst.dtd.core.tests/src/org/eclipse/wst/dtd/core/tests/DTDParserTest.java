/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.dtd.core.tests;

import java.io.IOException;
import java.net.URL;

import junit.framework.TestCase;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.wst.dtd.core.internal.emf.DTDElement;
import org.eclipse.wst.dtd.core.internal.emf.DTDFile;
import org.eclipse.wst.dtd.core.internal.emf.util.DTDUtil;
import org.eclipse.wst.dtd.core.tests.internal.DTDCoreTestsPlugin;

public class DTDParserTest extends TestCase {

	private static final String UNEXPECTED_FILE_CONTENTS = "Unexpected file contents";

	public void testMultipleCommentParsing() throws IOException {
		DTDUtil util = new DTDUtil();
		String sampleDTDpath = "/resources/dtdParserTest/sample.dtd";
		URL bundleURL = DTDCoreTestsPlugin.getDefault().getBundle().getEntry(sampleDTDpath);
		assertNotNull(sampleDTDpath + " not found in bundle", bundleURL);
		// Do not rely on Common URI Resolver to find the contents
		URL fileURL = FileLocator.toFileURL(bundleURL);
		util.parse(fileURL.toExternalForm());
		DTDFile dtdFile = util.getDTDFile();
		assertEquals(UNEXPECTED_FILE_CONTENTS, 1, dtdFile.getDTDContent().size());
		Object object = dtdFile.getDTDContent().get(0);
		assertTrue(UNEXPECTED_FILE_CONTENTS, object instanceof DTDElement);
		DTDElement dtdElement = (DTDElement) object;
		String comment = dtdElement.getComment();
		assertEquals("Comment value was not as expected", " line one \n line two ", comment);

	}

}
