/*******************************************************************************
 *Copyright (c) 2008 Standards for Technology in Automotive Retail and others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 *Contributors:
 *    David Carver (STAR) - bug 243577 - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.ui.tests.contentassist;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.wst.sse.core.internal.provisional.exceptions.ResourceAlreadyExists;
import org.eclipse.wst.sse.core.internal.provisional.exceptions.ResourceInUse;
import org.eclipse.wst.xsl.ui.tests.AbstractSourceViewerTest;

public class TestNamedTemplateCompletionProposal extends
		AbstractSourceViewerTest {

	private void setupTestFile(String fileName) throws ResourceAlreadyExists,
			ResourceInUse, IOException, CoreException {
		String xslFilePath = projectName + File.separator + fileName;
		loadFileForTesting(xslFilePath);
	}

	public void testXSLPropsoalAvailable() throws Exception {
		setupTestFile("TestNamedTemplatesAssist.xsl");

			ICompletionProposal[] proposals = getProposals(30,51);
			assertEquals("Missing Proposals", 3, proposals.length);
	}

}
