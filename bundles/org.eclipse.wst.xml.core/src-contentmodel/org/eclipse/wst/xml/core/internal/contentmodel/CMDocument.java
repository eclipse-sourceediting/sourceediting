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
package org.eclipse.wst.xml.core.internal.contentmodel;

/**
 * CMDocument interface
 */
public interface CMDocument extends CMNode {
/**
 * getElements method
 * @return CMNamedNodeMap
 *
 * Returns CMNamedNodeMap of ElementDeclaration
 */
CMNamedNodeMap getElements();
/**
 * getEntities method
 * @return CMNamedNodeMap
 *
 * Returns CMNamedNodeMap of EntityDeclaration
 */
CMNamedNodeMap getEntities();
/**
 * getNamespace method
 * @return CMNamespace
 */
CMNamespace getNamespace();
}
