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

/**
 * @deprecated use Annotation.getType() now, with one of the types specified via extension point:
 * see <code>extension point="org.eclipse.ui.editors.annotationTypes"</code>
 * and <code>extension point="org.eclipse.ui.editors.markerAnnotationSpecification"</code>

 */
final public class StructuredAnnotationType {

	public final static StructuredAnnotationType ALL = new StructuredAnnotationType();
	public final static StructuredAnnotationType UNKNOWN = new StructuredAnnotationType();
	public final static StructuredAnnotationType BOOKMARK = new StructuredAnnotationType();
	public final static StructuredAnnotationType TASK = new StructuredAnnotationType();
	public final static StructuredAnnotationType ERROR = new StructuredAnnotationType();
	public final static StructuredAnnotationType WARNING = new StructuredAnnotationType();
	public final static StructuredAnnotationType INFO = new StructuredAnnotationType();
	public final static StructuredAnnotationType SEARCH = new StructuredAnnotationType();


	private StructuredAnnotationType() {
	}

	public String toString() {
		if (this == ALL)
			return "StructuredAnnotationType.ALL"; //$NON-NLS-1$

		if (this == UNKNOWN)
			return "StructuredAnnotationType.UNKNOWN"; //$NON-NLS-1$

		if (this == BOOKMARK)
			return "StructuredAnnotationType.BOOKMARK"; //$NON-NLS-1$

		if (this == TASK)
			return "StructuredAnnotationType.TASK"; //$NON-NLS-1$

		if (this == ERROR)
			return "StructuredAnnotationType.ERROR"; //$NON-NLS-1$

		if (this == WARNING)
			return "StructuredAnnotationType.WARNING"; //$NON-NLS-1$

		if (this == SEARCH)
			return "StructuredAnnotationType.SEARCH"; //$NON-NLS-1$

		return ""; //$NON-NLS-1$
	}
}
