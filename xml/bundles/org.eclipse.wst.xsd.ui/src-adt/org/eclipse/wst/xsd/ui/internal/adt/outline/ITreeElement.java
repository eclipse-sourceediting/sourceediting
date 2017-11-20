/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.adt.outline;

import org.eclipse.swt.graphics.Image;

public interface ITreeElement
{
  public final static ITreeElement[] EMPTY_LIST = {};
  ITreeElement[] getChildren();
  ITreeElement getParent();
  boolean hasChildren();
  String getText();
  Image getImage();
}
