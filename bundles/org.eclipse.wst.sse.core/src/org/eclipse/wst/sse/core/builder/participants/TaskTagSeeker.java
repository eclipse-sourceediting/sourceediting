/*
* Copyright (c) 2002 IBM Corporation and others.
* All rights reserved.   This program and the accompanying materials
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*   IBM - Initial API and implementation
*   Jens Lukowski/Innoopract - initial renaming/restructuring
* 
*/
package org.eclipse.wst.sse.core.builder.participants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.wst.sse.core.ModelPlugin;
import org.eclipse.wst.sse.core.builder.IBuilderDelegate;
import org.eclipse.wst.sse.core.document.DocumentReader;
import org.eclipse.wst.sse.core.document.IEncodedDocument;
import org.eclipse.wst.sse.core.internal.Logger;
import org.eclipse.wst.sse.core.internal.modelhandler.ModelHandlerRegistry;
import org.eclipse.wst.sse.core.modelhandler.IModelHandler;
import org.eclipse.wst.sse.core.parser.RegionParser;
import org.eclipse.wst.sse.core.parser.StructuredDocumentRegionHandler;
import org.eclipse.wst.sse.core.parser.StructuredDocumentRegionParser;
import org.eclipse.wst.sse.core.preferences.CommonModelPreferenceNames;
import org.eclipse.wst.sse.core.text.IStructuredDocument;
import org.eclipse.wst.sse.core.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.text.ITextRegion;
import org.eclipse.wst.sse.core.text.ITextRegionList;
import org.eclipse.wst.sse.core.util.StringUtils;


/**
 * A participant to create IMarker.TASKs for "todos" and similiar comments.
 * Clients should not subclass.
 */
public abstract class TaskTagSeeker implements IBuilderDelegate {

	public class TaskTag {
		public int priority;
		public String text;

		public TaskTag(String taskText, int taskPriority) {
			this.text = taskText;
			this.priority = taskPriority;
		}
	}

	private static final boolean _debug = "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.wst.sse.core/builder/participant/tasktag")); //$NON-NLS-1$ //$NON-NLS-2$
	protected static final boolean _debugBuilderPerf = "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.wst.sse.core/builder/time")); //$NON-NLS-1$ //$NON-NLS-2$

	// the list of attributes for the new tasks for the current file
	protected List fNewMarkerAttributes = null;

	private TaskTag[] fTaskTags = null;
	List oldMarkers = null;
	private long time0;

	public TaskTagSeeker() {
		super();
		fNewMarkerAttributes = new ArrayList();
		if (_debug) {
			System.out.println(getClass().getName() + " instance created"); //$NON-NLS-1$
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 */
	public IStatus build(IFile file, int kind, Map args, IProgressMonitor monitor) {
		if (monitor.isCanceled()) {
			return Status.OK_STATUS;
		}
		if (_debugBuilderPerf) {
			time0 = System.currentTimeMillis();
		}
		// Delete old Task markers
		try {
			file.deleteMarkers(getMarkerType(), true, IResource.DEPTH_ZERO);
		}
		catch (CoreException e) {
			Logger.logException(e);
		}

		// on a clean build, don't add new tasks
		if (kind != IncrementalProjectBuilder.CLEAN_BUILD) {
			findTasks(file, monitor);
			createNewMarkers(file, monitor);
		}
		if (_debugBuilderPerf) {
			System.out.println("" + (System.currentTimeMillis() - time0) + "ms for " + file.getLocation()); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return Status.OK_STATUS;
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

	protected void createNewMarkers(IFile file, IProgressMonitor monitor) {
		final IFile finalFile = file;
		if (file.isAccessible() && fNewMarkerAttributes.size() > 0) {
			if (_debug) {
				System.out.println("" + fNewMarkerAttributes.size() + " tasks for " + file.getFullPath()); //$NON-NLS-1$ //$NON-NLS-2$
			}
			try {
				IWorkspaceRunnable r = new IWorkspaceRunnable() {
					public void run(IProgressMonitor progressMonitor) throws CoreException {
						for (int i = 0; i < fNewMarkerAttributes.size(); i++) {
							IMarker marker = finalFile.createMarker(getMarkerType());
							marker.setAttributes((Map) fNewMarkerAttributes.get(i));
						}
					}
				};
				finalFile.getWorkspace().run(r, null, IWorkspace.AVOID_UPDATE, monitor);
				fNewMarkerAttributes.clear();
			}
			catch (CoreException e1) {
				Logger.logException(e1);
			}
		}
	}

	private String detectCharset(IFile file) {
		if (file.getType() == IResource.FILE && file.isAccessible()) {
			IContentDescription d = null;
			try {
				// optimized description lookup, might not succeed
				d = file.getContentDescription();
				if (d != null)
					return d.getCharset();
			}
			catch (CoreException e) {
				// should not be possible given the accessible and file type
				// check above
			}
			try {
				InputStream contents = file.getContents();
				IContentDescription description = Platform.getContentTypeManager().getDescriptionFor(contents, file.getName(), new QualifiedName[]{IContentDescription.CHARSET});
				contents.close();
				if (description != null) {
					return description.getCharset();
				}
			}
			catch (IOException e) {
				// don't care
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
	protected void findTasks(IDocument document, IStructuredDocumentRegion documentRegion, ITextRegion comment) {
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
					String commentedText = getCommentedText(document, begin, length);
					for (int i = 0; i < fTaskTags.length; i++) {
						int tagIndex = commentedText.indexOf(fTaskTags[i].text);
						if (tagIndex >= 0) {
							String markerDescription = commentedText.substring(tagIndex);
							int markerOffset = begin + tagIndex;
							int markerLength = end - markerOffset;
							fNewMarkerAttributes.add(createInitialMarkerAttributes(markerDescription, lineNumber, markerOffset, markerLength, fTaskTags[i].priority));
							break;
						}
					}
				}
			}
			catch (BadLocationException e) {
				Logger.logException(e);
			}
		}
	}

	private void findTasks(IFile file, IProgressMonitor monitor) {
		try {
			IModelHandler handler = ModelHandlerRegistry.getInstance().getHandlerFor(file);

			// records if the optimized streamish parse was possible
			boolean didStreamParse = false;
			final IProgressMonitor progressMonitor = monitor;
			final IEncodedDocument defaultDocument = handler.getDocumentLoader().createNewStructuredDocument();
			if (defaultDocument instanceof IStructuredDocument) {
				RegionParser parser = ((IStructuredDocument) defaultDocument).getParser();
				if (parser instanceof StructuredDocumentRegionParser) {
					didStreamParse = true;
					String charset = detectCharset(file);
					StructuredDocumentRegionParser documentParser = (StructuredDocumentRegionParser) parser;
					final IDocument textDocument = new Document();
					setDocumentContent(textDocument, file.getContents(true), charset);
					documentParser.reset(new DocumentReader(textDocument));
					documentParser.addStructuredDocumentRegionHandler(new StructuredDocumentRegionHandler() {
						/**
						 * @see org.eclipse.wst.sse.core.parser.StructuredDocumentRegionHandler#nodeParsed(org.eclipse.wst.sse.core.text.IStructuredDocumentRegion)
						 */
						public void nodeParsed(IStructuredDocumentRegion documentRegion) {
							ITextRegionList regions = documentRegion.getRegions();
							for (int j = 0; j < regions.size(); j++) {
								ITextRegion comment = regions.get(j);
								findTasks(textDocument, documentRegion, comment);
							}
							// disconnect the document regions
							if (documentRegion.getPrevious() != null) {
								documentRegion.getPrevious().setPrevious(null);
								documentRegion.getPrevious().setNext(null);
							}
							if (progressMonitor.isCanceled()) {
								textDocument.set(""); //$NON-NLS-1$
							}
						}

						/**
						 * @see org.eclipse.wst.sse.core.parser.StructuredDocumentRegionHandler#resetNodes()
						 */
						public void resetNodes() {
						}
					});
					documentParser.getDocumentRegions();
				}
			}
			if (!didStreamParse) {
				// Use a StructuredDocument
				IEncodedDocument document = handler.getDocumentLoader().createNewStructuredDocument(file);
				if (document instanceof IStructuredDocument) {
					IStructuredDocumentRegion documentRegion = ((IStructuredDocument) document).getFirstStructuredDocumentRegion();
					while (documentRegion != null) {
						ITextRegionList regions = documentRegion.getRegions();
						for (int j = 0; j < regions.size(); j++) {
							ITextRegion comment = regions.get(j);
							findTasks(document, documentRegion, comment);
						}
						documentRegion = documentRegion.getNext();
					}
				}
			}
		}
		catch (CoreException e) {
			Logger.logException(e);
		}
		catch (IOException e) {
			Logger.logException(e);
		}
	}


	/**
	 * @param document
	 * @param begin
	 * @param length
	 * @return
	 * @throws BadLocationException
	 */
	protected String getCommentedText(IDocument document, int begin, int length) throws BadLocationException {
		return document.get(begin, length);
	}


	final protected String getMarkerType() {
		return ModelPlugin.getID() + ".task"; //$NON-NLS-1$
	}

	/**
	 * @param region2
	 * @return
	 */
	protected abstract boolean isCommentRegion(IStructuredDocumentRegion region, ITextRegion textRegion);

	private void loadPreference() {
		if (_debug) {
			System.out.println(this + " loadPreference()"); //$NON-NLS-1$
		}
		String tagsString = ModelPlugin.getDefault().getPluginPreferences().getString(CommonModelPreferenceNames.TASK_TAG_TAGS);
		String prioritiesString = ModelPlugin.getDefault().getPluginPreferences().getString(CommonModelPreferenceNames.TASK_TAG_PRIORITIES);

		StringTokenizer toker = new StringTokenizer(tagsString, ","); //$NON-NLS-1$
		List list = new ArrayList();
		while (toker.hasMoreTokens()) {
			// since we're separating the values with ',', escape ',' in the
			// values
			list.add(StringUtils.replace(toker.nextToken(), "&comma;", ",").trim()); //$NON-NLS-1$ //$NON-NLS-2$
		}
		String[] tags = (String[]) list.toArray(new String[0]);
		list.clear();

		toker = new StringTokenizer(prioritiesString, ","); //$NON-NLS-1$
		int i = 0;
		while (toker.hasMoreTokens()) {
			Integer number = null;
			try {
				number = Integer.valueOf(toker.nextToken().trim());
			}
			catch (NumberFormatException e) {
				number = new Integer(IMarker.PRIORITY_NORMAL);
			}
			if (i < tags.length) {
				list.add(new TaskTag(tags[i++], number.intValue()));
			}
		}
		fTaskTags = (TaskTag[]) list.toArray(new TaskTag[0]);
	}

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
	 */
	public void shutdown(IProject project) {
		if (_debug) {
			System.out.println(this + " shutdown for " + project.getName()); //$NON-NLS-1$
		}
		fTaskTags = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 */
	public void startup(IProject project, int kind, Map args) {
		if (_debug) {
			System.out.println(this + " startup for " + project.getName()); //$NON-NLS-1$
		}
		loadPreference();
	}
}
