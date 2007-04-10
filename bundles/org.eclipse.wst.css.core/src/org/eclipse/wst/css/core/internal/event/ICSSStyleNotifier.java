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
package org.eclipse.wst.css.core.internal.event;



import java.util.List;

/**
 * 
 */
public interface ICSSStyleNotifier extends ICSSStyleListener {

	/**
	 * 
	 */
	void addStyleListener(ICSSStyleListener listener);

	/**
	 * 
	 */
	List getStyleListeners();

	/**
	 */
	boolean isRecording();

	/**
	 * 
	 */
	void removeStyleListener(ICSSStyleListener listener);
}
