/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.internal.preferences.ui;

import org.eclipse.jst.jsp.ui.internal.style.IStyleConstantsJSP;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.sse.ui.internal.preferences.ui.StyledTextColorPicker;

/**
* Overrides StyledTextColorPicker for special enablement behavior 
* for JSPContent (only background settable)
**/
public class JSPStyledTextColorPicker extends StyledTextColorPicker {
	
	public JSPStyledTextColorPicker(Composite parent, int style) {
		super(parent, style);
	}
	
	/**
	 * Activate controls based on the given local color type.
	 * Overridden to disable foreground color, bold.
	 */
	protected void activate(String namedStyle) {
		super.activate(namedStyle);
		
		if(namedStyle == IStyleConstantsJSP.JSP_CONTENT) {
			fForeground.setEnabled(false);
			fBold.setEnabled(false);
			if (showItalic)
				fItalic.setEnabled(false);
			fForegroundLabel.setEnabled(false);	
		}
	}
}
