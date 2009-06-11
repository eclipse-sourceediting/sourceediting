/*******************************************************************************
 * Copyright (c) 2009 Standards for Technology in Automotive Retail and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     David Carver - initial API and implementation
 *     Kevin Sawicki bug 259053 - NPE for document results.
 *******************************************************************************/
package org.eclipse.wst.xsl.jaxp.debug.ui.internal.views;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.text.JobSafeStructuredDocument;
import org.eclipse.wst.xsl.internal.debug.ui.XSLDebugUIPlugin;

public class ResultRunnable implements Runnable {
	private static final String XML_PROCESSING_INSTRUCTION = "<?xml"; //$NON-NLS-1$
	private static final String DOCTYPE_HTML = "<!DOCTYPE html"; //$NON-NLS-1$
	private static final String HTML_CONTENT_TYPE_ID = "org.eclipse.wst.html.core.htmlsource"; //$NON-NLS-1$
	private static final String XML_CONTENT_TYPE_ID = "org.eclipse.core.runtime.xml"; //$NON-NLS-1$
	private SourceViewer sourceViewer = null;
	private final String results;
	private IWorkbenchPartSite viewSite = null;
	
	public ResultRunnable(SourceViewer viewer, String results, IWorkbenchPartSite site) {
		sourceViewer = viewer;
		this.results = results;
		viewSite = site;
	} 

	public void run()
	{
		// if this is the first lot of data, determine the correct content type and set the appropriate document
		if (sourceViewer.getDocument()  == null)
		{
			IDocument document = createDocument();
			sourceViewer.setDocument(document);
		}
		try
		{
			IDocument document = sourceViewer.getDocument();
			document.replace(document.getLength(), 0, results);
		}
		catch (BadLocationException e)
		{
			XSLDebugUIPlugin.log(e);
		}
		sourceViewer.revealRange(sourceViewer.getDocument().getLength(),0);
		viewSite.getPage().activate(viewSite.getPart());
	}

	protected IDocument createDocument() {
		IDocument document;
		if (results.startsWith(DOCTYPE_HTML))
		{
			document = createStructuredDocument(HTML_CONTENT_TYPE_ID);
		}
		else if (results.startsWith(XML_PROCESSING_INSTRUCTION))
		{
			document = createStructuredDocument(XML_CONTENT_TYPE_ID);
		}
		else
		{
			document = new JobSafeStructuredDocument();
		}
		return document;
	}

	protected IDocument createStructuredDocument(String contentType) {
		IDocument document;
		IStructuredModel scratchModel = StructuredModelManager.getModelManager().createUnManagedStructuredModelFor(contentType);
		document = scratchModel.getStructuredDocument();
		return document;
	}

}
