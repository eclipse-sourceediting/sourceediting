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
