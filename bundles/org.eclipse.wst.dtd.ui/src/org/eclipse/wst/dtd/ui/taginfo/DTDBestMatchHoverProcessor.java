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
package org.eclipse.wst.dtd.ui.taginfo;

import org.eclipse.jface.text.ITextHover;
import org.eclipse.wst.sse.ui.taginfo.AbstractBestMatchHoverProcessor;


/**
 * @author amywu
 */
public class DTDBestMatchHoverProcessor extends AbstractBestMatchHoverProcessor {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.dtd.ui.taginfo.AbstractBestMatchHoverProcessor#getTagInfoHover()
	 */
	protected ITextHover getTagInfoHover() {
		// DTD has no taginfo hover
		return null;
	}

}
