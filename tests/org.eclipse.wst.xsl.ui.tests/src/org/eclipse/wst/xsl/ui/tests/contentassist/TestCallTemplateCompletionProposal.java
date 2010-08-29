/*******************************************************************************
 *Copyright (c) 2008 Standards for Technology in Automotive Retail and others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 *Contributors:
 *    David Carver (STAR) - bug 243575 - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.ui.tests.contentassist;

import java.io.File;

import static org.junit.Assert.*;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.wst.xsl.ui.tests.AbstractSourceViewerTest;
import org.junit.Test;

public class TestCallTemplateCompletionProposal extends
		AbstractSourceViewerTest {

	@Test
	public void testXSLPropsoalAvailable() throws Exception {
		fileName = "calltemplateTest.xsl";
		String xslFilePath = projectName + File.separator + fileName;
		loadFileForTesting(xslFilePath);

		ICompletionProposal[] proposals = getProposals(12, 31);

		assertNotNull("Did not find proposals.", proposals);
	}

	@Test
	public void testUtilsProposalAvailable() throws Exception {
		fileName = "calltemplateTest.xsl";
		String xslFilePath = projectName + File.separator + fileName;
		loadFileForTesting(xslFilePath);

		ICompletionProposal[] proposals = getProposals(12, 31);
		assertNotNull("Did not find proposals.", proposals);
		assertTrue("Empty proposals returned.", proposals.length > 0);
		assertEquals("Wrong proposal found.", "long_date", proposals[0]
				.getDisplayString());
	}

}
