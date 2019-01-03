/*******************************************************************************
 * Copyright (c) 2001, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/
package org.eclipse.wst.sse.core.internal.format;

import org.eclipse.core.runtime.IProgressMonitor;
import org.w3c.dom.Node;

/**
 * Knows how to format a particular node.
 * 
 * eg. generic node, text node, document node, comment, etc...
 */
public interface IStructuredFormatter {
	
	void format(Node node);

	void format(Node node, IStructuredFormatContraints formatContraints);

	IStructuredFormatContraints getFormatContraints();

	IStructuredFormatPreferences getFormatPreferences();

	void setFormatPreferences(IStructuredFormatPreferences formatPreferences);

	void setProgressMonitor(IProgressMonitor monitor);
}
