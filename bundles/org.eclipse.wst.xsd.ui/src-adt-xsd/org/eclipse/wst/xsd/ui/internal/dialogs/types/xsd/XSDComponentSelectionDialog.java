/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.dialogs.types.xsd;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.wst.xsd.ui.internal.dialogs.types.common.IComponentSelectionProvider;
import org.eclipse.wst.xsd.ui.internal.dialogs.types.xml.XMLComponentSelectionDialog;

public class XSDComponentSelectionDialog extends XMLComponentSelectionDialog {

	public static final int ELEMENT = 1;
	public static final int TYPE = 2;
	
	public XSDComponentSelectionDialog(Shell shell, String dialogTitle,
			IComponentSelectionProvider provider) {
		super(shell, dialogTitle, provider);
	}
	
	
}
