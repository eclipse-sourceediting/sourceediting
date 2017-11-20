package org.eclipse.wst.xsl.ui.internal.wizards;

import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

class TemplateLabelProvider extends LabelProvider implements ITableLabelProvider
{
	public Image getColumnImage(Object element, int columnIndex)
	{
		return null;
	}

	public String getColumnText(Object element, int columnIndex)
	{
		Template template = (Template) element;

		switch (columnIndex)
		{
		case 0:
			return template.getName();
		case 1:
			return template.getDescription();
		default:
			return ""; //$NON-NLS-1$
		}
	}
}
