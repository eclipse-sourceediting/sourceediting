/*******************************************************************************
 * Copyright (c) 2001, 2008 IBM Corporation and others.
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
package org.eclipse.wst.sse.core.internal.tasks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.CharacterCodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.wst.sse.core.internal.Logger;
import org.eclipse.wst.sse.core.internal.document.DocumentReader;
import org.eclipse.wst.sse.core.internal.ltk.modelhandler.IModelHandler;
import org.eclipse.wst.sse.core.internal.ltk.parser.RegionParser;
import org.eclipse.wst.sse.core.internal.ltk.parser.StructuredDocumentRegionHandler;
import org.eclipse.wst.sse.core.internal.ltk.parser.StructuredDocumentRegionParser;
import org.eclipse.wst.sse.core.internal.modelhandler.ModelHandlerRegistry;
import org.eclipse.wst.sse.core.internal.provisional.document.IEncodedDocument;
import org.eclipse.wst.sse.core.internal.provisional.tasks.IFileTaskScanner;
import org.eclipse.wst.sse.core.internal.provisional.tasks.TaskTag;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;

/**
 * A delegate to create IMarker.TASKs for "todos" and similar comments.
 */
public abstract class StructuredFileTaskScanner implements IFileTaskScanner, IExecutableExtension {
	// the list of attributes for the new tasks for the current file
	protected List fNewMarkerAttributeMaps = null;

	List oldMarkers = null;
	private long time0;
	private String runtimeMarkerType;

	public StructuredFileTaskScanner() {
		super();
		fNewMarkerAttributeMaps = new ArrayList();
		if (Logger.DEBUG_TASKS) {
			System.out.println(getClass().getName() + " instance created"); //$NON-NLS-1$
		}
	}

	/**
	 * Returns the attributes with which a newly created marker will be
	 * initialized. Modified from the method in MarkerRulerAction
	 * 
	 * @return the initial marker attributes
	 */
	protected Map createInitialMarkerAttributes(String text, int documentLine, int startOffset, int length, int priority) {
		Map attributes = new HashMap(6);
		// marker line numbers are 1-based
		attributes.put(IMarker.LINE_NUMBER, new Integer(documentLine + 1));
		attributes.put(IMarker.TASK, getMarkerType());
		attributes.put(IMarker.CHAR_START, new Integer(startOffset));
		attributes.put(IMarker.CHAR_END, new Integer(startOffset + length));
		attributes.put(IMarker.MESSAGE, text);
		attributes.put(IMarker.USER_EDITABLE, Boolean.FALSE);

		switch (priority) {
			case IMarker.PRIORITY_HIGH : {
				attributes.put(IMarker.PRIORITY, new Integer(IMarker.PRIORITY_HIGH));
			}
				break;
			case IMarker.PRIORITY_LOW : {
				attributes.put(IMarker.PRIORITY, new Integer(IMarker.PRIORITY_LOW));
			}
				break;
			default : {
				attributes.put(IMarker.PRIORITY, new Integer(IMarker.PRIORITY_NORMAL));
			}
		}

		return attributes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.internal.provisional.tasks.IFileTaskScanner#getMarkerType()
	 */
	public String getMarkerType() {
		if (runtimeMarkerType != null)
			return runtimeMarkerType;

		return org.eclipse.core.resources.IMarker.TASK;
	}

	private String detectCharset(IFile file) {
		if (file.getType() == IResource.FILE && file.isAccessible()) {
			try {
				return file.getCharset(true);
			}
			catch (CoreException e) {
				Logger.logException(e);
			}
		}
		return ResourcesPlugin.getEncoding();
	}

	/**
	 * @param document
	 * @param documentRegion
	 * @param comment
	 */
	protected void findTasks(IDocument document, TaskTag[] taskTags, IStructuredDocumentRegion documentRegion, ITextRegion comment) {
		if (isCommentRegion(documentRegion, comment)) {
			int startOffset = documentRegion.getStartOffset(comment);
			int endOffset = documentRegion.getTextEndOffset(comment);
			try {
				int startLine = document.getLineOfOffset(startOffset);
				int endLine = document.getLineOfOffset(endOffset);
				for (int lineNumber = startLine; lineNumber <= endLine; lineNumber++) {
					IRegion line = document.getLineInformation(lineNumber);
					int begin = Math.max(startOffset, line.getOffset());
					int end = Math.min(endOffset, line.getOffset() + line.getLength());
					int length = end - begin;

					/* XXX: This generates a lot of garbage objects */

					String commentedText = getCommentedText(document, begin, length);
					String comparisonText = commentedText.toLowerCase(Locale.ENGLISH);

					for (int i = 0; i < taskTags.length; i++) {
						int tagIndex = comparisonText.indexOf(taskTags[i].getTag().toLowerCase(Locale.ENGLISH));
						if (tagIndex >= 0) {
							String markerDescription = commentedText.substring(tagIndex);
							int markerOffset = begin + tagIndex;
							int markerLength = end - markerOffset;
							fNewMarkerAttributeMaps.add(createInitialMarkerAttributes(markerDescription, lineNumber, markerOffset, markerLength, taskTags[i].getPriority()));
						}
					}
				}
			}
			catch (BadLocationException e) {
				Logger.logException(e);
			}
		}
	}

	private void findTasks(IFile file, final TaskTag[] taskTags, final IProgressMonitor monitor) {
		try {
			IModelHandler handler = ModelHandlerRegistry.getInstance().getHandlerFor(file);

			// records if the optimized streamish parse was possible
			boolean didStreamParse = false;
			final IEncodedDocument defaultDocument = handler.getDocumentLoader().createNewStructuredDocument();
			if (defaultDocument instanceof IStructuredDocument) {
				RegionParser parser = ((IStructuredDocument) defaultDocument).getParser();
				if (parser instanceof StructuredDocumentRegionParser) {
					didStreamParse = true;
					String charset = detectCharset(file);
					StructuredDocumentRegionParser documentParser = (StructuredDocumentRegionParser) parser;
					final IDocument textDocument = new Document();
					setDocumentContent(textDocument, file.getContents(true), charset);
					monitor.beginTask("", textDocument.getLength());
					documentParser.reset(new DocumentReader(textDocument));
					documentParser.addStructuredDocumentRegionHandler(new StructuredDocumentRegionHandler() {
						public void nodeParsed(IStructuredDocumentRegion documentRegion) {
							ITextRegionList regions = documentRegion.getRegions();
							for (int j = 0; j < regions.size(); j++) {
								ITextRegion comment = regions.get(j);
								findTasks(textDocument, taskTags, documentRegion, comment);
							}
							// disconnect the document regions
							if (documentRegion.getPrevious() != null) {
								documentRegion.getPrevious().setPrevious(null);
								documentRegion.getPrevious().setNext(null);
							}
							if (monitor.isCanceled()) {
								textDocument.set(""); //$NON-NLS-1$
							}
							monitor.worked(documentRegion.getLength());
						}

						public void resetNodes() {
						}
					});
					documentParser.getDocumentRegions();
				}
			}
			if (!didStreamParse) {
				// Use a StructuredDocument
				IEncodedDocument document = handler.getDocumentLoader().createNewStructuredDocument(file);
				monitor.beginTask("", document.getLength());
				if (document instanceof IStructuredDocument) {
					IStructuredDocumentRegion documentRegion = ((IStructuredDocument) document).getFirstStructuredDocumentRegion();
					while (documentRegion != null) {
						ITextRegionList regions = documentRegion.getRegions();
						for (int j = 0; j < regions.size(); j++) {
							ITextRegion comment = regions.get(j);
							findTasks(document, taskTags, documentRegion, comment);
						}
						monitor.worked(documentRegion.getLength());
						documentRegion = documentRegion.getNext();
					}
				}
			}
		}
		catch (CoreException e) {
			Logger.logException("Exception with " + file.getFullPath().toString(), e); //$NON-NLS-1$
		}
		catch (CharacterCodingException e) {
			Logger.log(Logger.INFO, "StructuredFileTaskScanner encountered CharacterCodingException reading " + file.getFullPath()); //$NON-NLS-1$
		}
		catch (Exception e) {
			Logger.logException("Exception with " + file.getFullPath().toString(), e); //$NON-NLS-1$
		}
		monitor.done();
	}

	protected String getCommentedText(IDocument document, int begin, int length) throws BadLocationException {
		return document.get(begin, length);
	}

	protected abstract boolean isCommentRegion(IStructuredDocumentRegion region, ITextRegion textRegion);

	public synchronized Map[] scan(IFile file, TaskTag[] taskTags, IProgressMonitor monitor) {
		fNewMarkerAttributeMaps.clear();
		if (monitor.isCanceled() || !shouldScan(file)) {
			return new Map[0];
		}
		if (Logger.DEBUG_TASKSPERF) {
			time0 = System.currentTimeMillis();
		}
		if (taskTags.length > 0) {
			findTasks(file, taskTags, monitor);
		}
		if (Logger.DEBUG_TASKSPERF) {
			System.out.println("" + (System.currentTimeMillis() - time0) + "ms for " + file.getFullPath()); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return (Map[]) fNewMarkerAttributeMaps.toArray(new Map[fNewMarkerAttributeMaps.size()]);
	}

	/**
	 * Sets the document content from this stream and closes the stream
	 */
	protected void setDocumentContent(IDocument document, InputStream contentStream, String charset) {
		Reader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(contentStream, charset), 2048);
			StringBuffer buffer = new StringBuffer(2048);
			char[] readBuffer = new char[2048];
			int n = in.read(readBuffer);
			while (n > 0) {
				buffer.append(readBuffer, 0, n);
				n = in.read(readBuffer);
			}
			document.set(buffer.toString());
		}
		catch (IOException x) {
		}
		finally {
			if (in != null) {
				try {
					in.close();
				}
				catch (IOException x) {
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.IExecutableExtension#setInitializationData(org.eclipse.core.runtime.IConfigurationElement,
	 *      java.lang.String, java.lang.Object)
	 */
	public void setInitializationData(IConfigurationElement config, String propertyName, Object data) throws CoreException {
		if (data != null && data instanceof String) {
			runtimeMarkerType = data.toString();
		}
	}

	boolean shouldScan(IResource r) {
		// skip "dot" files
		String s = r.getName();
		return s.length() == 0 || s.charAt(0) != '.';
	}

	public void shutdown(IProject project) {
		if (Logger.DEBUG_TASKS) {
			System.out.println(this + " shutdown for " + project.getName()); //$NON-NLS-1$
		}
	}

	public void startup(IProject project) {
		if (Logger.DEBUG_TASKS) {
			System.out.println(this + " startup for " + project.getName()); //$NON-NLS-1$
		}
		if (Logger.DEBUG_TASKSPERF) {
			time0 = System.currentTimeMillis();
		}
		if (Logger.DEBUG_TASKSPERF) {
			System.out.println("" + (System.currentTimeMillis() - time0) + "ms loading prefs for " + project.getName()); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
}
