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

import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.xsl.ui.tests.AbstractSourceViewerTest;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests everything about code completion and code assistance.
 * 
 */
public class TestTemplateModeCompletionProposal extends
		AbstractSourceViewerTest {

	public TestTemplateModeCompletionProposal() {
	}

	@Ignore
	@Test
	// FIXME: this test fails under maven/tycho
	public void testModeProposals() throws Exception {
		fileName = "modeTest.xsl";
		String xslFilePath = projectName + File.separator + fileName;
		loadFileForTesting(xslFilePath);
		IStructuredDocument document = (IStructuredDocument) sourceViewer
				.getDocument();
		// Line is off by one when calculating for the offset position
		int chars = 35;
		int line = 17;

		int offset = document.getLineOffset(line) + chars;
		// assertEquals("Wrong offset returned", 471, offset);

		ICompletionProposal[] proposals = getProposals(line, chars);
		assertProposalExists("mode1", proposals);
		assertProposalExists("mode2", proposals);
		assertProposalExists("mode3", proposals);

		proposals = getXMLProposals(offset);
		assertProposalExists("\"#all\"", proposals);

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