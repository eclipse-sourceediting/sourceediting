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
package org.eclipse.wst.sse.ui;



import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.internal.ui.IInternalDebugUIConstants;
import org.eclipse.jface.text.Position;
import org.eclipse.ui.texteditor.MarkerAnnotation;
import org.eclipse.ui.texteditor.ResourceMarkerAnnotationModel;
import org.eclipse.wst.sse.ui.extensions.breakpoint.IBreakpointConstants;


/**
 * Source editor resource marker annotation model implementation
 */
public class StructuredResourceMarkerAnnotationModel extends ResourceMarkerAnnotationModel {
	protected IResource fMarkerResource;
	protected String fSecondaryMarkerAttributeValue;
	public final static String SECONDARY_ID_KEY = IBreakpointConstants.RESOURCE_PATH;

	/**
	 * Constructor
	 * @param resource
	 */
	public StructuredResourceMarkerAnnotationModel(IResource resource) {
		super(resource);
		fMarkerResource = resource;
	}

	public StructuredResourceMarkerAnnotationModel(IResource resource, String secondaryID) {
		super(resource);
		fMarkerResource = resource;
		fSecondaryMarkerAttributeValue = secondaryID;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.AbstractMarkerAnnotationModel#createMarkerAnnotation(org.eclipse.core.resources.IMarker)
	 */
	protected MarkerAnnotation createMarkerAnnotation(IMarker marker) {
		return new StructuredMarkerAnnotation(marker);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.AbstractMarkerAnnotationModel#getMarkerPosition(org.eclipse.core.resources.IMarker)
	 */
	public Position getMarkerPosition(IMarker marker) {
		Position pos = super.getMarkerPosition(marker);
		try {
			// 4/26 (pa) IInternalDebugUIConstants.INSTRUCTION_POINTER changed to IInternalDebugUIConstants.ANN_INSTR_POINTER_CURRENT
			if ((pos == null || pos.getLength() == 0) && marker.getType() == IInternalDebugUIConstants.ANN_INSTR_POINTER_CURRENT) {
				pos = createPositionFromMarker(marker);
			}
		}
		catch (CoreException e) {
			// just return pos from super class
		}
		return pos;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.AbstractMarkerAnnotationModel#isAcceptable(org.eclipse.core.resources.IMarker)
	 */
	protected boolean isAcceptable(IMarker marker) {
		try {
			Object attr = marker.getAttribute(IBreakpointConstants.ATTR_HIDDEN);
			if(attr != null && ((Boolean)attr).equals(Boolean.TRUE))
				return false;
		} catch (CoreException e) {
			// ignore
		}
		
		if (fSecondaryMarkerAttributeValue == null)
			return super.isAcceptable(marker);
		return super.isAcceptable(marker) && fSecondaryMarkerAttributeValue.equalsIgnoreCase(marker.getAttribute(SECONDARY_ID_KEY, "")); //$NON-NLS-1$
	}

}
