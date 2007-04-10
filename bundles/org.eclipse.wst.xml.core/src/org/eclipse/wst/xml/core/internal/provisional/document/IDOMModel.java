/*******************************************************************************
 * Copyright (c) 2001, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/
package org.eclipse.wst.xml.core.internal.provisional.document;

import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.xml.core.internal.document.XMLModelNotifier;

/**
 * Provides means to get the XMLModel form of IStrucutredModel. Not to be
 * implemented or extended by clients.
 * 
 * @plannedfor 1.0
 */
public interface IDOMModel extends IStructuredModel {

	/**
	 * Returns the DOM Document.
	 * 
	 * @return the DOM Document.
	 */
	IDOMDocument getDocument();

	/**
	 * 
	 * Returns an source generator appropriate for this model.
	 * 
	 * @return the source generator
	 */
	ISourceGenerator getGenerator();

	/**
	 * NOT CLIENT API
	 * 
	 * Returns an XMLModelNotifier. Clients should not use.
	 * 
	 * ISSUE: should be "internalized".
	 * 
	 */
	XMLModelNotifier getModelNotifier();

	/**
	 * NOT CLIENT API
	 *
	 * Sets the model notifier Clients should not use.
	 * 
	 * ISSUE: need to review with legacy clients.
	 */
	void setModelNotifier(XMLModelNotifier notifier);
}
