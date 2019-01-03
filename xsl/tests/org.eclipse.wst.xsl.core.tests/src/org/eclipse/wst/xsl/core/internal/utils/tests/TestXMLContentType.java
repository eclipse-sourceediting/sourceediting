/*******************************************************************************
 * Copyright (c) 2009,2017 Standards for Technology in Automotive Retail and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     David Carver (STAR) - bug 264788 - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.core.internal.utils.tests;

import org.eclipse.wst.xsl.core.internal.util.XMLContentType;
import org.junit.Test;

import static org.junit.Assert.*;


public class TestXMLContentType {


	@Test
	public void testGetFileExtensions() {
		XMLContentType xmlContentType = new XMLContentType();
		String[] exts = xmlContentType.getFileExtensions();
		assertTrue("Missing xslt extension.", findExtension("xslt", exts));
		assertTrue("Missing xml extension.", findExtension("xml", exts));
		assertTrue("Missing xsl extension.", findExtension("xsl", exts));
		assertTrue("Missing xmi extension.", findExtension("xmi", exts));
		
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
