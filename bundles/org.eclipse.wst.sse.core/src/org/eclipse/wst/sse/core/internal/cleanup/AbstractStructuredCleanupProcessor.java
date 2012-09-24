/*******************************************************************************
 * Copyright (c) 2001, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/
package org.eclipse.wst.sse.core.internal.cleanup;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Vector;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentRewriteSession;
import org.eclipse.jface.text.DocumentRewriteSessionType;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension4;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.Logger;
import org.eclipse.wst.sse.core.internal.format.IStructuredFormatProcessor;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;


public abstract class AbstractStructuredCleanupProcessor implements IStructuredCleanupProcessor {
	public boolean refreshCleanupPreferences = true; // special flag for JUnit

	// tests to skip refresh
	// of cleanup preferences
	// when it's set to false

	public String cleanupContent(String input) throws IOException, CoreException {
		IStructuredModel structuredModel = null;
		InputStream inputStream = null;
		try {
			// setup structuredModel
			inputStream = new ByteArrayInputStream(input.getBytes("UTF8")); //$NON-NLS-1$
			String id = inputStream.toString() + getContentType();
			structuredModel = StructuredModelManager.getModelManager().getModelForRead(id, inputStream, null);

			// cleanup
			cleanupModel(structuredModel, 0, structuredModel.getStructuredDocument().getLength());

			// return output
			return structuredModel.getStructuredDocument().get();
		} finally {
			ensureClosed(null, inputStream);
			// release from model manager
			if (structuredModel != null)
				structuredModel.releaseFromRead();
		}
	}

	public String cleanupContent(String input, int start, int length) throws IOException, CoreException {
		IStructuredModel structuredModel = null;
		InputStream inputStream = null;
		try {
			// setup structuredModel
			inputStream = new ByteArrayInputStream(input.getBytes("UTF8")); //$NON-NLS-1$
			String id = inputStream.toString() + getContentType();
			structuredModel = StructuredModelManager.getModelManager().getModelForRead(id, inputStream, null);

			// cleanup
			cleanupModel(structuredModel, start, length);

			// return output
			return structuredModel.getStructuredDocument().get();
		} finally {
			ensureClosed(null, inputStream);
			// release from model manager
			if (structuredModel != null)
				structuredModel.releaseFromRead();
		}
	}

	public void cleanupDocument(IDocument document) throws IOException, CoreException {
		if (document == null)
			return;

		IStructuredModel structuredModel = null;
		// OutputStream outputStream = null;
		try {
			// setup structuredModel
			// Note: We are getting model for edit. Will save model if model
			// changed.
			structuredModel = StructuredModelManager.getModelManager().getExistingModelForEdit(document);

			// cleanup
			cleanupModel(structuredModel);

			// save model if needed
			if (!structuredModel.isSharedForEdit() && structuredModel.isSaveNeeded())
				structuredModel.save();
		} finally {
			// ensureClosed(outputStream, null);
			// release from model manager
			if (structuredModel != null)
				structuredModel.releaseFromEdit();
		}
	}

	public void cleanupDocument(IDocument document, int start, int length) throws IOException, CoreException {
		if (document == null)
			return;

		if (start >= 0 && length >= 0 && start + length <= document.getLength()) {
			IStructuredModel structuredModel = null;
			// OutputStream outputStream = null;
			try {
				// setup structuredModel
				// Note: We are getting model for edit. Will save model if
				// model changed.
				structuredModel = StructuredModelManager.getModelManager().getExistingModelForEdit(document);

				// cleanup
				cleanupModel(structuredModel, start, length);

				// save model if needed
				if (!structuredModel.isSharedForEdit() && structuredModel.isSaveNeeded())
					structuredModel.save();
			} finally {
				// ensureClosed(outputStream, null);
				// release from model manager
				if (structuredModel != null)
					structuredModel.releaseFromEdit();
			}
		}
	}

	public void cleanupFile(IFile file) throws IOException, CoreException {
		IStructuredModel structuredModel = null;
		// OutputStream outputStream = null;
		try {
			// setup structuredModel
			structuredModel = StructuredModelManager.getModelManager().getModelForRead(file);

			// cleanup
			cleanupModel(structuredModel, 0, structuredModel.getStructuredDocument().getLength());

			// save output to file
			// outputStream = new
			// FileOutputStream(file.getLocation().toString());
			structuredModel.save(file);
		} finally {
			// ensureClosed(outputStream, null);
			// release from model manager
			if (structuredModel != null)
				structuredModel.releaseFromRead();
		}
	}

	public void cleanupFile(IFile file, int start, int length) throws IOException, CoreException {
		IStructuredModel structuredModel = null;
		// OutputStream outputStream = null;
		try {
			// setup structuredModel
			structuredModel = StructuredModelManager.getModelManager().getModelForRead(file);

			// cleanup
			cleanupModel(structuredModel, start, length);

			// save output to file
			// outputStream = new
			// FileOutputStream(file.getLocation().toString());
			structuredModel.save(file);
		} finally {
			// ensureClosed(outputStream, null);
			// release from model manager
			if (structuredModel != null)
				structuredModel.releaseFromRead();
		}
	}

	public void cleanupFileName(String fileName) throws IOException, CoreException {
		IStructuredModel structuredModel = null;
		InputStream inputStream = null;
		// OutputStream outputStream = null;
		try {
			// setup structuredModel
			inputStream = new FileInputStream(fileName);
			structuredModel = StructuredModelManager.getModelManager().getModelForRead(fileName, inputStream, null);

			// cleanup
			cleanupModel(structuredModel, 0, structuredModel.getStructuredDocument().getLength());

			// save output to file
			// outputStream = new FileOutputStream(fileName);
			structuredModel.save();
		} finally {
			// ensureClosed(outputStream, inputStream);
			// release from model manager
			if (structuredModel != null)
				structuredModel.releaseFromRead();
		}
	}

	public void cleanupFileName(String fileName, int start, int length) throws IOException, CoreException {
		IStructuredModel structuredModel = null;
		InputStream inputStream = null;
		// OutputStream outputStream = null;
		try {
			// setup structuredModel
			inputStream = new FileInputStream(fileName);
			structuredModel = StructuredModelManager.getModelManager().getModelForRead(fileName, inputStream, null);

			// cleanup
			cleanupModel(structuredModel, start, length);

			// save output to file
			// outputStream = new FileOutputStream(fileName);
			structuredModel.save();
		} finally {
			// ensureClosed(outputStream, inputStream);
			// release from model manager
			if (structuredModel != null)
				structuredModel.releaseFromRead();
		}
	}

	public void cleanupModel(IStructuredModel structuredModel) {

		int start = 0;
		int length = structuredModel.getStructuredDocument().getLength();
		cleanupModel(structuredModel, start, length);
	}

	public void cleanupModel(IStructuredModel structuredModel, int start, int length) {

		if (structuredModel != null) {
			if ((start >= 0) && (length <= structuredModel.getStructuredDocument().getLength())) {
				Vector activeNodes = getActiveNodes(structuredModel, start, length);
				if (activeNodes.size() > 0) {
					Node firstNode = (Node) activeNodes.firstElement();
					Node lastNode = (Node) activeNodes.lastElement();
					boolean done = false;
					Node eachNode = firstNode;
					Node nextNode = null;

					// https://bugs.eclipse.org/bugs/show_bug.cgi?id=123621
					// if doing any sort of cleanup, set up rewrite session/modelchanged
					IDocumentExtension4 docExt4 = null;
					if (structuredModel.getStructuredDocument() instanceof IDocumentExtension4) {
						docExt4 = (IDocumentExtension4) structuredModel.getStructuredDocument();
					}
					DocumentRewriteSession rewriteSession = null;

					try {
						// whenever formatting model, fire
						// abouttochange/modelchanged
						structuredModel.aboutToChangeModel();
						rewriteSession = (docExt4 == null || docExt4.getActiveRewriteSession() != null) ? null : docExt4.startRewriteSession(DocumentRewriteSessionType.UNRESTRICTED);

						while (!done) {
							// update "done"
							done = (eachNode == lastNode);

						// get next sibling before cleanup because eachNode
							// may
							// be deleted,
							// for example when it's an empty text node
							nextNode = eachNode.getNextSibling();

							// cleanup selected node(s)
							cleanupNode(eachNode);

							// update each node
							if (nextNode != null && nextNode.getParentNode() == null)
								// nextNode is deleted during cleanup
								eachNode = eachNode.getNextSibling();
							else
								eachNode = nextNode;

							// This should not be needed, but just in case
							// something went wrong with with eachNode.
							// We don't want an infinite loop here.
							if (eachNode == null)
								done = true;
						}

						// format source
						if (getFormatSourcePreference(structuredModel)) {
							// format the document
							IStructuredFormatProcessor formatProcessor = getFormatProcessor();
							formatProcessor.formatModel(structuredModel);
						}
					}
					finally {
						// we need two finally's, just in case first fails
						try {
							if ((docExt4 != null) && (rewriteSession != null))
								docExt4.stopRewriteSession(rewriteSession);
						}
						finally {
							// always make sure to fire changedmodel when done
							structuredModel.changedModel();
						}
					}
				}
			}
		}
	}

	public void cleanupNode(Node node) {
		if (node != null) {
			Node cleanupNode = node;

			// cleanup the owner node if it's an attribute node
			if (cleanupNode.getNodeType() == Node.ATTRIBUTE_NODE)
				cleanupNode = ((Attr) cleanupNode).getOwnerElement();

			// refresh cleanup preferences before getting cleanup handler
			if (refreshCleanupPreferences)
				refreshCleanupPreferences();

			// get cleanup handler
			IStructuredCleanupHandler cleanupHandler = getCleanupHandler(cleanupNode);
			if (cleanupHandler != null) {
				// cleanup each node
				cleanupHandler.cleanup(cleanupNode);
			}
		}
	}

	protected void convertLineDelimiters(IDocument document, String newDelimiter) {
		final int lineCount = document.getNumberOfLines();
		Map partitioners = TextUtilities.removeDocumentPartitioners(document);
		try {
			for (int i = 0; i < lineCount; i++) {
				final String delimiter = document.getLineDelimiter(i);
				if (delimiter != null && delimiter.length() > 0 && !delimiter.equals(newDelimiter)) {
					IRegion region = document.getLineInformation(i);
					document.replace(region.getOffset() + region.getLength(), delimiter.length(), newDelimiter);
				}
			}
		} catch (BadLocationException e) {
			Logger.logException(e);
		} finally {
			TextUtilities.addDocumentPartitioners(document, partitioners);
		}
	}

	protected void ensureClosed(OutputStream outputStream, InputStream inputStream) {
		try {
			if (inputStream != null) {
				inputStream.close();
			}
		} catch (IOException e) {
			Logger.logException(e); // hopeless
		}
		try {
			if (outputStream != null) {
				outputStream.close();
			}
		} catch (IOException e) {
			Logger.logException(e); // hopeless
		}
	}

	protected Vector getActiveNodes(IStructuredModel structuredModel, int startNodeOffset, int length) {
		Vector activeNodes = new Vector();

		if (structuredModel != null) {
			Node startNode = (Node) structuredModel.getIndexedRegion(startNodeOffset);
			Node endNode = (Node) structuredModel.getIndexedRegion(startNodeOffset + length);

			// make sure it's an non-empty document
			if (startNode != null) {
				while (isSiblingOf(startNode, endNode) == false) {
					if (endNode != null)
						endNode = endNode.getParentNode();
					if (endNode == null) {
						startNode = startNode.getParentNode();
						endNode = (Node) structuredModel.getIndexedRegion(startNodeOffset + length);
					}
				}

				while (startNode != endNode) {
					activeNodes.addElement(startNode);
					startNode = startNode.getNextSibling();
				}
				if (startNode != null)
					activeNodes.addElement(startNode);
			}
		}

		return activeNodes;
	}

	abstract protected IStructuredCleanupHandler getCleanupHandler(Node node);

	abstract protected String getContentType();

	protected boolean getConvertEOLCodesPreference(IStructuredModel structuredModel) {

		boolean convertEOLCodes = true;
		IStructuredCleanupHandler cleanupHandler = getCleanupHandler((Node) structuredModel.getIndexedRegion(0));
		if (cleanupHandler != null) {
			IStructuredCleanupPreferences cleanupPreferences = cleanupHandler.getCleanupPreferences();
			convertEOLCodes = cleanupPreferences.getConvertEOLCodes();
		}
		return convertEOLCodes;
	}

	protected String getEOLCodePreference(IStructuredModel structuredModel) {

		IScopeContext[] scopeContext = new IScopeContext[]{new InstanceScope()};
		String eolCode = Platform.getPreferencesService().getString(Platform.PI_RUNTIME, Platform.PREF_LINE_SEPARATOR, null, scopeContext);

		IStructuredCleanupHandler cleanupHandler = getCleanupHandler((Node) structuredModel.getIndexedRegion(0));
		if (cleanupHandler != null) {
			IStructuredCleanupPreferences cleanupPreferences = cleanupHandler.getCleanupPreferences();
			eolCode = cleanupPreferences.getEOLCode();
		}
		return eolCode;
	}

	abstract protected IStructuredFormatProcessor getFormatProcessor();

	protected boolean getFormatSourcePreference(IStructuredModel structuredModel) {

		boolean formatSource = true;
		IStructuredCleanupHandler cleanupHandler = getCleanupHandler((Node) structuredModel.getIndexedRegion(0));
		if (cleanupHandler != null) {
			IStructuredCleanupPreferences cleanupPreferences = cleanupHandler.getCleanupPreferences();
			formatSource = cleanupPreferences.getFormatSource();
		}
		return formatSource;
	}

	protected boolean isSiblingOf(Node node, Node endNode) {
		if (endNode == null) {
			return true;
		} else {
			Node siblingNode = node;
			while (siblingNode != null) {
				if (siblingNode == endNode)
					return true;
				else
					siblingNode = siblingNode.getNextSibling();
			}
			return false;
		}
	}

	abstract protected void refreshCleanupPreferences();
}
