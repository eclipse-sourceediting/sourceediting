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
package org.eclipse.wst.sse.core.text;

import org.eclipse.jface.text.ITypedRegion;

/**
 * Similar to extended interface, except it allows the length, offset, and
 * type to be set. This is useful when iterating through a number of "small"
 * regions, that all map to the the same partion regions.
 */
public interface StructuredTypedRegion extends StructuredRegion, ITypedRegion {
	void setType(String type);
}
