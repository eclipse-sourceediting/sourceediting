/*
 * Copyright (c) 2005 IBM Corporation and others.
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
package org.eclipse.wst.sse.ui.internal.correction;

import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.source.ISourceViewer;

/**
 * Provides the appropriate correction assistant for a given source viewer.
 * This class should only be a placeholder until the base implements a common
 * way for quickfix/quick assist.
 */
abstract public class CorrectionAssistantProvider {
	/**
	 * Returns the correction assistant for the given sourceviewer.
	 * 
	 * @param sourceViewer
	 * @return
	 */
	abstract public IContentAssistant getCorrectionAssistant(ISourceViewer sourceViewer);
}
