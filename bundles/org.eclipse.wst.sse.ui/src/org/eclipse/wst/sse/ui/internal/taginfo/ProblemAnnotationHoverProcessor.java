/*******************************************************************************
 * Copyright (c) 2001, 2005 IBM Corporation and others.
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
package org.eclipse.wst.sse.ui.internal.taginfo;

import org.eclipse.jface.text.source.Annotation;
import org.eclipse.ui.texteditor.DefaultMarkerAnnotationAccess;

/**
 * Hover help that displays problem annotations when shown in text of editor.
 * 
 * @author amywu
 */
public class ProblemAnnotationHoverProcessor extends AnnotationHoverProcessor {

	// these strings are derived from the annotationTypes extension in
	// org.eclipse.ui.editors plugin
	// if those strings change, then these strings need to change as well
	final private String ANNOTATION_ERROR = "org.eclipse.wst.sse.ui.temp.error"; //$NON-NLS-1$
	final private String ANNOTATION_WARNING = "org.eclipse.wst.sse.ui.temp.warning"; //$NON-NLS-1$
	private DefaultMarkerAnnotationAccess fAnnotationAccess = new DefaultMarkerAnnotationAccess();

	/**
	 *  
	 */
	public ProblemAnnotationHoverProcessor() {
		super();
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.ui.taginfo.AnnotationHoverProcessor#isAnnotationValid(org.eclipse.jface.text.source.Annotation)
	 */
	protected boolean isAnnotationValid(Annotation a) {
		String type = a.getType();
		if (fAnnotationAccess.isSubtype(type, ANNOTATION_ERROR) || fAnnotationAccess.isSubtype(type, ANNOTATION_WARNING))
			return super.isAnnotationValid(a);
		return false;
	}
}
