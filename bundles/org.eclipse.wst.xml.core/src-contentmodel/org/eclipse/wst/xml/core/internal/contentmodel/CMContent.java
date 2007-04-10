/*******************************************************************************
 * Copyright (c) 2002, 2006 IBM Corporation and others.
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
package org.eclipse.wst.xml.core.internal.contentmodel;

public interface CMContent extends CMNode
{
/**
 * getMaxOccur method
 * @return int
 *
 * If -1, it's UNBOUNDED.
 */
int getMaxOccur();
/**
 * getMinOccur method
 * @return int
 *
 * If 0, it's OPTIONAL.
 * If 1, it's REQUIRED.
 */
int getMinOccur();
}
