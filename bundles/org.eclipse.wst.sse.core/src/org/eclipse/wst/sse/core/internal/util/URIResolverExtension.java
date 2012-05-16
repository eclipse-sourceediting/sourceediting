/*******************************************************************************
 * Copyright (c) 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.sse.core.internal.util;

/**
 * Extension to the {@link URIResolver} interface. Implementing this interface
 * allows for a new copy of the URIResolver to be created 
 *
 */
public interface URIResolverExtension {
	/**
	 * Creates a new instance of the implementing {@link URIResolver}
	 * 
	 * @return a new instance of the {@link URIResolver}
	 */
	URIResolver newInstance();
}