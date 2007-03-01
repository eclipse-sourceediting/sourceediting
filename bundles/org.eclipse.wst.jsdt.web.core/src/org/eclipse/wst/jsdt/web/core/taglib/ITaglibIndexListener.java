/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.jsdt.web.core.taglib;

/**
 * A listener for changes in the index's records.
 * 
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 */
public interface ITaglibIndexListener {

	/**
	 * Notifies this listener that an ITaglibRecordEvent has occurred
	 * 
	 * @param event
	 */
	void indexChanged(ITaglibRecordEvent event);
}
