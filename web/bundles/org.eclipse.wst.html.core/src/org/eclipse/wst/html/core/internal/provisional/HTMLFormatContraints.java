/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
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
package org.eclipse.wst.html.core.internal.provisional;

import org.eclipse.wst.sse.core.internal.format.IStructuredFormatContraints;

/**
 * @deprecated
 * Please un-deprecate this if Page Designer thinks they are needed.
 */
public interface HTMLFormatContraints extends IStructuredFormatContraints {
	/**
	 * @deprecated
	 * 	It's very hard to keep the available line width accurate.
	 * 	Sometimes a node wants to start on a new line, sometimes it doesn't.
	 * 	It's best for the node to figure out the available line width on the fly.
	 */
	int getAvailableLineWidth();

	/**
	 * @deprecated
	 * 	It's very hard to keep the available line width accurate.
	 * 	Sometimes a node wants to start on a new line, sometimes it doesn't.
	 * 	It's best for the node to figure out the available line width on the fly.
	 */
	void setAvailableLineWidth(int availableLineWidth);
}
