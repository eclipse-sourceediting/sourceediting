/*******************************************************************************
 * Copyright (c) 2004, 2011 IBM Corporation and others.
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
package org.eclipse.wst.html.core.internal.validate;

import org.eclipse.wst.sse.core.internal.validate.ErrorInfo;

abstract class AbstractErrorInfo implements ErrorInfo, ErrorState {


	private int state = NONE_ERROR;
	private Segment seg = null;

	public AbstractErrorInfo(int state, Segment seg) {
		super();
		this.state = state;
		this.seg = seg;
	}

	abstract public short getTargetType();

	public int getLength() {
		return (seg == null) ? 0 : seg.getLength();
	}

	public int getOffset() {
		return (seg == null) ? 0 : seg.getOffset();
	}

	public int getState() {
		return this.state;
	}

	/**
	 * @return the arguments to be injected into a message for the user about this error
	 */
	public String[] getMessageArguments() {
		String hint = getHint();
		return hint != null ? new String[] { hint } : null;
	}

}
