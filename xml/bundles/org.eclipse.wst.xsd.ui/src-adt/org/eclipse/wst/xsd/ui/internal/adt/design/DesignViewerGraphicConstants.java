/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.adt.design;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;

public interface DesignViewerGraphicConstants
{
  public static final String SCALED_HANDLE_LAYER = "Scaled Handle Layer"; //$NON-NLS-1$   
  public final static Font  smallFont = new Font(Display.getCurrent(), "Tahoma", 6, SWT.NONE); //$NON-NLS-1$
}
