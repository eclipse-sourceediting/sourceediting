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
package org.eclipse.wst.sse.ui.taginfo;

import org.eclipse.jface.text.source.Annotation;
import org.eclipse.wst.sse.ui.internal.reconcile.TemporaryAnnotation;


/**
 * Hover help that displays problem annotations when shown in text of editor.  
 * 
 * @author amywu
 */
public class ProblemAnnotationHoverProcessor extends AnnotationHoverProcessor {

	/**
	 * 
	 */
	public ProblemAnnotationHoverProcessor() {
		super();
	}
	
	
	/* (non-Javadoc)
	 */
	protected boolean isAnnotationValid(Annotation a) {
		String type = a.getType();
		if (TemporaryAnnotation.ANNOT_ERROR.equals(type) || TemporaryAnnotation.ANNOT_WARNING.equals(type))
			return super.isAnnotationValid(a);
		return false;
	}
}
