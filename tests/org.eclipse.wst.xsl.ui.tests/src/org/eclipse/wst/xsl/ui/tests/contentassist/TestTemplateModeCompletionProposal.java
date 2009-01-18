/*******************************************************************************
 * Copyright (c) Standards for Technology in Automotive Retail and others
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     David Carver - STAR - bug 230136 - intial API and implementation
 *******************************************************************************/

package org.eclipse.wst.xsl.ui.tests.contentassist;

import java.io.File;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.ui.internal.contentassist.ContentAssistUtils;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xsl.ui.tests.AbstractXSLUITest;
import org.eclipse.wst.xsl.ui.tests.XSLUITestsPlugin;

/**
 * Tests everything about code completion and code assistance.
 * 
 */
public class TestTemplateModeCompletionProposal extends
		AbstractCompletionProposalTest {

	public TestTemplateModeCompletionProposal() {
		// TODO Auto-generated constructor stub
	}

	public void testModeProposals() throws Exception {
		fileName = "modeTest.xsl";
		String xslFilePath = projectName + File.separator + fileName;
		loadFileForTesting(xslFilePath);
		IStructuredDocument document = (IStructuredDocument) sourceViewer
				.getDocument();
		// Column is off by one when calculating for the offset position
		int column = 36;
		int line = 16;

		try {
			int offset = document.getLineOffset(line) + column;
			// assertEquals("Wrong offset returned", 471, offset);

			ICompletionProposal[] proposals = getProposals(offset);
			assertProposalExists("\"#all\"", proposals);
			assertProposalExists("mode1", proposals);
			assertProposalExists("mode2", proposals);
			assertProposalExists("mode3", proposals);
		} catch (Exception ex) {
			model.releaseFromEdit();
			throw ex;
		} finally {
			model.releaseFromEdit();
		}

		sourceViewer = null;
	}

	private void assertProposalExists(String expected,
			ICompletionProposal[] proposal) throws Exception {
		assertNotNull("No proposals.", proposal);
		boolean foundsw = false;
		for (int i = 0; i < proposal.length; i++) {
			if (proposal[i].getDisplayString().equals(expected)) {
				foundsw = true;
				break;
			}
		}

		if (!foundsw) {
			fail("Proposal " + expected
					+ " was not found in the proposal list.");
		}
	}

}