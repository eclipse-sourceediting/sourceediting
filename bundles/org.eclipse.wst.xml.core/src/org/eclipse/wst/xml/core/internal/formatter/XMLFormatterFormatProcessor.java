/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xml.core.internal.formatter;

import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.DocumentRewriteSession;
import org.eclipse.jface.text.DocumentRewriteSessionType;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension4;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.format.IStructuredFormatProcessor;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.xml.core.internal.Logger;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.w3c.dom.Node;

/**
 * This is a wrapper for the new xml formatter so that it can be still
 * considered an IStruturedFormatProcessor
 */
public class XMLFormatterFormatProcessor implements IStructuredFormatProcessor {
	private DefaultXMLPartitionFormatter fFormatter = null;
	private IProgressMonitor fProgressMonitor = null;

	public void formatDocument(IDocument document, int start, int length) throws IOException, CoreException {
		if (document instanceof IStructuredDocument) {
			IStructuredModel model = StructuredModelManager.getModelManager().getModelForEdit((IStructuredDocument) document);
			if (model != null) {
				try {
					formatModel(model, start, length);
				}
				finally {
					model.releaseFromEdit();
				}
			}
		}
	}

	public void formatFile(IFile file) throws IOException, CoreException {
		if (file == null)
			return;

		IStructuredModel structuredModel = null;
		// OutputStream outputStream = null;
		try {
			// setup structuredModel
			// Note: We are getting model for edit. Will save model if model
			// changed.
			structuredModel = StructuredModelManager.getModelManager().getModelForEdit(file);

			// format
			formatModel(structuredModel);

			// save model if needed
			if (!structuredModel.isSharedForEdit() && structuredModel.isSaveNeeded())
				structuredModel.save();
		}
		finally {
			// ensureClosed(outputStream, null);
			// release from model manager
			if (structuredModel != null) {
				structuredModel.releaseFromEdit();
			}

		}
	}

	public void formatModel(IStructuredModel structuredModel) {
		int start = 0;
		int length = structuredModel.getStructuredDocument().getLength();

		formatModel(structuredModel, start, length);
	}

	public void formatModel(IStructuredModel structuredModel, int start, int length) {
		if(fProgressMonitor != null)
			fProgressMonitor.beginTask("", 2);
		IStructuredDocument structuredDocument = structuredModel.getStructuredDocument();
		DocumentRewriteSession activeRewriteSession = ((IDocumentExtension4) structuredDocument).getActiveRewriteSession();
		boolean startedRewriteSession = false;
		if (activeRewriteSession == null) {
			activeRewriteSession = ((IDocumentExtension4) structuredDocument).startRewriteSession(DocumentRewriteSessionType.UNRESTRICTED);
			startedRewriteSession = true;
		}
		getFormatter().setProgressMonitor(new NullProgressMonitor() {
			public boolean isCanceled() {
				return fProgressMonitor != null && fProgressMonitor.isCanceled();
			}
		});
		TextEdit edit = getFormatter().format(structuredModel, start, length);
		if(fProgressMonitor != null)
			fProgressMonitor.worked(1);
		
		try {
			structuredModel.aboutToChangeModel();
			edit.apply(structuredDocument);
			if(fProgressMonitor != null)
				fProgressMonitor.worked(1);
		}
		catch (Exception e) {
			Logger.log(Logger.WARNING_DEBUG, e.getMessage(), e);
		}
		finally {
			if (startedRewriteSession && activeRewriteSession != null) {
				((IDocumentExtension4) structuredDocument).stopRewriteSession(activeRewriteSession);
			}
			structuredModel.changedModel();
			if(fProgressMonitor != null)
				fProgressMonitor.done();
		}
	}

	public void formatNode(Node node) {
		if (node instanceof IDOMNode) {
			IDOMNode domNode = (IDOMNode) node;
			formatModel(domNode.getModel(), domNode.getStartOffset(), domNode.getLength());
		}
	}

	public void setProgressMonitor(IProgressMonitor monitor) {
		fProgressMonitor = monitor;
	}

	private DefaultXMLPartitionFormatter getFormatter() {
		if (fFormatter == null) {
			fFormatter = new DefaultXMLPartitionFormatter();
		}
		return fFormatter;
	}

}
