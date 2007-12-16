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

import java.util.ArrayList;
import java.util.List;

import org.apache.xalan.templates.ElemCallTemplate;
import org.apache.xalan.templates.ElemTemplate;
import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.templates.ElemVariable;
import org.apache.xml.utils.QName;
import org.apache.xpath.VariableStack;
import org.apache.xpath.XPath;
import org.eclipse.wst.xsl.debugger.StyleFrame;
import org.eclipse.wst.xsl.debugger.Variable;

public class XalanStyleFrame extends StyleFrame
{
	private final List localVariables = new ArrayList();
	private final ElemTemplateElement element;
	private final VariableStack varStack;

	public XalanStyleFrame(StyleFrame parent, ElemTemplateElement element, VariableStack varStack)
	{
		super(parent);
		this.element = element;
		this.varStack = varStack;
	}

	
	public int getColumn()
	{
		return element.getColumnNumber();
	}

	
	public String getFilename()
	{
		return element.getSystemId();
	}

	
	public int getLine()
	{
		return element.getLineNumber();
	}

	
	public String getName()
	{
		String name = element.getNodeName();
		if (element instanceof ElemTemplate)
		{
			ElemTemplate et = (ElemTemplate) element;
			QName q = et.getName();
			if (q != null)
			{
				name += " name=\"" + q.getLocalName() + "\"";
			}
			XPath xp = et.getMatch();
			if (xp != null)
			{
				name += " match=\"" + xp.getPatternString() + "\"";
			}
		}
		else if (element instanceof ElemCallTemplate)
		{
			ElemCallTemplate et = (ElemCallTemplate) element;
			QName q = et.getName();
			if (q != null)
			{
				name += " name=\"" + q.getLocalName() + "\"";
			}
		}
		return name;
	}

	
	public List getVariableStack()
	{
		List vars = new ArrayList();
		fillWithLocals(vars);
		fillWithGlobals(vars);
		return vars;
	}

	
	public Variable getVariable(String scope, int slotNumber)
	{
		List vars = new ArrayList();
		if (Variable.GLOBAL_SCOPE.equals(scope))
			fillWithGlobals(vars);
		else if (Variable.LOCAL_SCOPE.equals(scope))
			fillWithLocals(vars);
		return (Variable) vars.get(slotNumber);
	}

	public void addVariable(ElemVariable variable)
	{
		String scope = variable.getIsTopLevel() ? Variable.GLOBAL_SCOPE : Variable.LOCAL_SCOPE;
		Variable xvar = new XalanVariable(varStack, scope, localVariables.size(), variable);
		localVariables.add(xvar);
	}

	private void fillWithLocals(List vars)
	{
		XalanStyleFrame frame = this;
		while ((frame = (XalanStyleFrame) frame.getParent()) != null)
		{
			vars.addAll(frame.localVariables);
		}
	}

	private void fillWithGlobals(List vars)
	{
		XalanStyleFrame frame = this;
		while ((frame = (XalanStyleFrame) frame.getParent()) != null)
		{
			if (frame instanceof XalanRootStyleFrame)
			{
				XalanRootStyleFrame root = (XalanRootStyleFrame) frame;
				vars.addAll(root.getGlobals());
				break;
			}
		}

		// if (template != null)
		// {
		// Stylesheet sheet = template.getStylesheet();
		// for (int i = 0; i < sheet.getVariableOrParamCount(); i++)
		// {
		// ElemVariable variable = sheet.getVariableOrParam(i);
		// Variable var = new
		// XalanVariable(this,variable.getName().getLocalName(),Variable.GLOBAL_SCOPE,i,variable);
		// vars.add(var);
		// }
		// }

	}
}
