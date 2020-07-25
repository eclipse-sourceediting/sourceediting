/*******************************************************************************
 * Licensed Materials - Property of IBM
 * © Copyright IBM Corporation 2020. All Rights Reserved.
 * U.S. Government Users Restricted Rights - Use, duplication or disclosure
 * restricted by GSA ADP Schedule Contract with IBM Corp. 
 *******************************************************************************/

package org.eclipse.wst.jsdt.web.ui.internal.tasks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.FindReplaceDocumentAdapter;
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
		try {
			textFileBufferManager.connect(file.getFullPath(), LocationKind.IFILE, monitor);
			IDocument document = textFileBufferManager.getTextFileBuffer(file.getFullPath()).getDocument();
			IDocumentPartitioner partitioner = JavaScriptPlugin.getDefault().getJavaTextTools().createDocumentPartitioner();
			FindReplaceDocumentAdapter finder = new FindReplaceDocumentAdapter(document);
			partitioner.connect(document);
			ITypedRegion[] partitions = partitioner.computePartitioning(0, document.getLength());
			for (int i = 0; i < partitions.length; i++) {
				for (int j = 0; j < taskTags.length; j++) {
					switch (partitions[i].getType()) {
						case IJavaScriptPartitions.JAVA_SINGLE_LINE_COMMENT : {
							IRegion lineMatch = finder.find(partitions[i].getOffset(), taskTags[j].getTag(), true, false, true, false);
							if (lineMatch != null) {
								IRegion lineInfo = document.getLineInformationOfOffset(lineMatch.getOffset());
								int start = lineMatch.getOffset();
								int lengthToEndOfLine = lineInfo.getLength() - (lineMatch.getOffset() - lineInfo.getOffset());
								String text = document.get(start, lengthToEndOfLine).trim();
								Map<String, Object> attributesForNewTaskMarker = createInitialMarkerAttributes(text, document.getLineOfOffset(lineMatch.getOffset()), start, text.length(), IMarker.PRIORITY_NORMAL);
								newMarkers.add(attributesForNewTaskMarker);
							}
							break;
						}
						case IJavaScriptPartitions.JAVA_DOC :
						case IJavaScriptPartitions.JAVA_MULTI_LINE_COMMENT : {
							IRegion tagMatch = finder.find(partitions[i].getOffset(), taskTags[j].getTag(), true, false, true, false);
							while (tagMatch != null && tagMatch.getOffset() + tagMatch.getLength() < partitions[i].getOffset() + partitions[i].getLength()) {
								int start = tagMatch.getOffset();
								IRegion lineInfo = document.getLineInformationOfOffset(tagMatch.getOffset());
								int lengthToEndOfLine = lineInfo.getLength() - (tagMatch.getOffset() - lineInfo.getOffset());
								String text = document.get(start, lengthToEndOfLine).trim();
								int lineNumber = document.getLineOfOffset(tagMatch.getOffset());
								Map<String, Object> attributesForNewTaskMarker = createInitialMarkerAttributes(text, lineNumber, start, text.length(), taskTags[j].getPriority());
								newMarkers.add(attributesForNewTaskMarker);
								
								tagMatch = finder.find(lineInfo.getOffset() + lineInfo.getLength(), taskTags[j].getTag(), true, false, true, false);
							}
							break;
						}
						default :
					}
				}
			}
			partitioner.disconnect();

			file.deleteMarkers(MARKER_TYPE_TASK, true, IResource.DEPTH_ONE);
		}
		catch (CoreException e) {
			Logger.logException(e);
		}
		catch (BadLocationException e) {
			Logger.logException(e);
		}
		finally {
			try {
				textFileBufferManager.disconnect(file.getFullPath(), LocationKind.IFILE, monitor);
			}
			catch (CoreException e) {
				Logger.logException("Exception while disconnecting file buffer", e); //$NON-NLS-1$
			}
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
		// TODO Auto-generated method stub

	}

}
