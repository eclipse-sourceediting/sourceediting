/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
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
package org.eclipse.wst.sse.core.participants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.wst.sse.core.IModelManagerPlugin;
import org.eclipse.wst.sse.core.builder.IBuilderModelProvider;
import org.eclipse.wst.sse.core.internal.Logger;
import org.eclipse.wst.sse.core.preferences.CommonModelPreferenceNames;
import org.eclipse.wst.sse.core.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.text.ITextRegion;
import org.eclipse.wst.sse.core.util.StringUtils;


/**
 * A participant to update IMarker.TASKs for "TODO" and similiar comments.
 */
public abstract class TaskTagParticipant extends MarkerParticipant {


	public static class TaskTag {
		public int priority;
		public String text;

		public TaskTag(String taskText, int taskPriority) {
			this.text = taskText;
			this.priority = taskPriority;
		}
	}

	private static final boolean _debug = "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.wst.sse.core/builder/participant/tasktag")); //$NON-NLS-1$ //$NON-NLS-2$

	protected List fNewMarkerAttributes = null;

	private TaskTag[] fTaskTags = null;

	public TaskTagParticipant() {
		super();
		if (_debug) {
			System.out.println(getClass().getName() + " created"); //$NON-NLS-1$
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.builder.IBuilderParticipant#build(org.eclipse.core.resources.IResource[],
	 *      org.eclipse.core.resources.IProject,
	 *      org.eclipse.wst.sse.core.builder.IBuilderModelProvider,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	public boolean build(IResource[] resources, IProject project, IBuilderModelProvider provider, IProgressMonitor monitor) {
		if (_debug) {
			StringBuffer s = new StringBuffer();
			for (int i = 0; i < resources.length; i++) {
				s.append(resources[i].getFullPath());
				if (i < resources.length - 1)
					s.append(", "); //$NON-NLS-1$
			}
			System.out.println(this + " build for " + resources.length + " resources: " + s.toString()); //$NON-NLS-1$ //$NON-NLS-2$
		}
		boolean result = super.build(resources, project, provider, monitor);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.builder.IBuilderParticipant#build(org.eclipse.core.resources.IResourceDelta,
	 *      org.eclipse.core.resources.IProject,
	 *      org.eclipse.wst.sse.core.builder.IBuilderModelProvider,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	public boolean build(IResourceDelta delta, IProject project, IBuilderModelProvider provider, IProgressMonitor monitor) {
		if (_debug) {
			System.out.println(this + " building delta " + delta.getResource()); //$NON-NLS-1$
		}
		boolean result = super.build(delta, project, provider, monitor);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.builder.IBuilderParticipant#cleanup(org.eclipse.core.resources.IResource[],
	 *      org.eclipse.core.resources.IProject,
	 *      org.eclipse.wst.sse.core.builder.IBuilderModelProvider,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	public boolean cleanup(IResource[] resources, IProject project, IBuilderModelProvider provider, IProgressMonitor monitor) {
		if (_debug) {
			System.out.println(this + " cleanup for " + resources.length + " resources"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		boolean result = super.cleanup(resources, project, provider, monitor);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.builder.IBuilderParticipant#cleanup(org.eclipse.core.resources.IResourceDelta,
	 *      org.eclipse.core.resources.IProject,
	 *      org.eclipse.wst.sse.core.builder.IBuilderModelProvider,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	public boolean cleanup(IResourceDelta delta, IProject project, IBuilderModelProvider provider, IProgressMonitor monitor) {
		if (_debug) {
			System.out.println(this + " cleanup for delta " + delta.getResource()); //$NON-NLS-1$
		}
		boolean result = super.cleanup(delta, project, provider, monitor);
		return result;
	}

	protected void createTasks(IStructuredDocumentRegion region, ITextRegion textRegion) {
		IDocument document = getModelProvider().getDocument(getCurrentFile());
		int startOffset = region.getStartOffset(textRegion);
		int endOffset = region.getTextEndOffset(textRegion);
		try {
			int startLine = document.getLineOfOffset(startOffset);
			int endLine = document.getLineOfOffset(endOffset);
			for (int lineNumber = startLine; lineNumber <= endLine; lineNumber++) {
				IRegion line = document.getLineInformation(lineNumber);
				int begin = Math.max(startOffset, line.getOffset());
				int end = Math.min(endOffset, line.getOffset() + line.getLength());
				String commentedText = document.get(begin, end - begin);
				for (int i = 0; i < fTaskTags.length; i++) {
					int tagIndex = commentedText.indexOf(fTaskTags[i].text);
					if (tagIndex >= 0) {
						String markerDescription = commentedText.substring(tagIndex);
						int offset = begin + tagIndex;
						int length = end - offset;
						// defer marker creation until postBuild so we only
						// have one workspace runnable
						fNewMarkerAttributes.add(createInitialMarkerAttributes(markerDescription, lineNumber, offset, length, fTaskTags[i].priority));
						break;
					}
				}
			}
		} catch (BadLocationException e) {
			Logger.logException(e);
		}
	}

	protected void doBuildFor(IStructuredDocumentRegion region, ITextRegion textRegion) {
		super.doBuildFor(region, textRegion);
		if (isCommentRegion(region, textRegion)) {
			createTasks(region, textRegion);
		}
	}

	protected String getMarkerType() {
		return IMarker.TASK;
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
		IModelManagerPlugin plugin = (IModelManagerPlugin) Platform.getPlugin(IModelManagerPlugin.ID);
		String tagsString = plugin.getPluginPreferences().getString(CommonModelPreferenceNames.TASK_TAG_TAGS);
		String prioritiesString = plugin.getPluginPreferences().getString(CommonModelPreferenceNames.TASK_TAG_PRIORITIES);

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
			} catch (NumberFormatException e) {
				number = new Integer(IMarker.PRIORITY_NORMAL);
			}
			if (i < tags.length) {
				list.add(new TaskTag(tags[i++], number.intValue()));
			}
		}
		fTaskTags = (TaskTag[]) list.toArray(new TaskTag[0]);
		/*
		 * // commenting out - the order is important Arrays.sort(fTaskTags,
		 * new Comparator() { public boolean equals(Object obj) { return
		 * false; }
		 * 
		 * public int compare(Object o1, Object o2) { TaskTag t1 = (TaskTag)
		 * o1; TaskTag t2 = (TaskTag) o2; // reverse the priority so that HIGH >
		 * LOW if (t1.priority != t2.priority) return t2.priority -
		 * t1.priority; return Collator.getInstance().compare(t1.text,
		 * t2.text); } });
		 */
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.participants.MarkerParticipant#postBuild(org.eclipse.core.resources.IFile,
	 *      org.eclipse.wst.sse.core.builder.IBuilderModelProvider,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected void postBuild(IFile file, IBuilderModelProvider provider, IProgressMonitor monitor) {
		final IFile postBuildFile = file;
		if (postBuildFile.isAccessible()) {
			try {
				IWorkspaceRunnable r = new IWorkspaceRunnable() {
					public void run(IProgressMonitor progressMonitor) throws CoreException {
						for (int i = 0; i < fNewMarkerAttributes.size(); i++) {
							IMarker marker = postBuildFile.createMarker(IMarker.TASK);
							marker.setAttributes((Map) fNewMarkerAttributes.get(i));
						}
					}
				};
				postBuildFile.getWorkspace().run(r, null, IWorkspace.AVOID_UPDATE, monitor);
				fNewMarkerAttributes = null;
			} catch (CoreException e1) {
				Logger.logException(e1);
			}
		}
		super.postBuild(file, provider, monitor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.participants.MarkerParticipant#preBuild(org.eclipse.core.resources.IFile,
	 *      org.eclipse.wst.sse.core.builder.IBuilderModelProvider,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected void preBuild(IFile file, IBuilderModelProvider provider, IProgressMonitor monitor) {
		super.preBuild(file, provider, monitor);
		fNewMarkerAttributes = new ArrayList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.builder.IBuilderParticipant#shutdown(org.eclipse.core.resources.IProject)
	 */
	public void shutdown(IProject project) {
		super.shutdown(project);
		if (_debug) {
			System.out.println(this + " shutdown for " + project.getName()); //$NON-NLS-1$
		}
		fTaskTags = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.builder.IBuilderParticipant#startup(org.eclipse.core.resources.IProject,
	 *      int, java.util.Map)
	 */
	public void startup(IProject project, int kind, Map args) {
		super.startup(project, kind, args);
		if (_debug) {
			System.out.println(this + " startup for " + project.getName()); //$NON-NLS-1$
		}
		loadPreference();
	}
}
