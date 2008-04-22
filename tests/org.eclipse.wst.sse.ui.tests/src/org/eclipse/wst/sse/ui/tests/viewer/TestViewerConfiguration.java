/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.sse.ui.tests.viewer;

import junit.framework.TestCase;

import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.IUndoManager;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.formatter.IContentFormatter;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlinkPresenter;
import org.eclipse.jface.text.information.IInformationPresenter;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredPartitioning;
import org.eclipse.wst.sse.core.text.IStructuredPartitions;
import org.eclipse.wst.sse.ui.StructuredTextViewerConfiguration;
import org.eclipse.wst.sse.ui.internal.StructuredTextViewer;
import org.eclipse.wst.sse.ui.internal.provisional.style.LineStyleProvider;
import org.eclipse.wst.sse.ui.tests.Logger;

public class TestViewerConfiguration extends TestCase {

	private StructuredTextViewerConfiguration fConfig = null;
	private boolean fDisplayExists = true;
	private StructuredTextViewer fViewer = null;
	private boolean isSetup = false;

	public TestViewerConfiguration() {
		super("TestViewerConfiguration");
	}

	protected void setUp() throws Exception {

		super.setUp();
		if (!this.isSetup) {
			setUpViewerConfiguration();
			this.isSetup = true;
		}
	}

	private void setUpViewerConfiguration() {
		if (Display.getCurrent() != null) {

			Shell shell = null;
			Composite parent = null;

			if (PlatformUI.isWorkbenchRunning()) {
				shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			}
			else {
				shell = new Shell(Display.getCurrent());
			}
			parent = new Composite(shell, SWT.NONE);

			// dummy viewer
			fViewer = new StructuredTextViewer(parent, null, null, false, SWT.NONE);
			fConfig = new StructuredTextViewerConfiguration();
		}
		else {
			fDisplayExists = false;
			Logger.log(Logger.INFO, "Remember, viewer configuration tests are not run because workbench is not open (normal on build machine)");
		}
	}

	public void testGetAnnotationHover() {

		// probably no display
		if (!fDisplayExists)
			return;

		IAnnotationHover hover = fConfig.getAnnotationHover(fViewer);
		assertNotNull("AnnotationHover is null", hover);
	}

	/**
	 * Not necessary
	 */
	public void testGetAutoEditStrategies() {

		// probably no display
		if (!fDisplayExists)
			return;

		IAutoEditStrategy[] strategies = fConfig.getAutoEditStrategies(fViewer, IStructuredPartitions.DEFAULT_PARTITION);
		assertNotNull(strategies);
		assertTrue("there are no auto edit strategies", strategies.length > 0);
	}

	/**
	 * Not necessary
	 */
	public void testGetConfiguredContentTypes() {

		// probably no display
		if (!fDisplayExists)
			return;

		String[] configuredContentTypes = fConfig.getConfiguredContentTypes(fViewer);
		assertNotNull(configuredContentTypes);
		assertTrue(configuredContentTypes.length == 1);
	}

	public void testGetConfiguredDocumentPartitioning() {

		// probably no display
		if (!fDisplayExists)
			return;

		String partitioning = fConfig.getConfiguredDocumentPartitioning(fViewer);
		assertEquals(IStructuredPartitioning.DEFAULT_STRUCTURED_PARTITIONING, partitioning);
	}

	public void testGetConfiguredTextHoverStateMasks() {

		// probably no display
		if (!fDisplayExists)
			return;

		int[] masks = fConfig.getConfiguredTextHoverStateMasks(fViewer, IStructuredPartitions.DEFAULT_PARTITION);
		assertEquals(2, masks.length);
	}

	public void testGetContentAssistant() {

		// probably no display
		if (!fDisplayExists)
			return;

		IContentAssistant ca = fConfig.getContentAssistant(fViewer);
		assertNotNull("there is no content assistant", ca);
	}

	public void testGetContentFormatter() {

		// probably no display
		if (!fDisplayExists)
			return;

		IContentFormatter cf = fConfig.getContentFormatter(fViewer);
		assertNull("there is a content formatter", cf);
	}

	/**
	 * Not necessary
	 */
	public void testGetDoubleClickStrategy() {

		// probably no display
		if (!fDisplayExists)
			return;

		String[] contentTypes = fConfig.getConfiguredContentTypes(fViewer);
		for (int i = 0; i < contentTypes.length; i++) {
			ITextDoubleClickStrategy strategy = fConfig.getDoubleClickStrategy(fViewer, contentTypes[i]);
			if (strategy != null) {
				return;
			}
		}
		assertTrue("there are no configured double click strategies", false);
	}

	/**
	 * Not necessary
	 */
	public void testGetHyperlinkDetectors() {

		// probably no display
		if (!fDisplayExists)
			return;

		IHyperlinkDetector[] detectors = fConfig.getHyperlinkDetectors(fViewer);
		assertNotNull("there are no hyperlink detectors", detectors);
		assertTrue("there are no hyperlink detectors", detectors.length > 0);
	}

	public void testGetHyperlinkPresenter() {

		// probably no display
		if (!fDisplayExists)
			return;

		IHyperlinkPresenter presenter = fConfig.getHyperlinkPresenter(fViewer);
		assertNotNull("hyperlink presenter shouldn't be null", presenter);
	}

	public void testGetInformationControlCreator() {

		// probably no display
		if (!fDisplayExists)
			return;

		IInformationControlCreator infoControlCreator = fConfig.getInformationControlCreator(fViewer);
		assertNotNull("info control creator was null", infoControlCreator);
	}

	public void testGetInformationPresenter() {

		// probably no display
		if (!fDisplayExists)
			return;

		IInformationPresenter presenter = fConfig.getInformationPresenter(fViewer);
		assertNotNull("InformationPresenter was null", presenter);
	}

	public void testGetLineStyleProviders() {
		// probably no display
		if (!fDisplayExists)
			return;

		// there should be no linestyleproviders for default
		String[] contentTypes = fConfig.getConfiguredContentTypes(fViewer);
		for (int i = 0; i < contentTypes.length; i++) {
			LineStyleProvider providers[] = fConfig.getLineStyleProviders(fViewer, contentTypes[i]);
			assertNull("line style providers is not null", providers);
		}
	}

	/**
	 * Not necessary
	 */
	public void testGetOverviewRulerAnnotationHover() {

		// probably no display
		if (!fDisplayExists)
			return;

		IAnnotationHover annotationHover = fConfig.getOverviewRulerAnnotationHover(fViewer);
		assertNotNull("annotation hover was null", annotationHover);
	}

	public void testGetPresentationReconciler() {

		// probably no display
		if (!fDisplayExists)
			return;

		IPresentationReconciler presentationReconciler = fConfig.getPresentationReconciler(fViewer);
		// our default presentation reconciler is no longer null
		assertNotNull("presentation reconciler was null", presentationReconciler);
	}

	public void testGetReconciler() {

		// probably no display
		if (!fDisplayExists)
			return;

		IReconciler r = fConfig.getReconciler(fViewer);
		assertNotNull("Reconciler was null", r);
	}

	public void testGetUndoManager() {

		// probably no display
		if (!fDisplayExists)
			return;

		IUndoManager undoManager = fConfig.getUndoManager(fViewer);
		assertNotNull("undo manager was null", undoManager);
	}
}
