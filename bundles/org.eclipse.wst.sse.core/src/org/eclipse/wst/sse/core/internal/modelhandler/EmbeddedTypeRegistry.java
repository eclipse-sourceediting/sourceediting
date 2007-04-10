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
package org.eclipse.wst.sse.core.internal.modelhandler;

import org.eclipse.wst.sse.core.internal.ltk.modelhandler.EmbeddedTypeHandler;

/**
 * The entries in this registry are, conceptually, singleton's Since only one
 * instance is created in the registry, and then that instance returned when
 * required.
 * 
 * Note that there is intentionally no 'remove' method, Since the registry
 * itself is read it when once, from the platform's plugin registry, and is
 * not intended to be modified after that. A change in an extenstion in a
 * plugin.xml will only take effect when the workbench is re-started.
 *  
 */
public interface EmbeddedTypeRegistry {

	/**
	 * Method to return the specific type for the specific mimetype.
	 */
	public EmbeddedTypeHandler getTypeFor(String mimeType);

}
