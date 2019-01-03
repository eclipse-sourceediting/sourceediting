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
package org.eclipse.jst.jsp.core.internal.modelhandler;



import org.eclipse.wst.sse.core.internal.ltk.modelhandler.EmbeddedTypeHandler;

/**
 * This class is only for remembering old and new embedded handlers, 
 * in the event a re-init is needed.
 */
public class EmbeddedTypeStateData {


	EmbeddedTypeHandler oldHandler;
	EmbeddedTypeHandler newHandler;

	public EmbeddedTypeStateData(EmbeddedTypeHandler oldHandler, EmbeddedTypeHandler newHandler) {
		this.oldHandler = oldHandler;
		this.newHandler = newHandler;
	}

	public EmbeddedTypeHandler getNewHandler() {
		return newHandler;
	}

	public EmbeddedTypeHandler getOldHandler() {
		return oldHandler;
	}

}
