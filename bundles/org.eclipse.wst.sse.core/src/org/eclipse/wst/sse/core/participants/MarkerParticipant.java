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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.sse.core.builder.IBuilderModelProvider;
import org.eclipse.wst.sse.core.internal.Logger;


/**
 * A Builder Participant that updates a set of Markers for a given resource on
 * each build by removing all of the existing Markers. It collects the
 * existing markers in prebuild and deletes them in postbuild. Subclasses may
 * create new markers during the build itself.
 */

public abstract class MarkerParticipant extends AbstractBuilderParticipant {
	List oldMarkers = null;

	public MarkerParticipant() {
		super();
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
		attributes.put(getParticipantMarkerType(), Boolean.TRUE);
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

	/**
	 * @return - The IMarker type (ex.: IMarker.TASK) manipulated by this
	 *         participant
	 */
	protected abstract String getMarkerType();

	/**
	 * Returns an extra attribute name to indicate that the IMarker type was
	 * created by this participant, an "owner" ID.
	 */
	protected String getParticipantMarkerType() {
		return getClass().getName();
	}

	protected void postBuild(IFile file, IBuilderModelProvider provider, IProgressMonitor monitor) {
		IWorkspaceRunnable r = new IWorkspaceRunnable() {
			public void run(IProgressMonitor progressMonitor) throws CoreException {
				Iterator i = oldMarkers.iterator();
				while (i.hasNext()) {
					((IMarker) i.next()).delete();
				}
			}
		};
		try {
			if (file.isAccessible()) {
				file.getWorkspace().run(r, null, IWorkspace.AVOID_UPDATE, null);
			}
		} catch (CoreException e) {
			Logger.logException(e);
		}
		super.postBuild(file, provider, monitor);
	}

	protected void preBuild(IFile file, IBuilderModelProvider provider, IProgressMonitor monitor) {
		super.preBuild(file, provider, monitor);
		oldMarkers = new ArrayList(0);
		IMarker[] markers = null;
		try {
			// can't use deleteMarkers--not specific enough
			if (file.isAccessible()) {
				markers = file.findMarkers(getMarkerType(), true, IResource.DEPTH_INFINITE);
			}
		} catch (CoreException e) {
			Logger.logException(e);
		}
		if (markers != null) {
			for (int i = 0; i < markers.length; i++) {
				if (markers[i].getResource().equals(file) && markers[i].getAttribute(getParticipantMarkerType(), true)) {
					oldMarkers.add(markers[i]);
				}
			}
		}
	}
}
