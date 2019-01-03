/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xml.core.internal.provisional.format;

import org.eclipse.wst.sse.core.internal.format.IStructuredFormatContraints;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;

/**
 * Formats entities which really need no formatting. They are just like text
 * nodes.
 */
class NoMoveFormatter extends NodeFormatter {
	protected void formatIndentationBeforeNode(IDOMNode node, IStructuredFormatContraints formatContraints) {
		// node nothing
	}

	protected void formatIndentationAfterNode(IDOMNode node, IStructuredFormatContraints formatContraints) {
		// still do nothing
	}
}
