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
package org.eclipse.wst.xsl.xalan.debugger;

import java.util.List;

import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xpath.VariableStack;
import org.eclipse.wst.xsl.debugger.StyleFrame;

public class XalanRootStyleFrame extends XalanStyleFrame
{
	private final List globals;

	public XalanRootStyleFrame(StyleFrame parent, ElemTemplateElement element, VariableStack varStack, List globals)
	{
		super(parent, element, varStack);
		this.globals = globals;
	}

	public List getGlobals()
	{
		return globals;
	}
}
