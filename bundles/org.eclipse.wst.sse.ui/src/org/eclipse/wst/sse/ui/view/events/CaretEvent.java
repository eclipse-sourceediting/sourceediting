/*
* Copyright (c) 2002 IBM Corporation and others.
* All rights reserved.   This program and the accompanying materials
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*   IBM - Initial API and implementation
*   Jens Lukowski/Innoopract - initial renaming/restructuring
* 
*/
package org.eclipse.wst.sse.ui.view.events;



/**
 * Simply holds data to be passed to CaretEventListeners
 */
public class CaretEvent extends java.util.EventObject {

	// initialize to impossible location
	int fPosition = -1;

	/**
	 * doesnt't make sense to have a CaretEvent without the Caret postion, so use other constructor
	 */
	protected CaretEvent(Object source) {
		super(source);
	}

	/**
	 * This is the preferred constructor.
	 */
	public CaretEvent(Object source, int position) {
		super(source);
		setPosition(position);
	}

	public int getPosition() {
		return fPosition;
	}

	void setPosition(int newPosition) {
		fPosition = newPosition;
	}
}
