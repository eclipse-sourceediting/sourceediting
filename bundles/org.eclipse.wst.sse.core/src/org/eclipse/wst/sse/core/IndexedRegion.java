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
package org.eclipse.wst.sse.core;



/**
 * These are APIs found to be needed in our tree outline viewer, that go
 * beyond the standard DOM Node Interface.
 */
public interface IndexedRegion {

	boolean contains(int testPosition);

	int getEndOffset();

	int getStartOffset();
}
