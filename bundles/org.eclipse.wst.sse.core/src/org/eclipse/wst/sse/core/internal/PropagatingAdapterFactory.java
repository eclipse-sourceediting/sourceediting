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
package org.eclipse.wst.sse.core.internal;



import java.util.ArrayList;

import org.eclipse.wst.sse.core.IAdapterFactory;



public interface PropagatingAdapterFactory extends IAdapterFactory {

	void addContributedFactories(IAdapterFactory factory);

	void setContributedFactories(ArrayList list);

}
