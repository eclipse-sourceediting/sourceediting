/*******************************************************************************
 * Copyright (c) 2007 Chase Technology Ltd - http://www.chasetechnology.co.uk
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Doug Satchwell (Chase Technology Ltd) - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.internal.debug.ui.model;

import org.eclipse.core.resources.IFile;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.ILineBreakpoint;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.ui.IDebugModelPresentation;
import org.eclipse.debug.ui.IValueDetailListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.wst.xsl.internal.debug.ui.XSLDebugUIConstants;
import org.eclipse.wst.xsl.internal.debug.ui.XSLDebugUIPlugin;
import org.eclipse.wst.xsl.launching.model.XSLVariable;

/**
 * An <code>IDebugModelPresentation</code> for XSL debugging. 
 * 
 * @author Doug Satchwell
 */
public class XSLModelPresentation extends LabelProvider implements IDebugModelPresentation
{
	private Image localImg;

	public void setAttribute(String attribute, Object value)
	{
	}

	@Override
	public Image getImage(Object element)
	{
		if (element instanceof XSLVariable)
		{
			XSLVariable var = (XSLVariable) element;
			if (var.getScope().equals(XSLVariable.LOCAL_SCOPE))
			{
				if (localImg == null)
					localImg = XSLDebugUIPlugin.getImageDescriptor("/icons/localvariable_obj.gif").createImage(); //$NON-NLS-1$
				return localImg;
			}
		}
		return null;
	}

	@Override
	public String getText(Object element)
	{
		return null;
	}

	@Override
	public void dispose()
	{
		if (localImg != null)
			localImg.dispose();
		super.dispose();
	}

	public void computeDetail(IValue value, IValueDetailListener listener)
	{
		String detail = ""; //$NON-NLS-1$
		try
		{
			detail = value.getValueString();
		}
		catch (DebugException e)
		{
		}
		listener.detailComputed(value, detail);
	}

	public IEditorInput getEditorInput(Object element)
	{
		if (element instanceof IFile)
		{
			return new FileEditorInput((IFile) element);
		}
		if (element instanceof ILineBreakpoint)
		{
			return new FileEditorInput((IFile) ((ILineBreakpoint) element).getMarker().getResource());
		}
		return null;
	}

	public String getEditorId(IEditorInput input, Object element)
	{
		if (element instanceof IFile || element instanceof ILineBreakpoint)
		{
			return XSLDebugUIConstants.XSL_EDITOR_ID;
		}
		return null;
	}
}
