/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.internal.style.jspel;



/**
 * A java aware white space detector.
 */
public class JSPELWhitespaceDetector implements org.eclipse.jface.text.rules.IWhitespaceDetector {

	/**
	 * @see org.eclipse.jface.text.rules.IWhitespaceDetector#isWhitespace
	 */
	public boolean isWhitespace(char c) {
		return Character.isWhitespace(c);
	}
}
