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
package org.eclipse.wst.xsd.ui.internal.design.figures;

import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorPlugin;

public class SpacingFigure extends Label
{
  public SpacingFigure()
  {
    super(""); //$NON-NLS-1$
    setIcon(XSDEditorPlugin.getXSDImage("icons/Dot.gif")); //$NON-NLS-1$
    setBorder(new MarginBorder(3, 0, 3, 0));
  }
}
