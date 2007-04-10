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
package org.eclipse.wst.sse.core.internal.text.rules;

import org.eclipse.jface.text.IRegion;

/**
 * Like super class except allows length and offset to be modified. This is
 * convenient for some algorithms, and allows region objects to be reused.
 * Note: There MIGHT be some code that assumes regions are immutable. This
 * class would not be appropriate for those uses.
 */
public interface IStructuredRegion extends IRegion {
	void setLength(int length);

	void setOffset(int offset);
}
