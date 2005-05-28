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
package org.eclipse.wst.css.ui.internal.taginfo;

import org.eclipse.jface.text.ITextHover;
import org.eclipse.wst.sse.ui.internal.taginfo.AbstractBestMatchHoverProcessor;

/**
 * Provides the best css hover help documentation (by using other hover help
 * processors) Priority of hover help processors is: ProblemHoverProcessor,
 * AnnotationHoverProcessor
 */
public class CSSBestMatchHoverProcessor extends AbstractBestMatchHoverProcessor {

	protected ITextHover getTagInfoHover() {
		// CSS has no taginfo hover
		return null;
	}

}