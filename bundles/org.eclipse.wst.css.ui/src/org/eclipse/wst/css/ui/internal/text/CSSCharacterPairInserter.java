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
package org.eclipse.wst.css.ui.internal.text;

import org.eclipse.wst.sse.ui.internal.provisional.typing.AbstractCharacterPairInserter;

public class CSSCharacterPairInserter extends AbstractCharacterPairInserter {

	public boolean hasPair(char c) {
		switch (c) {
			case '"':
			case '\'':
			case '[':
			case '(':
				return true;
			default:
				return false;
		}
	}

	protected char getPair(char c) {
		switch (c) {
			case '\'':
			case '"':
				return c;
			case '(':
				return ')';
			case '[':
				return ']';
			default:
				throw new IllegalArgumentException();
		}
	}

}
