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
package org.eclipse.wst.sse.ui.internal.reconcile;

import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.reconciler.IReconcileResult;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.wst.sse.ui.ITemporaryAnnotation;


/**
 * An implementation of ITemporaryAnnotation
 * 
 * @ @author pavery
 */
public class TemporaryAnnotation extends Annotation implements ITemporaryAnnotation, IReconcileResult {

	// remember to change these if it changes in the extension point
	// may need a different home for them in the future, but they're here for now
	public final static String ANNOT_ERROR = "org.eclipse.wst.sse.ui.temp.error"; //$NON-NLS-1$
	public final static String ANNOT_WARNING = "org.eclipse.wst.sse.ui.temp.warning"; //$NON-NLS-1$
	public final static String ANNOT_INFO = "org.eclipse.wst.sse.ui.temp.info"; //$NON-NLS-1$

	public final static String ANNOT_BOOKMARK = "org.eclipse.ui.workbench.texteditor.bookmark"; //$NON-NLS-1$
	public final static String ANNOT_TASK = "org.eclipse.ui.workbench.texteditor.task"; //$NON-NLS-1$

	// pa_TODO what should the ID be for this?
	public final static String ANNOT_SEARCH = Annotation.TYPE_UNKNOWN;
	public final static String ANNOT_UNKNOWN = Annotation.TYPE_UNKNOWN;

	private int fProblemID;
	private Object fAdditionalFixInfo = null;

	private Object fKey = null;
	private Position fPosition = null;

	public TemporaryAnnotation(Position p, String type, String message, IReconcileAnnotationKey key) {
		super();
		fPosition = p;
		setType(type);
		fKey = key;
		setText(message);
	}

	public TemporaryAnnotation(Position p, String type, String message, IReconcileAnnotationKey key, int problemId) {
		super();
		fPosition = p;
		fKey = key;
		setType(type);
		setText(message);
		fProblemID = problemId;
	}

	/* (non-Javadoc)
	 */
	public String getDescription() {
		return getText();
	}

	public Object getKey() {
		return fKey;
	}

	public Position getPosition() {
		return fPosition;
	}

	public String toString() {
		return "" + fPosition.getOffset() + ':' + fPosition.getLength() + ": " + getText(); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * @deprecated use getText instead TODO remove in C5
	 */
	public String getMessage() {
		return getText();
	}


	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.source.Annotation#isPersistent()
	 */
	public boolean isPersistent() {
		return false;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		// this check doesn't take into consideration that annotation positions that change from a text edit before it
		// we should be checking if the annotation is still on the same line, and the distance from the start of the line is the same
		if(obj instanceof TemporaryAnnotation) {
			TemporaryAnnotation ta = (TemporaryAnnotation) obj;
			return ta.getText().equals(this.getText()) && ta.getPosition().equals(this.getPosition());
		}
		return super.equals(obj);
	}

	/**
	 * @return Returns the problemID.
	 */
	public int getProblemID() {
		return fProblemID;
	}

	/**
	 * Sets additional information useful to fixing this problem.
	 * @param an Object that contains additional info on how to fix this problem
	 */
	public void setAdditionalFixInfo(Object info) {
		fAdditionalFixInfo = info;
	}

	/**
	 * Additional info required to fix this problem.
	 * @return an Object that contains additional info on how to fix this problem, or null if there is none
	 */
	public Object getAdditionalFixInfo() {
		return fAdditionalFixInfo;
	}
}
