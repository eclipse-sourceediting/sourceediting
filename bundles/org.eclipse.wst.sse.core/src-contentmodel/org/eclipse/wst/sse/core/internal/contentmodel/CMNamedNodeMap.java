/*
* Copyright (c) 2002 IBM Corporation and others.
* All rights reserved.   This program and the accompanying materials
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*   IBM - Initial API and implementation
*   Jens Lukowski/Innoopract - initial renaming/restructuring
* 
*/
package org.eclipse.wst.sse.core.internal.contentmodel;
import java.util.Iterator;
/**
 * NamedCMNodeMap
 */
public interface CMNamedNodeMap {
/**
 * getLength method
 * @return int
 */
int getLength();
/**
 * getNamedItem method
 * @return CMNode
 * @param name java.lang.String
 */
CMNode getNamedItem(String name);
/**
 * item method
 * @return CMNode
 * @param index int
 */
CMNode item(int index);

/**
 *
 */
Iterator iterator();
}
