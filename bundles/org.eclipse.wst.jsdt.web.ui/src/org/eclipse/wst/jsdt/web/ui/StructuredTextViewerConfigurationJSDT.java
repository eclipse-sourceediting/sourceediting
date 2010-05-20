/*******************************************************************************
 * Copyright (c) 2004, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.jsdt.web.ui;

import org.eclipse.jface.text.formatter.IContentFormatter;
import org.eclipse.jface.text.formatter.MultiPassContentFormatter;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.wst.html.core.text.IHTMLPartitions;
import org.eclipse.wst.html.ui.StructuredTextViewerConfigurationHTML;
import org.eclipse.wst.jsdt.web.ui.internal.format.FormattingStrategyJSDT;

/**
*

* Provisional API: This class/interface is part of an interim API that is still under development and expected to
* change significantly before reaching stability. It is being made available at this early stage to solicit feedback
* from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken
* (repeatedly) as the API evolves.
* 
 * Configuration for a source viewer which shows Html and supports JSDT.
 * <p>
 * Clients can subclass and override just those methods which must be specific
 * to their needs.
 * </p>
 * 
 * @see org.eclipse.wst.sse.ui.StructuredTextViewerConfiguration
 * @since 1.0
 */
public class StructuredTextViewerConfigurationJSDT extends StructuredTextViewerConfigurationHTML {
	/**
	 * Create new instance of StructuredTextViewerConfigurationHTML
	 */
	public StructuredTextViewerConfigurationJSDT() {
		// Must have empty constructor to createExecutableExtension
		super();
	}
	
	public IContentFormatter getContentFormatter(ISourceViewer sourceViewer) {
		final IContentFormatter formatter = super.getContentFormatter(sourceViewer);
		if(formatter instanceof MultiPassContentFormatter) {
		/*
		 * Check for any externally supported auto edit strategies from EP.
		 * [Bradley Childs - childsb@us.ibm.com]
		 */
		String[] contentTypes = getConfiguredContentTypes(sourceViewer);
		for (int i = 0; i < contentTypes.length; i++) {
				if (IHTMLPartitions.SCRIPT.equals(contentTypes[i])) {
					((MultiPassContentFormatter) formatter).setSlaveStrategy(new FormattingStrategyJSDT(), contentTypes[i]);
				}
			}
		}
		return formatter;
	}
}
