/*******************************************************************************
 * Copyright (c) 2009, 2018 IBM Corporation and others.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.ui.internal.wizards;

import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.wst.xsl.ui.internal.XSLUIConstants;

class TemplateContentProvider implements IStructuredContentProvider
{
	private TemplateStore fStore;

	public void dispose()
	{
		fStore = null;
	}

	public Object[] getElements(Object input)
	{
		return fStore.getTemplates(XSLUIConstants.TEMPLATE_CONTEXT_XSL_NEW);
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
	{
		fStore = (TemplateStore) newInput;
	}
}
