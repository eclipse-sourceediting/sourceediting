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
package org.eclipse.wst.xml.ui.taginfo;

import org.eclipse.jface.text.ITextHover;
import org.eclipse.wst.sse.ui.taginfo.AbstractBestMatchHoverProcessor;


/**
 * Provides the best xml hover help documentation (by using other hover help
 * processors) Priority of hover help processors is: ProblemHoverProcessor,
 * XMLTagInfoHoverProcessor, AnnotationHoverProcessor
 */
public class XMLBestMatchHoverProcessor extends AbstractBestMatchHoverProcessor {
	XMLTagInfoHoverProcessor fTagInfoHover;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.ui.structured.taginfo.AbstractBestMatchHoverProcessor#getTagInfoHover()
	 */
	protected ITextHover getTagInfoHover() {
		if (fTagInfoHover == null) {
			fTagInfoHover = new XMLTagInfoHoverProcessor();
		}
		return fTagInfoHover;
	}

}
