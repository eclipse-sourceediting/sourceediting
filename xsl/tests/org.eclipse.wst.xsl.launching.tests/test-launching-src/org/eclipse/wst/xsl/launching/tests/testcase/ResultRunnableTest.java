/*******************************************************************************
 *Copyright (c) 2009, 2010 Standards for Technology in Automotive Retail and others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 *Contributors:
 *    David Carver (STAR) - initial API and implementation
 *    David Carver (Intalio) - migrate to junit 4 with maven migration
 *******************************************************************************/
package org.eclipse.wst.xsl.launching.tests.testcase;

import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.sse.ui.internal.StructuredTextViewer;
import org.eclipse.wst.xsl.ui.internal.StructuredTextViewerConfigurationXSL;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * This class is an abstract class for Content Completion Tests. It provides all
 * of the common methods that are used by the completion tests so that they
 * aren't duplicated across the various classes. Overrides can be done where
 * appropriate.
 * 
 * @author David Carver
 * 
 * @since 1.2
 */
public class ResultRunnableTest {

	protected StructuredTextViewer sourceViewer = null;
	protected Shell shell = null;
	protected Composite parent = null;


	public ResultRunnableTest() {
	}

	protected void initializeSourceViewer() {
		// some test environments might not have a "real" display
		if (Display.getCurrent() != null) {


			if (PlatformUI.isWorkbenchRunning()) {
				shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
						.getShell();
			} else {
				shell = new Shell(Display.getCurrent());
			}
			parent = new Composite(shell, SWT.NONE);

			// dummy viewer
			sourceViewer = new StructuredTextViewer(parent, null, null, false,
					SWT.NONE);
		} else {
			fail("Unable to run the test as a display must be available.");
		}

		configureSourceViewer();
	}

	protected void configureSourceViewer() {
		sourceViewer.configure(new StructuredTextViewerConfigurationXSL());

	}

	/**
	 * Setup the necessary projects, files, and source viewer for the tests.
	 */
	@Before
	public void setUp() throws Exception {
		initializeSourceViewer();
	}

	@After
	public void tearDown() throws Exception {
		parent.dispose();
	}
	

	@Test
	public void testNoProcessingInstruction() throws Exception {
		String results = "This is some sample text.";
		MockResultRunnable runnable = new MockResultRunnable(sourceViewer, results, null);
		IDocument document = runnable.testCreateDocument();
		assertNotNull("Structured Document wasn not created successfully.", document);
	}
}
