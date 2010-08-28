/*******************************************************************************
 * Copyright (c) Standards for Technology in Automotive Retail and others
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     David Carver - STAR - bug 244978 - intial API and implementation
 *******************************************************************************/

package org.eclipse.wst.xsl.ui.tests.contentassist;

import java.io.File;

import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.xsl.ui.tests.AbstractSourceViewerTest;

/**
 * Tests everything about code completion and code assistance.
 * 
 */
public class TestElementCompletionProposal extends AbstractSourceViewerTest {

	public void testXSLPropsoalAvailable() throws Exception {
		fileName = "testElementProposals.xsl";
		String xslFilePath = projectName + File.separator + fileName;
		loadFileForTesting(xslFilePath);
		IStructuredDocument document = (IStructuredDocument) sourceViewer
				.getDocument();
		// Column is off by one when calculating for the offset position
		int column = 16;
		int line = 5;

		int offset = document.getLineOffset(line) + column;

		ICompletionProposal[] proposals = getProposals(offset);
		assertNotNull("Did not find proposals.", proposals);
	}

	public void testXSLApplyTemplatesPropsoalAvailable() throws Exception {
		fileName = "testElementProposals.xsl";
		String xslFilePath = projectName + File.separator + fileName;
		loadFileForTesting(xslFilePath);
		IStructuredDocument document = (IStructuredDocument) sourceViewer
				.getDocument();
		// Column is off by one when calculating for the offset position
		int column = 13;
		int line = 6;

		ICompletionProposal[] proposals = getProposals(line, column);
		assertNotNull("Did not find proposals.", proposals);

		String proposalName = "";
		for (int cnt = 0; cnt < proposals.length; cnt++) {
			if (proposals[cnt].getDisplayString().equals("xsl:apply-templates")) {
				proposalName = proposals[cnt].getDisplayString();
			}
		}

		assertEquals("Did not find expected proposal.", "xsl:apply-templates",
				proposalName);
	}

	public void testXSLChoosePropsoalAvailable() throws Exception {
		fileName = "testElementProposals.xsl";
		String xslFilePath = projectName + File.separator + fileName;
		loadFileForTesting(xslFilePath);
		IStructuredDocument document = (IStructuredDocument) sourceViewer
				.getDocument();
		// Column is off by one when calculating for the offset position
		int column = 13;
		int line = 6;

		ICompletionProposal[] proposals = getProposals(line, column);
		assertNotNull("Did not find proposals.", proposals);

		String proposalName = "";
		for (int cnt = 0; cnt < proposals.length; cnt++) {
			if (proposals[cnt].getDisplayString().equals("xsl:choose")) {
				proposalName = proposals[cnt].getDisplayString();
			}
		}

		assertEquals("Did not find expected proposal.", "xsl:choose",
				proposalName);
	}

	public void testXSLWhenPropsoalNotAvailable() throws Exception {
		fileName = "testElementProposals.xsl";
		String xslFilePath = projectName + File.separator + fileName;
		loadFileForTesting(xslFilePath);
		IStructuredDocument document = (IStructuredDocument) sourceViewer
				.getDocument();
		// Column is off by one when calculating for the offset position
		int column = 16;
		int line = 5;

		int offset = document.getLineOffset(line) + column;

		ICompletionProposal[] proposals = getProposals(offset);
		assertNotNull("Did not find proposals.", proposals);

		String proposalName = "";
		for (int cnt = 0; cnt < proposals.length; cnt++) {
			if (proposals[cnt].getDisplayString().equals("xsl:when")) {
				proposalName = proposals[cnt].getDisplayString();
			}
		}

		assertFalse("xsl:when proposal found when it shouldn't have been.",
				proposalName.equals("xsl:when"));
	}
}