/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.taginfo;

import org.eclipse.jface.text.ITextHover;
import org.eclipse.ui.IEditorPart;
import org.eclipse.wst.sse.ui.taginfo.AbstractBestMatchHoverProcessor;

/**
 * Provides the best jsp java hover help documentation (by using other hover help processors)
 * Priority of hover help processors is:
 * AnnotationHoverProcessor, JSPJavaJavadocHover
 */
public class JSPJavaBestMatchHoverProcessor extends AbstractBestMatchHoverProcessor {
	JSPJavaJavadocHover fTagInfoHover;

	/* (non-Javadoc)
	 * @see com.ibm.sse.editor.structured.taginfo.AbstractBestMatchHoverProcessor#getTagInfoHover()
	 */
	protected ITextHover getTagInfoHover() {
		if (fTagInfoHover == null) {
			fTagInfoHover = new JSPJavaJavadocHover();
		}
		return fTagInfoHover;
	}

	public void setEditor(IEditorPart editor) {
		// jspjavajavadoc hover requires an editor to be set
		((JSPJavaJavadocHover) getTagInfoHover()).setEditor(editor);
	}
}