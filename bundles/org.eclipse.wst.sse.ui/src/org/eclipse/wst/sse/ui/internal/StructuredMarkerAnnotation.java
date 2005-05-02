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
package org.eclipse.wst.sse.ui.internal;


import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugModelPresentation;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.source.IAnnotationPresentation;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.MarkerAnnotation;
import org.eclipse.ui.texteditor.MarkerUtilities;
import org.eclipse.wst.sse.ui.internal.reconcile.TemporaryAnnotation;


/**
 * This is overridden to get around the problem of being registered as a
 * org.eclipse.wst.validation.core.problemmarker rather than a
 * org.eclipse.core.resource.problemmarker causing all problems to be skipped
 * in the OverviewRuler
 */
public class StructuredMarkerAnnotation extends MarkerAnnotation implements IAnnotationPresentation {

	private IDebugModelPresentation fPresentation;
	//controls if icon should be painted gray
	private boolean fIsGrayed = false;
	String fAnnotationType = null;

	StructuredMarkerAnnotation(IMarker marker) {
		super(marker);
		initAnnotationType();
	}
	
	public final String getAnnotationType() {
		return fAnnotationType;
	}
	
	/**
	 * Eventually will have to use IAnnotationPresentation & IAnnotationExtension
	 * @see org.eclipse.ui.texteditor.MarkerAnnotation#getImage(org.eclipse.swt.widgets.Display)
	 */
	protected Image getImage(Display display) {
		Image image = null;
		if (fAnnotationType == TemporaryAnnotation.ANNOT_BREAKPOINT) {
			image = super.getImage(display);
			if (image == null) {
				IMarker marker = getMarker();
				if (marker != null && marker.exists()) {
					image = fPresentation.getImage(getMarker());
				}
			}
		}
		else if(fAnnotationType == TemporaryAnnotation.ANNOT_ERROR) {
			image = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_ERROR_TSK);
		}
		else if(fAnnotationType == TemporaryAnnotation.ANNOT_WARNING) {
			image = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_WARN_TSK);
		}
		else if(fAnnotationType == TemporaryAnnotation.ANNOT_INFO) {
			image = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_INFO_TSK);
		}
		else if(fAnnotationType == TemporaryAnnotation.ANNOT_TASK) {
			image = PlatformUI.getWorkbench().getSharedImages().getImage(IDE.SharedImages.IMG_OBJS_TASK_TSK);
		}
		else if(fAnnotationType == TemporaryAnnotation.ANNOT_BOOKMARK) {
			image = PlatformUI.getWorkbench().getSharedImages().getImage(IDE.SharedImages.IMG_OBJS_BKMRK_TSK);
		}
			
		if(image != null && isGrayed())
			setImage(getGrayImage(display, image));
		else 
			setImage(image);
		
		return super.getImage(display);
	}

	private Image getGrayImage(Display display, Image image) {
		if (image != null) {
			String key= Integer.toString(image.hashCode());
			// make sure we cache the gray image
			Image grayImage = JFaceResources.getImageRegistry().get(key);
			if (grayImage == null) {
				grayImage= new Image(display, image, SWT.IMAGE_GRAY);
				JFaceResources.getImageRegistry().put(key, grayImage);
			}
			image= grayImage;
		}
		return image;
	}
	
	public final boolean isGrayed() {
		return fIsGrayed;
	}
	
	public final void setGrayed(boolean grayed) {
		fIsGrayed = grayed;
	}
	
	/**
	 * Initializes the annotation's icon representation and its drawing layer
	 * based upon the properties of the underlying marker.
	 */
	protected void initAnnotationType() {
		
		IMarker marker = getMarker();
		if (MarkerUtilities.isMarkerType(marker, IBreakpoint.BREAKPOINT_MARKER)) {

			if (fPresentation == null)
				fPresentation = DebugUITools.newDebugModelPresentation();

			setImage(null); // see bug 32469
			setLayer(4);
			//fImageType = BREAKPOINT_IMAGE;
			fAnnotationType = TemporaryAnnotation.ANNOT_BREAKPOINT;

		} else {
			
			fAnnotationType = TemporaryAnnotation.ANNOT_UNKNOWN;
			try {
				if (marker.isSubtypeOf(IMarker.PROBLEM)) {
					int severity = marker.getAttribute(IMarker.SEVERITY, -1);
					switch (severity) {
						case IMarker.SEVERITY_ERROR :
							fAnnotationType = TemporaryAnnotation.ANNOT_ERROR;
							break;
						case IMarker.SEVERITY_WARNING :
							fAnnotationType = TemporaryAnnotation.ANNOT_WARNING;
							break;
						case IMarker.SEVERITY_INFO :
							fAnnotationType = TemporaryAnnotation.ANNOT_INFO;
							break;
					}
				} else if (marker.isSubtypeOf(IMarker.TASK))
					fAnnotationType = TemporaryAnnotation.ANNOT_TASK;
				else if (marker.isSubtypeOf(NewSearchUI.SEARCH_MARKER)) {
					fAnnotationType = TemporaryAnnotation.ANNOT_SEARCH;
				} else if (marker.isSubtypeOf(IMarker.BOOKMARK))
					fAnnotationType = TemporaryAnnotation.ANNOT_BOOKMARK;

			} catch (CoreException e) {
				Logger.logException(e);
			}
		}
	}
}
