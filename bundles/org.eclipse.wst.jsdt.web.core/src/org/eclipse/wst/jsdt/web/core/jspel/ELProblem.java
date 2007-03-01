/*******************************************************************************
 * Copyright (c) 2005 BEA Systems and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     BEA Systems - initial implementation
 *     
 *******************************************************************************/

package org.eclipse.wst.jsdt.web.core.jspel;

import org.eclipse.jface.text.Position;

public class ELProblem {
	private Position fPos;
	private String fMessage;

	public ELProblem(Position pos, String message) {
		fPos = pos;
		fMessage = message;
	}

	public String getMessage() {
		return fMessage;
	}

	public Position getPosition() {
		return fPos;
	}
}
