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
package org.eclipse.jst.jsp.ui.internal.style.java;


/**
 * A Java aware word detector.
 */
public class JavaWordDetector implements org.eclipse.jface.text.rules.IWordDetector {

	/**
	 * @see org.eclipse.jface.text.rules.IWordDetector#isWordIdentifierPart
	 */
	public boolean isWordPart(char c) {
		return Character.isJavaIdentifierPart(c);
	}

	/**
	 * @see org.eclipse.jface.text.rules.IWordDetector#isWordIdentifierStart
	 */
	public boolean isWordStart(char c) {
		return Character.isJavaIdentifierStart(c);
	}
}
