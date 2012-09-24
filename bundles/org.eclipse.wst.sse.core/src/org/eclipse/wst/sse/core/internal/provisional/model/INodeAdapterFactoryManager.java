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

package org.eclipse.wst.sse.core.internal.provisional.model;

import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapterFactory;
/**
 * Provides a means for clients to register IAdapterFactories for use
 * by infrastructure when StructuredModels are created. 
 */
public interface INodeAdapterFactoryManager {

	/**
	 * 
	 * @param factory
	 * @param contentType
	 */
	void addAdapterFactory(INodeAdapterFactory factory, IContentType contentType);

	void removeAdapterFactory(INodeAdapterFactory factory, IContentType contentType);


}
