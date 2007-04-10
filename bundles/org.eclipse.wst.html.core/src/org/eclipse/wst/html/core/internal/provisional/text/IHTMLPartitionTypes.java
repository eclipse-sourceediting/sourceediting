/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.core.internal.provisional.text;


/**
 * This interface is not intended to be implemented. It defines the partition
 * types for HTML. Clients should reference the partition type Strings defined
 * here directly.
 * 
 * @deprecated use org.eclipse.wst.html.core.text.IHTMLPartitions
 */
public interface IHTMLPartitionTypes {

	String HTML_DEFAULT = "org.eclipse.wst.html.HTML_DEFAULT"; //$NON-NLS-1$
	String HTML_DECLARATION = "org.eclipse.wst.html.HTML_DECLARATION"; //$NON-NLS-1$
	String HTML_COMMENT = "org.eclipse.wst.html.HTML_COMMENT"; //$NON-NLS-1$

	String SCRIPT = "org.eclipse.wst.html.SCRIPT"; //$NON-NLS-1$
	String STYLE = "org.eclipse.wst.html.STYLE"; //$NON-NLS-1$

	// ISSUE: I think meta tag areas are here too?
}
