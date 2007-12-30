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
import java.util.Stack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
	private static final Log log = LogFactory.getLog(XalanStyleFrame.class);
	
	private final List localVariables = new ArrayList();
	private final ElemTemplateElement element;
	private final VariableStack varStack;
	private final Stack elementStack = new Stack();
	private int currentLine;

	public XalanStyleFrame(StyleFrame parent, ElemTemplateElement element, VariableStack varStack)
	{
		super(parent);
		this.element = element;
		this.varStack = varStack;
		pushElement(element);
	}
	
	public String getFilename()
	{
		return element.getStylesheet().getSystemId();
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
	
	public Object getTemplate()
	{
		return element.getOwnerXSLTemplate();
	}
	
	public int getCurrentLine()
	{
		return currentLine;
	}
	
	public void pushElement(ElemTemplateElement e)
	{
		currentLine = e.getLineNumber();
		elementStack.push(e);
		log.debug("Pushed element "+e);
	}

	public ElemTemplateElement popElement()
	{
		ElemTemplateElement e = (ElemTemplateElement)elementStack.pop();
		log.debug("Popped element "+e);
		currentLine = e.getEndLineNumber();
		return e;
	}

	public ElemTemplateElement peekElement()
	{
		if (elementStack.isEmpty())
			return null;
		return (ElemTemplateElement)elementStack.peek();
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
