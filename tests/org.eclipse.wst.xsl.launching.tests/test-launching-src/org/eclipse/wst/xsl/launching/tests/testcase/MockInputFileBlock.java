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

import org.eclipse.core.resources.IFile;
import org.eclipse.wst.xsl.internal.debug.ui.tabs.main.InputFileBlock;

public class MockInputFileBlock extends InputFileBlock {

	public MockInputFileBlock(IFile defaultFile) {
		super(defaultFile);
		// TODO Auto-generated constructor stub
	}
	
	public String[] getAvailableFileExtensions() {
		// Since this is private we need to expose it for testing.
		return getFileExtensions();
	}

}
