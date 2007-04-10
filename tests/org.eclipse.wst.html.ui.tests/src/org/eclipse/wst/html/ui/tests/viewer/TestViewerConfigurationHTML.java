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
package org.eclipse.wst.html.ui.tests.viewer;

import junit.framework.TestCase;

import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.formatter.IContentFormatter;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.jface.text.information.IInformationPresenter;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.html.core.text.IHTMLPartitions;
import org.eclipse.wst.html.ui.StructuredTextViewerConfigurationHTML;
import org.eclipse.wst.html.ui.tests.Logger;
import org.eclipse.wst.sse.ui.internal.StructuredTextViewer;
import org.eclipse.wst.sse.ui.internal.provisional.style.LineStyleProvider;

/**
 * @author pavery
 */
public class TestViewerConfigurationHTML extends TestCase {
    
	private StructuredTextViewerConfigurationHTML fConfig = null;
	private boolean fDisplayExists = true;
	private StructuredTextViewer fViewer = null;
	private boolean isSetup = false;
	
    public TestViewerConfigurationHTML() {
        super("TestViewerConfigurationHTML");
    }
    protected void setUp() throws Exception {
		
    	super.setUp();
		if(!this.isSetup){
			setUpViewerConfiguration();
			this.isSetup = true;
		}
    }
	
	private void setUpViewerConfiguration() {

		if(Display.getCurrent() != null) {
			
			Shell shell = null;
			Composite parent = null;
			
			if(PlatformUI.isWorkbenchRunning()) {
				shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			}
			else {	
				shell = new Shell(Display.getCurrent());
			}
			parent = new Composite(shell, SWT.NONE);
			
			// dummy viewer
			fViewer = new StructuredTextViewer(parent, null, null, false, SWT.NONE);
			fConfig = new StructuredTextViewerConfigurationHTML();
		}
		else {
			fDisplayExists = false;
			Logger.log(Logger.INFO, "Remember, viewer configuration tests are not run because workbench is not open (normal on build machine)");
		}
	}
    
	/**
	 * Not necessary
	 */
	public void testGetAnnotationHover() {
		
		// probably no display
		if(!fDisplayExists)
			return;
		
		IAnnotationHover hover = fConfig.getAnnotationHover(fViewer);
		assertNotNull("AnnotationHover is null", hover);
    }
	
	public void testGetAutoEditStrategies() {
		
		// probably no display
		if(!fDisplayExists)
			return;
		
		IAutoEditStrategy[] strategies = fConfig.getAutoEditStrategies(fViewer, IHTMLPartitions.HTML_DEFAULT);
		assertNotNull(strategies);
		assertTrue("there are no auto edit strategies", strategies.length>0);
	}
	
	public void testGetConfiguredContentTypes() {
		
		// probably no display
		if(!fDisplayExists)
			return;
		
		String[] configuredContentTypes = fConfig.getConfiguredContentTypes(fViewer);
		assertNotNull(configuredContentTypes);
		assertTrue("there are no configured content types", configuredContentTypes.length > 1);
	}
	
	/**
	 * Not necessary
	 */
	public void testGetContentAssistant() {
		
		// probably no display
		if(!fDisplayExists)
			return;
		
		IContentAssistant ca = fConfig.getContentAssistant(fViewer);
		assertNotNull("there is no content assistant", ca);
	}
	
	public void testGetContentFormatter() {
		
		// probably no display
		if(!fDisplayExists)
			return;
		
		IContentFormatter cf = fConfig.getContentFormatter(fViewer);
		assertNotNull("there is no content formatter", cf);
	}
	
	public void testGetDoubleClickStrategy() {
		
		// probably no display
		if(!fDisplayExists)
			return;
		
		String[] contentTypes = fConfig.getConfiguredContentTypes(fViewer);
		for (int i = 0; i < contentTypes.length; i++) {
			ITextDoubleClickStrategy strategy = fConfig.getDoubleClickStrategy(fViewer, contentTypes[i]);
			if(strategy != null) {
				return;
			}
		}
		assertTrue("there are no configured double click strategies", false);
	}
	
	public void testGetHyperlinkDetectors() {
		
		// probably no display
		if(!fDisplayExists)
			return;
		
		IHyperlinkDetector[] detectors = fConfig.getHyperlinkDetectors(fViewer);
		assertNotNull("there are no hyperlink detectors", detectors);
		assertTrue("there are no hyperlink detectors", detectors.length > 1);
	}
	
    public void testGetIndentPrefixes() {
		// probably no display
		if(!fDisplayExists)
			return;
		
		String[] contentTypes = fConfig.getConfiguredContentTypes(fViewer);
		for (int i = 0; i < contentTypes.length; i++) {
			String prefixes[] = fConfig.getIndentPrefixes(fViewer, contentTypes[i]);
			if(prefixes != null) {
				return;
			}
		}
		assertTrue("there are no configured indent prefixes", false);
	}
    
	/**
	 * Not necessary
	 */
    public void testGetInformationControlCreator() {
		// probably no display
		if(!fDisplayExists)
			return;
		
		IInformationControlCreator infoCreator = fConfig.getInformationControlCreator(fViewer);
		assertNotNull("InformationControlCreator is null", infoCreator);
    }
	
	/**
	 * Not necessary
	 */
	public void testGetInformationPresenter() {
		
		// probably no display
		if(!fDisplayExists)
			return;
		
		IInformationPresenter presenter = fConfig.getInformationPresenter(fViewer);
		assertNotNull("InformationPresenter is null", presenter);
	}
	
	public void testGetLineStyleProviders() {
		// probably no display
		if(!fDisplayExists)
			return;
		
		String[] contentTypes = fConfig.getConfiguredContentTypes(fViewer);
		for (int i = 0; i < contentTypes.length; i++) {
			LineStyleProvider providers[] = fConfig.getLineStyleProviders(fViewer, contentTypes[i]);
			if(providers != null) {
				return;
			}
		}
		assertTrue("there are no configured line style providers", false);
	}
	
	/**
	 * Not necessary
	 */
	public void testGetReconciler() {
		
		// probably no display
		if(!fDisplayExists)
			return;
		
		IReconciler r = fConfig.getReconciler(fViewer);
		assertNotNull("Reconciler is null", r);
	}
	
	public void testGetTextHover() {
		
		// probably no display
		if(!fDisplayExists)
			return;

		String[] hoverPartitions = new String[]{IHTMLPartitions.HTML_DEFAULT, IHTMLPartitions.SCRIPT};
		for (int i = 0; i < hoverPartitions.length; i++) {
			ITextHover hover = fConfig.getTextHover(fViewer, hoverPartitions[i], SWT.NONE);
			assertNotNull("hover was null for partition: " + hoverPartitions[i], hover);
		}
	}
}
