/*******************************************************************************
 *Copyright (c) 2008 Standards for Technology in Automotive Retail and others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 *Contributors:
 *    David Carver - bug 214228 - Verify that File Extensions available for input block
 *******************************************************************************/
package org.eclipse.wst.xsl.launching.tests.testcase;

import static org.junit.Assert.*;

import org.junit.Test;


public class InputFileBlockTest {
	
	@Test
	public void testXMLFileExtensions() throws Exception {
		MockInputFileBlock fileBlock = new MockInputFileBlock(null);
		String[] fileExtensions = fileBlock.getAvailableFileExtensions();
		assertNotNull("No file extensions returned.", fileExtensions);
		assertTrue("Did not find 'xml'", findExtension("xml", fileExtensions));
		assertTrue("Did not find 'xsl'", findExtension("xsl", fileExtensions));
		assertTrue("Did not find 'xslt'", findExtension("xslt", fileExtensions));
		assertTrue("Did not find 'xmi'", findExtension("xmi", fileExtensions));
	}
	
	private boolean findExtension(String extension, String[] exts) {
		boolean foundsw = false;
		for (int i = 0; i < exts.length; i++) {
			if (extension.equalsIgnoreCase(exts[i])) {
				foundsw = true;
			}
		}
		return foundsw;
	}
	
}
