/*******************************************************************************
 * Copyright (c) 2001, 2021 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/
package org.eclipse.wst.xml.core.internal.tasks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
import org.eclipse.jface.text.DefaultLineTracker;
import org.eclipse.jface.text.IRegion;
import org.eclipse.wst.sse.core.internal.provisional.tasks.IFileTaskScanner;
import org.eclipse.wst.sse.core.internal.provisional.tasks.TaskTag;
import org.eclipse.wst.xml.core.internal.Logger;
import org.eclipse.wst.xml.core.internal.parser.XMLLineTokenizer;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;

/**
 * A delegate to create IMarker.TASKs for "todos" and similar comments.
 */
public class XMLStreamingFileTaskScanner extends XMLLineTokenizer implements IFileTaskScanner, IExecutableExtension {
	private static final boolean DEBUG_TASKSPERF = false;

	// the list of attribute maps for the new tasks for the current file
	protected List<Map<String, Object>> fNewMarkerAttributeMaps = null;

	private long time0;
	private String runtimeMarkerType;

	public XMLStreamingFileTaskScanner() {
		super();
	}

	/**
	 * Returns the attributes with which a newly created marker will be
	 * initialized. Modified from the method in MarkerRulerAction
	 * 
	 * @return the initial marker attributes
	 */
	protected Map<String, Object> createInitialMarkerAttributes(String text, int documentLine, int startOffset, int length, int priority) {
		Map<String, Object> attributes = new HashMap<>(6);
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

	String detectCharset(IFile file) {
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

	void findTasks(IFile file, final TaskTag[] taskTags, final IProgressMonitor monitor) {
		String charset = detectCharset(file);
		monitor.beginTask("", IProgressMonitor.UNKNOWN);
		// used for breaking multi-line comments into individual lines
		DefaultLineTracker lineTracker = new DefaultLineTracker();
		String[] searchTags = new String[taskTags.length];
		for (int i = 0; i < searchTags.length; i++) {
			searchTags[i] = taskTags[i].getTag().toLowerCase(Locale.ENGLISH);
		}
		try (InputStream contents = file.getContents(true)) {
			reset(new BufferedReader(new InputStreamReader(contents, charset)));
			while (!isEOF()) {
				String regionType = primGetNextToken();
				if (isCommentType(regionType)) {
					String comment = yytext();
					lineTracker.set(comment);
					for (int lineNumber = 0; lineNumber < lineTracker.getNumberOfLines(); lineNumber++) {
						IRegion line = lineTracker.getLineInformation(lineNumber);
						String lineComment = comment.substring(line.getOffset(), line.getOffset() + line.getLength());
						String lowercaseText = lineComment.toLowerCase(Locale.ENGLISH);

						for (int i = 0; i < taskTags.length; i++) {
							int tagIndex = lowercaseText.indexOf(searchTags[i]);
							if (tagIndex >= 0) {
								String markerDescription = lineComment.substring(tagIndex);
								if (markerDescription.length() > 500) {
									markerDescription = markerDescription.substring(0,500);
								}
								int markerOffset = getOffset() + line.getOffset() + tagIndex;
								int markerLength = line.getLength() - tagIndex;
								fNewMarkerAttributeMaps.add(createInitialMarkerAttributes(markerDescription, lineNumber + getLine(), markerOffset, markerLength, taskTags[i].getPriority()));
							}
						}
					}
				}
			}
		}
		catch (IOException e) {
			Logger.logException("IOException reading file " + file.getFullPath().toString(), e);
		}
		catch (CoreException e) {
			Logger.logException(e);
		}
		catch (BadLocationException e) {
			Logger.logException(e);
		}
		finally {
			try {
				yyclose();
			}
			catch (IOException e) {
				// nothing to do
			}
		}
		monitor.done();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.wst.sse.core.internal.provisional.tasks.IFileTaskScanner
	 * #getMarkerType()
	 */
	public String getMarkerType() {
		if (runtimeMarkerType != null) {
			return runtimeMarkerType;
		}

		return org.eclipse.core.resources.IMarker.TASK;
	}

	protected boolean isCommentType(String regionType) {
		return DOMRegionContext.XML_COMMENT_TEXT.equals(regionType);
	}

	@SuppressWarnings("unchecked")
	public synchronized Map<String, Object>[] scan(IFile file, TaskTag[] taskTags, IProgressMonitor monitor) {
		fNewMarkerAttributeMaps = new ArrayList<>();
		if (monitor.isCanceled() || !shouldScan(file)) {
			return new Map[0];
		}
		if (DEBUG_TASKSPERF) {
			time0 = System.currentTimeMillis();
		}
		if (taskTags.length > 0) {
			findTasks(file, taskTags, monitor);
		}
		if (DEBUG_TASKSPERF) {
			System.out.println("" + (System.currentTimeMillis() - time0) + "ms for " + file.getFullPath()); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return fNewMarkerAttributeMaps.toArray(new Map[fNewMarkerAttributeMaps.size()]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.runtime.IExecutableExtension#setInitializationData
	 * (org.eclipse.core.runtime.IConfigurationElement, java.lang.String,
	 * java.lang.Object)
	 */
	@SuppressWarnings("rawtypes")
	public void setInitializationData(IConfigurationElement config, String propertyName, Object data) throws CoreException {
		if (data != null && data instanceof String) {
			runtimeMarkerType = data.toString();
		}
		if (data != null && data instanceof Map) {
			runtimeMarkerType = ((Map) data).get("runtimeMarkerType").toString();
		}
	}

	boolean shouldScan(IResource r) {
		return true;
	}

	public void shutdown(IProject project) {
	}

	public void startup(IProject project) {
		if (DEBUG_TASKSPERF) {
			time0 = System.currentTimeMillis();
		}
		if (DEBUG_TASKSPERF) {
			System.out.println("" + (System.currentTimeMillis() - time0) + "ms loading prefs for " + project.getName()); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
}
