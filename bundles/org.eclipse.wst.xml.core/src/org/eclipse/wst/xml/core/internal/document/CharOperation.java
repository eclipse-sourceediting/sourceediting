/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xml.core.internal.document;

class CharOperation {

	private CharOperation() {
	}

	static int indexOf(char[] array, char c) {
		return indexOf(array, c, 0);
	}

	static int indexOf(char[] array, char c, int start) {
		for (int i = start; i < array.length; i++) {
			if (array[i] == c)
				return i;
		}
		return -1;
	}

}
