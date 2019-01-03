/*******************************************************************************
 * Copyright (c) 2002, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/
package org.eclipse.wst.xml.core.internal.contentmodel.modelquery;

import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;
import org.w3c.dom.Node;


public interface ModelQueryAction
{
  public static final int INSERT  = 1;
  public static final int REMOVE  = 2;
  public static final int REPLACE = 4;

  public int getKind();
  /**
   * if start index == -1 then no insert is possible
   * if start index != -1 and endIndex == -1 then an insert at any position is possible
   */
  public int getStartIndex();
  public int getEndIndex();
  public Node getParent();
  public CMNode getCMNode();
  public Object getUserData();
  public void setUserData(Object object);
}
