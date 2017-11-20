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
package org.eclipse.wst.xml.core.internal.contentmodel.modelquery;

import org.eclipse.wst.xml.core.internal.contentmodel.CMDocument;
import org.w3c.dom.Node;



/**
 *
 */
public interface ModelQueryCMProvider
{
  /**
   * Returns the CMDocument that corresponds to the DOM Node.
   * or null if no CMDocument is appropriate for the DOM Node.
   */
  CMDocument getCorrespondingCMDocument(Node node);
}
