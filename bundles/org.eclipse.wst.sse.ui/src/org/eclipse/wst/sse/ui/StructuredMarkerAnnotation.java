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
package org.eclipse.wst.sse.ui;


import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugModelPresentation;
import org.eclipse.search.ui.SearchUI;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.texteditor.MarkerAnnotation;
import org.eclipse.ui.texteditor.MarkerUtilities;
import org.eclipse.wst.sse.ui.internal.Logger;
import org.eclipse.wst.sse.ui.internal.reconcile.TemporaryAnnotation;


/**
 * This is overridden to get around the problem of being registered as a
 * org.eclipse.wst.validation.core.problemmarker rather than a
 * org.eclipse.core.resource.problemmarker causing all problems to be skipped
 * in the OverviewRuler
 */
public class StructuredMarkerAnnotation extends MarkerAnnotation {
	//	private static final int ORIGINAL_MARKER_IMAGE= 1;
	//	private static final int QUICKFIX_IMAGE= 2;
	//	private static final int QUICKFIX_ERROR_IMAGE= 3;
	//	private static final int OVERLAY_IMAGE= 4;
	//	private static final int GRAY_IMAGE= 5;
	private static final int BREAKPOINT_IMAGE = 6;

	private static final int NO_IMAGE = 0;
	private int fImageType;
	private IDebugModelPresentation fPresentation;

	// TODO: private field never read loacally
	String fType = null;

	/**
	 * Constructor
	 * 
	 * @param marker
	 */
	StructuredMarkerAnnotation(IMarker marker) {
		super(marker);
		// sets fType, for use w/ StructuredAnnotationAccess
		initAnnotationType();
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.texteditor.MarkerAnnotation#getImage(org.eclipse.swt.widgets.Display)
	 */
	protected Image getImage(Display display) {
		if (fImageType == BREAKPOINT_IMAGE) {
			Image result = super.getImage(display);
			if (result == null) {
				IMarker marker = getMarker();
				if (marker != null && marker.exists()) {
					result = fPresentation.getImage(getMarker());
					setImage(result);
				}
			}
			return result;
		}
		return super.getImage(display);
	}

	/**
	 * Initializes the annotation's icon representation and its drawing layer
	 * based upon the properties of the underlying marker.
	 */
	protected void initAnnotationType() {
		//		fQuickFixIconEnabled=
		// PreferenceConstants.getPreferenceStore().getBoolean(PreferenceConstants.EDITOR_CORRECTION_INDICATION);
		fImageType = NO_IMAGE;
		IMarker marker = getMarker();
		if (MarkerUtilities.isMarkerType(marker, IBreakpoint.BREAKPOINT_MARKER)) {

			if (fPresentation == null)
				fPresentation = DebugUITools.newDebugModelPresentation();

			setImage(null); // see bug 32469
			setLayer(4);
			fImageType = BREAKPOINT_IMAGE;

			fType = TemporaryAnnotation.ANNOT_UNKNOWN;

		} else {
			fType = TemporaryAnnotation.ANNOT_UNKNOWN;
			try {
				if (marker.isSubtypeOf(IMarker.PROBLEM)) {
					int severity = marker.getAttribute(IMarker.SEVERITY, -1);
					switch (severity) {
						case IMarker.SEVERITY_ERROR :
							fType = TemporaryAnnotation.ANNOT_ERROR;
							break;
						case IMarker.SEVERITY_WARNING :
							fType = TemporaryAnnotation.ANNOT_WARNING;
							break;
						case IMarker.SEVERITY_INFO :
							fType = TemporaryAnnotation.ANNOT_INFO;
							break;
					}
				} else if (marker.isSubtypeOf(IMarker.TASK))
					fType = TemporaryAnnotation.ANNOT_TASK;
				else if (marker.isSubtypeOf(SearchUI.SEARCH_MARKER)) {
					fType = TemporaryAnnotation.ANNOT_SEARCH;
				} else if (marker.isSubtypeOf(IMarker.BOOKMARK))
					fType = TemporaryAnnotation.ANNOT_BOOKMARK;

			} catch (CoreException e) {
				Logger.logException(e);
			}
		}
	}
}
