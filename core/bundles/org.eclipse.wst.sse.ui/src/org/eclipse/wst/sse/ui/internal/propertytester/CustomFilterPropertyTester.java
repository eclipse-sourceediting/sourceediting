/*******************************************************************************
 * Copyright (c) 2011, 2018 IBM Corporation and others.
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
package org.eclipse.wst.sse.ui.internal.propertytester;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.eclipse.wst.sse.ui.internal.contentoutline.ConfigurableContentOutlinePage;

public class CustomFilterPropertyTester extends PropertyTester {
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.expressions.IPropertyTester#test(java.lang.Object,
	 *      java.lang.String, java.lang.Object[], java.lang.Object)
	 */
	public boolean test(Object receiver, String property, Object[] args,	Object expectedValue) {
		if (receiver instanceof IEditorPart) {
			Object textEditor = ((IEditorPart) receiver).getAdapter(ITextEditor.class);
			if (textEditor instanceof StructuredTextEditor) {
				IContentOutlinePage outlinePage  = ((IEditorPart) receiver).getAdapter(IContentOutlinePage.class);
				return (outlinePage instanceof ConfigurableContentOutlinePage && outlinePage.getControl() != null && !outlinePage.getControl().isDisposed());
			}
		}
		return false;
	}
	
}
