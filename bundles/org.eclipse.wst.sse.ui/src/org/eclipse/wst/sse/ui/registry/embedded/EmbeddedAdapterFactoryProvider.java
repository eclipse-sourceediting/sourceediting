/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
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
package org.eclipse.wst.sse.ui.registry.embedded;

import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.sse.core.internal.ltk.modelhandler.EmbeddedTypeHandler;


public interface EmbeddedAdapterFactoryProvider {

	public void addAdapterFactories(IStructuredModel structuredModel);

	public boolean isFor(EmbeddedTypeHandler typeHandler);
}
