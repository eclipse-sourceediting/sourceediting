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

import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.source.LineChangeHover;
import org.eclipse.swt.widgets.Shell;

/**
 * Escapes diff hover presentation text (converts < to &lt; > to &gt; etc...)
 * so that html in the diff file (displayed in hover) isn't presented as style (bold, italic, colors, etc...)
 * @author pavery
 */
public class StructuredLineChangeHover extends LineChangeHover {
	//	/* 
	//	 * @see org.eclipse.jface.text.source.LineChangeHover#getHoverInfo(org.eclipse.jface.text.source.ISourceViewer, int)
	//	 */
	//	public String getHoverInfo(ISourceViewer sourceViewer, int lineNumber) {
	//		return StringUtils.convertToHTMLContent(super.getHoverInfo(sourceViewer, lineNumber));
	//	}
	//	/* 
	//	 * @see org.eclipse.jface.text.source.LineChangeHover#getHoverInfo(org.eclipse.jface.text.source.ISourceViewer, int, int, int)
	//	 */
	//	public String getHoverInfo(ISourceViewer sourceViewer, int lineNumber, int first, int number) {
	//		return StringUtils.convertToHTMLContent(super.getHoverInfo(sourceViewer, lineNumber, first, number));
	//	}
	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.source.IAnnotationHoverExtension#getInformationControlCreator()
	 */
	public IInformationControlCreator getInformationControlCreator() {
		// use the default information control creator that just displays text as text, not html content
		// because there is no special html that should be presented when just showing diff
		// in the future, sourceviewer should be used instead of this plain text control like java uses
		// SourceViewerInformationControl
		return new IInformationControlCreator() {
			public IInformationControl createInformationControl(Shell parent) {
				return new DefaultInformationControl(parent);
			}
		};
	}
}
