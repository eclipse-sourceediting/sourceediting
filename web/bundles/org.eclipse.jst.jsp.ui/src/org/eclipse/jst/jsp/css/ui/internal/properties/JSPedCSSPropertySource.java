/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.css.ui.internal.properties;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jst.jsp.ui.internal.JSPUIMessages;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSNode;
import org.eclipse.wst.css.ui.internal.properties.CSSPropertySource;



public class JSPedCSSPropertySource extends CSSPropertySource {

	public JSPedCSSPropertySource(ICSSNode target) {
		super(target);
	}
	
	public void setPropertyValue(Object name, Object value) {
		// workaround to avoid DOMException: if value contains jsp element, nothing happen.
		String v = value.toString();
		if (v.indexOf("${") != -1 || v.indexOf("<%=") != -1){
			IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			String title = JSPUIMessages.Title_InvalidValue; //$NON-NLS-1$
			String message = JSPUIMessages.Message_InvalidValue; //$NON-NLS-1$
			MessageDialog.openWarning(window.getShell(), title, message);
			return;
		}
		super.setPropertyValue(name, value);
	}
}
