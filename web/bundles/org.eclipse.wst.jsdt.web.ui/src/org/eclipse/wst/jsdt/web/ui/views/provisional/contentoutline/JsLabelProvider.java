/*******************************************************************************
 * Copyright (c) 2011, 2012 IBM Corporation and others.
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
package org.eclipse.wst.jsdt.web.ui.views.provisional.contentoutline;

import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.jsdt.core.IJavaScriptElement;
import org.eclipse.wst.jsdt.ui.JavaScriptElementLabelProvider;
/**
*

* Provisional API: This class/interface is part of an interim API that is still under development and expected to
* change significantly before reaching stability. It is being made available at this early stage to solicit feedback
* from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken
* (repeatedly) as the API evolves.
*/
public class JsLabelProvider extends XMLLabelProvider {
	JavaScriptElementLabelProvider fLabelProvider = null;
	
	
	public Image getImage(Object o) {
		if (o instanceof IJavaScriptElement) {
			return getJavaElementLabelProvider().getImage(o);
		}
		return super.getImage(o);
	}
	
	private JavaScriptElementLabelProvider getJavaElementLabelProvider() {
		if (fLabelProvider == null) {
			fLabelProvider = new JavaScriptElementLabelProvider();
		}
		return fLabelProvider;
	}
	
	
	public String getText(Object o) {
		if (o instanceof IJavaScriptElement) {
			return getJavaElementLabelProvider().getText(o);
		}
		return super.getText(o);
	}
}
