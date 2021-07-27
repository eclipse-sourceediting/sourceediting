/*******************************************************************************
 * Copyright (c) 2020, 2021 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.jsdt.web.ui.internal.tasks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.wst.jsdt.internal.ui.JavaScriptPlugin;
import org.eclipse.wst.jsdt.ui.text.IJavaScriptPartitions;
import org.eclipse.wst.jsdt.web.ui.internal.Logger;
import org.eclipse.wst.sse.core.internal.provisional.tasks.IFileTaskScanner;
import org.eclipse.wst.sse.core.internal.provisional.tasks.TaskTag;

public class JSFileTaskScanner implements IFileTaskScanner {
	/**
	 * Provides task tag support for standalone *.js files
	 */
	private static final String MARKER_TYPE_TASK = "org.eclipse.wst.jsdt.core.task"; //$NON-NLS-1$

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.wst.sse.core.internal.provisional.tasks.IFileTaskScanner#
	 * getMarkerType()
	 */
	public String getMarkerType() {
		return MARKER_TYPE_TASK;
	}

	protected Map<String, Object> createInitialMarkerAttributes(String text, int documentLine, int startOffset, int length, int priority) {
		Map<String, Object> attributes = new HashMap<>(6);
		// marker line numbers are 1-based
		attributes.put(IMarker.LINE_NUMBER, Integer.valueOf(documentLine + 1));
		attributes.put(IMarker.TASK, getMarkerType());
		attributes.put(IMarker.CHAR_START, Integer.valueOf(startOffset));
		attributes.put(IMarker.CHAR_END, Integer.valueOf(startOffset + length));
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
	 * @see
	 * org.eclipse.wst.sse.core.internal.provisional.tasks.IFileTaskScanner#
	 * scan(org.eclipse.core.resources.IFile,
	 * org.eclipse.wst.sse.core.internal.provisional.tasks.TaskTag[],
	 * org.eclipse.core.runtime.IProgressMonitor)
	 */
	public Map<String, Object>[] scan(IFile file, TaskTag[] taskTags, IProgressMonitor monitor) {
		List<Map<String, Object>> newMarkers = new ArrayList<>();
		ITextFileBufferManager textFileBufferManager = FileBuffers.getTextFileBufferManager();
		SubMonitor localMonitor = SubMonitor.convert(monitor, 3);
		try {
			textFileBufferManager.connect(file.getFullPath(), LocationKind.IFILE, localMonitor.newChild(1));
			IDocument document = textFileBufferManager.getTextFileBuffer(file.getFullPath(), LocationKind.IFILE).getDocument();
			IDocumentPartitioner partitioner = JavaScriptPlugin.getDefault().getJavaTextTools().createDocumentPartitioner();
			partitioner.connect(document);
			ITypedRegion[] partitions = partitioner.computePartitioning(0, document.getLength());
			SubMonitor partitionMonitor = localMonitor.newChild(1);
			partitionMonitor.beginTask("", partitions.length);
			String[] lowerTags = new String[taskTags.length];
			for (int i = 0; i < taskTags.length; i++) {
				lowerTags[i] = taskTags[i].getTag().toLowerCase(Locale.US);
			}
			for (int i = 0; i < partitions.length; i++) {
				int openingLength = 2;
				switch (partitions[i].getType()) {
					case IJavaScriptPartitions.JAVA_SINGLE_LINE_COMMENT : {
						// includes the // leading characters
						IRegion lineInfo = document.getLineInformationOfOffset(partitions[i].getOffset());
						int start = partitions[i].getOffset() + openingLength;
						int lengthToEndOfLine = lineInfo.getOffset() + lineInfo.getLength() - start;
						String text = document.get(start, lengthToEndOfLine);
						String lowerText = text.toLowerCase(Locale.US);
						for (int j = 0; j < lowerTags.length; j++) {
							if ((lowerText.indexOf(lowerTags[j])) >= 0) {
								while (start < lineInfo.getOffset() + lineInfo.getLength() && Character.isWhitespace(text.charAt(start - partitions[i].getOffset() - openingLength))) {
									start++;
								}
								if (text.length() > 500) {
									text = text.substring(0,500);
								}
								Map<String, Object> attributesForNewTaskMarker = createInitialMarkerAttributes(text.trim(), document.getLineOfOffset(partitions[i].getOffset()), start, lineInfo.getOffset() + lineInfo.getLength() - start, taskTags[j].getPriority());
								newMarkers.add(attributesForNewTaskMarker);
							}
						}
						break;
					}
					case IJavaScriptPartitions.JAVA_DOC :
						openingLength = 3;
						//$FALL-THROUGH$
					case IJavaScriptPartitions.JAVA_MULTI_LINE_COMMENT : {
						// first line is possibly only partially a comment, act much like a single line comment
						int partitionEnd = partitions[i].getOffset() + partitions[i].getLength();
						// partition includes the opening 2 or 3 /**? and closing two */
						IRegion lineInformation = document.getLineInformationOfOffset(partitions[i].getOffset());
						int start = partitions[i].getOffset() + openingLength;
						int lengthToEndOfCommentLine = Math.min(lineInformation.getOffset() + lineInformation.getLength() - start, partitionEnd);
						String text = document.get(start, lengthToEndOfCommentLine);
						String lowerText = text.toLowerCase(Locale.US);
						for (int j = 0; j < lowerTags.length; j++) {
							if ((lowerText.indexOf(lowerTags[j])) >= 0) {
								while (start < lineInformation.getOffset() + lineInformation.getLength() && Character.isWhitespace(text.charAt(start - partitions[i].getOffset() - openingLength))) {
									start++;
								}
								Map<String, Object> attributesForNewTaskMarker = createInitialMarkerAttributes(text.trim(), document.getLineOfOffset(partitions[i].getOffset()), start, lengthToEndOfCommentLine, taskTags[j].getPriority());
								newMarkers.add(attributesForNewTaskMarker);
							}
						}
						// subsequent lines
						int secondLineNumber = document.getLineOfOffset(start) + 1;
						int lastLineNumber = document.getLineOfOffset(partitionEnd);
						for (int currentLineNumber = secondLineNumber; currentLineNumber <= lastLineNumber; currentLineNumber++) {
							lineInformation = document.getLineInformation(currentLineNumber);
							int applicableLength = lineInformation.getLength();
							text = document.get(lineInformation.getOffset(), applicableLength);

							// handle a last line that has more after the comment ends
							if (lineInformation.getOffset() + lineInformation.getLength() > partitionEnd) {
								applicableLength = partitionEnd - lineInformation.getOffset();
								text = document.get(lineInformation.getOffset(), applicableLength);
								while (text.length() > 2 && (text.charAt(text.length() - 1) == '/' || text.charAt(text.length() - 1) == '*')) {
									text = text.substring(0, text.length() - 1);
									applicableLength--;
								}
							}

							lowerText = text.toLowerCase(Locale.US);
							for (int j = 0; j < lowerTags.length; j++) {
								int index = 0;
								if ((index = lowerText.indexOf(lowerTags[j])) >= 0) {
									Map<String, Object> attributesForNewTaskMarker = createInitialMarkerAttributes(text.substring(index).trim(), currentLineNumber, lineInformation.getOffset() + index, applicableLength - index, taskTags[j].getPriority());
									newMarkers.add(attributesForNewTaskMarker);
								}
							}
						}
						break;
					}
				}
				partitionMonitor.worked(1);
			}
			partitioner.disconnect();
		}
		catch (CoreException e) {
			Logger.logException(e);
		}
		catch (BadLocationException e) {
			Logger.logException(e);
		}
		finally {
			try {
				textFileBufferManager.disconnect(file.getFullPath(), LocationKind.IFILE, localMonitor.newChild(1));
			}
			catch (CoreException e) {
				Logger.logException("Exception while disconnecting file buffer", e); //$NON-NLS-1$
			}
			localMonitor.done();
		}
		return newMarkers.toArray(new Map[newMarkers.size()]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.wst.sse.core.internal.provisional.tasks.IFileTaskScanner#
	 * shutdown(org.eclipse.core.resources.IProject)
	 */
	public void shutdown(IProject project) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.wst.sse.core.internal.provisional.tasks.IFileTaskScanner#
	 * startup(org.eclipse.core.resources.IProject)
	 */
	public void startup(IProject project) {
	}

}
